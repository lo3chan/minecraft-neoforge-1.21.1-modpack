package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_nether_dungeon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.spawner.MobSpawnerData;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallNetherDungeonMobSpawner extends StructureProcessor {
   public static final MapCodec<SmallNetherDungeonMobSpawner> CODEC = RecordCodecBuilder.mapCodec(
      codecBuilder -> codecBuilder.group(ResourceLocation.CODEC.fieldOf("spawner_mob").forGetter(SmallNetherDungeonMobSpawner::getSpawnerMob))
         .apply(codecBuilder, codecBuilder.stable(SmallNetherDungeonMobSpawner::new))
   );
   private final ResourceLocation spawnerMob;

   private SmallNetherDungeonMobSpawner(ResourceLocation spawnerMob) {
      this.spawnerMob = spawnerMob;
   }

   public ResourceLocation getSpawnerMob() {
      return this.spawnerMob;
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() instanceof SpawnerBlock) {
         MobSpawnerData spawner = MobSpawnerData.builder()
            .spawnPotentials(SimpleWeightedRandomList.single(new SpawnData((CompoundTag)Util.make(new CompoundTag(), compoundTag -> {
               compoundTag.putString("id", this.spawnerMob.toString());
               if (this.spawnerMob.toString().equals("minecraft:wither_skeleton")) {
                  compoundTag.put("ArmorItems", (Tag)Util.make(new ListTag(), armorItemsNbt -> {
                     Tag bootsNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_BOOTS)).getOrThrow();
                     armorItemsNbt.add(bootsNbt);
                     Tag leggingsNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_LEGGINGS)).getOrThrow();
                     armorItemsNbt.add(leggingsNbt);
                     Tag chestNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_CHESTPLATE)).getOrThrow();
                     armorItemsNbt.add(chestNbt);
                     Tag helmetNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_HELMET)).getOrThrow();
                     armorItemsNbt.add(helmetNbt);
                  }));
                  compoundTag.put("ArmorDropChances", (Tag)Util.make(new ListTag(), armorDropChancesNbt -> {
                     armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
                     armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
                     armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
                     armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
                  }));
                  compoundTag.put("HandItems", (Tag)Util.make(new ListTag(), handItemsNbt -> {
                     Tag stoneSwordNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.STONE_SWORD)).getOrThrow();
                     handItemsNbt.add(stoneSwordNbt);
                  }));
                  if (!BetterDungeonsCommon.CONFIG.smallNetherDungeons.witherSkeletonsDropWitherSkulls) {
                     compoundTag.putString("DeathLootTable", "minecraft:empty");
                  }
               } else if (this.spawnerMob.toString().equals("minecraft:blaze") && !BetterDungeonsCommon.CONFIG.smallNetherDungeons.blazesDropBlazeRods) {
                  compoundTag.putString("DeathLootTable", "minecraft:empty");
               }
            }), Optional.empty(), Optional.empty())))
            .setEntityType((EntityType)BuiltInRegistries.ENTITY_TYPE.get(this.spawnerMob))
            .build();
         if (this.spawnerMob.toString().equals("minecraft:wither_skeleton")) {
            spawner.nextSpawnData.getEntityToSpawn().put("ArmorItems", (Tag)Util.make(new ListTag(), armorItemsNbt -> {
               Tag bootsNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_BOOTS)).getOrThrow();
               armorItemsNbt.add(bootsNbt);
               Tag leggingsNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_LEGGINGS)).getOrThrow();
               armorItemsNbt.add(leggingsNbt);
               Tag chestNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_CHESTPLATE)).getOrThrow();
               armorItemsNbt.add(chestNbt);
               Tag helmetNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.NETHERITE_HELMET)).getOrThrow();
               armorItemsNbt.add(helmetNbt);
            }));
            spawner.nextSpawnData.getEntityToSpawn().put("ArmorDropChances", (Tag)Util.make(new ListTag(), armorDropChancesNbt -> {
               armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
               armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
               armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
               armorDropChancesNbt.add(FloatTag.valueOf(0.0F));
            }));
            spawner.nextSpawnData.getEntityToSpawn().put("HandItems", (Tag)Util.make(new ListTag(), handItemsNbt -> {
               Tag stoneSwordNbt = (Tag)ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, new ItemStack(Items.STONE_SWORD)).getOrThrow();
               handItemsNbt.add(stoneSwordNbt);
            }));
            if (!BetterDungeonsCommon.CONFIG.smallNetherDungeons.witherSkeletonsDropWitherSkulls) {
               spawner.nextSpawnData.getEntityToSpawn().putString("DeathLootTable", "minecraft:empty");
            }
         } else if (this.spawnerMob.toString().equals("minecraft:blaze") && !BetterDungeonsCommon.CONFIG.smallNetherDungeons.blazesDropBlazeRods) {
            spawner.nextSpawnData.getEntityToSpawn().putString("DeathLootTable", "minecraft:empty");
         }

         CompoundTag nbt = spawner.save();
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SPAWNER.defaultBlockState(), nbt);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_NETHER_DUNGEON_MOB_SPAWNER_PROCESSOR;
   }
}
