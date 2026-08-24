package org.dimdev.limlib.api.world.maze;

public class CombineMaze extends MazeComponent {
   MazeComponent[] components;

   public CombineMaze(MazeComponent... components) {
      super(components[0].width, components[0].height);
      this.components = components;
   }

   @Override
   public void create() {
      for (MazeComponent maze : this.components) {
         for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
               MazeComponent.CellState reference = maze.cellState(x, y);

               for (MazeComponent.Face face : MazeComponent.Face.values()) {
                  if (reference.goes(face)) {
                     this.cellState(x, y).go(face);
                  }
               }

               this.cellState(x, y).appendAll(reference.getExtra());
            }
         }
      }
   }
}
