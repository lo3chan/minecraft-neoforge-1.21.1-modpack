/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  java.lang.MatchException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFException;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFSprite;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFTexture {
    public static final String PATCH_NAMESPACE_PREFIX = "etf_patched_";
    public final ResourceLocation thisIdentifier;
    public TextureReturnState currentTextureState = TextureReturnState.NORMAL;
    public String eSuffix = null;
    private ResourceLocation thisIdentifier_Patched = null;
    private ResourceLocation emissiveIdentifier = null;
    private ResourceLocation emissiveBlinkIdentifier = null;
    private ResourceLocation emissiveBlink2Identifier = null;
    private ResourceLocation enchantIdentifier = null;
    private ResourceLocation enchantBlinkIdentifier = null;
    private ResourceLocation enchantBlink2Identifier = null;
    private ResourceLocation blinkIdentifier = null;
    private ResourceLocation blink2Identifier = null;
    private ResourceLocation blinkIdentifier_Patched = null;
    private ResourceLocation blink2Identifier_Patched = null;
    private Integer blinkLength;
    private Integer blinkFrequency;
    private boolean isBuilt;
    private ETFSprite atlasSprite;
    private boolean hasBeenReRegistered;
    private Boolean resourceExists;
    private boolean guiBlink;
    private boolean hasPatched;

    public ETFTexture(ResourceLocation variantIdentifier) {
        this.blinkLength = ETF.config().getConfig().blinkLength;
        this.blinkFrequency = ETF.config().getConfig().blinkFrequency;
        this.isBuilt = false;
        this.atlasSprite = null;
        this.hasBeenReRegistered = false;
        this.resourceExists = null;
        this.guiBlink = false;
        this.hasPatched = false;
        if (variantIdentifier == null) {
            ETFUtils2.logError("ETFTexture had a null identifier this should NOT happen");
            this.thisIdentifier = null;
            return;
        }
        this.thisIdentifier = variantIdentifier;
        this.setupBlinking();
        this.setupEmissives();
        this.setupEnchants();
    }

    public static ETFTexture manual(@NotNull ResourceLocation modifiedSkinIdentifier, @Nullable ResourceLocation blinkIdentifier, @Nullable ResourceLocation blink2Identifier, @Nullable ResourceLocation emissiveIdentifier, @Nullable ResourceLocation blinkEmissiveIdentifier, @Nullable ResourceLocation blink2EmissiveIdentifier, @Nullable ResourceLocation enchantIdentifier, @Nullable ResourceLocation blinkenchantIdentifier, @Nullable ResourceLocation blink2enchantIdentifier, @Nullable ResourceLocation patchIdentifier, @Nullable ResourceLocation blinkpatchIdentifier, @Nullable ResourceLocation blink2patchIdentifier) {
        return new ETFTexture(modifiedSkinIdentifier, blinkIdentifier, blink2Identifier, emissiveIdentifier, blinkEmissiveIdentifier, blink2EmissiveIdentifier, enchantIdentifier, blinkenchantIdentifier, blink2enchantIdentifier, patchIdentifier, blinkpatchIdentifier, blink2patchIdentifier);
    }

    public static ETFTexture manual(@NotNull ResourceLocation modifiedSkinIdentifier, @Nullable ResourceLocation emissiveIdentifier, @Nullable ResourceLocation enchantIdentifier) {
        return new ETFTexture(modifiedSkinIdentifier, null, null, emissiveIdentifier, null, null, enchantIdentifier, null, null, null, null, null);
    }

    private ETFTexture(@NotNull ResourceLocation modifiedSkinIdentifier, @Nullable ResourceLocation blinkIdentifier, @Nullable ResourceLocation blink2Identifier, @Nullable ResourceLocation emissiveIdentifier, @Nullable ResourceLocation blinkEmissiveIdentifier, @Nullable ResourceLocation blink2EmissiveIdentifier, @Nullable ResourceLocation enchantIdentifier, @Nullable ResourceLocation blinkenchantIdentifier, @Nullable ResourceLocation blink2enchantIdentifier, @Nullable ResourceLocation patchIdentifier, @Nullable ResourceLocation blinkpatchIdentifier, @Nullable ResourceLocation blink2patchIdentifier) {
        this.blinkLength = ETF.config().getConfig().blinkLength;
        this.blinkFrequency = ETF.config().getConfig().blinkFrequency;
        this.isBuilt = false;
        this.atlasSprite = null;
        this.hasBeenReRegistered = false;
        this.resourceExists = null;
        this.guiBlink = false;
        this.hasPatched = false;
        this.thisIdentifier = modifiedSkinIdentifier;
        this.blinkIdentifier = blinkIdentifier;
        this.blink2Identifier = blink2Identifier;
        this.emissiveIdentifier = emissiveIdentifier;
        this.emissiveBlinkIdentifier = blinkEmissiveIdentifier;
        this.emissiveBlink2Identifier = blink2EmissiveIdentifier;
        this.thisIdentifier_Patched = patchIdentifier;
        this.blinkIdentifier_Patched = blinkpatchIdentifier;
        this.blink2Identifier_Patched = blink2patchIdentifier;
        this.enchantIdentifier = enchantIdentifier;
        this.enchantBlinkIdentifier = blinkenchantIdentifier;
        this.enchantBlink2Identifier = blink2enchantIdentifier;
        boolean bl = this.hasPatched = this.thisIdentifier_Patched != null;
        if (this.hasPatched) {
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.thisIdentifier_Patched, this);
            if (this.blinkIdentifier_Patched != null) {
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blinkIdentifier_Patched, this);
            }
            if (this.blink2Identifier_Patched != null) {
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blink2Identifier_Patched, this);
            }
        }
        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.thisIdentifier, this);
        if (blinkIdentifier != null) {
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blinkIdentifier, this);
        }
        if (blink2Identifier != null) {
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blink2Identifier, this);
        }
    }

    private ETFTexture(@NotNull ResourceLocation modifiedSkinIdentifier, @Nullable ResourceLocation emissiveIdentifier) {
        this.blinkLength = ETF.config().getConfig().blinkLength;
        this.blinkFrequency = ETF.config().getConfig().blinkFrequency;
        this.isBuilt = false;
        this.atlasSprite = null;
        this.hasBeenReRegistered = false;
        this.resourceExists = null;
        this.guiBlink = false;
        this.hasPatched = false;
        this.thisIdentifier = modifiedSkinIdentifier;
        this.emissiveIdentifier = emissiveIdentifier;
    }

    public static ETFTexture ofUnmodifiable(@NotNull ResourceLocation identifier, @Nullable ResourceLocation emissiveIdentifier) {
        return new ETFTexture(identifier, emissiveIdentifier);
    }

    public static void patchTextureToRemoveZFightingWithOtherTexture(NativeImage baseImage, NativeImage otherImage) throws IndexOutOfBoundsException {
        try {
            if (otherImage.getWidth() == baseImage.getWidth() && otherImage.getHeight() == baseImage.getHeight()) {
                for (int x = 0; x < baseImage.getWidth(); ++x) {
                    for (int y = 0; y < baseImage.getHeight(); ++y) {
                        if (otherImage.getLuminanceOrAlpha(x, y) == 0) continue;
                        ETFUtils2.setPixel(baseImage, x, y, 0);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ETFException("additional texture is not the correct size, ETF has crashed in the patching stage");
        }
    }

    private static boolean doesAnimaticaVersionExist(ResourceLocation identifier) {
        if (identifier == null) {
            return false;
        }
        String idString = identifier.toString();
        if (idString.endsWith("-anim")) {
            return true;
        }
        return Minecraft.getInstance().getTextureManager().getTexture(ETFUtils2.res(idString + "-anim"), null) != null;
    }

    private void setupBlinking() {
        ResourceLocation propertyIdentifier;
        Properties blinkingProps;
        if (!ETF.config().getConfig().enableBlinking) {
            return;
        }
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional vanillaR1 = resourceManager.getResource(this.thisIdentifier);
        if (vanillaR1.isEmpty()) {
            return;
        }
        ResourceLocation possibleBlinkIdentifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink.png");
        Optional blinkR1 = resourceManager.getResource(possibleBlinkIdentifier);
        if (blinkR1.isEmpty()) {
            return;
        }
        String blink1PackName = ((Resource)blinkR1.get()).sourcePackId();
        if (!blink1PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(blink1PackName, ((Resource)vanillaR1.get()).sourcePackId()))) {
            return;
        }
        this.blinkIdentifier = possibleBlinkIdentifier;
        ResourceLocation possibleBlink2Identifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink2.png");
        Optional blinkR2 = resourceManager.getResource(possibleBlink2Identifier);
        if (blinkR2.isPresent() && blink1PackName.equals(((Resource)blinkR2.get()).sourcePackId())) {
            this.blink2Identifier = possibleBlink2Identifier;
        }
        if ((blinkingProps = ETFUtils2.readAndReturnPropertiesElseNull(propertyIdentifier = ETFUtils2.replaceIdentifier(possibleBlinkIdentifier, ".png", ".properties"))) == null) {
            return;
        }
        Optional propertyResource = resourceManager.getResource(propertyIdentifier);
        if (propertyResource.isEmpty() || !((Resource)propertyResource.get()).sourcePackId().equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(((Resource)propertyResource.get()).sourcePackId(), blink1PackName))) {
            return;
        }
        this.blinkLength = blinkingProps.containsKey("blinkLength") ? Integer.parseInt(blinkingProps.getProperty("blinkLength").replaceAll("\\D", "")) : ETF.config().getConfig().blinkLength;
        this.blinkFrequency = blinkingProps.containsKey("blinkFrequency") ? Integer.parseInt(blinkingProps.getProperty("blinkFrequency").replaceAll("\\D", "")) : ETF.config().getConfig().blinkFrequency;
    }

    public boolean exists() {
        if (this.resourceExists == null) {
            this.resourceExists = Minecraft.getInstance().getResourceManager().getResource(this.thisIdentifier).isPresent();
        }
        return this.isBuilt || this.resourceExists != false;
    }

    private void setupEmissives() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        for (String suffix : ETFManager.getInstance().EMISSIVE_SUFFIX_LIST) {
            String emissivePack;
            ResourceLocation emissiveId;
            Optional emissiveResource;
            Optional<Resource> baseResource = this.getResourceOrModifyForTrims(resourceManager);
            if (baseResource.isEmpty() || (emissiveResource = resourceManager.getResource(emissiveId = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", suffix + ".png"))).isEmpty() || !(emissivePack = ((Resource)emissiveResource.get()).sourcePackId()).equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(emissivePack, baseResource.get().sourcePackId()))) continue;
            this.emissiveIdentifier = emissiveId;
            this.eSuffix = suffix;
            ResourceLocation blinkId = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink" + suffix + ".png");
            Optional blinkResource = resourceManager.getResource(blinkId);
            if (!blinkResource.isPresent() || !emissivePack.equals(((Resource)blinkResource.get()).sourcePackId())) break;
            this.emissiveBlinkIdentifier = blinkId;
            ResourceLocation blink2Id = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink2" + suffix + ".png");
            Optional blink2Resource = resourceManager.getResource(blink2Id);
            if (!blink2Resource.isPresent() || !emissivePack.equals(((Resource)blink2Resource.get()).sourcePackId())) break;
            this.emissiveBlink2Identifier = blink2Id;
            break;
        }
    }

    private void setupEnchants() {
        if (!ETF.config().getConfig().enableEnchantedTextures) {
            return;
        }
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        String enchantSuffix = "_enchant";
        Optional<Resource> vanillaR1 = this.getResourceOrModifyForTrims(resourceManager);
        if (vanillaR1.isEmpty()) {
            return;
        }
        ResourceLocation possibleEnchantIdentifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", enchantSuffix + ".png");
        Optional enchantR1 = resourceManager.getResource(possibleEnchantIdentifier);
        if (enchantR1.isEmpty()) {
            return;
        }
        String enchantPackName = ((Resource)enchantR1.get()).sourcePackId();
        if (!enchantPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantPackName, vanillaR1.get().sourcePackId()))) {
            return;
        }
        this.enchantIdentifier = possibleEnchantIdentifier;
        ResourceLocation possibleEnchantBlinkIdentifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink" + enchantSuffix + ".png");
        Optional enchantBlinkR1 = resourceManager.getResource(possibleEnchantBlinkIdentifier);
        if (enchantBlinkR1.isEmpty()) {
            return;
        }
        String enchantBlinkPackName = ((Resource)enchantBlinkR1.get()).sourcePackId();
        if (!enchantBlinkPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantBlinkPackName, vanillaR1.get().sourcePackId()))) {
            return;
        }
        this.enchantBlinkIdentifier = possibleEnchantBlinkIdentifier;
        ResourceLocation possibleEnchantBlink2Identifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink2" + enchantSuffix + ".png");
        Optional enchantBlink2R1 = resourceManager.getResource(possibleEnchantBlink2Identifier);
        if (enchantBlink2R1.isEmpty()) {
            return;
        }
        String enchantBlink2PackName = ((Resource)enchantBlink2R1.get()).sourcePackId();
        if (enchantBlink2PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantBlink2PackName, vanillaR1.get().sourcePackId()))) {
            this.enchantBlink2Identifier = possibleEnchantBlink2Identifier;
        }
    }

    private Optional<Resource> getResourceOrModifyForTrims(ResourceManager resourceManager) {
        Optional<Resource> vanillaR1 = resourceManager.getResource(this.thisIdentifier);
        if (vanillaR1.isEmpty() && (this.thisIdentifier.getPath().contains("textures/trims/models/armor/") || this.thisIdentifier.getPath().contains("textures/trims/entity/"))) {
            vanillaR1 = resourceManager.getResource(ETFUtils2.res(this.thisIdentifier.getNamespace(), this.thisIdentifier.getPath().replaceAll("_(.*?)(?=\\.png)", "")));
            PackResources pack = vanillaR1.map(Resource::source).orElseGet(() -> Minecraft.getInstance().getVanillaPackResources());
            vanillaR1 = Optional.of(new Resource(pack, null));
        }
        return vanillaR1;
    }

    @NotNull
    public ResourceLocation getTextureIdentifier(@Nullable ETFEntityRenderState entity) {
        this.currentTextureState = this.canPatch() ? TextureReturnState.NORMAL_PATCHED : TextureReturnState.NORMAL;
        return this.getBlinkingIdentifier(entity);
    }

    @NotNull
    private ResourceLocation getBlinkingIdentifier(@Nullable ETFEntityRenderState state) {
        if (!this.doesBlink() || state == null || !(state.entity() instanceof LivingEntity)) {
            return this.identifierOfCurrentState();
        }
        ETFEntity entity = state.entity();
        if (this.guiBlink) {
            this.setBlink(Math.abs((int)System.currentTimeMillis() / 20 % 50000), 0);
        } else if (((LivingEntity)entity).hasPose(Pose.SLEEPING)) {
            this.modifyTextureState(TextureReturnState.APPLY_BLINK);
        } else if (((LivingEntity)entity).hasEffect(MobEffects.BLINDNESS)) {
            this.modifyTextureState(this.doesBlink2() ? TextureReturnState.APPLY_BLINK2 : TextureReturnState.APPLY_BLINK);
        } else {
            this.setBlink(((LivingEntity)entity).tickCount, Math.abs(state.uuid().hashCode()));
        }
        return this.identifierOfCurrentState();
    }

    private void setBlink(int currentTime, int hash) {
        int uuidHash = hash % (this.blinkFrequency * 2) + 20 + this.blinkFrequency;
        int timeModulated = Math.abs(currentTime % uuidHash);
        if (timeModulated <= this.blinkLength + this.blinkLength) {
            if (this.doesBlink2()) {
                if ((double)timeModulated >= (double)this.blinkLength.intValue() / 1.5 && timeModulated <= this.blinkLength + 1 + this.blinkLength / 3) {
                    this.modifyTextureState(TextureReturnState.APPLY_BLINK);
                } else {
                    this.modifyTextureState(TextureReturnState.APPLY_BLINK2);
                }
            } else {
                this.modifyTextureState(TextureReturnState.APPLY_BLINK);
            }
        }
    }

    public void setGUIBlink() {
        this.blinkFrequency = 100;
        this.blinkLength = 40;
        this.guiBlink = true;
    }

    public boolean isEmissive() {
        return this.emissiveIdentifier != null;
    }

    public boolean isEnchanted() {
        return this.enchantIdentifier != null;
    }

    public boolean canPatch() {
        return ETFRenderContext.isAllowedToPatch() && this.thisIdentifier_Patched != null;
    }

    public boolean doesBlink() {
        return this.blinkIdentifier != null;
    }

    @NotNull
    public ETFSprite getPaintingSprite(@NotNull TextureAtlasSprite originalSprite, @Nullable ResourceLocation originalID) {
        if (this.atlasSprite == null) {
            this.atlasSprite = new ETFSprite(originalSprite, this, this.thisIdentifier.equals((Object)originalID));
        }
        return this.atlasSprite;
    }

    public boolean doesBlink2() {
        return this.blink2Identifier != null;
    }

    public String toString() {
        return "[" + this.thisIdentifier.toString() + ", emissive=" + this.isEmissive() + ", blinks=" + this.doesBlink() + "]";
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, ModelPart modelPart) {
        this.renderEmissive(matrixStack, vertexConsumerProvider, modelPart, ETFManager.getEmissiveMode());
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, ModelPart modelPart, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        VertexConsumer vertexC = this.getEmissiveVertexConsumer(vertexConsumerProvider, null, modeToUsePossiblyManuallyChosen);
        if (vertexC != null) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
            modelPart.render(matrixStack, vertexC, 0xF000F2, OverlayTexture.NO_OVERLAY);
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Model model) {
        this.renderEmissive(matrixStack, vertexConsumerProvider, model, ETFManager.getEmissiveMode());
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        VertexConsumer vertexC = this.getEmissiveVertexConsumer(vertexConsumerProvider, model, modeToUsePossiblyManuallyChosen);
        if (vertexC != null) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
            model.renderToBuffer(matrixStack, vertexC, 0xF000F2, OverlayTexture.NO_OVERLAY);
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    @Nullable
    public VertexConsumer getEmissiveVertexConsumer(MultiBufferSource vertexConsumerProvider, @Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        RenderType type = this.getEmissiveRenderLayer(model, modeToUsePossiblyManuallyChosen);
        if (type == null) {
            return null;
        }
        return vertexConsumerProvider.getBuffer(type);
    }

    @Nullable
    public RenderType getEmissiveRenderLayer(@Nullable Model model) {
        return this.getEmissiveRenderLayer(model, ETFManager.getEmissiveMode());
    }

    @Nullable
    public RenderType getEmissiveRenderLayer(@Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        ETFRenderContext.preventRenderLayerTextureModify();
        RenderType type = this.getEmissiveVertexConsumerWrapped(model, modeToUsePossiblyManuallyChosen);
        ETFRenderContext.allowRenderLayerTextureModify();
        return type;
    }

    @Nullable
    private RenderType getEmissiveVertexConsumerWrapped(@Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        ResourceLocation emissiveToUse;
        if (this.isEmissive() && (emissiveToUse = this.getEmissiveIdentifierOfCurrentState()) != null) {
            if (modeToUsePossiblyManuallyChosen == ETFConfig.EmissiveRenderModes.BRIGHT) {
                return RenderType.beaconBeam((ResourceLocation)emissiveToUse, (boolean)true);
            }
            if (model == null) {
                return RenderType.entityCutoutNoCull((ResourceLocation)emissiveToUse);
            }
            return model.renderType(emissiveToUse);
        }
        return null;
    }

    private void modifyTextureState(TextureReturnState givenState) {
        switch (givenState.ordinal()) {
            case 7: {
                this.currentTextureState = this.currentTextureState == TextureReturnState.NORMAL_PATCHED ? TextureReturnState.BLINK_PATCHED : TextureReturnState.BLINK;
                break;
            }
            case 8: {
                this.currentTextureState = switch (this.currentTextureState.ordinal()) {
                    case 1, 3 -> TextureReturnState.BLINK2_PATCHED;
                    default -> TextureReturnState.BLINK2;
                };
            }
        }
    }

    @NotNull
    private ResourceLocation identifierOfCurrentState() {
        return switch (this.currentTextureState.ordinal()) {
            case 0 -> this.thisIdentifier;
            case 1 -> this.thisIdentifier_Patched;
            case 2 -> this.blinkIdentifier;
            case 3 -> this.blinkIdentifier_Patched;
            case 4 -> this.blink2Identifier;
            case 5 -> this.blink2Identifier_Patched;
            default -> this.thisIdentifier;
        };
    }

    @Nullable
    public ResourceLocation getEmissiveIdentifierOfCurrentState() {
        return switch (this.currentTextureState.ordinal()) {
            case 0, 1 -> this.emissiveIdentifier;
            case 2, 3 -> this.emissiveBlinkIdentifier;
            case 4, 5 -> this.emissiveBlink2Identifier;
            default -> this.emissiveIdentifier;
        };
    }

    @Nullable
    public ResourceLocation getEnchantIdentifierOfCurrentState() {
        return switch (this.currentTextureState.ordinal()) {
            case 0, 1 -> this.enchantIdentifier;
            case 2, 3 -> this.enchantBlinkIdentifier;
            case 4, 5 -> this.enchantBlink2Identifier;
            default -> this.enchantIdentifier;
        };
    }

    public void assertPatchedTextures() {
        if (!this.isEmissive() || this.hasPatched) {
            return;
        }
        this.hasPatched = true;
        ResourceManager files = Minecraft.getInstance().getResourceManager();
        if ((ETF.isThisModLoaded("iris") || ETF.isThisModLoaded("oculus")) && (files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_s.png")).isPresent() || files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_n.png")).isPresent())) {
            return;
        }
        if (ETF.isThisModLoaded("animatica") && (ETFTexture.doesAnimaticaVersionExist(this.thisIdentifier) || ETFTexture.doesAnimaticaVersionExist(this.emissiveIdentifier)) || ETF.isThisModLoaded("moremcmeta") && (files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", ".png.mcmeta")).isPresent() || files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", ".png.moremcmeta")).isPresent())) {
            return;
        }
        NativeImage newBaseTexture = ETFUtils2.getNativeImageElseNull(this.thisIdentifier);
        NativeImage newBlinkTexture = ETFUtils2.getNativeImageElseNull(this.blinkIdentifier);
        NativeImage newBlink2Texture = ETFUtils2.getNativeImageElseNull(this.blink2Identifier);
        boolean didPatch = false;
        if (this.emissiveIdentifier != null) {
            NativeImage emissiveImage = ETFUtils2.getNativeImageElseNull(this.emissiveIdentifier);
            try {
                ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(newBaseTexture, emissiveImage);
                didPatch = true;
                if (this.doesBlink() && this.emissiveBlinkIdentifier != null) {
                    NativeImage emissiveBlinkImage = ETFUtils2.getNativeImageElseNull(this.emissiveBlinkIdentifier);
                    ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(newBlinkTexture, emissiveBlinkImage);
                    if (this.doesBlink2() && this.emissiveBlink2Identifier != null) {
                        NativeImage emissiveBlink2Image = ETFUtils2.getNativeImageElseNull(this.emissiveBlink2Identifier);
                        ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(newBlink2Texture, emissiveBlink2Image);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (didPatch) {
                this.thisIdentifier_Patched = ETFUtils2.res(PATCH_NAMESPACE_PREFIX + this.thisIdentifier.getNamespace(), this.thisIdentifier.getPath());
                ETFUtils2.registerNativeImageToIdentifier(newBaseTexture, this.thisIdentifier_Patched);
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.thisIdentifier_Patched, this);
                if (this.doesBlink()) {
                    this.blinkIdentifier_Patched = ETFUtils2.res(PATCH_NAMESPACE_PREFIX + this.blinkIdentifier.getNamespace(), this.blinkIdentifier.getPath());
                    ETFUtils2.registerNativeImageToIdentifier(newBlinkTexture, this.blinkIdentifier_Patched);
                    ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blinkIdentifier_Patched, this);
                    if (this.doesBlink2()) {
                        this.blink2Identifier_Patched = ETFUtils2.res(PATCH_NAMESPACE_PREFIX + this.blink2Identifier.getNamespace(), this.blink2Identifier.getPath());
                        ETFUtils2.registerNativeImageToIdentifier(newBlink2Texture, this.blink2Identifier_Patched);
                        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blink2Identifier_Patched, this);
                    }
                }
            }
        }
    }

    public static enum TextureReturnState {
        NORMAL,
        NORMAL_PATCHED,
        BLINK,
        BLINK_PATCHED,
        BLINK2,
        BLINK2_PATCHED,
        APPLY_PATCH,
        APPLY_BLINK,
        APPLY_BLINK2;


        public String toString() {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> "normal";
                case 2 -> "blink";
                case 4 -> "blink2";
                case 1 -> "normal_patched";
                case 3 -> "blink_patched";
                case 5 -> "blink2_patched";
                case 7 -> "apply_blink";
                case 8 -> "apply_blink2";
                case 6 -> "apply_patch";
            };
        }
    }
}

