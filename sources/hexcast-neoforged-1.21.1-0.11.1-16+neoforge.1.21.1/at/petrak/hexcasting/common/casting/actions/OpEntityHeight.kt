package at.petrak.hexcasting.common.casting.actions

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity

@SourceDebugExtension(["SMAP\nOpEntityHeight.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpEntityHeight.kt\nat/petrak/hexcasting/common/casting/actions/OpEntityHeight\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,18:1\n301#2:19\n*S KotlinDebug\n*F\n+ 1 OpEntityHeight.kt\nat/petrak/hexcasting/common/casting/actions/OpEntityHeight\n*L\n15#1:19\n*E\n"])
public object OpEntityHeight : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val e: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      env.assertEntityInRange(e);
      return CollectionsKt.listOf(new DoubleIota((double)e.getBbHeight()));
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
