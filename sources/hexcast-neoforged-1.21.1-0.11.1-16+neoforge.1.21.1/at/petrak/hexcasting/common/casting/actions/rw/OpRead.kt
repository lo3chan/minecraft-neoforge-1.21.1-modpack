package at.petrak.hexcasting.common.casting.actions.rw

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack

public object OpRead : ConstMediaAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpRead::execute$lambda$0);
      if (var10000 == null) {
         return CollectionsKt.listOf(new NullIota());
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         val var8: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(handStack);
         if (var8 == null) {
            val var11: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var11.of(handStack, hand, "iota.read");
         } else {
            var var9: Iota = var8.readIota(env.getWorld());
            if (var9 == null) {
               var9 = var8.emptyIota();
               if (var9 == null) {
                  val var10: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
                  throw var10.of(handStack, hand, "iota.read");
               }
            }

            return CollectionsKt.listOf(var9);
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
   fun `execute$lambda$0`(`$env`: CastingEnvironment, it: ItemStack): Boolean {
      val dataHolder: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(it);
      return dataHolder != null && (dataHolder.readIota(`$env`.getWorld()) != null || dataHolder.emptyIota() != null);
   }
}
