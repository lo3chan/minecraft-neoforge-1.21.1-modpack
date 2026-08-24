package net.irisshaders.iris.uniforms.custom.cached;

import java.util.function.Supplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.parsing.MatrixType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL21;

public class Float4MatrixCachedUniform extends VectorCachedUniform<Matrix4fc> {
   private final float[] buffer = new float[16];

   public Float4MatrixCachedUniform(String name, UniformUpdateFrequency updateFrequency, Supplier<Matrix4fc> supplier) {
      super(name, updateFrequency, new Matrix4f(), supplier);
   }

   protected void setFrom(Matrix4fc other) {
      ((Matrix4f)this.cached).set(other);
   }

   @Override
   public void push(int location) {
      this.cached.get(this.buffer);
      GL21.glUniformMatrix4fv(location, false, this.buffer);
   }

   public MatrixType<Matrix4f> getType() {
      return MatrixType.MAT4;
   }
}
