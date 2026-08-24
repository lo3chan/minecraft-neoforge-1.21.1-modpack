package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.world.inventory.BlackpetalblockMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UndeadRevamp2ModMenus {
   public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, "undead_revamp2");
   public static final DeferredHolder<MenuType<?>, MenuType<BlackpetalblockMenu>> BLACKPETALBLOCK = REGISTRY.register(
      "blackpetalblock", () -> IMenuTypeExtension.create(BlackpetalblockMenu::new)
   );
}
