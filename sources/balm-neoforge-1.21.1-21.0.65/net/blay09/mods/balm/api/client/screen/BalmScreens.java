package net.blay09.mods.balm.api.client.screen;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Deprecated
public interface BalmScreens {
   @Deprecated
   default <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(
      ResourceLocation identifier, Supplier<MenuType<? extends T>> type, BalmScreenFactory<T, S> screenFactory
   ) {
      this.registerScreen(type, screenFactory);
   }

   @Deprecated
   AbstractWidget addRenderableWidget(Screen var1, AbstractWidget var2);

   @Deprecated
   <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void registerScreen(Supplier<MenuType<? extends T>> var1, BalmScreenFactory<T, S> var2);

   @Deprecated
   BalmScreens scoped(String var1);
}
