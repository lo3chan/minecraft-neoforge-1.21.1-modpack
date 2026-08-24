package vazkii.psi.common.spell.constant;

import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

public class PieceConstantE extends SpellPiece {
   public PieceConstantE(Spell spell) {
      super(spell);
   }

   @Override
   public EnumPieceType getPieceType() {
      return EnumPieceType.CONSTANT;
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object evaluate() {
      return 2.718281828459045;
   }

   @Override
   public Object execute(SpellContext context) {
      return this.evaluate();
   }
}
