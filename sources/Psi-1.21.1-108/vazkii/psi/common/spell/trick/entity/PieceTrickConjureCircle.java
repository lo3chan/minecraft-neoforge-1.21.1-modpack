package vazkii.psi.common.spell.trick.entity;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.entity.EntitySpellCircle;
import vazkii.psi.common.entity.ModEntities;

public class PieceTrickConjureCircle extends PieceTrick {
   private SpellParam<Number> time;
   private SpellParam<Vector3> position;
   private SpellParam<Number> scale;
   private SpellParam<Vector3> direction;

   public PieceTrickConjureCircle(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 13773354, false, false));
      this.addParam(this.direction = new ParamVector("psi.spellparam.direction", 2805970, true, false));
      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 2774482, true, true));
      this.addParam(this.scale = new ParamNumber("psi.spellparam.radius", 4117034, true, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
      super.addToMetadata(meta);
      double scl = this.getParamEvaluationeOrDefault(this.scale, 1).doubleValue();
      double tim = this.getParamEvaluationeOrDefault(this.time, 100).doubleValue();
      if (scl > 4.0 || scl <= 0.0) {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      } else if (tim <= 0.0) {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      } else {
         meta.addStat(EnumSpellStat.POTENCY, (int)(scl * tim / 100.0));
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 pos = SpellHelpers.getVector3(this, context, this.position, true, true, false);
      Vector3 dir = SpellHelpers.getDefaultedVector(this, context, this.direction, false, false, new Vector3(0.0, 1.0, 0.0));
      double scl = this.getParamValueOrDefault(context, this.scale, 1).doubleValue();
      double maxTimeAlive = this.getParamValueOrDefault(context, this.time, 100).doubleValue();
      ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
      ItemStack colorizer = ((ICAD)cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
      EntitySpellCircle circle = new EntitySpellCircle(ModEntities.spellCircle, context.caster.getCommandSenderWorld());
      circle.setInfo(context.caster, colorizer, ItemStack.EMPTY);
      circle.setPos(pos.x, pos.y, pos.z);
      circle.setLifetime((int)maxTimeAlive);
      circle.setDirection(dir.toVec3D().normalize());
      circle.setLookAngle(dir.toVec3D().normalize());
      circle.setScale((float)scl);
      circle.getCommandSenderWorld().addFreshEntity(circle);
      return null;
   }
}
