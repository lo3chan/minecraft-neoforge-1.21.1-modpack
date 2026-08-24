package net.cibernet.alchemancy.data.save;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.cibernet.alchemancy.util.RedstoneSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public class AlchemancyLevelData extends SavedData {
   private final Map<BlockPos, RedstoneSources.RedstoneSource> redstoneSources = new Object2ObjectOpenHashMap();

   public static AlchemancyLevelData compute(ServerLevel level) {
      return (AlchemancyLevelData)level.getDataStorage().computeIfAbsent(new Factory(AlchemancyLevelData::new, AlchemancyLevelData::load), "alchemancy");
   }

   public Map<BlockPos, RedstoneSources.RedstoneSource> getRedstoneSources() {
      return this.redstoneSources;
   }

   public CompoundTag save(CompoundTag tag, Provider registries) {
      ListTag redstoneSources = new ListTag();
      this.redstoneSources.forEach((pos, redstoneSource) -> {
         CompoundTag source = new CompoundTag();
         source.put("pos", NbtUtils.writeBlockPos(pos));
         source.put("data", redstoneSource.write());
         redstoneSources.add(source);
      });
      tag.put("redstone_sources", redstoneSources);
      return tag;
   }

   private static AlchemancyLevelData load(CompoundTag tag, Provider registries) {
      AlchemancyLevelData data = new AlchemancyLevelData();
      data.redstoneSources.clear();
      ListTag redstoneSources = tag.getList("restone_sources", 10);

      for (int i = 0; i < redstoneSources.size(); i++) {
         CompoundTag source = redstoneSources.getCompound(i);
         NbtUtils.readBlockPos(source, "pos").ifPresent(pos -> data.redstoneSources.put(pos, RedstoneSources.RedstoneSource.read(source.getCompound("data"))));
      }

      return data;
   }
}
