package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class PillarBlockBuilder extends BlockBuilder {
   private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/cube_column");

   public PillarBlockBuilder(ResourceLocation i) {
      super(i);
   }

   @Override
   protected void generateBlockState(VariantBlockStateGenerator bs) {
      ResourceLocation modelLocation = this.parentModel == null ? this.id.withPath(ID.BLOCK) : this.parentModel;
      bs.variant("axis=x", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation).x(90).y(90)));
      bs.variant("axis=y", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation)));
      bs.variant("axis=z", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelLocation).x(90)));
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator gen) {
      gen.blockModel(this.id, mg -> {
         String side = this.textures.getOrDefault("side", this.baseTexture);
         mg.texture("side", side);
         mg.texture("end", this.textures.getOrDefault("end", this.newID("block/", "_top").toString()));
         mg.parent(this.parentModel == null ? MODEL : this.parentModel);
      });
   }

   public Block createObject() {
      return (Block)(this.blockEntityInfo != null ? new PillarBlockBuilder.WithEntity(this) : new PillarBlockBuilder.PillarKubeBlock(this));
   }

   public static class PillarKubeBlock extends BasicKubeBlock {
      public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
      @Nullable
      public final VoxelShape shapeX = this.hasCustomShape() ? rotateShape(this.shape, Axis.X) : null;
      @Nullable
      public final VoxelShape shapeY = this.hasCustomShape() ? rotateShape(this.shape, Axis.Y) : null;
      @Nullable
      public final VoxelShape shapeZ = this.hasCustomShape() ? rotateShape(this.shape, Axis.Z) : null;

      public PillarKubeBlock(BlockBuilder p) {
         super(p);
      }

      private static VoxelShape rotateShape(VoxelShape shape, Axis axis) {
         List<AABB> newShapes = new ArrayList<>();
         switch (axis) {
            case Y:
               return shape;
            case X:
               shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(y1, z1, x1, y2, z2, x2)));
               break;
            case Z:
               shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(x1, z1, 1.0 - y2, x2, z2, 1.0 - y1)));
               break;
            default:
               throw new IllegalArgumentException("Cannot rotate around axis " + axis.getName());
         }

         return BlockBuilder.createShape(newShapes);
      }

      @Override
      protected void createBlockStateDefinition(@NotNull net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
         builder.add(new Property[]{AXIS});
         super.createBlockStateDefinition(builder);
      }

      @Override
      public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
         BlockState state = (BlockState)this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
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
         VoxelShape var10000;
         if (this.hasCustomShape()) {
            switch ((Axis)state.getValue(AXIS)) {
               case Y:
                  assert this.shapeY != null;

                  var10000 = this.shapeY;
                  break;
               case X:
                  assert this.shapeX != null;

                  var10000 = this.shapeX;
                  break;
               case Z:
                  assert this.shapeZ != null;

                  var10000 = this.shapeZ;
                  break;
               default:
                  throw new MatchException(null, null);
            }
         } else {
            var10000 = this.shape;
         }

         return var10000;
      }
   }

   public static class WithEntity extends PillarBlockBuilder.PillarKubeBlock implements EntityBlock {
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
