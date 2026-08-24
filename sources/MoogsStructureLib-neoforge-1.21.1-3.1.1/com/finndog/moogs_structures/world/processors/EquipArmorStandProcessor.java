package com.finndog.moogs_structures.world.processors;

import com.finndog.moogs_structures.modinit.MoogsStructuresProcessors;
import com.finndog.moogs_structures.utils.GeneralUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;

public class EquipArmorStandProcessor extends StructureEntityProcessor {
   public static final MapCodec<EquipArmorStandProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.mapPair(EquipArmorStandProcessor.ArmorSet.CODEC.fieldOf("armor"), Codec.intRange(1, 2147483647).fieldOf("weight"))
               .codec()
               .listOf()
               .fieldOf("armor_sets")
               .forGetter(p -> p.weightedSets)
         )
         .apply(instance, instance.stable(EquipArmorStandProcessor::new))
   );
   public final List<Pair<EquipArmorStandProcessor.ArmorSet, Integer>> weightedSets;

   private EquipArmorStandProcessor(List<Pair<EquipArmorStandProcessor.ArmorSet, Integer>> weightedSets) {
      this.weightedSets = weightedSets;
   }

   @Override
   public StructureEntityInfo processEntity(
      ServerLevelAccessor serverLevelAccessor,
      BlockPos structurePiecePos,
      BlockPos structurePieceBottomCenterPos,
      StructureEntityInfo localEntityInfo,
      StructureEntityInfo globalEntityInfo,
      StructurePlaceSettings structurePlaceSettings
   ) {
      CompoundTag nbt = globalEntityInfo.nbt;
      if (nbt == null || !"minecraft:armor_stand".equals(nbt.getString("id"))) {
         return globalEntityInfo;
      } else if (this.weightedSets.isEmpty()) {
         return globalEntityInfo;
      } else {
         RandomSource random = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);
         EquipArmorStandProcessor.ArmorSet set = GeneralUtils.getRandomEntry(this.weightedSets, random);
         if (set == null) {
            return globalEntityInfo;
         } else {
            Provider provider = serverLevelAccessor.registryAccess();
            CompoundTag newNbt = nbt.copy();
            ListTag armorItems = new ListTag();
            armorItems.add(saveOrEmpty(set.feet(), provider));
            armorItems.add(saveOrEmpty(set.legs(), provider));
            armorItems.add(saveOrEmpty(set.chest(), provider));
            armorItems.add(saveOrEmpty(set.head(), provider));
            newNbt.put("ArmorItems", armorItems);
            return new StructureEntityInfo(globalEntityInfo.pos, globalEntityInfo.blockPos, newNbt);
         }
      }
   }

   private static Tag saveOrEmpty(Optional<ItemStack> optionalStack, Provider provider) {
      return (Tag)(!optionalStack.isEmpty() && !optionalStack.get().isEmpty() ? optionalStack.get().save(provider) : new CompoundTag());
   }

   protected StructureProcessorType<?> getType() {
      return MoogsStructuresProcessors.EQUIP_ARMOR_STAND_PROCESSOR.get();
   }

   public record ArmorSet(Optional<ItemStack> head, Optional<ItemStack> chest, Optional<ItemStack> legs, Optional<ItemStack> feet) {
      public static final Codec<EquipArmorStandProcessor.ArmorSet> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("head").forGetter(EquipArmorStandProcessor.ArmorSet::head),
               ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("chest").forGetter(EquipArmorStandProcessor.ArmorSet::chest),
               ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("legs").forGetter(EquipArmorStandProcessor.ArmorSet::legs),
               ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("feet").forGetter(EquipArmorStandProcessor.ArmorSet::feet)
            )
            .apply(instance, EquipArmorStandProcessor.ArmorSet::new)
      );
   }
}
