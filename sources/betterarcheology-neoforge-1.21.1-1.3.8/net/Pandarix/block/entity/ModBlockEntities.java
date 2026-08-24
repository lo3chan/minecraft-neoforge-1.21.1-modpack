package net.Pandarix.block.entity;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.Pandarix.BACommon;
import net.Pandarix.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class ModBlockEntities {
   public static final Registrar<BlockEntityType<?>> BLOCK_ENTITIES = BACommon.REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<ArcheologyTableBlockEntity>> ARCHEOLOGY_TABLE = BLOCK_ENTITIES.register(
      BACommon.createResource("archeology_table"),
      () -> Builder.of(ArcheologyTableBlockEntity::new, new Block[]{(Block)ModBlocks.ARCHEOLOGY_TABLE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<VillagerFossilBlockEntity>> VILLAGER_FOSSIL = BLOCK_ENTITIES.register(
      BACommon.createResource("villager_fossil"),
      () -> Builder.of(VillagerFossilBlockEntity::new, new Block[]{(Block)ModBlocks.VILLAGER_FOSSIL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ChickenFossilBlockEntity>> CHICKEN_FOSSIL = BLOCK_ENTITIES.register(
      BACommon.createResource("chicken_fossil"),
      () -> Builder.of(ChickenFossilBlockEntity::new, new Block[]{(Block)ModBlocks.CHICKEN_FOSSIL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<GuardianFossilBlockEntity>> GUARDIAN_FOSSIL = BLOCK_ENTITIES.register(
      BACommon.createResource("guardian_fossil"),
      () -> Builder.of(GuardianFossilBlockEntity::new, new Block[]{(Block)ModBlocks.GUARDIAN_FOSSIL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<FleeFromBlockEntity>> FLEE_FROM = BLOCK_ENTITIES.register(
      BACommon.createResource("flee_from"), () -> Builder.of(FleeFromBlockEntity::new, new Block[]{(Block)ModBlocks.OCELOT_FOSSIL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SkeletonFleeFromBlockEntity>> SKELETON_FLEE_FROM = BLOCK_ENTITIES.register(
      BACommon.createResource("skeleton_flee_from"),
      () -> Builder.of(SkeletonFleeFromBlockEntity::new, new Block[]{(Block)ModBlocks.WOLF_FOSSIL.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<RadianceTotemBlockEntity>> RADIANCE_TOTEM = BLOCK_ENTITIES.register(
      BACommon.createResource("radiance_totem"),
      () -> Builder.of(RadianceTotemBlockEntity::new, new Block[]{(Block)ModBlocks.RADIANCE_TOTEM.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SusBlockEntity>> SUSBLOCK = BLOCK_ENTITIES.register(
      BACommon.createResource("sus_block"),
      () -> Builder.of(
            SusBlockEntity::new,
            new Block[]{(Block)ModBlocks.SUSPICIOUS_DIRT.get(), (Block)ModBlocks.SUSPICIOUS_RED_SAND.get(), (Block)ModBlocks.FOSSILIFEROUS_DIRT.get()}
         )
         .build(null)
   );

   public static void register() {
      BACommon.logRegistryEvent(BLOCK_ENTITIES);
   }
}
