package net.joefoxe.hexerei.block.custom;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.HerbJarContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.items.JarHandler;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HerbJar extends Block implements ITileEntity<HerbJarTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty GUI_RENDER = BooleanProperty.create("gui_render");
   public static final BooleanProperty DYED = BooleanProperty.create("dyed");
   public static final DyedItemColor DEFAULT_COLOR = new DyedItemColor(13218201, false);
   public static final VoxelShape SHAPE = Stream.of(
         Block.box(5.0, -0.5, 5.0, 11.0, 0.0, 11.0),
         Block.box(5.5, 13.0, 5.5, 10.5, 15.0, 10.5),
         Block.box(4.5, 12.0, 10.5, 11.5, 14.0, 11.5),
         Block.box(4.5, 12.0, 4.5, 11.5, 14.0, 5.5),
         Block.box(4.5, 12.0, 5.5, 5.5, 14.0, 10.5),
         Block.box(10.5, 12.0, 5.5, 11.5, 14.0, 10.5),
         Block.box(4.0, 0.0, 4.0, 12.0, 11.0, 12.0),
         Block.box(5.0, 11.0, 5.0, 11.0, 12.0, 11.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

      for (Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis() == Axis.Y) {
            BlockState blockstate = (BlockState)this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
               return (BlockState)((BlockState)((BlockState)((BlockState)blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER))
                        .setValue(GUI_RENDER, false))
                     .setValue(DYED, HexereiUtil.getDyeColor(context.getItemInHand()) != 4337438))
                  .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
         stack.useOn(new UseOnContext(player, hand, hitResult));
         return ItemInteractionResult.SUCCESS;
      } else {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if (tileEntity instanceof HerbJarTile && ((Direction)state.getValue(HorizontalDirectionalBlock.FACING)).getOpposite() == hitResult.getDirection()) {
            ((HerbJarTile)tileEntity).interactPutItems(player);
            return ItemInteractionResult.SUCCESS;
         } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
         }
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!player.isShiftKeyDown() && ((Direction)state.getValue(HorizontalDirectionalBlock.FACING)).getOpposite() == hitResult.getDirection()) {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      } else {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if (!level.isClientSide()) {
            if (!(tileEntity instanceof HerbJarTile)) {
               throw new IllegalStateException("Our Container provider is missing!");
            }

            ((HerbJarTile)tileEntity).sync();
            MenuProvider containerProvider = this.createContainerProvider(level, pos, this.getCloneItemStack(state, hitResult, level, pos, player));
            player.openMenu(
               containerProvider,
               b -> b.writeNbt(this.getCloneItemStack(level, pos, state).save(level.registryAccess())).writeBlockPos(tileEntity.getBlockPos())
            );
         }

         return InteractionResult.SUCCESS;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileentity = level.getBlockEntity(pos);
         if (tileentity != null) {
            ItemStack cloneItemStack = this.getCloneItemStack(state, new BlockHitResult(pos.getCenter(), Direction.UP, pos, true), level, pos, (Player)null);
            if (!level.isClientSide()) {
               popResource(level, pos, cloneItemStack);
            }
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   protected BlockHitResult rayTraceEyeLevel(Level world, Player player, double length) {
      Vec3 eyePos = player.getEyePosition(1.0F);
      Vec3 lookPos = player.getViewVector(1.0F);
      Vec3 endPos = eyePos.add(lookPos.x * length, lookPos.y * length, lookPos.z * length);
      ClipContext context = new ClipContext(eyePos, endPos, net.minecraft.world.level.ClipContext.Block.OUTLINE, Fluid.NONE, player);
      return world.clip(context);
   }

   public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) {
      BlockHitResult rayResult = this.rayTraceEyeLevel(worldIn, playerIn, playerIn.blockInteractionRange() + 1.0);
      if (rayResult.getType() != Type.MISS) {
         Direction side = rayResult.getDirection();
         BlockEntity tile = worldIn.getBlockEntity(pos);
         HerbJarTile herbJarTile = null;
         if (tile instanceof HerbJarTile) {
            herbJarTile = (HerbJarTile)tile;
         }

         if (((Direction)state.getValue(HorizontalDirectionalBlock.FACING)).getOpposite() == rayResult.getDirection()) {
            ItemStack item;
            if (playerIn.isShiftKeyDown()) {
               item = herbJarTile.takeItems(0, herbJarTile.itemHandler.getStackInSlot(0).getCount());
            } else {
               item = herbJarTile.takeItems(0, 1);
            }

            if (!item.isEmpty()) {
               if (!playerIn.getInventory().add(item)) {
                  this.dropItemStack(worldIn, pos.relative(side), playerIn, item);
                  worldIn.sendBlockUpdated(pos, state, state, 3);
               } else {
                  worldIn.playSound(
                     null,
                     pos.getX() + 0.5F,
                     pos.getY() + 0.5F,
                     pos.getZ() + 0.5F,
                     SoundEvents.ITEM_PICKUP,
                     SoundSource.PLAYERS,
                     0.2F,
                     ((worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
                  );
               }
            }

            super.attack(state, worldIn, pos, playerIn);
         }
      }
   }

   private void dropItemStack(Level world, BlockPos pos, Player player, @Nonnull ItemStack stack) {
      ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5F, pos.getY() + 0.3F, pos.getZ() + 0.5F, stack);
      Vec3 motion = entity.getDeltaMovement();
      entity.push(-motion.x, -motion.y, -motion.z);
      world.addFreshEntity(entity);
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public HerbJar(Properties properties) {
      super(properties.noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HANGING, Boolean.FALSE))
                  .setValue(WATERLOGGED, Boolean.FALSE))
               .setValue(GUI_RENDER, false))
            .setValue(DYED, false)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, HANGING, WATERLOGGED, GUI_RENDER, DYED});
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return super.getCloneItemStack(level, pos, state);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack item = new ItemStack(this);
      Optional<HerbJarTile> tileEntityOptional = Optional.ofNullable(this.getBlockEntity(level, pos));
      CompoundTag tag = ((CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      JarHandler empty = tileEntityOptional.<JarHandler>map(herb_jar -> herb_jar.itemHandler).orElse(new JarHandler(1, 1024));
      CompoundTag inv = tileEntityOptional.<CompoundTag>map(herb_jar -> herb_jar.itemHandler.serializeNBT(level.registryAccess())).orElse(new CompoundTag());
      if (!empty.getStackInSlot(0).isEmpty()) {
         tag.put("Inventory", inv);
      }

      tileEntityOptional.ifPresent(herbJarTile -> {
         if (herbJarTile.hasDyeColor()) {
            item.set(DataComponents.DYED_COLOR, herbJarTile.dyeColor);
         }
      });
      int toggled = tileEntityOptional.<Integer>map(herbJarTile -> herbJarTile.buttonToggled).orElse(0);
      if (toggled == 1) {
         tag.putInt("ButtonToggled", toggled);
      }

      if (!tag.isEmpty()) {
         item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      Component customName = tileEntityOptional.map(HerbJarTile::getCustomName).orElse(null);
      if (customName != null && !customName.getString().isEmpty()) {
         item.set(DataComponents.CUSTOM_NAME, customName);
      }

      return item;
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(worldIn, pos, state, placer, stack);
      if (stack.has(DataComponents.CUSTOM_NAME) && worldIn.getBlockEntity(pos) instanceof HerbJarTile herbJarTile) {
         herbJarTile.customName = stack.getHoverName();
      }

      if (!worldIn.isClientSide()) {
         this.withTileEntityDo(worldIn, pos, te -> {
            CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            te.readInventory(worldIn.registryAccess(), tag.getCompound("Inventory"));
            if (stack.has(DataComponents.DYED_COLOR)) {
               te.dyeColor = (DyedItemColor)stack.getOrDefault(DataComponents.DYED_COLOR, DEFAULT_COLOR);
            }

            te.buttonToggled = tag.getInt("ButtonToggled");
         });
      }
   }

   public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
      if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
         worldIn.setBlock(pos, (BlockState)state.setValue(WATERLOGGED, Boolean.TRUE), 3);
         worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
         return true;
      } else {
         return false;
      }
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !stateIn.canSurvive(worldIn, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      Direction direction = getBlockConnected(state).getOpposite();
      return Block.canSupportCenter(worldIn, pos.relative(direction), direction.getOpposite());
   }

   protected static Direction getBlockConnected(BlockState state) {
      return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
   }

   private MenuProvider createContainerProvider(final Level worldIn, final BlockPos pos, final ItemStack stack) {
      return new MenuProvider() {
         @Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new HerbJarContainer(i, stack, worldIn, pos, playerInventory, playerEntity);
         }

         public Component getDisplayName() {
            return ((HerbJarTile)worldIn.getBlockEntity(pos)).customName != null
               ? Component.translatable(((HerbJarTile)worldIn.getBlockEntity(pos)).customName.getString())
               : Component.translatable("screen.hexerei.herb_jar");
         }
      };
   }

   @Override
   public Class<HerbJarTile> getTileEntityClass() {
      return HerbJarTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new HerbJarTile((BlockEntityType<?>)ModTileEntities.HERB_JAR_TILE.get(), pos, state);
   }
}
