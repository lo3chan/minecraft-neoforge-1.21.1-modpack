package at.petrak.paucal.xplat.common;

import at.petrak.paucal.api.contrib.Contributor;
import at.petrak.paucal.xplat.PaucalMod;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.sounds.JOrbisAudioStream;
import org.jetbrains.annotations.Nullable;

public class ContributorsManifest {
   private static final Gson GSON = new Gson();
   private static final Jankson JANKSON = Jankson.builder().allowBareRootObject().build();
   private static Map<UUID, Contributor> CONTRIBUTORS = Object2ObjectMaps.emptyMap();
   private static Map<String, ByteBuffer> GITHUB_SOUNDS = Object2ObjectMaps.emptyMap();
   private static boolean startedLoading = false;

   @Nullable
   public static Contributor getContributor(UUID uuid) {
      return CONTRIBUTORS.get(uuid);
   }

   public static void loadContributors() {
      if (startedLoading) {
         PaucalMod.LOGGER.warn("Tried to reload the contributors in the middle of reloading the contributors");
      } else {
         startedLoading = true;
         Thread thread = new Thread(ContributorsManifest::fetchAndPopulate);
         thread.setName("PAUCAL Contributors Loading Thread");
         thread.setDaemon(true);
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(PaucalMod.LOGGER));
         thread.start();
      }
   }

   private static void fetchAndPopulate() {
      Pair<Map<UUID, Contributor>, Map<String, ByteBuffer>> pair = fetch();
      CONTRIBUTORS = (Map<UUID, Contributor>)pair.getFirst();
      GITHUB_SOUNDS = (Map<String, ByteBuffer>)pair.getSecond();
      startedLoading = false;
   }

   public static Pair<Map<UUID, Contributor>, Map<String, ByteBuffer>> fetch() {
      JsonObject config;
      try {
         URL url = new URL("https://raw.githubusercontent.com/gamma-delta/contributors/main/paucal/contributors-v01.json5");
         String unJanksoned = JANKSON.load(url.openStream()).toJson(false, false);
         config = (JsonObject)GSON.fromJson(unJanksoned, JsonObject.class);
      } catch (Exception var13) {
         PaucalMod.LOGGER.warn("Couldn't load contributors from Github, oh well :(", var13);
         if (var13 instanceof SyntaxError syn) {
            PaucalMod.LOGGER.warn(syn.getCompleteMessage());
         }

         return Pair.of(Object2ObjectMaps.emptyMap(), Object2ObjectMaps.emptyMap());
      }

      HashMap<UUID, Contributor> contributors = new HashMap<>();
      HashSet<String> ghSoundLocs = new HashSet<>();

      for (Entry<String, JsonElement> entry : config.entrySet()) {
         try {
            JsonObject rawEntry = entry.getValue().getAsJsonObject();
            UUID uuid = UUID.fromString(entry.getKey());
            Contributor contributor = new Contributor(uuid, rawEntry);
            contributors.put(uuid, contributor);
            ghSoundLocs.addAll(contributor.neededGithubSounds());
         } catch (Exception var12) {
            PaucalMod.LOGGER.warn("Exception when loading contributor '{}': {}", entry.getKey(), var12.getMessage());
         }
      }

      HashMap<String, ByteBuffer> sounds = new HashMap<>();

      for (String s : ghSoundLocs) {
         try {
            String unstub = "https://raw.githubusercontent.com/gamma-delta/contributors/main/paucal/headpat-sounds/" + s + ".ogg";
            URL url = new URL(unstub);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            byte[] oggBytes = is.readAllBytes();
            sounds.put(s, ByteBuffer.wrap(oggBytes));
         } catch (Exception var11) {
            PaucalMod.LOGGER.warn("Error when loading github sound '{}'", s, var11);
         }
      }

      PaucalMod.LOGGER.info("Loaded {} contributors and {} headpat sounds from Github", contributors.size(), sounds.size());
      return Pair.of(contributors, sounds);
   }

   @Nullable
   public static JOrbisAudioStream getSound(String name) {
      ByteBuffer oggBytes = GITHUB_SOUNDS.getOrDefault(name, null);
      if (oggBytes == null) {
         PaucalMod.LOGGER.warn("Tried to load a github sound {} that wasn't found", name);
         return null;
      } else {
         try {
            return new JOrbisAudioStream(new ByteArrayInputStream(oggBytes.array()));
         } catch (IOException var3) {
            PaucalMod.LOGGER.error("The github sound {} is an INVALID OGG FILE. This is Really Bad, what are you doing.", name, var3);
            return null;
         }
      }
   }
}
