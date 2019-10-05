package uk.org.whoami.easyban.util;

import java.net.*;

public class Subnet
{
    short[] subnet;
    short[] mask;
    
    public Subnet(final String subnet) throws IllegalArgumentException {
        if (!subnet.contains("/")) {
            throw new IllegalArgumentException("Invalid Subnet");
        }
        final String[] sub = subnet.split("/");
        if (sub.length != 2) {
            throw new IllegalArgumentException("Invalid Subnet");
        }
        try {
            this.subnet = inetAddressToArray(InetAddress.getByName(sub[0]));
        }
        catch (UnknownHostException ex) {
            throw new IllegalArgumentException("Invalid Networkprefix");
        }
        if (isParseableInteger(sub[1])) {
            final int cidr = Integer.parseInt(sub[1]);
            if (cidr < 0 || cidr > 32) {
                throw new IllegalArgumentException("Invalid CIDR-Mask");
            }
            this.mask = cidrToArray(cidr);
        }
        else {
            try {
                this.mask = inetAddressToArray(InetAddress.getByName(sub[1]));
            }
            catch (UnknownHostException ex) {
                throw new IllegalArgumentException("Invalid Subnetmask");
            }
        }
    }
    
    public Subnet(final short[] subnet, final short[] mask) {
        if (subnet.length > mask.length) {
            throw new IllegalArgumentException("Invalid Subnet");
        }
        this.subnet = subnet;
        this.mask = mask;
    }
    
    public boolean isIpInSubnet(final short[] ip) {
        if (this.mask.length < ip.length) {
            throw new IllegalArgumentException("Invalid Subnet");
        }
        for (int i = 0; i < ip.length; ++i) {
            final short tmp = (short)(255 - this.mask[i]);
            if (ip[i] - this.subnet[i] > tmp || ip[i] - this.subnet[i] < 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isIpInSubnet(final InetAddress ip) {
        return this.isIpInSubnet(inetAddressToArray(ip));
    }
    
    @Override
    public String toString() {
        return (this.subnet.length > 4) ? this.toIpv6String() : this.toIpv4String();
    }
    
    private String toIpv4String() {
        String ret = "";
        for (int i = 0; i < 4; ++i) {
            if (i != 0) {
                ret += ".";
            }
            ret += this.subnet[i];
        }
        ret += "/";
        for (int i = 0; i < 4; ++i) {
            if (i != 0) {
                ret += ".";
            }
            ret += this.mask[i];
        }
        return ret;
    }
    
    private String toIpv6String() {
        String ret = "";
        for (int i = 0; i < 16; i += 2) {
            if (i != 0) {
                ret += ":";
            }
            ret += Integer.toHexString(this.subnet[i]);
            String tmp = Integer.toHexString(this.subnet[i + 1]);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            ret += tmp;
        }
        ret += "/";
        for (int i = 0; i < 16; i += 2) {
            if (i != 0) {
                ret += ":";
            }
            ret += Integer.toHexString(this.mask[i]);
            String tmp = Integer.toHexString(this.mask[i + 1]);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            ret += tmp;
        }
        return ret;
    }
    
    public static short[] cidrToArray(int bits) {
        final short[] mask = new short[16];
        for (int i = 0; i < 16; ++i) {
            if (bits > 8) {
                mask[i] = 255;
                bits -= 8;
            }
            else {
                mask[i] = (short)(256.0 - Math.pow(2.0, 8 - bits));
                bits = 0;
            }
        }
        return mask;
    }
    
    public static short[] inetAddressToArray(final InetAddress ip) {
        final byte[] addr = ip.getAddress();
        final short[] ret = new short[addr.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = (short)(0xFF & addr[i]);
        }
        return ret;
    }
    
    public static boolean isParseableInteger(final String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
