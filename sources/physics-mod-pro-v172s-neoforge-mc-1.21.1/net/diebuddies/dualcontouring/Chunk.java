package net.diebuddies.dualcontouring;

public class Chunk {
   public Voxel[] voxels;
   public int width;
   public int height;
   public int depth;
   public int color;
   private int widthHeight;

   public Chunk(int width, int height, int depth, byte density, int ambient, int color) {
      this.reset(width, height, depth, density, ambient, color);
   }

   public Voxel get(int x, int y, int z) {
      return this.voxels[z * this.widthHeight + y * this.width + x];
   }

   public Chunk reset(int width, int height, int depth, byte density, int ambient, int color) {
      int size = width * height * depth;
      this.color = color;
      if (this.voxels != null && this.voxels.length >= size) {
         for (int i = 0; i < size; i++) {
            this.voxels[i].set(density, ambient);
         }
      } else {
         int oldLength = this.voxels == null ? 0 : this.voxels.length;
         Voxel[] oldVoxels = this.voxels;
         this.voxels = new Voxel[size * 2];
         if (oldLength > 0) {
            System.arraycopy(oldVoxels, 0, this.voxels, 0, oldLength);
         }

         for (int i = oldLength; i < this.voxels.length; i++) {
            this.voxels[i] = new Voxel(density, ambient);
         }
      }

      this.width = width;
      this.height = height;
      this.depth = depth;
      this.widthHeight = width * height;
      return this;
   }
}
