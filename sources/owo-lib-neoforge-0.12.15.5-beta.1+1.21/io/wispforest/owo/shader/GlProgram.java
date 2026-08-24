package io.wispforest.owo.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.wispforest.owo.mixin.shader.ShaderProgramAccessor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Tuple;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class GlProgram {
   private static final List<Tuple<Function<ResourceProvider, ShaderInstance>, Consumer<ShaderInstance>>> REGISTERED_PROGRAMS = new ArrayList<>();
   protected ShaderInstance backingProgram;

   public GlProgram(ResourceLocation id, VertexFormat vertexFormat) {
      REGISTERED_PROGRAMS.add(new Tuple((Function<ResourceProvider, ShaderInstance>)resourceFactory -> {
         try {
            return new GlProgram.OwoShaderProgram(resourceFactory, id, vertexFormat);
         } catch (IOException var4) {
            throw new RuntimeException("Failed to initialized owo shader program", var4);
         }
      }, (Consumer<ShaderInstance>)program -> {
         this.backingProgram = program;
         this.setup();
      }));
   }

   public void use() {
      RenderSystem.setShader(() -> this.backingProgram);
   }

   protected void setup() {
   }

   @Nullable
   protected Uniform findUniform(String name) {
      return ((ShaderProgramAccessor)this.backingProgram).owo$getLoadedUniforms().get(name);
   }

   @Internal
   public static void forEachProgram(Consumer<Tuple<Function<ResourceProvider, ShaderInstance>, Consumer<ShaderInstance>>> loader) {
      if (!ModLoader.hasErrors()) {
         REGISTERED_PROGRAMS.forEach(loader);
      }
   }

   public static class OwoShaderProgram extends ShaderInstance {
      private OwoShaderProgram(ResourceProvider factory, ResourceLocation id, VertexFormat format) throws IOException {
         super(factory, id, format);
      }
   }
}
