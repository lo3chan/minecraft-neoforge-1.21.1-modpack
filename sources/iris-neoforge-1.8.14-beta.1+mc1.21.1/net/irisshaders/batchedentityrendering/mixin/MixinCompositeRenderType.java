package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CompositeRenderType.class})
public abstract class MixinCompositeRenderType extends RenderType implements BlendingStateHolder {
   @Unique
   private static final String INIT = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;IZZLnet/minecraft/client/renderer/RenderType$CompositeState;)V";
   @Unique
   private TransparencyType transparencyType;

   private MixinCompositeRenderType(
      String name,
      VertexFormat vertexFormat,
      Mode drawMode,
      int expectedBufferSize,
      boolean hasCrumbling,
      boolean translucent,
      Runnable startAction,
      Runnable endAction
   ) {
      super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
   }

   @Inject(
      method = {"<init>(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;IZZLnet/minecraft/client/renderer/RenderType$CompositeState;)V"},
      at = {@At("RETURN")}
   )
   private void batchedentityrendering$onCompositeInit(
      String string, VertexFormat vertexFormat, Mode mode, int i, boolean bl, boolean bl2, CompositeState compositeState, CallbackInfo ci
   ) {
      TransparencyStateShard transparency = ((CompositeStateAccessor)compositeState).getTransparency();
      DepthTestStateShard depth = ((CompositeStateAccessor)compositeState).getDepth();
      if ("water_mask".equals(this.name) || depth == RenderStateShard.NO_DEPTH_TEST) {
         this.transparencyType = TransparencyType.WATER_MASK;
      } else if ("lines".equals(this.name)) {
         this.transparencyType = TransparencyType.LINES;
      } else if (transparency == RenderStateShardAccessor.getNO_TRANSPARENCY()) {
         this.transparencyType = TransparencyType.OPAQUE;
      } else if (transparency != RenderStateShardAccessor.getGLINT_TRANSPARENCY() && transparency != RenderStateShardAccessor.getCRUMBLING_TRANSPARENCY()) {
         this.transparencyType = TransparencyType.GENERAL_TRANSPARENT;
      } else {
         this.transparencyType = TransparencyType.DECAL;
      }
   }

   @Override
   public TransparencyType getTransparencyType() {
      return this.transparencyType;
   }

   @Override
   public void setTransparencyType(TransparencyType type) {
      this.transparencyType = type;
   }
}
