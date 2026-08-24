package com.anthonyhilyard.iceberg.neoforge.services;

import com.anthonyhilyard.iceberg.services.IKeyMappingRegistrar;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class NeoForgeKeyMappingRegistrar implements IKeyMappingRegistrar {
   private static Set<KeyMapping> keyMappings = Sets.newHashSet();
   private static final IKeyConflictContext noConflictContext = new IKeyConflictContext() {
      public boolean isActive() {
         return true;
      }

      public boolean conflicts(IKeyConflictContext other) {
         return false;
      }
   };
   private static final HashMap<IKeyMappingRegistrar.KeyMappingContext, IKeyConflictContext> contextResolver = new HashMap<IKeyMappingRegistrar.KeyMappingContext, IKeyConflictContext>() {
      {
         this.put(IKeyMappingRegistrar.KeyMappingContext.UNIVERSAL, KeyConflictContext.UNIVERSAL);
         this.put(IKeyMappingRegistrar.KeyMappingContext.GUI, KeyConflictContext.GUI);
         this.put(IKeyMappingRegistrar.KeyMappingContext.IN_GAME, KeyConflictContext.IN_GAME);
         this.put(IKeyMappingRegistrar.KeyMappingContext.NO_CONFLICT, NeoForgeKeyMappingRegistrar.noConflictContext);
      }
   };

   @Override
   public KeyMapping registerMapping(KeyMapping mapping) {
      keyMappings.add(mapping);
      return mapping;
   }

   @Override
   public KeyMapping registerMapping(KeyMapping mapping, IKeyMappingRegistrar.KeyMappingContext context) {
      mapping.setKeyConflictContext(contextResolver.getOrDefault(context, KeyConflictContext.UNIVERSAL));
      keyMappings.add(mapping);
      return mapping;
   }

   @SubscribeEvent
   public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
      for (KeyMapping mapping : keyMappings) {
         event.register(mapping);
      }
   }
}
