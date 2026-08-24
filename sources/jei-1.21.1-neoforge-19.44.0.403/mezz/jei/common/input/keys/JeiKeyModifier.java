package mezz.jei.common.input.keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public enum JeiKeyModifier {
   CONTROL_OR_COMMAND {
      @Override
      public boolean isActive(JeiKeyConflictContext context) {
         return Screen.hasControlDown();
      }

      @Override
      public Component getCombinedName(Component component) {
         return Minecraft.ON_OSX
            ? Component.translatable("jei.key.combo.command", new Object[]{component})
            : Component.translatable("jei.key.combo.control", new Object[]{component});
      }
   },
   SHIFT {
      @Override
      public boolean isActive(JeiKeyConflictContext context) {
         return Screen.hasShiftDown();
      }

      @Override
      public Component getCombinedName(Component component) {
         return Component.translatable("jei.key.combo.shift", new Object[]{component});
      }
   },
   ALT {
      @Override
      public boolean isActive(JeiKeyConflictContext context) {
         return Screen.hasAltDown();
      }

      @Override
      public Component getCombinedName(Component component) {
         return Component.translatable("jei.key.combo.alt", new Object[]{component});
      }
   },
   NONE {
      @Override
      public boolean isActive(JeiKeyConflictContext context) {
         return context.conflicts(JeiKeyConflictContext.IN_GAME)
            ? true
            : !CONTROL_OR_COMMAND.isActive(context) && !SHIFT.isActive(context) && !ALT.isActive(context);
      }

      @Override
      public Component getCombinedName(Component component) {
         return component;
      }
   };

   public abstract boolean isActive(JeiKeyConflictContext var1);

   public abstract Component getCombinedName(Component var1);
}
