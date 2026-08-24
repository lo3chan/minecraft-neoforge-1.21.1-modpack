package com.mcwfurnitures.kikoz.init;

import com.mcwfurnitures.kikoz.objects.Chair;
import com.mcwfurnitures.kikoz.objects.Chaise;
import com.mcwfurnitures.kikoz.objects.Couch;
import com.mcwfurnitures.kikoz.objects.Desk;
import com.mcwfurnitures.kikoz.objects.Table;
import com.mcwfurnitures.kikoz.objects.TableHitbox;
import com.mcwfurnitures.kikoz.objects.TallFurniture;
import com.mcwfurnitures.kikoz.objects.TallFurnitureHinge;
import com.mcwfurnitures.kikoz.objects.WideFurniture;
import com.mcwfurnitures.kikoz.objects.bookshelves.BookCabinet;
import com.mcwfurnitures.kikoz.objects.bookshelves.BookCabinetHinge;
import com.mcwfurnitures.kikoz.objects.bookshelves.BookDrawer;
import com.mcwfurnitures.kikoz.objects.cabinets.Cabinet;
import com.mcwfurnitures.kikoz.objects.cabinets.CabinetHinge;
import com.mcwfurnitures.kikoz.objects.chairs.ClassicChair;
import com.mcwfurnitures.kikoz.objects.chairs.ModernChair;
import com.mcwfurnitures.kikoz.objects.chairs.StripedChair;
import com.mcwfurnitures.kikoz.objects.counters.Counter;
import com.mcwfurnitures.kikoz.objects.counters.CupboardCounter;
import com.mcwfurnitures.kikoz.objects.counters.SinkCounter;
import com.mcwfurnitures.kikoz.objects.counters.StorageCounter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwfurnitures");
   public static final DeferredBlock<Block> OAK_WARDROBE = BLOCKS.register(
      "oak_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_MODERN_WARDROBE = BLOCKS.register(
      "oak_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_DOUBLE_WARDROBE = BLOCKS.register(
      "oak_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_BOOKSHELF = BLOCKS.register(
      "oak_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "oak_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_DRAWER = BLOCKS.register(
      "oak_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_DOUBLE_DRAWER = BLOCKS.register(
      "oak_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_BOOKSHELF_DRAWER = BLOCKS.register(
      "oak_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "oak_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_LARGE_DRAWER = BLOCKS.register(
      "oak_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "oak_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_TRIPLE_DRAWER = BLOCKS.register(
      "oak_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_DESK = BLOCKS.register("oak_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)));
   public static final DeferredBlock<Block> OAK_COVERED_DESK = BLOCKS.register(
      "oak_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_MODERN_DESK = BLOCKS.register(
      "oak_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_TABLE = BLOCKS.register(
      "oak_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_END_TABLE = BLOCKS.register(
      "oak_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_COFFEE_TABLE = BLOCKS.register(
      "oak_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_GLASS_TABLE = BLOCKS.register(
      "oak_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_CHAIR = BLOCKS.register(
      "oak_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_MODERN_CHAIR = BLOCKS.register(
      "oak_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_STRIPED_CHAIR = BLOCKS.register(
      "oak_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_STOOL_CHAIR = BLOCKS.register(
      "oak_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_COUNTER = BLOCKS.register(
      "oak_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_DRAWER_COUNTER = BLOCKS.register(
      "oak_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> OAK_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "oak_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> OAK_CUPBOARD_COUNTER = BLOCKS.register(
      "oak_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_WARDROBE = BLOCKS.register(
      "birch_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_MODERN_WARDROBE = BLOCKS.register(
      "birch_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_DOUBLE_WARDROBE = BLOCKS.register(
      "birch_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_BOOKSHELF = BLOCKS.register(
      "birch_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "birch_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_DRAWER = BLOCKS.register(
      "birch_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_DOUBLE_DRAWER = BLOCKS.register(
      "birch_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_BOOKSHELF_DRAWER = BLOCKS.register(
      "birch_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "birch_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_LARGE_DRAWER = BLOCKS.register(
      "birch_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "birch_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_TRIPLE_DRAWER = BLOCKS.register(
      "birch_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_DESK = BLOCKS.register(
      "birch_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_COVERED_DESK = BLOCKS.register(
      "birch_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_MODERN_DESK = BLOCKS.register(
      "birch_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_TABLE = BLOCKS.register(
      "birch_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_END_TABLE = BLOCKS.register(
      "birch_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_COFFEE_TABLE = BLOCKS.register(
      "birch_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_GLASS_TABLE = BLOCKS.register(
      "birch_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_CHAIR = BLOCKS.register(
      "birch_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_MODERN_CHAIR = BLOCKS.register(
      "birch_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_STRIPED_CHAIR = BLOCKS.register(
      "birch_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_STOOL_CHAIR = BLOCKS.register(
      "birch_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_COUNTER = BLOCKS.register(
      "birch_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_DRAWER_COUNTER = BLOCKS.register(
      "birch_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> BIRCH_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "birch_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> BIRCH_CUPBOARD_COUNTER = BLOCKS.register(
      "birch_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_WARDROBE = BLOCKS.register(
      "spruce_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_MODERN_WARDROBE = BLOCKS.register(
      "spruce_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_DOUBLE_WARDROBE = BLOCKS.register(
      "spruce_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_BOOKSHELF = BLOCKS.register(
      "spruce_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "spruce_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_DRAWER = BLOCKS.register(
      "spruce_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_DOUBLE_DRAWER = BLOCKS.register(
      "spruce_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_BOOKSHELF_DRAWER = BLOCKS.register(
      "spruce_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "spruce_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_LARGE_DRAWER = BLOCKS.register(
      "spruce_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "spruce_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_TRIPLE_DRAWER = BLOCKS.register(
      "spruce_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_DESK = BLOCKS.register(
      "spruce_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_COVERED_DESK = BLOCKS.register(
      "spruce_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_MODERN_DESK = BLOCKS.register(
      "spruce_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_TABLE = BLOCKS.register(
      "spruce_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_END_TABLE = BLOCKS.register(
      "spruce_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_COFFEE_TABLE = BLOCKS.register(
      "spruce_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_GLASS_TABLE = BLOCKS.register(
      "spruce_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_CHAIR = BLOCKS.register(
      "spruce_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_MODERN_CHAIR = BLOCKS.register(
      "spruce_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_STRIPED_CHAIR = BLOCKS.register(
      "spruce_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_STOOL_CHAIR = BLOCKS.register(
      "spruce_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_COUNTER = BLOCKS.register(
      "spruce_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_DRAWER_COUNTER = BLOCKS.register(
      "spruce_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "spruce_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_CUPBOARD_COUNTER = BLOCKS.register(
      "spruce_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_WARDROBE = BLOCKS.register(
      "jungle_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_MODERN_WARDROBE = BLOCKS.register(
      "jungle_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_DOUBLE_WARDROBE = BLOCKS.register(
      "jungle_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_BOOKSHELF = BLOCKS.register(
      "jungle_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "jungle_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_DRAWER = BLOCKS.register(
      "jungle_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_DOUBLE_DRAWER = BLOCKS.register(
      "jungle_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_BOOKSHELF_DRAWER = BLOCKS.register(
      "jungle_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "jungle_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_LARGE_DRAWER = BLOCKS.register(
      "jungle_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "jungle_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_TRIPLE_DRAWER = BLOCKS.register(
      "jungle_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_DESK = BLOCKS.register(
      "jungle_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_COVERED_DESK = BLOCKS.register(
      "jungle_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_MODERN_DESK = BLOCKS.register(
      "jungle_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_TABLE = BLOCKS.register(
      "jungle_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_END_TABLE = BLOCKS.register(
      "jungle_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_COFFEE_TABLE = BLOCKS.register(
      "jungle_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_GLASS_TABLE = BLOCKS.register(
      "jungle_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_CHAIR = BLOCKS.register(
      "jungle_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_MODERN_CHAIR = BLOCKS.register(
      "jungle_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_STRIPED_CHAIR = BLOCKS.register(
      "jungle_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_STOOL_CHAIR = BLOCKS.register(
      "jungle_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_COUNTER = BLOCKS.register(
      "jungle_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_DRAWER_COUNTER = BLOCKS.register(
      "jungle_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "jungle_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_CUPBOARD_COUNTER = BLOCKS.register(
      "jungle_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_WARDROBE = BLOCKS.register(
      "acacia_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_MODERN_WARDROBE = BLOCKS.register(
      "acacia_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_DOUBLE_WARDROBE = BLOCKS.register(
      "acacia_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_BOOKSHELF = BLOCKS.register(
      "acacia_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "acacia_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_DRAWER = BLOCKS.register(
      "acacia_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_DOUBLE_DRAWER = BLOCKS.register(
      "acacia_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_BOOKSHELF_DRAWER = BLOCKS.register(
      "acacia_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "acacia_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_LARGE_DRAWER = BLOCKS.register(
      "acacia_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "acacia_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_TRIPLE_DRAWER = BLOCKS.register(
      "acacia_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_DESK = BLOCKS.register(
      "acacia_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_COVERED_DESK = BLOCKS.register(
      "acacia_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_MODERN_DESK = BLOCKS.register(
      "acacia_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_TABLE = BLOCKS.register(
      "acacia_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_END_TABLE = BLOCKS.register(
      "acacia_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_COFFEE_TABLE = BLOCKS.register(
      "acacia_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_GLASS_TABLE = BLOCKS.register(
      "acacia_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_CHAIR = BLOCKS.register(
      "acacia_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_MODERN_CHAIR = BLOCKS.register(
      "acacia_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_STRIPED_CHAIR = BLOCKS.register(
      "acacia_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_STOOL_CHAIR = BLOCKS.register(
      "acacia_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_COUNTER = BLOCKS.register(
      "acacia_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_DRAWER_COUNTER = BLOCKS.register(
      "acacia_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> ACACIA_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "acacia_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> ACACIA_CUPBOARD_COUNTER = BLOCKS.register(
      "acacia_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_WARDROBE = BLOCKS.register(
      "dark_oak_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_MODERN_WARDROBE = BLOCKS.register(
      "dark_oak_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_DOUBLE_WARDROBE = BLOCKS.register(
      "dark_oak_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_BOOKSHELF = BLOCKS.register(
      "dark_oak_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "dark_oak_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_DRAWER = BLOCKS.register(
      "dark_oak_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_DOUBLE_DRAWER = BLOCKS.register(
      "dark_oak_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_BOOKSHELF_DRAWER = BLOCKS.register(
      "dark_oak_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "dark_oak_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_LARGE_DRAWER = BLOCKS.register(
      "dark_oak_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "dark_oak_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_TRIPLE_DRAWER = BLOCKS.register(
      "dark_oak_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_DESK = BLOCKS.register(
      "dark_oak_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_COVERED_DESK = BLOCKS.register(
      "dark_oak_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_MODERN_DESK = BLOCKS.register(
      "dark_oak_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_TABLE = BLOCKS.register(
      "dark_oak_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_END_TABLE = BLOCKS.register(
      "dark_oak_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_COFFEE_TABLE = BLOCKS.register(
      "dark_oak_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_GLASS_TABLE = BLOCKS.register(
      "dark_oak_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_CHAIR = BLOCKS.register(
      "dark_oak_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_MODERN_CHAIR = BLOCKS.register(
      "dark_oak_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_STRIPED_CHAIR = BLOCKS.register(
      "dark_oak_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_STOOL_CHAIR = BLOCKS.register(
      "dark_oak_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_COUNTER = BLOCKS.register(
      "dark_oak_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_DRAWER_COUNTER = BLOCKS.register(
      "dark_oak_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "dark_oak_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_CUPBOARD_COUNTER = BLOCKS.register(
      "dark_oak_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_WARDROBE = BLOCKS.register(
      "crimson_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_MODERN_WARDROBE = BLOCKS.register(
      "crimson_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_DOUBLE_WARDROBE = BLOCKS.register(
      "crimson_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_BOOKSHELF = BLOCKS.register(
      "crimson_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "crimson_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_DRAWER = BLOCKS.register(
      "crimson_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_DOUBLE_DRAWER = BLOCKS.register(
      "crimson_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_BOOKSHELF_DRAWER = BLOCKS.register(
      "crimson_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "crimson_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_LARGE_DRAWER = BLOCKS.register(
      "crimson_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "crimson_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_TRIPLE_DRAWER = BLOCKS.register(
      "crimson_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_DESK = BLOCKS.register(
      "crimson_desk", () -> new Desk(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_COVERED_DESK = BLOCKS.register(
      "crimson_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_MODERN_DESK = BLOCKS.register(
      "crimson_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_TABLE = BLOCKS.register(
      "crimson_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_END_TABLE = BLOCKS.register(
      "crimson_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_COFFEE_TABLE = BLOCKS.register(
      "crimson_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_GLASS_TABLE = BLOCKS.register(
      "crimson_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_CHAIR = BLOCKS.register(
      "crimson_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_MODERN_CHAIR = BLOCKS.register(
      "crimson_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_STRIPED_CHAIR = BLOCKS.register(
      "crimson_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_STOOL_CHAIR = BLOCKS.register(
      "crimson_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_COUNTER = BLOCKS.register(
      "crimson_counter", () -> new Counter(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_DRAWER_COUNTER = BLOCKS.register(
      "crimson_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "crimson_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_CUPBOARD_COUNTER = BLOCKS.register(
      "crimson_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_WARDROBE = BLOCKS.register(
      "warped_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_MODERN_WARDROBE = BLOCKS.register(
      "warped_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_DOUBLE_WARDROBE = BLOCKS.register(
      "warped_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_BOOKSHELF = BLOCKS.register(
      "warped_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "warped_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_DRAWER = BLOCKS.register(
      "warped_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_DOUBLE_DRAWER = BLOCKS.register(
      "warped_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_BOOKSHELF_DRAWER = BLOCKS.register(
      "warped_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "warped_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_LARGE_DRAWER = BLOCKS.register(
      "warped_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "warped_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_TRIPLE_DRAWER = BLOCKS.register(
      "warped_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_DESK = BLOCKS.register(
      "warped_desk", () -> new Desk(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_COVERED_DESK = BLOCKS.register(
      "warped_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_MODERN_DESK = BLOCKS.register(
      "warped_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_TABLE = BLOCKS.register(
      "warped_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_END_TABLE = BLOCKS.register(
      "warped_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_COFFEE_TABLE = BLOCKS.register(
      "warped_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_GLASS_TABLE = BLOCKS.register(
      "warped_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_CHAIR = BLOCKS.register(
      "warped_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_MODERN_CHAIR = BLOCKS.register(
      "warped_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_STRIPED_CHAIR = BLOCKS.register(
      "warped_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_STOOL_CHAIR = BLOCKS.register(
      "warped_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_COUNTER = BLOCKS.register(
      "warped_counter", () -> new Counter(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_DRAWER_COUNTER = BLOCKS.register(
      "warped_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> WARPED_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "warped_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> WARPED_CUPBOARD_COUNTER = BLOCKS.register(
      "warped_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_WARDROBE = BLOCKS.register(
      "mangrove_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_MODERN_WARDROBE = BLOCKS.register(
      "mangrove_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_DOUBLE_WARDROBE = BLOCKS.register(
      "mangrove_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_BOOKSHELF = BLOCKS.register(
      "mangrove_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "mangrove_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_DRAWER = BLOCKS.register(
      "mangrove_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_DOUBLE_DRAWER = BLOCKS.register(
      "mangrove_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_BOOKSHELF_DRAWER = BLOCKS.register(
      "mangrove_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "mangrove_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_LARGE_DRAWER = BLOCKS.register(
      "mangrove_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "mangrove_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_TRIPLE_DRAWER = BLOCKS.register(
      "mangrove_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_DESK = BLOCKS.register(
      "mangrove_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_COVERED_DESK = BLOCKS.register(
      "mangrove_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_MODERN_DESK = BLOCKS.register(
      "mangrove_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_TABLE = BLOCKS.register(
      "mangrove_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_END_TABLE = BLOCKS.register(
      "mangrove_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_COFFEE_TABLE = BLOCKS.register(
      "mangrove_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_GLASS_TABLE = BLOCKS.register(
      "mangrove_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_CHAIR = BLOCKS.register(
      "mangrove_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_MODERN_CHAIR = BLOCKS.register(
      "mangrove_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_STRIPED_CHAIR = BLOCKS.register(
      "mangrove_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_STOOL_CHAIR = BLOCKS.register(
      "mangrove_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_COUNTER = BLOCKS.register(
      "mangrove_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_DRAWER_COUNTER = BLOCKS.register(
      "mangrove_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "mangrove_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_CUPBOARD_COUNTER = BLOCKS.register(
      "mangrove_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_WARDROBE = BLOCKS.register(
      "stripped_oak_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_MODERN_WARDROBE = BLOCKS.register(
      "stripped_oak_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_oak_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_BOOKSHELF = BLOCKS.register(
      "stripped_oak_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_oak_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DRAWER = BLOCKS.register(
      "stripped_oak_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_oak_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_oak_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_oak_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_LARGE_DRAWER = BLOCKS.register(
      "stripped_oak_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_oak_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_oak_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DESK = BLOCKS.register(
      "stripped_oak_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_COVERED_DESK = BLOCKS.register(
      "stripped_oak_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_MODERN_DESK = BLOCKS.register(
      "stripped_oak_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_TABLE = BLOCKS.register(
      "stripped_oak_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_END_TABLE = BLOCKS.register(
      "stripped_oak_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_COFFEE_TABLE = BLOCKS.register(
      "stripped_oak_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_GLASS_TABLE = BLOCKS.register(
      "stripped_oak_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_CHAIR = BLOCKS.register(
      "stripped_oak_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_MODERN_CHAIR = BLOCKS.register(
      "stripped_oak_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_STRIPED_CHAIR = BLOCKS.register(
      "stripped_oak_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_STOOL_CHAIR = BLOCKS.register(
      "stripped_oak_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_COUNTER = BLOCKS.register(
      "stripped_oak_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DRAWER_COUNTER = BLOCKS.register(
      "stripped_oak_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_oak_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_oak_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_WARDROBE = BLOCKS.register(
      "stripped_birch_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_MODERN_WARDROBE = BLOCKS.register(
      "stripped_birch_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_birch_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_BOOKSHELF = BLOCKS.register(
      "stripped_birch_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_birch_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DRAWER = BLOCKS.register(
      "stripped_birch_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_birch_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_birch_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_birch_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_LARGE_DRAWER = BLOCKS.register(
      "stripped_birch_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_birch_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_birch_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DESK = BLOCKS.register(
      "stripped_birch_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_COVERED_DESK = BLOCKS.register(
      "stripped_birch_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_MODERN_DESK = BLOCKS.register(
      "stripped_birch_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_TABLE = BLOCKS.register(
      "stripped_birch_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_END_TABLE = BLOCKS.register(
      "stripped_birch_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_COFFEE_TABLE = BLOCKS.register(
      "stripped_birch_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_GLASS_TABLE = BLOCKS.register(
      "stripped_birch_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_CHAIR = BLOCKS.register(
      "stripped_birch_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_MODERN_CHAIR = BLOCKS.register(
      "stripped_birch_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_STRIPED_CHAIR = BLOCKS.register(
      "stripped_birch_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_STOOL_CHAIR = BLOCKS.register(
      "stripped_birch_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_COUNTER = BLOCKS.register(
      "stripped_birch_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DRAWER_COUNTER = BLOCKS.register(
      "stripped_birch_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_birch_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_birch_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_WARDROBE = BLOCKS.register(
      "stripped_spruce_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_MODERN_WARDROBE = BLOCKS.register(
      "stripped_spruce_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_spruce_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_BOOKSHELF = BLOCKS.register(
      "stripped_spruce_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_spruce_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DRAWER = BLOCKS.register(
      "stripped_spruce_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_spruce_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_spruce_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_spruce_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_LARGE_DRAWER = BLOCKS.register(
      "stripped_spruce_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_spruce_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_spruce_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DESK = BLOCKS.register(
      "stripped_spruce_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_COVERED_DESK = BLOCKS.register(
      "stripped_spruce_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_MODERN_DESK = BLOCKS.register(
      "stripped_spruce_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_TABLE = BLOCKS.register(
      "stripped_spruce_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_END_TABLE = BLOCKS.register(
      "stripped_spruce_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_COFFEE_TABLE = BLOCKS.register(
      "stripped_spruce_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_GLASS_TABLE = BLOCKS.register(
      "stripped_spruce_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_CHAIR = BLOCKS.register(
      "stripped_spruce_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_MODERN_CHAIR = BLOCKS.register(
      "stripped_spruce_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_STRIPED_CHAIR = BLOCKS.register(
      "stripped_spruce_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_STOOL_CHAIR = BLOCKS.register(
      "stripped_spruce_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_COUNTER = BLOCKS.register(
      "stripped_spruce_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_spruce_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_spruce_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_spruce_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_WARDROBE = BLOCKS.register(
      "stripped_jungle_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_MODERN_WARDROBE = BLOCKS.register(
      "stripped_jungle_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_jungle_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_BOOKSHELF = BLOCKS.register(
      "stripped_jungle_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_jungle_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DRAWER = BLOCKS.register(
      "stripped_jungle_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_jungle_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_jungle_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_jungle_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_LARGE_DRAWER = BLOCKS.register(
      "stripped_jungle_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_jungle_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_jungle_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DESK = BLOCKS.register(
      "stripped_jungle_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_COVERED_DESK = BLOCKS.register(
      "stripped_jungle_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_MODERN_DESK = BLOCKS.register(
      "stripped_jungle_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_TABLE = BLOCKS.register(
      "stripped_jungle_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_END_TABLE = BLOCKS.register(
      "stripped_jungle_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_COFFEE_TABLE = BLOCKS.register(
      "stripped_jungle_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_GLASS_TABLE = BLOCKS.register(
      "stripped_jungle_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_CHAIR = BLOCKS.register(
      "stripped_jungle_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_MODERN_CHAIR = BLOCKS.register(
      "stripped_jungle_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_STRIPED_CHAIR = BLOCKS.register(
      "stripped_jungle_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_STOOL_CHAIR = BLOCKS.register(
      "stripped_jungle_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_COUNTER = BLOCKS.register(
      "stripped_jungle_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_jungle_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_jungle_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_jungle_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_WARDROBE = BLOCKS.register(
      "stripped_acacia_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_MODERN_WARDROBE = BLOCKS.register(
      "stripped_acacia_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_acacia_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_BOOKSHELF = BLOCKS.register(
      "stripped_acacia_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_acacia_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DRAWER = BLOCKS.register(
      "stripped_acacia_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_acacia_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_acacia_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_acacia_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_LARGE_DRAWER = BLOCKS.register(
      "stripped_acacia_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_acacia_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_acacia_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DESK = BLOCKS.register(
      "stripped_acacia_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_COVERED_DESK = BLOCKS.register(
      "stripped_acacia_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_MODERN_DESK = BLOCKS.register(
      "stripped_acacia_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_TABLE = BLOCKS.register(
      "stripped_acacia_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_END_TABLE = BLOCKS.register(
      "stripped_acacia_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_COFFEE_TABLE = BLOCKS.register(
      "stripped_acacia_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_GLASS_TABLE = BLOCKS.register(
      "stripped_acacia_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_CHAIR = BLOCKS.register(
      "stripped_acacia_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_MODERN_CHAIR = BLOCKS.register(
      "stripped_acacia_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_STRIPED_CHAIR = BLOCKS.register(
      "stripped_acacia_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_STOOL_CHAIR = BLOCKS.register(
      "stripped_acacia_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_COUNTER = BLOCKS.register(
      "stripped_acacia_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DRAWER_COUNTER = BLOCKS.register(
      "stripped_acacia_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_acacia_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_acacia_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_WARDROBE = BLOCKS.register(
      "stripped_dark_oak_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_MODERN_WARDROBE = BLOCKS.register(
      "stripped_dark_oak_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_dark_oak_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_BOOKSHELF = BLOCKS.register(
      "stripped_dark_oak_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_dark_oak_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DRAWER = BLOCKS.register(
      "stripped_dark_oak_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_dark_oak_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_dark_oak_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_dark_oak_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_LARGE_DRAWER = BLOCKS.register(
      "stripped_dark_oak_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_dark_oak_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_dark_oak_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DESK = BLOCKS.register(
      "stripped_dark_oak_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_COVERED_DESK = BLOCKS.register(
      "stripped_dark_oak_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_MODERN_DESK = BLOCKS.register(
      "stripped_dark_oak_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_TABLE = BLOCKS.register(
      "stripped_dark_oak_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_END_TABLE = BLOCKS.register(
      "stripped_dark_oak_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_COFFEE_TABLE = BLOCKS.register(
      "stripped_dark_oak_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_GLASS_TABLE = BLOCKS.register(
      "stripped_dark_oak_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_CHAIR = BLOCKS.register(
      "stripped_dark_oak_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_MODERN_CHAIR = BLOCKS.register(
      "stripped_dark_oak_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_STRIPED_CHAIR = BLOCKS.register(
      "stripped_dark_oak_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_STOOL_CHAIR = BLOCKS.register(
      "stripped_dark_oak_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_COUNTER = BLOCKS.register(
      "stripped_dark_oak_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DRAWER_COUNTER = BLOCKS.register(
      "stripped_dark_oak_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_dark_oak_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_dark_oak_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_WARDROBE = BLOCKS.register(
      "stripped_crimson_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_MODERN_WARDROBE = BLOCKS.register(
      "stripped_crimson_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_crimson_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_BOOKSHELF = BLOCKS.register(
      "stripped_crimson_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_crimson_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DRAWER = BLOCKS.register(
      "stripped_crimson_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_crimson_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_crimson_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_crimson_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_LARGE_DRAWER = BLOCKS.register(
      "stripped_crimson_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_crimson_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_crimson_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DESK = BLOCKS.register(
      "stripped_crimson_desk", () -> new Desk(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_COVERED_DESK = BLOCKS.register(
      "stripped_crimson_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_MODERN_DESK = BLOCKS.register(
      "stripped_crimson_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_TABLE = BLOCKS.register(
      "stripped_crimson_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_END_TABLE = BLOCKS.register(
      "stripped_crimson_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_COFFEE_TABLE = BLOCKS.register(
      "stripped_crimson_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_GLASS_TABLE = BLOCKS.register(
      "stripped_crimson_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_CHAIR = BLOCKS.register(
      "stripped_crimson_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_MODERN_CHAIR = BLOCKS.register(
      "stripped_crimson_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_STRIPED_CHAIR = BLOCKS.register(
      "stripped_crimson_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_STOOL_CHAIR = BLOCKS.register(
      "stripped_crimson_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_COUNTER = BLOCKS.register(
      "stripped_crimson_counter", () -> new Counter(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DRAWER_COUNTER = BLOCKS.register(
      "stripped_crimson_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_crimson_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_crimson_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_WARDROBE = BLOCKS.register(
      "stripped_warped_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_MODERN_WARDROBE = BLOCKS.register(
      "stripped_warped_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_warped_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_BOOKSHELF = BLOCKS.register(
      "stripped_warped_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_warped_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DRAWER = BLOCKS.register(
      "stripped_warped_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_warped_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_warped_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_warped_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_LARGE_DRAWER = BLOCKS.register(
      "stripped_warped_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_warped_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_warped_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DESK = BLOCKS.register(
      "stripped_warped_desk", () -> new Desk(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_COVERED_DESK = BLOCKS.register(
      "stripped_warped_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_MODERN_DESK = BLOCKS.register(
      "stripped_warped_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_TABLE = BLOCKS.register(
      "stripped_warped_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_END_TABLE = BLOCKS.register(
      "stripped_warped_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_COFFEE_TABLE = BLOCKS.register(
      "stripped_warped_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_GLASS_TABLE = BLOCKS.register(
      "stripped_warped_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_CHAIR = BLOCKS.register(
      "stripped_warped_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_MODERN_CHAIR = BLOCKS.register(
      "stripped_warped_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_STRIPED_CHAIR = BLOCKS.register(
      "stripped_warped_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_STOOL_CHAIR = BLOCKS.register(
      "stripped_warped_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_COUNTER = BLOCKS.register(
      "stripped_warped_counter", () -> new Counter(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DRAWER_COUNTER = BLOCKS.register(
      "stripped_warped_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_warped_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_warped_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_WARDROBE = BLOCKS.register(
      "stripped_mangrove_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_MODERN_WARDROBE = BLOCKS.register(
      "stripped_mangrove_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_mangrove_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_BOOKSHELF = BLOCKS.register(
      "stripped_mangrove_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_mangrove_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DRAWER = BLOCKS.register(
      "stripped_mangrove_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_mangrove_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_mangrove_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_mangrove_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_LARGE_DRAWER = BLOCKS.register(
      "stripped_mangrove_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_mangrove_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_mangrove_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DESK = BLOCKS.register(
      "stripped_mangrove_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_COVERED_DESK = BLOCKS.register(
      "stripped_mangrove_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_MODERN_DESK = BLOCKS.register(
      "stripped_mangrove_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_TABLE = BLOCKS.register(
      "stripped_mangrove_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_END_TABLE = BLOCKS.register(
      "stripped_mangrove_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_COFFEE_TABLE = BLOCKS.register(
      "stripped_mangrove_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_GLASS_TABLE = BLOCKS.register(
      "stripped_mangrove_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_CHAIR = BLOCKS.register(
      "stripped_mangrove_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_MODERN_CHAIR = BLOCKS.register(
      "stripped_mangrove_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_STRIPED_CHAIR = BLOCKS.register(
      "stripped_mangrove_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_STOOL_CHAIR = BLOCKS.register(
      "stripped_mangrove_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_COUNTER = BLOCKS.register(
      "stripped_mangrove_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_mangrove_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_mangrove_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_mangrove_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_WARDROBE = BLOCKS.register(
      "cherry_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_MODERN_WARDROBE = BLOCKS.register(
      "cherry_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_DOUBLE_WARDROBE = BLOCKS.register(
      "cherry_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_BOOKSHELF = BLOCKS.register(
      "cherry_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "cherry_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_DRAWER = BLOCKS.register(
      "cherry_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_DOUBLE_DRAWER = BLOCKS.register(
      "cherry_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_BOOKSHELF_DRAWER = BLOCKS.register(
      "cherry_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "cherry_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_LARGE_DRAWER = BLOCKS.register(
      "cherry_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "cherry_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_TRIPLE_DRAWER = BLOCKS.register(
      "cherry_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_DESK = BLOCKS.register(
      "cherry_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_COVERED_DESK = BLOCKS.register(
      "cherry_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_MODERN_DESK = BLOCKS.register(
      "cherry_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_TABLE = BLOCKS.register(
      "cherry_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_END_TABLE = BLOCKS.register(
      "cherry_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_COFFEE_TABLE = BLOCKS.register(
      "cherry_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_GLASS_TABLE = BLOCKS.register(
      "cherry_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_CHAIR = BLOCKS.register(
      "cherry_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_MODERN_CHAIR = BLOCKS.register(
      "cherry_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_STRIPED_CHAIR = BLOCKS.register(
      "cherry_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_STOOL_CHAIR = BLOCKS.register(
      "cherry_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_COUNTER = BLOCKS.register(
      "cherry_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_DRAWER_COUNTER = BLOCKS.register(
      "cherry_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CHERRY_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "cherry_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CHERRY_CUPBOARD_COUNTER = BLOCKS.register(
      "cherry_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_WARDROBE = BLOCKS.register(
      "stripped_cherry_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_MODERN_WARDROBE = BLOCKS.register(
      "stripped_cherry_modern_wardrobe", () -> new TallFurnitureHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DOUBLE_WARDROBE = BLOCKS.register(
      "stripped_cherry_double_wardrobe", () -> new TallFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_BOOKSHELF = BLOCKS.register(
      "stripped_cherry_bookshelf", () -> new BookCabinet(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_BOOKSHELF_CUPBOARD = BLOCKS.register(
      "stripped_cherry_bookshelf_cupboard", () -> new BookCabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DRAWER = BLOCKS.register(
      "stripped_cherry_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DOUBLE_DRAWER = BLOCKS.register(
      "stripped_cherry_double_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_cherry_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_LOWER_BOOKSHELF_DRAWER = BLOCKS.register(
      "stripped_cherry_lower_bookshelf_drawer", () -> new BookDrawer(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_LARGE_DRAWER = BLOCKS.register(
      "stripped_cherry_large_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_LOWER_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_cherry_lower_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_TRIPLE_DRAWER = BLOCKS.register(
      "stripped_cherry_triple_drawer", () -> new WideFurniture(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DESK = BLOCKS.register(
      "stripped_cherry_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_COVERED_DESK = BLOCKS.register(
      "stripped_cherry_covered_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_MODERN_DESK = BLOCKS.register(
      "stripped_cherry_modern_desk", () -> new Desk(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_TABLE = BLOCKS.register(
      "stripped_cherry_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_END_TABLE = BLOCKS.register(
      "stripped_cherry_end_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_COFFEE_TABLE = BLOCKS.register(
      "stripped_cherry_coffee_table", () -> new Table(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_GLASS_TABLE = BLOCKS.register(
      "stripped_cherry_glass_table", () -> new TableHitbox(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_CHAIR = BLOCKS.register(
      "stripped_cherry_chair", () -> new ClassicChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_MODERN_CHAIR = BLOCKS.register(
      "stripped_cherry_modern_chair", () -> new ModernChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_STRIPED_CHAIR = BLOCKS.register(
      "stripped_cherry_striped_chair", () -> new StripedChair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_STOOL_CHAIR = BLOCKS.register(
      "stripped_cherry_stool_chair", () -> new Chair(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_COUNTER = BLOCKS.register(
      "stripped_cherry_counter", () -> new Counter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DRAWER_COUNTER = BLOCKS.register(
      "stripped_cherry_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DOUBLE_DRAWER_COUNTER = BLOCKS.register(
      "stripped_cherry_double_drawer_counter",
      () -> new StorageCounter(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_CUPBOARD_COUNTER = BLOCKS.register(
      "stripped_cherry_cupboard_counter", () -> new CupboardCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_KITCHEN_CABINET = BLOCKS.register(
      "oak_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "oak_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "oak_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_KITCHEN_CABINET = BLOCKS.register(
      "stripped_oak_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_oak_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_oak_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_KITCHEN_CABINET = BLOCKS.register(
      "birch_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "birch_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "birch_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_KITCHEN_CABINET = BLOCKS.register(
      "stripped_birch_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_birch_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_birch_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_KITCHEN_CABINET = BLOCKS.register(
      "spruce_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "spruce_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "spruce_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_spruce_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_spruce_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_spruce_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_KITCHEN_CABINET = BLOCKS.register(
      "jungle_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "jungle_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "jungle_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_jungle_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_jungle_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_jungle_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_KITCHEN_CABINET = BLOCKS.register(
      "acacia_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "acacia_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "acacia_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_KITCHEN_CABINET = BLOCKS.register(
      "stripped_acacia_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_acacia_double_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_acacia_glass_kitchen_cabinet",
      () -> new Cabinet(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_KITCHEN_CABINET = BLOCKS.register(
      "dark_oak_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "dark_oak_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "dark_oak_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_KITCHEN_CABINET = BLOCKS.register(
      "stripped_dark_oak_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_dark_oak_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_dark_oak_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> WARPED_KITCHEN_CABINET = BLOCKS.register(
      "warped_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "warped_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> WARPED_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "warped_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_KITCHEN_CABINET = BLOCKS.register(
      "stripped_warped_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_warped_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_warped_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_KITCHEN_CABINET = BLOCKS.register(
      "crimson_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "crimson_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "crimson_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_KITCHEN_CABINET = BLOCKS.register(
      "stripped_crimson_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_crimson_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_crimson_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_KITCHEN_CABINET = BLOCKS.register(
      "mangrove_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "mangrove_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "mangrove_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_mangrove_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_mangrove_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_mangrove_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CHERRY_KITCHEN_CABINET = BLOCKS.register(
      "cherry_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "cherry_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> CHERRY_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "cherry_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_KITCHEN_CABINET = BLOCKS.register(
      "stripped_cherry_kitchen_cabinet", () -> new CabinetHinge(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_DOUBLE_KITCHEN_CABINET = BLOCKS.register(
      "stripped_cherry_double_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_GLASS_KITCHEN_CABINET = BLOCKS.register(
      "stripped_cherry_glass_kitchen_cabinet",
      () -> new Cabinet(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(), Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F)
      )
   );
   public static final DeferredBlock<Block> OAK_KITCHEN_SINK = BLOCKS.register(
      "oak_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_OAK_KITCHEN_SINK = BLOCKS.register(
      "stripped_oak_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_KITCHEN_SINK = BLOCKS.register(
      "birch_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_BIRCH_KITCHEN_SINK = BLOCKS.register(
      "stripped_birch_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_KITCHEN_SINK = BLOCKS.register(
      "spruce_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_SPRUCE_KITCHEN_SINK = BLOCKS.register(
      "stripped_spruce_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_KITCHEN_SINK = BLOCKS.register(
      "jungle_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_JUNGLE_KITCHEN_SINK = BLOCKS.register(
      "stripped_jungle_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_KITCHEN_SINK = BLOCKS.register(
      "acacia_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_ACACIA_KITCHEN_SINK = BLOCKS.register(
      "stripped_acacia_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_KITCHEN_SINK = BLOCKS.register(
      "dark_oak_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_DARK_OAK_KITCHEN_SINK = BLOCKS.register(
      "stripped_dark_oak_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_KITCHEN_SINK = BLOCKS.register(
      "crimson_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.CRIMSON_NYLIUM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CRIMSON_KITCHEN_SINK = BLOCKS.register(
      "stripped_crimson_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.CRIMSON_NYLIUM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_KITCHEN_SINK = BLOCKS.register(
      "warped_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WARPED_NYLIUM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_WARPED_KITCHEN_SINK = BLOCKS.register(
      "stripped_warped_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WARPED_NYLIUM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_KITCHEN_SINK = BLOCKS.register(
      "mangrove_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_MANGROVE_KITCHEN_SINK = BLOCKS.register(
      "stripped_mangrove_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_KITCHEN_SINK = BLOCKS.register(
      "cherry_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> STRIPPED_CHERRY_KITCHEN_SINK = BLOCKS.register(
      "stripped_cherry_kitchen_sink", () -> new SinkCounter(Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WHITE_COUCH = BLOCKS.register(
      "white_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.WOOL).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WHITE_CHAISE = BLOCKS.register(
      "white_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.WOOL).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_COUCH = BLOCKS.register(
      "light_gray_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CHAISE = BLOCKS.register(
      "light_gray_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> GRAY_COUCH = BLOCKS.register(
      "gray_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_GRAY).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> GRAY_CHAISE = BLOCKS.register(
      "gray_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_GRAY).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BLACK_COUCH = BLOCKS.register(
      "black_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_BLACK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BLACK_CHAISE = BLOCKS.register(
      "black_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_BLACK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BROWN_COUCH = BLOCKS.register(
      "brown_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_BROWN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BROWN_CHAISE = BLOCKS.register(
      "brown_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_BROWN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> RED_COUCH = BLOCKS.register(
      "red_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_RED).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> RED_CHAISE = BLOCKS.register(
      "red_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_RED).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ORANGE_COUCH = BLOCKS.register(
      "orange_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_ORANGE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ORANGE_CHAISE = BLOCKS.register(
      "orange_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_ORANGE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> YELLOW_COUCH = BLOCKS.register(
      "yellow_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> YELLOW_CHAISE = BLOCKS.register(
      "yellow_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> LIME_COUCH = BLOCKS.register(
      "lime_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_LIGHT_GREEN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> LIME_CHAISE = BLOCKS.register(
      "lime_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_LIGHT_GREEN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> GREEN_COUCH = BLOCKS.register(
      "green_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_GREEN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> GREEN_CHAISE = BLOCKS.register(
      "green_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_GREEN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CYAN_COUCH = BLOCKS.register(
      "cyan_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_CYAN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CYAN_CHAISE = BLOCKS.register(
      "cyan_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_CYAN).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_COUCH = BLOCKS.register(
      "light_blue_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CHAISE = BLOCKS.register(
      "light_blue_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BLUE_COUCH = BLOCKS.register(
      "blue_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_BLUE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BLUE_CHAISE = BLOCKS.register(
      "blue_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_BLUE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> PURPLE_COUCH = BLOCKS.register(
      "purple_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_PURPLE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> PURPLE_CHAISE = BLOCKS.register(
      "purple_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_PURPLE).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MAGENTA_COUCH = BLOCKS.register(
      "magenta_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_MAGENTA).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MAGENTA_CHAISE = BLOCKS.register(
      "magenta_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_MAGENTA).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> PINK_COUCH = BLOCKS.register(
      "pink_couch", () -> new Couch(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> PINK_CHAISE = BLOCKS.register(
      "pink_chaise", () -> new Chaise(Properties.of().noOcclusion().sound(SoundType.WOOL).mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F))
   );
}
