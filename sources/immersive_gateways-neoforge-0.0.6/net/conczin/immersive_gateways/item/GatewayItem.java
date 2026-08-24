package net.conczin.immersive_gateways.item;

import java.util.HashSet;
import java.util.Set;
import net.conczin.immersive_gateways.Blocks;
import net.conczin.immersive_gateways.block.GatewayBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class GatewayItem extends Item {
   public GatewayItem(Properties properties) {
      super(properties);
   }

   private BoundingBox getBoundingBox(Level level, BlockPos pos) {
      Set<BlockPos> done = new HashSet<>();
      Set<BlockPos> todo = new HashSet<>();
      todo.add(pos);
      int minX = pos.getX();
      int minY = pos.getY();
      int minZ = pos.getZ();
      int maxX = pos.getX();
      int maxY = pos.getY();
      int maxZ = pos.getZ();

      while (!todo.isEmpty()) {
         BlockPos current = todo.iterator().next();
         todo.remove(current);
         done.add(current);
         if (!level.getBlockState(current).isAir()) {
            for (Direction direction : Direction.values()) {
               BlockPos neighbor = current.relative(direction);
               if (!done.contains(neighbor)) {
                  done.add(neighbor);
                  todo.add(neighbor);
               }
            }

            minX = Math.min(minX, current.getX());
            minY = Math.min(minY, current.getY());
            minZ = Math.min(minZ, current.getZ());
            maxX = Math.max(maxX, current.getX());
            maxY = Math.max(maxY, current.getY());
            maxZ = Math.max(maxZ, current.getZ());
         }
      }

      return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction direction = context.getClickedFace();
      Axis axis = context.getPlayer() != null && direction.getAxis().isVertical() ? Direction.fromYRot(context.getPlayer().getYRot() + 90.0).getAxis() : Axis.Y;
      if (level.getBlockEntity(pos) instanceof StructureBlockEntity structure) {
         structure.setMode(StructureMode.SAVE);
         BoundingBox boundingBox = this.getBoundingBox(level, pos);
         structure.setStructurePos(new BlockPos(boundingBox.minX() - pos.getX(), boundingBox.minY() - pos.getY(), boundingBox.minZ() - pos.getZ()));
         structure.setStructureSize(
            new Vec3i(boundingBox.maxX() - boundingBox.minX() + 1, boundingBox.maxY() - boundingBox.minY() + 1, boundingBox.maxZ() - boundingBox.minZ() + 1)
         );
         structure.saveStructure();
         return InteractionResult.CONSUME;
      } else {
         for (int i = 0; i < 8; i++) {
            pos = pos.offset(direction.getNormal());
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
               break;
            }

            BlockState blockState = (BlockState)Blocks.GATEWAY.defaultBlockState().setValue(GatewayBlock.AXIS, axis);
            level.setBlock(pos, blockState, 3);
         }

         return InteractionResult.CONSUME;
      }
   }
}
