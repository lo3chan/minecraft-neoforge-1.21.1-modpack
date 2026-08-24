package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.tileentity.ModChestBlockEntity;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModChest extends AbstractChestBlock<ModChestBlockEntity> implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
   public static final EnumProperty<ModChest.WoodType> WOOD_TYPE = EnumProperty.create("wood_type", ModChest.WoodType.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int EVENT_SET_OPEN_COUNT = 1;
   protected static final int AABB_OFFSET = 1;
   protected static final int AABB_HEIGHT = 14;
   protected static final VoxelShape NORTH_AABB = Block.box(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
   protected static final VoxelShape SOUTH_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
   protected static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
   protected static final VoxelShape EAST_AABB = Block.box(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
   protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
   private static final Combiner<ModChestBlockEntity, Optional<Container>> CHEST_COMBINER = new Combiner<ModChestBlockEntity, Optional<Container>>() {
      public Optional<Container> acceptDouble(ModChestBlockEntity p_51591_, ModChestBlockEntity p_51592_) {
         return Optional.of(new CompoundContainer(p_51591_, p_51592_));
      }

      public Optional<Container> acceptSingle(ModChestBlockEntity p_51589_) {
         return Optional.of(p_51589_);
      }

      public Optional<Container> acceptNone() {
         return Optional.empty();
      }
   };
   private static final Combiner<ModChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new Combiner<ModChestBlockEntity, Optional<MenuProvider>>() {
      public Optional<MenuProvider> acceptDouble(final ModChestBlockEntity p_51604_, final ModChestBlockEntity p_51605_) {
         final Container container = new CompoundContainer(p_51604_, p_51605_);
         return Optional.of(new MenuProvider() {
            @Nullable
            public AbstractContainerMenu createMenu(int p_51622_, Inventory p_51623_, Player p_51624_) {
               if (p_51604_.canOpen(p_51624_) && p_51605_.canOpen(p_51624_)) {
                  p_51604_.unpackLootTable(p_51623_.player);
                  p_51605_.unpackLootTable(p_51623_.player);
                  return ChestMenu.sixRows(p_51622_, p_51623_, container);
               } else {
                  return null;
               }
            }

            public Component getDisplayName() {
               if (p_51604_.hasCustomName()) {
                  return p_51604_.getDisplayName();
               } else {
                  return (Component)(p_51605_.hasCustomName() ? p_51605_.getDisplayName() : Component.translatable("container.chestDouble"));
               }
            }
         });
      }

      public Optional<MenuProvider> acceptSingle(ModChestBlockEntity p_51602_) {
         return Optional.of(p_51602_);
      }

      public Optional<MenuProvider> acceptNone() {
         return Optional.empty();
      }
   };
   public static final MapCodec<ModChest> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(propertiesCodec(), ModChest.WoodType.CODEC.fieldOf("type").forGetter(c -> c.type)).apply(instance, ModChest::new)
   );
   public ModChest.WoodType type;

   public ModChest(Properties pProperties, ModChest.WoodType type) {
      super(pProperties, ModTileEntities.CHEST_TILE::get);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                  .setValue(TYPE, ChestType.SINGLE))
               .setValue(WATERLOGGED, false))
            .setValue(WOOD_TYPE, type)
      );
      this.type = type;
   }

   public static BlockType getBlockType(BlockState p_51583_) {
      ChestType chesttype = (ChestType)p_51583_.getValue(TYPE);
      if (chesttype == ChestType.SINGLE) {
         return BlockType.SINGLE;
      } else {
         return chesttype == ChestType.RIGHT ? BlockType.FIRST : BlockType.SECOND;
      }
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      if (pFacingState.is(this) && pFacing.getAxis().isHorizontal()) {
         ChestType chesttype = (ChestType)pFacingState.getValue(TYPE);
         if (pState.getValue(TYPE) == ChestType.SINGLE
            && chesttype != ChestType.SINGLE
            && pState.getValue(FACING) == pFacingState.getValue(FACING)
            && getConnectedDirection(pFacingState) == pFacing.getOpposite()) {
            return (BlockState)pState.setValue(TYPE, chesttype.getOpposite());
         }
      } else if (getConnectedDirection(pState) == pFacing) {
         return (BlockState)pState.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (pState.getValue(TYPE) == ChestType.SINGLE) {
         return AABB;
      } else {
         return switch (getConnectedDirection(pState)) {
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            default -> NORTH_AABB;
         };
      }
   }

   public static Direction getConnectedDirection(BlockState p_51585_) {
      Direction direction = (Direction)p_51585_.getValue(FACING);
      return p_51585_.getValue(TYPE) == ChestType.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      ChestType chesttype = ChestType.SINGLE;
      Direction direction = pContext.getHorizontalDirection().getOpposite();
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      boolean flag = pContext.isSecondaryUseActive();
      Direction direction1 = pContext.getClickedFace();
      if (direction1.getAxis().isHorizontal() && flag) {
         Direction direction2 = this.candidatePartnerFacing(pContext, direction1.getOpposite());
         if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
            direction = direction2;
            chesttype = direction2.getCounterClockWise() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (chesttype == ChestType.SINGLE && !flag) {
         if (direction == this.candidatePartnerFacing(pContext, direction.getClockWise())) {
            chesttype = ChestType.LEFT;
         } else if (direction == this.candidatePartnerFacing(pContext, direction.getCounterClockWise())) {
            chesttype = ChestType.RIGHT;
         }
      }

      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, direction)).setValue(TYPE, chesttype))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   @Nullable
   private Direction candidatePartnerFacing(BlockPlaceContext pContext, Direction pDirection) {
      BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos().relative(pDirection));
      return blockstate.is(this) && blockstate.getValue(TYPE) == ChestType.SINGLE ? (Direction)blockstate.getValue(FACING) : null;
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof Container) {
            Containers.dropContents(pLevel, pPos, (Container)blockentity);
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
         if (menuprovider != null) {
            player.openMenu(menuprovider);
            player.awardStat(this.getOpenChestStat());
            PiglinAi.angerNearbyPiglins(player, true);
         }

         return InteractionResult.CONSUME;
      }
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if (pLevel.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         MenuProvider menuprovider = this.getMenuProvider(pState, pLevel, pPos);
         if (menuprovider != null) {
            pPlayer.openMenu(menuprovider);
            pPlayer.awardStat(this.getOpenChestStat());
            PiglinAi.angerNearbyPiglins(pPlayer, true);
         }

         return InteractionResult.CONSUME;
      }
   }

   protected Stat<ResourceLocation> getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   public BlockEntityType<? extends ModChestBlockEntity> blockEntityType() {
      return (BlockEntityType<? extends ModChestBlockEntity>)this.blockEntityType.get();
   }

   @Nullable
   public static Container getContainer(ModChest pChest, BlockState pState, Level pLevel, BlockPos pPos, boolean pOverride) {
      return (Container)((Optional)pChest.combine(pState, pLevel, pPos, pOverride).apply(CHEST_COMBINER)).orElse(null);
   }

   protected MapCodec<? extends AbstractChestBlock<ModChestBlockEntity>> codec() {
      return CODEC;
   }

   public NeighborCombineResult<? extends ModChestBlockEntity> combine(BlockState pState, Level pLevel, BlockPos pPos, boolean pOverride) {
      BiPredicate<LevelAccessor, BlockPos> bipredicate;
      if (pOverride) {
         bipredicate = (p_51578_, p_51579_) -> false;
      } else {
         bipredicate = ModChest::isChestBlockedAt;
      }

      return DoubleBlockCombiner.combineWithNeigbour(
         (BlockEntityType)this.blockEntityType.get(), ModChest::getBlockType, ModChest::getConnectedDirection, FACING, pState, pLevel, pPos, bipredicate
      );
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
      return (MenuProvider)((Optional)this.combine(pState, pLevel, pPos, false).apply(MENU_PROVIDER_COMBINER)).orElse(null);
   }

   public static Combiner<ModChestBlockEntity, Float2FloatFunction> opennessCombiner(final LidBlockEntity pLid) {
      return new Combiner<ModChestBlockEntity, Float2FloatFunction>() {
         public Float2FloatFunction acceptDouble(ModChestBlockEntity p_51633_, ModChestBlockEntity p_51634_) {
            return p_51638_ -> Math.max(p_51633_.getOpenNess(p_51638_), p_51634_.getOpenNess(p_51638_));
         }

         public Float2FloatFunction acceptSingle(ModChestBlockEntity p_51631_) {
            return p_51631_::getOpenNess;
         }

         public Float2FloatFunction acceptNone() {
            return pLid::getOpenNess;
         }
      };
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new ModChestBlockEntity((BlockEntityType<?>)ModTileEntities.CHEST_TILE.get(), pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, this.blockEntityType(), ChestBlockEntity::lidAnimateTick) : null;
   }

   public static boolean isChestBlockedAt(LevelAccessor p_51509_, BlockPos p_51510_) {
      return isBlockedChestByBlock(p_51509_, p_51510_) || isCatSittingOnChest(p_51509_, p_51510_);
   }

   private static boolean isBlockedChestByBlock(BlockGetter pLevel, BlockPos pPos) {
      BlockPos blockpos = pPos.above();
      return pLevel.getBlockState(blockpos).isRedstoneConductor(pLevel, blockpos);
   }

   private static boolean isCatSittingOnChest(LevelAccessor pLevel, BlockPos pPos) {
      List<Cat> list = pLevel.getEntitiesOfClass(
         Cat.class, new AABB(pPos.getX(), pPos.getY() + 1, pPos.getZ(), pPos.getX() + 1, pPos.getY() + 2, pPos.getZ() + 1)
      );
      if (!list.isEmpty()) {
         for (Cat cat : list) {
            if (cat.isInSittingPose()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasAnalogOutputSignal(BlockState pState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(this, pBlockState, pLevel, pPos, false));
   }

   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.setValue(FACING, pRotation.rotate((Direction)pState.getValue(FACING)));
   }

   public BlockState mirror(BlockState pState, Mirror pMirror) {
      BlockState rotated = pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
      return pMirror == Mirror.NONE ? rotated : (BlockState)rotated.setValue(TYPE, ((ChestType)rotated.getValue(TYPE)).getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING, TYPE, WATERLOGGED, WOOD_TYPE});
   }

   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof ModChestBlockEntity) {
         ((ModChestBlockEntity)blockentity).recheckOpen();
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack stack = super.getCloneItemStack(level, pos, state);
      if (level.getBlockEntity(pos) instanceof ModChestBlockEntity modChestBlockEntity && modChestBlockEntity.hasCustomName()) {
         stack.set(DataComponents.CUSTOM_NAME, modChestBlockEntity.getCustomName());
      }

      return stack;
   }

   public static enum WoodType implements StringRepresentable {
      WILLOW("willow", ModBlocks.WILLOW_PLANKS),
      POLISHED_WILLOW("polished_willow", ModBlocks.POLISHED_WILLOW_PLANKS),
      WITCH_HAZEL("witch_hazel", ModBlocks.WITCH_HAZEL_PLANKS),
      POLISHED_WITCH_HAZEL("polished_witch_hazel", ModBlocks.POLISHED_WITCH_HAZEL_PLANKS),
      MAHOGANY("mahogany", ModBlocks.MAHOGANY_PLANKS),
      POLISHED_MAHOGANY("polished_mahogany", ModBlocks.POLISHED_MAHOGANY_PLANKS);

      public static final EnumCodec<ModChest.WoodType> CODEC = StringRepresentable.fromEnum(ModChest.WoodType::values);
      private final String name;
      private final Supplier<Block> supplierPlanks;

      private WoodType(String name, Supplier<Block> supplierPlanks) {
         this.name = name;
         this.supplierPlanks = supplierPlanks;
      }

      public String getName() {
         return this.name;
      }

      public Block getPlanks() {
         return this.supplierPlanks.get();
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static ModChest.WoodType byId(int id) {
         ModChest.WoodType[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }

      public static ModChest.WoodType byName(String aName) {
         ModChest.WoodType[] type = values();
         return Arrays.stream(type).filter(t -> t.getName().equals(aName)).findFirst().orElse(type[0]);
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
