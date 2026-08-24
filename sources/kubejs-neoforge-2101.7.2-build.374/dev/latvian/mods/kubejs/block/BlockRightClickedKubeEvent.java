package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@Info("Invoked when a player right clicks on a block.\n")
public class BlockRightClickedKubeEvent implements KubePlayerEvent {
   private ItemStack item;
   private final Player player;
   private final InteractionHand hand;
   private final BlockPos pos;
   private final Direction direction;
   private final BlockHitResult hitResult;
   private LevelBlock block;

   public BlockRightClickedKubeEvent(ItemStack item, Player player, InteractionHand hand, BlockPos pos, Direction direction, @Nullable BlockHitResult hitResult) {
      this.item = item;
      this.player = player;
      this.hand = hand;
      this.pos = pos;
      this.direction = direction;
      this.hitResult = hitResult;
   }

   @Info("The player that right clicked the block.")
   @Override
   public Player getEntity() {
      return this.player;
   }

   @Info("The block that was right clicked.")
   public LevelBlock getBlock() {
      if (this.block == null) {
         this.block = this.player.level().kjs$getBlock(this.pos);
      }

      return this.block;
   }

   @Info("The hand that was used to right click the block.")
   public InteractionHand getHand() {
      return this.hand;
   }

   @Info("The position of the block that was right clicked.")
   public ItemStack getItem() {
      if (this.item == null) {
         this.item = this.player.getItemInHand(this.hand);
      }

      return this.item;
   }

   @Info("The face of the block being right clicked.")
   public Direction getFacing() {
      return this.direction;
   }

   @Nullable
   public BlockHitResult getHitResult() {
      return this.hitResult;
   }
}
