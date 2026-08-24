package at.petrak.hexcasting.api.casting

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage

public interface RenderedSpell {
   public abstract fun cast(env: CastingEnvironment) {
   }

   public open fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun cast(`$this`: RenderedSpell, env: CastingEnvironment, image: CastingImage): CastingImage? {
         `$this`.cast(env);
         return null;
      }
   }
}
