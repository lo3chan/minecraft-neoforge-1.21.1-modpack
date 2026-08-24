package net.mehvahdjukaar.moonlight.api.resources.textures;

public final class PixelContext {
   private final TextureImage image;
   int frameIndex;
   int localX;
   int localY;
   int globalX;
   int globalY;

   public PixelContext(TextureImage image) {
      this.image = image;
   }

   public int getValue() {
      return this.image.getPixel(this.globalX, this.globalY);
   }

   public void setValue(int value) {
      this.image.setPixel(this.globalX, this.globalY, value);
   }

   public void blendValue(int value) {
      this.image.blendPixel(this.globalX, this.globalY, value);
   }

   public int frameIndex() {
      return this.frameIndex;
   }

   public int frameX() {
      return this.localX;
   }

   public int frameY() {
      return this.localY;
   }

   public int x() {
      return this.globalX;
   }

   public int y() {
      return this.globalY;
   }
}
