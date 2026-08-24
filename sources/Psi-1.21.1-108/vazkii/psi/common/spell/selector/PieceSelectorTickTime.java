package vazkii.psi.common.spell.selector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorTickTime extends PieceSelector {
   public PieceSelectorTickTime(Spell spell) {
      super(spell);
   }

   public static double getMspt(SpellContext context) {
      if (context.focalPoint.getServer() == null) {
         return 0.0;
      } else {
         long[] tickTimes = context.focalPoint.getServer().getTickTime(context.focalPoint.level().dimension());
         return tickTimes == null ? 0.0 : mean(tickTimes) * 1.0E-6;
      }
   }

   private static long mean(long[] values) {
      long sum = 0L;

      for (long val : values) {
         sum += val;
      }

      return sum / values.length;
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object execute(SpellContext context) {
      return getMspt(context);
   }
}
