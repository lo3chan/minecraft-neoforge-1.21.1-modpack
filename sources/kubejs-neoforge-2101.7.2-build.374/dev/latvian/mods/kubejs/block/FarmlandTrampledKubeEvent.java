package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent;

@Info("Invoked when an entity attempts to trample farmland.\n")
public class FarmlandTrampledKubeEvent implements KubeEntityEvent {
   private final FarmlandTrampleEvent event;
   private final LevelBlock block;

   public FarmlandTrampledKubeEvent(FarmlandTrampleEvent event) {
      this.event = event;
      this.block = ((Level)event.getLevel()).kjs$getBlock(event.getPos()).cache(event.getState());
   }

   @Info("The distance of the entity from the block.")
   public float getDistance() {
      return this.event.getFallDistance();
   }

   @Info("The entity that is attempting to trample the farmland.")
   @Override
   public Entity getEntity() {
      return this.event.getEntity();
   }

   @Info("The level that the farmland and the entity are in.")
   @Override
   public Level getLevel() {
      return (Level)this.event.getLevel();
   }

   @Info("The farmland block.")
   public LevelBlock getBlock() {
      return this.block;
   }
}
