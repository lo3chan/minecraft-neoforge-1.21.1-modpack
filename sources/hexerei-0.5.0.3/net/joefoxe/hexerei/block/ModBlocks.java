package net.joefoxe.hexerei.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.joefoxe.hexerei.block.connected.AllSpriteShifts;
import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.CTModel;
import net.joefoxe.hexerei.block.connected.ConnectedPillarBlock;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.block.connected.FabricBlock;
import net.joefoxe.hexerei.block.connected.LayeredBlock;
import net.joefoxe.hexerei.block.connected.WaxedBlock;
import net.joefoxe.hexerei.block.connected.WaxedConnectedRotatedPillarBlock;
import net.joefoxe.hexerei.block.connected.WaxedGlassBlock;
import net.joefoxe.hexerei.block.connected.WaxedGlassPaneBlock;
import net.joefoxe.hexerei.block.connected.WaxedLayeredBlock;
import net.joefoxe.hexerei.block.connected.behavior.CarpetCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.CarpetStairsCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.DyedFullBlockCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.FullBlockCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.FullBlockTopBottomShiftCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.GlassPaneCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.GlassPaneTransparentCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.HorizontalCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.RotatedPillarCTBehaviour;
import net.joefoxe.hexerei.block.connected.behavior.TrapdoorCTBehaviour;
import net.joefoxe.hexerei.block.custom.Altar;
import net.joefoxe.hexerei.block.custom.BroomStand;
import net.joefoxe.hexerei.block.custom.BroomStandWall;
import net.joefoxe.hexerei.block.custom.BuddingSelenite;
import net.joefoxe.hexerei.block.custom.Candelabra;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.block.custom.CandleDipper;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetSlab;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs;
import net.joefoxe.hexerei.block.custom.ConnectingTable;
import net.joefoxe.hexerei.block.custom.CourierLetter;
import net.joefoxe.hexerei.block.custom.CourierPackage;
import net.joefoxe.hexerei.block.custom.CrystalBall;
import net.joefoxe.hexerei.block.custom.CuttingCrystal;
import net.joefoxe.hexerei.block.custom.FloweringLilyPadBlock;
import net.joefoxe.hexerei.block.custom.HerbDryingRack;
import net.joefoxe.hexerei.block.custom.HerbDryingRackFull;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.block.custom.MahoganyLog;
import net.joefoxe.hexerei.block.custom.MahoganyWood;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.block.custom.ModChest;
import net.joefoxe.hexerei.block.custom.ModHangingSign;
import net.joefoxe.hexerei.block.custom.ModHangingWallSign;
import net.joefoxe.hexerei.block.custom.ModSign;
import net.joefoxe.hexerei.block.custom.ModWallSign;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.block.custom.OwlCourierDepotWall;
import net.joefoxe.hexerei.block.custom.PestleAndMortar;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.joefoxe.hexerei.block.custom.SageBlock;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.block.custom.WallDryingRack;
import net.joefoxe.hexerei.block.custom.WillowLog;
import net.joefoxe.hexerei.block.custom.WillowVinesBlock;
import net.joefoxe.hexerei.block.custom.WillowVinesPlantBlock;
import net.joefoxe.hexerei.block.custom.WillowWood;
import net.joefoxe.hexerei.block.custom.WitchHazelLog;
import net.joefoxe.hexerei.block.custom.WitchHazelWood;
import net.joefoxe.hexerei.block.custom.Woodcutter;
import net.joefoxe.hexerei.block.custom.trees.HexereiTree;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.world.gen.ModConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "hexerei");
   public static Map<DeferredHolder<Block, ?>, Consumer<? super Block>> afterRegisterConsumer = new HashMap<>();
   public static final DeferredHolder<Block, Block> WILLOW_CONNECTED = registerBlockWithConsumer(
      "willow_connected",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WILLOW_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WILLOW_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_WILLOW_CONNECTED = registerBlockWithConsumer(
      "waxed_willow_connected",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_WILLOW_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_WILLOW_CONNECTED))
   );
   public static final DeferredHolder<Block, Block> POLISHED_WILLOW_CONNECTED = registerBlockWithConsumer(
      "polished_willow_connected",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WILLOW_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_POLISHED_WILLOW_CONNECTED = registerBlockWithConsumer(
      "waxed_polished_willow_connected",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WILLOW_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_WILLOW_CONNECTED))
   );
   public static final DeferredHolder<Block, ConnectedPillarBlock> POLISHED_WILLOW_PILLAR = registerBlockWithConsumer(
      "polished_willow_pillar",
      () -> new ConnectedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new RotatedPillarCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_PILLAR_SIDE, AllSpriteShifts.POLISHED_WILLOW_PILLAR_TOP)
         )
   );
   public static final DeferredHolder<Block, WaxedConnectedRotatedPillarBlock> WAXED_POLISHED_WILLOW_PILLAR = registerBlockWithConsumer(
      "waxed_polished_willow_pillar",
      () -> new WaxedConnectedRotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new RotatedPillarCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WILLOW_PILLAR_SIDE, AllSpriteShifts.WAXED_POLISHED_WILLOW_PILLAR_TOP)
         )
   );
   public static final DeferredHolder<Block, LayeredBlock> POLISHED_WILLOW_LAYERED = registerBlockWithConsumer(
      "polished_willow_layered",
      () -> new LayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new HorizontalCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_LAYERED, AllSpriteShifts.POLISHED_SMOOTH_WILLOW)
         )
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_POLISHED_WILLOW_LAYERED = registerBlockWithConsumer(
      "waxed_polished_willow_layered",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new HorizontalCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WILLOW_LAYERED, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW)
         )
   );
   public static final DeferredHolder<Block, Block> WITCH_HAZEL_CONNECTED = registerBlockWithConsumer(
      "witch_hazel_connected",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WITCH_HAZEL_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WITCH_HAZEL_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_WITCH_HAZEL_CONNECTED = registerBlockWithConsumer(
      "waxed_witch_hazel_connected",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_WITCH_HAZEL_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_WITCH_HAZEL_CONNECTED))
   );
   public static final DeferredHolder<Block, Block> POLISHED_WITCH_HAZEL_CONNECTED = registerBlockWithConsumer(
      "polished_witch_hazel_connected",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WITCH_HAZEL_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_POLISHED_WITCH_HAZEL_CONNECTED = registerBlockWithConsumer(
      "waxed_polished_witch_hazel_connected",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_CONNECTED))
   );
   public static final DeferredHolder<Block, ConnectedPillarBlock> POLISHED_WITCH_HAZEL_PILLAR = registerBlockWithConsumer(
      "polished_witch_hazel_pillar",
      () -> new ConnectedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new RotatedPillarCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_PILLAR_SIDE, AllSpriteShifts.POLISHED_WITCH_HAZEL_PILLAR_TOP)
         )
   );
   public static final DeferredHolder<Block, WaxedConnectedRotatedPillarBlock> WAXED_POLISHED_WITCH_HAZEL_PILLAR = registerBlockWithConsumer(
      "waxed_polished_witch_hazel_pillar",
      () -> new WaxedConnectedRotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new RotatedPillarCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_PILLAR_SIDE, AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_PILLAR_TOP)
         )
   );
   public static final DeferredHolder<Block, LayeredBlock> POLISHED_WITCH_HAZEL_LAYERED = registerBlockWithConsumer(
      "polished_witch_hazel_layered",
      () -> new LayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new HorizontalCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_LAYERED, AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL)
         )
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_POLISHED_WITCH_HAZEL_LAYERED = registerBlockWithConsumer(
      "waxed_polished_witch_hazel_layered",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new HorizontalCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_LAYERED, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL)
         )
   );
   public static final DeferredHolder<Block, Block> MAHOGANY_CONNECTED = registerBlockWithConsumer(
      "mahogany_connected",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.MAHOGANY_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.MAHOGANY_CONNECTED))
   );
   public static final DeferredHolder<Block, Block> POLISHED_MAHOGANY_CONNECTED = registerBlockWithConsumer(
      "polished_mahogany_connected",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_MAHOGANY_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_MAHOGANY_CONNECTED = registerBlockWithConsumer(
      "waxed_mahogany_connected",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_MAHOGANY_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_MAHOGANY_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_POLISHED_MAHOGANY_CONNECTED = registerBlockWithConsumer(
      "waxed_polished_mahogany_connected",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_MAHOGANY_CONNECTED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_MAHOGANY_CONNECTED))
   );
   public static final DeferredHolder<Block, ConnectedPillarBlock> POLISHED_MAHOGANY_PILLAR = registerBlockWithConsumer(
      "polished_mahogany_pillar",
      () -> new ConnectedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new RotatedPillarCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_PILLAR_SIDE, AllSpriteShifts.POLISHED_MAHOGANY_PILLAR_TOP)
         )
   );
   public static final DeferredHolder<Block, WaxedConnectedRotatedPillarBlock> WAXED_POLISHED_MAHOGANY_PILLAR = registerBlockWithConsumer(
      "waxed_polished_mahogany_pillar",
      () -> new WaxedConnectedRotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new RotatedPillarCTBehaviour(AllSpriteShifts.WAXED_POLISHED_MAHOGANY_PILLAR_SIDE, AllSpriteShifts.WAXED_POLISHED_MAHOGANY_PILLAR_TOP)
         )
   );
   public static final DeferredHolder<Block, LayeredBlock> POLISHED_MAHOGANY_LAYERED = registerBlockWithConsumer(
      "polished_mahogany_layered",
      () -> new LayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new HorizontalCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_LAYERED, AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY)
         )
   );
   public static final DeferredHolder<Block, WaxedLayeredBlock> WAXED_POLISHED_MAHOGANY_LAYERED = registerBlockWithConsumer(
      "waxed_polished_mahogany_layered",
      () -> new WaxedLayeredBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new HorizontalCTBehaviour(AllSpriteShifts.WAXED_POLISHED_MAHOGANY_LAYERED, AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY)
         )
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> STONE_WINDOW_PANE = registerBlockWithConsumer(
      "stone_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new GlassPaneTransparentCTBehaviour(AllSpriteShifts.STONE_WINDOW_PANE_CONNECTED, AllSpriteShifts.STONE_WINDOW_PANE_CONNECTED_GLASS)
         ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.STONE_WINDOW_PANE_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> WAXED_STONE_WINDOW_PANE = registerBlockWithConsumer(
      "waxed_stone_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new GlassPaneTransparentCTBehaviour(
               AllSpriteShifts.WAXED_STONE_WINDOW_PANE_CONNECTED, AllSpriteShifts.WAXED_STONE_WINDOW_PANE_CONNECTED_GLASS
            )
         ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_STONE_WINDOW_PANE_CONNECTED))
   );
   public static final DeferredHolder<Block, TransparentBlock> STONE_WINDOW = registerBlockWithConsumer(
      "stone_window",
      () -> new TransparentBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new FullBlockTopBottomShiftCTBehaviour(
               AllSpriteShifts.STONE_WINDOW_CONNECTED,
               AllSpriteShifts.STONE_WINDOW_CONNECTED_GLASS,
               AllSpriteShifts.STONE_WINDOW_CONNECTED_TOP,
               AllSpriteShifts.STONE_WINDOW_CONNECTED_TOP_GLASS
            )
         ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.STONE_WINDOW_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedBlock> WAXED_STONE_WINDOW = registerBlockWithConsumer(
      "waxed_stone_window",
      () -> new WaxedBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new FullBlockTopBottomShiftCTBehaviour(
               AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED,
               AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED_GLASS,
               AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED_TOP,
               AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED_TOP_GLASS
            )
         ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> MAHOGANY_WINDOW_PANE = registerBlockWithConsumer(
      "mahogany_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS_PANE))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> WAXED_MAHOGANY_WINDOW_PANE = registerBlockWithConsumer(
      "waxed_mahogany_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS_PANE))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> WILLOW_WINDOW_PANE = registerBlockWithConsumer(
      "willow_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS_PANE))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> WAXED_WILLOW_WINDOW_PANE = registerBlockWithConsumer(
      "waxed_willow_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS_PANE))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> WITCH_HAZEL_WINDOW_PANE = registerBlockWithConsumer(
      "witch_hazel_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE))
   );
   public static final DeferredHolder<Block, WaxedGlassPaneBlock> WAXED_WITCH_HAZEL_WINDOW_PANE = registerBlockWithConsumer(
      "waxed_witch_hazel_window_pane",
      () -> new WaxedGlassPaneBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE))
   );
   public static final DeferredHolder<Block, TransparentBlock> MAHOGANY_WINDOW = registerBlockWithConsumer(
      "mahogany_window",
      () -> new TransparentBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS))
   );
   public static final DeferredHolder<Block, WaxedGlassBlock> WAXED_MAHOGANY_WINDOW = registerBlockWithConsumer(
      "waxed_mahogany_window",
      () -> new WaxedGlassBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS))
   );
   public static final DeferredHolder<Block, TransparentBlock> WILLOW_WINDOW = registerBlockWithConsumer(
      "willow_window",
      () -> new TransparentBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS))
   );
   public static final DeferredHolder<Block, WaxedGlassBlock> WAXED_WILLOW_WINDOW = registerBlockWithConsumer(
      "waxed_willow_window",
      () -> new WaxedGlassBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS))
   );
   public static final DeferredHolder<Block, TransparentBlock> WITCH_HAZEL_WINDOW = registerBlockWithConsumer(
      "witch_hazel_window",
      () -> new TransparentBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS))
   );
   public static final DeferredHolder<Block, TransparentBlock> WAXED_WITCH_HAZEL_WINDOW = registerBlockWithConsumer(
      "waxed_witch_hazel_window",
      () -> new TransparentBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS))
   );
   public static final DeferredHolder<Block, FabricBlock> INFUSED_FABRIC_BLOCK_ORNATE = registerBlockWithConsumer(
      "infused_fabric_block_ornate",
      () -> new FabricBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE))
   );
   public static final DeferredHolder<Block, FabricBlock> WAXED_INFUSED_FABRIC_BLOCK_ORNATE = registerBlockWithConsumer(
      "waxed_infused_fabric_block_ornate",
      () -> new FabricBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE))
   );
   public static final DeferredHolder<Block, ConnectingCarpetDyed> INFUSED_FABRIC_CARPET_ORNATE = registerBlockWithConsumer(
      "infused_fabric_carpet_ornate",
      () -> new ConnectingCarpetDyed(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), DyeColor.WHITE),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetDyed> WAXED_INFUSED_FABRIC_CARPET_ORNATE = registerBlockWithConsumer(
      "waxed_infused_fabric_carpet_ornate",
      () -> new ConnectingCarpetDyed(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), DyeColor.WHITE),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetDyed> INFUSED_FABRIC_CARPET = registerBlockWithConsumer(
      "infused_fabric_carpet",
      () -> new ConnectingCarpetDyed(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), DyeColor.WHITE),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetDyed> WAXED_INFUSED_FABRIC_CARPET = registerBlockWithConsumer(
      "waxed_infused_fabric_carpet",
      () -> new ConnectingCarpetDyed(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), DyeColor.WHITE),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetStairs> INFUSED_FABRIC_CARPET_ORNATE_STAIRS = registerBlockWithConsumer(
      "infused_fabric_carpet_ornate_stairs",
      () -> new ConnectingCarpetStairs(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)INFUSED_FABRIC_CARPET_ORNATE.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetStairsCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetStairs> WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS = registerBlockWithConsumer(
      "waxed_infused_fabric_carpet_ornate_stairs",
      () -> new ConnectingCarpetStairs(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)WAXED_INFUSED_FABRIC_CARPET_ORNATE.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetStairsCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetStairs> INFUSED_FABRIC_CARPET_STAIRS = registerBlockWithConsumer(
      "infused_fabric_carpet_stairs",
      () -> new ConnectingCarpetStairs(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)INFUSED_FABRIC_CARPET.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetStairsCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetStairs> WAXED_INFUSED_FABRIC_CARPET_STAIRS = registerBlockWithConsumer(
      "waxed_infused_fabric_carpet_stairs",
      () -> new ConnectingCarpetStairs(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)WAXED_INFUSED_FABRIC_CARPET.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetStairsCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetSlab> INFUSED_FABRIC_CARPET_ORNATE_SLAB = registerBlockWithConsumer(
      "infused_fabric_carpet_ornate_slab",
      () -> new ConnectingCarpetSlab(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)WAXED_INFUSED_FABRIC_CARPET_ORNATE.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetSlab> WAXED_INFUSED_FABRIC_CARPET_ORNATE_SLAB = registerBlockWithConsumer(
      "waxed_infused_fabric_carpet_ornate_slab",
      () -> new ConnectingCarpetSlab(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)WAXED_INFUSED_FABRIC_CARPET_ORNATE.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetSlab> INFUSED_FABRIC_CARPET_SLAB = registerBlockWithConsumer(
      "infused_fabric_carpet_slab",
      () -> new ConnectingCarpetSlab(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)INFUSED_FABRIC_CARPET.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)
         )
   );
   public static final DeferredHolder<Block, ConnectingCarpetSlab> WAXED_INFUSED_FABRIC_CARPET_SLAB = registerBlockWithConsumer(
      "waxed_infused_fabric_carpet_slab",
      () -> new ConnectingCarpetSlab(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), (Block)WAXED_INFUSED_FABRIC_CARPET.get()
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(
            () -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED)
         )
   );
   public static final DeferredHolder<Block, FabricBlock> INFUSED_FABRIC_BLOCK = registerBlockWithConsumer(
      "infused_fabric_block",
      () -> new FabricBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new DyedFullBlockCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED))
   );
   public static final DeferredHolder<Block, ?> WAXED_INFUSED_FABRIC_BLOCK = registerBlockWithConsumer(
      "waxed_infused_fabric_block",
      () -> new FabricBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new DyedFullBlockCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED))
   );
   public static final DeferredHolder<Block, MixingCauldron> MIXING_CAULDRON = registerBlockNoItem(
      "mixing_cauldron",
      () -> new MixingCauldron(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .randomTicks()
            .explosionResistance(4.0F)
            .requiresCorrectToolForDrops()
            .strength(3.0F)
            .lightLevel(state -> 12)
      )
   );
   public static final DeferredHolder<Block, CandleDipper> CANDLE_DIPPER = registerBlock(
      "candle_dipper",
      () -> new CandleDipper(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .noCollission()
            .noOcclusion()
            .strength(3.0F)
            .requiresCorrectToolForDrops()
            .explosionResistance(8.0F)
      )
   );
   public static final DeferredHolder<Block, Coffer> COFFER = registerBlockNoItem(
      "coffer",
      () -> new Coffer(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .explosionResistance(8.0F)
      )
   );
   public static final DeferredHolder<Block, Coffer> ENTANGLED_COFFER = registerBlockNoItem(
      "entangled_coffer",
      () -> new Coffer(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .explosionResistance(8.0F)
      )
   );
   public static final DeferredHolder<Block, CuttingCrystal> CUTTING_CRYSTAL = registerBlockNoItem(
      "cutting_crystal",
      () -> new CuttingCrystal(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .explosionResistance(8.0F)
      )
   );
   public static final DeferredHolder<Block, OwlCourierDepotWall> WILLOW_COURIER_DEPOT_WALL = registerBlockNoItem(
      "willow_courier_depot_wall",
      () -> new OwlCourierDepotWall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, OwlCourierDepot> WILLOW_COURIER_DEPOT = registerBlockNoItem(
      "willow_courier_depot",
      () -> new OwlCourierDepot(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, OwlCourierDepotWall> MAHOGANY_COURIER_DEPOT_WALL = registerBlockNoItem(
      "mahogany_courier_depot_wall",
      () -> new OwlCourierDepotWall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, OwlCourierDepot> MAHOGANY_COURIER_DEPOT = registerBlockNoItem(
      "mahogany_courier_depot",
      () -> new OwlCourierDepot(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, OwlCourierDepotWall> WITCH_HAZEL_COURIER_DEPOT_WALL = registerBlockNoItem(
      "witch_hazel_courier_depot_wall",
      () -> new OwlCourierDepotWall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, OwlCourierDepot> WITCH_HAZEL_COURIER_DEPOT = registerBlockNoItem(
      "witch_hazel_courier_depot",
      () -> new OwlCourierDepot(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, CourierLetter> COURIER_LETTER = registerBlockNoItem(
      "courier_letter",
      () -> new CourierLetter(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_CARPET)
            .noCollission()
            .strength(0.1F)
            .noOcclusion()
            .explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, CourierPackage> COURIER_PACKAGE = registerBlockNoItem(
      "courier_package",
      () -> new CourierPackage(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(0.4F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, BroomStandWall> MAHOGANY_BROOM_STAND_WALL = registerBlockNoItem(
      "mahogany_broom_stand_wall",
      () -> new BroomStandWall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, BroomStand> MAHOGANY_BROOM_STAND = registerBlockNoItem(
      "mahogany_broom_stand",
      () -> new BroomStand(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, BroomStandWall> WILLOW_BROOM_STAND_WALL = registerBlockNoItem(
      "willow_broom_stand_wall",
      () -> new BroomStandWall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, BroomStand> WILLOW_BROOM_STAND = registerBlockNoItem(
      "willow_broom_stand",
      () -> new BroomStand(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, BroomStandWall> WITCH_HAZEL_BROOM_STAND_WALL = registerBlockNoItem(
      "witch_hazel_broom_stand_wall",
      () -> new BroomStandWall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, BroomStand> WITCH_HAZEL_BROOM_STAND = registerBlockNoItem(
      "witch_hazel_broom_stand",
      () -> new BroomStand(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).noOcclusion().explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, Altar> BOOK_OF_SHADOWS_ALTAR = registerBlock(
      "book_of_shadows_altar",
      () -> new Altar(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, ConnectingTable> WILLOW_ALTAR = registerBlock(
      "willow_altar",
      () -> new Altar(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, ConnectingTable> WITCH_HAZEL_ALTAR = registerBlock(
      "witch_hazel_altar",
      () -> new Altar(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Woodcutter> MAHOGANY_WOODCUTTER = registerBlock(
      "mahogany_woodcutter",
      () -> new Woodcutter(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONECUTTER)
            .explosionResistance(4.0F)
            .requiresCorrectToolForDrops()
            .strength(3.0F)
      )
   );
   public static final DeferredHolder<Block, Woodcutter> WILLOW_WOODCUTTER = registerBlock(
      "willow_woodcutter",
      () -> new Woodcutter(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONECUTTER)
            .explosionResistance(4.0F)
            .requiresCorrectToolForDrops()
            .strength(3.0F)
      )
   );
   public static final DeferredHolder<Block, Woodcutter> WITCH_HAZEL_WOODCUTTER = registerBlock(
      "witch_hazel_woodcutter",
      () -> new Woodcutter(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STONECUTTER)
            .explosionResistance(4.0F)
            .requiresCorrectToolForDrops()
            .strength(3.0F)
      )
   );
   public static final DeferredHolder<Block, ModChest> WILLOW_CHEST = registerBlockNoItem(
      "willow_chest",
      () -> new ModChest(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).explosionResistance(4.0F).strength(3.0F),
         ModChest.WoodType.WILLOW
      )
   );
   public static final DeferredHolder<Block, ModChest> WITCH_HAZEL_CHEST = registerBlockNoItem(
      "witch_hazel_chest",
      () -> new ModChest(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).explosionResistance(4.0F).strength(3.0F),
         ModChest.WoodType.WITCH_HAZEL
      )
   );
   public static final DeferredHolder<Block, ModChest> MAHOGANY_CHEST = registerBlockNoItem(
      "mahogany_chest",
      () -> new ModChest(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).explosionResistance(4.0F).strength(3.0F),
         ModChest.WoodType.MAHOGANY
      )
   );
   public static final DeferredHolder<Block, ModSign> MAHOGANY_SIGN = registerBlockNoItem(
      "mahogany_sign",
      () -> new ModSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModWoodType.MAHOGANY
      )
   );
   public static final DeferredHolder<Block, ModSign> WILLOW_SIGN = registerBlockNoItem(
      "willow_sign",
      () -> new ModSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModWoodType.WILLOW
      )
   );
   public static final DeferredHolder<Block, ModSign> WITCH_HAZEL_SIGN = registerBlockNoItem(
      "witch_hazel_sign",
      () -> new ModSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModWoodType.WITCH_HAZEL
      )
   );
   public static final DeferredHolder<Block, ModSign> POLISHED_MAHOGANY_SIGN = registerBlockNoItem(
      "polished_mahogany_sign",
      () -> new ModSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModWoodType.POLISHED_MAHOGANY
      )
   );
   public static final DeferredHolder<Block, ModSign> POLISHED_WILLOW_SIGN = registerBlockNoItem(
      "polished_willow_sign",
      () -> new ModSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModWoodType.POLISHED_WILLOW
      )
   );
   public static final DeferredHolder<Block, ModSign> POLISHED_WITCH_HAZEL_SIGN = registerBlockNoItem(
      "polished_witch_hazel_sign",
      () -> new ModSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModWoodType.POLISHED_WITCH_HAZEL
      )
   );
   public static final DeferredHolder<Block, ModWallSign> MAHOGANY_WALL_SIGN = registerBlockNoItem(
      "mahogany_wall_sign",
      () -> new ModWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModTileEntities.SIGN_TILE::get,
         ModWoodType.MAHOGANY
      )
   );
   public static final DeferredHolder<Block, ModWallSign> WILLOW_WALL_SIGN = registerBlockNoItem(
      "willow_wall_sign",
      () -> new ModWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModTileEntities.SIGN_TILE::get,
         ModWoodType.WILLOW
      )
   );
   public static final DeferredHolder<Block, ModWallSign> WITCH_HAZEL_WALL_SIGN = registerBlockNoItem(
      "witch_hazel_wall_sign",
      () -> new ModWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModTileEntities.SIGN_TILE::get,
         ModWoodType.WITCH_HAZEL
      )
   );
   public static final DeferredHolder<Block, ModWallSign> POLISHED_MAHOGANY_WALL_SIGN = registerBlockNoItem(
      "polished_mahogany_wall_sign",
      () -> new ModWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModTileEntities.SIGN_TILE::get,
         ModWoodType.POLISHED_MAHOGANY
      )
   );
   public static final DeferredHolder<Block, ModWallSign> POLISHED_WILLOW_WALL_SIGN = registerBlockNoItem(
      "polished_willow_wall_sign",
      () -> new ModWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModTileEntities.SIGN_TILE::get,
         ModWoodType.POLISHED_WILLOW
      )
   );
   public static final DeferredHolder<Block, ModWallSign> POLISHED_WITCH_HAZEL_WALL_SIGN = registerBlockNoItem(
      "polished_witch_hazel_wall_sign",
      () -> new ModWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
            .explosionResistance(4.0F)
            .strength(3.0F)
            .noCollission()
            .noOcclusion(),
         ModTileEntities.SIGN_TILE::get,
         ModWoodType.POLISHED_WITCH_HAZEL
      )
   );
   public static final DeferredHolder<Block, ModHangingSign> MAHOGANY_HANGING_SIGN = registerBlockNoItem(
      "mahogany_hanging_sign",
      () -> new ModHangingSign(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN), ModWoodType.MAHOGANY)
   );
   public static final DeferredHolder<Block, ModHangingSign> WILLOW_HANGING_SIGN = registerBlockNoItem(
      "willow_hanging_sign",
      () -> new ModHangingSign(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN), ModWoodType.WILLOW)
   );
   public static final DeferredHolder<Block, ModHangingSign> WITCH_HAZEL_HANGING_SIGN = registerBlockNoItem(
      "witch_hazel_hanging_sign",
      () -> new ModHangingSign(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN), ModWoodType.WITCH_HAZEL)
   );
   public static final DeferredHolder<Block, ModHangingWallSign> MAHOGANY_WALL_HANGING_SIGN = registerBlockNoItem(
      "mahogany_wall_hanging_sign",
      () -> new ModHangingWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodType.MAHOGANY
      )
   );
   public static final DeferredHolder<Block, ModHangingWallSign> WILLOW_WALL_HANGING_SIGN = registerBlockNoItem(
      "willow_wall_hanging_sign",
      () -> new ModHangingWallSign(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodType.WILLOW)
   );
   public static final DeferredHolder<Block, ModHangingWallSign> WITCH_HAZEL_WALL_HANGING_SIGN = registerBlockNoItem(
      "witch_hazel_wall_hanging_sign",
      () -> new ModHangingWallSign(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodType.WITCH_HAZEL
      )
   );
   public static final DeferredHolder<Block, SageBlock> SAGE = BLOCKS.register(
      "sage_crop", () -> new SageBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
   );
   public static final DeferredHolder<Block, PestleAndMortar> PESTLE_AND_MORTAR = registerBlock(
      "pestle_and_mortar",
      () -> new PestleAndMortar(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, SageBurningPlate> SAGE_BURNING_PLATE = registerBlock(
      "sage_burning_plate",
      () -> new SageBurningPlate(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, CrystalBall> CRYSTAL_BALL = registerBlock(
      "crystal_ball",
      () -> new CrystalBall(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(2.0F)
            .explosionResistance(2.0F)
            .lightLevel(state -> 9)
      )
   );
   public static final DeferredHolder<Block, HerbJar> HERB_JAR = registerBlockNoItem(
      "herb_jar",
      () -> new HerbJar(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PODZOL)
            .strength(1.0F)
            .explosionResistance(0.5F)
            .strength(1.0F, 1.0F)
            .sound(SoundType.GLASS)
      )
   );
   public static final DeferredHolder<Block, HerbDryingRackFull> HERB_DRYING_RACK_FULL = registerBlock(
      "herb_drying_rack_full",
      () -> new HerbDryingRackFull(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, HerbDryingRack> HERB_DRYING_RACK = registerBlock(
      "herb_drying_rack",
      () -> new HerbDryingRack(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, WallDryingRack> MAHOGANY_DRYING_RACK = registerBlock(
      "mahogany_drying_rack",
      () -> new WallDryingRack(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, WallDryingRack> WILLOW_DRYING_RACK = registerBlock(
      "willow_drying_rack",
      () -> new WallDryingRack(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, WallDryingRack> WITCH_HAZEL_DRYING_RACK = registerBlock(
      "witch_hazel_drying_rack",
      () -> new WallDryingRack(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).explosionResistance(2.0F)
      )
   );
   public static final DeferredHolder<Block, Candelabra> CANDELABRA = registerBlock(
      "candelabra",
      () -> new Candelabra(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(1.0F)
            .explosionResistance(2.0F)
            .lightLevel(state -> state.getValue(Candelabra.LIT) ? 15 : 0)
      )
   );
   public static final DeferredHolder<Block, Candle> CANDLE = registerBlockNoItem(
      "candle",
      () -> new Candle(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .noOcclusion()
            .strength(1.0F)
            .explosionResistance(0.5F)
            .lightLevel(state -> Math.min((Integer)state.getValue(Candle.CANDLES_LIT) * 12, 15))
      )
   );
   public static final DeferredHolder<Block, MahoganyLog> MAHOGANY_LOG = registerBlock(
      "mahogany_log", () -> new MahoganyLog(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG))
   );
   public static final DeferredHolder<Block, MahoganyWood> MAHOGANY_WOOD = registerBlock(
      "mahogany_wood", () -> new MahoganyWood(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD))
   );
   public static final DeferredHolder<Block, RotatedPillarBlock> STRIPPED_MAHOGANY_LOG = registerBlock(
      "stripped_mahogany_log",
      () -> new RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
   );
   public static final DeferredHolder<Block, RotatedPillarBlock> STRIPPED_MAHOGANY_WOOD = registerBlock(
      "stripped_mahogany_wood",
      () -> new RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
   );
   public static final DeferredHolder<Block, Block> MAHOGANY_PLANKS = registerBlock(
      "mahogany_planks", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, StairBlock> MAHOGANY_STAIRS = registerBlock(
      "mahogany_stairs",
      () -> new StairBlock(
         ((Block)MAHOGANY_PLANKS.get()).defaultBlockState(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
      )
   );
   public static final DeferredHolder<Block, FenceBlock> MAHOGANY_FENCE = registerBlock(
      "mahogany_fence", () -> new FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, FenceGateBlock> MAHOGANY_FENCE_GATE = registerBlock(
      "mahogany_fence_gate",
      () -> new FenceGateBlock(ModWoodType.MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, SlabBlock> MAHOGANY_SLAB = registerBlock(
      "mahogany_slab", () -> new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, ButtonBlock> MAHOGANY_BUTTON = registerBlock(
      "mahogany_button",
      () -> new ButtonBlock(
         ModBlockSetType.MAHOGANY,
         30,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, PressurePlateBlock> MAHOGANY_PRESSURE_PLATE = registerBlock(
      "mahogany_pressure_plate",
      () -> new PressurePlateBlock(
         ModBlockSetType.MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noCollission()
      )
   );
   public static final DeferredHolder<Block, DoorBlock> MAHOGANY_DOOR = registerBlock(
      "mahogany_door",
      () -> new DoorBlock(ModBlockSetType.MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion())
   );
   public static final DeferredHolder<Block, TrapDoorBlock> MAHOGANY_TRAPDOOR = registerBlock(
      "mahogany_trapdoor",
      () -> new TrapDoorBlock(
         ModBlockSetType.MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, LeavesBlock> MAHOGANY_LEAVES = registerBlock(
      "mahogany_leaves",
      () -> new LeavesBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
            .randomTicks()
            .sound(SoundType.AZALEA_LEAVES)
            .noOcclusion()
            .isSuffocating(ModBlocks.Properties::never)
            .isViewBlocking(ModBlocks.Properties::never)
      ) {
         public void animateTick(BlockState p_272714_, Level p_272837_, BlockPos p_273218_, RandomSource p_273360_) {
            super.animateTick(p_272714_, p_272837_, p_273218_, p_273360_);
            if (p_273360_.nextInt(10) == 0) {
               BlockPos blockpos = p_273218_.below();
               BlockState blockstate = p_272837_.getBlockState(blockpos);
               if (!isFaceFull(blockstate.getCollisionShape(p_272837_, blockpos), Direction.UP)) {
                  ParticleUtils.spawnParticleBelow(p_272837_, p_273218_, p_273360_, (ParticleOptions)ModParticleTypes.MAHOGANY_LEAVES.get());
               }
            }
         }
      }
   );
   public static final DeferredHolder<Block, SaplingBlock> MAHOGANY_SAPLING = registerBlock(
      "mahogany_sapling",
      () -> new SaplingBlock(
         HexereiTree.getGrower("mahogany_tree", ModConfiguredFeatures.MAHOGANY_KEY),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
      )
   );
   public static final DeferredHolder<Block, Block> POLISHED_MAHOGANY_PLANKS = registerBlock(
      "polished_mahogany_planks", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, StairBlock> POLISHED_MAHOGANY_STAIRS = registerBlock(
      "polished_mahogany_stairs",
      () -> new StairBlock(
         ((Block)POLISHED_MAHOGANY_PLANKS.get()).defaultBlockState(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
      )
   );
   public static final DeferredHolder<Block, FenceBlock> POLISHED_MAHOGANY_FENCE = registerBlock(
      "polished_mahogany_fence", () -> new FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, FenceGateBlock> POLISHED_MAHOGANY_FENCE_GATE = registerBlock(
      "polished_mahogany_fence_gate",
      () -> new FenceGateBlock(ModWoodType.MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, SlabBlock> POLISHED_MAHOGANY_SLAB = registerBlock(
      "polished_mahogany_slab", () -> new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, ButtonBlock> POLISHED_MAHOGANY_BUTTON = registerBlock(
      "polished_mahogany_button",
      () -> new ButtonBlock(
         ModBlockSetType.POLISHED_MAHOGANY,
         30,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, PressurePlateBlock> POLISHED_MAHOGANY_PRESSURE_PLATE = registerBlock(
      "polished_mahogany_pressure_plate",
      () -> new PressurePlateBlock(
         ModBlockSetType.POLISHED_MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noCollission()
      )
   );
   public static final DeferredHolder<Block, DoorBlock> POLISHED_MAHOGANY_DOOR = registerBlock(
      "polished_mahogany_door",
      () -> new DoorBlock(
         ModBlockSetType.POLISHED_MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, TrapDoorBlock> POLISHED_MAHOGANY_TRAPDOOR = registerBlockWithConsumer(
      "polished_mahogany_trapdoor",
      () -> new TrapDoorBlock(
         ModBlockSetType.POLISHED_MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new TrapdoorCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_TRAPDOOR)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_MAHOGANY_TRAPDOOR))
   );
   public static final DeferredHolder<Block, WillowLog> WILLOW_LOG = registerBlock(
      "willow_log", () -> new WillowLog(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG))
   );
   public static final DeferredHolder<Block, WillowWood> WILLOW_WOOD = registerBlock(
      "willow_wood", () -> new WillowWood(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD))
   );
   public static final DeferredHolder<Block, RotatedPillarBlock> STRIPPED_WILLOW_LOG = registerBlock(
      "stripped_willow_log", () -> new RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
   );
   public static final DeferredHolder<Block, RotatedPillarBlock> STRIPPED_WILLOW_WOOD = registerBlock(
      "stripped_willow_wood",
      () -> new RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
   );
   public static final DeferredHolder<Block, Block> WILLOW_PLANKS = registerBlock(
      "willow_planks", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, StairBlock> WILLOW_STAIRS = registerBlock(
      "willow_stairs",
      () -> new StairBlock(
         ((Block)WILLOW_PLANKS.get()).defaultBlockState(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
      )
   );
   public static final DeferredHolder<Block, Block> WILLOW_VINES = registerBlock(
      "willow_vines",
      () -> new WillowVinesBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .instabreak()
            .sound(SoundType.WEEPING_VINES)
      )
   );
   public static final DeferredHolder<Block, Block> WILLOW_VINES_PLANT = registerBlockNoItem(
      "willow_vines_plant",
      () -> new WillowVinesPlantBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .instabreak()
            .sound(SoundType.WEEPING_VINES)
      )
   );
   public static final DeferredHolder<Block, FenceBlock> WILLOW_FENCE = registerBlock(
      "willow_fence", () -> new FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, FenceGateBlock> WILLOW_FENCE_GATE = registerBlock(
      "willow_fence_gate",
      () -> new FenceGateBlock(ModWoodType.WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, SlabBlock> WILLOW_SLAB = registerBlock(
      "willow_slab", () -> new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, ButtonBlock> WILLOW_BUTTON = registerBlock(
      "willow_button",
      () -> new ButtonBlock(
         ModBlockSetType.WILLOW,
         30,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, PressurePlateBlock> WILLOW_PRESSURE_PLATE = registerBlock(
      "willow_pressure_plate",
      () -> new PressurePlateBlock(
         ModBlockSetType.WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noCollission()
      )
   );
   public static final DeferredHolder<Block, DoorBlock> WILLOW_DOOR = registerBlock(
      "willow_door",
      () -> new DoorBlock(ModBlockSetType.WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion())
   );
   public static final DeferredHolder<Block, TrapDoorBlock> WILLOW_TRAPDOOR = registerBlock(
      "willow_trapdoor",
      () -> new TrapDoorBlock(
         ModBlockSetType.WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, LeavesBlock> WILLOW_LEAVES = registerBlock(
      "willow_leaves",
      () -> new LeavesBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.AZALEA_LEAVES)
            .noOcclusion()
            .isSuffocating(ModBlocks.Properties::never)
            .isViewBlocking(ModBlocks.Properties::never)
      )
   );
   public static final DeferredHolder<Block, SaplingBlock> WILLOW_SAPLING = registerBlock(
      "willow_sapling",
      () -> new SaplingBlock(
         HexereiTree.getGrower("willow_tree", ModConfiguredFeatures.WILLOW_KEY),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
      )
   );
   public static final DeferredHolder<Block, Block> POLISHED_WILLOW_PLANKS = registerBlock(
      "polished_willow_planks", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, StairBlock> POLISHED_WILLOW_STAIRS = registerBlock(
      "polished_willow_stairs",
      () -> new StairBlock(
         ((Block)POLISHED_WILLOW_PLANKS.get()).defaultBlockState(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
      )
   );
   public static final DeferredHolder<Block, FenceBlock> POLISHED_WILLOW_FENCE = registerBlock(
      "polished_willow_fence", () -> new FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, FenceGateBlock> POLISHED_WILLOW_FENCE_GATE = registerBlock(
      "polished_willow_fence_gate",
      () -> new FenceGateBlock(ModWoodType.POLISHED_WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, SlabBlock> POLISHED_WILLOW_SLAB = registerBlock(
      "polished_willow_slab", () -> new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, ButtonBlock> POLISHED_WILLOW_BUTTON = registerBlock(
      "polished_willow_button",
      () -> new ButtonBlock(
         ModBlockSetType.POLISHED_WILLOW,
         30,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, PressurePlateBlock> POLISHED_WILLOW_PRESSURE_PLATE = registerBlock(
      "polished_willow_pressure_plate",
      () -> new PressurePlateBlock(
         ModBlockSetType.POLISHED_WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noCollission()
      )
   );
   public static final DeferredHolder<Block, DoorBlock> POLISHED_WILLOW_DOOR = registerBlock(
      "polished_willow_door",
      () -> new DoorBlock(
         ModBlockSetType.POLISHED_WILLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, TrapDoorBlock> POLISHED_WILLOW_TRAPDOOR = registerBlockWithConsumer(
      "polished_willow_trapdoor",
      () -> new TrapDoorBlock(
         ModBlockSetType.POLISHED_MAHOGANY, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new TrapdoorCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_TRAPDOOR)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WILLOW_TRAPDOOR))
   );
   public static final DeferredHolder<Block, WitchHazelLog> WITCH_HAZEL_LOG = registerBlock(
      "witch_hazel_log", () -> new WitchHazelLog(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG))
   );
   public static final DeferredHolder<Block, WitchHazelWood> WITCH_HAZEL_WOOD = registerBlock(
      "witch_hazel_wood", () -> new WitchHazelWood(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD))
   );
   public static final DeferredHolder<Block, RotatedPillarBlock> STRIPPED_WITCH_HAZEL_LOG = registerBlock(
      "stripped_witch_hazel_log",
      () -> new RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
   );
   public static final DeferredHolder<Block, RotatedPillarBlock> STRIPPED_WITCH_HAZEL_WOOD = registerBlock(
      "stripped_witch_hazel_wood",
      () -> new RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
   );
   public static final DeferredHolder<Block, Block> WITCH_HAZEL_PLANKS = registerBlock(
      "witch_hazel_planks", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, StairBlock> WITCH_HAZEL_STAIRS = registerBlock(
      "witch_hazel_stairs",
      () -> new StairBlock(
         ((Block)WITCH_HAZEL_PLANKS.get()).defaultBlockState(), net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
      )
   );
   public static final DeferredHolder<Block, FenceBlock> WITCH_HAZEL_FENCE = registerBlock(
      "witch_hazel_fence", () -> new FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, FenceGateBlock> WITCH_HAZEL_FENCE_GATE = registerBlock(
      "witch_hazel_fence_gate",
      () -> new FenceGateBlock(ModWoodType.WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, SlabBlock> WITCH_HAZEL_SLAB = registerBlock(
      "witch_hazel_slab", () -> new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, ButtonBlock> WITCH_HAZEL_BUTTON = registerBlock(
      "witch_hazel_button",
      () -> new ButtonBlock(
         ModBlockSetType.WITCH_HAZEL,
         30,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, PressurePlateBlock> WITCH_HAZEL_PRESSURE_PLATE = registerBlock(
      "witch_hazel_pressure_plate",
      () -> new PressurePlateBlock(
         ModBlockSetType.WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noCollission()
      )
   );
   public static final DeferredHolder<Block, DoorBlock> WITCH_HAZEL_DOOR = registerBlock(
      "witch_hazel_door",
      () -> new DoorBlock(
         ModBlockSetType.WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, TrapDoorBlock> WITCH_HAZEL_TRAPDOOR = registerBlock(
      "witch_hazel_trapdoor",
      () -> new TrapDoorBlock(
         ModBlockSetType.WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, LeavesBlock> WITCH_HAZEL_LEAVES = registerBlock(
      "witch_hazel_leaves",
      () -> new LeavesBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.AZALEA_LEAVES)
            .noOcclusion()
            .isSuffocating(ModBlocks.Properties::never)
            .isViewBlocking(ModBlocks.Properties::never)
      ) {
         public void animateTick(BlockState p_272714_, Level p_272837_, BlockPos p_273218_, RandomSource p_273360_) {
            super.animateTick(p_272714_, p_272837_, p_273218_, p_273360_);
            if (p_273360_.nextInt(10) == 0) {
               BlockPos blockpos = p_273218_.below();
               BlockState blockstate = p_272837_.getBlockState(blockpos);
               if (!isFaceFull(blockstate.getCollisionShape(p_272837_, blockpos), Direction.UP)) {
                  ParticleUtils.spawnParticleBelow(p_272837_, p_273218_, p_273360_, (ParticleOptions)ModParticleTypes.WITCH_HAZEL_LEAVES.get());
               }
            }
         }
      }
   );
   public static final DeferredHolder<Block, SaplingBlock> WITCH_HAZEL_SAPLING = registerBlock(
      "witch_hazel_sapling",
      () -> new SaplingBlock(
         HexereiTree.getGrower("witch_hazel_tree", ModConfiguredFeatures.WITCH_HAZEL_KEY),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
      )
   );
   public static final DeferredHolder<Block, Block> POLISHED_WITCH_HAZEL_PLANKS = registerBlock(
      "polished_witch_hazel_planks", () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, StairBlock> POLISHED_WITCH_HAZEL_STAIRS = registerBlock(
      "polished_witch_hazel_stairs",
      () -> new StairBlock(
         ((Block)POLISHED_WITCH_HAZEL_PLANKS.get()).defaultBlockState(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
      )
   );
   public static final DeferredHolder<Block, FenceBlock> POLISHED_WITCH_HAZEL_FENCE = registerBlock(
      "polished_witch_hazel_fence", () -> new FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, FenceGateBlock> POLISHED_WITCH_HAZEL_FENCE_GATE = registerBlock(
      "polished_witch_hazel_fence_gate",
      () -> new FenceGateBlock(ModWoodType.POLISHED_WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, SlabBlock> POLISHED_WITCH_HAZEL_SLAB = registerBlock(
      "polished_witch_hazel_slab", () -> new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
   );
   public static final DeferredHolder<Block, ButtonBlock> POLISHED_WITCH_HAZEL_BUTTON = registerBlock(
      "polished_witch_hazel_button",
      () -> new ButtonBlock(
         ModBlockSetType.POLISHED_WITCH_HAZEL,
         30,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, PressurePlateBlock> POLISHED_WITCH_HAZEL_PRESSURE_PLATE = registerBlock(
      "polished_witch_hazel_pressure_plate",
      () -> new PressurePlateBlock(
         ModBlockSetType.POLISHED_WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noCollission()
      )
   );
   public static final DeferredHolder<Block, DoorBlock> POLISHED_WITCH_HAZEL_DOOR = registerBlock(
      "polished_witch_hazel_door",
      () -> new DoorBlock(
         ModBlockSetType.POLISHED_WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
      )
   );
   public static final DeferredHolder<Block, TrapDoorBlock> POLISHED_WITCH_HAZEL_TRAPDOOR = registerBlockWithConsumer(
      "polished_witch_hazel_trapdoor",
      () -> new TrapDoorBlock(
         ModBlockSetType.POLISHED_WITCH_HAZEL, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
      ),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.connectedTextures(() -> new TrapdoorCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_TRAPDOOR)),
      !FMLEnvironment.dist.isClient()
         ? block -> {}
         : ModBlocks.ClientCTHandler.blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WITCH_HAZEL_TRAPDOOR))
   );
   public static final DeferredHolder<Block, FloweringLilyPadBlock> LILY_PAD_BLOCK = registerBlockNoItem(
      "flowering_lily_pad",
      () -> new FloweringLilyPadBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .instabreak()
            .sound(SoundType.LILY_PAD)
            .noOcclusion()
      )
   );
   public static final DeferredHolder<Block, PickablePlant> MANDRAKE_PLANT = registerBlock(
      "mandrake_plant",
      () -> new PickablePlant(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION),
         ModItems.MANDRAKE_FLOWERS.getKey(),
         2,
         ModItems.MANDRAKE_ROOT.getKey(),
         1
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Block, PickablePlant> BELLADONNA_PLANT = registerBlock(
      "belladonna_plant",
      () -> new PickablePlant(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION),
         ModItems.BELLADONNA_FLOWERS.getKey(),
         2,
         ModItems.BELLADONNA_BERRIES.getKey(),
         6
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Block, PickableDoublePlant> MUGWORT_BUSH = registerBlock(
      "mugwort_bush",
      () -> new PickableDoublePlant(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .instabreak()
            .sound(SoundType.AZALEA),
         ModItems.MUGWORT_LEAVES.getKey(),
         4,
         ModItems.MUGWORT_FLOWERS.getKey(),
         3
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Block, PickableDoublePlant> YELLOW_DOCK_BUSH = registerBlock(
      "yellow_dock_bush",
      () -> new PickableDoublePlant(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .instabreak()
            .sound(SoundType.AZALEA),
         ModItems.YELLOW_DOCK_LEAVES.getKey(),
         4,
         ModItems.YELLOW_DOCK_FLOWERS.getKey(),
         3
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Block, Block> POTTED_MANDRAKE_PLANT = registerBlockNoItem(
      "potted_mandrake_plant",
      () -> new FlowerPotBlock(
         (Block)MANDRAKE_PLANT.get(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, Block> POTTED_BELLADONNA_PLANT = registerBlockNoItem(
      "potted_belladonna_plant",
      () -> new FlowerPotBlock(
         (Block)BELLADONNA_PLANT.get(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, Block> POTTED_YELLOW_DOCK_BUSH = registerBlockNoItem(
      "potted_yellow_dock_bush",
      () -> new FlowerPotBlock(
         (Block)YELLOW_DOCK_BUSH.get(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, Block> POTTED_MUGWORT_BUSH = registerBlockNoItem(
      "potted_mugwort_bush",
      () -> new FlowerPotBlock(
         (Block)MUGWORT_BUSH.get(),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredHolder<Block, AmethystBlock> SELENITE_BLOCK = registerBlock(
      "selenite_block",
      () -> new AmethystBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .strength(0.5F)
            .sound(SoundType.AMETHYST)
            .noOcclusion()
      ) {
         public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
            return (pSide == Direction.UP || pSide == Direction.DOWN) && pAdjacentBlockState.is(this)
               || super.skipRendering(pState, pAdjacentBlockState, pSide);
         }
      }
   );
   public static final DeferredHolder<Block, AmethystBlock> BUDDING_SELENITE = registerBlock(
      "budding_selenite",
      () -> new BuddingSelenite(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .randomTicks()
            .strength(1.0F)
            .sound(SoundType.AMETHYST)
            .noOcclusion()
      ) {
         public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
            return (pSide == Direction.UP || pSide == Direction.DOWN) && pAdjacentBlockState.is(this)
               || super.skipRendering(pState, pAdjacentBlockState, pSide);
         }
      }
   );
   public static final DeferredHolder<Block, AmethystBlock> SELENITE_CLUSTER = registerBlock(
      "selenite_cluster",
      () -> new AmethystClusterBlock(
         7.0F,
         3.0F,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .noOcclusion()
            .strength(0.5F)
            .randomTicks()
            .sound(SoundType.AMETHYST_CLUSTER)
            .noOcclusion()
            .lightLevel(p_152632_ -> 5)
      )
   );
   public static final DeferredHolder<Block, AmethystBlock> LARGE_SELENITE_BUD = registerBlock(
      "large_selenite_bud",
      () -> new AmethystClusterBlock(
         5.0F,
         3.0F,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER)
            .sound(SoundType.MEDIUM_AMETHYST_BUD)
            .noOcclusion()
            .lightLevel(p_152629_ -> 4)
      )
   );
   public static final DeferredHolder<Block, AmethystBlock> MEDIUM_SELENITE_BUD = registerBlock(
      "medium_selenite_bud",
      () -> new AmethystClusterBlock(
         4.0F,
         3.0F,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER)
            .sound(SoundType.LARGE_AMETHYST_BUD)
            .noOcclusion()
            .lightLevel(p_152617_ -> 2)
      )
   );
   public static final DeferredHolder<Block, AmethystBlock> SMALL_SELENITE_BUD = registerBlock(
      "small_selenite_bud",
      () -> new AmethystClusterBlock(
         3.0F,
         4.0F,
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER)
            .sound(SoundType.SMALL_AMETHYST_BUD)
            .noOcclusion()
            .lightLevel(p_187409_ -> 1)
      )
   );
   public static final DeferredHolder<Block, Block> BLOOD_SIGIL = registerBlockNoItem(
      "blood_sigil",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .explosionResistance(5.0F)
      )
   );
   public static final DeferredHolder<Block, Block> MIXING_CAULDRON_DYE = registerBlockNoItem(
      "mixing_cauldron_dye",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .explosionResistance(5.0F)
      )
   );
   public static final DeferredHolder<Block, Block> CANDLE_DIPPER_WICK_BASE = registerBlockNoItem(
      "candle_dipper_wick_base",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).noCollission().explosionResistance(1.0F)
      )
   );
   public static final DeferredHolder<Block, Block> CANDLE_DIPPER_WICK = registerBlockNoItem(
      "candle_dipper_wick",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.0F)
            .noCollission()
            .explosionResistance(1.0F)
      )
   );
   public static final DeferredHolder<Block, Block> CANDLE_DIPPER_CANDLE_1 = registerBlockNoItem(
      "candle_dipper_candle_1",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.0F)
            .noCollission()
            .explosionResistance(1.0F)
      )
   );
   public static final DeferredHolder<Block, Block> CANDLE_DIPPER_CANDLE_2 = registerBlockNoItem(
      "candle_dipper_candle_2",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.0F)
            .noCollission()
            .explosionResistance(1.0F)
      )
   );
   public static final DeferredHolder<Block, Block> CANDLE_DIPPER_CANDLE_3 = registerBlockNoItem(
      "candle_dipper_candle_3",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.0F)
            .noCollission()
            .explosionResistance(1.0F)
      )
   );
   public static final DeferredHolder<Block, Block> CRYSTAL_BALL_ORB = registerBlockNoItem(
      "crystal_ball_orb",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> CRYSTAL_BALL_LARGE_RING = registerBlockNoItem(
      "crystal_ball_large_ring",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> CRYSTAL_BALL_SMALL_RING = registerBlockNoItem(
      "crystal_ball_small_ring",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> CRYSTAL_BALL_STAND = registerBlockNoItem(
      "crystal_ball_stand",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_OF_SHADOWS_COVER = registerBlockNoItem(
      "book_of_shadows_cover",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_OF_SHADOWS_COVER_CORNERS = registerBlockNoItem(
      "book_of_shadows_cover_corners",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_OF_SHADOWS_BACK = registerBlockNoItem(
      "book_of_shadows_back",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_OF_SHADOWS_BACK_CORNERS = registerBlockNoItem(
      "book_of_shadows_back_corners",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_OF_SHADOWS_BINDING = registerBlockNoItem(
      "book_of_shadows_binding",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_OF_SHADOWS_PAGE = registerBlockNoItem(
      "book_of_shadows_page_blank",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_COVER = registerBlockNoItem(
      "book_cover",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_COVER_CORNERS = registerBlockNoItem(
      "book_cover_corners",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_BACK = registerBlockNoItem(
      "book_back",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_BACK_CORNERS = registerBlockNoItem(
      "book_back_corners",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_BINDING = registerBlockNoItem(
      "book_binding",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> BOOK_PAGE = registerBlockNoItem(
      "book_page_blank",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.0F).explosionResistance(2.0F))
   );
   public static final DeferredHolder<Block, Block> COFFER_CHEST = registerBlockNoItem(
      "coffer_chest",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredHolder<Block, Block> COFFER_LID = registerBlockNoItem(
      "coffer_lid",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredHolder<Block, Block> COFFER_CONTAINER = registerBlockNoItem(
      "coffer_container",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> COFFER_HINGE = registerBlockNoItem(
      "coffer_hinge",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_JAR_GENERIC = registerBlockNoItem(
      "herb_jar_generic",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_JAR_BELLADONNA = registerBlockNoItem(
      "herb_jar_belladonna",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_JAR_MANDRAKE_PLANT = registerBlockNoItem(
      "herb_jar_mandrake_plant",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_JAR_MANDRAKE_ROOT = registerBlockNoItem(
      "herb_jar_mandrake_root",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_JAR_MUGWORT = registerBlockNoItem(
      "herb_jar_mugwort",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_JAR_YELLOW_DOCK = registerBlockNoItem(
      "herb_jar_yellow_dock",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_DRYING_RACK_BROWN_MUSHROOM_1 = registerBlockNoItem(
      "herb_drying_rack_brown_mushroom_1",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_DRYING_RACK_BROWN_MUSHROOM_2 = registerBlockNoItem(
      "herb_drying_rack_brown_mushroom_2",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_DRYING_RACK_RED_MUSHROOM_1 = registerBlockNoItem(
      "herb_drying_rack_red_mushroom_1",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> HERB_DRYING_RACK_RED_MUSHROOM_2 = registerBlockNoItem(
      "herb_drying_rack_red_mushroom_2",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> PESTLE_AND_MORTAR_PESTLE = registerBlockNoItem(
      "pestle_and_mortar_pestle",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_5 = registerBlockNoItem(
      "dried_sage_bundle_plate_5",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_4 = registerBlockNoItem(
      "dried_sage_bundle_plate_4",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_3 = registerBlockNoItem(
      "dried_sage_bundle_plate_3",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_2 = registerBlockNoItem(
      "dried_sage_bundle_plate_2",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_1 = registerBlockNoItem(
      "dried_sage_bundle_plate_1",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_5_LIT = registerBlockNoItem(
      "dried_sage_bundle_plate_5_lit",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_4_LIT = registerBlockNoItem(
      "dried_sage_bundle_plate_4_lit",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_3_LIT = registerBlockNoItem(
      "dried_sage_bundle_plate_3_lit",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_2_LIT = registerBlockNoItem(
      "dried_sage_bundle_plate_2_lit",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );
   public static final DeferredHolder<Block, Block> DRIED_SAGE_BUNDLE_PLATE_1_LIT = registerBlockNoItem(
      "dried_sage_bundle_plate_1_lit",
      () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).explosionResistance(8.0F))
   );

   private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
      DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
      registerBlockItem(name, toReturn);
      return toReturn;
   }

   private static <T extends Block> DeferredHolder<Block, T> registerBlockWithConsumer(String name, Supplier<T> block, Consumer<Block>... consumers) {
      DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
      afterRegisterConsumer.put(toReturn, block1 -> {
         for (Consumer<Block> consumer1 : consumers) {
            consumer1.accept((Block)toReturn.get());
         }
      });
      return toReturn;
   }

   private static <T extends Block> DeferredHolder<Block, T> registerBlockNoItem(String name, Supplier<T> block) {
      return BLOCKS.register(name, block);
   }

   private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block) {
      ModItems.ITEMS.register(name, () -> new BlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties()));
   }

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }

   public static class ClientCTHandler {
      private static void registerCTBehviour(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
         ConnectedTextureBehaviour behavior = behaviorSupplier.get();
         ClientProxy.MODEL_SWAPPER.getCustomBlockModels().register(HexereiUtil.getKeyOrThrow(entry), model -> new CTModel(model, behavior));
      }

      public static Consumer<Block> blockConnectivity(BiConsumer<Block, BlockConnectivity> consumer) {
         return entry -> onClient(() -> () -> registerBlockConnectivity(entry, consumer));
      }

      private static void registerBlockConnectivity(Block entry, BiConsumer<Block, BlockConnectivity> consumer) {
         consumer.accept(entry, ClientProxy.BLOCK_CONNECTIVITY);
      }

      protected static void onClient(Supplier<Runnable> toRun) {
         if (FMLEnvironment.dist.isClient()) {
            toRun.get().run();
         }
      }

      public static Consumer<Block> connectedTextures(Supplier<ConnectedTextureBehaviour> behavior) {
         return entry -> onClient(() -> () -> registerCTBehviour(entry, behavior));
      }
   }

   public static class Properties {
      public static boolean always(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
         return true;
      }

      public static boolean hasPostProcess(BlockState state, BlockGetter reader, BlockPos pos) {
         return true;
      }

      public static boolean never(BlockState state, BlockGetter reader, BlockPos pos) {
         return false;
      }
   }
}
