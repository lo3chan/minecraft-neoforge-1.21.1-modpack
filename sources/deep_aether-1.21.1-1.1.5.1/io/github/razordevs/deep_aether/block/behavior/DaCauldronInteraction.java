package io.github.razordevs.deep_aether.block.behavior;

import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteraction.InteractionMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface DaCauldronInteraction {
   Map<String, InteractionMap> INTERACTIONS = new Object2ObjectArrayMap();
   InteractionMap POISON = newInteractionMap("poison");
   CauldronInteraction FILL_POISON = (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.emptyBucket(
      level, blockPos, player, hand, itemStack, ((Block)DABlocks.POISON_CAULDRON.get()).defaultBlockState(), SoundEvents.BUCKET_EMPTY
   );
   CauldronInteraction FILL_POISON_SKYROOT = (blockState, level, blockPos, player, hand, itemStack) -> emptySkyrootBucket(
      level, blockPos, player, hand, itemStack, ((Block)DABlocks.POISON_CAULDRON.get()).defaultBlockState()
   );

   private static ItemInteractionResult emptySkyrootBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state) {
      if (!level.isClientSide()) {
         Item item = stack.getItem();
         player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get())));
         player.awardStat(Stats.FILL_CAULDRON);
         player.awardStat(Stats.ITEM_USED.get(item));
         level.setBlockAndUpdate(pos, state);
         level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
      }

      return ItemInteractionResult.sidedSuccess(level.isClientSide());
   }

   static InteractionMap newInteractionMap(String pName) {
      Object2ObjectOpenHashMap<Item, CauldronInteraction> object2objectopenhashmap = new Object2ObjectOpenHashMap();
      object2objectopenhashmap.defaultReturnValue(
         (CauldronInteraction)(state, level, pos, player, hand, stack) -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
      );
      InteractionMap cauldroninteraction$interactionmap = new InteractionMap(pName, object2objectopenhashmap);
      INTERACTIONS.put(pName, cauldroninteraction$interactionmap);
      return cauldroninteraction$interactionmap;
   }

   static void bootStrap() {
      POISON.map()
         .put(
            Items.BUCKET,
            (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.fillBucket(
               blockState,
               level,
               blockPos,
               player,
               hand,
               itemStack,
               new ItemStack((ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get()),
               state -> true,
               SoundEvents.BUCKET_FILL
            )
         );
      POISON.map()
         .put(
            (Item)AetherItems.SKYROOT_BUCKET.get(),
            (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.fillBucket(
               blockState,
               level,
               blockPos,
               player,
               hand,
               itemStack,
               new ItemStack((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get()),
               state -> true,
               SoundEvents.BUCKET_FILL
            )
         );
      CauldronInteraction.EMPTY.map().put((Item)AetherItems.SKYROOT_POISON_BUCKET.get(), FILL_POISON_SKYROOT);
      CauldronInteraction.EMPTY.map().put((Item)DAItems.PLACEABLE_POISON_BUCKET.get(), FILL_POISON);
      POISON.map()
         .put(
            Items.BUCKET,
            (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.fillBucket(
               blockState,
               level,
               blockPos,
               player,
               hand,
               itemStack,
               new ItemStack((ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get()),
               state -> true,
               SoundEvents.BUCKET_FILL
            )
         );
      POISON.map()
         .put(
            (Item)AetherItems.SKYROOT_BUCKET.get(),
            (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.fillBucket(
               blockState,
               level,
               blockPos,
               player,
               hand,
               itemStack,
               new ItemStack((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get()),
               state -> true,
               SoundEvents.BUCKET_FILL
            )
         );
   }
}
