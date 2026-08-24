package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.common.blocks.BlockConjured;
import at.petrak.hexcasting.common.blocks.BlockConjuredLight;
import at.petrak.hexcasting.common.blocks.BlockFlammable;
import at.petrak.hexcasting.common.blocks.BlockQuenchedAllay;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicLigature;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicRecord;
import at.petrak.hexcasting.common.blocks.circles.BlockEmptyImpetus;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import at.petrak.hexcasting.common.blocks.circles.directrix.BlockBooleanDirectrix;
import at.petrak.hexcasting.common.blocks.circles.directrix.BlockEmptyDirectrix;
import at.petrak.hexcasting.common.blocks.circles.directrix.BlockRedstoneDirectrix;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockLookingImpetus;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockRedstoneImpetus;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockRightClickImpetus;
import at.petrak.hexcasting.common.blocks.decoration.BlockAkashicLeaves;
import at.petrak.hexcasting.common.blocks.decoration.BlockAkashicLog;
import at.petrak.hexcasting.common.blocks.decoration.BlockAmethystDirectional;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexDoor;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexFence;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexFenceGate;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexPressurePlate;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexSlab;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexStairs;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexTrapdoor;
import at.petrak.hexcasting.common.blocks.decoration.BlockHexWoodButton;
import at.petrak.hexcasting.common.blocks.decoration.BlockSconce;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public class HexBlocks {
   private static final Map<ResourceLocation, Block> BLOCKS = new LinkedHashMap<>();
   private static final Map<ResourceLocation, Pair<Block, Properties>> BLOCK_ITEMS = new LinkedHashMap<>();
   private static final Map<CreativeModeTab, List<Block>> BLOCK_TABS = new LinkedHashMap<>();
   public static final Block CONJURED_LIGHT = blockItem(
      "conjured_light",
      new BlockConjuredLight(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.NONE)
            .sound(SoundType.AMETHYST)
            .lightLevel(state -> 15)
            .noLootTable()
            .isValidSpawn((xva$0, xva$1, xva$2, xva$3) -> never(xva$0, xva$1, xva$2, xva$3))
            .instabreak()
            .pushReaction(PushReaction.DESTROY)
            .noCollission()
            .isSuffocating((xva$0, xva$1, xva$2) -> never(xva$0, xva$1, xva$2))
            .isViewBlocking((xva$0, xva$1, xva$2) -> never(xva$0, xva$1, xva$2))
      ),
      new Properties()
   );
   public static final Block CONJURED_BLOCK = blockItem(
      "conjured_block",
      new BlockConjured(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.NONE)
            .sound(SoundType.AMETHYST)
            .lightLevel(state -> 2)
            .noLootTable()
            .isValidSpawn((xva$0, xva$1, xva$2, xva$3) -> never(xva$0, xva$1, xva$2, xva$3))
            .instabreak()
            .noOcclusion()
            .isSuffocating((xva$0, xva$1, xva$2) -> never(xva$0, xva$1, xva$2))
            .isViewBlocking((xva$0, xva$1, xva$2) -> never(xva$0, xva$1, xva$2))
      ),
      new Properties()
   );
   public static final BlockSlate SLATE = blockNoItem("slate", new BlockSlate(slateish().pushReaction(PushReaction.DESTROY)));
   public static final BlockEmptyImpetus IMPETUS_EMPTY = blockItem("impetus/empty", new BlockEmptyImpetus(slateish().pushReaction(PushReaction.BLOCK)));
   public static final BlockRightClickImpetus IMPETUS_RIGHTCLICK = blockItem(
      "impetus/rightclick",
      new BlockRightClickImpetus(slateish().pushReaction(PushReaction.BLOCK).lightLevel(bs -> bs.getValue(BlockAbstractImpetus.ENERGIZED) ? 15 : 0))
   );
   public static final BlockLookingImpetus IMPETUS_LOOK = blockItem(
      "impetus/look",
      new BlockLookingImpetus(slateish().pushReaction(PushReaction.BLOCK).lightLevel(bs -> bs.getValue(BlockAbstractImpetus.ENERGIZED) ? 15 : 0))
   );
   public static final BlockRedstoneImpetus IMPETUS_REDSTONE = blockItem(
      "impetus/redstone",
      new BlockRedstoneImpetus(slateish().pushReaction(PushReaction.BLOCK).lightLevel(bs -> bs.getValue(BlockAbstractImpetus.ENERGIZED) ? 15 : 0))
   );
   public static final BlockEmptyDirectrix EMPTY_DIRECTRIX = blockItem("directrix/empty", new BlockEmptyDirectrix(slateish().pushReaction(PushReaction.BLOCK)));
   public static final BlockRedstoneDirectrix DIRECTRIX_REDSTONE = blockItem(
      "directrix/redstone", new BlockRedstoneDirectrix(slateish().pushReaction(PushReaction.BLOCK))
   );
   public static final BlockBooleanDirectrix DIRECTRIX_BOOLEAN = blockItem(
      "directrix/boolean", new BlockBooleanDirectrix(slateish().pushReaction(PushReaction.BLOCK))
   );
   public static final BlockAkashicRecord AKASHIC_RECORD = blockItem("akashic_record", new BlockAkashicRecord(akashicWoodyHard().lightLevel(bs -> 15)));
   public static final BlockAkashicBookshelf AKASHIC_BOOKSHELF = blockItem(
      "akashic_bookshelf", new BlockAkashicBookshelf(akashicWoodyHard().lightLevel(bs -> bs.getValue(BlockAkashicBookshelf.HAS_BOOKS) ? 4 : 0))
   );
   public static final BlockAkashicLigature AKASHIC_LIGATURE = blockItem("akashic_connector", new BlockAkashicLigature(akashicWoodyHard().lightLevel(bs -> 4)));
   public static final BlockQuenchedAllay QUENCHED_ALLAY = blockItem("quenched_allay", new BlockQuenchedAllay(quenched()));
   public static final BlockQuenchedAllay QUENCHED_ALLAY_TILES = blockItem("quenched_allay_tiles", new BlockQuenchedAllay(quenched()));
   public static final BlockQuenchedAllay QUENCHED_ALLAY_BRICKS = blockItem("quenched_allay_bricks", new BlockQuenchedAllay(quenched()));
   public static final BlockQuenchedAllay QUENCHED_ALLAY_BRICKS_SMALL = blockItem("quenched_allay_bricks_small", new BlockQuenchedAllay(quenched()));
   public static final Block SLATE_BLOCK = blockItem("slate_block", new Block(slateish().strength(2.0F, 4.0F)));
   public static final Block SLATE_TILES = blockItem("slate_tiles", new Block(slateish().strength(2.0F, 4.0F)));
   public static final Block SLATE_BRICKS = blockItem("slate_bricks", new Block(slateish().strength(2.0F, 4.0F)));
   public static final Block SLATE_BRICKS_SMALL = blockItem("slate_bricks_small", new Block(slateish().strength(2.0F, 4.0F)));
   public static final RotatedPillarBlock SLATE_PILLAR = blockItem("slate_pillar", new RotatedPillarBlock(slateish().strength(2.0F, 4.0F)));
   public static final ColoredFallingBlock AMETHYST_DUST_BLOCK = blockItem(
      "amethyst_dust_block",
      new ColoredFallingBlock(
         new ColorRGBA(-5009677),
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.SAND)
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(0.5F)
            .sound(SoundType.SAND)
      )
   );
   public static final AmethystBlock AMETHYST_TILES = blockItem(
      "amethyst_tiles", new AmethystBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.AMETHYST_BLOCK))
   );
   public static final AmethystBlock AMETHYST_BRICKS = blockItem(
      "amethyst_bricks", new AmethystBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.AMETHYST_BLOCK))
   );
   public static final AmethystBlock AMETHYST_BRICKS_SMALL = blockItem(
      "amethyst_bricks_small", new AmethystBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.AMETHYST_BLOCK))
   );
   public static final BlockAmethystDirectional AMETHYST_PILLAR = blockItem(
      "amethyst_pillar", new BlockAmethystDirectional(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.AMETHYST_BLOCK))
   );
   public static final Block SLATE_AMETHYST_TILES = blockItem("slate_amethyst_tiles", new Block(slateish().strength(2.0F, 4.0F)));
   public static final Block SLATE_AMETHYST_BRICKS = blockItem("slate_amethyst_bricks", new Block(slateish().strength(2.0F, 4.0F)));
   public static final Block SLATE_AMETHYST_BRICKS_SMALL = blockItem("slate_amethyst_bricks_small", new Block(slateish().strength(2.0F, 4.0F)));
   public static final RotatedPillarBlock SLATE_AMETHYST_PILLAR = blockItem("slate_amethyst_pillar", new RotatedPillarBlock(slateish().strength(2.0F, 4.0F)));
   public static final Block SCROLL_PAPER = blockItem("scroll_paper", new BlockFlammable(papery(MapColor.TERRACOTTA_WHITE), 100, 60));
   public static final Block ANCIENT_SCROLL_PAPER = blockItem("ancient_scroll_paper", new BlockFlammable(papery(MapColor.TERRACOTTA_ORANGE), 100, 60));
   public static final Block SCROLL_PAPER_LANTERN = blockItem(
      "scroll_paper_lantern", new BlockFlammable(papery(MapColor.TERRACOTTA_WHITE).lightLevel($ -> 15), 100, 60)
   );
   public static final Block ANCIENT_SCROLL_PAPER_LANTERN = blockItem(
      "ancient_scroll_paper_lantern", new BlockFlammable(papery(MapColor.TERRACOTTA_ORANGE).lightLevel($ -> 12), 100, 60)
   );
   public static final BlockSconce SCONCE = blockItem(
      "amethyst_sconce",
      new BlockSconce(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .sound(SoundType.AMETHYST)
            .strength(1.0F)
            .lightLevel($ -> 15)
      ),
      HexItems.props().rarity(Rarity.RARE)
   );
   public static final BlockAkashicLog EDIFIED_LOG = blockItem("edified_log", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog EDIFIED_LOG_AMETHYST = blockItem("edified_log_amethyst", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog EDIFIED_LOG_AVENTURINE = blockItem("edified_log_aventurine", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog EDIFIED_LOG_CITRINE = blockItem("edified_log_citrine", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog EDIFIED_LOG_PURPLE = blockItem("edified_log_purple", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog STRIPPED_EDIFIED_LOG = blockItem("stripped_edified_log", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog EDIFIED_WOOD = blockItem("edified_wood", new BlockAkashicLog(edifiedWoody()));
   public static final BlockAkashicLog STRIPPED_EDIFIED_WOOD = blockItem("stripped_edified_wood", new BlockAkashicLog(edifiedWoody()));
   public static final Block EDIFIED_PLANKS = blockItem("edified_planks", new BlockFlammable(edifiedWoody(), 20, 5));
   public static final Block EDIFIED_PANEL = blockItem("edified_panel", new BlockFlammable(edifiedWoody(), 20, 5));
   public static final Block EDIFIED_TILE = blockItem("edified_tile", new BlockFlammable(edifiedWoody(), 20, 5));
   public static final DoorBlock EDIFIED_DOOR = blockItem("edified_door", new BlockHexDoor(edifiedWoody().noOcclusion()));
   public static final TrapDoorBlock EDIFIED_TRAPDOOR = blockItem("edified_trapdoor", new BlockHexTrapdoor(edifiedWoody().noOcclusion()));
   public static final StairBlock EDIFIED_STAIRS = blockItem(
      "edified_stairs", new BlockHexStairs(EDIFIED_PLANKS.defaultBlockState(), edifiedWoody().noOcclusion())
   );
   public static final FenceBlock EDIFIED_FENCE = blockItem("edified_fence", new BlockHexFence(edifiedWoody().noOcclusion()));
   public static final FenceGateBlock EDIFIED_FENCE_GATE = blockItem("edified_fence_gate", new BlockHexFenceGate(edifiedWoody().noOcclusion()));
   public static final SlabBlock EDIFIED_SLAB = blockItem("edified_slab", new BlockHexSlab(edifiedWoody().noOcclusion()));
   public static final ButtonBlock EDIFIED_BUTTON = blockItem("edified_button", new BlockHexWoodButton(edifiedWoody().noOcclusion().noCollission()));
   public static final PressurePlateBlock EDIFIED_PRESSURE_PLATE = blockItem(
      "edified_pressure_plate", new BlockHexPressurePlate(HexBlockSetTypes.EDIFIED_WOOD, edifiedWoody().noOcclusion().noCollission())
   );
   public static final BlockAkashicLeaves AMETHYST_EDIFIED_LEAVES = blockItem("amethyst_edified_leaves", new BlockAkashicLeaves(leaves(MapColor.COLOR_PURPLE)));
   public static final BlockAkashicLeaves AVENTURINE_EDIFIED_LEAVES = blockItem(
      "aventurine_edified_leaves", new BlockAkashicLeaves(leaves(MapColor.COLOR_BLUE))
   );
   public static final BlockAkashicLeaves CITRINE_EDIFIED_LEAVES = blockItem("citrine_edified_leaves", new BlockAkashicLeaves(leaves(MapColor.COLOR_YELLOW)));

   public static void registerBlocks(BiConsumer<Block, ResourceLocation> r) {
      for (Entry<ResourceLocation, Block> e : BLOCKS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   public static void registerBlockItems(BiConsumer<Item, ResourceLocation> r) {
      for (Entry<ResourceLocation, Pair<Block, Properties>> e : BLOCK_ITEMS.entrySet()) {
         r.accept(new BlockItem((Block)e.getValue().getFirst(), (Properties)e.getValue().getSecond()), e.getKey());
      }
   }

   public static void registerBlockCreativeTab(Consumer<Block> r, CreativeModeTab tab) {
      for (Block block : BLOCK_TABS.getOrDefault(tab, List.of())) {
         r.accept(block);
      }
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties slateish() {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.DEEPSLATE_TILES).strength(4.0F, 4.0F);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties papery(MapColor color) {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .mapColor(color)
         .sound(SoundType.GRASS)
         .instabreak()
         .ignitedByLava()
         .pushReaction(PushReaction.DESTROY);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties akashicWoodyHard() {
      return woodyHard(MapColor.COLOR_PURPLE);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties woodyHard(MapColor color) {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_LOG)
         .mapColor(color)
         .sound(SoundType.WOOD)
         .strength(3.0F, 4.0F);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties edifiedWoody() {
      return woody(MapColor.COLOR_PURPLE);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties woody(MapColor color) {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_LOG).mapColor(color).sound(SoundType.WOOD).strength(2.0F);
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties leaves(MapColor color) {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.OAK_LEAVES)
         .strength(0.2F)
         .randomTicks()
         .sound(SoundType.GRASS)
         .noOcclusion()
         .isValidSpawn((bs, level, pos, type) -> type == EntityType.OCELOT || type == EntityType.PARROT)
         .isSuffocating((xva$0, xva$1, xva$2) -> never(xva$0, xva$1, xva$2))
         .isViewBlocking((xva$0, xva$1, xva$2) -> never(xva$0, xva$1, xva$2));
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties quenched() {
      return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofLegacyCopy(Blocks.AMETHYST_BLOCK).lightLevel($ -> 4).noOcclusion();
   }

   private static boolean never(Object... args) {
      return false;
   }

   private static <T extends Block> T blockNoItem(String name, T block) {
      Block old = BLOCKS.put(HexAPI.modLoc(name), block);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return block;
      }
   }

   private static <T extends Block> T blockItem(String name, T block) {
      return blockItem(name, block, HexItems.props(), HexCreativeTabs.HEX);
   }

   private static <T extends Block> T blockItem(String name, T block, @Nullable CreativeModeTab tab) {
      return blockItem(name, block, HexItems.props(), tab);
   }

   private static <T extends Block> T blockItem(String name, T block, Properties props) {
      return blockItem(name, block, props, HexCreativeTabs.HEX);
   }

   private static <T extends Block> T blockItem(String name, T block, Properties props, @Nullable CreativeModeTab tab) {
      blockNoItem(name, block);
      Pair<Block, Properties> old = BLOCK_ITEMS.put(HexAPI.modLoc(name), new Pair(block, props));
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         if (tab != null) {
            BLOCK_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(block);
         }

         return block;
      }
   }
}
