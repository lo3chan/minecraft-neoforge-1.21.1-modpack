package net.diebuddies.opengl;

import org.lwjgl.opengl.GL32C;

public class StateTracker {
   public static void bindVertexArray(int id) {
      GL32C.glBindVertexArray(id);
   }

   public static void unbindVertexArray() {
      bindVertexArray(0);
   }
}
