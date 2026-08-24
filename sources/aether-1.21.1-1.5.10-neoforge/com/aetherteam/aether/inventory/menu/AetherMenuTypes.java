package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.client.gui.screen.inventory.AltarScreen;
import com.aetherteam.aether.client.gui.screen.inventory.FreezerScreen;
import com.aetherteam.aether.client.gui.screen.inventory.IncubatorScreen;
import com.aetherteam.aether.client.gui.screen.inventory.LoreBookScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherMenuTypes {
   public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, "aether");
   public static final DeferredHolder<MenuType<?>, MenuType<AetherAccessoriesMenu>> ACCESSORIES = register("accessories", AetherAccessoriesMenu::new);
   public static final DeferredHolder<MenuType<?>, MenuType<LoreBookMenu>> BOOK_OF_LORE = register("book_of_lore", LoreBookMenu::new);
   public static final DeferredHolder<MenuType<?>, MenuType<AltarMenu>> ALTAR = register("altar", AltarMenu::new);
   public static final DeferredHolder<MenuType<?>, MenuType<FreezerMenu>> FREEZER = register("freezer", FreezerMenu::new);
   public static final DeferredHolder<MenuType<?>, MenuType<IncubatorMenu>> INCUBATOR = register("incubator", IncubatorMenu::new);

   private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, MenuSupplier<T> menu) {
      return MENU_TYPES.register(name, () -> new MenuType(menu, FeatureFlags.VANILLA_SET));
   }

   public static void registerMenuScreens(RegisterMenuScreensEvent event) {
      event.register((MenuType)ACCESSORIES.get(), AetherAccessoriesScreen::new);
      event.register((MenuType)BOOK_OF_LORE.get(), LoreBookScreen::new);
      event.register((MenuType)ALTAR.get(), AltarScreen::new);
      event.register((MenuType)FREEZER.get(), FreezerScreen::new);
      event.register((MenuType)INCUBATOR.get(), IncubatorScreen::new);
   }
}
