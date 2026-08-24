package net.irisshaders.iris.mixin;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.SkipList;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.blending.DepthColorStorage;
import net.irisshaders.iris.mixinterface.ShaderInstanceInterface;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.pipeline.programs.FallbackShader;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ShaderInstance.class})
public abstract class MixinShaderInstance implements ShaderInstanceInterface {
   @Unique
   private static final ImmutableSet<String> ATTRIBUTE_LIST = ImmutableSet.of("Position", "Color", "Normal", "UV0", "UV1", "UV2", new String[0]);
   @Shadow
   private static ShaderInstance lastAppliedShader;
   @Shadow
   @Final
   private int programId;
   @Shadow
   @Final
   private Program vertexProgram;
   @Shadow
   @Final
   private Program fragmentProgram;
   @Unique
   private MethodHandle shouldSkip;

   @Override
   public void setShouldSkip(MethodHandle s) {
      this.shouldSkip = s;
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/renderer/ShaderInstance;CHUNK_OFFSET:Lcom/mojang/blaze3d/shaders/Uniform;"
      )},
      require = 0
   )
   private void iris$storeSkipFabric(ResourceProvider resourceProvider, String string, VertexFormat vertexFormat, CallbackInfo ci) {
      this.shouldSkip = SkipList.shouldSkipList.computeIfAbsent(this.getClass(), x -> {
         try {
            MethodHandle iris$skipDraw = MethodHandles.lookup().findVirtual((Class<?>)x, "iris$skipDraw", MethodType.methodType(boolean.class));
            Iris.logger.warn("Class " + x.getName() + " has opted out of being rendered with shaders.");
            return iris$skipDraw;
         } catch (IllegalAccessException | NoSuchMethodException var2x) {
            return SkipList.NONE;
         }
      });
      if (Iris.getIrisConfig().shouldSkip(ResourceLocation.tryParse(string))) {
         this.shouldSkip = SkipList.ALWAYS;
      }
   }

   public boolean iris$shouldSkipThis() {
      if (Iris.getIrisConfig().shouldAllowUnknownShaders()) {
         if (ShadowRenderer.ACTIVE) {
            return true;
         } else if (!shouldOverrideShaders()) {
            return false;
         } else if (this.shouldSkip == SkipList.NONE) {
            return false;
         } else if (this.shouldSkip == SkipList.ALWAYS) {
            return true;
         } else {
            try {
               return (boolean)this.shouldSkip.invoke((ShaderInstance)((ShaderInstance)this));
            } catch (Throwable var2) {
               throw new RuntimeException(var2);
            }
         }
      } else {
         return !(this instanceof ExtendedShader) && !(this instanceof FallbackShader) && shouldOverrideShaders();
      }
   }

   @Unique
   private static boolean shouldOverrideShaders() {
      WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
      return pipeline instanceof ShaderRenderingPipeline ? ((ShaderRenderingPipeline)pipeline).shouldOverrideShaders() : false;
   }

   @Shadow
   public abstract int getId();

   @Redirect(
      method = {"updateLocations"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
         remap = false
      )
   )
   private void iris$redirectLogSpam(Logger logger, String message, Object arg1, Object arg2) {
      if (!(this instanceof ExtendedShader) && !(this instanceof FallbackShader)) {
         logger.warn(message, arg1, arg2);
      }
   }

   @Redirect(
      method = {"<init>*"},
      require = 1,
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/shaders/Uniform;glBindAttribLocation(IILjava/lang/CharSequence;)V"
      )
   )
   public void iris$redirectBindAttributeLocation(int i, int j, CharSequence charSequence) {
      if (this instanceof ExtendedShader && ATTRIBUTE_LIST.contains(charSequence)) {
         Uniform.glBindAttribLocation(i, j, "iris_" + charSequence);
      } else {
         Uniform.glBindAttribLocation(i, j, charSequence);
      }
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void name(ResourceProvider resourceProvider, String string, VertexFormat vertexFormat, CallbackInfo ci) {
      GLDebug.nameObject(33506, this.programId, string);
      GLDebug.nameObject(33505, this.vertexProgram.getId(), string);
      GLDebug.nameObject(33505, this.fragmentProgram.getId(), string);
   }

   @Inject(
      method = {"apply"},
      at = {@At("HEAD")}
   )
   private void iris$lockDepthColorState(CallbackInfo ci) {
      if (lastAppliedShader != null) {
         lastAppliedShader.clear();
         lastAppliedShader = null;
      }
   }

   @Inject(
      method = {"apply"},
      at = {@At("TAIL")}
   )
   private void onTail(CallbackInfo ci) {
      if (!this.iris$shouldSkipThis()) {
         if (!this.isKnownShader() && shouldOverrideShaders()) {
            WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
            if (pipeline instanceof IrisRenderingPipeline && !ShadowRenderer.ACTIVE) {
               ((IrisRenderingPipeline)pipeline).bindDefault();
            }
         }
      } else {
         DepthColorStorage.disableDepthColor();
      }
   }

   private boolean isKnownShader() {
      return this instanceof ExtendedShader || this instanceof FallbackShader;
   }

   @Inject(
      method = {"clear"},
      at = {@At("HEAD")}
   )
   private void iris$unlockDepthColorState(CallbackInfo ci) {
      if (!this.iris$shouldSkipThis()) {
         if (!this.isKnownShader() && shouldOverrideShaders()) {
            WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
            if (pipeline instanceof IrisRenderingPipeline) {
               Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }
         }
      } else {
         DepthColorStorage.unlockDepthColor();
      }
   }

   @Inject(
      method = {"<init>"},
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/GsonHelper;parse(Ljava/io/Reader;)Lcom/google/gson/JsonObject;"
      )}
   )
   public void iris$setupGeometryShader(ResourceProvider resourceProvider, String string, VertexFormat vertexFormat, CallbackInfo ci) {
      this.iris$createExtraShaders(resourceProvider, string);
   }

   @Override
   public void iris$createExtraShaders(ResourceProvider provider, String name) {
   }

   static {
      SkipList.shouldSkipList.put(ExtendedShader.class, SkipList.NONE);
      SkipList.shouldSkipList.put(FallbackShader.class, SkipList.NONE);
   }
}
