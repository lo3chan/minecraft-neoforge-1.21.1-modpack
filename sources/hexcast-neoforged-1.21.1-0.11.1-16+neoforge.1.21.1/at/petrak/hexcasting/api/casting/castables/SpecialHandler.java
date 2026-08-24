package at.petrak.hexcasting.api.casting.castables;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface SpecialHandler {
   Action act();

   Component getName();

   @FunctionalInterface
   public interface Factory<T extends SpecialHandler> {
      @Nullable
      T tryMatch(HexPattern var1);
   }
}
