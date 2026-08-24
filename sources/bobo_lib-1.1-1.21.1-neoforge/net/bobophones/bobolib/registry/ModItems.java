package net.bobophones.bobolib.registry;

import java.util.function.Supplier;
import net.bobophones.bobolib.item.DevTool;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ModItems {
   public static final Items reg = DeferredRegister.createItems("bobo_lib");
   public static final DeferredItem<Item> dev_tool = reg("dev_tool", DevTool::new);

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static <T extends Item> DeferredItem<T> reg(String name, Supplier<T> value) {
      return reg.register(name, value);
   }
}
