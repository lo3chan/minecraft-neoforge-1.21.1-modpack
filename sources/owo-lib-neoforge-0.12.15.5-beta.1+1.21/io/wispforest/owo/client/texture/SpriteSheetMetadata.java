package io.wispforest.owo.client.texture;

public record SpriteSheetMetadata(int width, int height, int frameWidth, int frameHeight, int offset) {
   public SpriteSheetMetadata(int width, int height, int frameWidth, int frameHeight) {
      this(width, height, frameWidth, frameHeight, 0);
   }

   public SpriteSheetMetadata(int size, int frameSize) {
      this(size, size, frameSize, frameSize, 0);
   }
}
