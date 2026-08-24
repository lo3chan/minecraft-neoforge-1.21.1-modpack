package com.mcwfurnitures.kikoz.init;

import com.mcwfurnitures.kikoz.objects.CraftingItem;
import com.mcwfurnitures.kikoz.objects.ItemBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwfurnitures");
   public static final DeferredItem<Item> CABINET_DOOR = ITEMS.register("cabinet_door", () -> new CraftingItem(new Properties()));
   public static final DeferredItem<Item> CABINET_DRAWER = ITEMS.register("cabinet_drawer", () -> new CraftingItem(new Properties()));
   public static final DeferredItem<Item> OAK_WARDROBE = ITEMS.register(
      "oak_wardrobe", () -> new ItemBlock((Block)BlockInit.OAK_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_MODERN_WARDROBE = ITEMS.register(
      "oak_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.OAK_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_DOUBLE_WARDROBE = ITEMS.register(
      "oak_double_wardrobe", () -> new ItemBlock((Block)BlockInit.OAK_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BOOKSHELF = ITEMS.register(
      "oak_bookshelf", () -> new ItemBlock((Block)BlockInit.OAK_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BOOKSHELF_CUPBOARD = ITEMS.register(
      "oak_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.OAK_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_DRAWER = ITEMS.register("oak_drawer", () -> new ItemBlock((Block)BlockInit.OAK_DRAWER.get(), new Properties()));
   public static final DeferredItem<Item> OAK_DOUBLE_DRAWER = ITEMS.register(
      "oak_double_drawer", () -> new ItemBlock((Block)BlockInit.OAK_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BOOKSHELF_DRAWER = ITEMS.register(
      "oak_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.OAK_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "oak_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.OAK_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LARGE_DRAWER = ITEMS.register(
      "oak_large_drawer", () -> new ItemBlock((Block)BlockInit.OAK_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "oak_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.OAK_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_TRIPLE_DRAWER = ITEMS.register(
      "oak_triple_drawer", () -> new ItemBlock((Block)BlockInit.OAK_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_DESK = ITEMS.register("oak_desk", () -> new ItemBlock((Block)BlockInit.OAK_DESK.get(), new Properties()));
   public static final DeferredItem<Item> OAK_COVERED_DESK = ITEMS.register(
      "oak_covered_desk", () -> new ItemBlock((Block)BlockInit.OAK_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_MODERN_DESK = ITEMS.register(
      "oak_modern_desk", () -> new ItemBlock((Block)BlockInit.OAK_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_TABLE = ITEMS.register("oak_table", () -> new ItemBlock((Block)BlockInit.OAK_TABLE.get(), new Properties()));
   public static final DeferredItem<Item> OAK_END_TABLE = ITEMS.register(
      "oak_end_table", () -> new ItemBlock((Block)BlockInit.OAK_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_COFFEE_TABLE = ITEMS.register(
      "oak_coffee_table", () -> new ItemBlock((Block)BlockInit.OAK_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_GLASS_TABLE = ITEMS.register(
      "oak_glass_table", () -> new ItemBlock((Block)BlockInit.OAK_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_CHAIR = ITEMS.register("oak_chair", () -> new ItemBlock((Block)BlockInit.OAK_CHAIR.get(), new Properties()));
   public static final DeferredItem<Item> OAK_MODERN_CHAIR = ITEMS.register(
      "oak_modern_chair", () -> new ItemBlock((Block)BlockInit.OAK_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_STRIPED_CHAIR = ITEMS.register(
      "oak_striped_chair", () -> new ItemBlock((Block)BlockInit.OAK_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_STOOL_CHAIR = ITEMS.register(
      "oak_stool_chair", () -> new ItemBlock((Block)BlockInit.OAK_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_COUNTER = ITEMS.register("oak_counter", () -> new ItemBlock((Block)BlockInit.OAK_COUNTER.get(), new Properties()));
   public static final DeferredItem<Item> OAK_DRAWER_COUNTER = ITEMS.register(
      "oak_drawer_counter", () -> new ItemBlock((Block)BlockInit.OAK_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "oak_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.OAK_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_CUPBOARD_COUNTER = ITEMS.register(
      "oak_cupboard_counter", () -> new ItemBlock((Block)BlockInit.OAK_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WARDROBE = ITEMS.register(
      "birch_wardrobe", () -> new ItemBlock((Block)BlockInit.BIRCH_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_MODERN_WARDROBE = ITEMS.register(
      "birch_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.BIRCH_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DOUBLE_WARDROBE = ITEMS.register(
      "birch_double_wardrobe", () -> new ItemBlock((Block)BlockInit.BIRCH_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BOOKSHELF = ITEMS.register(
      "birch_bookshelf", () -> new ItemBlock((Block)BlockInit.BIRCH_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BOOKSHELF_CUPBOARD = ITEMS.register(
      "birch_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.BIRCH_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DRAWER = ITEMS.register(
      "birch_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DOUBLE_DRAWER = ITEMS.register(
      "birch_double_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BOOKSHELF_DRAWER = ITEMS.register(
      "birch_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "birch_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LARGE_DRAWER = ITEMS.register(
      "birch_large_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "birch_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_TRIPLE_DRAWER = ITEMS.register(
      "birch_triple_drawer", () -> new ItemBlock((Block)BlockInit.BIRCH_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DESK = ITEMS.register("birch_desk", () -> new ItemBlock((Block)BlockInit.BIRCH_DESK.get(), new Properties()));
   public static final DeferredItem<Item> BIRCH_COVERED_DESK = ITEMS.register(
      "birch_covered_desk", () -> new ItemBlock((Block)BlockInit.BIRCH_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_MODERN_DESK = ITEMS.register(
      "birch_modern_desk", () -> new ItemBlock((Block)BlockInit.BIRCH_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_TABLE = ITEMS.register("birch_table", () -> new ItemBlock((Block)BlockInit.BIRCH_TABLE.get(), new Properties()));
   public static final DeferredItem<Item> BIRCH_END_TABLE = ITEMS.register(
      "birch_end_table", () -> new ItemBlock((Block)BlockInit.BIRCH_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_COFFEE_TABLE = ITEMS.register(
      "birch_coffee_table", () -> new ItemBlock((Block)BlockInit.BIRCH_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_GLASS_TABLE = ITEMS.register(
      "birch_glass_table", () -> new ItemBlock((Block)BlockInit.BIRCH_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_CHAIR = ITEMS.register("birch_chair", () -> new ItemBlock((Block)BlockInit.BIRCH_CHAIR.get(), new Properties()));
   public static final DeferredItem<Item> BIRCH_MODERN_CHAIR = ITEMS.register(
      "birch_modern_chair", () -> new ItemBlock((Block)BlockInit.BIRCH_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_STRIPED_CHAIR = ITEMS.register(
      "birch_striped_chair", () -> new ItemBlock((Block)BlockInit.BIRCH_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_STOOL_CHAIR = ITEMS.register(
      "birch_stool_chair", () -> new ItemBlock((Block)BlockInit.BIRCH_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_COUNTER = ITEMS.register(
      "birch_counter", () -> new ItemBlock((Block)BlockInit.BIRCH_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DRAWER_COUNTER = ITEMS.register(
      "birch_drawer_counter", () -> new ItemBlock((Block)BlockInit.BIRCH_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "birch_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.BIRCH_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_CUPBOARD_COUNTER = ITEMS.register(
      "birch_cupboard_counter", () -> new ItemBlock((Block)BlockInit.BIRCH_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WARDROBE = ITEMS.register(
      "spruce_wardrobe", () -> new ItemBlock((Block)BlockInit.SPRUCE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_MODERN_WARDROBE = ITEMS.register(
      "spruce_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.SPRUCE_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DOUBLE_WARDROBE = ITEMS.register(
      "spruce_double_wardrobe", () -> new ItemBlock((Block)BlockInit.SPRUCE_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BOOKSHELF = ITEMS.register(
      "spruce_bookshelf", () -> new ItemBlock((Block)BlockInit.SPRUCE_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BOOKSHELF_CUPBOARD = ITEMS.register(
      "spruce_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.SPRUCE_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DRAWER = ITEMS.register(
      "spruce_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DOUBLE_DRAWER = ITEMS.register(
      "spruce_double_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BOOKSHELF_DRAWER = ITEMS.register(
      "spruce_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "spruce_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LARGE_DRAWER = ITEMS.register(
      "spruce_large_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "spruce_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_TRIPLE_DRAWER = ITEMS.register(
      "spruce_triple_drawer", () -> new ItemBlock((Block)BlockInit.SPRUCE_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DESK = ITEMS.register("spruce_desk", () -> new ItemBlock((Block)BlockInit.SPRUCE_DESK.get(), new Properties()));
   public static final DeferredItem<Item> SPRUCE_COVERED_DESK = ITEMS.register(
      "spruce_covered_desk", () -> new ItemBlock((Block)BlockInit.SPRUCE_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_MODERN_DESK = ITEMS.register(
      "spruce_modern_desk", () -> new ItemBlock((Block)BlockInit.SPRUCE_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_TABLE = ITEMS.register(
      "spruce_table", () -> new ItemBlock((Block)BlockInit.SPRUCE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_END_TABLE = ITEMS.register(
      "spruce_end_table", () -> new ItemBlock((Block)BlockInit.SPRUCE_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_COFFEE_TABLE = ITEMS.register(
      "spruce_coffee_table", () -> new ItemBlock((Block)BlockInit.SPRUCE_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_GLASS_TABLE = ITEMS.register(
      "spruce_glass_table", () -> new ItemBlock((Block)BlockInit.SPRUCE_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_CHAIR = ITEMS.register(
      "spruce_chair", () -> new ItemBlock((Block)BlockInit.SPRUCE_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_MODERN_CHAIR = ITEMS.register(
      "spruce_modern_chair", () -> new ItemBlock((Block)BlockInit.SPRUCE_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_STRIPED_CHAIR = ITEMS.register(
      "spruce_striped_chair", () -> new ItemBlock((Block)BlockInit.SPRUCE_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_STOOL_CHAIR = ITEMS.register(
      "spruce_stool_chair", () -> new ItemBlock((Block)BlockInit.SPRUCE_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_COUNTER = ITEMS.register(
      "spruce_counter", () -> new ItemBlock((Block)BlockInit.SPRUCE_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DRAWER_COUNTER = ITEMS.register(
      "spruce_drawer_counter", () -> new ItemBlock((Block)BlockInit.SPRUCE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "spruce_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.SPRUCE_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_CUPBOARD_COUNTER = ITEMS.register(
      "spruce_cupboard_counter", () -> new ItemBlock((Block)BlockInit.SPRUCE_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WARDROBE = ITEMS.register(
      "jungle_wardrobe", () -> new ItemBlock((Block)BlockInit.JUNGLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_MODERN_WARDROBE = ITEMS.register(
      "jungle_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.JUNGLE_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DOUBLE_WARDROBE = ITEMS.register(
      "jungle_double_wardrobe", () -> new ItemBlock((Block)BlockInit.JUNGLE_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BOOKSHELF = ITEMS.register(
      "jungle_bookshelf", () -> new ItemBlock((Block)BlockInit.JUNGLE_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BOOKSHELF_CUPBOARD = ITEMS.register(
      "jungle_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.JUNGLE_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DRAWER = ITEMS.register(
      "jungle_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DOUBLE_DRAWER = ITEMS.register(
      "jungle_double_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BOOKSHELF_DRAWER = ITEMS.register(
      "jungle_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "jungle_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LARGE_DRAWER = ITEMS.register(
      "jungle_large_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "jungle_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_TRIPLE_DRAWER = ITEMS.register(
      "jungle_triple_drawer", () -> new ItemBlock((Block)BlockInit.JUNGLE_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DESK = ITEMS.register("jungle_desk", () -> new ItemBlock((Block)BlockInit.JUNGLE_DESK.get(), new Properties()));
   public static final DeferredItem<Item> JUNGLE_COVERED_DESK = ITEMS.register(
      "jungle_covered_desk", () -> new ItemBlock((Block)BlockInit.JUNGLE_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_MODERN_DESK = ITEMS.register(
      "jungle_modern_desk", () -> new ItemBlock((Block)BlockInit.JUNGLE_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_TABLE = ITEMS.register(
      "jungle_table", () -> new ItemBlock((Block)BlockInit.JUNGLE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_END_TABLE = ITEMS.register(
      "jungle_end_table", () -> new ItemBlock((Block)BlockInit.JUNGLE_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_COFFEE_TABLE = ITEMS.register(
      "jungle_coffee_table", () -> new ItemBlock((Block)BlockInit.JUNGLE_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_GLASS_TABLE = ITEMS.register(
      "jungle_glass_table", () -> new ItemBlock((Block)BlockInit.JUNGLE_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_CHAIR = ITEMS.register(
      "jungle_chair", () -> new ItemBlock((Block)BlockInit.JUNGLE_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_MODERN_CHAIR = ITEMS.register(
      "jungle_modern_chair", () -> new ItemBlock((Block)BlockInit.JUNGLE_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_STRIPED_CHAIR = ITEMS.register(
      "jungle_striped_chair", () -> new ItemBlock((Block)BlockInit.JUNGLE_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_STOOL_CHAIR = ITEMS.register(
      "jungle_stool_chair", () -> new ItemBlock((Block)BlockInit.JUNGLE_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_COUNTER = ITEMS.register(
      "jungle_counter", () -> new ItemBlock((Block)BlockInit.JUNGLE_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DRAWER_COUNTER = ITEMS.register(
      "jungle_drawer_counter", () -> new ItemBlock((Block)BlockInit.JUNGLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "jungle_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.JUNGLE_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_CUPBOARD_COUNTER = ITEMS.register(
      "jungle_cupboard_counter", () -> new ItemBlock((Block)BlockInit.JUNGLE_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WARDROBE = ITEMS.register(
      "acacia_wardrobe", () -> new ItemBlock((Block)BlockInit.ACACIA_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_MODERN_WARDROBE = ITEMS.register(
      "acacia_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.ACACIA_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DOUBLE_WARDROBE = ITEMS.register(
      "acacia_double_wardrobe", () -> new ItemBlock((Block)BlockInit.ACACIA_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BOOKSHELF = ITEMS.register(
      "acacia_bookshelf", () -> new ItemBlock((Block)BlockInit.ACACIA_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BOOKSHELF_CUPBOARD = ITEMS.register(
      "acacia_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.ACACIA_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DRAWER = ITEMS.register(
      "acacia_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DOUBLE_DRAWER = ITEMS.register(
      "acacia_double_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BOOKSHELF_DRAWER = ITEMS.register(
      "acacia_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "acacia_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LARGE_DRAWER = ITEMS.register(
      "acacia_large_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "acacia_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_TRIPLE_DRAWER = ITEMS.register(
      "acacia_triple_drawer", () -> new ItemBlock((Block)BlockInit.ACACIA_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DESK = ITEMS.register("acacia_desk", () -> new ItemBlock((Block)BlockInit.ACACIA_DESK.get(), new Properties()));
   public static final DeferredItem<Item> ACACIA_COVERED_DESK = ITEMS.register(
      "acacia_covered_desk", () -> new ItemBlock((Block)BlockInit.ACACIA_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_MODERN_DESK = ITEMS.register(
      "acacia_modern_desk", () -> new ItemBlock((Block)BlockInit.ACACIA_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_TABLE = ITEMS.register(
      "acacia_table", () -> new ItemBlock((Block)BlockInit.ACACIA_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_END_TABLE = ITEMS.register(
      "acacia_end_table", () -> new ItemBlock((Block)BlockInit.ACACIA_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_COFFEE_TABLE = ITEMS.register(
      "acacia_coffee_table", () -> new ItemBlock((Block)BlockInit.ACACIA_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_GLASS_TABLE = ITEMS.register(
      "acacia_glass_table", () -> new ItemBlock((Block)BlockInit.ACACIA_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_CHAIR = ITEMS.register(
      "acacia_chair", () -> new ItemBlock((Block)BlockInit.ACACIA_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_MODERN_CHAIR = ITEMS.register(
      "acacia_modern_chair", () -> new ItemBlock((Block)BlockInit.ACACIA_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_STRIPED_CHAIR = ITEMS.register(
      "acacia_striped_chair", () -> new ItemBlock((Block)BlockInit.ACACIA_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_STOOL_CHAIR = ITEMS.register(
      "acacia_stool_chair", () -> new ItemBlock((Block)BlockInit.ACACIA_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_COUNTER = ITEMS.register(
      "acacia_counter", () -> new ItemBlock((Block)BlockInit.ACACIA_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DRAWER_COUNTER = ITEMS.register(
      "acacia_drawer_counter", () -> new ItemBlock((Block)BlockInit.ACACIA_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "acacia_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.ACACIA_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_CUPBOARD_COUNTER = ITEMS.register(
      "acacia_cupboard_counter", () -> new ItemBlock((Block)BlockInit.ACACIA_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WARDROBE = ITEMS.register(
      "dark_oak_wardrobe", () -> new ItemBlock((Block)BlockInit.DARK_OAK_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_MODERN_WARDROBE = ITEMS.register(
      "dark_oak_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.DARK_OAK_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DOUBLE_WARDROBE = ITEMS.register(
      "dark_oak_double_wardrobe", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BOOKSHELF = ITEMS.register(
      "dark_oak_bookshelf", () -> new ItemBlock((Block)BlockInit.DARK_OAK_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BOOKSHELF_CUPBOARD = ITEMS.register(
      "dark_oak_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.DARK_OAK_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DRAWER = ITEMS.register(
      "dark_oak_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DOUBLE_DRAWER = ITEMS.register(
      "dark_oak_double_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BOOKSHELF_DRAWER = ITEMS.register(
      "dark_oak_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "dark_oak_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LARGE_DRAWER = ITEMS.register(
      "dark_oak_large_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "dark_oak_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_TRIPLE_DRAWER = ITEMS.register(
      "dark_oak_triple_drawer", () -> new ItemBlock((Block)BlockInit.DARK_OAK_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DESK = ITEMS.register(
      "dark_oak_desk", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_COVERED_DESK = ITEMS.register(
      "dark_oak_covered_desk", () -> new ItemBlock((Block)BlockInit.DARK_OAK_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_MODERN_DESK = ITEMS.register(
      "dark_oak_modern_desk", () -> new ItemBlock((Block)BlockInit.DARK_OAK_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_TABLE = ITEMS.register(
      "dark_oak_table", () -> new ItemBlock((Block)BlockInit.DARK_OAK_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_END_TABLE = ITEMS.register(
      "dark_oak_end_table", () -> new ItemBlock((Block)BlockInit.DARK_OAK_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_COFFEE_TABLE = ITEMS.register(
      "dark_oak_coffee_table", () -> new ItemBlock((Block)BlockInit.DARK_OAK_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_GLASS_TABLE = ITEMS.register(
      "dark_oak_glass_table", () -> new ItemBlock((Block)BlockInit.DARK_OAK_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_CHAIR = ITEMS.register(
      "dark_oak_chair", () -> new ItemBlock((Block)BlockInit.DARK_OAK_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_MODERN_CHAIR = ITEMS.register(
      "dark_oak_modern_chair", () -> new ItemBlock((Block)BlockInit.DARK_OAK_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_STRIPED_CHAIR = ITEMS.register(
      "dark_oak_striped_chair", () -> new ItemBlock((Block)BlockInit.DARK_OAK_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_STOOL_CHAIR = ITEMS.register(
      "dark_oak_stool_chair", () -> new ItemBlock((Block)BlockInit.DARK_OAK_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_COUNTER = ITEMS.register(
      "dark_oak_counter", () -> new ItemBlock((Block)BlockInit.DARK_OAK_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DRAWER_COUNTER = ITEMS.register(
      "dark_oak_drawer_counter", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "dark_oak_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_CUPBOARD_COUNTER = ITEMS.register(
      "dark_oak_cupboard_counter", () -> new ItemBlock((Block)BlockInit.DARK_OAK_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_WARDROBE = ITEMS.register(
      "crimson_wardrobe", () -> new ItemBlock((Block)BlockInit.CRIMSON_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_MODERN_WARDROBE = ITEMS.register(
      "crimson_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.CRIMSON_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DOUBLE_WARDROBE = ITEMS.register(
      "crimson_double_wardrobe", () -> new ItemBlock((Block)BlockInit.CRIMSON_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BOOKSHELF = ITEMS.register(
      "crimson_bookshelf", () -> new ItemBlock((Block)BlockInit.CRIMSON_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BOOKSHELF_CUPBOARD = ITEMS.register(
      "crimson_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.CRIMSON_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DRAWER = ITEMS.register(
      "crimson_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DOUBLE_DRAWER = ITEMS.register(
      "crimson_double_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BOOKSHELF_DRAWER = ITEMS.register(
      "crimson_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "crimson_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LARGE_DRAWER = ITEMS.register(
      "crimson_large_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "crimson_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_TRIPLE_DRAWER = ITEMS.register(
      "crimson_triple_drawer", () -> new ItemBlock((Block)BlockInit.CRIMSON_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DESK = ITEMS.register(
      "crimson_desk", () -> new ItemBlock((Block)BlockInit.CRIMSON_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_COVERED_DESK = ITEMS.register(
      "crimson_covered_desk", () -> new ItemBlock((Block)BlockInit.CRIMSON_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_MODERN_DESK = ITEMS.register(
      "crimson_modern_desk", () -> new ItemBlock((Block)BlockInit.CRIMSON_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_TABLE = ITEMS.register(
      "crimson_table", () -> new ItemBlock((Block)BlockInit.CRIMSON_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_END_TABLE = ITEMS.register(
      "crimson_end_table", () -> new ItemBlock((Block)BlockInit.CRIMSON_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_COFFEE_TABLE = ITEMS.register(
      "crimson_coffee_table", () -> new ItemBlock((Block)BlockInit.CRIMSON_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_GLASS_TABLE = ITEMS.register(
      "crimson_glass_table", () -> new ItemBlock((Block)BlockInit.CRIMSON_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_CHAIR = ITEMS.register(
      "crimson_chair", () -> new ItemBlock((Block)BlockInit.CRIMSON_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_MODERN_CHAIR = ITEMS.register(
      "crimson_modern_chair", () -> new ItemBlock((Block)BlockInit.CRIMSON_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STRIPED_CHAIR = ITEMS.register(
      "crimson_striped_chair", () -> new ItemBlock((Block)BlockInit.CRIMSON_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STOOL_CHAIR = ITEMS.register(
      "crimson_stool_chair", () -> new ItemBlock((Block)BlockInit.CRIMSON_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_COUNTER = ITEMS.register(
      "crimson_counter", () -> new ItemBlock((Block)BlockInit.CRIMSON_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DRAWER_COUNTER = ITEMS.register(
      "crimson_drawer_counter", () -> new ItemBlock((Block)BlockInit.CRIMSON_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "crimson_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.CRIMSON_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_CUPBOARD_COUNTER = ITEMS.register(
      "crimson_cupboard_counter", () -> new ItemBlock((Block)BlockInit.CRIMSON_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_WARDROBE = ITEMS.register(
      "warped_wardrobe", () -> new ItemBlock((Block)BlockInit.WARPED_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_MODERN_WARDROBE = ITEMS.register(
      "warped_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.WARPED_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DOUBLE_WARDROBE = ITEMS.register(
      "warped_double_wardrobe", () -> new ItemBlock((Block)BlockInit.WARPED_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BOOKSHELF = ITEMS.register(
      "warped_bookshelf", () -> new ItemBlock((Block)BlockInit.WARPED_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BOOKSHELF_CUPBOARD = ITEMS.register(
      "warped_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.WARPED_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DRAWER = ITEMS.register(
      "warped_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DOUBLE_DRAWER = ITEMS.register(
      "warped_double_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BOOKSHELF_DRAWER = ITEMS.register(
      "warped_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "warped_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LARGE_DRAWER = ITEMS.register(
      "warped_large_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "warped_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_TRIPLE_DRAWER = ITEMS.register(
      "warped_triple_drawer", () -> new ItemBlock((Block)BlockInit.WARPED_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DESK = ITEMS.register("warped_desk", () -> new ItemBlock((Block)BlockInit.WARPED_DESK.get(), new Properties()));
   public static final DeferredItem<Item> WARPED_COVERED_DESK = ITEMS.register(
      "warped_covered_desk", () -> new ItemBlock((Block)BlockInit.WARPED_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_MODERN_DESK = ITEMS.register(
      "warped_modern_desk", () -> new ItemBlock((Block)BlockInit.WARPED_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_TABLE = ITEMS.register(
      "warped_table", () -> new ItemBlock((Block)BlockInit.WARPED_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_END_TABLE = ITEMS.register(
      "warped_end_table", () -> new ItemBlock((Block)BlockInit.WARPED_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_COFFEE_TABLE = ITEMS.register(
      "warped_coffee_table", () -> new ItemBlock((Block)BlockInit.WARPED_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_GLASS_TABLE = ITEMS.register(
      "warped_glass_table", () -> new ItemBlock((Block)BlockInit.WARPED_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_CHAIR = ITEMS.register(
      "warped_chair", () -> new ItemBlock((Block)BlockInit.WARPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_MODERN_CHAIR = ITEMS.register(
      "warped_modern_chair", () -> new ItemBlock((Block)BlockInit.WARPED_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STRIPED_CHAIR = ITEMS.register(
      "warped_striped_chair", () -> new ItemBlock((Block)BlockInit.WARPED_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STOOL_CHAIR = ITEMS.register(
      "warped_stool_chair", () -> new ItemBlock((Block)BlockInit.WARPED_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_COUNTER = ITEMS.register(
      "warped_counter", () -> new ItemBlock((Block)BlockInit.WARPED_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DRAWER_COUNTER = ITEMS.register(
      "warped_drawer_counter", () -> new ItemBlock((Block)BlockInit.WARPED_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "warped_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.WARPED_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_CUPBOARD_COUNTER = ITEMS.register(
      "warped_cupboard_counter", () -> new ItemBlock((Block)BlockInit.WARPED_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WARDROBE = ITEMS.register(
      "mangrove_wardrobe", () -> new ItemBlock((Block)BlockInit.MANGROVE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_MODERN_WARDROBE = ITEMS.register(
      "mangrove_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.MANGROVE_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DOUBLE_WARDROBE = ITEMS.register(
      "mangrove_double_wardrobe", () -> new ItemBlock((Block)BlockInit.MANGROVE_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BOOKSHELF = ITEMS.register(
      "mangrove_bookshelf", () -> new ItemBlock((Block)BlockInit.MANGROVE_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BOOKSHELF_CUPBOARD = ITEMS.register(
      "mangrove_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.MANGROVE_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DRAWER = ITEMS.register(
      "mangrove_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DOUBLE_DRAWER = ITEMS.register(
      "mangrove_double_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BOOKSHELF_DRAWER = ITEMS.register(
      "mangrove_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "mangrove_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LARGE_DRAWER = ITEMS.register(
      "mangrove_large_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "mangrove_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_TRIPLE_DRAWER = ITEMS.register(
      "mangrove_triple_drawer", () -> new ItemBlock((Block)BlockInit.MANGROVE_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DESK = ITEMS.register(
      "mangrove_desk", () -> new ItemBlock((Block)BlockInit.MANGROVE_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_COVERED_DESK = ITEMS.register(
      "mangrove_covered_desk", () -> new ItemBlock((Block)BlockInit.MANGROVE_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_MODERN_DESK = ITEMS.register(
      "mangrove_modern_desk", () -> new ItemBlock((Block)BlockInit.MANGROVE_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_TABLE = ITEMS.register(
      "mangrove_table", () -> new ItemBlock((Block)BlockInit.MANGROVE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_END_TABLE = ITEMS.register(
      "mangrove_end_table", () -> new ItemBlock((Block)BlockInit.MANGROVE_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_COFFEE_TABLE = ITEMS.register(
      "mangrove_coffee_table", () -> new ItemBlock((Block)BlockInit.MANGROVE_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_GLASS_TABLE = ITEMS.register(
      "mangrove_glass_table", () -> new ItemBlock((Block)BlockInit.MANGROVE_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_CHAIR = ITEMS.register(
      "mangrove_chair", () -> new ItemBlock((Block)BlockInit.MANGROVE_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_MODERN_CHAIR = ITEMS.register(
      "mangrove_modern_chair", () -> new ItemBlock((Block)BlockInit.MANGROVE_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_STRIPED_CHAIR = ITEMS.register(
      "mangrove_striped_chair", () -> new ItemBlock((Block)BlockInit.MANGROVE_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_STOOL_CHAIR = ITEMS.register(
      "mangrove_stool_chair", () -> new ItemBlock((Block)BlockInit.MANGROVE_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_COUNTER = ITEMS.register(
      "mangrove_counter", () -> new ItemBlock((Block)BlockInit.MANGROVE_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DRAWER_COUNTER = ITEMS.register(
      "mangrove_drawer_counter", () -> new ItemBlock((Block)BlockInit.MANGROVE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "mangrove_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.MANGROVE_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_CUPBOARD_COUNTER = ITEMS.register(
      "mangrove_cupboard_counter", () -> new ItemBlock((Block)BlockInit.MANGROVE_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_WARDROBE = ITEMS.register(
      "stripped_oak_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_MODERN_WARDROBE = ITEMS.register(
      "stripped_oak_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_oak_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_BOOKSHELF = ITEMS.register(
      "stripped_oak_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_oak_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DRAWER = ITEMS.register(
      "stripped_oak_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DOUBLE_DRAWER = ITEMS.register(
      "stripped_oak_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_oak_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_oak_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_LARGE_DRAWER = ITEMS.register(
      "stripped_oak_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_oak_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_TRIPLE_DRAWER = ITEMS.register(
      "stripped_oak_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DESK = ITEMS.register(
      "stripped_oak_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_COVERED_DESK = ITEMS.register(
      "stripped_oak_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_MODERN_DESK = ITEMS.register(
      "stripped_oak_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_TABLE = ITEMS.register(
      "stripped_oak_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_END_TABLE = ITEMS.register(
      "stripped_oak_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_COFFEE_TABLE = ITEMS.register(
      "stripped_oak_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_GLASS_TABLE = ITEMS.register(
      "stripped_oak_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_CHAIR = ITEMS.register(
      "stripped_oak_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_MODERN_CHAIR = ITEMS.register(
      "stripped_oak_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_STRIPED_CHAIR = ITEMS.register(
      "stripped_oak_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_STOOL_CHAIR = ITEMS.register(
      "stripped_oak_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_COUNTER = ITEMS.register(
      "stripped_oak_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DRAWER_COUNTER = ITEMS.register(
      "stripped_oak_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_oak_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_oak_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_WARDROBE = ITEMS.register(
      "stripped_birch_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_MODERN_WARDROBE = ITEMS.register(
      "stripped_birch_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_birch_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_BOOKSHELF = ITEMS.register(
      "stripped_birch_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_birch_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DRAWER = ITEMS.register(
      "stripped_birch_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DOUBLE_DRAWER = ITEMS.register(
      "stripped_birch_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_birch_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_birch_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_LARGE_DRAWER = ITEMS.register(
      "stripped_birch_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_birch_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_TRIPLE_DRAWER = ITEMS.register(
      "stripped_birch_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DESK = ITEMS.register(
      "stripped_birch_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_COVERED_DESK = ITEMS.register(
      "stripped_birch_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_MODERN_DESK = ITEMS.register(
      "stripped_birch_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_TABLE = ITEMS.register(
      "stripped_birch_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_END_TABLE = ITEMS.register(
      "stripped_birch_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_COFFEE_TABLE = ITEMS.register(
      "stripped_birch_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_GLASS_TABLE = ITEMS.register(
      "stripped_birch_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_CHAIR = ITEMS.register(
      "stripped_birch_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_MODERN_CHAIR = ITEMS.register(
      "stripped_birch_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_STRIPED_CHAIR = ITEMS.register(
      "stripped_birch_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_STOOL_CHAIR = ITEMS.register(
      "stripped_birch_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_COUNTER = ITEMS.register(
      "stripped_birch_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DRAWER_COUNTER = ITEMS.register(
      "stripped_birch_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_birch_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_birch_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_WARDROBE = ITEMS.register(
      "stripped_spruce_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_MODERN_WARDROBE = ITEMS.register(
      "stripped_spruce_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_spruce_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_BOOKSHELF = ITEMS.register(
      "stripped_spruce_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_spruce_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DRAWER = ITEMS.register(
      "stripped_spruce_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DOUBLE_DRAWER = ITEMS.register(
      "stripped_spruce_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_spruce_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_spruce_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_LARGE_DRAWER = ITEMS.register(
      "stripped_spruce_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_spruce_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_TRIPLE_DRAWER = ITEMS.register(
      "stripped_spruce_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DESK = ITEMS.register(
      "stripped_spruce_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_COVERED_DESK = ITEMS.register(
      "stripped_spruce_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_MODERN_DESK = ITEMS.register(
      "stripped_spruce_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_TABLE = ITEMS.register(
      "stripped_spruce_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_END_TABLE = ITEMS.register(
      "stripped_spruce_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_COFFEE_TABLE = ITEMS.register(
      "stripped_spruce_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_GLASS_TABLE = ITEMS.register(
      "stripped_spruce_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_CHAIR = ITEMS.register(
      "stripped_spruce_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_MODERN_CHAIR = ITEMS.register(
      "stripped_spruce_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_STRIPED_CHAIR = ITEMS.register(
      "stripped_spruce_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_STOOL_CHAIR = ITEMS.register(
      "stripped_spruce_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_COUNTER = ITEMS.register(
      "stripped_spruce_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DRAWER_COUNTER = ITEMS.register(
      "stripped_spruce_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_spruce_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_spruce_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_WARDROBE = ITEMS.register(
      "stripped_jungle_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_MODERN_WARDROBE = ITEMS.register(
      "stripped_jungle_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_jungle_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_BOOKSHELF = ITEMS.register(
      "stripped_jungle_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_jungle_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DRAWER = ITEMS.register(
      "stripped_jungle_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DOUBLE_DRAWER = ITEMS.register(
      "stripped_jungle_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_jungle_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_jungle_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_LARGE_DRAWER = ITEMS.register(
      "stripped_jungle_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_jungle_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_TRIPLE_DRAWER = ITEMS.register(
      "stripped_jungle_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DESK = ITEMS.register(
      "stripped_jungle_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_COVERED_DESK = ITEMS.register(
      "stripped_jungle_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_MODERN_DESK = ITEMS.register(
      "stripped_jungle_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_TABLE = ITEMS.register(
      "stripped_jungle_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_END_TABLE = ITEMS.register(
      "stripped_jungle_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_COFFEE_TABLE = ITEMS.register(
      "stripped_jungle_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_GLASS_TABLE = ITEMS.register(
      "stripped_jungle_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_CHAIR = ITEMS.register(
      "stripped_jungle_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_MODERN_CHAIR = ITEMS.register(
      "stripped_jungle_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_STRIPED_CHAIR = ITEMS.register(
      "stripped_jungle_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_STOOL_CHAIR = ITEMS.register(
      "stripped_jungle_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_COUNTER = ITEMS.register(
      "stripped_jungle_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_jungle_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_jungle_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_jungle_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_WARDROBE = ITEMS.register(
      "stripped_acacia_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_MODERN_WARDROBE = ITEMS.register(
      "stripped_acacia_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_acacia_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_BOOKSHELF = ITEMS.register(
      "stripped_acacia_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_acacia_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DRAWER = ITEMS.register(
      "stripped_acacia_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DOUBLE_DRAWER = ITEMS.register(
      "stripped_acacia_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_acacia_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_acacia_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_LARGE_DRAWER = ITEMS.register(
      "stripped_acacia_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_acacia_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_TRIPLE_DRAWER = ITEMS.register(
      "stripped_acacia_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DESK = ITEMS.register(
      "stripped_acacia_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_COVERED_DESK = ITEMS.register(
      "stripped_acacia_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_MODERN_DESK = ITEMS.register(
      "stripped_acacia_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_TABLE = ITEMS.register(
      "stripped_acacia_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_END_TABLE = ITEMS.register(
      "stripped_acacia_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_COFFEE_TABLE = ITEMS.register(
      "stripped_acacia_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_GLASS_TABLE = ITEMS.register(
      "stripped_acacia_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_CHAIR = ITEMS.register(
      "stripped_acacia_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_MODERN_CHAIR = ITEMS.register(
      "stripped_acacia_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_STRIPED_CHAIR = ITEMS.register(
      "stripped_acacia_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_STOOL_CHAIR = ITEMS.register(
      "stripped_acacia_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_COUNTER = ITEMS.register(
      "stripped_acacia_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DRAWER_COUNTER = ITEMS.register(
      "stripped_acacia_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_acacia_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_acacia_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_WARDROBE = ITEMS.register(
      "stripped_dark_oak_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_MODERN_WARDROBE = ITEMS.register(
      "stripped_dark_oak_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_dark_oak_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_BOOKSHELF = ITEMS.register(
      "stripped_dark_oak_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_dark_oak_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DRAWER = ITEMS.register(
      "stripped_dark_oak_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DOUBLE_DRAWER = ITEMS.register(
      "stripped_dark_oak_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_dark_oak_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_dark_oak_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_LARGE_DRAWER = ITEMS.register(
      "stripped_dark_oak_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_dark_oak_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_TRIPLE_DRAWER = ITEMS.register(
      "stripped_dark_oak_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DESK = ITEMS.register(
      "stripped_dark_oak_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_COVERED_DESK = ITEMS.register(
      "stripped_dark_oak_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_MODERN_DESK = ITEMS.register(
      "stripped_dark_oak_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_TABLE = ITEMS.register(
      "stripped_dark_oak_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_END_TABLE = ITEMS.register(
      "stripped_dark_oak_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_COFFEE_TABLE = ITEMS.register(
      "stripped_dark_oak_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_GLASS_TABLE = ITEMS.register(
      "stripped_dark_oak_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_CHAIR = ITEMS.register(
      "stripped_dark_oak_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_MODERN_CHAIR = ITEMS.register(
      "stripped_dark_oak_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_STRIPED_CHAIR = ITEMS.register(
      "stripped_dark_oak_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_STOOL_CHAIR = ITEMS.register(
      "stripped_dark_oak_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_COUNTER = ITEMS.register(
      "stripped_dark_oak_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DRAWER_COUNTER = ITEMS.register(
      "stripped_dark_oak_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_dark_oak_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_dark_oak_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_WARDROBE = ITEMS.register(
      "stripped_crimson_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_MODERN_WARDROBE = ITEMS.register(
      "stripped_crimson_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_crimson_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_BOOKSHELF = ITEMS.register(
      "stripped_crimson_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_crimson_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DRAWER = ITEMS.register(
      "stripped_crimson_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DOUBLE_DRAWER = ITEMS.register(
      "stripped_crimson_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_crimson_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_crimson_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_LARGE_DRAWER = ITEMS.register(
      "stripped_crimson_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_crimson_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_TRIPLE_DRAWER = ITEMS.register(
      "stripped_crimson_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DESK = ITEMS.register(
      "stripped_crimson_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_COVERED_DESK = ITEMS.register(
      "stripped_crimson_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_MODERN_DESK = ITEMS.register(
      "stripped_crimson_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_TABLE = ITEMS.register(
      "stripped_crimson_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_END_TABLE = ITEMS.register(
      "stripped_crimson_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_COFFEE_TABLE = ITEMS.register(
      "stripped_crimson_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_GLASS_TABLE = ITEMS.register(
      "stripped_crimson_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_CHAIR = ITEMS.register(
      "stripped_crimson_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_MODERN_CHAIR = ITEMS.register(
      "stripped_crimson_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_STRIPED_CHAIR = ITEMS.register(
      "stripped_crimson_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_STOOL_CHAIR = ITEMS.register(
      "stripped_crimson_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_COUNTER = ITEMS.register(
      "stripped_crimson_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DRAWER_COUNTER = ITEMS.register(
      "stripped_crimson_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_crimson_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_crimson_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_WARDROBE = ITEMS.register(
      "stripped_warped_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_MODERN_WARDROBE = ITEMS.register(
      "stripped_warped_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_warped_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_BOOKSHELF = ITEMS.register(
      "stripped_warped_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_warped_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DRAWER = ITEMS.register(
      "stripped_warped_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DOUBLE_DRAWER = ITEMS.register(
      "stripped_warped_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_warped_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_warped_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_LARGE_DRAWER = ITEMS.register(
      "stripped_warped_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_warped_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_TRIPLE_DRAWER = ITEMS.register(
      "stripped_warped_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DESK = ITEMS.register(
      "stripped_warped_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_COVERED_DESK = ITEMS.register(
      "stripped_warped_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_MODERN_DESK = ITEMS.register(
      "stripped_warped_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_TABLE = ITEMS.register(
      "stripped_warped_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_END_TABLE = ITEMS.register(
      "stripped_warped_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_COFFEE_TABLE = ITEMS.register(
      "stripped_warped_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_GLASS_TABLE = ITEMS.register(
      "stripped_warped_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_CHAIR = ITEMS.register(
      "stripped_warped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_MODERN_CHAIR = ITEMS.register(
      "stripped_warped_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_STRIPED_CHAIR = ITEMS.register(
      "stripped_warped_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_STOOL_CHAIR = ITEMS.register(
      "stripped_warped_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_COUNTER = ITEMS.register(
      "stripped_warped_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DRAWER_COUNTER = ITEMS.register(
      "stripped_warped_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_warped_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_warped_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_WARDROBE = ITEMS.register(
      "stripped_mangrove_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_MODERN_WARDROBE = ITEMS.register(
      "stripped_mangrove_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_mangrove_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_BOOKSHELF = ITEMS.register(
      "stripped_mangrove_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_mangrove_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DRAWER = ITEMS.register(
      "stripped_mangrove_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DOUBLE_DRAWER = ITEMS.register(
      "stripped_mangrove_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_mangrove_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_mangrove_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_LARGE_DRAWER = ITEMS.register(
      "stripped_mangrove_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_mangrove_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_TRIPLE_DRAWER = ITEMS.register(
      "stripped_mangrove_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DESK = ITEMS.register(
      "stripped_mangrove_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_COVERED_DESK = ITEMS.register(
      "stripped_mangrove_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_MODERN_DESK = ITEMS.register(
      "stripped_mangrove_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_TABLE = ITEMS.register(
      "stripped_mangrove_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_END_TABLE = ITEMS.register(
      "stripped_mangrove_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_COFFEE_TABLE = ITEMS.register(
      "stripped_mangrove_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_GLASS_TABLE = ITEMS.register(
      "stripped_mangrove_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_CHAIR = ITEMS.register(
      "stripped_mangrove_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_MODERN_CHAIR = ITEMS.register(
      "stripped_mangrove_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_STRIPED_CHAIR = ITEMS.register(
      "stripped_mangrove_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_STOOL_CHAIR = ITEMS.register(
      "stripped_mangrove_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_COUNTER = ITEMS.register(
      "stripped_mangrove_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DRAWER_COUNTER = ITEMS.register(
      "stripped_mangrove_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_mangrove_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_mangrove_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_WARDROBE = ITEMS.register(
      "cherry_wardrobe", () -> new ItemBlock((Block)BlockInit.CHERRY_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_MODERN_WARDROBE = ITEMS.register(
      "cherry_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.CHERRY_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DOUBLE_WARDROBE = ITEMS.register(
      "cherry_double_wardrobe", () -> new ItemBlock((Block)BlockInit.CHERRY_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BOOKSHELF = ITEMS.register(
      "cherry_bookshelf", () -> new ItemBlock((Block)BlockInit.CHERRY_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BOOKSHELF_CUPBOARD = ITEMS.register(
      "cherry_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.CHERRY_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DRAWER = ITEMS.register(
      "cherry_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DOUBLE_DRAWER = ITEMS.register(
      "cherry_double_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BOOKSHELF_DRAWER = ITEMS.register(
      "cherry_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "cherry_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LARGE_DRAWER = ITEMS.register(
      "cherry_large_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "cherry_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_TRIPLE_DRAWER = ITEMS.register(
      "cherry_triple_drawer", () -> new ItemBlock((Block)BlockInit.CHERRY_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DESK = ITEMS.register("cherry_desk", () -> new ItemBlock((Block)BlockInit.CHERRY_DESK.get(), new Properties()));
   public static final DeferredItem<Item> CHERRY_COVERED_DESK = ITEMS.register(
      "cherry_covered_desk", () -> new ItemBlock((Block)BlockInit.CHERRY_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_MODERN_DESK = ITEMS.register(
      "cherry_modern_desk", () -> new ItemBlock((Block)BlockInit.CHERRY_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_TABLE = ITEMS.register(
      "cherry_table", () -> new ItemBlock((Block)BlockInit.CHERRY_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_END_TABLE = ITEMS.register(
      "cherry_end_table", () -> new ItemBlock((Block)BlockInit.CHERRY_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_COFFEE_TABLE = ITEMS.register(
      "cherry_coffee_table", () -> new ItemBlock((Block)BlockInit.CHERRY_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_GLASS_TABLE = ITEMS.register(
      "cherry_glass_table", () -> new ItemBlock((Block)BlockInit.CHERRY_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_CHAIR = ITEMS.register(
      "cherry_chair", () -> new ItemBlock((Block)BlockInit.CHERRY_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_MODERN_CHAIR = ITEMS.register(
      "cherry_modern_chair", () -> new ItemBlock((Block)BlockInit.CHERRY_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_STRIPED_CHAIR = ITEMS.register(
      "cherry_striped_chair", () -> new ItemBlock((Block)BlockInit.CHERRY_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_STOOL_CHAIR = ITEMS.register(
      "cherry_stool_chair", () -> new ItemBlock((Block)BlockInit.CHERRY_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_COUNTER = ITEMS.register(
      "cherry_counter", () -> new ItemBlock((Block)BlockInit.CHERRY_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DRAWER_COUNTER = ITEMS.register(
      "cherry_drawer_counter", () -> new ItemBlock((Block)BlockInit.CHERRY_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "cherry_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.CHERRY_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_CUPBOARD_COUNTER = ITEMS.register(
      "cherry_cupboard_counter", () -> new ItemBlock((Block)BlockInit.CHERRY_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_WARDROBE = ITEMS.register(
      "stripped_cherry_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_MODERN_WARDROBE = ITEMS.register(
      "stripped_cherry_modern_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_MODERN_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DOUBLE_WARDROBE = ITEMS.register(
      "stripped_cherry_double_wardrobe", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DOUBLE_WARDROBE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_BOOKSHELF = ITEMS.register(
      "stripped_cherry_bookshelf", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_BOOKSHELF.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_BOOKSHELF_CUPBOARD = ITEMS.register(
      "stripped_cherry_bookshelf_cupboard", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_BOOKSHELF_CUPBOARD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DRAWER = ITEMS.register(
      "stripped_cherry_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DOUBLE_DRAWER = ITEMS.register(
      "stripped_cherry_double_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DOUBLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_cherry_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_LOWER_BOOKSHELF_DRAWER = ITEMS.register(
      "stripped_cherry_lower_bookshelf_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_LOWER_BOOKSHELF_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_LARGE_DRAWER = ITEMS.register(
      "stripped_cherry_large_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_LARGE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_LOWER_TRIPLE_DRAWER = ITEMS.register(
      "stripped_cherry_lower_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_LOWER_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_TRIPLE_DRAWER = ITEMS.register(
      "stripped_cherry_triple_drawer", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_TRIPLE_DRAWER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DESK = ITEMS.register(
      "stripped_cherry_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_COVERED_DESK = ITEMS.register(
      "stripped_cherry_covered_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_COVERED_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_MODERN_DESK = ITEMS.register(
      "stripped_cherry_modern_desk", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_MODERN_DESK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_TABLE = ITEMS.register(
      "stripped_cherry_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_END_TABLE = ITEMS.register(
      "stripped_cherry_end_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_END_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_COFFEE_TABLE = ITEMS.register(
      "stripped_cherry_coffee_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_COFFEE_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_GLASS_TABLE = ITEMS.register(
      "stripped_cherry_glass_table", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_GLASS_TABLE.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_CHAIR = ITEMS.register(
      "stripped_cherry_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_MODERN_CHAIR = ITEMS.register(
      "stripped_cherry_modern_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_MODERN_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_STRIPED_CHAIR = ITEMS.register(
      "stripped_cherry_striped_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_STRIPED_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_STOOL_CHAIR = ITEMS.register(
      "stripped_cherry_stool_chair", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_STOOL_CHAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_COUNTER = ITEMS.register(
      "stripped_cherry_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DRAWER_COUNTER = ITEMS.register(
      "stripped_cherry_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DOUBLE_DRAWER_COUNTER = ITEMS.register(
      "stripped_cherry_double_drawer_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DOUBLE_DRAWER_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_CUPBOARD_COUNTER = ITEMS.register(
      "stripped_cherry_cupboard_counter", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_CUPBOARD_COUNTER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_KITCHEN_CABINET = ITEMS.register(
      "oak_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.OAK_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "oak_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.OAK_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_GLASS_KITCHEN_CABINET = ITEMS.register(
      "oak_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.OAK_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_KITCHEN_CABINET = ITEMS.register(
      "stripped_oak_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_oak_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_oak_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_KITCHEN_CABINET = ITEMS.register(
      "birch_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.BIRCH_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "birch_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.BIRCH_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_GLASS_KITCHEN_CABINET = ITEMS.register(
      "birch_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.BIRCH_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_KITCHEN_CABINET = ITEMS.register(
      "stripped_birch_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_birch_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_birch_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_KITCHEN_CABINET = ITEMS.register(
      "spruce_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.SPRUCE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "spruce_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.SPRUCE_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_GLASS_KITCHEN_CABINET = ITEMS.register(
      "spruce_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.SPRUCE_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_KITCHEN_CABINET = ITEMS.register(
      "stripped_spruce_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_spruce_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_spruce_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_KITCHEN_CABINET = ITEMS.register(
      "jungle_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.JUNGLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "jungle_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.JUNGLE_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_GLASS_KITCHEN_CABINET = ITEMS.register(
      "jungle_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.JUNGLE_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_jungle_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_jungle_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_jungle_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_KITCHEN_CABINET = ITEMS.register(
      "acacia_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.ACACIA_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "acacia_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.ACACIA_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_GLASS_KITCHEN_CABINET = ITEMS.register(
      "acacia_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.ACACIA_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_KITCHEN_CABINET = ITEMS.register(
      "stripped_acacia_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_acacia_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_acacia_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_KITCHEN_CABINET = ITEMS.register(
      "dark_oak_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.DARK_OAK_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "dark_oak_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.DARK_OAK_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_GLASS_KITCHEN_CABINET = ITEMS.register(
      "dark_oak_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.DARK_OAK_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_KITCHEN_CABINET = ITEMS.register(
      "stripped_dark_oak_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_dark_oak_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_dark_oak_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_KITCHEN_CABINET = ITEMS.register(
      "warped_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.WARPED_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "warped_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.WARPED_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_GLASS_KITCHEN_CABINET = ITEMS.register(
      "warped_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.WARPED_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_KITCHEN_CABINET = ITEMS.register(
      "stripped_warped_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_warped_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_warped_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_KITCHEN_CABINET = ITEMS.register(
      "crimson_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.CRIMSON_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "crimson_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.CRIMSON_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_GLASS_KITCHEN_CABINET = ITEMS.register(
      "crimson_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.CRIMSON_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_KITCHEN_CABINET = ITEMS.register(
      "stripped_crimson_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_crimson_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_crimson_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_KITCHEN_CABINET = ITEMS.register(
      "mangrove_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.MANGROVE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "mangrove_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.MANGROVE_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_GLASS_KITCHEN_CABINET = ITEMS.register(
      "mangrove_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.MANGROVE_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_KITCHEN_CABINET = ITEMS.register(
      "stripped_mangrove_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_mangrove_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_mangrove_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_KITCHEN_CABINET = ITEMS.register(
      "cherry_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.CHERRY_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "cherry_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.CHERRY_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_GLASS_KITCHEN_CABINET = ITEMS.register(
      "cherry_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.CHERRY_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_KITCHEN_CABINET = ITEMS.register(
      "stripped_cherry_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_DOUBLE_KITCHEN_CABINET = ITEMS.register(
      "stripped_cherry_double_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_DOUBLE_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_GLASS_KITCHEN_CABINET = ITEMS.register(
      "stripped_cherry_glass_kitchen_cabinet", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_GLASS_KITCHEN_CABINET.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_KITCHEN_SINK = ITEMS.register(
      "oak_kitchen_sink", () -> new ItemBlock((Block)BlockInit.OAK_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_KITCHEN_SINK = ITEMS.register(
      "stripped_oak_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_OAK_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_KITCHEN_SINK = ITEMS.register(
      "birch_kitchen_sink", () -> new ItemBlock((Block)BlockInit.BIRCH_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_KITCHEN_SINK = ITEMS.register(
      "stripped_birch_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_BIRCH_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_KITCHEN_SINK = ITEMS.register(
      "spruce_kitchen_sink", () -> new ItemBlock((Block)BlockInit.SPRUCE_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_KITCHEN_SINK = ITEMS.register(
      "stripped_spruce_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_SPRUCE_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_KITCHEN_SINK = ITEMS.register(
      "jungle_kitchen_sink", () -> new ItemBlock((Block)BlockInit.JUNGLE_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_KITCHEN_SINK = ITEMS.register(
      "stripped_jungle_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_JUNGLE_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_KITCHEN_SINK = ITEMS.register(
      "acacia_kitchen_sink", () -> new ItemBlock((Block)BlockInit.ACACIA_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_KITCHEN_SINK = ITEMS.register(
      "stripped_acacia_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_ACACIA_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_KITCHEN_SINK = ITEMS.register(
      "dark_oak_kitchen_sink", () -> new ItemBlock((Block)BlockInit.DARK_OAK_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_KITCHEN_SINK = ITEMS.register(
      "stripped_dark_oak_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_KITCHEN_SINK = ITEMS.register(
      "crimson_kitchen_sink", () -> new ItemBlock((Block)BlockInit.CRIMSON_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_KITCHEN_SINK = ITEMS.register(
      "stripped_crimson_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_CRIMSON_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_KITCHEN_SINK = ITEMS.register(
      "warped_kitchen_sink", () -> new ItemBlock((Block)BlockInit.WARPED_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_KITCHEN_SINK = ITEMS.register(
      "stripped_warped_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_WARPED_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_KITCHEN_SINK = ITEMS.register(
      "mangrove_kitchen_sink", () -> new ItemBlock((Block)BlockInit.MANGROVE_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_KITCHEN_SINK = ITEMS.register(
      "stripped_mangrove_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_MANGROVE_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_KITCHEN_SINK = ITEMS.register(
      "cherry_kitchen_sink", () -> new ItemBlock((Block)BlockInit.CHERRY_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_KITCHEN_SINK = ITEMS.register(
      "stripped_cherry_kitchen_sink", () -> new ItemBlock((Block)BlockInit.STRIPPED_CHERRY_KITCHEN_SINK.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_COUCH = ITEMS.register("white_couch", () -> new ItemBlock((Block)BlockInit.WHITE_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> WHITE_CHAISE = ITEMS.register(
      "white_chaise", () -> new ItemBlock((Block)BlockInit.WHITE_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_COUCH = ITEMS.register(
      "light_gray_couch", () -> new ItemBlock((Block)BlockInit.LIGHT_GRAY_COUCH.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CHAISE = ITEMS.register(
      "light_gray_chaise", () -> new ItemBlock((Block)BlockInit.LIGHT_GRAY_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_COUCH = ITEMS.register("gray_couch", () -> new ItemBlock((Block)BlockInit.GRAY_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> GRAY_CHAISE = ITEMS.register("gray_chaise", () -> new ItemBlock((Block)BlockInit.GRAY_CHAISE.get(), new Properties()));
   public static final DeferredItem<Item> BLACK_COUCH = ITEMS.register("black_couch", () -> new ItemBlock((Block)BlockInit.BLACK_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> BLACK_CHAISE = ITEMS.register(
      "black_chaise", () -> new ItemBlock((Block)BlockInit.BLACK_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_COUCH = ITEMS.register("brown_couch", () -> new ItemBlock((Block)BlockInit.BROWN_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> BROWN_CHAISE = ITEMS.register(
      "brown_chaise", () -> new ItemBlock((Block)BlockInit.BROWN_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_COUCH = ITEMS.register("red_couch", () -> new ItemBlock((Block)BlockInit.RED_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> RED_CHAISE = ITEMS.register("red_chaise", () -> new ItemBlock((Block)BlockInit.RED_CHAISE.get(), new Properties()));
   public static final DeferredItem<Item> ORANGE_COUCH = ITEMS.register(
      "orange_couch", () -> new ItemBlock((Block)BlockInit.ORANGE_COUCH.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CHAISE = ITEMS.register(
      "orange_chaise", () -> new ItemBlock((Block)BlockInit.ORANGE_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_COUCH = ITEMS.register(
      "yellow_couch", () -> new ItemBlock((Block)BlockInit.YELLOW_COUCH.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CHAISE = ITEMS.register(
      "yellow_chaise", () -> new ItemBlock((Block)BlockInit.YELLOW_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_COUCH = ITEMS.register("lime_couch", () -> new ItemBlock((Block)BlockInit.LIME_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> LIME_CHAISE = ITEMS.register("lime_chaise", () -> new ItemBlock((Block)BlockInit.LIME_CHAISE.get(), new Properties()));
   public static final DeferredItem<Item> GREEN_COUCH = ITEMS.register("green_couch", () -> new ItemBlock((Block)BlockInit.GREEN_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> GREEN_CHAISE = ITEMS.register(
      "green_chaise", () -> new ItemBlock((Block)BlockInit.GREEN_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_COUCH = ITEMS.register("cyan_couch", () -> new ItemBlock((Block)BlockInit.CYAN_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> CYAN_CHAISE = ITEMS.register("cyan_chaise", () -> new ItemBlock((Block)BlockInit.CYAN_CHAISE.get(), new Properties()));
   public static final DeferredItem<Item> LIGHT_BLUE_COUCH = ITEMS.register(
      "light_blue_couch", () -> new ItemBlock((Block)BlockInit.LIGHT_BLUE_COUCH.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CHAISE = ITEMS.register(
      "light_blue_chaise", () -> new ItemBlock((Block)BlockInit.LIGHT_BLUE_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_COUCH = ITEMS.register("blue_couch", () -> new ItemBlock((Block)BlockInit.BLUE_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> BLUE_CHAISE = ITEMS.register("blue_chaise", () -> new ItemBlock((Block)BlockInit.BLUE_CHAISE.get(), new Properties()));
   public static final DeferredItem<Item> PURPLE_COUCH = ITEMS.register(
      "purple_couch", () -> new ItemBlock((Block)BlockInit.PURPLE_COUCH.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CHAISE = ITEMS.register(
      "purple_chaise", () -> new ItemBlock((Block)BlockInit.PURPLE_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_COUCH = ITEMS.register(
      "magenta_couch", () -> new ItemBlock((Block)BlockInit.MAGENTA_COUCH.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CHAISE = ITEMS.register(
      "magenta_chaise", () -> new ItemBlock((Block)BlockInit.MAGENTA_CHAISE.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_COUCH = ITEMS.register("pink_couch", () -> new ItemBlock((Block)BlockInit.PINK_COUCH.get(), new Properties()));
   public static final DeferredItem<Item> PINK_CHAISE = ITEMS.register("pink_chaise", () -> new ItemBlock((Block)BlockInit.PINK_CHAISE.get(), new Properties()));
}
