package vazkii.psi.common.spell.selector.entity;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public abstract class PieceSelectorNearby extends PieceSelector {
   SpellParam<Vector3> position;
   SpellParam<Number> radius;

   public PieceSelectorNearby(Spell spell) {
      super(spell);
   }

   @NotNull
   private static AABB getArea(Vector3 positionVal, double radiusVal, Vector3 positionCenter) {
      AABB axis = new AABB(
         positionVal.x - radiusVal,
         positionVal.y - radiusVal,
         positionVal.z - radiusVal,
         positionVal.x + radiusVal,
         positionVal.y + radiusVal,
         positionVal.z + radiusVal
      );
      AABB eris = new AABB(
         positionCenter.x - 32.0, positionCenter.y - 32.0, positionCenter.z - 32.0, positionCenter.x + 32.0, positionCenter.y + 32.0, positionCenter.z + 32.0
      );
      return axis.intersect(eris);
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, true, false));
      this.addParam(this.radius = new ParamNumber("psi.spellparam.radius", 4117034, true, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      double radiusVal = this.getParamEvaluationeOrDefault(this.radius, 64.0).doubleValue();
      if (radiusVal <= 0.0) {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValueOrDefault(context, this.position, Vector3.fromVec3d(context.focalPoint.position()));
      double radiusVal = this.getParamValueOrDefault(context, this.radius, 64.0).doubleValue();
      Vector3 positionCenter = Vector3.fromVec3d(context.focalPoint.position());
      if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         AABB area = getArea(positionVal, radiusVal, positionCenter);
         Predicate<Entity> pred = this.getTargetPredicate(context);
         List<Entity> list = context.caster
            .getCommandSenderWorld()
            .getEntitiesOfClass(Entity.class, area, e -> e != null && pred.test(e) && e != context.caster && e != context.focalPoint && context.isInRadius(e));
         return EntityListWrapper.make(list);
      }
   }

   public abstract Predicate<Entity> getTargetPredicate(SpellContext var1);

   @Override
   public Class<?> getEvaluationType() {
      return EntityListWrapper.class;
   }
}
