package net.Pandarix.block;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.Pandarix.BACommon;
import net.Pandarix.block.custom.ArchelogyTable;
import net.Pandarix.block.custom.ChickenFossilBlock;
import net.Pandarix.block.custom.ChickenFossilBodyBlock;
import net.Pandarix.block.custom.ChickenFossilHeadBlock;
import net.Pandarix.block.custom.CreeperFossilBlock;
import net.Pandarix.block.custom.CreeperFossilBodyBlock;
import net.Pandarix.block.custom.CreeperFossilHeadBlock;
import net.Pandarix.block.custom.EvokerTrapBlock;
import net.Pandarix.block.custom.FossilBaseBlock;
import net.Pandarix.block.custom.FossilBaseBodyBlock;
import net.Pandarix.block.custom.FossilBaseHeadBlock;
import net.Pandarix.block.custom.FossilBaseWithEntityBlock;
import net.Pandarix.block.custom.GrowthTotemBlock;
import net.Pandarix.block.custom.GuardianFossilBlock;
import net.Pandarix.block.custom.GuardianFossilBodyBlock;
import net.Pandarix.block.custom.GuardianFossilHeadBlock;
import net.Pandarix.block.custom.LootVaseBlock;
import net.Pandarix.block.custom.OcelotFossilBlock;
import net.Pandarix.block.custom.OcelotFossilBodyBlock;
import net.Pandarix.block.custom.OcelotFossilHeadBlock;
import net.Pandarix.block.custom.RadianceTotemBlock;
import net.Pandarix.block.custom.SheepFossilBlock;
import net.Pandarix.block.custom.SheepFossilBodyBlock;
import net.Pandarix.block.custom.SheepFossilHeadBlock;
import net.Pandarix.block.custom.SusBlock;
import net.Pandarix.block.custom.VaseBlock;
import net.Pandarix.block.custom.VillagerFossilBlock;
import net.Pandarix.block.custom.VillagerFossilBodyBlock;
import net.Pandarix.block.custom.VillagerFossilHeadBlock;
import net.Pandarix.block.custom.WolfFossilBlock;
import net.Pandarix.block.custom.WolfFossilBodyBlock;
import net.Pandarix.block.custom.WolfFossilHeadBlock;
import net.Pandarix.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
   public static final Registrar<Block> BLOCKS = BACommon.REGISTRIES.get().get(Registries.BLOCK);
   public static final RegistrySupplier<Block> SUSPICIOUS_RED_SAND = registerBlock(
      "suspicious_red_sand",
      () -> new SusBlock(
         Blocks.RED_SAND,
         Properties.of()
            .mapColor(MapColor.SAND)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.25F)
            .sound(SoundType.SUSPICIOUS_SAND)
            .pushReaction(PushReaction.DESTROY),
         SoundEvents.BRUSH_SAND,
         SoundEvents.BRUSH_SAND_COMPLETED
      )
   );
   public static final RegistrySupplier<Block> SUSPICIOUS_DIRT = registerBlock(
      "suspicious_dirt",
      () -> new SusBlock(
         Blocks.DIRT,
         Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.25F)
            .sound(SoundType.SUSPICIOUS_GRAVEL)
            .pushReaction(PushReaction.DESTROY),
         SoundEvents.BRUSH_GRAVEL,
         SoundEvents.BRUSH_GRAVEL_COMPLETED
      )
   );
   public static final RegistrySupplier<Block> FOSSILIFEROUS_DIRT = registerBlock(
      "fossiliferous_dirt",
      () -> new SusBlock(
         Blocks.DIRT,
         Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.25F)
            .sound(SoundType.SUSPICIOUS_GRAVEL)
            .pushReaction(PushReaction.DESTROY),
         SoundEvents.BRUSH_GRAVEL,
         SoundEvents.BRUSH_GRAVEL_COMPLETED
      )
   );
   public static final RegistrySupplier<Block> CHISELED_BONE_BLOCK = registerBlock(
      "chiseled_bone_block",
      () -> new RotatedPillarBlock(
         Properties.of().mapColor(MapColor.STONE).strength(0.3F).instrument(NoteBlockInstrument.XYLOPHONE).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> VILLAGER_FOSSIL = registerRareBlock(
      "villager_fossil",
      () -> new VillagerFossilBlock(
         Properties.of()
            .mapColor(MapColor.SAND)
            .instrument(NoteBlockInstrument.XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(2.0F)
            .sound(SoundType.BONE_BLOCK)
            .lightLevel(state -> (Integer)state.getValue(VillagerFossilBlock.INVENTORY_LUMINANCE))
      )
   );
   public static final RegistrySupplier<Block> VILLAGER_FOSSIL_HEAD = registerRareBlock(
      "villager_fossil_head",
      () -> new VillagerFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> VILLAGER_FOSSIL_BODY = registerRareBlock(
      "villager_fossil_body",
      () -> new VillagerFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> OCELOT_FOSSIL = registerRareBlock(
      "ocelot_fossil",
      () -> new OcelotFossilBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> OCELOT_FOSSIL_HEAD = registerRareBlock(
      "ocelot_fossil_head",
      () -> new OcelotFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> OCELOT_FOSSIL_BODY = registerRareBlock(
      "ocelot_fossil_body",
      () -> new OcelotFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> GUARDIAN_FOSSIL = registerRareBlock(
      "guardian_fossil",
      () -> new GuardianFossilBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> GUARDIAN_FOSSIL_HEAD = registerRareBlock(
      "guardian_fossil_head",
      () -> new GuardianFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> GUARDIAN_FOSSIL_BODY = registerRareBlock(
      "guardian_fossil_body",
      () -> new GuardianFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> SHEEP_FOSSIL = registerRareBlock(
      "sheep_fossil",
      () -> new SheepFossilBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> SHEEP_FOSSIL_HEAD = registerRareBlock(
      "sheep_fossil_head",
      () -> new SheepFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> SHEEP_FOSSIL_BODY = registerRareBlock(
      "sheep_fossil_body",
      () -> new SheepFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> CHICKEN_FOSSIL = registerRareBlock(
      "chicken_fossil",
      () -> new ChickenFossilBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> CHICKEN_FOSSIL_HEAD = registerRareBlock(
      "chicken_fossil_head",
      () -> new ChickenFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> CHICKEN_FOSSIL_BODY = registerRareBlock(
      "chicken_fossil_body",
      () -> new ChickenFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> CREEPER_FOSSIL = registerRareBlock(
      "creeper_fossil",
      () -> new CreeperFossilBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> CREEPER_FOSSIL_HEAD = registerRareBlock(
      "creeper_fossil_head",
      () -> new CreeperFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> CREEPER_FOSSIL_BODY = registerRareBlock(
      "creeper_fossil_body",
      () -> new CreeperFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> WOLF_FOSSIL = registerRareBlock(
      "wolf_fossil",
      () -> new WolfFossilBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> WOLF_FOSSIL_HEAD = registerRareBlock(
      "wolf_fossil_head",
      () -> new WolfFossilHeadBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final RegistrySupplier<Block> WOLF_FOSSIL_BODY = registerRareBlock(
      "wolf_fossil_body",
      () -> new WolfFossilBodyBlock(
         Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final WoodType ROTTEN_WOOD_TYPE = registerWoodType("rotten_wood");
   public static final BlockSetType ROTTEN_WOOD_BLOCKSET = registerBlockSetType("rotten_wood");
   public static final RegistrySupplier<Block> ROTTEN_LOG = registerBlock(
      "rotten_log",
      () -> new RotatedPillarBlock(
         Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).ignitedByLava().sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_PLANKS = registerBlock(
      "rotten_planks",
      () -> new Block(Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).ignitedByLava().sound(SoundType.STEM))
   );
   public static final RegistrySupplier<Block> ROTTEN_SLAB = registerBlock(
      "rotten_slab",
      () -> new SlabBlock(
         Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_STAIRS = registerBlock(
      "rotten_stairs",
      () -> new StairBlock(
         ((Block)ROTTEN_PLANKS.get()).defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_FENCE = registerBlock(
      "rotten_fence",
      () -> new FenceBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F, 3.0F)
            .ignitedByLava()
            .sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_FENCE_GATE = registerBlock(
      "rotten_fence_gate",
      () -> new FenceGateBlock(
         ROTTEN_WOOD_TYPE,
         Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F, 3.0F)
            .ignitedByLava()
            .sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_TRAPDOOR = registerBlock(
      "rotten_trapdoor",
      () -> new TrapDoorBlock(
         ROTTEN_WOOD_BLOCKSET,
         Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .instrument(NoteBlockInstrument.BASS)
            .strength(3.0F)
            .noOcclusion()
            .isValidSpawn((state, getter, pos, entityType) -> false)
            .ignitedByLava()
            .sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_DOOR = registerBlock(
      "rotten_door",
      () -> new DoorBlock(
         ROTTEN_WOOD_BLOCKSET,
         Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .instrument(NoteBlockInstrument.BASS)
            .strength(3.0F)
            .noOcclusion()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> ROTTEN_PRESSURE_PLATE = registerBlock(
      "rotten_pressure_plate",
      () -> new PressurePlateBlock(
         ROTTEN_WOOD_BLOCKSET,
         Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .strength(0.5F)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .sound(SoundType.STEM)
      )
   );
   public static final RegistrySupplier<Block> INFESTED_MUD_BRICKS = registerBlock(
      "infested_mud_bricks",
      () -> new InfestedBlock(
         Blocks.MUD_BRICKS,
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 3.0F)
            .sound(SoundType.MUD_BRICKS)
      )
   );
   public static final RegistrySupplier<Block> CRACKED_MUD_BRICKS = registerBlock(
      "cracked_mud_bricks",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 3.0F)
            .sound(SoundType.MUD_BRICKS)
      )
   );
   public static final RegistrySupplier<Block> CRACKED_MUD_BRICK_SLAB = registerBlock(
      "cracked_mud_brick_slab",
      () -> new SlabBlock(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 3.0F)
            .sound(SoundType.MUD_BRICKS)
      )
   );
   public static final RegistrySupplier<Block> CRACKED_MUD_BRICK_STAIRS = registerBlock(
      "cracked_mud_brick_stairs",
      () -> new StairBlock(
         ((Block)CRACKED_MUD_BRICKS.get()).defaultBlockState(),
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 3.0F)
            .sound(SoundType.MUD_BRICKS)
      )
   );
   public static final RegistrySupplier<Block> ARCHEOLOGY_TABLE = registerBlock(
      "archeology_table",
      () -> new ArchelogyTable(
         Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava()
      )
   );
   public static final RegistrySupplier<Block> LOOT_VASE = registerBlock(
      "loot_vase", () -> new LootVaseBlock(Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.DECORATED_POT))
   );
   public static final RegistrySupplier<Block> VASE = registerBlock(
      "vase", () -> new VaseBlock(Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.DECORATED_POT))
   );
   public static final RegistrySupplier<Block> LOOT_VASE_CREEPER = registerBlock(
      "loot_vase_creeper",
      () -> new LootVaseBlock(Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.DECORATED_POT))
   );
   public static final RegistrySupplier<Block> VASE_CREEPER = registerBlock(
      "vase_creeper", () -> new VaseBlock(Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.DECORATED_POT))
   );
   public static final RegistrySupplier<Block> LOOT_VASE_GREEN = registerBlock(
      "loot_vase_green", () -> new LootVaseBlock(Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.DECORATED_POT))
   );
   public static final RegistrySupplier<Block> VASE_GREEN = registerBlock(
      "vase_green", () -> new VaseBlock(Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.DECORATED_POT))
   );
   public static final RegistrySupplier<Block> EVOKER_TRAP = registerBlock(
      "evoker_trap",
      () -> new EvokerTrapBlock(
         Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .pushReaction(PushReaction.BLOCK)
            .strength(20.0F)
            .requiresCorrectToolForDrops()
      )
   );
   public static final RegistrySupplier<Block> GROWTH_TOTEM = registerRareBlock(
      "growth_totem",
      () -> new GrowthTotemBlock(
         MobEffects.GLOWING,
         15,
         Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .pushReaction(PushReaction.DESTROY)
            .sound(SoundType.AMETHYST)
            .offsetType(OffsetType.NONE)
            .lightLevel(state -> 15)
      )
   );
   public static final RegistrySupplier<Block> RADIANCE_TOTEM = registerRareBlock(
      "radiance_totem",
      () -> new RadianceTotemBlock(
         Properties.of()
            .mapColor(MapColor.GOLD)
            .forceSolidOn()
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.LANTERN)
            .lightLevel(p_152677_ -> 15)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
      )
   );

   public static boolean isFossil(Block block) {
      return block instanceof FossilBaseBodyBlock
         || block instanceof FossilBaseWithEntityBlock
         || block instanceof FossilBaseHeadBlock
         || block instanceof FossilBaseBlock;
   }

   private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
      return registerBlock(name, block, null);
   }

   private static <T extends Block> RegistrySupplier<T> registerRareBlock(String name, Supplier<T> block) {
      return registerBlock(name, block, Rarity.UNCOMMON);
   }

   private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, Rarity rarity) {
      RegistrySupplier<T> toReturn = BLOCKS.register(BACommon.createResource(name), block);
      registerBlockItem(name, toReturn, rarity);
      return toReturn;
   }

   private static <T extends Block> void registerBlockItem(String name, RegistrySupplier<T> block, Rarity rarity) {
      ModItems.ITEMS
         .register(
            BACommon.createResource(name),
            () -> new BlockItem(
               (Block)block.get(),
               rarity != null ? new net.minecraft.world.item.Item.Properties().rarity(rarity) : new net.minecraft.world.item.Item.Properties()
            )
         );
   }

   private static WoodType registerWoodType(String id) {
      return WoodType.register(new WoodType("betterarcheology." + id, new BlockSetType(id)));
   }

   private static BlockSetType registerBlockSetType(String id) {
      return BlockSetType.register(new BlockSetType(id));
   }

   public static void register() {
      BACommon.logRegistryEvent(BLOCKS);
   }
}
