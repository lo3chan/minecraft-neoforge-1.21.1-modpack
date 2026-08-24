package io.wispforest.owo.mixin;

import io.wispforest.owo.util.TagInjector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagLoader.EntryWithSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TagLoader.class})
public class TagGroupLoaderMixin {
   @Shadow
   @Final
   private String directory;

   @Inject(
      method = {"load(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"},
      at = {@At("TAIL")}
   )
   public void injectValues(ResourceManager manager, CallbackInfoReturnable<Map<ResourceLocation, List<EntryWithSource>>> cir) {
      Map<ResourceLocation, List<EntryWithSource>> map = (Map<ResourceLocation, List<EntryWithSource>>)cir.getReturnValue();
      TagInjector.ADDITIONS.forEach((location, entries) -> {
         if (this.directory.equals(location.type())) {
            List<EntryWithSource> list = map.computeIfAbsent(location.tagId(), id -> new ArrayList<>());
            entries.forEach(addition -> list.add(new EntryWithSource(addition, "owo")));
         }
      });
   }
}
