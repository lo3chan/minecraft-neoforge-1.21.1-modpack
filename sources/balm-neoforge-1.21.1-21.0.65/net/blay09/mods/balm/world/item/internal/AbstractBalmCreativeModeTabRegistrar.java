package net.blay09.mods.balm.world.item.internal;

import java.util.function.BiFunction;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Builder;

public abstract class AbstractBalmCreativeModeTabRegistrar implements BalmCreativeModeTabRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   protected AbstractBalmCreativeModeTabRegistrar(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public BalmCreativeModeTabRegistration register(String name, BiFunction<ResourceLocation, Builder, Builder> constructor) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<CreativeModeTab> resourceKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, identifier);
      Holder<CreativeModeTab> holder = this.registrar.register(resourceKey, id -> constructor.apply(id, this.createBuilder()).build());
      return new AbstractBalmCreativeModeTabRegistrar.BalmCreativeModeTabRegistrationImpl(holder);
   }

   private record BalmCreativeModeTabRegistrationImpl(Holder<CreativeModeTab> holder) implements BalmCreativeModeTabRegistration {
      @Override
      public Holder<CreativeModeTab> asHolder() {
         return this.holder;
      }
   }
}
