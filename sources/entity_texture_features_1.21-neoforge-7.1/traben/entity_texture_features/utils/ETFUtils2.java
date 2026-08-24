package traben.entity_texture_features.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

public abstract class ETFUtils2 {
   public static final int FULL_BRIGHT = 15728880;

   @NotNull
   public static ResourceLocation res(String fullPath) {
      return ResourceLocation.parse(fullPath);
   }

   @NotNull
   public static ResourceLocation res(String namespace, String path) {
      return ResourceLocation.fromNamespaceAndPath(namespace, path);
   }

   public static void setPixel(NativeImage image, int x, int y, int color) {
      image.setPixelRGBA(x, y, color);
   }

   public static int getPixel(NativeImage image, int x, int y) {
      return image.getPixelRGBA(x, y);
   }

   public static int packLight(int sky, int block) {
      return LightTexture.pack(sky, block);
   }

   public static int optifineHashing(int x) {
      x ^= 61 ^ x >> 16;
      x += x << 3;
      x ^= x >> 4;
      x *= 668265261;
      return x ^ x >> 15;
   }

   @Deprecated
   public static void printDebugImage(NativeImage image) {
      if (ETF.isFabric() == ETF.FABRIC_API && ETF.getConfigDirectory() != null) {
         Path outputDirectory = Path.of(ETF.getConfigDirectory().toFile().getParent(), "\\ETF_debug_printout.png");

         try {
            image.writeToFile(outputDirectory);
            logMessage("printed debug image to: " + outputDirectory, false);
         } catch (Exception var3) {
            logError(var3.toString(), false);
         }
      }
   }

   public static ResourceLocation getETFVariantNotNullForInjector(ResourceLocation identifier) {
      if (identifier != null && ETFRenderContext.getCurrentEntityState() != null && ETFRenderContext.isAllowedToRenderLayerTextureModify()) {
         ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(identifier, ETFRenderContext.getCurrentEntityState());
         if (ETFRenderContext.isAllowedToPatch()) {
            etfTexture.assertPatchedTextures();
         }

         ResourceLocation modified = etfTexture.getTextureIdentifier(ETFRenderContext.getCurrentEntityState());
         return modified == null ? identifier : modified;
      } else {
         return identifier;
      }
   }

