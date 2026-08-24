package vazkii.psi.common.spell.trick;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickEvaluate extends PieceTrick {
   SpellParam<SpellParam.Any> target;

   public PieceTrickEvaluate(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamAny("psi.spellparam.target", 2774482, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) {
   }

   @Override
   public Object execute(SpellContext context) {
      return null;
   }
}
