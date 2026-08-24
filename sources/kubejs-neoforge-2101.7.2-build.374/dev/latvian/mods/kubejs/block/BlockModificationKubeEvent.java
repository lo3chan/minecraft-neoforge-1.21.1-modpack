package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModificationKubeEvent implements KubeEvent {
   @Info("Modifies blocks that match the given predicate.\n\n**NOTE**: tag predicates are not supported at this time.\n")
   public void modify(BlockStatePredicate predicate, Consumer<BlockModificationKubeEvent.BlockModifications> c) {
      for (Block block : predicate.getBlocks()) {
         c.accept(new BlockModificationKubeEvent.BlockModifications(block));
      }
   }

   public record BlockModifications(Block block) {
      public void setHasCollision(boolean v) {
         this.block.kjs$setHasCollision(v);
      }

      public void setExplosionResistance(float v) {
         this.block.kjs$setExplosionResistance(v);
      }

      public void setIsRandomlyTicking(boolean v) {
         this.block.kjs$setIsRandomlyTicking(v);
      }

      public void setRandomTickCallback(Consumer<RandomTickCallback> callback) {
         this.block.kjs$setRandomTickCallback(callback);
      }

      public void setSoundType(SoundType v) {
         this.block.kjs$setSoundType(v);
      }

      public void setFriction(float v) {
         this.block.kjs$setFriction(v);
      }

      public void setSpeedFactor(float v) {
         this.block.kjs$setSpeedFactor(v);
      }

      public void setJumpFactor(float v) {
         this.block.kjs$setJumpFactor(v);
      }

      public void setNameKey(String key) {
         this.block.kjs$setNameKey(key);
      }

      public void setDestroySpeed(float v) {
         for (BlockState state : this.block.kjs$getBlockStates()) {
            state.kjs$setDestroySpeed(v);
         }
      }

      public void setLightEmission(int v) {
         for (BlockState state : this.block.kjs$getBlockStates()) {
            state.kjs$setLightEmission(v);
         }
      }

      public void setRequiresTool(boolean v) {
         for (BlockState state : this.block.kjs$getBlockStates()) {
            state.kjs$setRequiresTool(v);
         }
      }
   }
}
