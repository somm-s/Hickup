package ch.cydcampus.hickup.pipeline.source;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;
import ch.cydcampus.hickup.util.TimeInterval;

/**
 * Use database as source for packets. It is assumed that all packets reside in
 * one table. The table must have the following columns (exact names, order irrelevant):
 * protocol, size, timestamp, src_ip, dst_ip, src_port, dst_port
 * To stream packets from the database, the table is split into chunks and loaded chunk by chunk.
 */
public class DataBaseSource extends DataSource {
    
    private static final int PACKETS_PER_CHUNK = 1000;
    private String query;
    private String querysuffix;
    private String url;
    private String[] queryAdditions;
    private Connection connection;

    public DataBaseSource(String host, int port, String database, String user, String password, String table) {
        this.url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the PostgreSQL server: " + e.getMessage());
        }

        this.query = "SELECT * FROM " + table + " WHERE 1 = 1";
        this.querysuffix = " ORDER BY timestamp";
        String metadataQuery = "SELECT MIN(timestamp) AS min_time, MAX(timestamp) AS max_time, COUNT(*) AS num_packets, SUM(size) AS total_size FROM " + table + ";";
        long minTime = 0; // in microseconds
        long maxTime = 0; // in microseconds
        long numPackets = 0; // number of packets
        long totalSize = 0; // in bytes

        try {
            PreparedStatement statement = connection.prepareStatement(metadataQuery);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Timestamp minTimeTimestamp = resultSet.getTimestamp("min_time");
            System.out.println("Min time: " + minTimeTimestamp);
            Timestamp maxTimeTimestamp = resultSet.getTimestamp("max_time");
            System.out.println("Max time: " + maxTimeTimestamp);
            minTime = TimeInterval.timeToMicro(minTimeTimestamp);
            maxTime = TimeInterval.timeToMicro(maxTimeTimestamp);
            numPackets = resultSet.getLong("num_packets");
            totalSize = resultSet.getLong("total_size");
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }

        System.out.println("Min time: " + (minTime / 1000000) + " seconds");
        System.out.println("Max time: " + (maxTime / 1000000) + " seconds");
        System.out.println("Duration (minutes): " + ((maxTime - minTime) / 1000000 / 60));
        System.out.println("Number of packets: " + numPackets);
        System.out.println("Total size: " + (totalSize / 1000000) + " MB");
        int numSplits = (int) Math.ceil((double) numPackets / PACKETS_PER_CHUNK);
        System.out.println("Number of splits: " + numSplits);
        long timeInterval = (maxTime - minTime) / numSplits;
        System.out.println("Time interval (s): " + (timeInterval / 1000000));
        numSplits++; // add one split for the last chunk

        queryAdditions = new String[numSplits];
        for(int i = 0; i < numSplits; i++) {
            long start = minTime + i * timeInterval;
            long end = minTime + (i + 1) * timeInterval;
            queryAdditions[i] = " AND timestamp >= '" + TimeInterval.microToTime(start) + "' AND timestamp < '" + TimeInterval.microToTime(end) + "' ";
        }
    }

    private void getPointsFromSQL() {
        int i = 0;
        while(i < queryAdditions.length) {
            if(this.queueLimitReached()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Error sleeping: " + e.getMessage());
                }
                continue;
            }
            String query = this.query + this.queryAdditions[i++] + this.querysuffix;
            System.out.println(query);
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                getPointsFromPreparedStatement(statement);
            } catch (SQLException e) {
                System.err.println("Error executing query: " + e.getMessage());
            }
        }
    }

    private PacketAbstraction packetFromResultSet(ResultSet resultSet) throws SQLException, UnknownHostException {
        Protocol protocol = Protocol.fromInt(resultSet.getInt("protocol"));
        int packetSize = resultSet.getInt("size");
        Timestamp time = resultSet.getTimestamp("timestamp");
        String srcIp = resultSet.getString("src_ip");
        String dstIp = resultSet.getString("dst_ip");
        InetAddress srcAddr = InetAddress.getByName(srcIp);
        InetAddress dstAddr = InetAddress.getByName(dstIp);
        int srcPort = resultSet.getInt("src_port");
        int dstPort = resultSet.getInt("dst_port");
        return AbstractionFactory.getInstance().allocateFromFields(srcAddr, dstAddr, srcPort, dstPort, protocol, packetSize, TimeInterval.timeToMicro(time));
    }

    private void getPointsFromPreparedStatement(PreparedStatement preparedStatement) {
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                PacketAbstraction p = packetFromResultSet(resultSet);
                this.produce(p);
            }
        } catch (SQLException | UnknownHostException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }

    @Override
    public void start() {
        new Thread(() -> {
            getPointsFromSQL();
        }).start();
    }
}
