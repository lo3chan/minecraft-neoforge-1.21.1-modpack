package org.dimdev.limlib.post;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record StaticPostEffect(ResourceLocation shaderName) implements PostEffect {
   public static final MapCodec<StaticPostEffect> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ResourceLocation.CODEC.fieldOf("shader_name").stable().forGetter(StaticPostEffect::shaderName))
         .apply(instance, instance.stable(StaticPostEffect::new))
   );

   @Override
   public PostEffect.PostEffectType<StaticPostEffect> type() {
      return PostEffect.PostEffectType.STATIC;
   }

   @Override
   public boolean shouldRender() {
      return true;
   }

   @Override
   public ResourceLocation getShaderLocation() {
      return ResourceLocation.fromNamespaceAndPath(this.shaderName.getNamespace(), "shaders/post/" + this.shaderName.getPath() + ".json");
   }
}
