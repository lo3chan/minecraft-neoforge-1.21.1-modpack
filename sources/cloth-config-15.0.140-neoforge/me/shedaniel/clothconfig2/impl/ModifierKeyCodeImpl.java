package me.shedaniel.clothconfig2.impl;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModifierKeyCodeImpl implements ModifierKeyCode {
   private Key keyCode;
   private Modifier modifier;

   @Override
   public Key getKeyCode() {
      return this.keyCode;
   }

   @Override
   public Modifier getModifier() {
      return this.modifier;
   }

   @Override
   public ModifierKeyCode setKeyCode(Key keyCode) {
      this.keyCode = keyCode.getType().getOrCreate(keyCode.getValue());
      if (keyCode.equals(InputConstants.UNKNOWN)) {
         this.setModifier(Modifier.none());
      }

      return this;
   }

   @Override
   public ModifierKeyCode setModifier(Modifier modifier) {
      this.modifier = Modifier.of(modifier.getValue());
      return this;
   }

   @Override
   public String toString() {
      return this.getLocalizedName().getString();
   }

   @Override
   public Component getLocalizedName() {
      Component base = this.keyCode.getDisplayName();
      if (this.modifier.hasShift()) {
         base = Component.translatable("modifier.cloth-config.shift", new Object[]{base});
      }

      if (this.modifier.hasControl()) {
         base = Component.translatable("modifier.cloth-config.ctrl", new Object[]{base});
      }

      if (this.modifier.hasAlt()) {
         base = Component.translatable("modifier.cloth-config.alt", new Object[]{base});
      }

      return base;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof ModifierKeyCode that) ? false : this.keyCode.equals(that.getKeyCode()) && this.modifier.equals(that.getModifier());
      }
   }

   @Override
   public int hashCode() {
      int result = this.keyCode != null ? this.keyCode.hashCode() : 0;
      return 31 * result + (this.modifier != null ? this.modifier.hashCode() : 0);
   }
}
