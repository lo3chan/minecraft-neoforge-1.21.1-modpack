package io.wispforest.owo.offline;

import io.wispforest.owo.Owo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

public final class OfflineDataLookup {
   private OfflineDataLookup() {
   }

   public static void put(UUID player, CompoundTag nbt) {
      ((DataSavedEvents.PlayerData)DataSavedEvents.PLAYER_DATA.invoker()).onSaved(player, nbt);

      try {
         Path savedPlayersPath = Owo.currentServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
         Path file = Files.createTempFile(savedPlayersPath, player.toString() + "-", ".dat");
         NbtIo.writeCompressed(nbt, file);
         Path newDataFile = savedPlayersPath.resolve(player + ".dat");
         Path oldDataFile = savedPlayersPath.resolve(player + ".dat_old");
         Util.safeReplaceFile(newDataFile, file, oldDataFile);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      }
   }

   @Nullable
   public static CompoundTag get(UUID player) {
      try {
         Path savedPlayersPath = Owo.currentServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
         Path savedDataPath = savedPlayersPath.resolve(player.toString() + ".dat");
         CompoundTag rawNbt = NbtIo.readCompressed(savedDataPath, NbtAccounter.unlimitedHeap());
         int dataVersion = rawNbt.contains("DataVersion", 3) ? rawNbt.getInt("DataVersion") : -1;
         return DataFixTypes.PLAYER.updateToCurrentVersion(DataFixers.getDataFixer(), rawNbt, dataVersion);
      } catch (IOException var5) {
         Owo.LOGGER.error("Couldn't get player data for offline player {}", player, var5);
         return null;
      }
   }

   public static void edit(UUID player, Function<CompoundTag, CompoundTag> editor) {
      put(player, editor.apply(get(player)));
   }

   public static List<UUID> savedPlayers() {
      List<UUID> list = new ArrayList<>();
      Path savedPlayersPath = Owo.currentServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
      if (!Files.isDirectory(savedPlayersPath)) {
         return Collections.emptyList();
      } else {
         try {
            Iterator<Path> iterator = Files.list(savedPlayersPath).iterator();

            while (iterator.hasNext()) {
               Path savedPlayerFile = iterator.next();
               if (!Files.isDirectory(savedPlayerFile) && savedPlayerFile.toString().endsWith(".dat")) {
                  try {
                     String filename = savedPlayerFile.getFileName().toString();
                     String uuidStr = filename.substring(0, filename.lastIndexOf(46));
                     UUID uuid = UUID.fromString(uuidStr);
                     list.add(uuid);
                  } catch (IllegalArgumentException var7) {
                     Owo.LOGGER.error("Encountered invalid UUID in playerdata directory", var7);
                  }
               }
            }

            return list;
         } catch (IOException var8) {
            Owo.LOGGER.error("Couldn't list offline player UUIDs", var8);
            throw new RuntimeException(var8);
         }
      }
   }
}
