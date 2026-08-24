package net.joefoxe.hexerei.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.neoforged.fml.loading.FMLEnvironment;

public class HexereiSupporterBenefits {
   public static final List<UUID> supporters = new ArrayList<>();

   public static boolean matchesSupporterUUID(UUID uuid) {
      return supporters.contains(uuid);
   }

   public static void init() {
      try {
         String readUrl = readUrl(new URL("https://raw.githubusercontent.com/JoeFoxe/Hexerei-1.19/1.21.1/supporters.json"));
         JsonObject object = JsonParser.parseString(readUrl).getAsJsonObject();

         for (JsonElement element : object.getAsJsonArray("supporters")) {
            String uuid = element.getAsString();
            supporters.add(UUID.fromString(uuid.trim()));
         }
      } catch (IOException var6) {
         var6.printStackTrace();
         if (!FMLEnvironment.production) {
            throw new RuntimeException("Failed to load supporters.json");
         }
      }
   }

   public static String readUrl(URL url) throws IOException {
      String var5;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
         StringBuilder builder = new StringBuilder();
         char[] chars = new char[1024];

         int read;
         while ((read = reader.read(chars)) != -1) {
            builder.append(chars, 0, read);
         }

         var5 = builder.toString();
      }

      return var5;
   }
}
