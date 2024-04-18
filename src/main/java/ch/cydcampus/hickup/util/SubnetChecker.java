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
        for (InetAddress subnet : subnetMasks.keySet()) {
            byte[] subnetMask = subnetMasks.get(subnet);
            if (isIpAddressInSubnet(ipAddress, subnet, subnetMask)) {
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

        for (int i = 0; i < addressLength; i++) {
            int byteValue = 0xff;
            int bitsRemaining = prefixLength - (i * 8);
            if (bitsRemaining < 8) {
                byteValue <<= (8 - bitsRemaining);
            }
            mask[i] = (byte) byteValue;
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
                    InetAddress.getByName("2001:0db8:85a3::")
            };
            int[] subnetPrefixLengths = {23, 48};

            SubnetChecker checker = new SubnetChecker(subnets, subnetPrefixLengths);

            // Example usage
            InetAddress ipAddress = InetAddress.getByName("192.168.1.100");
            boolean isInSubnet = checker.isIpAddressInSubnets(ipAddress);
            System.out.println("Is IP address in subnet? " + isInSubnet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
