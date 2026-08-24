package vazkii.psi.common.spell.trick;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickDebug extends PieceTrick {
   SpellParam<SpellParam.Any> target;
   SpellParam<Number> number;

   public PieceTrickDebug(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamAny("psi.spellparam.target", 2774482, false));
      this.addParam(this.number = new ParamNumber("psi.spellparam.number", 13773354, true, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) {
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Number numberVal = this.getParamValue(context, this.number);
      Object targetVal = this.getParamValue(context, this.target);
      Component component = Component.literal(String.valueOf(targetVal));
      if (numberVal != null) {
         String numStr = numberVal + "";
         if (numberVal.doubleValue() - numberVal.intValue() == 0.0) {
            int numInt = numberVal.intValue();
            numStr = numInt + "";
         }

         component = Component.literal("[" + numStr + "]")
            .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))
            .append(Component.literal(" ").setStyle(Style.EMPTY.withColor(ChatFormatting.RESET)))
            .append(component);
      }

      context.caster.sendSystemMessage(component);
      return null;
   }
}
