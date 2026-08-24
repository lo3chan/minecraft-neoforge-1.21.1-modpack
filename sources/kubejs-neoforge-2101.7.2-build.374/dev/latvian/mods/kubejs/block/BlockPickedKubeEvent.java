package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeRayTraceResult;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

@Info("Invoked when player middle-clicks on a block.\n")
public class BlockPickedKubeEvent implements KubePlayerEvent {
   public final Level level;
   public final LevelBlock block;
   public final Player player;
   private final HitResult hitResult;
   private KubeRayTraceResult target;

   public BlockPickedKubeEvent(Level level, BlockPos pos, BlockState state, Player player, HitResult hitResult) {
      this.level = level;
      this.block = level.kjs$getBlock(pos).cache(state);
      this.player = player;
      this.hitResult = hitResult;
   }

   @Override
   public Level getLevel() {
      return this.block.getLevel();
   }

   @Override
   public Player getEntity() {
      return this.player;
   }

   public KubeRayTraceResult getTarget() {
      if (this.target == null) {
         this.target = new KubeRayTraceResult(this.player, this.hitResult);
      }

      return this.target;
   }

   @HideFromJS
   @Nullable
   public static ItemStack handle(BlockState state, HitResult target, LevelReader levelReader, BlockPos pos, Player player) {
      if (levelReader instanceof Level level) {
         ResourceKey<Block> key = state.kjs$getKey();
         if (BlockEvents.PICKED.hasListeners(key)
            && BlockEvents.PICKED.post(level, key, new BlockPickedKubeEvent(level, pos, state, player, target)).value() instanceof ItemStack stack) {
            return stack;
         }
      }

      return null;
   }
}
