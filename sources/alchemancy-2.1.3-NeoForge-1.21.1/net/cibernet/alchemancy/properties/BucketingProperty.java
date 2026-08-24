package net.cibernet.alchemancy.properties;

import java.util.Optional;
import javax.annotation.Nullable;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.fluids.FluidStack;

public class BucketingProperty extends Property implements IDataHolder<Fluid> {
   @Override
   public int getColor(ItemStack stack) {
      return 2511981;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      Fluid storedFluid = this.getData(stack);
      return (Component)(!storedFluid.equals(this.getDefaultData())
         ? Component.translatable("property.detail", new Object[]{name, storedFluid.defaultFluidState().createLegacyBlock().getBlock().getName()})
            .withColor(this.getColor(stack))
         : name);
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled() && this.handleInteraction(event.getLevel(), event.getItemStack(), event.getEntity())) {
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (!event.isCanceled() && this.handleInteraction(event.getLevel(), event.getItemStack(), event.getPlayer())) {
         event.setCancellationResult(ItemInteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      if (this.handleInteraction(blockSource.level(), stack, blockSource.pos().relative(direction), direction, null)) {
         InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
         return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
      } else {
         return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
      }
   }

   private boolean handleInteraction(Level level, ItemStack stack, Player user) {
      Fluid storedFluid = this.getData(stack);
      BlockHitResult hitResult = Item.getPlayerPOVHitResult(
         level,
         user,
         storedFluid.equals(Fluids.EMPTY) ? net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY : net.minecraft.world.level.ClipContext.Fluid.NONE
      );
      BlockPos hitPos = hitResult.getBlockPos();
      Direction hitDirection = hitResult.getDirection();
      return hitResult.getType() == Type.BLOCK ? this.handleInteraction(level, stack, hitPos, hitDirection, user) : false;
   }

   private boolean handleInteraction(Level level, ItemStack stack, BlockPos pos, Direction direction, @Nullable Player user) {
      Fluid storedFluid = this.getData(stack);
      BlockState state = level.getBlockState(pos);
      if (storedFluid.equals(this.getDefaultData())) {
         if (state.getBlock() instanceof BucketPickup bucketPickup
            && bucketPickup.pickupBlock(user, level, pos, state).getItem() instanceof BucketItem bucketItem) {
            if (user != null) {
               bucketPickup.getPickupSound(state).ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
               level.gameEvent(user, GameEvent.FLUID_PICKUP, pos);
            } else {
               bucketPickup.getPickupSound(state).ifPresent(sound -> level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 0.5F));
               level.gameEvent(GameEvent.FLUID_PICKUP, pos, Context.of(state));
            }

            if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.NULLIFIER)) {
               this.setData(stack, bucketItem.content);
            }

            return true;
         }
      } else if (this.placeLiquid(level, pos, storedFluid, user, direction)) {
         this.setData(stack, this.getDefaultData());
         return true;
      }

      return false;
   }

   public boolean isEmpty(ItemStack stack) {
      return this.getData(stack).equals(this.getDefaultData());
   }

   @Override
   public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource) {
      if (!stack.has(DataComponents.INTANGIBLE_PROJECTILE) && !(itemEntity instanceof AbstractArrow arrow && arrow.pickup == Pickup.DISALLOWED)) {
         if (this.placeLiquid(itemEntity.level(), itemEntity.blockPosition(), this.getData(stack), null, null)) {
            this.setData(stack, this.getDefaultData());
         }
      }
   }

   public boolean placeLiquid(Level level, BlockPos hitPos, ItemStack sourceStack, @Nullable Player user, @Nullable Direction hitDirection) {
      if (this.placeLiquid(level, hitPos, this.getData(sourceStack), user, hitDirection)) {
         this.setData(sourceStack, this.getDefaultData());
         return true;
      } else {
         return false;
      }
   }

   protected boolean placeLiquid(Level level, BlockPos hitPos, Fluid storedFluid, @Nullable Player user, @Nullable Direction hitDirection) {
      if (storedFluid.equals(this.getDefaultData())) {
         return false;
      } else {
         BlockState hitState = level.getBlockState(hitPos);
         if (hitState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer
            && liquidBlockContainer.canPlaceLiquid(user, level, hitPos, hitState, storedFluid)) {
            liquidBlockContainer.placeLiquid(level, hitPos, hitState, storedFluid.defaultFluidState());
            this.playEmptySound(user, level, hitPos, storedFluid);
            return true;
         } else if (storedFluid.getFluidType().isVaporizedOnPlacement(level, hitPos, FluidStack.EMPTY)) {
            storedFluid.getFluidType().onVaporize(user, level, hitPos, FluidStack.EMPTY);
            return true;
         } else if (level.dimensionType().ultraWarm() && storedFluid.is(FluidTags.WATER)) {
            int l = hitPos.getX();
            int i = hitPos.getY();
            int j = hitPos.getZ();
            level.playSound(
               user, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
            );

            for (int k = 0; k < 8; k++) {
               level.addParticle(ParticleTypes.LARGE_SMOKE, l + Math.random(), i + Math.random(), j + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
         } else {
            if (hitState.canBeReplaced(storedFluid)) {
               if (!level.isClientSide() && !hitState.liquid()) {
                  level.destroyBlock(hitPos, true);
               }
            } else if (hitDirection != null) {
               hitPos = hitPos.relative(hitDirection);
               hitState = level.getBlockState(hitPos);
            }

            if ((!hitState.isAir() && !hitState.canBeReplaced(storedFluid) || !level.setBlock(hitPos, storedFluid.defaultFluidState().createLegacyBlock(), 11))
               && !hitState.getFluidState().isSource()) {
               return false;
            } else {
               this.playEmptySound(user, level, hitPos, storedFluid);
               return true;
            }
         }
      }
   }

   protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos, Fluid fluid) {
      SoundEvent soundevent = fluid.getFluidType().getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
      if (soundevent == null) {
         soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
      }

      level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
   }

   public Fluid readData(CompoundTag tag) {
      if (tag.contains("fluid", 8)) {
         Optional<Reference<Fluid>> fluid = CommonUtils.registryAccessStatic()
            .lookupOrThrow(Registries.FLUID)
            .get(ResourceKey.create(Registries.FLUID, ResourceLocation.parse(tag.getString("fluid"))));
         if (fluid.isPresent()) {
            return (Fluid)fluid.get().value();
         }
      }

      return this.getDefaultData();
   }

   public CompoundTag writeData(final Fluid data) {
      return new CompoundTag() {
         {
            if (!data.equals(Fluids.EMPTY)) {
               this.putString("fluid", BuiltInRegistries.FLUID.getKey(data).toString());
            }
         }
      };
   }

   public Fluid getDefaultData() {
      return Fluids.EMPTY;
   }
}
