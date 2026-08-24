package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;

@Info("Invoked when a block is destroyed by a player.\n")
public class BlockBrokenKubeEvent implements KubePlayerEvent {
   private final BreakEvent event;

   public BlockBrokenKubeEvent(BreakEvent event) {
      this.event = event;
   }

   @Info("The player that broke the block.")
   @Override
   public Player getEntity() {
      return this.event.getPlayer();
   }

   @Info("The block that was broken.")
   public LevelBlock getBlock() {
      return ((Level)this.event.getLevel()).kjs$getBlock(this.event.getPos()).cache(this.event.getState());
   }
}
