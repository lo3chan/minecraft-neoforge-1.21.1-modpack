package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.client.SoundsGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class VirtualAssetPack extends VirtualResourcePack implements KubeAssetGenerator {
   private final Map<ResourceLocation, LoadedTexture> loadedTextures = new HashMap<>();
   private final Map<String, SoundsGenerator> sounds = new HashMap<>();

   public VirtualAssetPack(GeneratedDataStage stage, Supplier<RegistryAccessContainer> registries) {
      super(ScriptType.CLIENT, PackType.CLIENT_RESOURCES, stage, registries);
   }

   @Override
   public LoadedTexture loadTexture(ResourceLocation id) {
      return this.loadedTextures.computeIfAbsent(id, x$0 -> KubeAssetGenerator.super.loadTexture(x$0));
   }

   @Override
   public void texture(ResourceLocation target, LoadedTexture texture) {
      KubeAssetGenerator.super.texture(target, texture);
      if (texture.width > 0 && texture.height > 0) {
         this.loadedTextures.put(target, texture);
      }
   }

   @Override
   public void close() {
      super.close();
      this.loadedTextures.clear();
   }

   @Override
   public void sounds(String namespace, Consumer<SoundsGenerator> consumer) {
      this.sounds.put(namespace, (SoundsGenerator)Util.make(new SoundsGenerator(), consumer));
   }

   @Override
   public void flush() {
      super.flush();
      this.sounds.forEach((mod, gen) -> this.json(ResourceLocation.fromNamespaceAndPath(mod, "sounds.json"), gen.toJson()));
      this.sounds.clear();
   }
}
