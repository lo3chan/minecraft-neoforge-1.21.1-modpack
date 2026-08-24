package net.mehvahdjukaar.amendments.mixins;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.EnumMap;
import net.mehvahdjukaar.amendments.AmendmentsClient;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({WallSignBlock.class})
public abstract class WallSignBlockMixin extends Block {
   @Unique
   private static final EnumMap<Direction, VoxelShape> AMENDMENTS_VISUAL_SHAPE = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.NORTH,
         Block.box(0.0, 4.0, 14.0, 16.0, 13.0, 16.0),
         Direction.SOUTH,
         Block.box(0.0, 4.0, 0.0, 16.0, 13.0, 2.0),
         Direction.EAST,
         Block.box(0.0, 4.0, 0.0, 2.0, 13.0, 16.0),
         Direction.WEST,
         Block.box(14.0, 4.0, 0.0, 16.0, 13.0, 16.0)
      )
   );

   public WallSignBlockMixin(Properties properties) {
      super(properties);
   }

   @ModifyReturnValue(
      method = {"getShape"},
      at = {@At("RETURN")}
   )
   public VoxelShape getShape(VoxelShape original, BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return level instanceof Level l && l.isClientSide && ClientConfigs.isPixelConsistentSign(state)
         ? AMENDMENTS_VISUAL_SHAPE.get(state.getValue(WallSignBlock.FACING))
         : original;
   }

   public RenderShape getRenderShape(BlockState state) {
      return PlatHelper.getPhysicalSide().isClient() && AmendmentsClient.WAS_INIT && ClientConfigs.isPixelConsistentSign(state)
         ? RenderShape.MODEL
         : super.getRenderShape(state);
   }
}
