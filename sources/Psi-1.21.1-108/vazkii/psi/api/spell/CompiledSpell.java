package vazkii.psi.api.spell;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageSpellError;

public class CompiledSpell {
   public final Spell sourceSpell;
   public final SpellMetadata metadata = new SpellMetadata();
   public final Stack<CompiledSpell.Action> actions = new Stack<>();
   public final Map<SpellPiece, CompiledSpell.CatchHandler> errorHandlers = new HashMap<>();
   public final Map<SpellPiece, CompiledSpell.Action> actionMap = new HashMap<>();
   public final boolean[][] spotsEvaluated;
   public CompiledSpell.Action currentAction;

   public CompiledSpell(Spell source) {
      this.sourceSpell = source;
      this.metadata.setStat(EnumSpellStat.BANDWIDTH, source.grid.getSize());
      this.spotsEvaluated = new boolean[9][9];
   }

   public boolean execute(SpellContext context) throws SpellRuntimeException {
      IPlayerData data = PsiAPI.internalHandler.getDataForPlayer(context.caster);

      while (!context.actions.isEmpty()) {
         CompiledSpell.Action a = context.actions.pop();
         this.currentAction = a;
         PsiAPI.internalHandler.setCrashData(this, a.piece);
         a.execute(data, context);
         PsiAPI.internalHandler.setCrashData(null, null);
         this.currentAction = null;
         if (context.stopped) {
            return false;
         }

         if (context.delay > 0) {
            return true;
         }
      }

      return false;
   }

   public void safeExecute(SpellContext context) {
      if (!context.caster.getCommandSenderWorld().isClientSide) {
         try {
            if (context.actions == null) {
               context.actions = (Stack<CompiledSpell.Action>)this.actions.clone();
            }

            if (context.cspell.execute(context)) {
               PsiAPI.internalHandler.delayContext(context);
            }
         } catch (SpellRuntimeException var6) {
            if (!context.shouldSuppressErrors()) {
               context.caster.sendSystemMessage(var6.toComponent().setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
               int x = context.cspell.currentAction.piece.x + 1;
               int y = context.cspell.currentAction.piece.y + 1;
               MessageSpellError message = new MessageSpellError("psi.spellerror.position", x, y);
               MessageRegister.sendToPlayer((ServerPlayer)context.caster, message);
            }
         }
      }
   }

   public class Action {
      public final SpellPiece piece;

      public Action(SpellPiece piece) {
         this.piece = piece;
      }

      public void execute(IPlayerData data, SpellContext context) throws SpellRuntimeException {
         try {
            data.markPieceExecuted(this.piece);
            Object o = this.piece.execute(context);
            Class<?> eval = this.piece.getEvaluationType();
            if (eval != null && eval != Void.class) {
               context.evaluatedObjects[this.piece.x][this.piece.y] = o;
            }
         } catch (SpellRuntimeException var5) {
            if (CompiledSpell.this.errorHandlers.containsKey(this.piece)) {
               if (!CompiledSpell.this.errorHandlers.get(this.piece).suppress(this.piece, context, var5)) {
                  throw var5;
               }
            } else {
               throw var5;
            }
         }
      }
   }

   public static class CatchHandler {
      public final SpellPiece handlerPiece;
      public final IErrorCatcher handler;

      public CatchHandler(SpellPiece handlerPiece) {
         this.handlerPiece = handlerPiece;
         this.handler = (IErrorCatcher)handlerPiece;
      }

      public boolean suppress(SpellPiece piece, SpellContext context, SpellRuntimeException exception) {
         boolean handled = this.handler.catchException(piece, context, exception);
         if (handled) {
            Class<?> eval = piece.getEvaluationType();
            if (eval != null && eval != Void.class) {
               context.evaluatedObjects[piece.x][piece.y] = this.handler.supplyReplacementValue(piece, context, exception);
            }
         }

         return handled;
      }
   }
}
