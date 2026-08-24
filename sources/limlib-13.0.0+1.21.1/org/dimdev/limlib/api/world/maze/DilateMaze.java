package org.dimdev.limlib.api.world.maze;

public class DilateMaze extends MazeComponent {
   private MazeComponent mazeIn;
   private int dilationX;
   private int dilationY;

   public DilateMaze(MazeComponent mazeIn, int dilation) {
      this(mazeIn, dilation, dilation);
   }

   public DilateMaze(MazeComponent mazeIn, int dilationX, int dilationY) {
      super(mazeIn.width * dilationX, mazeIn.height * dilationY);
      this.mazeIn = mazeIn;
      this.dilationX = dilationX;
      this.dilationY = dilationY;
   }

   @Override
   public void create() {
      for (int x = 0; x < this.mazeIn.width; x++) {
         for (int y = 0; y < this.mazeIn.height; y++) {
            for (int dx = 0; dx < this.dilationX; dx++) {
               for (int dy = 0; dy < this.dilationY; dy++) {
                  int mazeX = x * this.dilationX + dx;
                  int mazeY = y * this.dilationY + dy;
                  MazeComponent.Vec2i position = new MazeComponent.Vec2i(mazeX, mazeY);
                  MazeComponent.CellState reference = this.mazeIn.cellState(x, y);
                  if (dx % this.dilationX == 0) {
                     if (dy % this.dilationY == 0) {
                        MazeComponent.CellState copy = reference.copy();
                        copy.setPosition(position);
                        this.maze[mazeY * this.width + mazeX] = copy;
                     } else if (this.mazeIn.cellState(x, y).goesRight()) {
                        MazeComponent.CellState copy = new MazeComponent.CellState();
                        copy.right();
                        copy.left();
                        copy.setPosition(position);
                        copy.appendAll(reference.getExtra());
                        this.maze[mazeY * this.width + mazeX] = copy;
                     }
                  } else if (dy % this.dilationY == 0) {
                     if (this.mazeIn.cellState(x, y).goesUp()) {
                        MazeComponent.CellState copy = new MazeComponent.CellState();
                        copy.up();
                        copy.down();
                        copy.setPosition(position);
                        copy.appendAll(reference.getExtra());
                        this.maze[mazeY * this.width + mazeX] = copy;
                     }
                  } else {
                     MazeComponent.CellState copy = new MazeComponent.CellState();
                     copy.setPosition(position);
                     copy.appendAll(reference.getExtra());
                     this.maze[mazeY * this.width + mazeX] = copy;
                  }
               }
            }
         }
      }
   }

   public MazeComponent getMazeIn() {
      return this.mazeIn;
   }

   public int getDilationX() {
      return this.dilationX;
   }

   public int getDilationY() {
      return this.dilationY;
   }
}
