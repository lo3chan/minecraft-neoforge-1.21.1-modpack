package net.diebuddies.physics.snow.contouring;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class Vertex {
   public Vector3d position;
   public Vector3f normal;
   public int light;

   public Vertex(Vector3d position, Vector3f normal, int light) {
      this.position = position;
      this.normal = normal;
      this.light = light;
   }

   public Vertex() {
      this(new Vector3d(), new Vector3f(), 0);
   }

   public Vertex set(Vector3d position, Vector3f normal, int light) {
      this.position.set(position);
      this.normal.set(normal);
      this.light = light;
      return this;
   }
}
