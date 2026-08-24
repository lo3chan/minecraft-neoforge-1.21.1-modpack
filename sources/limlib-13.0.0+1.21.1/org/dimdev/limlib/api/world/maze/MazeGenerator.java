package org.dimdev.limlib.api.world.maze;

import java.util.HashMap;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import org.dimdev.limlib.api.world.LimlibHelper;

public class MazeGenerator<M extends MazeComponent> {
   private final HashMap<MazeComponent.Vec2i, M> mazes = new HashMap<>(30);
   public final int width;
   public final int height;
   public final int thicknessX;
   public final int thicknessY;
   public final long seedModifier;

   public MazeGenerator(int width, int height, int thicknessX, int thicknessY, long seedModifier) {
      this.width = width;
      this.height = height;
      this.thicknessX = thicknessX;
      this.thicknessY = thicknessY;
      this.seedModifier = seedModifier;
   }

   public void generateMaze(
      MazeComponent.Vec2i pos, WorldGenRegion region, MazeGenerator.MazeCreator<M> mazeCreator, MazeGenerator.CellDecorator<M> cellDecorator
   ) {
      for (int x = 0; x < 16; x++) {
         for (int y = 0; y < 16; y++) {
            MazeComponent.Vec2i inPos = pos.add(x, y);
            if (Math.floorMod(inPos.getX(), this.thicknessX) == 0 && Math.floorMod(inPos.getY(), this.thicknessY) == 0) {
               MazeComponent.Vec2i mazePos = new MazeComponent.Vec2i(
                  inPos.getX() - Math.floorMod(inPos.getX(), this.width * this.thicknessX),
                  inPos.getY() - Math.floorMod(inPos.getY(), this.height * this.thicknessY)
               );
               M maze;
               if (this.mazes.containsKey(mazePos)) {
                  maze = this.mazes.get(mazePos);
               } else {
                  maze = mazeCreator.newMaze(
                     region,
                     mazePos,
                     this.width,
                     this.height,
                     RandomSource.create(LimlibHelper.blockSeed(mazePos.getX(), mazePos.getY(), region.getSeed() + this.seedModifier))
                  );
                  this.mazes.put(mazePos, maze);
               }

               int mazeX = (inPos.getX() - mazePos.getX()) / this.thicknessX;
               int mazeY = (inPos.getY() - mazePos.getY()) / this.thicknessY;
               MazeComponent.CellState originCell = maze.cellState(mazeX, mazeY);
               cellDecorator.generate(
                  region,
                  inPos,
                  mazePos,
                  maze,
                  originCell,
                  new MazeComponent.Vec2i(this.thicknessX, this.thicknessY),
                  RandomSource.create(LimlibHelper.blockSeed(mazePos.getX(), mazePos.getY(), region.getSeed() + this.seedModifier))
               );
            }
         }
      }
   }

   public HashMap<MazeComponent.Vec2i, M> getMazes() {
      return this.mazes;
   }

   @FunctionalInterface
   public interface CellDecorator<M extends MazeComponent> {
      void generate(
         WorldGenRegion var1,
         MazeComponent.Vec2i var2,
         MazeComponent.Vec2i var3,
         M var4,
         MazeComponent.CellState var5,
         MazeComponent.Vec2i var6,
         RandomSource var7
      );
   }

   @FunctionalInterface
   public interface MazeCreator<M extends MazeComponent> {
      M newMaze(WorldGenRegion var1, MazeComponent.Vec2i var2, int var3, int var4, RandomSource var5);
   }
}
