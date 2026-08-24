package fuzs.puzzleslib.api.client.renderer.v1.chunk;

import net.minecraft.client.renderer.RenderType;

public enum ChunkSectionLayer {
   SOLID(RenderType.solid()),
   CUTOUT(RenderType.cutout()),
   CUTOUT_MIPPED(RenderType.cutoutMipped()),
   TRANSLUCENT(RenderType.translucent()),
   TRIPWIRE(RenderType.tripwire());

   public final RenderType renderType;

   private ChunkSectionLayer(RenderType renderType) {
      this.renderType = renderType;
   }
}
