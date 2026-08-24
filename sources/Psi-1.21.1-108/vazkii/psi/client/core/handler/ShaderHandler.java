package vazkii.psi.client.core.handler;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import vazkii.psi.common.Psi;

@EventBusSubscriber(
   modid = "psi",
   value = {Dist.CLIENT}
)
public final class ShaderHandler {
   private static ShaderInstance psiBarShader;

   @SubscribeEvent
   static void registerShaders(RegisterShadersEvent event) throws IOException {
      event.registerShader(
         new ShaderInstance(event.getResourceProvider(), Psi.location("psi_bar"), DefaultVertexFormat.POSITION_TEX_COLOR), shader -> psiBarShader = shader
      );
   }

   public static ShaderInstance getPsiBarShader() {
      return psiBarShader;
   }
}
