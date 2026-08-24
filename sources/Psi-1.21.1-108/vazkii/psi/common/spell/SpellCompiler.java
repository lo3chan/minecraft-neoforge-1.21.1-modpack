package vazkii.psi.common.spell;

import com.mojang.datafixers.util.Either;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.IErrorCatcher;
import vazkii.psi.api.spell.ISpellCompiler;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

public final class SpellCompiler implements ISpellCompiler {
   private final Set<SpellPiece> redirectionPieces = new HashSet<>();
   private CompiledSpell compiled;

   @Override
   public Either<CompiledSpell, SpellCompilationException> compile(Spell in) {
      try {
         return Either.left(this.doCompile(in));
      } catch (SpellCompilationException var3) {
         return Either.right(var3);
      }
   }

   public CompiledSpell doCompile(Spell spell) throws SpellCompilationException {
      if (spell == null) {
         throw new SpellCompilationException("psi.spellerror.nospell");
      } else {
         this.redirectionPieces.clear();
         this.compiled = new CompiledSpell(spell);

         for (SpellPiece piece : this.findPieces(EnumPieceType.ERROR_HANDLER::equals)) {
            this.buildHandler(piece);
         }

         List<SpellPiece> tricks = this.findPieces(EnumPieceType::isTrick);
         if (tricks.isEmpty()) {
            throw new SpellCompilationException("psi.spellerror.notricks");
         } else {
            for (SpellPiece trick : tricks) {
               this.buildPiece(trick);
            }

            if (this.compiled.metadata.getStat(EnumSpellStat.COST) < 0 || this.compiled.metadata.getStat(EnumSpellStat.POTENCY) < 0) {
               throw new SpellCompilationException("psi.spellerror.statoverflow");
            } else if (spell.name != null && !spell.name.isEmpty()) {
               return this.compiled;
            } else {
               throw new SpellCompilationException("psi.spellerror.noname");
            }
         }
      }
   }

   public void buildPiece(SpellPiece piece) throws SpellCompilationException {
      this.buildPiece(piece, new HashSet<>());
   }

   public void buildPiece(SpellPiece piece, Set<SpellPiece> visited) throws SpellCompilationException {
      if (!visited.add(piece)) {
         throw new SpellCompilationException("psi.spellerror.loop", piece.x, piece.y);
      } else {
         if (this.compiled.actionMap.containsKey(piece)) {
            CompiledSpell.Action a = this.compiled.actionMap.get(piece);
            this.compiled.actions.remove(a);
            this.compiled.actions.add(a);
         } else {
            CompiledSpell.Action a = this.compiled.new Action(piece);
            this.compiled.actions.add(a);
            this.compiled.actionMap.put(piece, a);
            piece.addToMetadata(this.compiled.metadata);
         }

         CompiledSpell.CatchHandler catchHandler = this.compiled.errorHandlers.get(piece);
         if (catchHandler != null) {
            this.buildPiece(catchHandler.handlerPiece, new HashSet<>(visited));
         }

         EnumSet<SpellParam.Side> usedSides = EnumSet.noneOf(SpellParam.Side.class);
         HashSet<SpellPiece> params = new HashSet<>();
         HashSet<SpellPiece> handledErrors = new HashSet<>();

         for (SpellParam<?> param : piece.paramSides.keySet()) {
            if (!this.checkSideDisabled(param, piece, usedSides)) {
               SpellParam.Side side = piece.paramSides.get(param);
               SpellPiece pieceAt = this.compiled.sourceSpell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, side, this::buildRedirect);
               if (pieceAt == null) {
                  throw new SpellCompilationException("psi.spellerror.nullparam", piece.x, piece.y);
               }

               if (!param.canAccept(pieceAt)) {
                  throw new SpellCompilationException("psi.spellerror.invalidparam", piece.x, piece.y);
               }

               if (piece instanceof IErrorCatcher && ((IErrorCatcher)piece).catchParam(param)) {
                  handledErrors.add(pieceAt);
               } else {
                  params.add(pieceAt);
               }
            }
         }

         for (SpellPiece pieceAtx : params) {
            HashSet<SpellPiece> visitedCopy = new HashSet<>(visited);
            visitedCopy.addAll(handledErrors);
            this.buildPiece(pieceAtx, visitedCopy);
         }
      }
   }

   public void buildHandler(SpellPiece piece) throws SpellCompilationException {
      if (piece instanceof IErrorCatcher errorCatcher) {
         CompiledSpell.CatchHandler errorHandler = new CompiledSpell.CatchHandler(piece);
         EnumSet usedSides = EnumSet.noneOf(SpellParam.Side.class);

         for (SpellParam<?> param : piece.paramSides.keySet()) {
            if (errorCatcher.catchParam(param) && !this.checkSideDisabled(param, piece, usedSides)) {
               SpellParam.Side side = piece.paramSides.get(param);
               SpellPiece pieceAt = this.compiled.sourceSpell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, side, this::buildRedirect);
               if (pieceAt == null) {
                  throw new SpellCompilationException("psi.spellerror.nullparam", piece.x, piece.y);
               }

               if (!param.canAccept(pieceAt)) {
                  throw new SpellCompilationException("psi.spellerror.invalidparam", piece.x, piece.y);
               }

               this.compiled.errorHandlers.put(pieceAt, errorHandler);
            }
         }
      }
   }

   public void buildRedirect(SpellPiece piece) throws SpellCompilationException {
      if (this.redirectionPieces.add(piece)) {
         piece.addToMetadata(this.compiled.metadata);
         EnumSet<SpellParam.Side> usedSides = EnumSet.noneOf(SpellParam.Side.class);

         for (SpellParam<?> param : piece.paramSides.keySet()) {
            this.checkSideDisabled(param, piece, usedSides);
         }
      }
   }

   private boolean checkSideDisabled(SpellParam<?> param, SpellPiece parent, EnumSet<SpellParam.Side> seen) throws SpellCompilationException {
      SpellParam.Side side = parent.paramSides.get(param);
      if (side.isEnabled()) {
         if (!seen.add(side)) {
            throw new SpellCompilationException("psi.spellerror.samesideparams", parent.x, parent.y);
         } else {
            return false;
         }
      } else if (!param.canDisable) {
         throw new SpellCompilationException("psi.spellerror.unsetparam", parent.x, parent.y);
      } else {
         return true;
      }
   }

   public List<SpellPiece> findPieces(Predicate<EnumPieceType> match) {
      List<SpellPiece> results = new LinkedList<>();

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece piece = this.compiled.sourceSpell.grid.gridData[j][i];
            if (piece != null && match.test(piece.getPieceType())) {
               results.addFirst(piece);
            }
         }
      }

      return results;
   }
}
