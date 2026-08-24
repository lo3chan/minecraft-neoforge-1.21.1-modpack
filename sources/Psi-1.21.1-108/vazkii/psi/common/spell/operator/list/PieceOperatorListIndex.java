package vazkii.psi.common.spell.operator.list;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorListIndex extends PieceOperator {
   SpellParam<EntityListWrapper> list;
   SpellParam<Number> number;

   public PieceOperatorListIndex(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.list = new ParamEntityListWrapper("psi.spellparam.list", 2805970, false, false));
      this.addParam(this.number = new ParamNumber("psi.spellparam.number", 7678674, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int num = this.getNotNullParamValue(context, this.number).intValue();
      EntityListWrapper listVal = this.getNotNullParamValue(context, this.list);
      if (num >= 0 && num < listVal.size()) {
         return listVal.get(num);
      } else {
         throw new SpellRuntimeException("psi.spellerror.out_of_bounds");
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }
}
