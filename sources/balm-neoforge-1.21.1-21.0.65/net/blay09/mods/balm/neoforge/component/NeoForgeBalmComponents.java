package net.blay09.mods.balm.neoforge.component;

import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.component.BalmComponents;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgeBalmComponents implements BalmComponents {
   @Override
   public <TComponent> DeferredObject<DataComponentType<TComponent>> registerComponent(
      Supplier<DataComponentType<TComponent>> supplier, ResourceLocation identifier
   ) {
      DeferredRegister<DataComponentType<?>> register = DeferredRegisters.get(Registries.DATA_COMPONENT_TYPE, identifier.getNamespace());
      DeferredHolder<DataComponentType<?>, DataComponentType<TComponent>> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }
}
