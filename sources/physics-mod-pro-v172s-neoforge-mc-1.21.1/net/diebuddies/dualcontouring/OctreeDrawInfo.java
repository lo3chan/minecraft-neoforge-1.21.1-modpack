package net.diebuddies.dualcontouring;

import org.joml.Vector3f;

public class OctreeDrawInfo {
   public Vector3f pos = new Vector3f();
   public int ambient;
   public byte corners;
   public int index;

   @Override
   public String toString() {
      return "OctreeDrawInfo [pos=" + this.pos + ", corners=" + this.corners + ", index=" + this.index + "]";
   }
}
