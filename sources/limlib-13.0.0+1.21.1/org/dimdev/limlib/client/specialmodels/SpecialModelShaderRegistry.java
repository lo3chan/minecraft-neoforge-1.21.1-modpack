package org.dimdev.limlib.client.specialmodels;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.Nullable;

public final class SpecialModelShaderRegistry {
   private static final Map<ResourceLocation, SpecialModelShaderRegistry.SpecialModelShaderOptions> OPTIONS = new LinkedHashMap<>();
   private static final Map<ResourceLocation, ShaderInstance> SHADERS = new ConcurrentHashMap<>();

   public static void register(ResourceLocation rendererId, ResourceLocation shaderId, @Nullable ShaderCallback setupCallback) {
      register(rendererId, shaderId, DefaultVertexFormat.BLOCK, setupCallback);
   }

   public static synchronized void register(
      ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat, @Nullable ShaderCallback setupCallback
   ) {
      register(new SpecialModelShaderRegistry.SpecialModelShaderOptions(rendererId, shaderId, vertexFormat, setupCallback));
   }

   public static synchronized void register(SpecialModelShaderRegistry.SpecialModelShaderOptions options) {
      SpecialModelShaderRegistry.SpecialModelShaderOptions existing = OPTIONS.get(options.rendererId());
      if (existing != null) {
         if (!existing.sameDefinition(options)) {
            throw new IllegalArgumentException("Special model shader renderer is already registered: " + options.rendererId());
         }
      } else {
         OPTIONS.put(options.rendererId(), options);
      }
   }

   public static synchronized boolean isRegistered(ResourceLocation rendererId) {
      return OPTIONS.containsKey(rendererId);
   }

   public static synchronized VertexFormat getVertexFormat(ResourceLocation rendererId) {
      SpecialModelShaderRegistry.SpecialModelShaderOptions options = OPTIONS.get(rendererId);
      return options != null ? options.vertexFormat() : DefaultVertexFormat.BLOCK;
   }

   public static synchronized int appendOverlayState(
      ResourceLocation rendererId, BlockAndTintGetter level, BlockPos pos, BlockState state, BakedModel model, long modelSeed
   ) {
      SpecialModelShaderRegistry.SpecialModelShaderOptions options = OPTIONS.get(rendererId);
      return options != null && options.setupCallback() != null
         ? options.setupCallback().appendOverlayState(level, pos, state, model, modelSeed)
         : OverlayTexture.NO_OVERLAY;
   }

   @Nullable
   public static ShaderInstance getShader(ResourceLocation rendererId) {
      return SHADERS.get(rendererId);
   }

   public static synchronized Collection<SpecialModelShaderRegistry.SpecialModelShaderOptions> registeredOptions() {
      return List.copyOf(OPTIONS.values());
   }

   public static void registerCoreShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> context) throws IOException {
      SHADERS.clear();

      for (SpecialModelShaderRegistry.SpecialModelShaderOptions options : registeredOptions()) {
         context.accept(options.shaderId(), options.vertexFormat(), (Consumer<ShaderInstance>)shader -> {
            if (options.setupCallback() != null) {
               ((ShaderInstanceExt)shader).addUniformSetCallback(options.setupCallback()::setup);
            }

            SHADERS.put(options.rendererId(), shader);
         });
      }
   }

   private SpecialModelShaderRegistry() {
   }

   public record SpecialModelShaderOptions(
      ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat, @Nullable ShaderCallback setupCallback
   ) {
      public SpecialModelShaderOptions(
         ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat, @Nullable ShaderCallback setupCallback
      ) {
         Objects.requireNonNull(rendererId, "rendererId");
         Objects.requireNonNull(shaderId, "shaderId");
         Objects.requireNonNull(vertexFormat, "vertexFormat");
         this.rendererId = rendererId;
         this.shaderId = shaderId;
         this.vertexFormat = vertexFormat;
         this.setupCallback = setupCallback;
      }

      private boolean sameDefinition(SpecialModelShaderRegistry.SpecialModelShaderOptions other) {
         return this.rendererId.equals(other.rendererId)
            && this.shaderId.equals(other.shaderId)
            && this.vertexFormat.equals(other.vertexFormat)
            && this.setupCallback == other.setupCallback;
      }
   }
}
