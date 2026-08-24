package net.diebuddies.opengl;

public class DrawBuffer {
   protected int id;
   private Texture texture;

   public DrawBuffer(int id, Texture texture) {
      this.id = id;
      this.texture = texture;
   }

   public int getID() {
      return this.id;
   }

   public Texture getTexture() {
      return this.texture;
   }

   public void setID(int id) {
      this.id = id;
   }

   public void setTexture(Texture texture) {
      this.texture = texture;
   }

   public void destroy() {
      this.texture.destroy();
   }
}
