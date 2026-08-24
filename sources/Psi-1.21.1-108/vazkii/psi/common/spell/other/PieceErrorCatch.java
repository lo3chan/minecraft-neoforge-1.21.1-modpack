package vazkii.psi.common.spell.other;

import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.piece.PieceErrorHandler;

public class PieceErrorCatch extends PieceErrorHandler {
   SpellParam<SpellParam.Any> fallback;

   public PieceErrorCatch(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      super.initParams();
      this.addParam(this.fallback = new ParamAny("psi.spellparam.fallback", 7763574, false) {
         @Override
         public boolean canAccept(SpellPiece other) {
            try {
               SpellParam.Side side = PieceErrorCatch.this.paramSides.get(PieceErrorCatch.this.piece);
               SpellPiece actualPiece = PieceErrorCatch.this.spell.grid.getPieceAtSideWithRedirections(PieceErrorCatch.this.x, PieceErrorCatch.this.y, side);
               return super.canAccept(other) && actualPiece.getEvaluationType().isAssignableFrom(other.getEvaluationType());
            } catch (SpellCompilationException var4) {
               return super.canAccept(other);
            }
         }
      });
   }

   @Override
   protected String paramName() {
      return "psi.spellparam.target";
   }

   @Override
   public boolean catchException(SpellPiece errorPiece, SpellContext context, SpellRuntimeException exception) {
      try {
         SpellParam.Side side = this.paramSides.get(this.piece);
         SpellPiece actualPiece = this.spell.grid.getPieceAtSideWithRedirections(this.x, this.y, side);
         return errorPiece == actualPiece;
      } catch (SpellCompilationException var6) {
         return false;
      }
   }

   @NotNull
   @Override
   public Object supplyReplacementValue(SpellPiece errorPiece, SpellContext context, SpellRuntimeException exception) {
      return this.getRawParamValue(context, this.fallback);
   }
}
