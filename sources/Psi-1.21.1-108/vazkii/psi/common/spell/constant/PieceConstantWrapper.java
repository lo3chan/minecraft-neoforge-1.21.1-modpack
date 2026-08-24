package vazkii.psi.common.spell.constant;

import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;

public class PieceConstantWrapper extends SpellPiece {
   SpellParam<Number> target;
   SpellParam<Number> max;
   boolean evaluating = false;

   public PieceConstantWrapper(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamNumber("psi.spellparam.target", 13773354, false, false));
      this.addParam(this.max = new ParamNumber("psi.spellparam.constant", 4117034, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double targetVal = this.getParamValue(context, this.target).doubleValue();
      double maxVal = this.getParamValue(context, this.max).doubleValue();
      if (maxVal > 0.0) {
         return Math.min(maxVal, Math.abs(targetVal));
      } else {
         return maxVal < 0.0 ? Math.max(maxVal, -Math.abs(targetVal)) : 0.0;
      }
   }

   @Override
   public Object evaluate() throws SpellCompilationException {
      if (this.evaluating) {
         return 0.0;
      } else {
         this.evaluating = true;
         Object ret = this.getParamEvaluation(this.max);
         this.evaluating = false;
         return ret;
      }
   }

   @Override
   public EnumPieceType getPieceType() {
      return EnumPieceType.CONSTANT;
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}
