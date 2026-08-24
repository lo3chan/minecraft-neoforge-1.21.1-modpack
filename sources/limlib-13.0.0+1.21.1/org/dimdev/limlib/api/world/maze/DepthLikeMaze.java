package org.dimdev.limlib.api.world.maze;

import java.util.Stack;
import net.minecraft.nbt.CompoundTag;

public abstract class DepthLikeMaze extends MazeComponent {
   public Stack<MazeComponent.Vec2i> stack = new Stack<>();
   public int visitedCells = 0;

   public DepthLikeMaze(int width, int height) {
      super(width, height);
   }

   public boolean hasNeighbourUp(MazeComponent.Vec2i vec) {
      return this.fits(vec.up()) && !this.visited(vec.up());
   }

   public boolean hasNeighbourRight(MazeComponent.Vec2i vec) {
      return this.fits(vec.right()) && !this.visited(vec.right());
   }

   public boolean hasNeighbourDown(MazeComponent.Vec2i vec) {
      return this.fits(vec.down()) && !this.visited(vec.down());
   }

   public boolean hasNeighbourLeft(MazeComponent.Vec2i vec) {
      return this.fits(vec.left()) && !this.visited(vec.left());
   }

   public boolean hasNeighbours(MazeComponent.Vec2i vec) {
      return this.hasNeighbourUp(vec) || this.hasNeighbourRight(vec) || this.hasNeighbourDown(vec) || this.hasNeighbourLeft(vec);
   }

   public boolean hasNeighbour(MazeComponent.Vec2i vec, MazeComponent.Face face) {
      return switch (face) {
         case UP -> this.hasNeighbourUp(vec);
         case DOWN -> this.hasNeighbourDown(vec);
         case LEFT -> this.hasNeighbourLeft(vec);
         case RIGHT -> this.hasNeighbourRight(vec);
      };
   }

   public CompoundTag visit(MazeComponent.Vec2i vec) {
      return this.visit(vec, true);
   }

   public CompoundTag visit(MazeComponent.Vec2i vec, boolean visited) {
      CompoundTag appendage = new CompoundTag();
      appendage.putBoolean("visited", visited);
      this.cellState(vec).getExtra().put("visited", appendage);
      return appendage;
   }

   public boolean visited(MazeComponent.Vec2i vec) {
      return this.cellState(vec).getExtra().containsKey("visited") ? this.cellState(vec).getExtra().get("visited").getBoolean("visited") : false;
   }
}
