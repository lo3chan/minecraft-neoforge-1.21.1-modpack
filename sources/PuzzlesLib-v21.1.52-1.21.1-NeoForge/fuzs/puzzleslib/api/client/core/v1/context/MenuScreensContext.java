package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@FunctionalInterface
public interface MenuScreensContext {
   <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> var1, ScreenConstructor<M, S> var2);
}
