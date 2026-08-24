package fuzs.puzzleslib.api.client.key.v1;

import net.minecraft.client.Minecraft;

public enum KeyActivationContext {
   UNIVERSAL,
   GAME,
   SCREEN;

   public boolean isActive() {
      return switch (this) {
         case UNIVERSAL -> true;
         case GAME -> Minecraft.getInstance().screen == null;
         case SCREEN -> Minecraft.getInstance().screen != null;
      };
   }

   public boolean isConflictingWith(KeyActivationContext other) {
      return this == UNIVERSAL || other == UNIVERSAL || this == other;
   }
}
