package net.mehvahdjukaar.moonlight.core.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.util.FileDownloadUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class OurModsList {
   private static final Codec<List<OurModsList.Entry>> LIST_CODEC = OurModsList.Entry.CODEC.listOf().fieldOf("mods").codec();
   private static final String FETCH_URL = "https://raw.githubusercontent.com/MehVahdJukaar/mod_pages/heads/master/moonlight_mods.json";
   private static final Gson GSON = new Gson();
   private static volatile OurModsList.State state = OurModsList.State.NOT_STARTED;
   private static volatile List<OurModsList.Entry> mods = List.of();

   public static OurModsList.State getState() {
      return state;
   }

   public static List<OurModsList.Entry> getMods() {
      return mods;
   }

   public static synchronized void fetchIfNeeded() {
      if (state != OurModsList.State.LOADING && state != OurModsList.State.LOADED) {
         state = OurModsList.State.LOADING;
         Thread t = new Thread(
            () -> {
               try {
                  JsonElement json = (JsonElement)GSON.fromJson(
                     FileDownloadUtils.readString("https://raw.githubusercontent.com/MehVahdJukaar/mod_pages/heads/master/moonlight_mods.json"),
                     JsonElement.class
                  );
                  mods = (List<OurModsList.Entry>)LIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                  state = OurModsList.State.LOADED;
               } catch (Exception var1) {
                  Moonlight.LOGGER
                     .warn(
                        "Failed to fetch mods list from {}: {}",
                        "https://raw.githubusercontent.com/MehVahdJukaar/mod_pages/heads/master/moonlight_mods.json",
                        var1.toString()
                     );
                  state = OurModsList.State.FAILED;
               }
            },
            "Moonlight Mods List Fetcher"
         );
         t.setDaemon(true);
         t.start();
      }
   }

   public record Entry(String modId, String name, String description, @Nullable String iconUrl, @Nullable String curseforgeUrl, @Nullable String modrinthUrl) {
      public static final Codec<OurModsList.Entry> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               Codec.STRING.fieldOf("id").forGetter(OurModsList.Entry::modId),
               Codec.STRING.fieldOf("name").forGetter(OurModsList.Entry::name),
               Codec.STRING.optionalFieldOf("description", "").forGetter(OurModsList.Entry::description),
               Codec.STRING.optionalFieldOf("icon").forGetter(e -> Optional.ofNullable(e.iconUrl)),
               Codec.STRING.optionalFieldOf("curseforge").forGetter(e -> Optional.ofNullable(e.curseforgeUrl)),
               Codec.STRING.optionalFieldOf("modrinth").forGetter(e -> Optional.ofNullable(e.modrinthUrl))
            )
            .apply(
               i,
               (id, name, desc, icon, cf, mr) -> new OurModsList.Entry(
                  id, name, desc, (String)icon.orElse(null), (String)cf.orElse(null), (String)mr.orElse(null)
               )
            )
      );
   }

   public static enum State {
      NOT_STARTED,
      LOADING,
      LOADED,
      FAILED;
   }
}
