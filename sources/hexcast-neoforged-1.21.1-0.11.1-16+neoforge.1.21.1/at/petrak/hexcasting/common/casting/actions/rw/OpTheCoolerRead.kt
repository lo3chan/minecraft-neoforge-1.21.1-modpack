package at.petrak.hexcasting.common.casting.actions.rw

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.entity.Entity

public object OpTheCoolerRead : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      env.assertEntityInRange(target);
      val var10000: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(target);
      if (var10000 == null) {
         throw MishapBadEntity.Companion.of(target, "iota.read");
      } else {
         var var6: Iota = var10000.readIota(env.getWorld());
         if (var6 == null) {
            var6 = var10000.emptyIota();
            if (var6 == null) {
               throw MishapBadEntity.Companion.of(target, "iota.read");
            }
         }

         return CollectionsKt.listOf(var6);
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
