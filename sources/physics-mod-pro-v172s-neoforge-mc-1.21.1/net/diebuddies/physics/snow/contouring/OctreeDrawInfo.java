package net.diebuddies.physics.snow.contouring;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class OctreeDrawInfo {
   public Vector3d pos;
   public Vector3f normal;
   public int light;
   public int corners;
   public int index;

   @Override
   public String toString() {
      return "OctreeDrawInfo [pos=" + this.pos + ", normal=" + this.normal + ", corners=" + this.corners + ", index=" + this.index + "]";
   }
}
