package dev.latvian.mods.kubejs.gui;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface KubeJSMenus {
   DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, "kubejs");
   Supplier<MenuType<KubeJSMenu>> MENU = REGISTRY.register("menu", () -> new MenuType(KubeJSMenu.FACTORY, FeatureFlags.VANILLA_SET));
}
