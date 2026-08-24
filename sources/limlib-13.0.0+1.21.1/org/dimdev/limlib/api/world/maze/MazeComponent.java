package org.dimdev.limlib.api.world.maze;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;

public abstract class MazeComponent {
   public final int width;
   public final int height;
   public final MazeComponent.CellState[] maze;
   public boolean generated = false;

   public MazeComponent(int width, int height) {
      this.width = width;
      this.height = height;
      this.maze = new MazeComponent.CellState[width * height];

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            MazeComponent.CellState state = new MazeComponent.CellState();
            state.setPosition(new MazeComponent.Vec2i(x, y));
            this.maze[y * this.width + x] = state;
         }
      }
   }

   public void generateMaze() {
      this.generateMaze(false);
   }

   public void generateMaze(boolean doesThrow) {
      if (this.generated) {
         if (doesThrow) {
            throw new UnsupportedOperationException("This maze has already been created");
         }
      } else {
         this.create();
         this.generated = true;
      }
   }

   public abstract void create();

   public MazeComponent.CellState cellState(int x, int y) {
      return this.maze[y * this.width + x];
   }

   public MazeComponent.CellState cellState(MazeComponent.Vec2i pos) {
      return this.cellState(pos.getX(), pos.getY());
   }

   public boolean fits(MazeComponent.Vec2i vec) {
      return vec.getX() >= 0 && vec.getX() < this.width && vec.getY() >= 0 && vec.getY() < this.height;
   }

   @Override
   public String toString() {
      StringBuilder row = new StringBuilder();
      row.append("\n");

      for (int x = 1; x <= this.width; x++) {
         for (int y = 0; y < this.height; y++) {
            row.append(this.cellState(this.width - x, y).toString());
         }

         row.append("\n");
      }

      return row.toString();
   }

   public static class CellState {
      private MazeComponent.Vec2i position = new MazeComponent.Vec2i(0, 0);
      private boolean up = false;
      private boolean right = false;
      private boolean down = false;
      private boolean left = false;
      private Map<String, CompoundTag> extra = Maps.newHashMap();

      public MazeComponent.CellState copy() {
         MazeComponent.CellState newState = new MazeComponent.CellState();
         newState.setPosition(this.position);
         newState.up(this.up);
         newState.right(this.right);
         newState.down(this.down);
         newState.left(this.left);
         newState.appendAll(this.extra);
         return newState;
      }

      public void up() {
         this.up = true;
      }

      public void right() {
         this.right = true;
      }

      public void down() {
         this.down = true;
      }

      public void left() {
         this.left = true;
      }

      public void go(MazeComponent.Face face) {
         switch (face) {
            case UP:
               this.up();
               break;
            case DOWN:
               this.down();
               break;
            case LEFT:
               this.left();
               break;
            case RIGHT:
               this.right();
         }
      }

      public void setPosition(MazeComponent.Vec2i position) {
         this.position = position;
      }

      public void up(boolean up) {
         this.up = up;
      }

      public void right(boolean right) {
         this.right = right;
      }

      public void down(boolean down) {
         this.down = down;
      }

      public void left(boolean left) {
         this.left = left;
      }

      public void append(String name, CompoundTag data) {
         this.extra.put(name, data);
      }

      public void appendAll(Map<String, CompoundTag> data) {
         this.extra.putAll(data);
      }

      public MazeComponent.Vec2i getPosition() {
         return this.position;
      }

      public boolean goesUp() {
         return this.up;
      }

      public boolean goesRight() {
         return this.right;
      }

      public boolean goesDown() {
         return this.down;
      }

      public boolean goesLeft() {
         return this.left;
      }

      public boolean goes() {
         return this.up || this.down || this.left || this.right;
      }

      public boolean goes(MazeComponent.Face face) {
         return switch (face) {
            case UP -> this.up;
            case DOWN -> this.down;
            case LEFT -> this.left;
            case RIGHT -> this.right;
         };
      }

      public Map<String, CompoundTag> getExtra() {
         return this.extra;
      }

      @Override
      public String toString() {
         if (this.goesLeft() && this.goesUp() && this.goesRight() && this.goesDown()) {
            return "┼";
         } else if (this.goesLeft() && this.goesUp() && this.goesRight() && !this.goesDown()) {
            return "┴";
         } else if (this.goesLeft() && this.goesUp() && !this.goesRight() && this.goesDown()) {
            return "┤";
         } else if (this.goesLeft() && this.goesUp() && !this.goesRight() && !this.goesDown()) {
            return "┘";
         } else if (this.goesLeft() && !this.goesUp() && this.goesRight() && this.goesDown()) {
            return "┬";
         } else if (this.goesLeft() && !this.goesUp() && this.goesRight() && !this.goesDown()) {
            return "─";
         } else if (this.goesLeft() && !this.goesUp() && !this.goesRight() && this.goesDown()) {
            return "┐";
         } else if (this.goesLeft() && !this.goesUp() && !this.goesRight() && !this.goesDown()) {
            return "╴";
         } else if (!this.goesLeft() && this.goesUp() && this.goesRight() && this.goesDown()) {
            return "├";
         } else if (!this.goesLeft() && this.goesUp() && this.goesRight() && !this.goesDown()) {
            return "└";
         } else if (!this.goesLeft() && this.goesUp() && !this.goesRight() && this.goesDown()) {
            return "│";
         } else if (!this.goesLeft() && this.goesUp() && !this.goesRight() && !this.goesDown()) {
            return "╵";
         } else if (!this.goesLeft() && !this.goesUp() && this.goesRight() && this.goesDown()) {
            return "┌";
         } else if (!this.goesLeft() && !this.goesUp() && this.goesRight() && !this.goesDown()) {
            return "╶";
         } else {
            return !this.goesLeft() && !this.goesUp() && !this.goesRight() && this.goesDown() ? "╷" : "░";
         }
      }
   }

   public static enum Face {
      UP,
      DOWN,
      LEFT,
      RIGHT;

      public MazeComponent.Face mirror() {
         return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
         };
      }

      public MazeComponent.Face clockwise() {
         return switch (this) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
         };
      }

      public MazeComponent.Face anticlockwise() {
         return this.clockwise().clockwise().clockwise();
      }
   }

   public static class Vec2i {
      private int x;
      private int y;

      public Vec2i(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public Vec2i(Vec3i pos) {
         this.x = pos.getX();
         this.y = pos.getZ();
      }

      public BlockPos toBlock() {
         return new BlockPos(this.x, 0, this.y);
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public MazeComponent.Vec2i add(int x, int y) {
         return new MazeComponent.Vec2i(this.x + x, this.y + y);
      }

      public MazeComponent.Vec2i up() {
         return this.up(1);
      }

      public MazeComponent.Vec2i down() {
         return this.down(1);
      }

      public MazeComponent.Vec2i left() {
         return this.left(1);
      }

      public MazeComponent.Vec2i right() {
         return this.right(1);
      }

      public MazeComponent.Vec2i up(int d) {
         return this.add(d, 0);
      }

      public MazeComponent.Vec2i down(int d) {
         return this.add(-d, 0);
      }

      public MazeComponent.Vec2i left(int d) {
         return this.add(0, -d);
      }

      public MazeComponent.Vec2i right(int d) {
         return this.add(0, d);
      }

      public MazeComponent.Vec2i go(MazeComponent.Face face) {
         return this.go(face, 1);
      }

      public MazeComponent.Vec2i go(MazeComponent.Face face, int d) {
         return switch (face) {
            case UP -> this.up();
            case DOWN -> this.down(d);
            case LEFT -> this.left(d);
            case RIGHT -> this.right(d);
         };
      }

      public MazeComponent.Face normal(MazeComponent.Vec2i b) {
         if (b.equals(this.up())) {
            return MazeComponent.Face.UP;
         } else if (b.equals(this.left())) {
            return MazeComponent.Face.LEFT;
         } else if (b.equals(this.right())) {
            return MazeComponent.Face.RIGHT;
         } else if (b.equals(this.down())) {
            return MazeComponent.Face.DOWN;
         } else {
            throw new IllegalArgumentException("Cannot find the normal between two non-adjacent vectors");
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.x, this.y);
      }

      @Override
      public boolean equals(Object obj) {
         return !(obj instanceof MazeComponent.Vec2i pos) ? super.equals(obj) : pos.x == this.x && pos.y == this.y;
      }

      @Override
      public String toString() {
         return "(" + this.x + ", " + this.y + ")";
      }
   }
}
