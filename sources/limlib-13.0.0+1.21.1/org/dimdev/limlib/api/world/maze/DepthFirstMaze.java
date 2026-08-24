package org.dimdev.limlib.api.world.maze;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.RandomSource;

public class DepthFirstMaze extends DepthLikeMaze {
   public RandomSource random;

   public DepthFirstMaze(int width, int height, RandomSource RandomGenerator) {
      super(width, height);
      this.random = RandomGenerator;
   }

   @Override
   public void create() {
      this.visit(new MazeComponent.Vec2i(0, 0));
      this.visitedCells++;
      this.stack.push(new MazeComponent.Vec2i(0, 0));

      while (this.visitedCells < this.width * this.height) {
         List<MazeComponent.Face> neighbours = Lists.newArrayList();

         for (MazeComponent.Face face : MazeComponent.Face.values()) {
            if (this.hasNeighbour(this.stack.peek(), face)) {
               neighbours.add(face);
            }
         }

         if (!neighbours.isEmpty()) {
            MazeComponent.Face nextFace = neighbours.get(this.random.nextInt(neighbours.size()));
            this.cellState(this.stack.peek()).go(nextFace);
            this.cellState(this.stack.peek().go(nextFace)).go(nextFace.mirror());
            this.visit(this.stack.peek().go(nextFace));
            this.stack.push(this.stack.peek().go(nextFace));
            this.visitedCells++;
         } else {
            this.stack.pop();
         }
      }
   }
}
