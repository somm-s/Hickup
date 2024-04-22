package ch.cydcampus.hickup.util;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class SubnetChecker {

    private final Map<InetAddress, byte[]> subnetMasks = new HashMap<>();

    public SubnetChecker(InetAddress[] subnets, int[] subnetPrefixLengths) {
        for (int i = 0; i < subnets.length; i++) {
            InetAddress subnet = subnets[i];
            int prefixLength = subnetPrefixLengths[i];
            subnetMasks.put(subnet, calculateSubnetMask(prefixLength, subnet.getAddress().length));
        }
    }

    public boolean isIpAddressInSubnets(InetAddress ipAddress) {
        for (Map.Entry<InetAddress, byte[]> entry : subnetMasks.entrySet()) {
            InetAddress subnet = entry.getKey();
            byte[] subnetMask = entry.getValue();
            if (isIpAddressInSubnet(ipAddress, subnet, subnetMask)) {
                System.out.println("found...");
                return true;
            }
        }
        return false;
    }

    private boolean isIpAddressInSubnet(InetAddress ipAddress, InetAddress subnetAddress, byte[] subnetMask) {
        byte[] ipBytes = ipAddress.getAddress();
        byte[] subnetBytes = subnetAddress.getAddress();

        if (ipBytes.length != subnetBytes.length) {
            return false; // IPv4 and IPv6 have different lengths
        }

        byte[] maskedIp = applySubnetMask(ipBytes, subnetMask);
        byte[] maskedSubnet = applySubnetMask(subnetBytes, subnetMask);


        for (int i = 0; i < ipBytes.length; i++) {
            if (maskedIp[i] != maskedSubnet[i]) {
                return false;
            }
        }
        return true;
    }

    private byte[] calculateSubnetMask(int prefixLength, int addressLength) {
        byte[] mask = new byte[addressLength];
    
        for (int i = 0; i < prefixLength; i++) {
            mask[i / 8] |= (1 << (7 - (i % 8)));
        }
    
        return mask;
    }
    

    private byte[] applySubnetMask(byte[] ipAddress, byte[] subnetMask) {
        byte[] maskedAddress = new byte[ipAddress.length];
        for (int i = 0; i < ipAddress.length; i++) {
            maskedAddress[i] = (byte) (ipAddress[i] & subnetMask[i]);
        }
        return maskedAddress;
    }

    public static void main(String[] args) {
        try {
            InetAddress[] subnets = {
                    InetAddress.getByName("192.168.1.0"),
                    InetAddress.getByName("2a07:1182:7:11::")
            };
            int[] subnetPrefixLengths = {24, 64};

            SubnetChecker checker = new SubnetChecker(subnets, subnetPrefixLengths);

            // Example usage
            InetAddress ipAddress = InetAddress.getByName("2a07:1182:7:11:0:0:0:33");
            System.out.println(ipAddress);
            System.out.println(subnets[1]);
            boolean isInSubnet = checker.isIpAddressInSubnets(ipAddress);
            System.out.println("Is IP address in subnet? " + isInSubnet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
