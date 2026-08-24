package com.aetherteam.aether.block;

import com.aetherteam.aether.block.construction.AerogelBlock;
import com.aetherteam.aether.block.construction.AerogelSlabBlock;
import com.aetherteam.aether.block.construction.AerogelStairsBlock;
import com.aetherteam.aether.block.construction.AerogelWallBlock;
import com.aetherteam.aether.block.construction.AetherDirtPathBlock;
import com.aetherteam.aether.block.construction.AetherFarmBlock;
import com.aetherteam.aether.block.construction.BookshelfBlock;
import com.aetherteam.aether.block.construction.IcestoneSlabBlock;
import com.aetherteam.aether.block.construction.IcestoneStairsBlock;
import com.aetherteam.aether.block.construction.IcestoneWallBlock;
import com.aetherteam.aether.block.construction.QuicksoilGlassBlock;
import com.aetherteam.aether.block.construction.QuicksoilGlassPaneBlock;
import com.aetherteam.aether.block.construction.SkyrootCeilingHangingSignBlock;
import com.aetherteam.aether.block.construction.SkyrootSignBlock;
import com.aetherteam.aether.block.construction.SkyrootWallHangingSignBlock;
import com.aetherteam.aether.block.construction.SkyrootWallSignBlock;
import com.aetherteam.aether.block.dungeon.ChestMimicBlock;
import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.aetherteam.aether.block.dungeon.TrappedBlock;
import com.aetherteam.aether.block.dungeon.TreasureChestBlock;
import com.aetherteam.aether.block.dungeon.TreasureDoorwayBlock;
import com.aetherteam.aether.block.miscellaneous.AetherFrostedIceBlock;
import com.aetherteam.aether.block.miscellaneous.FacingPillarBlock;
import com.aetherteam.aether.block.miscellaneous.FloatingBlock;
import com.aetherteam.aether.block.miscellaneous.UnstableObsidianBlock;
import com.aetherteam.aether.block.natural.AercloudBlock;
import com.aetherteam.aether.block.natural.AetherDoubleDropBlock;
import com.aetherteam.aether.block.natural.AetherDoubleDropsLeaves;
import com.aetherteam.aether.block.natural.AetherDoubleDropsOreBlock;
import com.aetherteam.aether.block.natural.AetherFlowerBlock;
import com.aetherteam.aether.block.natural.AetherGrassBlock;
import com.aetherteam.aether.block.natural.AetherLogBlock;
import com.aetherteam.aether.block.natural.BerryBushBlock;
import com.aetherteam.aether.block.natural.BerryBushStemBlock;
import com.aetherteam.aether.block.natural.BlueAercloudBlock;
import com.aetherteam.aether.block.natural.CrystalFruitLeavesBlock;
import com.aetherteam.aether.block.natural.EnchantedAetherGrassBlock;
import com.aetherteam.aether.block.natural.IcestoneBlock;
import com.aetherteam.aether.block.natural.LeavesWithParticlesBlock;
import com.aetherteam.aether.block.natural.QuicksoilBlock;
import com.aetherteam.aether.block.portal.AetherPortalBlock;
import com.aetherteam.aether.block.utility.AltarBlock;
import com.aetherteam.aether.block.utility.FreezerBlock;
import com.aetherteam.aether.block.utility.IncubatorBlock;
import com.aetherteam.aether.block.utility.SkyrootBedBlock;
import com.aetherteam.aether.block.utility.SunAltarBlock;
import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import com.aetherteam.aether.blockentity.SkyrootBedBlockEntity;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.mixin.mixins.common.accessor.FireBlockAccessor;
import com.aetherteam.aether.world.treegrower.AetherTreeGrowers;
import com.aetherteam.nitrogen.item.block.EntityBlockItem;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class AetherBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("aether");
   public static final DeferredBlock<AetherPortalBlock> AETHER_PORTAL = BLOCKS.register(
      "aether_portal",
      () -> new AetherPortalBlock(
         Properties.of()
            .noCollission()
            .randomTicks()
            .strength(-1.0F)
            .sound(SoundType.GLASS)
            .lightLevel(AetherBlocks::lightLevel11)
            .pushReaction(PushReaction.BLOCK)
            .forceSolidOn()
      )
   );
   public static final DeferredBlock<Block> AETHER_GRASS_BLOCK = register(
      "aether_grass_block",
      () -> new AetherGrassBlock(Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).randomTicks().strength(0.2F).sound(SoundType.GRASS))
   );
   public static final DeferredBlock<Block> ENCHANTED_AETHER_GRASS_BLOCK = register(
      "enchanted_aether_grass_block",
      () -> new EnchantedAetherGrassBlock(Properties.of().mapColor(MapColor.GOLD).randomTicks().strength(0.2F).sound(SoundType.GRASS))
   );
   public static final DeferredBlock<Block> AETHER_DIRT = register(
      "aether_dirt", () -> new AetherDoubleDropBlock(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(0.2F).sound(SoundType.GRAVEL))
   );
   public static final DeferredBlock<Block> QUICKSOIL = register(
      "quicksoil",
      () -> new QuicksoilBlock(
         Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.SNARE).strength(0.5F).friction(1.1F).sound(SoundType.SAND)
      )
   );
   public static final DeferredBlock<Block> HOLYSTONE = register(
      "holystone",
      () -> new AetherDoubleDropBlock(
         Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MOSSY_HOLYSTONE = register(
      "mossy_holystone", () -> new AetherDoubleDropBlock(Properties.ofFullCopy((BlockBehaviour)HOLYSTONE.get()))
   );
   public static final DeferredBlock<Block> AETHER_FARMLAND = register(
      "aether_farmland",
      () -> new AetherFarmBlock(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_CYAN)
            .randomTicks()
            .strength(0.2F)
            .sound(SoundType.GRAVEL)
            .isViewBlocking(AetherBlocks::always)
            .isSuffocating(AetherBlocks::always)
      )
   );
   public static final DeferredBlock<Block> AETHER_DIRT_PATH = register(
      "aether_dirt_path",
      () -> new AetherDirtPathBlock(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_CYAN)
            .strength(0.2F)
            .sound(SoundType.GRASS)
            .isViewBlocking(AetherBlocks::always)
            .isSuffocating(AetherBlocks::always)
      )
   );
   public static final DeferredBlock<Block> COLD_AERCLOUD = register(
      "cold_aercloud",
      () -> new AercloudBlock(
         Properties.of()
            .mapColor(MapColor.SNOW)
            .instrument(NoteBlockInstrument.FLUTE)
            .strength(0.3F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .dynamicShape()
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> BLUE_AERCLOUD = register(
      "blue_aercloud",
      () -> new BlueAercloudBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .instrument(NoteBlockInstrument.FLUTE)
            .strength(0.3F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .dynamicShape()
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> GOLDEN_AERCLOUD = register(
      "golden_aercloud",
      () -> new AercloudBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.FLUTE)
            .strength(0.3F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .dynamicShape()
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> ICESTONE = register(
      "icestone",
      () -> new IcestoneBlock(
         Properties.of()
            .mapColor(MapColor.ICE)
            .instrument(NoteBlockInstrument.CHIME)
            .strength(0.5F)
            .randomTicks()
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> AMBROSIUM_ORE = register(
      "ambrosium_ore",
      () -> new AetherDoubleDropsOreBlock(
         UniformInt.of(0, 2), Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ZANITE_ORE = register(
      "zanite_ore",
      () -> new DropExperienceBlock(
         UniformInt.of(3, 5), Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAVITITE_ORE = register(
      "gravitite_ore",
      () -> new FloatingBlock(
         false, Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).randomTicks().requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> SKYROOT_LEAVES = register(
      "skyroot_leaves",
      () -> new AetherDoubleDropsLeaves(
         Properties.of()
            .mapColor(MapColor.GRASS)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> GOLDEN_OAK_LEAVES = register(
      "golden_oak_leaves",
      () -> new LeavesWithParticlesBlock(
         AetherParticleTypes.GOLDEN_OAK_LEAVES,
         Properties.of()
            .mapColor(MapColor.GOLD)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> CRYSTAL_LEAVES = register(
      "crystal_leaves",
      () -> new LeavesWithParticlesBlock(
         AetherParticleTypes.CRYSTAL_LEAVES,
         Properties.of()
            .mapColor(MapColor.DIAMOND)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> CRYSTAL_FRUIT_LEAVES = register(
      "crystal_fruit_leaves",
      () -> new CrystalFruitLeavesBlock(
         AetherParticleTypes.CRYSTAL_LEAVES,
         Properties.of()
            .mapColor(MapColor.DIAMOND)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> HOLIDAY_LEAVES = register(
      "holiday_leaves",
      () -> new LeavesWithParticlesBlock(
         AetherParticleTypes.HOLIDAY_LEAVES,
         Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> DECORATED_HOLIDAY_LEAVES = register(
      "decorated_holiday_leaves",
      () -> new LeavesWithParticlesBlock(
         AetherParticleTypes.HOLIDAY_LEAVES,
         Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<RotatedPillarBlock> SKYROOT_LOG = register(
      "skyroot_log", () -> new AetherLogBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_LOG))
   );
   public static final DeferredBlock<RotatedPillarBlock> GOLDEN_OAK_LOG = register(
      "golden_oak_log", () -> new AetherLogBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_LOG))
   );
   public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SKYROOT_LOG = register(
      "stripped_skyroot_log", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG))
   );
   public static final DeferredBlock<RotatedPillarBlock> SKYROOT_WOOD = register(
      "skyroot_wood", () -> new AetherLogBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_WOOD))
   );
   public static final DeferredBlock<RotatedPillarBlock> GOLDEN_OAK_WOOD = register(
      "golden_oak_wood", () -> new AetherLogBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_WOOD))
   );
   public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SKYROOT_WOOD = register(
      "stripped_skyroot_wood", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STRIPPED_OAK_WOOD))
   );
   public static final DeferredBlock<Block> SKYROOT_PLANKS = register(
      "skyroot_planks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> HOLYSTONE_BRICKS = register(
      "holystone_bricks",
      () -> new Block(Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<TransparentBlock> QUICKSOIL_GLASS = register(
      "quicksoil_glass",
      () -> new QuicksoilGlassBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.2F)
            .friction(1.1F)
            .lightLevel(AetherBlocks::lightLevel11)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::never)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<IronBarsBlock> QUICKSOIL_GLASS_PANE = register(
      "quicksoil_glass_pane",
      () -> new QuicksoilGlassPaneBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.2F)
            .friction(1.1F)
            .lightLevel(AetherBlocks::lightLevel11)
            .sound(SoundType.GLASS)
            .noOcclusion()
      )
   );
   public static final DeferredBlock<Block> AEROGEL = register(
      "aerogel",
      () -> new AerogelBlock(
         Properties.of()
            .mapColor(MapColor.DIAMOND)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .strength(1.0F, 2000.0F)
            .sound(SoundType.METAL)
            .noOcclusion()
            .requiresCorrectToolForDrops()
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> AMBROSIUM_BLOCK = register(
      "ambrosium_block",
      () -> new Block(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> ZANITE_BLOCK = register(
      "zanite_block",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .instrument(NoteBlockInstrument.BIT)
            .strength(5.0F, 6.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredBlock<Block> ENCHANTED_GRAVITITE = register(
      "enchanted_gravitite",
      () -> new FloatingBlock(
         true,
         Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .instrument(NoteBlockInstrument.PLING)
            .strength(5.0F, 6.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredBlock<Block> ALTAR = register(
      "altar", () -> new AltarBlock(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(2.5F))
   );
   public static final DeferredBlock<Block> FREEZER = register(
      "freezer", () -> new FreezerBlock(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F))
   );
   public static final DeferredBlock<Block> INCUBATOR = register(
      "incubator", () -> new IncubatorBlock(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F))
   );
   public static final DeferredBlock<Block> AMBROSIUM_WALL_TORCH = BLOCKS.register(
      "ambrosium_wall_torch", () -> new WallTorchBlock(ParticleTypes.SMOKE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WALL_TORCH))
   );
   public static final DeferredBlock<Block> AMBROSIUM_TORCH = register(
      "ambrosium_torch", () -> new TorchBlock(ParticleTypes.SMOKE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.TORCH))
   );
   public static final DeferredBlock<StandingSignBlock> SKYROOT_SIGN = register(
      "skyroot_sign",
      () -> new SkyrootSignBlock(
         AetherWoodTypes.SKYROOT,
         Properties.of()
            .mapColor(MapColor.SAND)
            .forceSolidOn()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .strength(1.0F)
            .sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<WallSignBlock> SKYROOT_WALL_SIGN = BLOCKS.register(
      "skyroot_wall_sign",
      () -> new SkyrootWallSignBlock(
         AetherWoodTypes.SKYROOT,
         Properties.of()
            .mapColor(MapColor.SAND)
            .forceSolidOn()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .strength(1.0F)
            .sound(SoundType.WOOD)
            .lootFrom(SKYROOT_SIGN)
      )
   );
   public static final DeferredBlock<CeilingHangingSignBlock> SKYROOT_HANGING_SIGN = register(
      "skyroot_hanging_sign",
      () -> new SkyrootCeilingHangingSignBlock(
         AetherWoodTypes.SKYROOT,
         Properties.of()
            .mapColor(net.minecraft.world.level.block.Blocks.OAK_LOG.defaultMapColor())
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .strength(1.0F)
            .ignitedByLava()
      )
   );
   public static final DeferredBlock<WallHangingSignBlock> SKYROOT_WALL_HANGING_SIGN = BLOCKS.register(
      "skyroot_wall_hanging_sign",
      () -> new SkyrootWallHangingSignBlock(
         AetherWoodTypes.SKYROOT,
         Properties.of()
            .mapColor(net.minecraft.world.level.block.Blocks.OAK_LOG.defaultMapColor())
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .strength(1.0F)
            .ignitedByLava()
      )
   );
   public static final DeferredBlock<Block> BERRY_BUSH = register(
      "berry_bush",
      () -> new BerryBushBlock(
         Properties.of()
            .mapColor(MapColor.GRASS)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(AetherBlocks::ocelotOrParrot)
            .isRedstoneConductor(AetherBlocks::never)
            .isSuffocating(AetherBlocks::never)
            .isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> BERRY_BUSH_STEM = register(
      "berry_bush_stem",
      () -> new BerryBushStemBlock(
         Properties.of().mapColor(MapColor.GRASS).pushReaction(PushReaction.DESTROY).strength(0.2F).sound(SoundType.GRASS).noCollission()
      )
   );
   public static final DeferredBlock<FlowerPotBlock> POTTED_BERRY_BUSH = BLOCKS.register(
      "potted_berry_bush",
      () -> new FlowerPotBlock(
         () -> (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT,
         BERRY_BUSH,
         Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
      )
   );
   public static final DeferredBlock<FlowerPotBlock> POTTED_BERRY_BUSH_STEM = BLOCKS.register(
      "potted_berry_bush_stem",
      () -> new FlowerPotBlock(
         () -> (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT,
         BERRY_BUSH_STEM,
         Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
      )
   );
   public static final DeferredBlock<Block> PURPLE_FLOWER = register(
      "purple_flower", () -> new AetherFlowerBlock(AetherEffects.INEBRIATION, 12.0F, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DANDELION))
   );
   public static final DeferredBlock<Block> WHITE_FLOWER = register(
      "white_flower", () -> new AetherFlowerBlock(MobEffects.SLOW_FALLING, 4.0F, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DANDELION))
   );
   public static final DeferredBlock<FlowerPotBlock> POTTED_PURPLE_FLOWER = BLOCKS.register(
      "potted_purple_flower",
      () -> new FlowerPotBlock(
         () -> (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT,
         PURPLE_FLOWER,
         Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
      )
   );
   public static final DeferredBlock<FlowerPotBlock> POTTED_WHITE_FLOWER = BLOCKS.register(
      "potted_white_flower",
      () -> new FlowerPotBlock(
         () -> (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT,
         WHITE_FLOWER,
         Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
      )
   );
   public static final DeferredBlock<SaplingBlock> SKYROOT_SAPLING = register(
      "skyroot_sapling", () -> new SaplingBlock(AetherTreeGrowers.SKYROOT, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_SAPLING))
   );
   public static final DeferredBlock<SaplingBlock> GOLDEN_OAK_SAPLING = register(
      "golden_oak_sapling", () -> new SaplingBlock(AetherTreeGrowers.GOLDEN_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_SAPLING))
   );
   public static final DeferredBlock<FlowerPotBlock> POTTED_SKYROOT_SAPLING = BLOCKS.register(
      "potted_skyroot_sapling",
      () -> new FlowerPotBlock(
         () -> (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT,
         SKYROOT_SAPLING,
         Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
      )
   );
   public static final DeferredBlock<FlowerPotBlock> POTTED_GOLDEN_OAK_SAPLING = BLOCKS.register(
      "potted_golden_oak_sapling",
      () -> new FlowerPotBlock(
         () -> (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT,
         GOLDEN_OAK_SAPLING,
         Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
      )
   );
   public static final DeferredBlock<Block> CARVED_STONE = register(
      "carved_stone",
      () -> new Block(Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 6.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> SENTRY_STONE = register(
      "sentry_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)CARVED_STONE.get()).lightLevel(AetherBlocks::lightLevel11))
   );
   public static final DeferredBlock<Block> ANGELIC_STONE = register(
      "angelic_stone",
      () -> new Block(Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 6.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_ANGELIC_STONE = register(
      "light_angelic_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)ANGELIC_STONE.get()).lightLevel(AetherBlocks::lightLevel11))
   );
   public static final DeferredBlock<Block> HELLFIRE_STONE = register(
      "hellfire_stone",
      () -> new Block(Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 6.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_HELLFIRE_STONE = register(
      "light_hellfire_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)HELLFIRE_STONE.get()).lightLevel(AetherBlocks::lightLevel11))
   );
   public static final DeferredBlock<Block> LOCKED_CARVED_STONE = register(
      "locked_carved_stone", () -> new Block(Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F))
   );
   public static final DeferredBlock<Block> LOCKED_SENTRY_STONE = register(
      "locked_sentry_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LOCKED_CARVED_STONE.get()).lightLevel(AetherBlocks::lightLevel11))
   );
   public static final DeferredBlock<Block> LOCKED_ANGELIC_STONE = register(
      "locked_angelic_stone", () -> new Block(Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F))
   );
   public static final DeferredBlock<Block> LOCKED_LIGHT_ANGELIC_STONE = register(
      "locked_light_angelic_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LOCKED_ANGELIC_STONE.get()).lightLevel(AetherBlocks::lightLevel11))
   );
   public static final DeferredBlock<Block> LOCKED_HELLFIRE_STONE = register(
      "locked_hellfire_stone", () -> new Block(Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F))
   );
   public static final DeferredBlock<Block> LOCKED_LIGHT_HELLFIRE_STONE = register(
      "locked_light_hellfire_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LOCKED_HELLFIRE_STONE.get()).lightLevel(AetherBlocks::lightLevel11))
   );
   public static final DeferredBlock<Block> TRAPPED_CARVED_STONE = register(
      "trapped_carved_stone",
      () -> new TrappedBlock(
         AetherEntityTypes.SENTRY::get, () -> ((Block)CARVED_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)CARVED_STONE.get())
      )
   );
   public static final DeferredBlock<Block> TRAPPED_SENTRY_STONE = register(
      "trapped_sentry_stone",
      () -> new TrappedBlock(
         AetherEntityTypes.SENTRY::get, () -> ((Block)SENTRY_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)SENTRY_STONE.get())
      )
   );
   public static final DeferredBlock<Block> TRAPPED_ANGELIC_STONE = register(
      "trapped_angelic_stone",
      () -> new TrappedBlock(
         AetherEntityTypes.VALKYRIE::get,
         () -> ((Block)LOCKED_ANGELIC_STONE.get()).defaultBlockState(),
         Properties.ofFullCopy((BlockBehaviour)LOCKED_ANGELIC_STONE.get())
      )
   );
   public static final DeferredBlock<Block> TRAPPED_LIGHT_ANGELIC_STONE = register(
      "trapped_light_angelic_stone",
      () -> new TrappedBlock(
         AetherEntityTypes.VALKYRIE::get,
         () -> ((Block)LOCKED_LIGHT_ANGELIC_STONE.get()).defaultBlockState(),
         Properties.ofFullCopy((BlockBehaviour)LOCKED_LIGHT_ANGELIC_STONE.get())
      )
   );
   public static final DeferredBlock<Block> TRAPPED_HELLFIRE_STONE = register(
      "trapped_hellfire_stone",
      () -> new TrappedBlock(
         AetherEntityTypes.FIRE_MINION::get,
         () -> ((Block)LOCKED_HELLFIRE_STONE.get()).defaultBlockState(),
         Properties.ofFullCopy((BlockBehaviour)LOCKED_HELLFIRE_STONE.get())
      )
   );
   public static final DeferredBlock<Block> TRAPPED_LIGHT_HELLFIRE_STONE = register(
      "trapped_light_hellfire_stone",
      () -> new TrappedBlock(
         AetherEntityTypes.FIRE_MINION::get,
         () -> ((Block)LOCKED_LIGHT_HELLFIRE_STONE.get()).defaultBlockState(),
         Properties.ofFullCopy((BlockBehaviour)LOCKED_LIGHT_HELLFIRE_STONE.get())
      )
   );
   public static final DeferredBlock<Block> BOSS_DOORWAY_CARVED_STONE = register(
      "boss_doorway_carved_stone",
      () -> new DoorwayBlock(
         AetherEntityTypes.SLIDER::get,
         Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).forceSolidOn()
      )
   );
   public static final DeferredBlock<Block> BOSS_DOORWAY_SENTRY_STONE = register(
      "boss_doorway_sentry_stone",
      () -> new DoorwayBlock(AetherEntityTypes.SLIDER::get, Properties.ofFullCopy((BlockBehaviour)BOSS_DOORWAY_CARVED_STONE.get()))
   );
   public static final DeferredBlock<Block> BOSS_DOORWAY_ANGELIC_STONE = register(
      "boss_doorway_angelic_stone",
      () -> new DoorwayBlock(
         AetherEntityTypes.VALKYRIE_QUEEN::get,
         Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).forceSolidOn()
      )
   );
   public static final DeferredBlock<Block> BOSS_DOORWAY_LIGHT_ANGELIC_STONE = register(
      "boss_doorway_light_angelic_stone",
      () -> new DoorwayBlock(AetherEntityTypes.VALKYRIE_QUEEN::get, Properties.ofFullCopy((BlockBehaviour)BOSS_DOORWAY_ANGELIC_STONE.get()))
   );
   public static final DeferredBlock<Block> BOSS_DOORWAY_HELLFIRE_STONE = register(
      "boss_doorway_hellfire_stone",
      () -> new DoorwayBlock(
         AetherEntityTypes.SUN_SPIRIT::get,
         Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).forceSolidOn()
      )
   );
   public static final DeferredBlock<Block> BOSS_DOORWAY_LIGHT_HELLFIRE_STONE = register(
      "boss_doorway_light_hellfire_stone",
      () -> new DoorwayBlock(AetherEntityTypes.SUN_SPIRIT::get, Properties.ofFullCopy((BlockBehaviour)BOSS_DOORWAY_HELLFIRE_STONE.get()))
   );
   public static final DeferredBlock<Block> TREASURE_DOORWAY_CARVED_STONE = register(
      "treasure_doorway_carved_stone", () -> new TreasureDoorwayBlock(Properties.ofFullCopy((BlockBehaviour)LOCKED_CARVED_STONE.get()))
   );
   public static final DeferredBlock<Block> TREASURE_DOORWAY_SENTRY_STONE = register(
      "treasure_doorway_sentry_stone", () -> new TreasureDoorwayBlock(Properties.ofFullCopy((BlockBehaviour)LOCKED_SENTRY_STONE.get()))
   );
   public static final DeferredBlock<Block> TREASURE_DOORWAY_ANGELIC_STONE = register(
      "treasure_doorway_angelic_stone", () -> new TreasureDoorwayBlock(Properties.ofFullCopy((BlockBehaviour)LOCKED_ANGELIC_STONE.get()))
   );
   public static final DeferredBlock<Block> TREASURE_DOORWAY_LIGHT_ANGELIC_STONE = register(
      "treasure_doorway_light_angelic_stone", () -> new TreasureDoorwayBlock(Properties.ofFullCopy((BlockBehaviour)LOCKED_LIGHT_ANGELIC_STONE.get()))
   );
   public static final DeferredBlock<Block> TREASURE_DOORWAY_HELLFIRE_STONE = register(
      "treasure_doorway_hellfire_stone", () -> new TreasureDoorwayBlock(Properties.ofFullCopy((BlockBehaviour)LOCKED_HELLFIRE_STONE.get()))
   );
   public static final DeferredBlock<Block> TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE = register(
      "treasure_doorway_light_hellfire_stone", () -> new TreasureDoorwayBlock(Properties.ofFullCopy((BlockBehaviour)LOCKED_LIGHT_HELLFIRE_STONE.get()))
   );
   public static final DeferredBlock<Block> CHEST_MIMIC = register(
      "chest_mimic", () -> new ChestMimicBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHEST))
   );
   public static final DeferredBlock<Block> TREASURE_CHEST = register(
      "treasure_chest",
      () -> new TreasureChestBlock(
         Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<RotatedPillarBlock> PILLAR = register(
      "pillar",
      () -> new RotatedPillarBlock(
         Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<FacingPillarBlock> PILLAR_TOP = register(
      "pillar_top",
      () -> new FacingPillarBlock(
         Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PRESENT = register(
      "present", () -> new Block(Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BELL).strength(0.1F).sound(SoundType.WOOL))
   );
   public static final DeferredBlock<FenceBlock> SKYROOT_FENCE = register(
      "skyroot_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_FENCE))
   );
   public static final DeferredBlock<FenceGateBlock> SKYROOT_FENCE_GATE = register(
      "skyroot_fence_gate", () -> new FenceGateBlock(AetherWoodTypes.SKYROOT, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE))
   );
   public static final DeferredBlock<DoorBlock> SKYROOT_DOOR = register(
      "skyroot_door", () -> new DoorBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<TrapDoorBlock> SKYROOT_TRAPDOOR = register(
      "skyroot_trapdoor",
      () -> new TrapDoorBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<ButtonBlock> SKYROOT_BUTTON = register(
      "skyroot_button", () -> new ButtonBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, 30, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_BUTTON))
   );
   public static final DeferredBlock<PressurePlateBlock> SKYROOT_PRESSURE_PLATE = register(
      "skyroot_pressure_plate",
      () -> new PressurePlateBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PRESSURE_PLATE))
   );
   public static final DeferredBlock<ButtonBlock> HOLYSTONE_BUTTON = register(
      "holystone_button", () -> new ButtonBlock(BlockSetType.STONE, 20, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BUTTON))
   );
   public static final DeferredBlock<PressurePlateBlock> HOLYSTONE_PRESSURE_PLATE = register(
      "holystone_pressure_plate",
      () -> new PressurePlateBlock(
         BlockSetType.STONE,
         Properties.of()
            .mapColor(MapColor.WOOL)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .noCollission()
            .strength(0.5F)
      )
   );
   public static final DeferredBlock<WallBlock> CARVED_WALL = register(
      "carved_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)CARVED_STONE.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> ANGELIC_WALL = register(
      "angelic_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)ANGELIC_STONE.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> HELLFIRE_WALL = register(
      "hellfire_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)HELLFIRE_STONE.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> HOLYSTONE_WALL = register(
      "holystone_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)HOLYSTONE.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> MOSSY_HOLYSTONE_WALL = register(
      "mossy_holystone_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)MOSSY_HOLYSTONE.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> ICESTONE_WALL = register(
      "icestone_wall", () -> new IcestoneWallBlock(Properties.ofFullCopy((BlockBehaviour)ICESTONE.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> HOLYSTONE_BRICK_WALL = register(
      "holystone_brick_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)HOLYSTONE_BRICKS.get()).forceSolidOn())
   );
   public static final DeferredBlock<WallBlock> AEROGEL_WALL = register(
      "aerogel_wall",
      () -> new AerogelWallBlock(
         Properties.of()
            .mapColor(MapColor.DIAMOND)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.0F, 2000.0F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
            .isViewBlocking(AetherBlocks::never)
            .noOcclusion()
      )
   );
   public static final DeferredBlock<StairBlock> SKYROOT_STAIRS = register(
      "skyroot_stairs", () -> new StairBlock(((Block)SKYROOT_PLANKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)SKYROOT_PLANKS.get()))
   );
   public static final DeferredBlock<StairBlock> CARVED_STAIRS = register(
      "carved_stairs", () -> new StairBlock(((Block)CARVED_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)CARVED_STONE.get()))
   );
   public static final DeferredBlock<StairBlock> ANGELIC_STAIRS = register(
      "angelic_stairs", () -> new StairBlock(((Block)ANGELIC_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)ANGELIC_STONE.get()))
   );
   public static final DeferredBlock<StairBlock> HELLFIRE_STAIRS = register(
      "hellfire_stairs", () -> new StairBlock(((Block)HELLFIRE_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)HELLFIRE_STONE.get()))
   );
   public static final DeferredBlock<StairBlock> HOLYSTONE_STAIRS = register(
      "holystone_stairs", () -> new StairBlock(((Block)HOLYSTONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)HOLYSTONE.get()))
   );
   public static final DeferredBlock<StairBlock> MOSSY_HOLYSTONE_STAIRS = register(
      "mossy_holystone_stairs",
      () -> new StairBlock(((Block)MOSSY_HOLYSTONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)MOSSY_HOLYSTONE.get()))
   );
   public static final DeferredBlock<StairBlock> ICESTONE_STAIRS = register(
      "icestone_stairs", () -> new IcestoneStairsBlock(((Block)ICESTONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)ICESTONE.get()))
   );
   public static final DeferredBlock<StairBlock> HOLYSTONE_BRICK_STAIRS = register(
      "holystone_brick_stairs",
      () -> new StairBlock(((Block)HOLYSTONE_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)HOLYSTONE_BRICKS.get()))
   );
   public static final DeferredBlock<StairBlock> AEROGEL_STAIRS = register(
      "aerogel_stairs",
      () -> new AerogelStairsBlock(
         ((Block)AEROGEL.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)AEROGEL.get()).isViewBlocking(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<SlabBlock> SKYROOT_SLAB = register(
      "skyroot_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)SKYROOT_PLANKS.get()).strength(2.0F, 3.0F))
   );
   public static final DeferredBlock<SlabBlock> CARVED_SLAB = register(
      "carved_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)CARVED_STONE.get()).strength(0.5F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> ANGELIC_SLAB = register(
      "angelic_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)ANGELIC_STONE.get()).strength(0.5F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> HELLFIRE_SLAB = register(
      "hellfire_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)HELLFIRE_STONE.get()).strength(0.5F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> HOLYSTONE_SLAB = register(
      "holystone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)HOLYSTONE.get()).strength(0.5F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> MOSSY_HOLYSTONE_SLAB = register(
      "mossy_holystone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)MOSSY_HOLYSTONE.get()).strength(0.5F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> ICESTONE_SLAB = register(
      "icestone_slab", () -> new IcestoneSlabBlock(Properties.ofFullCopy((BlockBehaviour)ICESTONE.get()).strength(0.5F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> HOLYSTONE_BRICK_SLAB = register(
      "holystone_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)HOLYSTONE_BRICKS.get()).strength(2.0F, 6.0F))
   );
   public static final DeferredBlock<SlabBlock> AEROGEL_SLAB = register(
      "aerogel_slab",
      () -> new AerogelSlabBlock(Properties.ofFullCopy((BlockBehaviour)AEROGEL.get()).strength(1.0F, 2000.0F).isViewBlocking(AetherBlocks::never))
   );
   public static final DeferredBlock<Block> SUN_ALTAR = register(
      "sun_altar",
      () -> new SunAltarBlock(Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> SKYROOT_BOOKSHELF = register(
      "skyroot_bookshelf", () -> new BookshelfBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BOOKSHELF))
   );
   public static final DeferredBlock<BedBlock> SKYROOT_BED = register(
      "skyroot_bed", () -> new SkyrootBedBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CYAN_BED))
   );
   public static final DeferredBlock<Block> FROSTED_ICE = BLOCKS.register(
      "frosted_ice",
      () -> new AetherFrostedIceBlock(
         Properties.of()
            .mapColor(MapColor.ICE)
            .friction(0.98F)
            .randomTicks()
            .strength(0.5F)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
            .isRedstoneConductor(AetherBlocks::never)
      )
   );
   public static final DeferredBlock<Block> UNSTABLE_OBSIDIAN = BLOCKS.register(
      "unstable_obsidian",
      () -> new UnstableObsidianBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .randomTicks()
            .requiresCorrectToolForDrops()
            .strength(50.0F, 1200.0F)
      )
   );

   public static void registerPots() {
      FlowerPotBlock pot = (FlowerPotBlock)net.minecraft.world.level.block.Blocks.FLOWER_POT;
      pot.addPlant(BuiltInRegistries.BLOCK.getKey((Block)BERRY_BUSH.get()), POTTED_BERRY_BUSH);
      pot.addPlant(BuiltInRegistries.BLOCK.getKey((Block)BERRY_BUSH_STEM.get()), POTTED_BERRY_BUSH_STEM);
      pot.addPlant(BuiltInRegistries.BLOCK.getKey((Block)PURPLE_FLOWER.get()), POTTED_PURPLE_FLOWER);
      pot.addPlant(BuiltInRegistries.BLOCK.getKey((Block)WHITE_FLOWER.get()), POTTED_WHITE_FLOWER);
      pot.addPlant(BuiltInRegistries.BLOCK.getKey((Block)SKYROOT_SAPLING.get()), POTTED_SKYROOT_SAPLING);
      pot.addPlant(BuiltInRegistries.BLOCK.getKey((Block)GOLDEN_OAK_SAPLING.get()), POTTED_GOLDEN_OAK_SAPLING);
   }

   public static void registerFlammability() {
      FireBlockAccessor fireBlockAccessor = (FireBlockAccessor)net.minecraft.world.level.block.Blocks.FIRE;
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_LEAVES.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)GOLDEN_OAK_LEAVES.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)CRYSTAL_LEAVES.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)CRYSTAL_FRUIT_LEAVES.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)HOLIDAY_LEAVES.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)DECORATED_HOLIDAY_LEAVES.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_LOG.get(), 5, 5);
      fireBlockAccessor.callSetFlammable((Block)GOLDEN_OAK_LOG.get(), 5, 5);
      fireBlockAccessor.callSetFlammable((Block)STRIPPED_SKYROOT_LOG.get(), 5, 5);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_WOOD.get(), 5, 5);
      fireBlockAccessor.callSetFlammable((Block)GOLDEN_OAK_WOOD.get(), 5, 5);
      fireBlockAccessor.callSetFlammable((Block)STRIPPED_SKYROOT_WOOD.get(), 5, 5);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_PLANKS.get(), 5, 20);
      fireBlockAccessor.callSetFlammable((Block)BERRY_BUSH.get(), 30, 60);
      fireBlockAccessor.callSetFlammable((Block)BERRY_BUSH_STEM.get(), 60, 100);
      fireBlockAccessor.callSetFlammable((Block)PURPLE_FLOWER.get(), 60, 100);
      fireBlockAccessor.callSetFlammable((Block)WHITE_FLOWER.get(), 60, 100);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_FENCE_GATE.get(), 5, 20);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_FENCE.get(), 5, 20);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_STAIRS.get(), 5, 20);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_SLAB.get(), 5, 20);
      fireBlockAccessor.callSetFlammable((Block)SKYROOT_BOOKSHELF.get(), 30, 20);
   }

   public static void registerFluidInteractions() {
      FluidInteractionRegistry.addInteraction(
         (FluidType)NeoForgeMod.WATER_TYPE.value(),
         new InteractionInformation(
            (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is((Block)QUICKSOIL.get())
               && level.getBlockState(relativePos).is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK),
            ((Block)HOLYSTONE.get()).defaultBlockState()
         )
      );
   }

   public static void registerWoodTypes() {
      WoodType.register(AetherWoodTypes.SKYROOT);
   }

   private static <T extends Block> DeferredBlock<T> baseRegister(
      String name, Supplier<? extends T> block, Function<DeferredBlock<T>, Supplier<? extends Item>> item
   ) {
      DeferredBlock<T> register = BLOCKS.register(name, block);
      AetherItems.ITEMS.register(name, item.apply(register));
      return register;
   }

   private static <B extends Block> DeferredBlock<B> register(String name, Supplier<B> block) {
      return baseRegister(name, block, AetherBlocks::registerBlockItem);
   }

   private static <T extends Block> Supplier<BlockItem> registerBlockItem(DeferredBlock<T> deferredBlock) {
      return () -> {
         DeferredBlock<T> block = Objects.requireNonNull(deferredBlock);
         if (block == ENCHANTED_AETHER_GRASS_BLOCK || block == QUICKSOIL_GLASS || block == QUICKSOIL_GLASS_PANE || block == ENCHANTED_GRAVITITE) {
            return new BlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties().rarity(Rarity.RARE));
         } else if (block == AEROGEL || block == AEROGEL_WALL || block == AEROGEL_STAIRS || block == AEROGEL_SLAB) {
            return new BlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties().rarity(AetherItems.AETHER_LOOT));
         } else if (block == AMBROSIUM_TORCH) {
            return new StandingAndWallBlockItem(
               (Block)AMBROSIUM_TORCH.get(), (Block)AMBROSIUM_WALL_TORCH.get(), new net.minecraft.world.item.Item.Properties(), Direction.DOWN
            );
         } else if (block == SKYROOT_SIGN) {
            return new SignItem(new net.minecraft.world.item.Item.Properties().stacksTo(16), (Block)SKYROOT_SIGN.get(), (Block)SKYROOT_WALL_SIGN.get());
         } else if (block == SKYROOT_HANGING_SIGN) {
            return new HangingSignItem(
               (Block)SKYROOT_HANGING_SIGN.get(), (Block)SKYROOT_WALL_HANGING_SIGN.get(), new net.minecraft.world.item.Item.Properties().stacksTo(16)
            );
         } else if (block == CHEST_MIMIC) {
            return new EntityBlockItem((Block)block.get(), ChestMimicBlockEntity::new, new net.minecraft.world.item.Item.Properties());
         } else if (block == TREASURE_CHEST) {
            return new EntityBlockItem((Block)block.get(), TreasureChestBlockEntity::new, new net.minecraft.world.item.Item.Properties());
         } else if (block == SKYROOT_DOOR) {
            return new DoubleHighBlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties());
         } else if (block == SUN_ALTAR) {
            return new BlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties().fireResistant());
         } else {
            return (BlockItem)(block == SKYROOT_BED
               ? new EntityBlockItem((Block)block.get(), SkyrootBedBlockEntity::new, new net.minecraft.world.item.Item.Properties().stacksTo(1))
               : new BlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties()));
         }
      };
   }

   private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
      return false;
   }

   private static boolean always(BlockState state, BlockGetter getter, BlockPos pos) {
      return true;
   }

   private static <A> boolean never(BlockState state, BlockGetter getter, BlockPos pos, A block) {
      return false;
   }

   private static boolean ocelotOrParrot(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
      return type == EntityType.OCELOT || type == EntityType.PARROT;
   }

   private static int lightLevel11(BlockState state) {
      return 11;
   }
}
