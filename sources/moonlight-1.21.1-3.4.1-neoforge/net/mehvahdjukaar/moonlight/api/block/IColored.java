package net.mehvahdjukaar.moonlight.api.block;

import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

public interface IColored {
   @Nullable
   DyeColor getColor();

   default boolean supportsBlankColor() {
      return false;
   }
}
