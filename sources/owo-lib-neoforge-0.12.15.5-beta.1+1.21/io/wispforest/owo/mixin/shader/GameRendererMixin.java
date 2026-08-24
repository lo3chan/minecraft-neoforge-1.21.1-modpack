package io.wispforest.owo.mixin.shader;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.datafixers.util.Pair;
import io.wispforest.owo.shader.GlProgram;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Inject(
      method = {"reloadShaders(Lnet/minecraft/server/packs/resources/ResourceProvider;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
         ordinal = 0
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   void loadAllTheShaders(ResourceProvider factory, CallbackInfo ci, List<Program> stages, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shadersToLoad) {
      if (!ModLoader.hasErrors()) {
         GlProgram.forEachProgram(loader -> shadersToLoad.add(new Pair((ShaderInstance)((Function)loader.getA()).apply(factory), (Consumer)loader.getB())));
      }
   }
}
