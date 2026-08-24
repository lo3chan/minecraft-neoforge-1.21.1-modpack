package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherBlockEntityTypes {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "aether");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AetherFlowerBlockEntity>> FLOWER = BLOCK_ENTITY_TYPES.register(
      "flower",
      () -> Builder.of(AetherFlowerBlockEntity::new, new Block[]{(Block)AetherBlocks.PURPLE_FLOWER.get(), (Block)AetherBlocks.WHITE_FLOWER.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IcestoneBlockEntity>> ICESTONE = BLOCK_ENTITY_TYPES.register(
      "icestone",
      () -> Builder.of(
            IcestoneBlockEntity::new,
            new Block[]{
               (Block)AetherBlocks.ICESTONE.get(),
               (Block)AetherBlocks.ICESTONE_SLAB.get(),
               (Block)AetherBlocks.ICESTONE_STAIRS.get(),
               (Block)AetherBlocks.ICESTONE_WALL.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarBlockEntity>> ALTAR = BLOCK_ENTITY_TYPES.register(
      "altar", () -> Builder.of(AltarBlockEntity::new, new Block[]{(Block)AetherBlocks.ALTAR.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FreezerBlockEntity>> FREEZER = BLOCK_ENTITY_TYPES.register(
      "freezer", () -> Builder.of(FreezerBlockEntity::new, new Block[]{(Block)AetherBlocks.FREEZER.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncubatorBlockEntity>> INCUBATOR = BLOCK_ENTITY_TYPES.register(
      "incubator", () -> Builder.of(IncubatorBlockEntity::new, new Block[]{(Block)AetherBlocks.INCUBATOR.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChestMimicBlockEntity>> CHEST_MIMIC = BLOCK_ENTITY_TYPES.register(
      "chest_mimic", () -> Builder.of(ChestMimicBlockEntity::new, new Block[]{(Block)AetherBlocks.CHEST_MIMIC.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TreasureChestBlockEntity>> TREASURE_CHEST = BLOCK_ENTITY_TYPES.register(
      "treasure_chest", () -> Builder.of(TreasureChestBlockEntity::new, new Block[]{(Block)AetherBlocks.TREASURE_CHEST.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkyrootBedBlockEntity>> SKYROOT_BED = BLOCK_ENTITY_TYPES.register(
      "skyroot_bed", () -> Builder.of(SkyrootBedBlockEntity::new, new Block[]{(Block)AetherBlocks.SKYROOT_BED.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkyrootSignBlockEntity>> SKYROOT_SIGN = BLOCK_ENTITY_TYPES.register(
      "skyroot_sign",
      () -> Builder.of(SkyrootSignBlockEntity::new, new Block[]{(Block)AetherBlocks.SKYROOT_WALL_SIGN.get(), (Block)AetherBlocks.SKYROOT_SIGN.get()})
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkyrootHangingSignBlockEntity>> SKYROOT_HANGING_SIGN = BLOCK_ENTITY_TYPES.register(
      "skyroot_hanging_sign",
      () -> Builder.of(
            SkyrootHangingSignBlockEntity::new,
            new Block[]{(Block)AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get(), (Block)AetherBlocks.SKYROOT_HANGING_SIGN.get()}
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunAltarBlockEntity>> SUN_ALTAR = BLOCK_ENTITY_TYPES.register(
      "sun_altar", () -> Builder.of(SunAltarBlockEntity::new, new Block[]{(Block)AetherBlocks.SUN_ALTAR.get()}).build(null)
   );
}
