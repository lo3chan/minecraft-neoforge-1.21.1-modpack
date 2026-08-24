package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class EntityTypeModuleNeoForge {
   public static final Map<AutoRegisterEntityType<? extends LivingEntity>, Supplier<Builder>> ENTITY_ATTRIBUTES = new HashMap<>();

   public static void processEntries() {
      YungsApiNeoForge.loadingContextEventBus
         .addListener(
            YungsApiNeoForge.buildAutoRegistrar(Registries.ENTITY_TYPE, AutoRegistrationManager.ENTITY_TYPES, EntityTypeModuleNeoForge::buildEntityType)
         );
      YungsApiNeoForge.loadingContextEventBus.addListener(EntityTypeModuleNeoForge::registerEntityAttributes);
   }

   private static EntityType<?> buildEntityType(AutoRegisterField data) {
      AutoRegisterEntityType autoRegisterEntityType = (AutoRegisterEntityType)data.object();
      EntityType<?> entityType = (EntityType<?>)autoRegisterEntityType.get();
      if (autoRegisterEntityType.hasAttributes()) {
         ENTITY_ATTRIBUTES.put(autoRegisterEntityType, autoRegisterEntityType.getAttributesSupplier());
      }

      return entityType;
   }

   private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
      ENTITY_ATTRIBUTES.forEach((entityType, builderSupplier) -> {
         Builder builder = builderSupplier.get();
         builder.add(NeoForgeMod.SWIM_SPEED.getDelegate()).add(NeoForgeMod.NAMETAG_DISTANCE.getDelegate());
         event.put((EntityType)entityType.get(), builder.build());
      });
   }
}
