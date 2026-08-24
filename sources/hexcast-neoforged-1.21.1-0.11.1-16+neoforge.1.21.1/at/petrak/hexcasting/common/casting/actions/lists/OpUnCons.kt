package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota

public object OpUnCons : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: SpellList = OperatorUtils.getList(args, 0, this.getArgc());
      return if (list.getNonEmpty())
         CollectionsKt.listOf(new Iota[]{new ListIota(list.getCdr()), list.getCar()})
         else
         CollectionsKt.listOf(new Iota[]{(Iota)args.get(0), new NullIota()});
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
