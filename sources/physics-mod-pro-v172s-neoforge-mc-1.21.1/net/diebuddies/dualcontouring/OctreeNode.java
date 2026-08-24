package net.diebuddies.dualcontouring;

import org.joml.Vector3i;

public class OctreeNode {
   public OctreeNode[] children = new OctreeNode[8];
   public OctreeNodeType type = OctreeNodeType.NONE;
   public OctreeDrawInfo drawInfo;
   public Vector3i min = new Vector3i();
   public int size;

   @Override
   public String toString() {
      return this.generateString(0);
   }

   public OctreeNode reset() {
      for (int i = 0; i < 8; i++) {
         this.children[i] = null;
      }

      this.size = 0;
      this.type = OctreeNodeType.NONE;
      return this;
   }

   public String generateString(int spaces) {
      String spacesString = "";

      for (int i = 0; i < spaces; i++) {
         spacesString = spacesString + " ";
      }

      int childrenCount = 0;

      for (int i = 0; i < this.children.length; i++) {
         if (this.children[i] != null) {
            childrenCount++;
         }
      }

      spacesString = spacesString
         + this.min
         + ", size: "
         + this.size
         + ", draw info: "
         + this.drawInfo
         + ", children: "
         + childrenCount
         + ", type: "
         + this.type
         + "\n";

      for (int ix = 0; ix < this.children.length; ix++) {
         if (this.children[ix] != null) {
            spacesString = spacesString + this.children[ix].generateString(spaces + 1);
         }
      }

      return spacesString;
   }

   public int getChildrenSize() {
      int childrenSize = 0;

      for (int i = 0; this.children != null && i < this.children.length; i++) {
         if (this.children[i] != null) {
            childrenSize = ++childrenSize + this.children[i].getChildrenSize();
         }
      }

      return childrenSize;
   }

   public int getChildrenSize(OctreeNodeType type) {
      int childrenSize = 0;

      for (int i = 0; this.children != null && i < this.children.length; i++) {
         if (this.children[i] != null) {
            if (this.children[i].type == type) {
               childrenSize++;
            }

            childrenSize += this.children[i].getChildrenSize(type);
         }
      }

      return childrenSize;
   }

   public OctreeNode copy() {
      OctreeNode node = new OctreeNode();
      node.type = this.type;
      node.children = this.children;
      node.type = this.type;
      node.drawInfo = this.drawInfo;
      node.min = this.min;
      node.size = this.size;
      return node;
   }
}
