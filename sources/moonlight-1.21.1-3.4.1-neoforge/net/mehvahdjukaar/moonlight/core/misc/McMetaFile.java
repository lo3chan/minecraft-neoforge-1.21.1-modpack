package net.mehvahdjukaar.moonlight.core.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record McMetaFile(@Nullable AnimationMetadataSection animation, JsonObject moddedStuff) {
   private static List<Field> FIELDS = null;
   private static final int VANILLA_FIELDS = 5;

   public static McMetaFile of(@NotNull AnimationMetadataSection vanillaMcmeta) {
      return of(vanillaMcmeta, new JsonObject());
   }

   public static McMetaFile of(@NotNull AnimationMetadataSection vanillaMcmeta, JsonObject moddedStuff) {
      return new McMetaFile(vanillaMcmeta == AnimationMetadataSection.EMPTY ? null : vanillaMcmeta, moddedStuff);
   }

   public static McMetaFile read(Resource resource) throws IOException {
      McMetaFile var5;
      try (InputStream metadataStream = resource.open()) {
         byte[] bytes = metadataStream.readAllBytes();
         AnimationMetadataSection metadata = (AnimationMetadataSection)AbstractPackResources.getMetadataFromStream(
            AnimationMetadataSection.SERIALIZER, new ByteArrayInputStream(bytes)
         );
         JsonObject moddedObj = readModdedObj(bytes);
         var5 = new McMetaFile(metadata, moddedObj);
      }

      return var5;
   }

   private static JsonObject readModdedObj(byte[] bytes) {
      JsonObject jo = GsonHelper.parse(new String(bytes));

      for (String key : new String[]{"animation", "frametime", "width", "height", "interpolate", "frames"}) {
         jo.remove(key);
      }

      return jo;
   }

   @Nullable
   public static McMetaFile merge(@Nullable McMetaFile mostImportant, @Nullable McMetaFile leastImportant) {
      if (mostImportant == null && leastImportant == null) {
         return null;
      } else if (leastImportant == null) {
         return mostImportant;
      } else if (mostImportant == null) {
         return leastImportant;
      } else {
         return !mostImportant.hasAnimation() ? new McMetaFile(leastImportant.animation, mostImportant.moddedStuff) : mostImportant;
      }
   }

   public boolean hasAnimation() {
      return this.animation != null;
   }

   public int requiredFrameCount() {
      if (this.animation == null) {
         return 0;
      } else {
         int[] highest = new int[]{-1};
         this.animation.forEachFrame((i, t) -> highest[0] = Math.max(highest[0], i));
         return highest[0] + 1;
      }
   }

   public int getAnimationFrameWidth() {
      return this.animation == null ? -1 : this.animation.frameWidth;
   }

   public int getAnimationFrameHeight() {
      return this.animation == null ? -1 : this.animation.frameHeight;
   }

   public JsonObject toJson() {
      JsonObject obj = this.moddedStuff.deepCopy();
      if (this.animation == null) {
         return obj;
      } else {
         JsonObject animObj = new JsonObject();
         animObj.addProperty("frametime", this.animation.getDefaultFrameTime());
         animObj.addProperty("interpolate", this.animation.isInterpolatedFrames());
         animObj.addProperty("height", this.animation.frameHeight);
         animObj.addProperty("width", this.animation.frameWidth);
         JsonArray frames = new JsonArray();
         this.animation.forEachFrame((i, t) -> {
            if (t != -1) {
               JsonObject o = new JsonObject();
               o.addProperty("time", t);
               o.addProperty("index", i);
               frames.add(o);
            } else {
               frames.add(i);
            }
         });
         if (!frames.isEmpty()) {
            animObj.add("frames", frames);
         }

         obj.add("animation", animObj);
         return obj;
      }
   }

   public McMetaFile copy() {
      return new McMetaFile(this.animation, this.moddedStuff.deepCopy());
   }

   public McMetaFile cloneWithSize(int frameWidth, int frameHeight) {
      if (this.animation == null) {
         return this.copy();
      } else {
         List<AnimationFrame> frameData = new ArrayList<>();
         this.animation.forEachFrame((i, t) -> frameData.add(new AnimationFrame(i, t)));
         AnimationMetadataSection newMetadata = new AnimationMetadataSection(
            frameData, frameWidth, frameHeight, this.animation.getDefaultFrameTime(), this.animation.isInterpolatedFrames()
         );
         JsonObject newModdedStuff = this.moddedStuff.deepCopy();
         return new McMetaFile(newMetadata, newModdedStuff);
      }
   }

   public static void copyAllMixinAddedFields(AnimationMetadataSection from, AnimationMetadataSection to) {
      if (FIELDS == null) {
         FIELDS = new ArrayList<>();
         Field[] f = AnimationMetadataSection.class.getDeclaredFields();

         for (int i = 0; i < f.length; i++) {
            if (i > 4) {
               Field field = f[i];
               FIELDS.add(field);
               field.setAccessible(true);
            }
         }
      }

      for (Field field : FIELDS) {
         try {
            field.set(to, field.get(from));
         } catch (IllegalAccessException var5) {
            throw new RuntimeException(var5);
         }
      }
   }
}
