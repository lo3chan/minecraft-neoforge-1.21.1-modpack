package at.petrak.hexcasting.common.casting.actions.rw

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.item.ItemStack

@SourceDebugExtension(["SMAP\nOpWritable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpWritable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpWritable\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,28:1\n299#2:29\n299#2:30\n299#2:31\n*S KotlinDebug\n*F\n+ 1 OpWritable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpWritable\n*L\n21#1:29\n23#1:30\n25#1:31\n*E\n"])
public object OpWritable : ConstMediaAction {
   public open val argc: Int

   public fun canWriteAny(datumHolder: ADIotaHolder): Boolean {
      return datumHolder.writeable();
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpWritable::execute$lambda$0);
      label11:
      if (var10000 == null) {
         return CollectionsKt.listOf(new BooleanIota(false));
      } else {
         val var11: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(var10000.component1());
         return if (var11 == null) CollectionsKt.listOf(new BooleanIota(false)) else CollectionsKt.listOf(new BooleanIota(this.canWriteAny(var11)));
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

   @JvmStatic
   fun `execute$lambda$0`(it: ItemStack): Boolean {
      val datumHolder: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(it);
      return datumHolder != null && INSTANCE.canWriteAny(datumHolder);
   }
}
