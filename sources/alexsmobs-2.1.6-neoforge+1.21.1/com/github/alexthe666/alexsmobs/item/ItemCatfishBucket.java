package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

public class ItemCatfishBucket extends BucketItem {
   public ItemCatfishBucket(Fluid fluid, Properties builder) {
      super(fluid, builder.stacksTo(1));
   }

   public void checkExtraContent(@Nullable Player player, Level level, ItemStack stack, BlockPos pos) {
      if (level instanceof ServerLevel) {
         ItemModFishBucket.spawnFish(AMEntityRegistry.CATFISH.get(), (ServerLevel)level, stack, pos);
         level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
      }
   }

   protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos) {
      level.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }
}
