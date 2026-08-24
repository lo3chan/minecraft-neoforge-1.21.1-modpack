package net.joefoxe.hexerei.block.connected;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class CustomBlockModels {
   private final Multimap<ResourceLocation, Function<BakedModel, ? extends BakedModel>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
   private final Map<Block, Function<BakedModel, ? extends BakedModel>> finalModelFuncs = new IdentityHashMap<>();
   private boolean funcsLoaded = false;

   public void register(ResourceLocation block, Function<BakedModel, ? extends BakedModel> func) {
      this.modelFuncs.put(block, func);
   }

   public void forEach(BiConsumer<Block, Function<BakedModel, ? extends BakedModel>> consumer) {
      this.loadEntriesIfMissing();
      this.finalModelFuncs.forEach(consumer);
   }

   private void loadEntriesIfMissing() {
      if (!this.funcsLoaded) {
         this.loadEntries();
         this.funcsLoaded = true;
      }
   }

   private void loadEntries() {
      this.finalModelFuncs.clear();
      this.modelFuncs.asMap().forEach((location, funcList) -> {
         if (BuiltInRegistries.BLOCK.containsKey(location)) {
            Block block = (Block)BuiltInRegistries.BLOCK.get(location);
            Function<BakedModel, ? extends BakedModel> finalFunc = null;

            for (Function<BakedModel, ? extends BakedModel> func : funcList) {
               if (finalFunc == null) {
                  finalFunc = func;
               } else {
                  finalFunc = finalFunc.andThen(func);
               }
            }

            this.finalModelFuncs.put(block, finalFunc);
         }
      });
   }
}
