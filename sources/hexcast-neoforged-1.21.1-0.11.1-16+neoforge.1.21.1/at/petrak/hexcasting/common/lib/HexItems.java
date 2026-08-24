package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.items.ItemJewelerHammer;
import at.petrak.hexcasting.common.items.ItemLens;
import at.petrak.hexcasting.common.items.ItemLoreFragment;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.magic.ItemArtifact;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.items.magic.ItemCypher;
import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;
import at.petrak.hexcasting.common.items.magic.ItemTrinket;
import at.petrak.hexcasting.common.items.pigment.ItemAmethystAndCopperPigment;
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment;
import at.petrak.hexcasting.common.items.pigment.ItemPridePigment;
import at.petrak.hexcasting.common.items.pigment.ItemUUIDPigment;
import at.petrak.hexcasting.common.items.storage.ItemAbacus;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.common.items.storage.ItemScroll;
import at.petrak.hexcasting.common.items.storage.ItemSlate;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.items.storage.ItemThoughtKnot;
import com.google.common.base.Suppliers;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;

public class HexItems {
   private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();
   private static final Map<CreativeModeTab, List<HexItems.TabEntry>> ITEM_TABS = new LinkedHashMap<>();
   public static final Item AMETHYST_DUST = make("amethyst_dust", new Item(props()));
   public static final Item CHARGED_AMETHYST = make("charged_amethyst", new Item(props()));
   public static final Item QUENCHED_SHARD = make("quenched_allay_shard", new Item(props()));
   public static final ItemStaff STAFF_OAK = make("staff/oak", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_SPRUCE = make("staff/spruce", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_BIRCH = make("staff/birch", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_JUNGLE = make("staff/jungle", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_ACACIA = make("staff/acacia", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_DARK_OAK = make("staff/dark_oak", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_CRIMSON = make("staff/crimson", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_WARPED = make("staff/warped", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_MANGROVE = make("staff/mangrove", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_CHERRY = make("staff/cherry", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_BAMBOO = make("staff/bamboo", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_EDIFIED = make("staff/edified", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_QUENCHED = make("staff/quenched", new ItemStaff(unstackable()));
   public static final ItemStaff STAFF_MINDSPLICE = make("staff/mindsplice", new ItemStaff(unstackable()));
   public static final ItemLens SCRYING_LENS = make("lens", new ItemLens(props().stacksTo(1)));
   public static final ItemAbacus ABACUS = make("abacus", new ItemAbacus(unstackable()));
   public static final ItemThoughtKnot THOUGHT_KNOT = make("thought_knot", new ItemThoughtKnot(unstackable()));
   public static final ItemFocus FOCUS = make("focus", new ItemFocus(unstackable()));
   public static final ItemSpellbook SPELLBOOK = make("spellbook", new ItemSpellbook(unstackable()));
   public static final ItemCypher CYPHER = make("cypher", new ItemCypher(unstackable()));
   public static final ItemTrinket TRINKET = make("trinket", new ItemTrinket(unstackable().rarity(Rarity.UNCOMMON)));
   public static final ItemArtifact ARTIFACT = make("artifact", new ItemArtifact(unstackable().rarity(Rarity.RARE)));
   public static final ItemJewelerHammer JEWELER_HAMMER = make(
      "jeweler_hammer", new ItemJewelerHammer(Tiers.IRON, 0, -2.8F, props().stacksTo(1).durability(Tiers.DIAMOND.getUses()))
   );
   public static final ItemScroll SCROLL_SMOL = make("scroll_small", new ItemScroll(props(), 1));
   public static final ItemScroll SCROLL_MEDIUM = make("scroll_medium", new ItemScroll(props(), 2));
   public static final ItemScroll SCROLL_LARGE = make("scroll", new ItemScroll(props(), 3));
   public static final ItemSlate SLATE = make("slate", new ItemSlate(HexBlocks.SLATE, props()));
   public static final ItemMediaBattery BATTERY = make("battery", new ItemMediaBattery(unstackable()), null);
   public static final Supplier<ItemStack> BATTERY_DUST_STACK = addToTab(
      () -> ItemMediaBattery.withMedia(new ItemStack(BATTERY), 640000L, 640000L), HexCreativeTabs.HEX
   );
   public static final Supplier<ItemStack> BATTERY_SHARD_STACK = addToTab(
      () -> ItemMediaBattery.withMedia(new ItemStack(BATTERY), 3200000L, 3200000L), HexCreativeTabs.HEX
   );
   public static final Supplier<ItemStack> BATTERY_CRYSTAL_STACK = addToTab(
      () -> ItemMediaBattery.withMedia(new ItemStack(BATTERY), 6400000L, 6400000L), HexCreativeTabs.HEX
   );
   public static final Supplier<ItemStack> BATTERY_QUENCHED_SHARD_STACK = addToTab(
      () -> ItemMediaBattery.withMedia(new ItemStack(BATTERY), 19200000L, 19200000L), HexCreativeTabs.HEX
   );
   public static final Supplier<ItemStack> BATTERY_QUENCHED_BLOCK_STACK = addToTab(
      () -> ItemMediaBattery.withMedia(new ItemStack(BATTERY), 76800000L, 76800000L), HexCreativeTabs.HEX
   );
   public static final EnumMap<DyeColor, ItemDyePigment> DYE_PIGMENTS = (EnumMap<DyeColor, ItemDyePigment>)Util.make(() -> {
      EnumMap<DyeColor, ItemDyePigment> out = new EnumMap<>(DyeColor.class);

      for (DyeColor dye : DyeColor.values()) {
         out.put(dye, make("dye_colorizer_" + dye.getName(), new ItemDyePigment(dye, unstackable())));
      }

      return out;
   });
   public static final EnumMap<ItemPridePigment.Type, ItemPridePigment> PRIDE_PIGMENTS = (EnumMap<ItemPridePigment.Type, ItemPridePigment>)Util.make(() -> {
      EnumMap<ItemPridePigment.Type, ItemPridePigment> out = new EnumMap<>(ItemPridePigment.Type.class);

      for (ItemPridePigment.Type politicsInMyVidya : ItemPridePigment.Type.values()) {
         out.put(politicsInMyVidya, make("pride_colorizer_" + politicsInMyVidya.getName(), new ItemPridePigment(politicsInMyVidya, unstackable())));
      }

      return out;
   });
   public static final Item UUID_PIGMENT = make("uuid_colorizer", new ItemUUIDPigment(unstackable()));
   public static final Item DEFAULT_PIGMENT = make("default_colorizer", new ItemAmethystAndCopperPigment(unstackable()));
   public static final Item SUBMARINE_SANDWICH = make("sub_sandwich", new Item(props().food(new Builder().nutrition(14).saturationModifier(1.2F).build())));
   public static final ItemLoreFragment LORE_FRAGMENT = make("lore_fragment", new ItemLoreFragment(unstackable().rarity(Rarity.RARE)));
   public static final ItemCreativeUnlocker CREATIVE_UNLOCKER = make(
      "creative_unlocker",
      new ItemCreativeUnlocker(unstackable().rarity(Rarity.EPIC).food(new Builder().nutrition(20).saturationModifier(1.0F).alwaysEdible().build()))
   );

   public static void registerItems(BiConsumer<Item, ResourceLocation> r) {
      for (Entry<ResourceLocation, Item> e : ITEMS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   public static void registerItemCreativeTab(Output r, CreativeModeTab tab) {
      for (HexItems.TabEntry item : ITEM_TABS.getOrDefault(tab, List.of())) {
         item.register(r);
      }
   }

   public static Properties props() {
      return new Properties();
   }

   public static Properties unstackable() {
      return props().stacksTo(1);
   }

   private static <T extends Item> T make(ResourceLocation id, T item, @Nullable CreativeModeTab tab) {
      Item old = ITEMS.put(id, item);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         if (tab != null) {
            ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new HexItems.TabEntry.ItemEntry(item));
         }

         return item;
      }
   }

   private static <T extends Item> T make(String id, T item, @Nullable CreativeModeTab tab) {
      return make(HexAPI.modLoc(id), item, tab);
   }

   private static <T extends Item> T make(String id, T item) {
      return make(HexAPI.modLoc(id), item, HexCreativeTabs.HEX);
   }

   private static Supplier<ItemStack> addToTab(Supplier<ItemStack> stack, CreativeModeTab tab) {
      com.google.common.base.Supplier<ItemStack> memoised = Suppliers.memoize(stack::get);
      ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new HexItems.TabEntry.StackEntry(memoised));
      return memoised;
   }

   private abstract static class TabEntry {
      abstract void register(Output var1);

      static class ItemEntry extends HexItems.TabEntry {
         private final Item item;

         ItemEntry(Item item) {
            this.item = item;
         }

         @Override
         void register(Output r) {
            r.accept(this.item);
         }
      }

      static class StackEntry extends HexItems.TabEntry {
         private final Supplier<ItemStack> stack;

         StackEntry(Supplier<ItemStack> stack) {
            this.stack = stack;
         }

         @Override
         void register(Output r) {
            r.accept(this.stack.get());
         }
      }
   }
}
