package com.github.alexthe666.alexsmobs.client.render;

public final class AMBookPose {
   public static final int PICKAXE = 1;
   public static final int NO_SHAKE = 2;
   public static final int FAKE_HEAD = 4;

   private AMBookPose() {
   }

   public static int capture() {
      return (RenderUnderminer.renderWithPickaxe ? 1 : 0) | (RenderLaviathan.renderWithoutShaking ? 2 : 0) | (RenderMurmurBody.renderWithHead ? 4 : 0);
   }

   public static int swap(int flags) {
      int previous = capture();
      RenderUnderminer.renderWithPickaxe = (flags & 1) != 0;
      RenderLaviathan.renderWithoutShaking = (flags & 2) != 0;
      RenderMurmurBody.renderWithHead = (flags & 4) != 0;
      return previous;
   }
}
