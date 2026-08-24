package net.mehvahdjukaar.moonlight.api.block;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public interface ILightable {
   TagKey<Item> FLINT_AND_STEELS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/igniter"));

   boolean isLitUp(BlockState var1, BlockGetter var2, BlockPos var3);

   default void setLitUp(BlockState state, LevelAccessor world, BlockPos pos, boolean lit) {
      this.setLitUp(state, world, pos, null, lit);
   }

   void setLitUp(BlockState var1, LevelAccessor var2, BlockPos var3, @Nullable Entity var4, boolean var5);

   @Deprecated(
      forRemoval = true
   )
   default boolean lightUp(@Nullable Entity player, BlockState state, BlockPos pos, LevelAccessor world, ILightable.FireSoundType fireSourceType) {
      return this.tryLightUp(player, state, pos, world, fireSourceType);
   }

   default boolean tryLightUp(@Nullable Entity player, BlockState state, BlockPos pos, LevelAccessor world, ILightable.FireSoundType fireSourceType) {
      if (!this.isLitUp(state, world, pos)) {
         if (!world.isClientSide()) {
            this.setLitUp(state, world, pos, true);
            this.playLightUpSound(world, pos, fireSourceType);
         }

         world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
         return true;
      } else {
         return false;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   default boolean extinguish(@Nullable Entity player, BlockState state, BlockPos pos, LevelAccessor world) {
      return this.tryExtinguish(player, state, pos, world);
   }

   default boolean tryExtinguish(@Nullable Entity player, BlockState state, BlockPos pos, LevelAccessor world) {
      if (this.isLitUp(state, world, pos)) {
         if (!world.isClientSide()) {
            this.playExtinguishSound(world, pos);
            this.setLitUp(state, world, pos, false);
         } else {
            this.spawnSmokeParticles(state, pos, world);
         }

         world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
         return true;
      } else {
         return false;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   default boolean interactWithEntity(Level level, BlockState state, Entity projectile, BlockPos pos) {
      return this.lightableInteractWithEntity(level, state, projectile, pos);
   }

   default boolean lightableInteractWithEntity(Level level, BlockState state, Entity projectile, BlockPos pos) {
      if (projectile.isOnFire()) {
         Entity owner = projectile instanceof TraceableEntity te ? te.getOwner() : null;
         if (owner == null || owner instanceof Player || PlatHelper.isMobGriefingOn(level, owner)) {
            return this.tryLightUp(projectile, state, pos, level, ILightable.FireSoundType.FLAMING_ARROW);
         }
      }

      return false;
   }

   @Deprecated(
      forRemoval = true
   )
   default ItemInteractionResult interactWithPlayerItem(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
      return this.lightableInteractWithPlayerItem(state, level, pos, player, hand, stack);
   }

   default ItemInteractionResult lightableInteractWithPlayerItem(
      BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack
   ) {
      if (Utils.mayPerformBlockAction(player, pos, stack)) {
         if (!this.isLitUp(state, level, pos)) {
            Item item = stack.getItem();
            if (item instanceof FireChargeItem) {
               if (this.tryLightUp(player, state, pos, level, ILightable.FireSoundType.FIRE_CHANGE)) {
                  stack.consume(1, player);
                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               }
            } else if (isIgniter(stack) && this.tryLightUp(player, state, pos, level, ILightable.FireSoundType.FLINT_AND_STEEL)) {
               stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
               return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
         } else if (this.canBeExtinguishedBy(stack) && this.tryExtinguish(player, state, pos, level) && !(stack.getItem() instanceof BrushItem)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   static boolean isIgniter(ItemStack stack) {
      return stack.getItem() instanceof FlintAndSteelItem || stack.is(FLINT_AND_STEELS) || PlatHelper.canLightFire(stack);
   }

   default boolean canBeExtinguishedBy(ItemStack item) {
      return item.getItem() instanceof ShovelItem || item.getItem() instanceof BrushItem;
   }

   default void playLightUpSound(LevelAccessor world, BlockPos pos, ILightable.FireSoundType type) {
      type.play(world, pos);
   }

   default void playExtinguishSound(LevelAccessor world, BlockPos pos) {
      world.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F, 1.5F);
   }

   default void spawnSmokeParticles(BlockState state, BlockPos pos, LevelAccessor world) {
      RandomSource random = world.getRandom();

      for (int i = 0; i < 10; i++) {
         world.addParticle(
            ParticleTypes.SMOKE,
            pos.getX() + 0.25F + random.nextFloat() * 0.5F,
            pos.getY() + 0.35F + random.nextFloat() * 0.5F,
            pos.getZ() + 0.25F + random.nextFloat() * 0.5F,
            0.0,
            0.005,
            0.0
         );
      }
   }

   @FunctionalInterface
   public interface FireSoundType {
      ILightable.FireSoundType FLINT_AND_STEEL = (level, pos) -> level.playSound(
         null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F
      );
      ILightable.FireSoundType FIRE_CHANGE = (level, pos) -> level.playSound(
         null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F
      );
      ILightable.FireSoundType FLAMING_ARROW = (level, pos) -> level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.5F, 1.4F);

      void play(LevelAccessor var1, BlockPos var2);
   }
}
