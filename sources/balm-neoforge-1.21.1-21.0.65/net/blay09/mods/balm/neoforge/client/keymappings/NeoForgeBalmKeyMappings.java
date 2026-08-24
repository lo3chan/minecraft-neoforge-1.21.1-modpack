package net.blay09.mods.balm.neoforge.client.keymappings;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.keymappings.KeyConflictContext;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.balm.api.client.keymappings.KeyModifiers;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.common.StaticNamespaceResolver;
import net.blay09.mods.balm.common.client.keymappings.CommonBalmKeyMappings;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.jetbrains.annotations.Nullable;

public class NeoForgeBalmKeyMappings extends CommonBalmKeyMappings {
   public NeoForgeBalmKeyMappings(NamespaceResolver namespaceResolver) {
      super(namespaceResolver);
   }

   @Override
   public BalmKeyMappings scoped(String modId) {
      return new NeoForgeBalmKeyMappings(new StaticNamespaceResolver(modId));
   }

   private static IKeyConflictContext toForge(KeyConflictContext context) {
      return switch (context) {
         case UNIVERSAL -> net.neoforged.neoforge.client.settings.KeyConflictContext.UNIVERSAL;
         case GUI -> net.neoforged.neoforge.client.settings.KeyConflictContext.GUI;
         case INGAME -> net.neoforged.neoforge.client.settings.KeyConflictContext.IN_GAME;
      };
   }

   private static net.neoforged.neoforge.client.settings.KeyModifier toForge(KeyModifier modifier) {
      return switch (modifier) {
         case SHIFT -> net.neoforged.neoforge.client.settings.KeyModifier.SHIFT;
         case CONTROL -> net.neoforged.neoforge.client.settings.KeyModifier.CONTROL;
         case ALT -> net.neoforged.neoforge.client.settings.KeyModifier.ALT;
         default -> net.neoforged.neoforge.client.settings.KeyModifier.NONE;
      };
   }

   @Override
   public KeyMapping registerKeyMapping(String name, KeyConflictContext conflictContext, KeyModifier modifier, Type type, int keyCode, String category) {
      KeyMapping keyMapping = new KeyMapping(name, toForge(conflictContext), toForge(modifier), type, keyCode, category);
      this.getActiveRegistrations().keyMappings.add(keyMapping);
      return keyMapping;
   }

   @Override
   public KeyMapping registerKeyMapping(String name, KeyConflictContext conflictContext, KeyModifiers modifiers, Type type, int keyCode, String category) {
      List<KeyModifier> keyModifiers = modifiers.asList();
      KeyModifier mainModifier = !keyModifiers.isEmpty() ? keyModifiers.get(0) : KeyModifier.NONE;
      KeyMapping keyMapping = new KeyMapping(name, toForge(conflictContext), toForge(mainModifier), type, keyCode, category);
      this.getActiveRegistrations().keyMappings.add(keyMapping);
      if (keyModifiers.size() > 1) {
         this.registerModifierKeyMappings(keyMapping, conflictContext, keyModifiers.subList(1, keyModifiers.size()));
      }

      if (modifiers.hasCustomModifiers()) {
         this.registerCustomModifierKeyMappings(keyMapping, conflictContext, modifiers.getCustomModifiers());
      }

      return keyMapping;
   }

   @Override
   public boolean isActiveAndMatches(@Nullable KeyMapping keyMapping, Key input) {
      return this.isActive(keyMapping) && keyMapping.isActiveAndMatches(input);
   }

   @Override
   public boolean isActiveAndMatches(@Nullable KeyMapping keyMapping, int keyCode, int scanCode) {
      return this.isActive(keyMapping) && keyMapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
   }

   @Override
   public boolean isActiveAndMatches(@Nullable KeyMapping keyMapping, Type type, int keyCode, int scanCode) {
      if (!this.isActive(keyMapping)) {
         return false;
      } else {
         return type == Type.MOUSE
            ? keyMapping.isActiveAndMatches(Type.MOUSE.getOrCreate(keyCode))
            : keyMapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
      }
   }

   private boolean isActiveAndMatchesStrictModifier(@Nullable KeyMapping keyMapping, int keyCode, int scanCode) {
      if (!this.isActive(keyMapping)) {
         return false;
      } else {
         return keyMapping.getKeyModifier() != net.neoforged.neoforge.client.settings.KeyModifier.NONE
               || !net.neoforged.neoforge.client.settings.KeyModifier.SHIFT.isActive(keyMapping.getKeyConflictContext())
                  && !net.neoforged.neoforge.client.settings.KeyModifier.CONTROL.isActive(keyMapping.getKeyConflictContext())
                  && !net.neoforged.neoforge.client.settings.KeyModifier.ALT.isActive(keyMapping.getKeyConflictContext())
            ? keyMapping.matches(keyCode, scanCode)
            : false;
      }
   }

   @Override
   protected boolean isContextActive(KeyMapping keyMapping) {
      return keyMapping.getKeyConflictContext().isActive();
   }

   private NeoForgeBalmKeyMappings.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmKeyMappings.Registrations.class);
   }

   public static class Registrations {
      public final List<KeyMapping> keyMappings = new ArrayList<>();

      @SubscribeEvent
      public void registerKeyMappings(RegisterKeyMappingsEvent event) {
         this.keyMappings.forEach(event::register);
      }
   }
}
