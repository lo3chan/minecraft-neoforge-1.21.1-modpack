package jeresources.api.render;

public interface IScissorHook {
   IScissorHook.ScissorInfo transformScissor(IScissorHook.ScissorInfo var1);

   public static class ScissorInfo {
      public int x;
      public int y;
      public int width;
      public int height;

      public ScissorInfo(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
}
