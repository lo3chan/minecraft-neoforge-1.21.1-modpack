package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFSprite {
   public final boolean isETFAltered;
   private final TextureAtlasSprite sprite;
   private final TextureAtlasSprite emissiveSprite;

   public ETFSprite(@NotNull TextureAtlasSprite originalSprite, @NotNull ETFTexture etfTexture, boolean isNotVariant) {
      if (isNotVariant) {
         this.sprite = originalSprite;
         this.isETFAltered = false;
      } else {
         ResourceLocation variantId = etfTexture.getTextureIdentifier(null);
         Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(variantId);
         if (resource.isPresent()) {
            TextureAtlasSprite possibleVariant = null;
            SpriteContents contents = load(ETFUtils2.res(variantId + "-etf_sprite"), resource.get());

            try {
               if (contents != null) {
                  possibleVariant = new TextureAtlasSprite(variantId, contents, contents.width(), contents.height(), 0, 0);
               }
            } catch (Throwable var13) {
               if (contents != null) {
                  try {
                     contents.close();
                  } catch (Throwable var11) {
                     var13.addSuppressed(var11);
                  }
               }

               throw var13;
            }

            if (contents != null) {
               contents.close();
            }

            this.sprite = Objects.requireNonNullElse(possibleVariant, originalSprite);
            this.isETFAltered = possibleVariant != null;
         } else {
            this.sprite = originalSprite;
            this.isETFAltered = false;
         }
      }

      TextureAtlasSprite possibleEmissive = null;
      if (etfTexture.eSuffix != null) {
         ResourceLocation emissiveId = etfTexture.getEmissiveIdentifierOfCurrentState();
         if (emissiveId != null) {
            Optional<Resource> resourcex = Minecraft.getInstance().getResourceManager().getResource(emissiveId);
            if (resourcex.isPresent()) {
               SpriteContents contents = load(ETFUtils2.res(emissiveId + "-etf_sprite"), resourcex.get());

               try {
                  if (contents != null) {
                     possibleEmissive = new TextureAtlasSprite(emissiveId, contents, contents.width(), contents.height(), 0, 0);
                  }
               } catch (Throwable var12) {
                  if (contents != null) {
                     try {
                        contents.close();
                     } catch (Throwable var10) {
                        var12.addSuppressed(var10);
                     }
                  }

                  throw var12;
               }

               if (contents != null) {
                  contents.close();
               }
            }
         }
      }

      this.emissiveSprite = possibleEmissive;
   }

   @Nullable
   public static SpriteContents load(ResourceLocation id, Resource resource) {
      ResourceMetadata animationResourceMetadata;
      try {
         animationResourceMetadata = resource.metadata();
      } catch (Exception var10) {
         return null;
      }

      NativeImage nativeImage;
      try (InputStream inputStream = resource.open()) {
         nativeImage = NativeImage.read(inputStream);
      } catch (IOException var9) {
         return null;
      }

      FrameSize spriteDimensions = new FrameSize(nativeImage.getWidth(), nativeImage.getHeight());
      if (Mth.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) && Mth.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
         return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
      } else {
         nativeImage.close();
         return null;
      }
   }

   @NotNull
   public TextureAtlasSprite getEmissive() {
      return this.emissiveSprite;
   }

   public boolean isEmissive() {
      return this.emissiveSprite != null;
   }

   @NotNull
   public TextureAtlasSprite getSpriteVariant() {
      return this.sprite;
   }
}
