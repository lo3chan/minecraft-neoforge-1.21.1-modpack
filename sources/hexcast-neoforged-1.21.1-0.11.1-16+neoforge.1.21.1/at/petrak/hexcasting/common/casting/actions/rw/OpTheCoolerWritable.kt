package at.petrak.hexcasting.common.casting.actions.rw

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity

@SourceDebugExtension(["SMAP\nOpTheCoolerWritable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpTheCoolerWritable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpTheCoolerWritable\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,28:1\n299#2:29\n299#2:30\n*S KotlinDebug\n*F\n+ 1 OpTheCoolerWritable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpTheCoolerWritable\n*L\n21#1:29\n25#1:30\n*E\n"])
public object OpTheCoolerWritable : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      env.assertEntityInRange(target);
      val var10000: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(target);
      return if (var10000 == null)
         CollectionsKt.listOf(new BooleanIota(false))
         else
         CollectionsKt.listOf(new BooleanIota(OpWritable.INSTANCE.canWriteAny(var10000)));
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
