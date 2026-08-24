package pl.skidam.automodpack_core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import pl.skidam.automodpack_core.GlobalVariables;

public class Json {
   private static final String CURSEFORGE_API_KEY = "JDJhJDEwJHNrbDRkNFkyTVI2Yy5uWmhWM3VWSy5HQmVLZDNNTDRSS3lNbnM4RFpxajkxSGpmL0hZcmNT";
   private static final String CURSEFORGE_API_HOST = "api.curseforge.com";

   public static String getCurseForgeApiKey() {
      return new String(Base64.getDecoder().decode("JDJhJDEwJHNrbDRkNFkyTVI2Yy5uWmhWM3VWSy5HQmVLZDNNTDRSS3lNbnM4RFpxajkxSGpmL0hZcmNT"), StandardCharsets.UTF_8);
   }

   public static JsonArray fromUrlAsArray(String url) {
      JsonElement element = null;

      try {
         HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
         connection.setRequestProperty("User-Agent", "github/skidamek/automodpack/" + GlobalVariables.AM_VERSION);
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(5000);
         connection.setDoOutput(true);
         connection.connect();
         if (connection.getResponseCode() == 200) {
            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream())) {
               JsonParser parser = new JsonParser();
               element = parser.parse(isr);
            }
         }

         connection.disconnect();
      } catch (SocketTimeoutException var8) {
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
   }

   public static JsonObject fromFile(Path path) throws IOException {
      if (Files.exists(path) && Files.isRegularFile(path)) {
         JsonParser parser = new JsonParser();
         byte[] bytes = Files.readAllBytes(path);
         StringBuilder sb = new StringBuilder();
         byte[] var4 = bytes;
         int var5 = bytes.length;

         for (int var6 = 0; var6 < var5; var6++) {
            Byte b = var4[var6];
            sb.append((char)b.byteValue());
         }

         return parser.parse(sb.toString()).getAsJsonObject();
      } else {
         return null;
      }
   }

   public static JsonObject fromUrl(String url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
      connection.setRequestProperty("User-Agent", "github/skidamek/automodpack/" + GlobalVariables.AM_VERSION);
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      connection.connect();
      JsonElement element = null;
      int code = connection.getResponseCode();
      if (code == 200) {
         try (InputStreamReader isr = new InputStreamReader(connection.getInputStream())) {
            element = new JsonParser().parse(isr);
         }
      } else {
         GlobalVariables.LOGGER.warn("{} responded {} code", url, code);
      }

      connection.disconnect();
      return element != null && !element.isJsonArray() ? element.getAsJsonObject() : null;
   }

   public static JsonObject fromModrinthUrl(String requestUrl, List<String> listOfSha1) throws IOException {
      if (listOfSha1 != null && !listOfSha1.isEmpty()) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("hashes", new Gson().toJsonTree(listOfSha1));
         jsonObject.addProperty("algorithm", "sha1");
         String body = jsonObject.toString();
         URL url = new URL(requestUrl);
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.addRequestProperty("Content-Type", "application/json");
         connection.addRequestProperty("Accept", "application/json");
         connection.setConnectTimeout(3000);
         connection.setReadTimeout(10000);
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);
         connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
         connection.connect();
         JsonElement element = null;
         int code = connection.getResponseCode();
         if (code == 200) {
            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream())) {
               element = new JsonParser().parse(isr);
            }
         } else {
            GlobalVariables.LOGGER.warn("{} responded {} code", url, code);
         }

         connection.disconnect();
         return element != null && !element.isJsonArray() ? element.getAsJsonObject() : null;
      } else {
         return null;
      }
   }

   public static JsonObject fromCurseForgeUrl(String requestUrl, List<String> listOfMurmur) throws IOException {
      if (listOfMurmur != null && !listOfMurmur.isEmpty()) {
         JsonObject jsonObject = new JsonObject();
         Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
         jsonObject.add("fingerprints", gson.toJsonTree(listOfMurmur));
         String body = jsonObject.toString();
         URL url = new URL(requestUrl);
         if ("https".equalsIgnoreCase(url.getProtocol())
            && "api.curseforge.com".equalsIgnoreCase(url.getHost())
            && url.getUserInfo() == null
            && (url.getPort() == -1 || url.getPort() == 443)) {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("x-api-key", getCurseForgeApiKey());
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            connection.connect();
            JsonElement element = null;
            int code = connection.getResponseCode();
            if (code == 200) {
               try (InputStreamReader isr = new InputStreamReader(connection.getInputStream())) {
                  element = new JsonParser().parse(isr);
               }
            } else {
               GlobalVariables.LOGGER.warn("{} responded {} code", url, code);
            }

            connection.disconnect();
            return element != null && !element.isJsonArray() ? element.getAsJsonObject() : null;
         } else {
            throw new IOException("Refusing to send the CurseForge API key to an untrusted endpoint");
         }
      } else {
         return null;
      }
   }
}
