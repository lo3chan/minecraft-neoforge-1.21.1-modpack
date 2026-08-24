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

@SourceDebugExtension(["SMAP\nOpReadable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpReadable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpReadable\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,27:1\n299#2:28\n299#2:29\n299#2:30\n299#2:31\n*S KotlinDebug\n*F\n+ 1 OpReadable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpReadable\n*L\n15#1:28\n18#1:29\n22#1:30\n24#1:31\n*E\n"])
public object OpReadable : ConstMediaAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpReadable::execute$lambda$0);
      if (var10000 == null) {
         return CollectionsKt.listOf(new BooleanIota(false));
      } else {
         val var13: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(var10000.component1());
         label15:
         if (var13 == null) {
            return CollectionsKt.listOf(new BooleanIota(false));
         } else {
            return if (var13.readIota(env.getWorld()) == null && var13.emptyIota() == null)
               CollectionsKt.listOf(new BooleanIota(false))
               else
               CollectionsKt.listOf(new BooleanIota(true));
         }
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
      return IXplatAbstractions.INSTANCE.findDataHolder(it) != null;
   }
}
