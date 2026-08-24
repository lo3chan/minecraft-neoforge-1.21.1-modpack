package vazkii.psi.common.spell.trick;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.PlayerDataHandler;

public class PieceTrickRussianRoulette extends PieceTrick {
   public PieceTrickRussianRoulette(Spell spell) {
      super(spell);
   }

   public static int getRandomSocketableSlot(ISocketable socketable) {
      return (int)(Math.random() * (socketable.getLastSlot() + 1));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
      super.addToMetadata(meta);
   }

   @Override
   public Object execute(SpellContext context) {
      ItemStack stack = context.tool.isEmpty() ? PsiAPI.getPlayerCAD(context.caster) : context.tool;
      boolean updateLoopcast = stack.getItem() instanceof ICAD && context.castFrom == PlayerDataHandler.get(context.caster).loopcastHand;
      ISocketable capability = (ISocketable)stack.getCapability(PsiAPI.SOCKETABLE_CAPABILITY);
      if (capability == null) {
         return null;
      } else {
         int targetSlot = getRandomSocketableSlot(capability);
         capability.setSelectedSlot(targetSlot);
         if (updateLoopcast) {
            PlayerDataHandler.get(context.caster).lastTickLoopcastStack = stack.copy();
         }

         return null;
      }
   }
}