   public static boolean renderEmissive(ETFTexture texture, MultiBufferSource provider, ETFUtils2.RenderMethodForOverlay renderer) {
      if (!ETF.config().getConfig().canDoEmissiveTextures()) {
         return false;
      } else {
         ResourceLocation emissive = texture.getEmissiveIdentifierOfCurrentState();
         if (emissive != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer emissiveConsumer = provider.getBuffer(
               ETFRenderContext.canRenderInBrightMode()
                  ? RenderType.beaconBeam(emissive, true)
                  : (ETFRenderContext.shouldEmissiveUseCullingLayer() ? RenderType.entityTranslucentCull(emissive) : RenderType.entityTranslucent(emissive))
            );
            if (wasAllowed) {
               ETFRenderContext.allowRenderLayerTextureModify();
            }

            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(emissiveConsumer, 15728882);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean renderEnchanted(ETFTexture texture, MultiBufferSource provider, int light, ETFUtils2.RenderMethodForOverlay renderer) {
      ResourceLocation enchanted = texture.getEnchantIdentifierOfCurrentState();
      if (enchanted != null) {
         boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
         ETFRenderContext.preventRenderLayerTextureModify();
         VertexConsumer enchantedVertex = ItemRenderer.getArmorFoilBuffer(provider, RenderType.armorCutoutNoCull(enchanted), true);
         if (wasAllowed) {
            ETFRenderContext.allowRenderLayerTextureModify();
         }

         ETFRenderContext.startSpecialRenderOverlayPhase();
         renderer.render(enchantedVertex, light);
         ETFRenderContext.endSpecialRenderOverlayPhase();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public static ResourceLocation addVariantNumberSuffix(@NotNull ResourceLocation identifier, int variant) {
      ResourceLocation changed = res(addVariantNumberSuffix(identifier.toString(), variant));
      return identifier.equals(changed) ? null : changed;
   }

   @NotNull
   public static String addVariantNumberSuffix(String identifierString, int variant) {
      if (variant < 2) {
         return identifierString;
      } else {
         String file = identifierString.endsWith(".png") ? "png" : identifierString.substring(identifierString.lastIndexOf(46) + 1);
         return identifierString.matches("\\D+\\d+\\." + file)
            ? identifierString.replace("." + file, "." + variant + "." + file)
            : identifierString.replace("." + file, variant + "." + file);
      }
   }

   @Nullable
   public static ResourceLocation replaceIdentifier(ResourceLocation id, String regex, String replace) {
      if (id == null) {
         return null;
      } else {
         try {
            return res(id.getNamespace(), id.getPath().replaceFirst(regex, replace));
         } catch (ResourceLocationException var4) {
            logError(ETF.getTextFromTranslation("config.entity_texture_features.illegal_path_recommendation").getString() + "\n" + var4);
         } catch (Exception var5) {
         }

         return null;
      }
   }

   @Nullable
   public static String returnNameOfHighestPackFromTheseMultiple(String[] packNameList) {
      ArrayList<String> packNames = new ArrayList<>(Arrays.asList(packNameList));
      ArrayList<String> knownResourcepackOrder = ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER;

      while (packNames.size() > 1) {
         packNames.remove(knownResourcepackOrder.indexOf(packNames.get(0)) >= knownResourcepackOrder.indexOf(packNames.get(1)) ? 1 : 0);
      }

      return packNames.get(0);
   }

   @Nullable
   public static String returnNameOfHighestPackFromTheseTwo(@Nullable String pack1, @Nullable String pack2) {
      if (pack1 == null) {
         return null;
      } else if (!pack1.equals(pack2) && pack2 != null) {
         return ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack1) >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack2)
            ? pack1
            : pack2;
      } else {
         return pack1;
      }
   }

   @Nullable
   public static Properties readAndReturnPropertiesElseNull(ResourceLocation path) {
      Properties props = new Properties();

      try {
         Properties var3;
         try (InputStream in = ((Resource)Minecraft.getInstance().getResourceManager().getResource(path).get()).open()) {
            props.load(in);
            var3 = props;
         }

         return var3;
      } catch (Exception var7) {
         return null;
      }
   }

   @Nullable
   public static List<Properties> readAndReturnAllLayeredPropertiesElseNull(ResourceLocation path) {
      List<Properties> props = new ArrayList<>();

      try {
         for (Resource resource : Minecraft.getInstance().getResourceManager().getResourceStack(path)) {
            if (resource != null) {
               try (InputStream in = resource.open()) {
                  Properties prop = new Properties();
                  prop.load(in);
                  if (!prop.isEmpty()) {
                     props.add(prop);
                  }
               } catch (Exception var10) {
               }
            }
         }

         return props.isEmpty() ? null : props;
      } catch (Exception var11) {
         return null;
      }
   }

   public static NativeImage getNativeImageElseNull(@Nullable ResourceLocation identifier) {
      try {
         Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(identifier);
         if (resource.isPresent()) {
            try {
               NativeImage var11;
               try (InputStream in = resource.get().open()) {
                  var11 = NativeImage.read(in);
               }

               return var11;
            } catch (Exception var8) {
               return null;
            }
         } else if (Minecraft.getInstance().getTextureManager().getTexture(identifier) instanceof DynamicTexture nativeImageBackedTexture) {
            NativeImage image2 = nativeImageBackedTexture.getPixels();
            if (image2 == null) {
               return null;
            } else {
               NativeImage image3 = new NativeImage(image2.getWidth(), image2.getHeight(), false);
               image3.copyFrom(image2);
               return image3;
            }
         } else {
            return null;
         }
      } catch (Exception var9) {
         return null;
      }
   }

   public static void logMessage(String obj) {
      logMessage(obj, false);
   }

   public static void logMessage(String obj, boolean inChat) {
      if (inChat) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.displayClientMessage(MutableComponent.create(new LiteralContents("§a[INFO]§r [ETF]: " + obj)), false);
         } else {
            ETF.LOGGER.info("[ETF]: {}", obj);
         }
      } else {
         ETF.LOGGER.info("[ETF]: {}", obj);
      }
   }

   public static void logWarn(String obj) {
      logWarn(obj, false);
   }

   public static void logWarn(String obj, boolean inChat) {
      if (inChat) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.displayClientMessage(
               MutableComponent.create(new LiteralContents("§e[WARN]§r [Entity Texture Features]: " + obj)).withStyle(ChatFormatting.YELLOW), false
            );
         } else {
            ETF.LOGGER.warn("[ETF]: {}", obj);
         }
      } else {
         ETF.LOGGER.warn("[ETF]: {}", obj);
      }
   }

   public static void logError(String obj) {
      logError(obj, false);
   }

   public static void logError(String obj, boolean inChat) {
      if (inChat) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.displayClientMessage(
               MutableComponent.create(new LiteralContents("§4[ERROR]§r [Entity Texture Features]: " + obj))
                  .withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}),
               false
            );
         } else {
            ETF.LOGGER.error("[ETF]: {}", obj);
         }
      } else {
         ETF.LOGGER.error("[ETF]: {}", obj);
      }
   }

   public static NativeImage emptyNativeImage() {
      return emptyNativeImage(64, 64);
   }

   public static NativeImage emptyNativeImage(int Width, int Height) {
      NativeImage empty = new NativeImage(Width, Height, false);
      empty.fillRect(0, 0, Width, Height, 0);
      return empty;
   }

   public static boolean registerNativeImageToIdentifier(NativeImage image, ResourceLocation identifier) {
      if (image != null && identifier != null) {
         try {
            Minecraft.getInstance().execute(() -> {
               try {
                  NativeImage closableImage = new NativeImage(image.getWidth(), image.getHeight(), true);
                  closableImage.copyFrom(image);
                  Minecraft.getInstance().getTextureManager().release(identifier);
                  DynamicTexture closableBackedTexture = new DynamicTexture(closableImage);
                  Minecraft.getInstance().getTextureManager().register(identifier, closableBackedTexture);
               } catch (Exception var4) {
                  logError("registering native image failed (inner): " + var4);
               }
            });
            return true;
         } catch (Exception var3) {
            logError("registering native image failed: " + var3);
            return false;
         }
      } else {
         logError("registering native image failed: " + image + ", " + identifier);
         return false;
      }
   }

   public static void checkModCompatibility() {
      for (ETFConfigWarning warning : ETFConfigWarnings.getRegisteredWarnings()) {
         warning.testWarningAndApplyFixIfEnabled();
      }
   }

   public interface RenderMethodForOverlay {
      void render(VertexConsumer var1, int var2);
   }
}
