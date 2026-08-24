package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class CardinalBlockBuilder extends BlockBuilder {
   private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/orientable");
   private static final ResourceLocation BOTTOM_MODEL = ResourceLocation.withDefaultNamespace("block/orientable_with_bottom");

   public CardinalBlockBuilder(ResourceLocation i) {
      super(i);
   }

   @Override
   protected void generateBlockState(VariantBlockStateGenerator bs) {
      ResourceLocation modelLocation = this.parentModel == null ? this.id.withPath(ID.BLOCK) : this.parentModel;
      bs.variant("facing=north", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation)));
      bs.variant("facing=east", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation).y(90)));
      bs.variant("facing=south", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation).y(180)));
      bs.variant("facing=west", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation).y(270)));
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator gen) {
      gen.blockModel(this.id, mg -> {
         String side = this.textures.getOrDefault("side", this.baseTexture);
         mg.texture("side", side);
         mg.texture("front", this.textures.getOrDefault("front", this.newID("block/", "_front").toString()));
         mg.texture("particle", this.textures.getOrDefault("particle", side));
         mg.texture("top", this.textures.getOrDefault("top", side));
         if (this.textures.containsKey("bottom")) {
            mg.parent(BOTTOM_MODEL);
            mg.texture("bottom", this.textures.get("bottom"));
         } else {
            mg.parent(MODEL);
         }

         if (this.parentModel != null) {
            mg.parent(this.parentModel);
         }
      });
   }

   public Block createObject() {
      return (Block)(this.blockEntityInfo != null ? new CardinalBlockBuilder.WithEntity(this) : new CardinalBlockBuilder.CardinalKubeBlock(this));
   }

   public static class CardinalKubeBlock extends BasicKubeBlock {
      public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
      public final Map<Direction, VoxelShape> shapes = new HashMap<>();

      public CardinalKubeBlock(BlockBuilder p) {
         super(p);
         if (this.hasCustomShape()) {
            Plane.HORIZONTAL.forEach(direction -> this.shapes.put(direction, rotateShape(this.shape, direction)));
         }
      }

      private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
         List<AABB> newShapes = new ArrayList<>();
         switch (direction) {
            case NORTH:
               return shape;
            case SOUTH:
               shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1)));
               break;
            case WEST:
               shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1)));
               break;
            case EAST:
               shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(1.0 - z2, y1, x1, 1.0 - z1, y2, x2)));
               break;
            default:
               throw new IllegalArgumentException("Cannot rotate around direction " + direction.getName());
         }

         return BlockBuilder.createShape(newShapes);
      }

      @Override
      protected void createBlockStateDefinition(@NotNull net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
         builder.add(new Property[]{FACING});
         super.createBlockStateDefinition(builder);
      }

      @Override
      public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
         BlockState state = (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
         if (this.blockBuilder.canBeWaterlogged()) {
            state = (BlockState)state.setValue(
               BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER
            );
         }

         return state;
      }

      private boolean hasCustomShape() {
         return this.shape != Shapes.block();
      }

      @Deprecated
      @Override
      public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
         return this.hasCustomShape() ? this.shapes.get(state.getValue(FACING)) : this.shape;
      }
   }

   public static class WithEntity extends CardinalBlockBuilder.CardinalKubeBlock implements EntityBlock {
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
