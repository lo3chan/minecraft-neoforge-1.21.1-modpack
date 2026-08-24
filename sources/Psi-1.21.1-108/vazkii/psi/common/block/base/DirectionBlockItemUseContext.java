package vazkii.psi.common.block.base;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class DirectionBlockItemUseContext extends BlockPlaceContext {
   private final Direction horizontalFacing;

   public DirectionBlockItemUseContext(UseOnContext itemUseContext, Direction horizontalFacing) {
      super(itemUseContext);
      this.horizontalFacing = horizontalFacing;
   }

   @NotNull
   public Direction getHorizontalDirection() {
      return this.horizontalFacing.getAxis() == Axis.Y ? Direction.NORTH : this.horizontalFacing;
   }

   @NotNull
   public Direction getNearestLookingDirection() {
      return this.getHitResult().getDirection();
   }

   @NotNull
   public Direction[] getNearestLookingDirections() {
      return switch (this.getHitResult().getDirection()) {
         case UP -> new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
         case NORTH -> new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
         case SOUTH -> new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
         case WEST -> new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
         case EAST -> new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
         default -> new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
      };
   }
}
