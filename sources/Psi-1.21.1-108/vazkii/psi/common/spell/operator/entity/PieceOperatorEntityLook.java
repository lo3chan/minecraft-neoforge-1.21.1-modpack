package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorEntityLook extends PieceOperator {
   SpellParam<Entity> target;

   public PieceOperatorEntityLook(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity e = this.getParamValue(context, this.target);
      if (e == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return !(e instanceof Projectile) && !(e instanceof FallingBlockEntity) ? new Vector3(e.getViewVector(1.0F)) : new Vector3(e.getDeltaMovement());
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}
