package ch.cydcampus.hickup.pipeline.source;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;
import ch.cydcampus.hickup.pipeline.filter.Filter;
import ch.cydcampus.hickup.pipeline.filter.IPFilter;
import ch.cydcampus.hickup.pipeline.filter.PacketSizeFilter;
import ch.cydcampus.hickup.pipeline.filter.TimeFilter;
import ch.cydcampus.hickup.pipeline.filter.Filter.FilterType;
import ch.cydcampus.hickup.util.TimeInterval;

/*
 * TODO: Add functionality to go through data in chunks (e.g. 1 minute at a time)
 * First collect metadata about the query and then divide into chunks of right size.
 */
public class DataBaseSource extends DataSource {
    
    private String query;
    private String querysuffix;
    private String url;
    private boolean dataLoaded = false;

    private Connection connection;

    public DataBaseSource(String host, int port, String database, String user, String password, String table) {
        this.url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

        // connect to the database
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the PostgreSQL server: " + e.getMessage());
        }

        this.query = "SELECT * FROM " + table + " WHERE 1 = 1";
        this.querysuffix = " ORDER BY timestamp";
    }

    @Override
    public void setFilter(Filter filter) {
    
        // check type of filter and cast to correct type. Extract filter parameters and convert to sql query conditions.
        FilterType filterType = filter.getFilterType();

        switch (filterType) {
            case IP:
                IPFilter ipFilter = (IPFilter) filter;
                String negation = ipFilter.isBlacklist() ? "NOT " : "";
                String connector = ipFilter.isBlacklist() ? " AND " : " OR ";
                String ips = "(";
                for (InetAddress ip : ipFilter.getIps()) {
                    ips += "'" + ip.getHostAddress() + "', ";
                }
                ips = ips.substring(0, ips.length() - 2) + ")";
                query += " AND " + negation + "(src_ip IN " + ips + connector + "dst_ip IN " + ips + ")";
                break;
            case PACKET_SIZE:
                PacketSizeFilter packetSizeFilter = (PacketSizeFilter) filter;
                query += " AND ";
                if(packetSizeFilter.isBlacklist()) {
                    query += "NOT ";
                }
                query += "(size >= " + packetSizeFilter.getMinBytes() + " AND size <= " + packetSizeFilter.getMaxBytes() + ")";
                break;
            case TIME:
                TimeFilter timeFilter = (TimeFilter) filter;
                query += " AND ";
                if(timeFilter.isBlacklist()) {
                    query += "NOT ";
                }
                query += "(timestamp between '" + timeFilter.getMin() + "' AND '" + timeFilter.getMax() + "')";
                break;
            default:
                throw new UnsupportedOperationException("Filter type " + filterType + " not supported by database source.");
        }
        System.out.println(query);
    }


    @Override
    public Set<FilterType> getSupportedFilters() {
        return Set.of(FilterType.IP, FilterType.PORT, FilterType.TIME, FilterType.PACKET_SIZE, FilterType.PROTOCOL, FilterType.HOST_PAIR, FilterType.PORT_PAIR);
    }

    private void getPointsFromSQL() {

        String query = this.query + this.querysuffix;

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            getPointsFromPreparedStatement(statement);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
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
            dataLoaded = true;
        }).start();
    }

}
