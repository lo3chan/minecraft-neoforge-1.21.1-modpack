package net.blay09.mods.balm.world.inventory;

import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface BalmMenuTypeRegistrar {
   <TMenu extends AbstractContainerMenu, TPayload> BalmMenuTypeRegistration<TMenu> register(String var1, BalmMenuFactory<TMenu, TPayload> var2);

   <TMenu extends AbstractContainerMenu, TPayload> MenuType<TMenu> createMenuType(BalmMenuFactory<TMenu, TPayload> var1);
}
