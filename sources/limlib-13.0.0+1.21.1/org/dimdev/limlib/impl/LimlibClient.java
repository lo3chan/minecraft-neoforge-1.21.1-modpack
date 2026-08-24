package org.dimdev.limlib.impl;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.limlib.api.client.ModClient;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry;
import org.dimdev.limlib.impl.client.LimLibClientSided;
import org.dimdev.limlib.impl.shader.PostProcesserManager;

public enum LimlibClient implements ModClient<LimLibClientSided<?>> {
   INSTANCE;

   public void init(LimLibClientSided<?> sided) {
      EffectRenderers.init();
      SpecialModelLoadingPlugin.init(sided);
      sided.registerClientLoader("shaders", PostProcesserManager.INSTANCE::onResourceManagerReload);
   }

   @Override
   public void initShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> shaderRegister) {
      try {
         SpecialModelShaderRegistry.registerCoreShaders(shaderRegister);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public String getModId() {
      return "limlib";
   }
}
