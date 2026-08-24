package net.diebuddies.dualcontouring;

public class SimplePoolOctreeNode {
   private OctreeNode[] objects;
   private int index;

   public SimplePoolOctreeNode(int size) {
      this.objects = new OctreeNode[size];

      for (int i = 0; i < size; i++) {
         this.objects[i] = new OctreeNode();
      }
   }

   public OctreeNode get() {
      if (this.index < this.objects.length) {
         return this.objects[this.index++].reset();
      } else {
         this.resize();
         return this.objects[this.index++].reset();
      }
   }

   private void resize() {
      OctreeNode[] newArray = new OctreeNode[this.objects.length * 2];
      System.arraycopy(this.objects, 0, newArray, 0, this.objects.length);

      for (int i = this.objects.length; i < newArray.length; i++) {
         newArray[i] = new OctreeNode();
      }

      this.objects = newArray;
   }

   public void reset() {
      this.index = 0;
   }
}
