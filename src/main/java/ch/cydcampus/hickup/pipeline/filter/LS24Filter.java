package ch.cydcampus.hickup.pipeline.filter;

import java.net.InetAddress;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.util.SubnetChecker;

public class LS24Filter implements FilterRule {


    // discard if communicating with the following subnets
    int[] discardPrefixLengths = {
        24, // BT SINET
        64,
        24, // BT Monitoring
        64,
    };
    String[] discardHostNames = {
        "100.100.7.0", // BT SINET
        "2a07:1182:7:a100:a::",
        "100.101.207.0", // BT Monitoring
        "2a07:1182:7:6001::",
    };

    int internalFeatureIndex;
    SubnetChecker discardChecker; // subnets we don't monitor

    public LS24Filter(int internalFeatureIndex) {

        InetAddress[] discardSubnets = new InetAddress[discardHostNames.length];
        for(int i = 0; i < discardHostNames.length; i++) {
            try {
                discardSubnets[i] = InetAddress.getByName(discardHostNames[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.internalFeatureIndex = internalFeatureIndex;
        this.discardChecker = new SubnetChecker(discardSubnets, discardPrefixLengths);
    }

    @Override
    public boolean filter(Abstraction abstraction) {
        InetAddress srcAddress = abstraction.getFeature(PipelineConfig.SRC_IP_INDEX).asIP();
        InetAddress dstAddress = abstraction.getFeature(PipelineConfig.DST_IP_INDEX).asIP();

        boolean srcInDiscardSubnet = discardChecker.isIpAddressInSubnets(srcAddress);
        boolean dstInDiscardSubnet = discardChecker.isIpAddressInSubnets(dstAddress);
        // only keep traffic that has exactly one end in internal net and does not communicate with non-monitored subnets

        int internalFeature = abstraction.getFeature(internalFeatureIndex).asInt();

        return srcInDiscardSubnet || dstInDiscardSubnet || internalFeature == 0;
    }
    
}


