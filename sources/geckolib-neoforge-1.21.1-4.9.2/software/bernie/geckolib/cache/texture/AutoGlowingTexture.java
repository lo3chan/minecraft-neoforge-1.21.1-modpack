package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.OverlayStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.resource.GeoGlowingTextureMeta;

public class AutoGlowingTexture extends GeoAbstractTexture {
   private static final ShaderStateShard SHADER_STATE = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
   private static final TransparencyStateShard TRANSPARENCY_STATE = new TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   private static final WriteMaskStateShard WRITE_MASK = new WriteMaskStateShard(true, true);
   private static final BiFunction<ResourceLocation, Boolean, RenderType> GLOWING_RENDER_TYPE = Util.memoize(
      (texture, isGlowing) -> {
         TextureStateShard textureState = new TextureStateShard(texture, false, false);
         return RenderType.create(
            "geo_glowing_layer",
            DefaultVertexFormat.NEW_ENTITY,
            Mode.QUADS,
            256,
            false,
            true,
            CompositeState.builder()
               .setShaderState(SHADER_STATE)
               .setTextureState(textureState)
               .setTransparencyState(TRANSPARENCY_STATE)
               .setOverlayState(new OverlayStateShard(true))
               .setWriteMaskState(WRITE_MASK)
               .createCompositeState(isGlowing)
         );
      }
   );
   @Deprecated(
      forRemoval = true
   )
   private static final Function<ResourceLocation, RenderType> RENDER_TYPE_FUNCTION = Util.memoize(texture -> GLOWING_RENDER_TYPE.apply(texture, false));
   private static final String APPENDIX = "_glowmask";
   public static boolean PRINT_DEBUG_IMAGES = false;
   protected final ResourceLocation textureBase;
   protected final ResourceLocation glowLayer;

   public AutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
      this.textureBase = originalLocation;
      this.glowLayer = location;
   }

   public static ResourceLocation getEmissiveResource(ResourceLocation baseResource) {
      ResourceLocation path = appendToPath(baseResource, "_glowmask");
      generateTexture(path, textureManager -> textureManager.register(path, new AutoGlowingTexture(baseResource, path)));
      return path;
   }

   @Nullable
   @Override
   protected RenderCall loadTexture(ResourceManager resourceManager, Minecraft mc) throws IOException {
      AbstractTexture originalTexture;
      try {
         originalTexture = (AbstractTexture)mc.submit(() -> mc.getTextureManager().getTexture(this.textureBase)).get();
      } catch (ExecutionException | InterruptedException var13) {
         throw new IOException("Failed to load original texture: " + this.textureBase, var13);
      }

      Resource textureBaseResource = (Resource)resourceManager.getResource(this.textureBase).get();
      NativeImage baseImage = originalTexture instanceof DynamicTexture dynamicTexture
         ? dynamicTexture.getPixels()
         : NativeImage.read(textureBaseResource.open());
      NativeImage glowImage = null;
      Optional<TextureMetadataSection> textureBaseMeta = textureBaseResource.metadata().getSection(TextureMetadataSection.SERIALIZER);
      boolean blur = textureBaseMeta.isPresent() && textureBaseMeta.get().isBlur();
      boolean clamp = textureBaseMeta.isPresent() && textureBaseMeta.get().isClamp();

      try {
         Optional<Resource> glowLayerResource = resourceManager.getResource(this.glowLayer);
         GeoGlowingTextureMeta glowLayerMeta = null;
         if (glowLayerResource.isPresent()) {
            glowImage = NativeImage.read(glowLayerResource.get().open());
            glowLayerMeta = GeoGlowingTextureMeta.fromExistingImage(glowImage);
            if (baseImage != null && (glowImage.getWidth() != baseImage.getWidth() || glowImage.getHeight() != baseImage.getHeight())) {
               throw new IllegalStateException(
                  String.format("Glowmask texture dimensions do not match base texture dimensions! Mask: %s, Base: %s", this.glowLayer, this.textureBase)
               );
            }
         } else {
            Optional<GeoGlowingTextureMeta> meta = textureBaseResource.metadata().getSection(GeoGlowingTextureMeta.DESERIALIZER);
            if (meta.isPresent()) {
               glowLayerMeta = meta.get();
               glowImage = new NativeImage(baseImage.getWidth(), baseImage.getHeight(), true);
            }
         }

         if (glowLayerMeta != null) {
            glowLayerMeta.createImageMask(baseImage, glowImage);
            if (PRINT_DEBUG_IMAGES && GeckoLibServices.PLATFORM.isDevelopmentEnvironment()) {
               this.printDebugImageToDisk(this.textureBase, baseImage);
               this.printDebugImageToDisk(this.glowLayer, glowImage);
            }
         }
      } catch (IOException var14) {
         GeckoLibConstants.LOGGER.warn("Resource failed to open for glowlayer meta: {}", this.glowLayer, var14);
      }

      NativeImage mask = glowImage;
      if (mask == null) {
         return null;
      } else {
         boolean animated;
         boolean var19 = animated = originalTexture instanceof AnimatableTexture animatableTexture && animatableTexture.isAnimated();
         if (animated) {
            ((AnimatableTexture)originalTexture).animationContents.animatedTexture.setGlowMaskTexture(this, baseImage, mask);
         }

         return () -> {
            if (!animated) {
               uploadSimple(this.getId(), mask, blur, clamp);
            }

            if (originalTexture instanceof DynamicTexture dynamicTexturex) {
               dynamicTexturex.upload();
            } else {
               uploadSimple(originalTexture.getId(), baseImage, blur, clamp);
            }
         };
      }
   }

   public static RenderType getRenderType(ResourceLocation texture) {
      return GLOWING_RENDER_TYPE.apply(getEmissiveResource(texture), false);
   }

   public static RenderType getOutlineRenderType(ResourceLocation texture) {
      return GLOWING_RENDER_TYPE.apply(getEmissiveResource(texture), true);
   }
}
