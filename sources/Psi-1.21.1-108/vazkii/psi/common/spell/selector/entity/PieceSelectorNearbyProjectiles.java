package vazkii.psi.common.spell.selector.entity;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.Projectile;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.entity.EntitySpellProjectile;

public class PieceSelectorNearbyProjectiles extends PieceSelectorNearby {
   public PieceSelectorNearbyProjectiles(Spell spell) {
      super(spell);
   }

   @Override
   public Predicate<Entity> getTargetPredicate(SpellContext context) {
      return e -> (e instanceof Projectile || e instanceof EyeOfEnder) && !(e instanceof EntitySpellProjectile);
   }
}
