package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorEntityHealth extends PieceOperator {
   SpellParam<Entity> target;

   public PieceOperatorEntityHealth(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity entity = this.getNotNullParamValue(context, this.target);
      if (!(entity instanceof LivingEntity)) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return ((LivingEntity)entity).getHealth() / ((LivingEntity)entity).getMaxHealth();
      }
   }
}
