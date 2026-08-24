package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagLoader.EntryWithSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TagLoader.class})
public abstract class TagLoaderMixin<T> implements TagLoaderKJS<T> {
   @Unique
   private ReloadableServerResourcesKJS kjs$resources;
   @Unique
   @Nullable
   private Registry<T> kjs$storedRegistry;

   @Inject(
      method = {"load"},
      at = {@At("RETURN")}
   )
   private void customTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<EntryWithSource>>> cir) {
      this.kjs$customTags(this.kjs$resources, (Map<ResourceLocation, List<EntryWithSource>>)cir.getReturnValue());
   }

   @Override
   public void kjs$init(ReloadableServerResourcesKJS resources, Registry<T> registry) {
      this.kjs$resources = resources;
      this.kjs$storedRegistry = registry;
   }

   @Override
   public ReloadableServerResourcesKJS kjs$getResources() {
      return this.kjs$resources;
   }

   @Nullable
   @Override
   public Registry<T> kjs$getRegistry() {
      return this.kjs$storedRegistry;
   }
}
