package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GameRenderer.class})
public interface GameRendererAccessor {
   @Accessor
   PostChain getBlurEffect();

   @Accessor
   boolean getRenderHand();

   @Accessor
   boolean getPanoramicMode();

   @Invoker
   void invokeBobView(PoseStack var1, float var2);

   @Invoker
   void invokeBobHurt(PoseStack var1, float var2);

   @Invoker
   double invokeGetFov(Camera var1, float var2, boolean var3);

   @Invoker("shouldRenderBlockOutline")
   boolean shouldRenderBlockOutlineA();
}
