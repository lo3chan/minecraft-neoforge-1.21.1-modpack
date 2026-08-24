package net.blay09.mods.balm.api.client.keymappings;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Deprecated
public class KeyModifiers {
   private final EnumSet<KeyModifier> modifiers = EnumSet.noneOf(KeyModifier.class);
   private final List<Key> customModifiers = new ArrayList<>();

   private KeyModifiers(KeyModifier... modifiers) {
      this.modifiers.addAll(Arrays.asList(modifiers));
   }

   public static KeyModifiers of(KeyModifier... modifiers) {
      return new KeyModifiers(modifiers);
   }

   public static KeyModifiers ofCustom(Key... modifiers) {
      KeyModifiers keyModifiers = new KeyModifiers();

      for (Key modifier : modifiers) {
         keyModifiers.addCustomModifier(modifier);
      }

      return keyModifiers;
   }

   public boolean contains(KeyModifier keyModifier) {
      return this.modifiers.contains(keyModifier);
   }

   public int size() {
      return this.modifiers.size();
   }

   public boolean isEmpty() {
      return this.modifiers.isEmpty();
   }

   public KeyModifiers addCustomModifier(Key key) {
      this.customModifiers.add(key);
      return this;
   }

   public KeyModifiers addCustomModifier(int keyCode) {
      this.customModifiers.add(Type.KEYSYM.getOrCreate(keyCode));
      return this;
   }

   public List<Key> getCustomModifiers() {
      return this.customModifiers;
   }

   public List<KeyModifier> asList() {
      return new ArrayList<>(this.modifiers);
   }

   public boolean hasCustomModifiers() {
      return !this.customModifiers.isEmpty();
   }
}
