package com.seibel.distanthorizons.core.util.objects;

public class ParsedIp {
   public static final String NUMERIC_IP_REGEX = "^[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*(:[0-9]*)?$";
   public static final String LAN_IP_REGEX = "(10|172\\.16|192\\.168).*";
   public final String ip;
   public final String port;
   public final boolean isNumeric;

   public ParsedIp(String fullIp) {
      fullIp = fullIp.trim();
      this.isNumeric = fullIp.matches("^[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*(:[0-9]*)?$");
      if (this.isNumeric) {
         String[] list = fullIp.split(":");
         if (list.length == 2) {
            this.ip = list[0];
            this.port = list[1];
         } else {
            this.ip = fullIp;
            this.port = null;
         }
      } else {
         this.ip = fullIp;
         this.port = null;
      }
   }

   public ParsedIp(String newIp, String newPort) {
      this.ip = newIp;
      this.port = newPort;
      this.isNumeric = this.ip.matches("^[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*(:[0-9]*)?$");
   }

   public boolean isLan() {
      return this.ip.toLowerCase().equals("localhost") || this.ip.matches("(10|172\\.16|192\\.168).*");
   }

   @Override
   public String toString() {
      return this.ip + (this.port != null ? ":" + this.port : "");
   }
}
