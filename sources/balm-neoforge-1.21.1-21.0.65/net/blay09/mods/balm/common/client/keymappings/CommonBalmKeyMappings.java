package net.blay09.mods.balm.common.client.keymappings;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.keymappings.KeyConflictContext;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.balm.api.client.keymappings.KeyModifiers;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class CommonBalmKeyMappings implements BalmKeyMappings {
   protected final NamespaceResolver namespaceResolver;
   private static final Set<KeyMapping> ignoreConflicts = Sets.newConcurrentHashSet();
   private static final Map<KeyMapping, Set<KeyMapping>> multiModifierKeyMappings = new ConcurrentHashMap<>();

   public CommonBalmKeyMappings(NamespaceResolver namespaceResolver) {
      this.namespaceResolver = namespaceResolver;
   }

   @Override
   public KeyMapping registerKeyMapping(String name, int keyCode, String category) {
      return this.registerKeyMapping(name, KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Type.KEYSYM, keyCode, category);
   }

   @Override
   public KeyMapping registerKeyMapping(String name, Type type, int keyCode, String category) {
      return this.registerKeyMapping(name, KeyConflictContext.UNIVERSAL, KeyModifier.NONE, type, keyCode, category);
   }

   @Override
   public KeyMapping registerKeyMapping(String name, KeyConflictContext conflictContext, KeyModifier modifier, int keyCode, String category) {
      return this.registerKeyMapping(name, conflictContext, modifier, Type.KEYSYM, keyCode, category);
   }

   @Override
   public KeyMapping registerKeyMapping(String name, KeyConflictContext conflictContext, KeyModifiers modifiers, int keyCode, String category) {
      return this.registerKeyMapping(name, conflictContext, modifiers, Type.KEYSYM, keyCode, category);
   }

   protected void registerModifierKeyMappings(KeyMapping baseMapping, KeyConflictContext conflictContext, List<KeyModifier> keyModifiers) {
      for (int i = 0; i < keyModifiers.size(); i++) {
         String subName = i > 0 ? baseMapping.getName() + "_modifier_" + i : baseMapping.getName() + "_modifier";
         KeyMapping subKeyMapping = this.registerKeyMapping(
            subName, conflictContext, KeyModifier.NONE, Type.KEYSYM, this.toKeyCode(keyModifiers.get(i)), baseMapping.getCategory()
         );
         multiModifierKeyMappings.computeIfAbsent(baseMapping, it -> new HashSet<>()).add(subKeyMapping);
         ignoreConflicts.add(subKeyMapping);
      }
   }

   protected void registerCustomModifierKeyMappings(KeyMapping baseMapping, KeyConflictContext conflictContext, List<Key> keyModifiers) {
      for (int i = 0; i < keyModifiers.size(); i++) {
         String subName = i > 0 ? baseMapping.getName() + "_modifier_" + i : baseMapping.getName() + "_modifier";
         KeyMapping subKeyMapping = this.registerKeyMapping(
            subName, conflictContext, KeyModifier.NONE, Type.KEYSYM, keyModifiers.get(i).getValue(), baseMapping.getCategory()
         );
         multiModifierKeyMappings.computeIfAbsent(baseMapping, it -> new HashSet<>()).add(subKeyMapping);
         ignoreConflicts.add(subKeyMapping);
      }
   }

   private int toKeyCode(KeyModifier keyModifier) {
      return switch (keyModifier) {
         case SHIFT -> 340;
         case CONTROL -> 341;
         case ALT -> 342;
         default -> -1;
      };
   }

   protected boolean areModifiersActive(KeyMapping keyMapping) {
      for (KeyMapping modifierMapping : multiModifierKeyMappings.getOrDefault(keyMapping, Collections.emptySet())) {
         if ((!modifierMapping.matches(340, 0) && !modifierMapping.matches(344, 0) || !Screen.hasShiftDown())
            && (!modifierMapping.matches(341, 0) && !modifierMapping.matches(345, 0) || !Screen.hasControlDown())
            && (!modifierMapping.matches(342, 0) && !modifierMapping.matches(346, 0) || !Screen.hasAltDown())
            && !this.isActiveAndKeyDown(modifierMapping)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isActiveAndKeyDown(@Nullable KeyMapping keyMapping) {
      if (!this.isActive(keyMapping)) {
         return false;
      } else {
         Key key = ((KeyMappingAccessor)keyMapping).getKey();
         return keyMapping.isDown()
            || key.getValue() != -1
               && key.getType() == Type.KEYSYM
               && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getValue());
      }
   }

   @Override
   public boolean isKeyDownIgnoreContext(@Nullable KeyMapping keyMapping) {
      if (!this.isActiveIgnoreContext(keyMapping)) {
         return false;
      } else {
         Key key = ((KeyMappingAccessor)keyMapping).getKey();
         return keyMapping.isDown()
            || key.getValue() != -1
               && key.getType() == Type.KEYSYM
               && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getValue());
      }
   }

   @Override
   public boolean isActiveAndWasPressed(@Nullable KeyMapping keyMapping) {
      return this.isActive(keyMapping) && keyMapping.consumeClick();
   }

   @Contract("null -> false")
   protected boolean isActive(@Nullable KeyMapping keyMapping) {
      return keyMapping == null ? false : this.isContextActive(keyMapping) && this.areModifiersActive(keyMapping);
   }

   @Contract("null -> false")
   protected boolean isActiveIgnoreContext(@Nullable KeyMapping keyMapping) {
      return keyMapping == null ? false : this.areModifiersActive(keyMapping);
   }

   @Override
   public boolean isActiveAndMatches(@Nullable KeyMapping keyMapping, Key input) {
      if (!this.isActive(keyMapping)) {
         return false;
      } else {
         return input.getType() == Type.MOUSE
            ? keyMapping.matchesMouse(input.getValue())
            : keyMapping.matches(
               input.getType() == Type.KEYSYM ? input.getValue() : InputConstants.UNKNOWN.getValue(),
               input.getType() == Type.SCANCODE ? input.getValue() : InputConstants.UNKNOWN.getValue()
            );
      }
   }

   @Override
   public boolean isActiveAndMatches(@Nullable KeyMapping keyMapping, int keyCode, int scanCode) {
      return this.isActive(keyMapping) && keyMapping.matches(keyCode, scanCode);
   }

   @Override
   public boolean isActiveAndMatches(@Nullable KeyMapping keyMapping, Type type, int keyCode, int scanCode) {
      return this.isActive(keyMapping) && (type == Type.MOUSE ? keyMapping.matchesMouse(keyCode) : keyMapping.matches(keyCode, scanCode));
   }

   @Override
   public Optional<Boolean> conflictsWith(KeyMapping first, KeyMapping second) {
      return !ignoreConflicts.contains(first) && !ignoreConflicts.contains(second) ? Optional.empty() : Optional.of(false);
   }

   @Override
   public void ignoreConflicts(KeyMapping keyMapping) {
      ignoreConflicts.add(keyMapping);
      ignoreConflicts.addAll(multiModifierKeyMappings.getOrDefault(keyMapping, Collections.emptySet()));
   }

   @Override
   public boolean shouldIgnoreConflicts(KeyMapping keyMapping) {
      return ignoreConflicts.contains(keyMapping);
   }

   protected abstract boolean isContextActive(KeyMapping var1);

   protected boolean isContextActive(KeyConflictContext conflictContext) {
      return switch (conflictContext) {
         case GUI -> Minecraft.getInstance().screen != null;
         case INGAME -> Minecraft.getInstance().screen == null;
         default -> true;
      };
   }
}
