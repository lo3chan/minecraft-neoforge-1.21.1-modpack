package net.diebuddies.dualcontouring;

public class Voxel {
   public byte density;
   public int ambient;

   public Voxel(byte density, int ambient) {
      this.density = density;
      this.ambient = ambient;
   }

   public Voxel() {
      this.density = 0;
      this.ambient = 0;
   }

   public void set(Voxel voxel) {
      this.density = voxel.density;
      this.ambient = voxel.ambient;
   }

   public void set(byte density, int ambient) {
      this.density = density;
      this.ambient = ambient;
   }

   public Voxel copy() {
      return new Voxel(this.density, this.ambient);
   }
}
