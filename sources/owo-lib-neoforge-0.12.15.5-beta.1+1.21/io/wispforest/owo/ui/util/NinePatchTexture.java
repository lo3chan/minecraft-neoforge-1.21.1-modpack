package io.wispforest.owo.ui.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public class NinePatchTexture {
   private final ResourceLocation texture;
   private final int u;
   private final int v;
   private final Size cornerPatchSize;
   private final Size centerPatchSize;
   private final Size textureSize;
   private final boolean repeat;

   public NinePatchTexture(ResourceLocation texture, int u, int v, Size cornerPatchSize, Size centerPatchSize, Size textureSize, boolean repeat) {
      this.texture = texture;
      this.u = u;
      this.v = v;
      this.textureSize = textureSize;
      this.cornerPatchSize = cornerPatchSize;
      this.centerPatchSize = centerPatchSize;
      this.repeat = repeat;
   }

   public NinePatchTexture(ResourceLocation texture, int u, int v, Size patchSize, Size textureSize, boolean repeat) {
      this(texture, u, v, patchSize, patchSize, textureSize, repeat);
   }

   public NinePatchTexture(ResourceLocation texture, Size patchSize, Size textureSize, boolean repeat) {
      this(texture, 0, 0, patchSize, textureSize, repeat);
   }

   public void draw(OwoUIDrawContext context, PositionedRectangle rectangle) {
      this.draw(context, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
   }

   public void draw(OwoUIDrawContext context, int x, int y, int width, int height) {
      context.recordQuads();
      int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
      int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();
      context.blit(
         this.texture, x, y, this.u, this.v, this.cornerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(), this.textureSize.height()
      );
      context.blit(
         this.texture,
         x + width - this.cornerPatchSize.width(),
         y,
         this.u + rightEdge,
         this.v,
         this.cornerPatchSize.width(),
         this.cornerPatchSize.height(),
         this.textureSize.width(),
         this.textureSize.height()
      );
      context.blit(
         this.texture,
         x,
         y + height - this.cornerPatchSize.height(),
         this.u,
         this.v + bottomEdge,
         this.cornerPatchSize.width(),
         this.cornerPatchSize.height(),
         this.textureSize.width(),
         this.textureSize.height()
      );
      context.blit(
         this.texture,
         x + width - this.cornerPatchSize.width(),
         y + height - this.cornerPatchSize.height(),
         this.u + rightEdge,
         this.v + bottomEdge,
         this.cornerPatchSize.width(),
         this.cornerPatchSize.height(),
         this.textureSize.width(),
         this.textureSize.height()
      );
      if (this.repeat) {
         this.drawRepeated(context, x, y, width, height);
      } else {
         this.drawStretched(context, x, y, width, height);
      }

      context.submitQuads();
   }

   protected void drawStretched(OwoUIDrawContext context, int x, int y, int width, int height) {
      int doubleCornerHeight = this.cornerPatchSize.height() * 2;
      int doubleCornerWidth = this.cornerPatchSize.width() * 2;
      int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
      int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();
      if (width > doubleCornerWidth && height > doubleCornerHeight) {
         context.blit(
            this.texture,
            x + this.cornerPatchSize.width(),
            y + this.cornerPatchSize.height(),
            width - doubleCornerWidth,
            height - doubleCornerHeight,
            this.u + this.cornerPatchSize.width(),
            this.v + this.cornerPatchSize.height(),
            this.centerPatchSize.width(),
            this.centerPatchSize.height(),
            this.textureSize.width(),
            this.textureSize.height()
         );
      }

      if (width > doubleCornerWidth) {
         context.blit(
            this.texture,
            x + this.cornerPatchSize.width(),
            y,
            width - doubleCornerWidth,
            this.cornerPatchSize.height(),
            this.u + this.cornerPatchSize.width(),
            this.v,
            this.centerPatchSize.width(),
            this.cornerPatchSize.height(),
            this.textureSize.width(),
            this.textureSize.height()
         );
         context.blit(
            this.texture,
            x + this.cornerPatchSize.width(),
            y + height - this.cornerPatchSize.height(),
            width - doubleCornerWidth,
            this.cornerPatchSize.height(),
            this.u + this.cornerPatchSize.width(),
            this.v + bottomEdge,
            this.centerPatchSize.width(),
            this.cornerPatchSize.height(),
            this.textureSize.width(),
            this.textureSize.height()
         );
      }

      if (height > doubleCornerHeight) {
         context.blit(
            this.texture,
            x,
            y + this.cornerPatchSize.height(),
            this.cornerPatchSize.width(),
            height - doubleCornerHeight,
            this.u,
            this.v + this.cornerPatchSize.height(),
            this.cornerPatchSize.width(),
            this.centerPatchSize.height(),
            this.textureSize.width(),
            this.textureSize.height()
         );
         context.blit(
            this.texture,
            x + width - this.cornerPatchSize.width(),
            y + this.cornerPatchSize.height(),
            this.cornerPatchSize.width(),
            height - doubleCornerHeight,
            this.u + rightEdge,
            this.v + this.cornerPatchSize.height(),
            this.cornerPatchSize.width(),
            this.centerPatchSize.height(),
            this.textureSize.width(),
            this.textureSize.height()
         );
      }
   }

   protected void drawRepeated(OwoUIDrawContext context, int x, int y, int width, int height) {
      int doubleCornerHeight = this.cornerPatchSize.height() * 2;
      int doubleCornerWidth = this.cornerPatchSize.width() * 2;
      int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
      int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();
      if (width > doubleCornerWidth && height > doubleCornerHeight) {
         for (int leftoverHeight = height - doubleCornerHeight; leftoverHeight > 0; leftoverHeight -= this.centerPatchSize.height()) {
            int drawHeight = Math.min(this.centerPatchSize.height(), leftoverHeight);

            for (int leftoverWidth = width - doubleCornerWidth; leftoverWidth > 0; leftoverWidth -= this.centerPatchSize.width()) {
               int drawWidth = Math.min(this.centerPatchSize.width(), leftoverWidth);
               context.blit(
                  this.texture,
                  x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
                  y + this.cornerPatchSize.height() + leftoverHeight - drawHeight,
                  drawWidth,
                  drawHeight,
                  this.u + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
                  this.v + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
                  drawWidth,
                  drawHeight,
                  this.textureSize.width(),
                  this.textureSize.height()
               );
            }
         }
      }

      if (width > doubleCornerWidth) {
         for (int leftoverWidth = width - doubleCornerWidth; leftoverWidth > 0; leftoverWidth -= this.centerPatchSize.width()) {
            int drawWidth = Math.min(this.centerPatchSize.width(), leftoverWidth);
            context.blit(
               this.texture,
               x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
               y,
               drawWidth,
               this.cornerPatchSize.height(),
               this.u + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
               this.v,
               drawWidth,
               this.cornerPatchSize.height(),
               this.textureSize.width(),
               this.textureSize.height()
            );
            context.blit(
               this.texture,
               x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
               y + height - this.cornerPatchSize.height(),
               drawWidth,
               this.cornerPatchSize.height(),
               this.u + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
               this.v + bottomEdge,
               drawWidth,
               this.cornerPatchSize.height(),
               this.textureSize.width(),
               this.textureSize.height()
            );
         }
      }

      if (height > doubleCornerHeight) {
         for (int leftoverHeight = height - doubleCornerHeight; leftoverHeight > 0; leftoverHeight -= this.centerPatchSize.height()) {
            int drawHeight = Math.min(this.centerPatchSize.height(), leftoverHeight);
            context.blit(
               this.texture,
               x,
               y + this.cornerPatchSize.height() + leftoverHeight - drawHeight,
               this.cornerPatchSize.width(),
               drawHeight,
               this.u,
               this.v + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
               this.cornerPatchSize.width(),
               drawHeight,
               this.textureSize.width(),
               this.textureSize.height()
            );
            context.blit(
               this.texture,
               x + width - this.cornerPatchSize.width(),
               y + this.cornerPatchSize.height() + leftoverHeight - drawHeight,
               this.cornerPatchSize.width(),
               drawHeight,
               this.u + rightEdge,
               this.v + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
               this.cornerPatchSize.width(),
               drawHeight,
               this.textureSize.width(),
               this.textureSize.height()
            );
         }
      }
   }

   public static void draw(ResourceLocation texture, OwoUIDrawContext context, int x, int y, int width, int height) {
      ifPresent(texture, ninePatchTexture -> ninePatchTexture.draw(context, x, y, width, height));
   }

   public static void draw(ResourceLocation texture, OwoUIDrawContext context, PositionedRectangle rectangle) {
      ifPresent(texture, ninePatchTexture -> ninePatchTexture.draw(context, rectangle));
   }

   private static void ifPresent(ResourceLocation texture, Consumer<NinePatchTexture> action) {
      if (NinePatchTexture.MetadataLoader.LOADED_TEXTURES.containsKey(texture)) {
         action.accept(NinePatchTexture.MetadataLoader.LOADED_TEXTURES.get(texture));
      }
   }

   public static class MetadataLoader extends SimpleJsonResourceReloadListener {
      private static final Map<ResourceLocation, NinePatchTexture> LOADED_TEXTURES = new HashMap<>();

      public MetadataLoader() {
         super(new Gson(), "nine_patch_textures");
      }

      public ResourceLocation getFabricId() {
         return ResourceLocation.fromNamespaceAndPath("owo", "nine_patch_metadata");
      }

      protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
         prepared.forEach((resourceId, jsonElement) -> {
            if (jsonElement instanceof JsonObject object) {
               ResourceLocation texture = ResourceLocation.parse(GsonHelper.getAsString(object, "texture"));
               Size textureSize = Size.of(GsonHelper.getAsInt(object, "texture_width"), GsonHelper.getAsInt(object, "texture_height"));
               int u = GsonHelper.getAsInt(object, "u", 0);
               int v = GsonHelper.getAsInt(object, "v", 0);
               boolean repeat = GsonHelper.getAsBoolean(object, "repeat");
               if (object.has("corner_patch_size")) {
                  JsonObject cornerPatchObject = GsonHelper.getAsJsonObject(object, "corner_patch_size");
                  JsonObject centerPatchObject = GsonHelper.getAsJsonObject(object, "center_patch_size");
                  Size cornerPatchSize = Size.of(GsonHelper.getAsInt(cornerPatchObject, "width"), GsonHelper.getAsInt(cornerPatchObject, "height"));
                  Size centerPatchSize = Size.of(GsonHelper.getAsInt(centerPatchObject, "width"), GsonHelper.getAsInt(centerPatchObject, "height"));
                  LOADED_TEXTURES.put(resourceId, new NinePatchTexture(texture, u, v, cornerPatchSize, centerPatchSize, textureSize, repeat));
               } else {
                  JsonObject patchSizeObject = GsonHelper.getAsJsonObject(object, "patch_size");
                  Size patchSize = Size.of(GsonHelper.getAsInt(patchSizeObject, "width"), GsonHelper.getAsInt(patchSizeObject, "height"));
                  LOADED_TEXTURES.put(resourceId, new NinePatchTexture(texture, u, v, patchSize, textureSize, repeat));
               }
            }
         });
      }
   }
}
