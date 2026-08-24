package net.diebuddies.dualcontouring;

import org.joml.Vector3i;

public class SimplePoolVector3i {
   private Vector3i[] objects;
   private int index;

   public SimplePoolVector3i(int size) {
      this.objects = new Vector3i[size];

      for (int i = 0; i < size; i++) {
         this.objects[i] = new Vector3i();
      }
   }

   public Vector3i get(int x, int y, int z) {
      if (this.index < this.objects.length) {
         return this.objects[this.index++].set(x, y, z);
      } else {
         this.resize();
         return this.objects[this.index++].set(x, y, z);
      }
   }

   private void resize() {
      Vector3i[] newArray = new Vector3i[this.objects.length * 2];
      System.arraycopy(this.objects, 0, newArray, 0, this.objects.length);

      for (int i = this.objects.length; i < newArray.length; i++) {
         newArray[i] = new Vector3i();
      }

      this.objects = newArray;
   }

   public void reset() {
      this.index = 0;
   }
}
