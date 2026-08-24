package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import it.unimi.dsi.fastutil.booleans.BooleanList
import java.util.ArrayList
import net.minecraft.resources.ResourceLocation

public class OpMask(mask: BooleanList, key: ResourceLocation) : ConstMediaAction {
   public final val mask: BooleanList
   public final val key: ResourceLocation

   public open val argc: Int
      public open get() {
         return this.mask.size();
      }


   init {
      this.mask = mask;
      this.key = key;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val out: ArrayList = new ArrayList(this.mask.size());
      val var4: java.util.Iterator = (this.mask as java.lang.Iterable).iterator();
      var var5: Int = 0;

      while (var4.hasNext()) {
         val i: Int = var5++;
         if (var4.next() as java.lang.Boolean) {
            out.add(args.get(i));
         }
      }

      return out;
   }

   override fun getMediaCost(): Long {
      return ConstMediaAction.DefaultImpls.getMediaCost(this);
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }
}
