package me.shedaniel.clothconfig2.api;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import me.shedaniel.clothconfig2.impl.ModifierKeyCodeImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public interface ModifierKeyCode {
   static ModifierKeyCode of(Key keyCode, Modifier modifier) {
      return new ModifierKeyCodeImpl().setKeyCodeAndModifier(keyCode, modifier);
   }

   static ModifierKeyCode copyOf(ModifierKeyCode code) {
      return of(code.getKeyCode(), code.getModifier());
   }

   static ModifierKeyCode unknown() {
      return of(InputConstants.UNKNOWN, Modifier.none());
   }

   Key getKeyCode();

   ModifierKeyCode setKeyCode(Key var1);

   default Type getType() {
      return this.getKeyCode().getType();
   }

   Modifier getModifier();

   ModifierKeyCode setModifier(Modifier var1);

   default ModifierKeyCode copy() {
      return copyOf(this);
   }

   default boolean matchesMouse(int button) {
      return !this.isUnknown() && this.getType() == Type.MOUSE && this.getKeyCode().getValue() == button && this.getModifier().matchesCurrent();
   }

   default boolean matchesKey(int keyCode, int scanCode) {
      if (this.isUnknown()) {
         return false;
      } else {
         return keyCode == InputConstants.UNKNOWN.getValue()
            ? this.getType() == Type.SCANCODE && this.getKeyCode().getValue() == scanCode && this.getModifier().matchesCurrent()
            : this.getType() == Type.KEYSYM && this.getKeyCode().getValue() == keyCode && this.getModifier().matchesCurrent();
      }
   }

   default boolean matchesCurrentMouse() {
      return !this.isUnknown() && this.getType() == Type.MOUSE && this.getModifier().matchesCurrent()
         ? GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), this.getKeyCode().getValue()) == 1
         : false;
   }

   default boolean matchesCurrentKey() {
      return !this.isUnknown()
         && this.getType() == Type.KEYSYM
         && this.getModifier().matchesCurrent()
         && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), this.getKeyCode().getValue());
   }

   default ModifierKeyCode setKeyCodeAndModifier(Key keyCode, Modifier modifier) {
      this.setKeyCode(keyCode);
      this.setModifier(modifier);
      return this;
   }

   default ModifierKeyCode clearModifier() {
      return this.setModifier(Modifier.none());
   }

   @Override
   String toString();

   Component getLocalizedName();

   default boolean isUnknown() {
      return this.getKeyCode().equals(InputConstants.UNKNOWN);
   }
}
