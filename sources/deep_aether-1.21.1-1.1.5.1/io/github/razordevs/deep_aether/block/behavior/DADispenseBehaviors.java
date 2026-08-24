package io.github.razordevs.deep_aether.block.behavior;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class DADispenseBehaviors {
   public static final DispenseItemBehavior WATER_BOTTLE_TO_AETHER_MUD_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
      public ItemStack execute(BlockSource source, ItemStack stack) {
         if (!((PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).is(Potions.WATER)) {
            return super.execute(source, stack);
         } else {
            ServerLevel serverlevel = source.level();
            BlockPos blockpos = source.pos();
            BlockPos blockpos1 = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
            if (serverlevel.getBlockState(blockpos1).is((Block)AetherBlocks.AETHER_DIRT.get())) {
               if (!serverlevel.isClientSide) {
                  for (int i = 0; i < 5; i++) {
                     serverlevel.sendParticles(
                        ParticleTypes.SPLASH,
                        blockpos.getX() + serverlevel.random.nextDouble(),
                        blockpos.getY() + 1,
                        blockpos.getZ() + serverlevel.random.nextDouble(),
                        1,
                        0.0,
                        0.0,
                        0.0,
                        1.0
                     );
                  }
               }

               serverlevel.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               serverlevel.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
               serverlevel.setBlockAndUpdate(blockpos1, ((Block)DABlocks.AETHER_MUD.get()).defaultBlockState());
               return new ItemStack(Items.GLASS_BOTTLE);
            } else if (!serverlevel.getBlockState(blockpos1).getBlockHolder().is(BlockTags.CONVERTABLE_TO_MUD)) {
               return super.execute(source, stack);
            } else {
               if (!serverlevel.isClientSide) {
                  for (int i = 0; i < 5; i++) {
                     serverlevel.sendParticles(
                        ParticleTypes.SPLASH,
                        blockpos.getX() + serverlevel.random.nextDouble(),
                        blockpos.getY() + 1,
                        blockpos.getZ() + serverlevel.random.nextDouble(),
                        1,
                        0.0,
                        0.0,
                        0.0,
                        1.0
                     );
                  }
               }

               serverlevel.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               serverlevel.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
               serverlevel.setBlockAndUpdate(blockpos1, Blocks.MUD.defaultBlockState());
               return new ItemStack(Items.GLASS_BOTTLE);
            }
         }
      }
   };
   public static final DispenseItemBehavior DEEP_AETHER_BUCKET_PICKUP_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      public ItemStack execute(BlockSource source, ItemStack stack) {
         DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
         BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         Level level = source.level();
         if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, stack)) {
            dispensiblecontaineritem.checkExtraContent(null, level, stack, blockpos);
            return new ItemStack(Items.BUCKET);
         } else {
            return this.defaultDispenseItemBehavior.dispense(source, stack);
         }
      }
   };
   public static final DispenseItemBehavior SKYROOT_POISON_BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      public ItemStack execute(BlockSource source, ItemStack stack) {
         DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)DAItems.PLACEABLE_POISON_BUCKET.get();
         BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         Level level = source.level();
         return dispensiblecontaineritem.emptyContents(null, level, blockpos, null, stack)
            ? new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get())
            : this.defaultDispenseItemBehavior.dispense(source, stack);
      }
   };
}
