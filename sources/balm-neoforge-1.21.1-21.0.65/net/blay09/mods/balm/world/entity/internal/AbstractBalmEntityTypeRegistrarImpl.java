package net.blay09.mods.balm.world.entity.internal;

import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public abstract class AbstractBalmEntityTypeRegistrarImpl implements BalmEntityTypeRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   protected AbstractBalmEntityTypeRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   protected abstract <T extends Entity> void registerDefaultAttributes(
      Holder<EntityType<T>> var1, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> var2
   );

   protected abstract <T extends Entity> void registerSpawnPlacement(
      Holder<EntityType<T>> var1, SpawnPlacementType var2, Types var3, Supplier<SpawnPredicate<T>> var4
   );

   @Override
   public <T extends Entity> BalmEntityTypeRegistration<T> register(String name, Supplier<Builder<T>> builder) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<EntityType<?>> resourceKey = ResourceKey.create(Registries.ENTITY_TYPE, identifier);
      Holder<EntityType<?>> holder = this.registrar.register(resourceKey, id -> builder.get().build(resourceKey.location().toString()));
      return new AbstractBalmEntityTypeRegistrarImpl.BalmEntityTypeRegistrationImpl<>(holder);
   }

   @Override
   public void addAlias(ResourceLocation oldId, ResourceLocation newId) {
      this.registrar.addAlias(Registries.ENTITY_TYPE, oldId, newId);
   }

   @Override
   public void addAlias(String oldName, String newName) {
      this.addAlias(ResourceLocation.fromNamespaceAndPath(this.namespace, oldName), ResourceLocation.fromNamespaceAndPath(this.namespace, newName));
   }

   private class BalmEntityTypeRegistrationImpl<T extends Entity> implements BalmEntityTypeRegistration<T> {
      private final Holder<EntityType<T>> holder;

      private BalmEntityTypeRegistrationImpl(Holder<?> holder) {
         this.holder = (Holder<EntityType<T>>)holder;
      }

      @Override
      public Holder<EntityType<T>> asHolder() {
         return this.holder;
      }

      @Override
      public BalmEntityTypeRegistration<T> withDefaultAttributes(
         Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributesFunction
      ) {
         AbstractBalmEntityTypeRegistrarImpl.this.registerDefaultAttributes(this.holder, attributesFunction);
         return this;
      }

      @Override
      public BalmEntityTypeRegistration<T> withDefaultAttributes(
         Function<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder, net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributesFunction
      ) {
         AbstractBalmEntityTypeRegistrarImpl.this.registerDefaultAttributes(this.holder, () -> attributesFunction.apply(AttributeSupplier.builder()));
         return this;
      }

      @Override
      public BalmEntityTypeRegistration<T> withSpawnPlacement(SpawnPlacementType spawnPlacementType, Types heightmapType, Supplier<SpawnPredicate<T>> placement) {
         AbstractBalmEntityTypeRegistrarImpl.this.registerSpawnPlacement(this.holder, spawnPlacementType, heightmapType, placement);
         return this;
      }
   }
}
