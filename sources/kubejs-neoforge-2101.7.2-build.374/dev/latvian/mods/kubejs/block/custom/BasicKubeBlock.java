package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRightClickedKubeEvent;
import dev.latvian.mods.kubejs.block.KubeJSBlockProperties;
import dev.latvian.mods.kubejs.block.callback.AfterEntityFallenOnBlockCallback;
import dev.latvian.mods.kubejs.block.callback.BlockExplodedCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateMirrorCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateModifyCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateModifyPlacementCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateRotateCallback;
import dev.latvian.mods.kubejs.block.callback.CanBeReplacedCallback;
import dev.latvian.mods.kubejs.block.callback.EntityBlockCallback;
import dev.latvian.mods.kubejs.block.callback.EntityFallenOnBlockCallback;
import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentHolder;
import dev.latvian.mods.kubejs.block.entity.KubeBlockEntity;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BasicKubeBlock extends Block implements SimpleWaterloggedBlock {
   public final BlockBuilder blockBuilder;
   public final VoxelShape shape;

   public BasicKubeBlock(BlockBuilder p) {
      super(p.createProperties());
      this.blockBuilder = p;
      this.shape = BlockBuilder.createShape(p.customShape);
      BlockState blockState = (BlockState)this.stateDefinition.any();
      if (this.blockBuilder.defaultStateModification != null) {
         BlockStateModifyCallback callbackJS = new BlockStateModifyCallback(blockState);
         if (safeCallback(
            ScriptType.STARTUP, this.blockBuilder.defaultStateModification, callbackJS, "Error while creating default blockState for block " + p.id
         )) {
            this.registerDefaultState(callbackJS.getState());
         }
      } else if (this.blockBuilder.canBeWaterlogged()) {
         this.registerDefaultState((BlockState)blockState.setValue(BlockStateProperties.WATERLOGGED, false));
      }
   }

   public BlockBuilder kjs$getBlockBuilder() {
      return this.blockBuilder;
   }

   @Deprecated
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return this.shape;
   }

   protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
      if (this.properties instanceof KubeJSBlockProperties kp) {
         for (Property<?> property : kp.blockBuilder.blockStateProperties) {
            builder.add(new Property[]{property});
         }

         kp.blockBuilder.blockStateProperties = Collections.unmodifiableSet(kp.blockBuilder.blockStateProperties);
      }
   }

   @Deprecated
   public FluidState getFluidState(BlockState state) {
      return state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      if (this.blockBuilder.placementStateModification != null) {
         BlockStateModifyPlacementCallback callbackJS = new BlockStateModifyPlacementCallback(context, this);
         if (safeCallback(
            context.getLevel(),
            this.blockBuilder.placementStateModification,
            callbackJS,
            "Error while modifying BlockState placement of " + this.blockBuilder.id
         )) {
            return callbackJS.getState();
         }
      }

      return !this.blockBuilder.canBeWaterlogged()
         ? this.defaultBlockState()
         : (BlockState)this.defaultBlockState()
            .setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
      if (this.blockBuilder.canBeReplacedFunction != null) {
         CanBeReplacedCallback callbackJS = new CanBeReplacedCallback(context, blockState);
         return this.blockBuilder.canBeReplacedFunction.test(callbackJS);
      } else {
         return super.canBeReplaced(blockState, context);
      }
   }

   @Deprecated
   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      if (state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false)) {
         world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return state;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
      return this.blockBuilder.transparent || !state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false);
   }

   @Deprecated
   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (this.blockBuilder.randomTickCallback != null) {
         RandomTickCallback callback = new RandomTickCallback(level.kjs$getBlock(pos), random);
         safeCallback(level, this.blockBuilder.randomTickCallback, callback, "Error while random ticking custom block ");
      }
   }

   public boolean isRandomlyTicking(BlockState state) {
      return this.blockBuilder.randomTickCallback != null;
   }

   @Deprecated
   public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return this.blockBuilder.transparent ? Shapes.empty() : super.getVisualShape(state, level, pos, ctx);
   }

   @Deprecated
   public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
      return this.blockBuilder.transparent ? 1.0F : super.getShadeBrightness(state, level, pos);
   }

   @Deprecated
   public boolean skipRendering(BlockState state, BlockState state2, Direction direction) {
      return this.blockBuilder.transparent ? state2.is(this) || super.skipRendering(state, state2, direction) : super.skipRendering(state, state2, direction);
   }

   private static <T> boolean safeCallback(ScriptTypeHolder holder, Consumer<T> consumer, T value, String errorMessage) {
      try {
         consumer.accept(value);
         return true;
      } catch (Throwable var5) {
         holder.kjs$getScriptType().console.error(errorMessage, var5);
         return false;
      }
   }

   public boolean canPlaceLiquid(Player player, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return this.blockBuilder.canBeWaterlogged() ? super.canPlaceLiquid(player, blockGetter, blockPos, blockState, fluid) : false;
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return this.blockBuilder.canBeWaterlogged() ? super.placeLiquid(levelAccessor, blockPos, blockState, fluidState) : false;
   }

   public ItemStack pickupBlock(Player player, LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      return this.blockBuilder.canBeWaterlogged() ? super.pickupBlock(player, levelAccessor, blockPos, blockState) : ItemStack.EMPTY;
   }

   public Optional<SoundEvent> getPickupSound() {
      return this.blockBuilder.canBeWaterlogged() ? super.getPickupSound() : Optional.empty();
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (this.blockBuilder.insideCallback != null) {
         EntityBlockCallback callbackJS = new EntityBlockCallback(level, entity, pos, state);
         safeCallback(level, this.blockBuilder.insideCallback, callbackJS, "Error while an entity was inside a custom block ");
      } else {
         super.entityInside(state, level, pos, entity);
      }
   }

   public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
      if (this.blockBuilder.stepOnCallback != null) {
         EntityBlockCallback callbackJS = new EntityBlockCallback(level, entity, blockPos, blockState);
         safeCallback(level, this.blockBuilder.stepOnCallback, callbackJS, "Error while an entity stepped on custom block ");
      } else {
         super.stepOn(level, blockPos, blockState, entity);
      }
   }

   public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
      if (this.blockBuilder.fallOnCallback != null) {
         EntityFallenOnBlockCallback callbackJS = new EntityFallenOnBlockCallback(level, entity, blockPos, blockState, f);
         safeCallback(level, this.blockBuilder.fallOnCallback, callbackJS, "Error while an entity fell on custom block ");
      } else {
         super.fallOn(level, blockState, blockPos, entity, f);
      }
   }

   public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
      if (this.blockBuilder.afterFallenOnCallback != null) {
         AfterEntityFallenOnBlockCallback callbackJS = new AfterEntityFallenOnBlockCallback(blockGetter, entity);
         safeCallback(entity, this.blockBuilder.afterFallenOnCallback, callbackJS, "Error while bouncing entity from custom block ");
         if (!callbackJS.hasChangedVelocity()) {
            super.updateEntityAfterFallOn(blockGetter, entity);
         }
      } else {
         super.updateEntityAfterFallOn(blockGetter, entity);
      }
   }

   public void wasExploded(Level level, BlockPos blockPos, Explosion explosion) {
      if (this.blockBuilder.explodedCallback != null) {
         BlockExplodedCallback callbackJS = new BlockExplodedCallback(level, blockPos, explosion);
         safeCallback(level, this.blockBuilder.explodedCallback, callbackJS, "Error while exploding custom block ");
      } else {
         super.wasExploded(level, blockPos, explosion);
      }
   }

   public BlockState rotate(BlockState blockState, Rotation rotation) {
      if (this.blockBuilder.rotateStateModification != null) {
         BlockStateRotateCallback callbackJS = new BlockStateRotateCallback(blockState, rotation);
         if (safeCallback(ScriptType.STARTUP, this.blockBuilder.rotateStateModification, callbackJS, "Error while rotating BlockState of ")) {
            return callbackJS.getState();
         }
      }

      return super.rotate(blockState, rotation);
   }

   public BlockState mirror(BlockState blockState, Mirror mirror) {
      if (this.blockBuilder.mirrorStateModification != null) {
         BlockStateMirrorCallback callbackJS = new BlockStateMirrorCallback(blockState, mirror);
         if (safeCallback(ScriptType.STARTUP, this.blockBuilder.mirrorStateModification, callbackJS, "Error while mirroring BlockState of ")) {
            return callbackJS.getState();
         }
      }

      return super.mirror(blockState, mirror);
   }

   public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (this.blockBuilder.rightClick != null) {
         if (!level.isClientSide()) {
            this.blockBuilder.rightClick.accept(new BlockRightClickedKubeEvent(stack, player, hand, pos, hit.getDirection(), hit));
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bl) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof KubeBlockEntity entity) {
            if (level instanceof ServerLevel s) {
               for (BlockEntityAttachmentHolder entry : entity.attachmentArray) {
                  entry.attachment().onRemove(s, entity, newState);
               }
            }

            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, bl);
      }
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      if (livingEntity != null && !level.isClientSide() && level.getBlockEntity(blockPos) instanceof KubeBlockEntity e) {
         e.placerId = livingEntity.getUUID();
      }
   }

   public static class Builder extends BlockBuilder {
      public Builder(ResourceLocation i) {
         super(i);
      }

      public Block createObject() {
         return (Block)(this.blockEntityInfo != null ? new BasicKubeBlock.WithEntity(this) : new BasicKubeBlock(this));
      }
   }

   public static class WithEntity extends BasicKubeBlock implements EntityBlock {
      public WithEntity(BlockBuilder p) {
         super(p);
      }

      @Nullable
      public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
         return this.blockBuilder.blockEntityInfo.createBlockEntity(pos, state);
      }

      @Nullable
      public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
         return this.blockBuilder.blockEntityInfo.getTicker(level);
      }
   }
}
