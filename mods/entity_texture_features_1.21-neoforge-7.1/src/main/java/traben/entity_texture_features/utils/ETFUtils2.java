/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.ChatFormatting
 *  net.minecraft.ResourceLocationException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.contents.PlainTextContents$LiteralContents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
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
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
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
    public static final int FULL_BRIGHT = 0xF000F0;

    @NotNull
    public static ResourceLocation res(String fullPath) {
        return ResourceLocation.parse((String)fullPath);
    }

    @NotNull
    public static ResourceLocation res(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath((String)namespace, (String)path);
    }

    public static void setPixel(NativeImage image, int x, int y, int color) {
        image.setPixelRGBA(x, y, color);
    }

    public static int getPixel(NativeImage image, int x, int y) {
        return image.getPixelRGBA(x, y);
    }

    public static int packLight(int sky, int block) {
        return LightTexture.pack((int)sky, (int)block);
    }

    public static int optifineHashing(int x) {
        x ^= 0x3D ^ x >> 16;
        x += x << 3;
        x ^= x >> 4;
        x *= 668265261;
        x ^= x >> 15;
        return x;
    }

    @Deprecated
    public static void printDebugImage(NativeImage image) {
        if (ETF.isFabric() == ETF.FABRIC_API && ETF.getConfigDirectory() != null) {
            Path outputDirectory = Path.of(ETF.getConfigDirectory().toFile().getParent(), "\\ETF_debug_printout.png");
            try {
                image.writeToFile(outputDirectory);
                ETFUtils2.logMessage("printed debug image to: " + String.valueOf(outputDirectory), false);
            }
            catch (Exception e) {
                ETFUtils2.logError(e.toString(), false);
            }
        }
    }

    public static ResourceLocation getETFVariantNotNullForInjector(ResourceLocation identifier) {
        ResourceLocation modified;
        if (identifier == null || ETFRenderContext.getCurrentEntityState() == null || !ETFRenderContext.isAllowedToRenderLayerTextureModify()) {
            return identifier;
        }
        ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(identifier, ETFRenderContext.getCurrentEntityState());
        if (ETFRenderContext.isAllowedToPatch()) {
            etfTexture.assertPatchedTextures();
        }
        return (modified = etfTexture.getTextureIdentifier(ETFRenderContext.getCurrentEntityState())) == null ? identifier : modified;
    }

    public static boolean renderEmissive(ETFTexture texture, MultiBufferSource provider, RenderMethodForOverlay renderer) {
        if (!ETF.config().getConfig().canDoEmissiveTextures()) {
            return false;
        }
        ResourceLocation emissive = texture.getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer emissiveConsumer = provider.getBuffer(ETFRenderContext.canRenderInBrightMode() ? RenderType.beaconBeam((ResourceLocation)emissive, (boolean)true) : (ETFRenderContext.shouldEmissiveUseCullingLayer() ? RenderType.entityTranslucentCull((ResourceLocation)emissive) : RenderType.entityTranslucent((ResourceLocation)emissive)));
            if (wasAllowed) {
                ETFRenderContext.allowRenderLayerTextureModify();
            }
            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(emissiveConsumer, 0xF000F2);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    public static boolean renderEnchanted(ETFTexture texture, MultiBufferSource provider, int light, RenderMethodForOverlay renderer) {
        ResourceLocation enchanted = texture.getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer enchantedVertex = ItemRenderer.getArmorFoilBuffer((MultiBufferSource)provider, (RenderType)RenderType.armorCutoutNoCull((ResourceLocation)enchanted), (boolean)true);
            if (wasAllowed) {
                ETFRenderContext.allowRenderLayerTextureModify();
            }
            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(enchantedVertex, light);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    @Nullable
    public static ResourceLocation addVariantNumberSuffix(@NotNull ResourceLocation identifier, int variant) {
        ResourceLocation changed = ETFUtils2.res(ETFUtils2.addVariantNumberSuffix(identifier.toString(), variant));
        return identifier.equals((Object)changed) ? null : changed;
    }

    @NotNull
    public static String addVariantNumberSuffix(String identifierString, int variant) {
        String file;
        if (variant < 2) {
            return identifierString;
        }
        String string = file = identifierString.endsWith(".png") ? "png" : identifierString.substring(identifierString.lastIndexOf(46) + 1);
        if (identifierString.matches("\\D+\\d+\\." + file)) {
            return identifierString.replace("." + file, "." + variant + "." + file);
        }
        return identifierString.replace("." + file, variant + "." + file);
    }

    @Nullable
    public static ResourceLocation replaceIdentifier(ResourceLocation id, String regex, String replace) {
        if (id == null) {
            return null;
        }
        try {
            return ETFUtils2.res(id.getNamespace(), id.getPath().replaceFirst(regex, replace));
        }
        catch (ResourceLocationException idFail) {
            ETFUtils2.logError(ETF.getTextFromTranslation("config.entity_texture_features.illegal_path_recommendation").getString() + "\n" + String.valueOf((Object)idFail));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseMultiple(String[] packNameList) {
        ArrayList<String> packNames = new ArrayList<String>(Arrays.asList(packNameList));
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
        }
        if (pack1.equals(pack2) || pack2 == null) {
            return pack1;
        }
        return ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack1) >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack2) ? pack1 : pack2;
    }

    @Nullable
    public static Properties readAndReturnPropertiesElseNull(ResourceLocation path) {
        Properties properties;
        block8: {
            Properties props = new Properties();
            InputStream in = ((Resource)Minecraft.getInstance().getResourceManager().getResource(path).get()).open();
            try {
                props.load(in);
                properties = props;
                if (in == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    return null;
                }
            }
            in.close();
        }
        return properties;
    }

    @Nullable
    public static List<Properties> readAndReturnAllLayeredPropertiesElseNull(ResourceLocation path) {
        ArrayList<Properties> props = new ArrayList<Properties>();
        try {
            List resources = Minecraft.getInstance().getResourceManager().getResourceStack(path);
            for (Resource resource : resources) {
                if (resource == null) continue;
                try {
                    InputStream in = resource.open();
                    try {
                        Properties prop = new Properties();
                        prop.load(in);
                        if (prop.isEmpty()) continue;
                        props.add(prop);
                    }
                    finally {
                        if (in == null) continue;
                        in.close();
                    }
                }
                catch (Exception exception) {}
            }
            return props.isEmpty() ? null : props;
        }
        catch (Exception e) {
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static NativeImage getNativeImageElseNull(@Nullable ResourceLocation identifier) {
        try {
            Optional resource = Minecraft.getInstance().getResourceManager().getResource(identifier);
            if (resource.isPresent()) {
                try (InputStream in = ((Resource)resource.get()).open();){
                    NativeImage nativeImage = NativeImage.read((InputStream)in);
                    return nativeImage;
                }
                catch (Exception e) {
                    return null;
                }
            }
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(identifier);
            if (!(texture instanceof DynamicTexture)) return null;
            DynamicTexture nativeImageBackedTexture = (DynamicTexture)texture;
            NativeImage image2 = nativeImageBackedTexture.getPixels();
            if (image2 == null) {
                return null;
            }
            NativeImage image3 = new NativeImage(image2.getWidth(), image2.getHeight(), false);
            image3.copyFrom(image2);
            return image3;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void logMessage(String obj) {
        ETFUtils2.logMessage(obj, false);
    }

    public static void logMessage(String obj, boolean inChat) {
        if (inChat) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage((Component)MutableComponent.create((ComponentContents)new PlainTextContents.LiteralContents("\u00a7a[INFO]\u00a7r [ETF]: " + obj)), false);
            } else {
                ETF.LOGGER.info("[ETF]: {}", (Object)obj);
            }
        } else {
            ETF.LOGGER.info("[ETF]: {}", (Object)obj);
        }
    }

    public static void logWarn(String obj) {
        ETFUtils2.logWarn(obj, false);
    }

    public static void logWarn(String obj, boolean inChat) {
        if (inChat) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage((Component)MutableComponent.create((ComponentContents)new PlainTextContents.LiteralContents("\u00a7e[WARN]\u00a7r [Entity Texture Features]: " + obj)).withStyle(ChatFormatting.YELLOW), false);
            } else {
                ETF.LOGGER.warn("[ETF]: {}", (Object)obj);
            }
        } else {
            ETF.LOGGER.warn("[ETF]: {}", (Object)obj);
        }
    }

    public static void logError(String obj) {
        ETFUtils2.logError(obj, false);
    }

    public static void logError(String obj, boolean inChat) {
        if (inChat) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage((Component)MutableComponent.create((ComponentContents)new PlainTextContents.LiteralContents("\u00a74[ERROR]\u00a7r [Entity Texture Features]: " + obj)).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), false);
            } else {
                ETF.LOGGER.error("[ETF]: {}", (Object)obj);
            }
        } else {
            ETF.LOGGER.error("[ETF]: {}", (Object)obj);
        }
    }

    public static NativeImage emptyNativeImage() {
        return ETFUtils2.emptyNativeImage(64, 64);
    }

    public static NativeImage emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
    }

    public static boolean registerNativeImageToIdentifier(NativeImage image, ResourceLocation identifier) {
        if (image == null || identifier == null) {
            ETFUtils2.logError("registering native image failed: " + String.valueOf(image) + ", " + String.valueOf(identifier));
            return false;
        }
        try {
            Minecraft.getInstance().execute(() -> {
                try {
                    NativeImage closableImage = new NativeImage(image.getWidth(), image.getHeight(), true);
                    closableImage.copyFrom(image);
                    Minecraft.getInstance().getTextureManager().release(identifier);
                    DynamicTexture closableBackedTexture = new DynamicTexture(closableImage);
                    Minecraft.getInstance().getTextureManager().register(identifier, (AbstractTexture)closableBackedTexture);
                }
                catch (Exception e) {
                    ETFUtils2.logError("registering native image failed (inner): " + String.valueOf(e));
                }
            });
            return true;
        }
        catch (Exception e) {
            ETFUtils2.logError("registering native image failed: " + String.valueOf(e));
            return false;
        }
    }

    public static void checkModCompatibility() {
        for (ETFConfigWarning warning : ETFConfigWarnings.getRegisteredWarnings()) {
            warning.testWarningAndApplyFixIfEnabled();
        }
    }

    public static interface RenderMethodForOverlay {
        public void render(VertexConsumer var1, int var2);
    }
}

