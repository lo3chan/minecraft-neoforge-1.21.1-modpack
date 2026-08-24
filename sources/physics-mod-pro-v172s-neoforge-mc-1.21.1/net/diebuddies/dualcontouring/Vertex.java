package net.diebuddies.dualcontouring;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
   public Vector3f position = new Vector3f();
   public Vector3f normal = new Vector3f();
   public Vector3f tangent = new Vector3f();
   public Vector2f uv = new Vector2f();
   public int ambient;

   public Vertex set(Vector3f position, int ambient) {
      this.position.set(position);
      this.ambient = ambient;
      this.uv.set(0.0F);
      this.tangent.set(0.0F);
      this.normal.set(0.0F);
      return this;
   }

   @Override
   public String toString() {
      return "Vertex [position=" + this.position + ", normal=" + this.normal + "]";
   }
}
