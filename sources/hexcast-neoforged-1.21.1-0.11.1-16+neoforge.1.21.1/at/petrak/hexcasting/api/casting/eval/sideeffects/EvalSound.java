package at.petrak.hexcasting.api.casting.eval.sideeffects;

import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

public record EvalSound(@Nullable SoundEvent sound, int priority) {
   public EvalSound greaterOf(EvalSound that) {
      return this.priority > that.priority ? this : that;
   }
}
