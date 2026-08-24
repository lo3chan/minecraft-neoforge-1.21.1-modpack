package at.petrak.hexcasting.common.casting.actions.selectors

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity

@SourceDebugExtension(["SMAP\nOpGetCaster.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpGetCaster.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetCaster\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,19:1\n310#2:20\n310#2:21\n*S KotlinDebug\n*F\n+ 1 OpGetCaster.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetCaster\n*L\n13#1:20\n16#1:21\n*E\n"])
public object OpGetCaster : ConstMediaAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
      if (ctx.getCaster() == null) {
         return CollectionsKt.listOf(new NullIota());
      } else {
         ctx.assertEntityInRange(ctx.getCaster() as Entity);
         val `$this$asActionResult$iv`: Entity = ctx.getCaster() as Entity;
         return CollectionsKt.listOf(if (`$this$asActionResult$iv` == null) new NullIota() else new EntityIota(`$this$asActionResult$iv`));
      }
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
