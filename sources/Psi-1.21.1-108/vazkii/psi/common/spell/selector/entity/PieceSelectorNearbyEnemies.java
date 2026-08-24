package vazkii.psi.common.spell.selector.entity;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

public class PieceSelectorNearbyEnemies extends PieceSelectorNearby {
   public PieceSelectorNearbyEnemies(Spell spell) {
      super(spell);
   }

   @Override
   public Predicate<Entity> getTargetPredicate(SpellContext context) {
      return e -> e instanceof Enemy;
   }
}
