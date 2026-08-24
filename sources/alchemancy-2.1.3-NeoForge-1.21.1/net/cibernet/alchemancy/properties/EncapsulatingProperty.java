package net.cibernet.alchemancy.properties;

import java.util.Optional;
import javax.annotation.Nullable;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class EncapsulatingProperty extends Property implements IDataHolder<EncapsulatingProperty.BlockData> {
   private static final EncapsulatingProperty.BlockData DEFAULT = new EncapsulatingProperty.BlockData(Blocks.AIR.defaultBlockState(), new CompoundTag());

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      Level level = event.getLevel();
      BlockPos pos = event.getPos();
      EncapsulatingProperty.BlockData data = this.getData(event.getItemStack());
      if (!event.isCanceled() && !level.isClientSide()) {
         if (data.blockState.isAir()) {
            if (this.pickUpBlock(level, pos, event.getItemStack(), event.getPlayer())) {
               event.setCancellationResult(ItemInteractionResult.SUCCESS);
               event.setCanceled(true);
            } else if (event.getPlayer() != null) {
               event.getPlayer()
                  .displayClientMessage(
                     Component.translatable("property.alchemancy.encapsulating.cannot_store", new Object[]{level.getBlockState(pos).getBlock().getName()})
                        .withStyle(ChatFormatting.RED),
                     true
                  );
            }
         } else {
            if (event.getFace() != null && !level.getBlockState(pos).canBeReplaced()) {
               pos = pos.relative(event.getFace());
            }

            if (this.attemptPlaceBlock(level, pos, data, event.getItemStack(), event.getPlayer())) {
               event.setCancellationResult(ItemInteractionResult.SUCCESS);
               event.setCanceled(true);
            }
         }
      }
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      Level level = blockSource.level();
      BlockPos pos = blockSource.pos().relative(direction);
      EncapsulatingProperty.BlockData data = this.getData(stack);
      if (data.blockState.isAir()) {
         if (this.pickUpBlock(level, pos, stack, null)) {
            InfusionPropertyDispenseBehavior.playDefaultParticles(blockSource, direction);
            return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
         }
      } else if (this.attemptPlaceBlock(level, pos, data, stack, null)) {
         InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
         return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
      }

      return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
   }

   @Override
   public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource) {
      if (!stack.has(DataComponents.INTANGIBLE_PROJECTILE) && !(itemEntity instanceof AbstractArrow arrow && arrow.pickup == Pickup.DISALLOWED)) {
         this.attemptPlaceBlock(itemEntity.level(), itemEntity.blockPosition(), this.getData(stack), stack, null);
      }
   }

   public boolean pickUpBlock(Level level, BlockPos pos, ItemStack stack, @Nullable Entity user) {
      BlockState state = level.getBlockState(pos);
      if (!state.isAir() && !(state.getDestroySpeed(level, pos) < 0.0F) && !state.is(AlchemancyTags.Blocks.CANNOT_ENCAPSULATE)) {
         if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
         }

         if (state.hasProperty(BlockStateProperties.BED_PART)) {
            if (state.getValue(BlockStateProperties.BED_PART) == BedPart.HEAD) {
               state = level.getBlockState(pos.relative(((Direction)state.getValue(BedBlock.FACING)).getOpposite()));
            } else {
               pos = pos.relative((Direction)state.getValue(BedBlock.FACING));
            }
         }

         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity != null) {
            level.removeBlockEntity(pos);
         }

         if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.NULLIFIER)) {
            this.setData(stack, state, blockEntity);
         }

         level.removeBlock(pos, false);
         level.levelEvent(user instanceof Player player ? player : null, 2001, pos, Block.getId(state));
         level.playSound(null, pos, state.getSoundType(level, pos, user).getBreakSound(), SoundSource.BLOCKS, 1.0F, 0.5F);
         return true;
      } else {
         return false;
      }
   }

   private static BlockState fixChestBlockstate(Level level, BlockPos pos, BlockState state) {
      if (!state.hasProperty(ChestBlock.TYPE)) {
         return state;
      } else {
         ChestType chestType = (ChestType)state.getValue(ChestBlock.TYPE);
         if (chestType == ChestType.SINGLE) {
            return (BlockState)state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
         } else {
            BlockState connectedBlock = level.getBlockState(pos.relative(ChestBlock.getConnectedDirection(state)));
            if (!connectedBlock.is(state.getBlock())) {
               return (BlockState)state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
            } else {
               return connectedBlock.getValue(ChestBlock.TYPE) != chestType.getOpposite()
                  ? state
                  : (BlockState)state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
            }
         }
      }
   }

   public boolean attemptPlaceBlock(Level level, BlockPos pos, ItemStack source, @Nullable Entity user) {
      return this.attemptPlaceBlock(level, pos, this.getData(source), source, user);
   }

   public boolean attemptPlaceBlock(Level level, BlockPos pos, EncapsulatingProperty.BlockData data, ItemStack source, @Nullable Entity user) {
      BlockState oldState = level.getBlockState(pos);
      BlockState state = fixChestBlockstate(level, pos, data.blockState());
      if (oldState.canBeReplaced() && (state.canSurvive(level, pos) || state.is(AlchemancyTags.Blocks.ENCAPSULATING_ALWAYS_PLACES))) {
         if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
            && !level.getBlockState(pos.relative(((DoubleBlockHalf)state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)).getDirectionToOther()))
               .canBeReplaced()) {
            return false;
         } else if (state.hasProperty(BlockStateProperties.BED_PART)
            && !level.getBlockState(
                  pos.relative(
                     state.getValue(BlockStateProperties.BED_PART) == BedPart.HEAD
                        ? ((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)).getOpposite()
                        : (Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                  )
               )
               .canBeReplaced()) {
            return false;
         } else {
            level.setBlockAndUpdate(pos, state);
            state.getBlock().setPlacedBy(level, pos, state, user instanceof LivingEntity living ? living : null, ItemStack.EMPTY);
            BlockEntity blockEntity = this.createBlockEntity(pos, data);
            if (blockEntity != null) {
               level.setBlockEntity(blockEntity);
            }

            level.playSound(null, pos, state.getSoundType(level, pos, user).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.5F);
            this.setData(source, this.getDefaultData());
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9922967;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      BlockState storedBlock = this.getData(stack).blockState;
      return (Component)(!storedBlock.isAir()
         ? Component.translatable("property.detail", new Object[]{name, storedBlock.getBlock().getName()}).withColor(this.getColor(stack))
         : name);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, ItemStack dataItem) {
      return this.createBlockEntity(pos, this.getData(dataItem));
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, EncapsulatingProperty.BlockData data) {
      return this.createBlockEntity(data.blockState, pos, data.blockEntityData);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockState state, BlockPos pos, CompoundTag data) {
      if (state.getBlock() instanceof EntityBlock entityBlock) {
         BlockEntity result = entityBlock.newBlockEntity(pos, state);
         if (result != null) {
            result.loadCustomOnly(data, CommonUtils.registryAccessStatic());
            return result;
         }
      }

      return null;
   }

   public void setData(ItemStack stack, BlockState state, @Nullable BlockEntity blockEntity) {
      IDataHolder.super.setData(
         stack,
         new EncapsulatingProperty.BlockData(state, blockEntity == null ? new CompoundTag() : blockEntity.saveCustomOnly(CommonUtils.registryAccessStatic()))
      );
   }

   public EncapsulatingProperty.BlockData readData(CompoundTag tag) {
      if (tag.contains("block_id", 8)) {
         RegistryAccess registryAccess = CommonUtils.registryAccessStatic();
         Optional<Reference<Block>> block = registryAccess.lookupOrThrow(Registries.BLOCK)
            .get(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(tag.getString("block_id"))));
         if (block.isPresent()) {
            BlockState state = ((Block)block.get().value()).defaultBlockState();
            if (tag.contains("block_state", 10)) {
               StateDefinition<Block, BlockState> stateDefinition = state.getBlock().getStateDefinition();
               CompoundTag blockStateTag = tag.getCompound("block_state");

               for (String statePropertyName : blockStateTag.getAllKeys()) {
                  net.minecraft.world.level.block.state.properties.Property<?> stateProperty = stateDefinition.getProperty(statePropertyName);
                  if (stateProperty != null) {
                     state = updateState(state, stateProperty, blockStateTag.getString(statePropertyName));
                  }
               }
            }

            return new EncapsulatingProperty.BlockData(state, tag.getCompound("block_entity"));
         }
      }

      return this.getDefaultData();
   }

   public CompoundTag writeData(final EncapsulatingProperty.BlockData data) {
      return new CompoundTag() {
         {
            RegistryAccess registryAccess = CommonUtils.registryAccessStatic();
            if (!data.blockEntityData.isEmpty()) {
               this.put("block_entity", data.blockEntityData);
            }

            if (!data.blockState.isAir()) {
               BlockState state = data.blockState;
               if (!state.getValues().isEmpty()) {
                  CompoundTag statesTag = new CompoundTag();

                  for (net.minecraft.world.level.block.state.properties.Property<?> stateProperty : state.getValues().keySet()) {
                     statesTag.putString(stateProperty.getName(), EncapsulatingProperty.this.getStateValueName(stateProperty, state));
                  }

                  this.put("block_state", statesTag);
               }

               this.putString("block_id", state.getBlockHolder().getKey().location().toString());
            }
         }
      };
   }

   private static <T extends Comparable<T>> BlockState updateState(
      BlockState state, net.minecraft.world.level.block.state.properties.Property<T> property, String propertyName
   ) {
      return property.getValue(propertyName).map(p_330629_ -> (BlockState)state.setValue(property, p_330629_)).orElse(state);
   }

   private <T extends Comparable<T>> String getStateValueName(net.minecraft.world.level.block.state.properties.Property<T> property, BlockState state) {
      return property.getName(state.getValue(property));
   }

   public EncapsulatingProperty.BlockData getDefaultData() {
      return DEFAULT;
   }

   public record BlockData(BlockState blockState, CompoundTag blockEntityData) {
   }
}
