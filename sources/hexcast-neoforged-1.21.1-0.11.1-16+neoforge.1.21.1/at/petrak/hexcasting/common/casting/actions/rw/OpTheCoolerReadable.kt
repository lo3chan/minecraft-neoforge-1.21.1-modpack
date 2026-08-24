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

@SourceDebugExtension(["SMAP\nOpTheCoolerReadable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpTheCoolerReadable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpTheCoolerReadable\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,30:1\n299#2:31\n299#2:32\n299#2:33\n*S KotlinDebug\n*F\n+ 1 OpTheCoolerReadable.kt\nat/petrak/hexcasting/common/casting/actions/rw/OpTheCoolerReadable\n*L\n21#1:31\n25#1:32\n27#1:33\n*E\n"])
public object OpTheCoolerReadable : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      env.assertEntityInRange(target);
      val var10000: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(target);
      label13:
      if (var10000 == null) {
         return CollectionsKt.listOf(new BooleanIota(false));
      } else {
         return if (var10000.readIota(env.getWorld()) == null && var10000.emptyIota() == null)
            CollectionsKt.listOf(new BooleanIota(false))
            else
            CollectionsKt.listOf(new BooleanIota(true));
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
