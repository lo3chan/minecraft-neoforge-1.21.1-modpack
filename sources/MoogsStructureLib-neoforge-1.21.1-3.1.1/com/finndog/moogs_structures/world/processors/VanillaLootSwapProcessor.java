package com.finndog.moogs_structures.world.processors;

import com.finndog.moogs_structures.config.ReplaceVanillaManager;
import com.finndog.moogs_structures.modinit.MoogsStructuresProcessors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class VanillaLootSwapProcessor extends StructureProcessor {
   public static final MapCodec<VanillaLootSwapProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.STRING.fieldOf("modid").forGetter(p -> p.modid),
            Codec.STRING.fieldOf("vanilla_key").forGetter(p -> p.vanillaKey),
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("loot_table_mapping").forGetter(p -> p.lootTableMapping),
            Codec.STRING.optionalFieldOf("seed_strategy", "preserve").forGetter(p -> p.seedStrategy)
         )
         .apply(instance, instance.stable(VanillaLootSwapProcessor::new))
   );
   private final String modid;
   private final String vanillaKey;
   private final Map<ResourceLocation, ResourceLocation> lootTableMapping;
   private final String seedStrategy;

   private VanillaLootSwapProcessor(String modid, String vanillaKey, Map<ResourceLocation, ResourceLocation> lootTableMapping, String seedStrategy) {
      this.modid = modid;
      this.vanillaKey = vanillaKey;
      this.lootTableMapping = lootTableMapping;
      this.seedStrategy = seedStrategy;
   }

   public StructureBlockInfo processBlock(
      LevelReader worldReader, BlockPos pos, BlockPos blockPos, StructureBlockInfo localInfo, StructureBlockInfo worldInfo, StructurePlaceSettings settings
   ) {
      CompoundTag nbt = worldInfo.nbt();
      if (nbt != null && nbt.contains("LootTable", 8)) {
         ResourceLocation current = ResourceLocation.tryParse(nbt.getString("LootTable"));
         ResourceLocation target = current == null ? null : this.lootTableMapping.get(current);
         if (target != null && ReplaceVanillaManager.isEnabled(this.modid, this.vanillaKey)) {
            CompoundTag newNbt = nbt.copy();
            newNbt.putString("LootTable", target.toString());
            String var11 = this.seedStrategy;
            switch (var11) {
               case "randomize":
                  newNbt.putLong("LootTableSeed", settings.getRandom(worldInfo.pos()).nextLong());
                  break;
               case "clear":
                  newNbt.remove("LootTableSeed");
            }

            return new StructureBlockInfo(worldInfo.pos(), worldInfo.state(), newNbt);
         } else {
            return worldInfo;
         }
      } else {
         return worldInfo;
      }
   }

   protected StructureProcessorType<?> getType() {
      return MoogsStructuresProcessors.VANILLA_LOOT_SWAP_PROCESSOR.get();
   }
}
