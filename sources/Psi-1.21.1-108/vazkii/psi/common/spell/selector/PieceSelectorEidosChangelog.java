package vazkii.psi.common.spell.selector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.common.core.handler.PlayerDataHandler;

public class PieceSelectorEidosChangelog extends PieceSelector {
   SpellParam<Number> number;

   public PieceSelectorEidosChangelog(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.number = new ParamNumber("psi.spellparam.number", 2774482, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int i = this.getParamValue(context, this.number).intValue();
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(context.caster);
      if (i > 0 && i <= data.eidosChangelog.size()) {
         Vector3 vec = data.eidosChangelog.get(data.eidosChangelog.size() - i);
         if (vec == null) {
            throw new SpellRuntimeException("psi.spellerror.nullvector");
         } else {
            return vec;
         }
      } else {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}
