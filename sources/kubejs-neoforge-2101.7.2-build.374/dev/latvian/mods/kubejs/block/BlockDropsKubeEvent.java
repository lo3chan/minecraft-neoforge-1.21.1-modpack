package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;

@Info("Modify dropped items and xp from block.\n")
public class BlockDropsKubeEvent implements KubeEntityEvent {
   private final BlockDropsEvent event;

   public BlockDropsKubeEvent(BlockDropsEvent event) {
      this.event = event;
   }

   public ServerLevel getLevel() {
      return this.event.getLevel();
   }

   @Nullable
   @Override
   public Entity getEntity() {
      return this.event.getBreaker();
   }

   @Info("The block that was broken.")
   public LevelBlock getBlock() {
      return this.event.getLevel().kjs$getBlock(this.event.getPos()).cache(this.event.getState()).cache(this.event.getBlockEntity());
   }

   @Info("The experience dropped by the block.")
   public int getXp() {
      return this.event.getDroppedExperience();
   }

   @Info("Sets the experience dropped by the block.")
   public void setXp(int xp) {
      this.event.setDroppedExperience(xp);
   }

   @Info("Dropped item entities.")
   public List<ItemEntity> getItemEntities() {
      return this.event.getDrops();
   }

   @Info("Dropped items. Immutable.")
   public List<ItemStack> getItems() {
      return this.event.getDrops().stream().<ItemStack>map(ItemEntity::getItem).toList();
   }

   public boolean containsItem(ItemPredicate item) {
      for (ItemEntity drop : this.event.getDrops()) {
         if (item.test(drop.getItem())) {
            return true;
         }
      }

      return false;
   }

   public ItemEntity addItem(ItemStack item) {
      double x = this.event.getPos().getX() + 0.5 + Mth.nextDouble(this.event.getLevel().random, -0.25, 0.25);
      double y = this.event.getPos().getY() + 0.5 + Mth.nextDouble(this.event.getLevel().random, -0.25, 0.25) - EntityType.ITEM.getHeight() / 2.0;
      double z = this.event.getPos().getZ() + 0.5 + Mth.nextDouble(this.event.getLevel().random, -0.25, 0.25);
      ItemEntity entity = new ItemEntity(this.event.getLevel(), x, y, z, item);
      this.event.getDrops().add(entity);
      return entity;
   }

   public void removeItem(ItemPredicate item) {
      this.event.getDrops().removeIf(drop -> item.test(drop.getItem()));
   }

   @Info("The tool used when breaking this block. May be null.")
   @Nullable
   public ItemStack getTool() {
      return this.event.getTool().isEmpty() ? null : this.event.getTool();
   }
}
