package net.nycto_team.overpacked.registry;

import java.util.function.Supplier;
import net.bobophones.bobolib.item.SimpleItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import net.nycto_team.overpacked.item.GiantBackpackItem;

public class ModItems {
   public static final Items reg = DeferredRegister.createItems("overpacked");
   public static final DeferredItem<Item> giant_backpack = reg("giant_backpack", () -> new GiantBackpackItem(-1));
   public static final DeferredItem<Item> white_giant_backpack = reg("white_giant_backpack", () -> new GiantBackpackItem(0));
   public static final DeferredItem<Item> orange_giant_backpack = reg("orange_giant_backpack", () -> new GiantBackpackItem(1));
   public static final DeferredItem<Item> magenta_giant_backpack = reg("magenta_giant_backpack", () -> new GiantBackpackItem(2));
   public static final DeferredItem<Item> light_blue_giant_backpack = reg("light_blue_giant_backpack", () -> new GiantBackpackItem(3));
   public static final DeferredItem<Item> yellow_giant_backpack = reg("yellow_giant_backpack", () -> new GiantBackpackItem(4));
   public static final DeferredItem<Item> lime_giant_backpack = reg("lime_giant_backpack", () -> new GiantBackpackItem(5));
   public static final DeferredItem<Item> pink_giant_backpack = reg("pink_giant_backpack", () -> new GiantBackpackItem(6));
   public static final DeferredItem<Item> gray_giant_backpack = reg("gray_giant_backpack", () -> new GiantBackpackItem(7));
   public static final DeferredItem<Item> light_gray_giant_backpack = reg("light_gray_giant_backpack", () -> new GiantBackpackItem(8));
   public static final DeferredItem<Item> cyan_giant_backpack = reg("cyan_giant_backpack", () -> new GiantBackpackItem(9));
   public static final DeferredItem<Item> purple_giant_backpack = reg("purple_giant_backpack", () -> new GiantBackpackItem(10));
   public static final DeferredItem<Item> blue_giant_backpack = reg("blue_giant_backpack", () -> new GiantBackpackItem(11));
   public static final DeferredItem<Item> brown_giant_backpack = reg("brown_giant_backpack", () -> new GiantBackpackItem(12));
   public static final DeferredItem<Item> green_giant_backpack = reg("green_giant_backpack", () -> new GiantBackpackItem(13));
   public static final DeferredItem<Item> red_giant_backpack = reg("red_giant_backpack", () -> new GiantBackpackItem(14));
   public static final DeferredItem<Item> black_giant_backpack = reg("black_giant_backpack", () -> new GiantBackpackItem(15));
   public static final DeferredItem<Item> backpack_pocket = reg("backpack_pocket", SimpleItem::new);

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static <T extends Item> DeferredItem<T> reg(String name, Supplier<T> value) {
      return reg.register(name, value);
   }
}
