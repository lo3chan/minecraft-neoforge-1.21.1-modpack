package vazkii.psi.common.spell.selector.entity;

import java.util.function.Predicate;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

public class PieceSelectorNearbyGlowing extends PieceSelectorNearby {
   public PieceSelectorNearbyGlowing(Spell spell) {
      super(spell);
   }

   @Override
   public Predicate<Entity> getTargetPredicate(SpellContext context) {
      return e -> e != null
         && (e instanceof EyeOfEnder || e.isOnFire() || e.hasGlowingTag() || e instanceof LivingEntity && ((LivingEntity)e).hasEffect(MobEffects.GLOWING));
   }
}
