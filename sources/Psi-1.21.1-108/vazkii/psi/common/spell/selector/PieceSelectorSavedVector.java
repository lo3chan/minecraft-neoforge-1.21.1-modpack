package vazkii.psi.common.spell.selector;

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
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorSavedVector extends PieceSelector {
   SpellParam<Number> number;

   public PieceSelectorSavedVector(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.number", true).mul(6.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.number = new ParamNumber("psi.spellparam.number", 2774482, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double numberVal = this.getParamEvaluation(this.number);
      if (numberVal != null && !(numberVal <= 0.0) && numberVal == numberVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, numberVal.intValue() * 6);
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int numberVal = this.getParamValue(context, this.number).intValue();
      int n = numberVal - 1;
      if (context.customData.containsKey("psi:SlotLocked" + n)) {
         throw new SpellRuntimeException("psi.spellerror.lockedmemory");
      } else {
         ItemStack cadStack = PsiAPI.getPlayerCAD(context.caster);
         if (cadStack != null && cadStack.getItem() instanceof ICAD cad) {
            return cad.getStoredVector(cadStack, n);
         } else {
            throw new SpellRuntimeException("psi.spellerror.nocad");
         }
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}
