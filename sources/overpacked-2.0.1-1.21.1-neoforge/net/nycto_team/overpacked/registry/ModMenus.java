package net.nycto_team.overpacked.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nycto_team.overpacked.menu.GiantBackpackMenu;

public class ModMenus {
   public static final DeferredRegister<MenuType<?>> reg = DeferredRegister.create(Registries.MENU, "overpacked");
   public static final Supplier<MenuType<GiantBackpackMenu>> giant_backpack = reg("giant_backpack", GiantBackpackMenu::new);

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> reg(String name, IContainerFactory<T> value) {
      return reg.register(name, () -> IMenuTypeExtension.create(value));
   }
}
