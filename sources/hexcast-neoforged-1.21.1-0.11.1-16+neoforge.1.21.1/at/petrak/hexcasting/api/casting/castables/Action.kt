package at.petrak.hexcasting.api.casting.castables

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import java.text.DecimalFormat
import net.minecraft.world.phys.Vec3

public interface Action {
   public abstract fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
   }

   public companion object {
      public const val RAYCAST_DISTANCE: Double = 32.0
      public final val DOUBLE_FORMATTER: DecimalFormat = new DecimalFormat("####.####")

      public fun raycastEnd(origin: Vec3, look: Vec3): Vec3 {
         val var10000: Vec3 = origin.add(look.normalize().scale(32.0));
         return var10000;
      }

      public fun makeConstantOp(x: Iota): Action {
         return new ConstMediaAction(x) {
            {
               this.$x = `$x`;
            }

            @Override
            public int getArgc() {
               return 0;
            }

            @Override
            public java.util.List<Iota> execute(java.util.List<? extends Iota> args, CastingEnvironment env) {
               return CollectionsKt.listOf(this.$x);
            }

            @Override
            public long getMediaCost() {
               return ConstMediaAction.DefaultImpls.getMediaCost(this);
            }

            @Override
            public ConstMediaAction.CostMediaActionResult executeWithOpCount(java.util.List<? extends Iota> args, CastingEnvironment env) {
               return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
            }

            @Override
            public OperationResult operate(CastingEnvironment env, CastingImage image, SpellContinuation continuation) {
               return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
            }
         };
      }
   }
}
