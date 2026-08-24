package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.ParticleGenerator;
import dev.latvian.mods.kubejs.client.SoundsGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ID;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public interface KubeAssetGenerator extends KubeResourceGenerator {
   ResourceLocation GENERATED_ITEM_MODEL = ResourceLocation.withDefaultNamespace("item/generated");
   ResourceLocation HANDHELD_ITEM_MODEL = ResourceLocation.withDefaultNamespace("item/handheld");
   ResourceLocation CUBE_BLOCK_MODEL = ResourceLocation.withDefaultNamespace("block/cube");
   ResourceLocation CUBE_ALL_BLOCK_MODEL = ResourceLocation.withDefaultNamespace("block/cube_all");

   default LoadedTexture loadTexture(ResourceLocation id) {
      return LoadedTexture.load(id);
   }

   default void blockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
      VariantBlockStateGenerator gen = (VariantBlockStateGenerator)Util.make(new VariantBlockStateGenerator(), consumer);
      this.json(id.withPath(ID.BLOCKSTATE), gen.toJson());
   }

   default void multipartState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
      MultipartBlockStateGenerator gen = (MultipartBlockStateGenerator)Util.make(new MultipartBlockStateGenerator(), consumer);
      this.json(id.withPath(ID.BLOCKSTATE), gen.toJson());
   }

   default void blockModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
      ModelGenerator gen = (ModelGenerator)Util.make(new ModelGenerator(), consumer);
      this.json(id.withPath(ID.BLOCK_MODEL), gen.toJson());
   }

   default void itemModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
      ModelGenerator gen = (ModelGenerator)Util.make(new ModelGenerator(), consumer);
      this.json(id.withPath(ID.ITEM_MODEL), gen.toJson());
   }

   default void defaultItemModel(ResourceLocation id) {
      this.itemModel(id, model -> {
         model.parent(GENERATED_ITEM_MODEL);
         model.texture("layer0", id.withPath(ID.ITEM).toString());
      });
   }

   default void defaultHandheldItemModel(ResourceLocation id) {
      this.itemModel(id, model -> {
         model.parent(HANDHELD_ITEM_MODEL);
         model.texture("layer0", id.withPath(ID.ITEM).toString());
      });
   }

   default void texture(ResourceLocation target, LoadedTexture texture) {
      if (texture.width > 0 && texture.height > 0) {
         this.add(new GeneratedData(target.withPath(ID.PNG_TEXTURE), texture::toBytes));
         if (texture.mcmeta != null) {
            this.add(new GeneratedData(target.withPath(ID.PNG_TEXTURE_MCMETA), () -> texture.mcmeta));
         }
      } else {
         ConsoleJS.CLIENT.error("Failed to save texture " + target);
      }
   }

   default void stencil(ResourceLocation target, ResourceLocation stencil, Map<KubeColor, KubeColor> colors) {
      LoadedTexture stencilTexture = this.loadTexture(stencil);
      if (stencilTexture.width != 0 && stencilTexture.height != 0) {
         this.texture(target, stencilTexture.remap(colors));
      } else {
         ConsoleJS.CLIENT.error("Failed to load texture " + stencil);
      }
   }

   default boolean mask(ResourceLocation target, ResourceLocation mask, ResourceLocation input) {
      LoadedTexture maskTexture = this.loadTexture(mask);
      if (maskTexture.height == maskTexture.width && maskTexture.width != 0) {
         LoadedTexture in = this.loadTexture(input);
         if (in.width != 0 && in.height != 0) {
            int w = Math.max(maskTexture.width, in.width);
            if (maskTexture.width != in.width) {
               int mframes = maskTexture.height / maskTexture.width;
               int iframes = in.height / in.width;
               maskTexture = maskTexture.resize(w, w * mframes);
               in = in.resize(w, w * iframes).copy();
            } else {
               in = in.copy();
            }

            for (int y = 0; y < in.height; y++) {
               for (int x = 0; x < w; x++) {
                  int ii = x + y * w;
                  int m = maskTexture.pixels[x + y % maskTexture.height * w];
                  int ma = m >> 24 & 0xFF;
                  if (ma == 0) {
                     in.pixels[ii] = 0;
                  } else {
                     float mr = (m >> 16 & 0xFF) / 255.0F;
                     float mg = (m >> 8 & 0xFF) / 255.0F;
                     float mb = (m & 0xFF) / 255.0F;
                     float ir = (in.pixels[ii] >> 16 & 0xFF) / 255.0F;
                     float ig = (in.pixels[ii] >> 8 & 0xFF) / 255.0F;
                     float ib = (in.pixels[ii] & 0xFF) / 255.0F;
                     in.pixels[ii] = (int)(mr * ir * 255.0F) << 16 | (int)(mg * ig * 255.0F) << 8 | (int)(mb * ib * 255.0F) | ma << 24;
                  }
               }
            }

            this.texture(target, in);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   default void particle(ResourceLocation id, Consumer<ParticleGenerator> consumer) {
      this.json(id.withPath(ID.PARTICLE), ((ParticleGenerator)Util.make(new ParticleGenerator(), consumer)).toJson());
   }

   default void sounds(String namespace, Consumer<SoundsGenerator> consumer) {
   }
}
