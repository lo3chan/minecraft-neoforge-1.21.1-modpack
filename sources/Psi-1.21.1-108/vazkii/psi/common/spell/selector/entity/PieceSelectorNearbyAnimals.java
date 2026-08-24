package vazkii.psi.common.spell.selector.entity;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

public class PieceSelectorNearbyAnimals extends PieceSelectorNearby {
   public PieceSelectorNearbyAnimals(Spell spell) {
      super(spell);
   }

   @Override
   public Predicate<Entity> getTargetPredicate(SpellContext context) {
      return e -> (e instanceof Animal || e instanceof WaterAnimal) && !(e instanceof Enemy);
   }
}
