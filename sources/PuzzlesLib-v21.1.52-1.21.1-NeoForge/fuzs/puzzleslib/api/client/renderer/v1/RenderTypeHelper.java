package fuzs.puzzleslib.api.client.renderer.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class RenderTypeHelper {
   private RenderTypeHelper() {
   }

   public static RenderType getRenderType(Block block) {
      return ClientProxyImpl.get().getRenderType(block);
   }

   public static RenderType getRenderType(Fluid fluid) {
      return ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
   }

   public static void registerRenderType(Block block, RenderType renderType) {
      ClientProxyImpl.get().registerRenderType(block, renderType);
   }

   public static void registerRenderType(Fluid fluid, RenderType renderType) {
      ClientProxyImpl.get().registerRenderType(fluid, renderType);
   }
}
