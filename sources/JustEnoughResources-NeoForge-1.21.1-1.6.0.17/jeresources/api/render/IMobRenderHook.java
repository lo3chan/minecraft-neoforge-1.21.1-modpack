package jeresources.api.render;

import net.minecraft.world.entity.LivingEntity;

public interface IMobRenderHook<T extends LivingEntity> {
   IMobRenderHook.RenderInfo transform(IMobRenderHook.RenderInfo var1, T var2);

   public static class RenderInfo {
      public int x;
      public int y;
      public double scale;
      public double yaw;
      public double pitch;

      public RenderInfo(int x, int y, double scale, double yaw, double pitch) {
         this.x = x;
         this.y = y;
         this.scale = scale;
         this.yaw = yaw;
         this.pitch = pitch;
      }
   }
}
