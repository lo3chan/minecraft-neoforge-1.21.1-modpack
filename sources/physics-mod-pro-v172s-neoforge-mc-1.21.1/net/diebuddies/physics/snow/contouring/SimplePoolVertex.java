package net.diebuddies.physics.snow.contouring;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class SimplePoolVertex {
   private Vertex[] objects;
   private int index;

   public SimplePoolVertex(int size) {
      this.objects = new Vertex[size];

      for (int i = 0; i < size; i++) {
         this.objects[i] = new Vertex();
      }
   }

   public Vertex get(Vector3d position, Vector3f normal, int light) {
      if (this.index < this.objects.length) {
         return this.objects[this.index++].set(position, normal, light);
      } else {
         this.resize();
         return this.objects[this.index++].set(position, normal, light);
      }
   }

   private void resize() {
      Vertex[] newArray = new Vertex[this.objects.length * 2];
      System.arraycopy(this.objects, 0, newArray, 0, this.objects.length);

      for (int i = this.objects.length; i < newArray.length; i++) {
         newArray[i] = new Vertex();
      }

      this.objects = newArray;
   }

   public void reset() {
      this.index = 0;
   }
}
