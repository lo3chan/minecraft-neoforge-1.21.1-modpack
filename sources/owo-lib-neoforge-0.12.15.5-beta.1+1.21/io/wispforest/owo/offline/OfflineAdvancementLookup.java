package io.wispforest.owo.offline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.offline.AdvancementProgressAccessor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

public final class OfflineAdvancementLookup {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   public static final Codec<Map<ResourceLocation, AdvancementProgress>> CODEC = DataFixTypes.ADVANCEMENTS
      .wrapCodec(Codec.unboundedMap(ResourceLocation.CODEC, AdvancementProgress.CODEC), DataFixers.getDataFixer(), 1343);

   private OfflineAdvancementLookup() {
   }

   public static void put(UUID player, Map<ResourceLocation, AdvancementProgress> map) {
      ((DataSavedEvents.Advancements)DataSavedEvents.ADVANCEMENTS.invoker()).onSaved(player, map);

      try {
         Path advancementsPath = Owo.currentServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR);
         Path advancementPath = advancementsPath.resolve(player.toString() + ".json");
         JsonElement saved = (JsonElement)CODEC.encodeStart(JsonOps.INSTANCE, map).getOrThrow(IllegalStateException::new);

         try (BufferedWriter bw = Files.newBufferedWriter(advancementPath)) {
            GSON.toJson(saved, bw);
         }
      } catch (IOException var10) {
         Owo.LOGGER.error("Couldn't save advancements of offline player {}", player, var10);
         throw new RuntimeException(var10);
      }
   }

   @Nullable
   public static Map<ResourceLocation, AdvancementProgress> get(UUID player) {
      try {
         Path advancementsPath = Owo.currentServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR);
         if (!Files.exists(advancementsPath)) {
            return null;
         } else {
            Path advancementFile = advancementsPath.resolve(player + ".json");
            if (!Files.exists(advancementFile)) {
               return null;
            } else {
               Map<ResourceLocation, AdvancementProgress> parsedMap;
               try (
                  InputStream s = Files.newInputStream(advancementFile);
                  InputStreamReader streamReader = new InputStreamReader(s);
               ) {
                  JsonReader reader = new JsonReader(streamReader);

                  try {
                     reader.setLenient(false);
                     JsonElement jsonElement = Streams.parse(reader);
                     parsedMap = (Map<ResourceLocation, AdvancementProgress>)CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                  } catch (Throwable var12) {
                     try {
                        reader.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }

                     throw var12;
                  }

                  reader.close();
               }

               for (Entry<ResourceLocation, AdvancementProgress> entry : parsedMap.entrySet()) {
                  AdvancementRequirements requirements = ((AdvancementProgressAccessor)entry.getValue()).getRequirements();
                  if (requirements.size() == 0) {
                     AdvancementHolder adv = Owo.currentServer().getAdvancements().get(entry.getKey());
                     if (adv != null) {
                        ((AdvancementProgressAccessor)entry.getValue()).setRequirements(adv.value().requirements());
                     }
                  }
               }

               return parsedMap;
            }
         }
      } catch (IOException var15) {
         Owo.LOGGER.error("Couldn't get advancements for offline player {}", player, var15);
         throw new RuntimeException(var15);
      }
   }

   public static void edit(UUID player, Consumer<OfflineAdvancementState> editor) {
      Map<ResourceLocation, AdvancementProgress> advancementData = get(player);
      if (advancementData == null) {
         advancementData = new HashMap<>();
      }

      OfflineAdvancementState transaction = new OfflineAdvancementState(advancementData);
      editor.accept(transaction);
      put(player, transaction.advancementData());
   }

   public static List<UUID> savedPlayers() {
      Path advancementsPath = Owo.currentServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR);
      if (!Files.isDirectory(advancementsPath)) {
         return Collections.emptyList();
      } else {
         List<UUID> list = new ArrayList<>();

         try {
            Iterator<Path> iterator = Files.list(advancementsPath).iterator();

            while (iterator.hasNext()) {
               Path savedPlayerFile = iterator.next();
               if (!Files.isDirectory(savedPlayerFile) && savedPlayerFile.toString().endsWith(".json")) {
                  try {
                     String filename = savedPlayerFile.getFileName().toString();
                     String uuidStr = filename.substring(0, filename.lastIndexOf(46));
                     UUID uuid = UUID.fromString(uuidStr);
                     list.add(uuid);
                  } catch (IllegalArgumentException var7) {
                     Owo.LOGGER.error("Encountered invalid UUID in advancements directory", var7);
                  }
               }
            }

            return list;
         } catch (IOException var8) {
            throw new RuntimeException(var8);
         }
      }
   }
}
