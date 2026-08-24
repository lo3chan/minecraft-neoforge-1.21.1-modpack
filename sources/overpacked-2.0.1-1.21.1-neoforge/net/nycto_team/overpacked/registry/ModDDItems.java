package net.nycto_team.overpacked.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import net.nycto_team.overpacked.item.GiantBackpackItem;

public class ModDDItems {
   public static final Items reg = DeferredRegister.createItems("overpacked");
   public static final DeferredItem<Item> maroon_giant_backpack = reg("maroon_giant_backpack", () -> new GiantBackpackItem(16));
   public static final DeferredItem<Item> rose_giant_backpack = reg("rose_giant_backpack", () -> new GiantBackpackItem(17));
   public static final DeferredItem<Item> coral_giant_backpack = reg("coral_giant_backpack", () -> new GiantBackpackItem(18));
   public static final DeferredItem<Item> indigo_giant_backpack = reg("indigo_giant_backpack", () -> new GiantBackpackItem(19));
   public static final DeferredItem<Item> navy_giant_backpack = reg("navy_giant_backpack", () -> new GiantBackpackItem(20));
   public static final DeferredItem<Item> slate_giant_backpack = reg("slate_giant_backpack", () -> new GiantBackpackItem(21));
   public static final DeferredItem<Item> olive_giant_backpack = reg("olive_giant_backpack", () -> new GiantBackpackItem(22));
   public static final DeferredItem<Item> amber_giant_backpack = reg("amber_giant_backpack", () -> new GiantBackpackItem(23));
   public static final DeferredItem<Item> beige_giant_backpack = reg("beige_giant_backpack", () -> new GiantBackpackItem(24));
   public static final DeferredItem<Item> teal_giant_backpack = reg("teal_giant_backpack", () -> new GiantBackpackItem(25));
   public static final DeferredItem<Item> mint_giant_backpack = reg("mint_giant_backpack", () -> new GiantBackpackItem(26));
   public static final DeferredItem<Item> aqua_giant_backpack = reg("aqua_giant_backpack", () -> new GiantBackpackItem(27));
   public static final DeferredItem<Item> verdant_giant_backpack = reg("verdant_giant_backpack", () -> new GiantBackpackItem(28));
   public static final DeferredItem<Item> forest_giant_backpack = reg("forest_giant_backpack", () -> new GiantBackpackItem(29));
   public static final DeferredItem<Item> ginger_giant_backpack = reg("ginger_giant_backpack", () -> new GiantBackpackItem(30));
   public static final DeferredItem<Item> tan_giant_backpack = reg("tan_giant_backpack", () -> new GiantBackpackItem(31));

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static <T extends Item> DeferredItem<T> reg(String name, Supplier<T> value) {
      return reg.register(name, value);
   }
}
