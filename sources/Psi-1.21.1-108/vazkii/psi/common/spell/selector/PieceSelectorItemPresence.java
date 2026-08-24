package vazkii.psi.common.spell.selector;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorItemPresence extends PieceSelector {
   SpellParam<Number> slot;

   public PieceSelectorItemPresence(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.slot = new ParamNumber("psi.spellparam.slot", 2774482, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Number slotVal = this.getParamValue(context, this.slot);
      int invSlot = (slotVal == null ? context.getTargetSlot() : Math.abs(slotVal.intValue() - 1)) % context.caster.getInventory().items.size();
      ItemStack stack = context.caster.getInventory().getItem(invSlot);
      return (double)stack.getCount();
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}
