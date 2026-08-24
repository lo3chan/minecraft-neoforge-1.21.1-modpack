package net.blay09.mods.balm.core.component.internal;

import java.util.function.BiFunction;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BalmDataComponentTypeRegistrarImpl implements BalmDataComponentTypeRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public BalmDataComponentTypeRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public <T> BalmDataComponentTypeRegistration<T> register(String name, BiFunction<ResourceLocation, Builder<T>, Builder<T>> constructor) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<DataComponentType<?>> resourceKey = ResourceKey.create(Registries.DATA_COMPONENT_TYPE, identifier);
      Holder<DataComponentType<?>> holder = this.registrar.register(resourceKey, id -> constructor.apply(id, this.createBuilder()).build());
      return new BalmDataComponentTypeRegistrarImpl.BalmDataComponentTypeRegistrationImpl<>(holder);
   }

   @Override
   public void addAlias(ResourceLocation oldId, ResourceLocation newId) {
      this.registrar.addAlias(Registries.DATA_COMPONENT_TYPE, oldId, newId);
   }

   @Override
   public void addAlias(String oldName, String newName) {
      this.addAlias(ResourceLocation.fromNamespaceAndPath(this.namespace, oldName), ResourceLocation.fromNamespaceAndPath(this.namespace, newName));
   }

   @Override
   public <T> Builder<T> createBuilder() {
      return DataComponentType.builder();
   }

   private static final class BalmDataComponentTypeRegistrationImpl<T> implements BalmDataComponentTypeRegistration<T> {
      private final Holder<DataComponentType<T>> holder;

      private BalmDataComponentTypeRegistrationImpl(Holder<?> holder) {
         this.holder = (Holder<DataComponentType<T>>)holder;
      }

      @Override
      public Holder<DataComponentType<T>> asHolder() {
         return this.holder;
      }
   }
}
