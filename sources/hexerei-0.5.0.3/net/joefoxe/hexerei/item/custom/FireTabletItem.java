package net.joefoxe.hexerei.item.custom;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class FireTabletItem extends Item {
   private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);

   public FireTabletItem(Properties properties) {
      super(properties);
   }
}
