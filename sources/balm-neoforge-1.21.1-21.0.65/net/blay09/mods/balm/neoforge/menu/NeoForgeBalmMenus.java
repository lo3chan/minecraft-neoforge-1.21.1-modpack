package net.blay09.mods.balm.neoforge.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgeBalmMenus implements BalmMenus {
   @Override
   public <TMenu extends AbstractContainerMenu, TPayload> DeferredObject<MenuType<TMenu>> registerMenu(
      ResourceLocation identifier, BalmMenuFactory<TMenu, TPayload> factory
   ) {
      DeferredRegister<MenuType<?>> register = DeferredRegisters.get(Registries.MENU, identifier.getNamespace());
      DeferredHolder<MenuType<?>, MenuType<TMenu>> registryObject = register.register(
         identifier.getPath(),
         () -> new MenuType(
            (IContainerFactory)(syncId, inventory, buf) -> factory.create(syncId, inventory, (TPayload)factory.getStreamCodec().decode(buf)),
            FeatureFlagSet.of(FeatureFlags.VANILLA)
         )
      );
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }
}
