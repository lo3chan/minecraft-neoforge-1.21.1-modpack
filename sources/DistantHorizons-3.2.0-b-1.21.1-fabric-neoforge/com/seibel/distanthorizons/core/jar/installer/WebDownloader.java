package com.seibel.distanthorizons.core.jar.installer;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.json.JsonFormat;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class WebDownloader {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static boolean netIsAvailable() {
      try {
         URL url = new URL("https://example.com");
         URLConnection conn = url.openConnection();
         conn.connect();
         conn.getInputStream().close();
         return false;
      } catch (Exception var2) {
         return true;
      }
   }

   public static void downloadAsFile(URL url, File file) throws Exception {
      HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
      long filesize = connection.getContentLengthLong();
      if (filesize == -1L) {
         throw new Exception("Content length must not be -1 (unknown)!");
      } else {
         long totalDataRead = 0L;
         BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

         try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

            try {
               byte[] data = new byte[1024];
               int percent = -1;

               int i;
               while ((i = in.read(data, 0, 1024)) >= 0) {
                  totalDataRead += i;
                  bout.write(data, 0, i);
                  int newPercent = (int)(totalDataRead * 100L / filesize);
                  if (percent != newPercent) {
                     percent = newPercent;
                     LOGGER.info(newPercent + "% downloaded");
                  }
               }
            } catch (Throwable var16) {
               try {
                  bout.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }

               throw var16;
            }

            bout.close();
         } catch (Throwable var17) {
            try {
               in.close();
            } catch (Throwable var14) {
               var17.addSuppressed(var14);
            }

            throw var17;
         }

         in.close();
      }
   }

   public static String downloadAsString(URL url) throws Exception {
      StringBuilder stringBuilder = new StringBuilder();
      URLConnection urlConnection = url.openConnection();
      urlConnection.setConnectTimeout(1000);
      urlConnection.setReadTimeout(1000);
      BufferedReader bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      String line;
      while ((line = bReader.readLine()) != null) {
         stringBuilder.append(line);
      }

      return stringBuilder.toString();
   }

   public static String formatMarkdownToHtml(String md, int width) {
      String str = String.format("<html><div style=\"width:%dpx;\">%s</div></html>", width, md);
      return new MarkdownFormatter.HTMLFormat().convertTo(str);
   }

   public static Config parseWebJson(String url) throws Exception {
      return parseWebJson(new URL(url));
   }

   public static Config parseWebJson(URL url) throws Exception {
      return JsonFormat.minimalInstance().createParser().parse(downloadAsString(url));
   }

   public static ArrayList<Config> parseWebJsonList(String url) throws Exception {
      return parseWebJsonList(new URL(url));
   }

   public static ArrayList<Config> parseWebJsonList(URL url) throws Exception {
      return JsonFormat.minimalInstance().createParser().parse("{\"E\":" + downloadAsString(url) + "}").get("E");
   }

   private static String checksum(String filepath, MessageDigest md) throws IOException {
      DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md);

      try {
         while (dis.read() != -1) {
         }

         md = dis.getMessageDigest();
      } catch (Throwable var8) {
         try {
            dis.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }

      dis.close();
      StringBuilder var10 = new StringBuilder();

      for (byte b : md.digest()) {
         var10.append(String.format("%02x", b));
      }

      return var10.toString();
   }
}
