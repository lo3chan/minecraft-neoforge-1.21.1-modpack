package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import org.jetbrains.annotations.Nullable;

@Info("Invoked when a player left clicks on a block.\n")
public class BlockLeftClickedKubeEvent implements KubePlayerEvent {
   private final LeftClickBlock event;

   public BlockLeftClickedKubeEvent(LeftClickBlock event) {
      this.event = event;
   }

   @Info("The player that left clicked the block.")
   @Override
   public Player getEntity() {
      return this.event.getEntity();
   }

   @Info("The block that was left clicked.")
   public LevelBlock getBlock() {
      return this.event.getLevel().kjs$getBlock(this.event.getPos());
   }

   @Info("The item that was used to left click the block.")
   public ItemStack getItem() {
      return this.event.getEntity().getItemInHand(this.event.getHand());
   }

   @Info("The face of the block that was left clicked.")
   @Nullable
   public Direction getFacing() {
      return this.event.getFace();
   }
}
