package net.astralya.hexalia.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.custom.AegifloraBlockEntity;
import net.astralya.hexalia.block.entity.custom.AstrylisBlockEntity;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.astralya.hexalia.block.entity.custom.DreamcatcherBlockEntity;
import net.astralya.hexalia.block.entity.custom.EggClusterBlockEntity;
import net.astralya.hexalia.block.entity.custom.GrimshadeBlockEntity;
import net.astralya.hexalia.block.entity.custom.LourdesBlockEntity;
import net.astralya.hexalia.block.entity.custom.MortarAndPestleBlockEntity;
import net.astralya.hexalia.block.entity.custom.NautiliteBlockEntity;
import net.astralya.hexalia.block.entity.custom.NestingBlockEntity;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.astralya.hexalia.block.entity.custom.WindsongBlockEntity;
import net.astralya.hexalia.block.entity.wood.ModHangingSignBlockEntity;
import net.astralya.hexalia.block.entity.wood.ModSignBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public final class ModBlockEntityTypes {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create("hexalia", Registries.BLOCK_ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<RitualTableBlockEntity>> RITUAL_TABLE = BLOCK_ENTITY_TYPES.register(
      "ritual_table", () -> Builder.of(RitualTableBlockEntity::new, new Block[]{(Block)ModBlocks.RITUAL_TABLE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<RitualBrazierBlockEntity>> RITUAL_BRAZIER = BLOCK_ENTITY_TYPES.register(
      "ritual_brazier", () -> Builder.of(RitualBrazierBlockEntity::new, new Block[]{(Block)ModBlocks.RITUAL_BRAZIER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SmallCauldronBlockEntity>> SMALL_CAULDRON = BLOCK_ENTITY_TYPES.register(
      "small_cauldron", () -> Builder.of(SmallCauldronBlockEntity::new, new Block[]{(Block)ModBlocks.SMALL_CAULDRON.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<MortarAndPestleBlockEntity>> MORTAR_AND_PESTLE = BLOCK_ENTITY_TYPES.register(
      "mortar_and_pestle", () -> Builder.of(MortarAndPestleBlockEntity::new, new Block[]{(Block)ModBlocks.MORTAR_AND_PESTLE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CenserBlockEntity>> CENSER = BLOCK_ENTITY_TYPES.register(
      "censer", () -> Builder.of(CenserBlockEntity::new, new Block[]{(Block)ModBlocks.CENSER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<EggClusterBlockEntity>> EGG_CLUSTER = BLOCK_ENTITY_TYPES.register(
      "egg_cluster", () -> Builder.of(EggClusterBlockEntity::new, new Block[]{(Block)ModBlocks.EGG_CLUSTER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<NestingBlockEntity>> NESTING_BLOCK = BLOCK_ENTITY_TYPES.register(
      "nesting_block", () -> Builder.of(NestingBlockEntity::new, new Block[]{(Block)ModBlocks.NESTING_BLOCK.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ShelfBlockEntity>> SHELF = BLOCK_ENTITY_TYPES.register(
      "shelf", () -> Builder.of(ShelfBlockEntity::new, new Block[]{(Block)ModBlocks.SHELF.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<DreamcatcherBlockEntity>> DREAMCATCHER = BLOCK_ENTITY_TYPES.register(
      "dreamcatcher", () -> Builder.of(DreamcatcherBlockEntity::new, new Block[]{(Block)ModBlocks.DREAMCATCHER.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<NautiliteBlockEntity>> NAUTILITE = BLOCK_ENTITY_TYPES.register(
      "nautilite", () -> Builder.of(NautiliteBlockEntity::new, new Block[]{(Block)ModBlocks.NAUTILITE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<AstrylisBlockEntity>> ASTRYLIS = BLOCK_ENTITY_TYPES.register(
      "astrylis", () -> Builder.of(AstrylisBlockEntity::new, new Block[]{(Block)ModBlocks.ASTRYLIS.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<WindsongBlockEntity>> WINDSONG = BLOCK_ENTITY_TYPES.register(
      "windsong", () -> Builder.of(WindsongBlockEntity::new, new Block[]{(Block)ModBlocks.WINDSONG.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<GrimshadeBlockEntity>> GRIMSHADE = BLOCK_ENTITY_TYPES.register(
      "grimshade", () -> Builder.of(GrimshadeBlockEntity::new, new Block[]{(Block)ModBlocks.GRIMSHADE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<LourdesBlockEntity>> LOURDES = BLOCK_ENTITY_TYPES.register(
      "lourdes", () -> Builder.of(LourdesBlockEntity::new, new Block[]{(Block)ModBlocks.LOURDES.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<AegifloraBlockEntity>> AEGIFLORA = BLOCK_ENTITY_TYPES.register(
      "aegiflora",
      () -> Builder.of(AegifloraBlockEntity::new, new Block[]{(Block)ModBlocks.AEGIFLORA.get(), (Block)ModBlocks.WITHERED_AEGIFLORA.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ModSignBlockEntity>> MOD_SIGN = BLOCK_ENTITY_TYPES.register(
      "sign",
      () -> Builder.of(
            ModSignBlockEntity::new,
            new Block[]{
               (Block)ModBlocks.COTTONWOOD_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_WALL_SIGN.get(),
               (Block)ModBlocks.WILLOW_SIGN.get(),
               (Block)ModBlocks.WILLOW_WALL_SIGN.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ModHangingSignBlockEntity>> MOD_HANGING_SIGN = BLOCK_ENTITY_TYPES.register(
      "hanging_sign",
      () -> Builder.of(
            ModHangingSignBlockEntity::new,
            new Block[]{
               (Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_WALL_SIGN.get()
            }
         )
         .build(null)
   );

   private ModBlockEntityTypes() {
   }

   public static void init() {
      BLOCK_ENTITY_TYPES.register();
   }
}
