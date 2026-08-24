package vazkii.psi.common.spell.trick;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickSaveVector extends PieceTrick {
   public static final String KEY_SLOT_LOCKED = "psi:SlotLocked";
   SpellParam<Number> number;
   SpellParam<Vector3> target;

   public PieceTrickSaveVector(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(2.0));
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.number", true).mul(8.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.number = new ParamNumber("psi.spellparam.number", 2774482, false, true));
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 13773354, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
      Double numberVal = this.getParamEvaluation(this.number);
      if (numberVal != null && !(numberVal <= 0.0) && numberVal == numberVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, numberVal.intValue() * 8);
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Number numberVal = this.getParamValue(context, this.number).doubleValue();
      Vector3 targetVal = this.getParamValue(context, this.target);
      int n = numberVal.intValue() - 1;
      if (context.customData.containsKey("psi:SlotLocked" + n)) {
         return null;
      } else {
         ItemStack cadStack = PsiAPI.getPlayerCAD(context.caster);
         if (cadStack != null && cadStack.getItem() instanceof ICAD cad) {
            cad.setStoredVector(cadStack, n, targetVal);
            context.customData.put("psi:SlotLocked" + n, 0);
            return null;
         } else {
            throw new SpellRuntimeException("psi.spellerror.nocad");
         }
      }
   }
}
