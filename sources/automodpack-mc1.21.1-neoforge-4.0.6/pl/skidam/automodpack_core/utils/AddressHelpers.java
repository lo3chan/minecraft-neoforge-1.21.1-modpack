package pl.skidam.automodpack_core.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;
import pl.skidam.automodpack_core.GlobalVariables;

public class AddressHelpers {
   public static String getPublicIp() {
      String[] services = new String[]{"https://ip.seeip.org/json", "https://api.ipify.org?format=json"};

      for (String service : services) {
         try {
            return Objects.requireNonNull(Json.fromUrl(service)).get("ip").getAsString();
         } catch (Exception var6) {
         }
      }

      GlobalVariables.LOGGER.error("AutoModpack couldn't get your public IP address, please configure it manually.");
      return null;
   }

   public static String getLocalIp() {
      return getNetworkIp(Inet4Address.class);
   }

   public static String getLocalIpv6() {
      return getNetworkIp(Inet6Address.class);
   }

   private static String getNetworkIp(Class<? extends InetAddress> ipClass) {
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

         while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isUp() && !networkInterface.isLoopback()) {
               Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

               while (addresses.hasMoreElements()) {
                  InetAddress addr = addresses.nextElement();
                  if (ipClass.isInstance(addr) && !addr.isLinkLocalAddress()) {
                     return addr.getHostAddress().split("%")[0];
                  }
               }
            }
         }
      } catch (SocketException var5) {
         var5.printStackTrace();
      }

      return null;
   }

   public static boolean areIpsEqual(String ip1, String ip2) {
      try {
         return InetAddress.getByName(ip1).equals(InetAddress.getByName(ip2));
      } catch (UnknownHostException var3) {
         return false;
      }
   }

   public static String normalizeIp(String ip) {
      if (ip == null) {
         return null;
      } else {
         ip = ip.trim();
         if (ip.startsWith("/")) {
            ip = ip.substring(1);
         }

         if (ip.contains(":")) {
            int portIndex = ip.lastIndexOf(":");
            ip = ip.substring(0, portIndex);
         }

         if (ip.startsWith("[") && ip.endsWith("]")) {
            ip = ip.substring(1, ip.length() - 1);
         }

         return ip;
      }
   }

   public static InetSocketAddress format(String host, int port) {
      if (host.endsWith(".")) {
         host = host.substring(0, host.length() - 1);
      }

      host = host.toLowerCase();
      return InetSocketAddress.createUnresolved(host, port);
   }

   public static InetSocketAddress parse(String address) {
      if (address == null) {
         return null;
      } else {
         InetSocketAddress socketAddress = null;

         try {
            int portIndex = address.lastIndexOf(58);
            if (portIndex != -1) {
               String host = address.substring(0, portIndex);
               String port = address.substring(portIndex + 1);
               if (port.matches("\\d+")) {
                  socketAddress = format(host, Integer.parseInt(port));
               }
            }

            if (socketAddress == null) {
               socketAddress = format(address, 0);
            }
         } catch (Exception var5) {
            GlobalVariables.LOGGER.error("Error while parsing address", var5);
         }

         return socketAddress;
      }
   }

   public static boolean isLocal(String address) {
      if (address == null) {
         return true;
      } else {
         address = normalizeIp(address);
         if (!address.startsWith("192.168.") && !address.startsWith("127.") && !address.startsWith("::1") && !address.startsWith("0:0:0:0:")) {
            String localIp = getLocalIp();
            String localIpv6 = getLocalIpv6();
            return areIpsEqual(address, localIp) || areIpsEqual(address, localIpv6);
         } else {
            return true;
         }
      }
   }
}
