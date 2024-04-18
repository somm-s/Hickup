package ch.cydcampus.hickup.pipeline.feature.combinationrules;

import java.net.InetAddress;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.util.SubnetChecker;

public class LS24InternalCombinationRule implements FeatureCombinationRule {

    // internal traffic, monitor edge traffic
    int[] subnetPrefixLengths = {
        24, // BAF INT
        64,
        24, // BGP 5G Core
        64,
        26, // BGP 5G DMZ
        64,
        24, // BGP Satellite
        64,
        24, // BGP INT
        64,
        26, // BGP DMZ
        64, 
        26, // BGP 5G RAN
        64,
        23, // GT SINET
        64,
        26, // BEG DMZ
        64,
        24, // BEG GAS
        64,
        24, // BEG IED
        64,
        24, // BEG SCADA
        64,
        24, // BEG INT
        64,
        24, // BAF ADS
        64,
        24, // BAF 5G RAN
        64,
        26, // BAF DMZ
        64,
    };
    String[] hostNames = {
        "10.7.11.0", // BAF INT
        "2a07:1182:7:11::",
        "10.7.14.0", // BGP 5G Core
        "2a07:1182:7:14::",
        "100.99.7.0", // BGP 5G DMZ
        "2a07:1182:7:40::",
        "10.7.20.0", // BGP Satellite
        "2a07:1182:7:f20::",
        "10.7.13.0", // BGP INT
        "2a07:1182:7:13::",
        "100.98.7.0", // BGP DMZ
        "2a07:1182:7:30::",
        "10.7.8.0", // BGP 5G RAN
        "2a07:1182:7:8::",
        "100.100.100.0", // GT SINET
        "2a07:1182:100:100::",
        "100.97.7.0", // BEG DMZ
        "2a07:1182:7:20::",
        "10.7.17.0", // BEG GAS
        "2a07:1182:7:17::",
        "10.7.22.0", // BEG IED
        "2a07:1182:7:22::",
        "10.7.19.0", // BEG SCADA
        "2a07:1182:7:19::",
        "10.7.12.0", // BEG INT
        "2a07:1182:7:12::",
        "10.7.15.0", // BAF ADS
        "2a07:1182:7:15::",
        "10.7.9.0", // BAF 5G RAN
        "2a07:1182:7:9::",
        "100.96.7.0", // BAF DMZ
        "2a07:1182:7:10::",
    };

    int outputIndex;
    SubnetChecker checker;

    public LS24InternalCombinationRule() {
                InetAddress[] subnets = new InetAddress[hostNames.length];
        for(int i = 0; i < hostNames.length; i++) {
            try {
                subnets[i] = InetAddress.getByName(hostNames[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.checker = new SubnetChecker(subnets, subnetPrefixLengths);
    }

    @Override
    public FeatureCombinationRule setIndices(int[] indices) {
        return this;
    }

    @Override
    public FeatureCombinationRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    @Override
    public void combine(Abstraction abstraction) {
        InetAddress srcAddress = abstraction.getFeature(PipelineConfig.SRC_IP_INDEX).asIP();
        InetAddress dstAddress = abstraction.getFeature(PipelineConfig.DST_IP_INDEX).asIP();

        boolean srcInSubnet = checker.isIpAddressInSubnets(srcAddress);
        boolean dstInSubnet = checker.isIpAddressInSubnets(dstAddress);
        boolean isSrcSmaller = srcAddress.toString().compareTo(dstAddress.toString()) < 0;

        if(srcInSubnet == dstInSubnet) {
            abstraction.getFeature(outputIndex).set(0); // filter
        } else if(srcInSubnet && isSrcSmaller || dstInSubnet && !isSrcSmaller) {
            abstraction.getFeature(outputIndex).set(1); // smaller IP is internal and other is external
        } else {
            abstraction.getFeature(outputIndex).set(2); // smaller IP is external and other is internal
        }
    }
    
}
