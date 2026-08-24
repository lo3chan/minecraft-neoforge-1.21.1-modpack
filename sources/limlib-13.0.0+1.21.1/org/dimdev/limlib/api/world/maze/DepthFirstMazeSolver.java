package org.dimdev.limlib.api.world.maze;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Stack;
import net.minecraft.util.RandomSource;

public class DepthFirstMazeSolver extends DepthLikeMaze {
   private final MazeComponent mazeToSolve;
   private final MazeComponent.Vec2i end;
   private final List<MazeComponent.Vec2i> beginnings;
   public final RandomSource random;

   public DepthFirstMazeSolver(MazeComponent mazeToSolve, RandomSource random, MazeComponent.Vec2i end, MazeComponent.Vec2i... beginnings) {
      super(mazeToSolve.width, mazeToSolve.height);
      this.mazeToSolve = mazeToSolve;
      this.end = end;
      this.beginnings = Lists.newArrayList(beginnings);
      this.random = random;
   }

   @Override
   public void create() {
      List<Stack<MazeComponent.Vec2i>> paths = Lists.newArrayList();
      this.beginnings.forEach(beginning -> {
         Stack<MazeComponent.Vec2i> stack = new Stack<>();
         stack.push(new MazeComponent.Vec2i(beginning.getX(), beginning.getY()));
         MazeComponent.Vec2i peek = stack.peek();
         this.visit(peek);

         for (; !peek.equals(this.end); peek = stack.peek()) {
            List<MazeComponent.Face> neighbours = Lists.newArrayList();

            for (MazeComponent.Face face : MazeComponent.Face.values()) {
               if (this.hasNeighbour(peek, face)) {
                  neighbours.add(face);
               }
            }

            if (!neighbours.isEmpty()) {
               MazeComponent.Face nextFace = neighbours.get(this.random.nextInt(neighbours.size()));
               this.visit(peek.go(nextFace));
               stack.push(peek.go(nextFace));
            } else {
               stack.pop();
            }
         }

         for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
               this.visit(new MazeComponent.Vec2i(x, y), false);
            }
         }

         paths.add(stack);
      });
      paths.forEach(path -> {
         for (int i = 0; i < path.size(); i++) {
            MazeComponent.Vec2i pos = path.get(i);
            if (i + 1 != path.size()) {
               MazeComponent.Vec2i nextPos = path.get(i + 1);
               MazeComponent.Face face = pos.normal(nextPos);
               this.cellState(pos).go(face);
               this.cellState(pos.go(face)).go(face.mirror());
               this.cellState(pos).appendAll(this.mazeToSolve.cellState(pos).getExtra());
            }

            if (this.beginnings.contains(pos) || pos.equals(this.end)) {
               if (pos.getX() == 0) {
                  this.cellState(pos).down();
               }

               if (pos.getY() == 0) {
                  this.cellState(pos).left();
               }

               if (pos.getX() == this.width - 1) {
                  this.cellState(pos).up();
               }

               if (pos.getY() == this.height - 1) {
                  this.cellState(pos).right();
               }
            }
         }
      });
   }

   public MazeComponent getMazeToSolve() {
      return this.mazeToSolve;
   }

   @Override
   public boolean hasNeighbourUp(MazeComponent.Vec2i vec) {
      return super.hasNeighbourUp(vec) && this.mazeToSolve.cellState(vec).goesUp();
   }

   @Override
   public boolean hasNeighbourRight(MazeComponent.Vec2i vec) {
      return super.hasNeighbourRight(vec) && this.mazeToSolve.cellState(vec).goesRight();
   }

   @Override
   public boolean hasNeighbourDown(MazeComponent.Vec2i vec) {
      return super.hasNeighbourDown(vec) && this.mazeToSolve.cellState(vec).goesDown();
   }

   @Override
   public boolean hasNeighbourLeft(MazeComponent.Vec2i vec) {
      return super.hasNeighbourLeft(vec) && this.mazeToSolve.cellState(vec).goesLeft();
   }
}
