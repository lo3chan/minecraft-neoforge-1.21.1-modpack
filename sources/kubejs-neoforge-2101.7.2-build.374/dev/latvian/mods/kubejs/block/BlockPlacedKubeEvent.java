package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;

@Info("Invoked when a block is placed.\n")
public class BlockPlacedKubeEvent implements KubeEntityEvent {
   private final EntityPlaceEvent event;

   public BlockPlacedKubeEvent(EntityPlaceEvent event) {
      this.event = event;
   }

   @Info("The level of the block that was placed.")
   @Override
   public Level getLevel() {
      return (Level)this.event.getLevel();
   }

   @Info("The entity that placed the block. Can be `null`, e.g. when a block is placed by a dispenser.")
   @Override
   public Entity getEntity() {
      return this.event.getEntity();
   }

   @Info("The block that is placed.")
   public LevelBlock getBlock() {
      return ((Level)this.event.getLevel()).kjs$getBlock(this.event.getPos()).cache(this.event.getPlacedBlock());
   }
}
