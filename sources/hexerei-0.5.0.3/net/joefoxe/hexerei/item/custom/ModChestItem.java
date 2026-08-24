package net.joefoxe.hexerei.item.custom;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class ModChestItem extends BlockItem {
   private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);

   public ModChestItem(Block block, Properties properties) {
      super(block, properties);
   }
}
