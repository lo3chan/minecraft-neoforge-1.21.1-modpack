package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

@ReturnsSelf
public abstract class ModelledBuilderBase<T> extends BuilderBase<T> {
   public transient ResourceLocation parentModel = null;
   public transient Map<String, String> textures = new HashMap<>(0);
   public transient String baseTexture = "";
   public transient Consumer<ModelGenerator> modelGenerator = null;

   public ModelledBuilderBase(ResourceLocation id) {
      super(id);
   }

   @Info("Sets the texture.")
   public ModelledBuilderBase<T> texture(String tex) {
      this.baseTexture = tex;
      return this;
   }

   @Info("Sets the texture by given key.")
   public ModelledBuilderBase<T> texture(String[] key, String tex) {
      for (String k : key) {
         this.textures.put(k, tex);
      }

      return this;
   }

   @Info("Directly set the texture map.")
   public ModelledBuilderBase<T> textures(Map<String, String> map) {
      this.textures.putAll(map);
      return this;
   }

   @Info("Sets the parent model.")
   public ModelledBuilderBase<T> parentModel(ResourceLocation id) {
      this.parentModel = id;
      return this;
   }

   @Info("Replaces default model with custom generator.")
   public ModelledBuilderBase<T> modelGenerator(Consumer<ModelGenerator> generator) {
      this.modelGenerator = generator;
      return this;
   }
}
