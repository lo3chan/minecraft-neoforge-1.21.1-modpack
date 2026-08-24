package net.irisshaders.iris.pbr.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.mixin.texture.SpriteContentsAnimatedTextureAccessor;
import net.irisshaders.iris.mixin.texture.SpriteContentsFrameInfoAccessor;
import net.irisshaders.iris.mixin.texture.SpriteContentsTickerAccessor;
import net.irisshaders.iris.pbr.loader.AtlasPBRLoader;
import net.irisshaders.iris.pbr.util.TextureManipulationUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.SpriteContents.FrameInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite.Ticker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class PBRAtlasTexture extends AbstractTexture implements PBRDumpable {
   protected final TextureAtlas atlasTexture;
   protected final PBRType type;
   protected final ResourceLocation id;
   protected final Map<ResourceLocation, AtlasPBRLoader.PBRTextureAtlasSprite> texturesByName = new HashMap<>();
   protected final List<Ticker> animatedTextures = new ArrayList<>();
   protected int width;
   protected int height;
   protected int mipLevel;

   public PBRAtlasTexture(TextureAtlas atlasTexture, PBRType type) {
      this.atlasTexture = atlasTexture;
      this.type = type;
      this.id = ResourceLocation.fromNamespaceAndPath(
         atlasTexture.location().getNamespace(), atlasTexture.location().getPath().replace(".png", "") + type.getSuffix() + ".png"
      );
   }

   public static void syncAnimation(
      net.minecraft.client.renderer.texture.SpriteContents.Ticker source, net.minecraft.client.renderer.texture.SpriteContents.Ticker target
   ) {
      SpriteContentsTickerAccessor sourceAccessor = (SpriteContentsTickerAccessor)source;
      List<FrameInfo> sourceFrames = ((SpriteContentsAnimatedTextureAccessor)sourceAccessor.getAnimationInfo()).getFrames();
      int ticks = 0;

      for (int f = 0; f < sourceAccessor.getFrame(); f++) {
         ticks += ((SpriteContentsFrameInfoAccessor)sourceFrames.get(f)).getTime();
      }

      SpriteContentsTickerAccessor targetAccessor = (SpriteContentsTickerAccessor)target;
      List<FrameInfo> targetFrames = ((SpriteContentsAnimatedTextureAccessor)targetAccessor.getAnimationInfo()).getFrames();
      int cycleTime = 0;
      int frameCount = targetFrames.size();

      for (FrameInfo frame : targetFrames) {
         cycleTime += ((SpriteContentsFrameInfoAccessor)frame).getTime();
      }

      ticks %= cycleTime;
      int targetFrame = 0;

      while (true) {
         int time = ((SpriteContentsFrameInfoAccessor)targetFrames.get(targetFrame)).getTime();
         if (ticks < time) {
            targetAccessor.setFrame(targetFrame);
            targetAccessor.setSubFrame(ticks + sourceAccessor.getSubFrame());
            return;
         }

         targetFrame++;
         ticks -= time;
      }
   }

   protected static void dumpSpriteNames(Path dir, String fileName, Map<ResourceLocation, AtlasPBRLoader.PBRTextureAtlasSprite> sprites) {
      Path path = dir.resolve(fileName + ".txt");

      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
         for (Entry<ResourceLocation, AtlasPBRLoader.PBRTextureAtlasSprite> entry : sprites.entrySet().stream().sorted(Entry.comparingByKey()).toList()) {
            AtlasPBRLoader.PBRTextureAtlasSprite sprite = entry.getValue();
            writer.write(
               String.format(
                  Locale.ROOT,
                  "%s\tx=%d\ty=%d\tw=%d\th=%d%n",
                  entry.getKey(),
                  sprite.getX(),
                  sprite.getY(),
                  sprite.contents().width(),
                  sprite.contents().height()
               )
            );
         }
      } catch (IOException var10) {
         Iris.logger.warn("Failed to write file {}", path, var10);
      }
   }

   public PBRType getType() {
      return this.type;
   }

   public ResourceLocation getAtlasId() {
      return this.id;
   }

   public void addSprite(AtlasPBRLoader.PBRTextureAtlasSprite sprite) {
      this.texturesByName.put(sprite.contents().name(), sprite);
   }

   @Nullable
   public AtlasPBRLoader.PBRTextureAtlasSprite getSprite(ResourceLocation id) {
      return this.texturesByName.get(id);
   }

   public void clear() {
      this.animatedTextures.forEach(Ticker::close);
      this.texturesByName.clear();
      this.animatedTextures.clear();
   }

   public void upload(int atlasWidth, int atlasHeight, int mipLevel) {
      int glId = this.getId();
      TextureUtil.prepareImage(glId, mipLevel, atlasWidth, atlasHeight);
      TextureManipulationUtil.fillWithColor(glId, mipLevel, this.type.getDefaultValue());
      this.width = atlasWidth;
      this.height = atlasHeight;
      this.mipLevel = mipLevel;

      for (AtlasPBRLoader.PBRTextureAtlasSprite sprite : this.texturesByName.values()) {
         try {
            this.uploadSprite(sprite);
         } catch (Throwable var10) {
            CrashReport crashReport = CrashReport.forThrowable(var10, "Stitching texture atlas");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Texture being stitched together");
            crashReportCategory.setDetail("Atlas path", this.id);
            crashReportCategory.setDetail("Sprite", sprite);
            throw new ReportedException(crashReport);
         }
      }

      PBRAtlasHolder pbrHolder = ((TextureAtlasExtension)this.atlasTexture).getOrCreatePBRHolder();
      switch (this.type) {
         case NORMAL:
            pbrHolder.setNormalAtlas(this);
            break;
         case SPECULAR:
            pbrHolder.setSpecularAtlas(this);
      }
   }

   public boolean tryUpload(int atlasWidth, int atlasHeight, int mipLevel) {
      try {
         this.upload(atlasWidth, atlasHeight, mipLevel);
         return true;
      } catch (Throwable var5) {
         return false;
      }
   }

   protected void uploadSprite(AtlasPBRLoader.PBRTextureAtlasSprite sprite) {
      Ticker spriteTicker = sprite.createTicker();
      if (spriteTicker != null) {
         this.animatedTextures.add(spriteTicker);
         net.minecraft.client.renderer.texture.SpriteContents.Ticker sourceTicker = ((net.irisshaders.iris.pbr.SpriteContentsExtension)sprite.getBaseSprite()
               .contents())
            .getCreatedTicker();
         net.minecraft.client.renderer.texture.SpriteContents.Ticker targetTicker = ((net.irisshaders.iris.pbr.SpriteContentsExtension)sprite.contents())
            .getCreatedTicker();
         if (sourceTicker != null && targetTicker != null) {
            syncAnimation(sourceTicker, targetTicker);
            SpriteContentsTickerAccessor tickerAccessor = (SpriteContentsTickerAccessor)targetTicker;
            SpriteContentsAnimatedTextureAccessor infoAccessor = (SpriteContentsAnimatedTextureAccessor)tickerAccessor.getAnimationInfo();
            infoAccessor.invokeUploadFrame(
               sprite.getX(), sprite.getY(), ((SpriteContentsFrameInfoAccessor)infoAccessor.getFrames().get(tickerAccessor.getFrame())).getIndex()
            );
            return;
         }
      }

      sprite.uploadFirstFrame();
   }

   public void cycleAnimationFrames() {
      this.bind();

      for (Ticker ticker : this.animatedTextures) {
         ticker.tickAndUpload();
      }
   }

   public void close() {
      PBRAtlasHolder pbrHolder = ((TextureAtlasExtension)this.atlasTexture).getPBRHolder();
      if (pbrHolder != null) {
         switch (this.type) {
            case NORMAL:
               pbrHolder.setNormalAtlas(null);
               break;
            case SPECULAR:
               pbrHolder.setSpecularAtlas(null);
         }
      }

      this.clear();
   }

   public void load(ResourceManager manager) {
   }

   public void dumpContents(ResourceLocation id, Path path) {
      String fileName = id.toDebugFileName();
      TextureUtil.writeAsPNG(path, fileName, this.getId(), this.mipLevel, this.width, this.height);
      dumpSpriteNames(path, fileName, this.texturesByName);
   }

   @Override
   public ResourceLocation getDefaultDumpLocation() {
      return this.id;
   }
}
