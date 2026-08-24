package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.mehvahdjukaar.amendments.common.tile.CeilingBannerBlockTile;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.mehvahdjukaar.moonlight.api.map.ExpandedMapData;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CeilingBannerBlock extends AbstractBannerBlock {
   public static final MapCodec<CeilingBannerBlock> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(DyeColor.CODEC.fieldOf("color").forGetter(AbstractBannerBlock::getColor), propertiesCodec()).apply(i, CeilingBannerBlock::new)
   );
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape SHAPE_X = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_Z = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
   private String descriptionId;

   public CeilingBannerBlock(DyeColor color, Properties properties) {
      super(color, properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHED, false));
   }

   protected MapCodec<? extends CeilingBannerBlock> codec() {
      return CODEC;
   }

   public List<ItemStack> getDrops(BlockState blockState, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return BannerBlock.byColor(this.getColor()).defaultBlockState().getDrops(builder);
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      BlockState above = world.getBlockState(pos.above());
      return state.getValue(ATTACHED) ? this.canAttach(state, above) : above.isSolid();
   }

   private boolean canAttach(BlockState state, BlockState above) {
      return CompatHandler.SUPPLEMENTARIES ? SuppCompat.canBannerAttachToRope(state, above) : false;
   }

   public BlockState updateShape(BlockState myState, Direction direction, BlockState otherState, LevelAccessor world, BlockPos myPos, BlockPos otherPos) {
      return direction == Direction.UP && !myState.canSurvive(world, myPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(myState, direction, myState, world, myPos, otherPos);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? SHAPE_X : SHAPE_Z;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      if (context.getClickedFace() == Direction.DOWN) {
         BlockState blockstate = this.defaultBlockState();
         LevelReader world = context.getLevel();
         BlockPos blockpos = context.getClickedPos();
         blockstate = (BlockState)blockstate.setValue(FACING, context.getHorizontalDirection().getOpposite());
         boolean attached = this.canAttach(blockstate, world.getBlockState(blockpos.above()));
         blockstate = (BlockState)blockstate.setValue(ATTACHED, attached);
         if (blockstate.canSurvive(world, blockpos)) {
            return blockstate;
         }
      }

      return null;
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, ATTACHED});
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new CeilingBannerBlockTile(pPos, pState, this.getColor());
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      if (item instanceof MapItem) {
         if (!level.isClientSide && MapItem.getSavedData(itemstack, level) instanceof ExpandedMapData data) {
            data.ml$toggleCustomDecoration(level, pos);
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         Block baseBlock = BlocksColorAPI.getColoredBlock("banner", this.getColor());
         if (baseBlock == null) {
            return "block.amendments.ceiling_banner";
         }

         this.descriptionId = baseBlock.getDescriptionId();
      }

      return this.descriptionId;
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return super.getCloneItemStack(level, pos, state);
   }
}
