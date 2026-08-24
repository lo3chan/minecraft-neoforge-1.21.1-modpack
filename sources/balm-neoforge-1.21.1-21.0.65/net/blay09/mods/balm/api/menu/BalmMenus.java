package net.blay09.mods.balm.api.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Deprecated
public interface BalmMenus {
   @Deprecated
   <TMenu extends AbstractContainerMenu, TPayload> DeferredObject<MenuType<TMenu>> registerMenu(ResourceLocation var1, BalmMenuFactory<TMenu, TPayload> var2);
}
