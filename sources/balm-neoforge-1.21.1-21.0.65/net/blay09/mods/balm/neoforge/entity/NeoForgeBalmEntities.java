package net.blay09.mods.balm.neoforge.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.entity.BalmEntities;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record NeoForgeBalmEntities(NamespaceResolver namespaceResolver) implements BalmEntities {
   @Override
   public <T extends Entity> DeferredObject<EntityType<T>> registerEntity(ResourceLocation identifier, Builder<T> typeBuilder) {
      DeferredRegister<EntityType<?>> register = DeferredRegisters.get(Registries.ENTITY_TYPE, identifier.getNamespace());
      DeferredHolder<EntityType<?>, EntityType<T>> registryObject = register.register(identifier.getPath(), () -> typeBuilder.build(identifier.toString()));
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public <T extends LivingEntity> DeferredObject<EntityType<T>> registerEntity(
      ResourceLocation identifier, Builder<T> typeBuilder, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributeBuilder
   ) {
      DeferredRegister<EntityType<?>> register = DeferredRegisters.get(Registries.ENTITY_TYPE, identifier.getNamespace());
      NeoForgeBalmEntities.Registrations registrations = this.getActiveRegistrations();
      DeferredHolder<EntityType<?>, EntityType<T>> registryObject = register.register(identifier.getPath(), () -> {
         EntityType<T> entityType = typeBuilder.build(identifier.toString());
         registrations.attributeSuppliers.put(entityType, attributeBuilder.get().build());
         return entityType;
      });
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   private NeoForgeBalmEntities.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmEntities.Registrations.class);
   }

   public static class Registrations {
      public final Map<EntityType<? extends LivingEntity>, AttributeSupplier> attributeSuppliers = new HashMap<>();

      @SubscribeEvent
      public void registerAttributes(EntityAttributeCreationEvent event) {
         for (Entry<EntityType<? extends LivingEntity>, AttributeSupplier> entry : this.attributeSuppliers.entrySet()) {
            event.put(entry.getKey(), entry.getValue());
         }
      }
   }
}
