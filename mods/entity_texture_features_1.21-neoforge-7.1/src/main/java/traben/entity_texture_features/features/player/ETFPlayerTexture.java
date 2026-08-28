/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.HttpTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.player;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFException;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFPlayerTexture {
    public static final String SKIN_NAMESPACE = "etf_skin";
    public static NativeImage clientPlayerOriginalSkinImageForTool = null;
    public static boolean remappingETFSkin = false;
    public ResourceLocation baseEnchantIdentifier = null;
    public ResourceLocation baseEnchantBlinkIdentifier = null;
    public ResourceLocation baseEnchantBlink2Identifier = null;
    public ResourceLocation texturedNoseIdentifier = null;
    public ResourceLocation texturedNoseIdentifierEmissive = null;
    public ResourceLocation texturedNoseIdentifierEnchanted = null;
    public boolean hasVillagerNose = false;
    public boolean hasFeatures = false;
    public int coatStyle = 0;
    public int coatLength = 1;
    public int blinkType = 0;
    public int blinkHeight = 1;
    public boolean hasEmissives = false;
    public boolean hasEnchant = false;
    public ETFTexture etfTextureOfFinalBaseSkin;
    public ETFConfigScreenSkinTool.NoseType noseType = ETFConfigScreenSkinTool.NoseType.NONE;
    public ETFPlayerEntity player;
    public boolean wasForcedSolid = false;
    ResourceLocation coatIdentifier = null;
    ResourceLocation coatEmissiveIdentifier = null;
    ResourceLocation coatEnchantedIdentifier = null;
    boolean hasFatCoat = false;
    private boolean isTextureReady = false;
    private NativeImage originalSkin;
    private ResourceLocation normalVanillaSkinIdentifier = null;
    private static final ETFException TRY_AGAIN_LATER = new ETFException("try again later");
    public boolean shouldRetryOnFail = false;

    public ResourceLocation getOriginal() {
        return this.normalVanillaSkinIdentifier;
    }

    public ETFPlayerTexture(ETFPlayerEntity player, ResourceLocation rendererGivenSkin) {
        this.player = player;
        this.normalVanillaSkinIdentifier = rendererGivenSkin;
        if (player instanceof Player) {
            this.checkTexture(false);
        } else {
            try {
                HttpTexture skin = (HttpTexture)Minecraft.getInstance().getSkinManager().skinTextures.textureManager.getTexture(rendererGivenSkin, null);
                assert (skin.file != null);
                FileInputStream fileInputStream = new FileInputStream(skin.file);
                NativeImage vanilla = NativeImage.read((InputStream)fileInputStream);
                fileInputStream.close();
                this.originalSkin = ETFUtils2.emptyNativeImage(64, 64);
                this.originalSkin.copyFrom(vanilla);
                vanilla.close();
                this.checkTexture(true);
            }
            catch (Exception e) {
                this.skinFailed("player head block failure");
            }
        }
    }

    private ETFPlayerTexture(ResourceLocation rendererGivenSkin, boolean shouldRetryOnFail) {
        this.player = null;
        this.shouldRetryOnFail = shouldRetryOnFail;
        this.normalVanillaSkinIdentifier = rendererGivenSkin;
    }

    public ETFPlayerTexture() {
        this.player = (ETFPlayerEntity)Minecraft.getInstance().player;
        assert (this.player != null);
        if (this.player.etf$getEntity() != null) {
            assert (Minecraft.getInstance().player != null);
            NativeImage skin = ETFUtils2.getNativeImageElseNull(Minecraft.getInstance().player.getSkin().texture());
            if (skin != null) {
                clientPlayerOriginalSkinImageForTool = skin;
                this.changeSkinToThisForTool(skin);
                return;
            }
        }
        ETFUtils2.logError("ETFPlayerTexture went wrong");
    }

    @Nullable
    private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck, boolean invertMatch) {
        return ETFPlayerTexture.returnMatchPixels(baseSkin, boundsToCheck, null, invertMatch);
    }

    @Nullable
    private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck, @Nullable NativeImage second, boolean invertMatch) {
        NativeImage texture;
        if (baseSkin == null || boundsToCheck == null) {
            return null;
        }
        boolean hasSecondImageToBeUsedAsBase = second != null;
        HashSet<Integer> matchColors = new HashSet<Integer>();
        for (int x = boundsToCheck[0]; x <= boundsToCheck[2]; ++x) {
            for (int y = boundsToCheck[1]; y <= boundsToCheck[3]; ++y) {
                if (baseSkin.getLuminanceOrAlpha(x, y) == 0) continue;
                matchColors.add(ETFUtils2.getPixel(baseSkin, x, y));
            }
        }
        if (matchColors.isEmpty()) {
            return null;
        }
        if (!hasSecondImageToBeUsedAsBase) {
            texture = new NativeImage(baseSkin.getWidth(), baseSkin.getHeight(), false);
            texture.copyFrom(baseSkin);
        } else {
            texture = new NativeImage(second.getWidth(), second.getHeight(), false);
            texture.copyFrom(second);
        }
        for (int x = 0; x < texture.getWidth(); ++x) {
            for (int y = 0; y < texture.getHeight(); ++y) {
                if (invertMatch) {
                    if (!matchColors.contains(ETFUtils2.getPixel(texture, x, y))) continue;
                    ETFUtils2.setPixel(texture, x, y, 0);
                    continue;
                }
                if (matchColors.contains(ETFUtils2.getPixel(texture, x, y))) continue;
                ETFUtils2.setPixel(texture, x, y, 0);
            }
        }
        return ETFPlayerTexture.returnNullIfEmptyImage(texture);
    }

    @Nullable
    private static NativeImage returnNullIfEmptyImage(NativeImage imageToCheck) {
        boolean foundAPixel = false;
        block0: for (int x = 0; x < imageToCheck.getWidth(); ++x) {
            for (int y = 0; y < imageToCheck.getHeight(); ++y) {
                if (ETFUtils2.getPixel(imageToCheck, x, y) == 0) continue;
                foundAPixel = true;
                break block0;
            }
        }
        return foundAPixel ? imageToCheck : null;
    }

    private static int[] getSkinPixelBounds(String choiceKey) {
        int[] nArray;
        switch (choiceKey) {
            case "marker1": {
                int[] nArray2 = new int[4];
                nArray2[0] = 56;
                nArray2[1] = 16;
                nArray2[2] = 63;
                nArray = nArray2;
                nArray2[3] = 23;
                break;
            }
            case "marker2": {
                int[] nArray3 = new int[4];
                nArray3[0] = 56;
                nArray3[1] = 24;
                nArray3[2] = 63;
                nArray = nArray3;
                nArray3[3] = 31;
                break;
            }
            case "marker3": {
                int[] nArray4 = new int[4];
                nArray4[0] = 56;
                nArray4[1] = 32;
                nArray4[2] = 63;
                nArray = nArray4;
                nArray4[3] = 39;
                break;
            }
            case "marker4": {
                int[] nArray5 = new int[4];
                nArray5[0] = 56;
                nArray5[1] = 40;
                nArray5[2] = 63;
                nArray = nArray5;
                nArray5[3] = 47;
                break;
            }
            case "optimizedEyeSmall": {
                int[] nArray6 = new int[4];
                nArray6[0] = 12;
                nArray6[1] = 16;
                nArray6[2] = 19;
                nArray = nArray6;
                nArray6[3] = 16;
                break;
            }
            case "optimizedEye2High": {
                int[] nArray7 = new int[4];
                nArray7[0] = 12;
                nArray7[1] = 16;
                nArray7[2] = 19;
                nArray = nArray7;
                nArray7[3] = 17;
                break;
            }
            case "optimizedEye2High_second": {
                int[] nArray8 = new int[4];
                nArray8[0] = 12;
                nArray8[1] = 18;
                nArray8[2] = 19;
                nArray = nArray8;
                nArray8[3] = 19;
                break;
            }
            case "optimizedEye4High": {
                int[] nArray9 = new int[4];
                nArray9[0] = 12;
                nArray9[1] = 16;
                nArray9[2] = 19;
                nArray = nArray9;
                nArray9[3] = 19;
                break;
            }
            case "optimizedEye4High_second": {
                int[] nArray10 = new int[4];
                nArray10[0] = 36;
                nArray10[1] = 16;
                nArray10[2] = 43;
                nArray = nArray10;
                nArray10[3] = 19;
                break;
            }
            case "face1": {
                int[] nArray11 = new int[4];
                nArray11[0] = 0;
                nArray11[1] = 0;
                nArray11[2] = 7;
                nArray = nArray11;
                nArray11[3] = 7;
                break;
            }
            case "face2": {
                int[] nArray12 = new int[4];
                nArray12[0] = 24;
                nArray12[1] = 0;
                nArray12[2] = 31;
                nArray = nArray12;
                nArray12[3] = 7;
                break;
            }
            case "face3": {
                int[] nArray13 = new int[4];
                nArray13[0] = 32;
                nArray13[1] = 0;
                nArray13[2] = 39;
                nArray = nArray13;
                nArray13[3] = 7;
                break;
            }
            case "face4": {
                int[] nArray14 = new int[4];
                nArray14[0] = 56;
                nArray14[1] = 0;
                nArray14[2] = 63;
                nArray = nArray14;
                nArray14[3] = 7;
                break;
            }
            case "cape1": {
                int[] nArray15 = new int[4];
                nArray15[0] = 12;
                nArray15[1] = 32;
                nArray15[2] = 19;
                nArray = nArray15;
                nArray15[3] = 35;
                break;
            }
            case "cape2": {
                int[] nArray16 = new int[4];
                nArray16[0] = 36;
                nArray16[1] = 32;
                nArray16[2] = 43;
                nArray = nArray16;
                nArray16[3] = 35;
                break;
            }
            case "cape3": {
                int[] nArray17 = new int[4];
                nArray17[0] = 12;
                nArray17[1] = 48;
                nArray17[2] = 19;
                nArray = nArray17;
                nArray17[3] = 51;
                break;
            }
            case "cape4": {
                int[] nArray18 = new int[4];
                nArray18[0] = 28;
                nArray18[1] = 48;
                nArray18[2] = 35;
                nArray = nArray18;
                nArray18[3] = 51;
                break;
            }
            case "cape5": {
                int[] nArray19 = new int[4];
                nArray19[0] = 44;
                nArray19[1] = 48;
                nArray19[2] = 51;
                nArray = nArray19;
                nArray19[3] = 51;
                break;
            }
            case "cape5.1": {
                int[] nArray20 = new int[4];
                nArray20[0] = 44;
                nArray20[1] = 48;
                nArray20[2] = 45;
                nArray = nArray20;
                nArray20[3] = 51;
                break;
            }
            case "cape5.2": {
                int[] nArray21 = new int[4];
                nArray21[0] = 46;
                nArray21[1] = 48;
                nArray21[2] = 47;
                nArray = nArray21;
                nArray21[3] = 51;
                break;
            }
            case "cape5.3": {
                int[] nArray22 = new int[4];
                nArray22[0] = 48;
                nArray22[1] = 48;
                nArray22[2] = 49;
                nArray = nArray22;
                nArray22[3] = 51;
                break;
            }
            case "cape5.4": {
                int[] nArray23 = new int[4];
                nArray23[0] = 50;
                nArray23[1] = 48;
                nArray23[2] = 51;
                nArray = nArray23;
                nArray23[3] = 51;
                break;
            }
            case "capeVertL": {
                int[] nArray24 = new int[4];
                nArray24[0] = 1;
                nArray24[1] = 1;
                nArray24[2] = 1;
                nArray = nArray24;
                nArray24[3] = 16;
                break;
            }
            case "capeVertR": {
                int[] nArray25 = new int[4];
                nArray25[0] = 10;
                nArray25[1] = 1;
                nArray25[2] = 10;
                nArray = nArray25;
                nArray25[3] = 16;
                break;
            }
            case "capeHorizL": {
                int[] nArray26 = new int[4];
                nArray26[0] = 1;
                nArray26[1] = 1;
                nArray26[2] = 10;
                nArray = nArray26;
                nArray26[3] = 1;
                break;
            }
            case "capeHorizR": {
                int[] nArray27 = new int[4];
                nArray27[0] = 1;
                nArray27[1] = 16;
                nArray27[2] = 10;
                nArray = nArray27;
                nArray27[3] = 16;
                break;
            }
            default: {
                int[] nArray28 = new int[4];
                nArray28[0] = 0;
                nArray28[1] = 0;
                nArray28[2] = 0;
                nArray = nArray28;
                nArray28[3] = 0;
            }
        }
        return nArray;
    }

    private static NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown) {
        return ETFPlayerTexture.returnOptimizedBlinkFace(baseSkin, eyeBounds, eyeHeightFromTopDown, null);
    }

    private static NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown, int[] secondLayerBounds) {
        NativeImage texture = new NativeImage(64, 64, false);
        texture.copyFrom(baseSkin);
        ETFPlayerTexture.copyToPixels(baseSkin, texture, eyeBounds, 8, 8 + (eyeHeightFromTopDown - 1));
        if (secondLayerBounds != null) {
            ETFPlayerTexture.copyToPixels(baseSkin, texture, secondLayerBounds, 40, 8 + (eyeHeightFromTopDown - 1));
        }
        return texture;
    }

    private static void forceSolidLowerSkin(NativeImage skin) {
        try {
            ETFPlayerTexture.stripAlphaInclusive(skin, 8, 0, 23, 15);
            ETFPlayerTexture.stripAlphaInclusive(skin, 0, 20, 55, 31);
            ETFPlayerTexture.stripAlphaInclusive(skin, 0, 8, 7, 15);
            ETFPlayerTexture.stripAlphaInclusive(skin, 24, 8, 31, 15);
            ETFPlayerTexture.stripAlphaInclusive(skin, 0, 16, 11, 19);
            ETFPlayerTexture.stripAlphaInclusive(skin, 20, 16, 35, 19);
            ETFPlayerTexture.stripAlphaInclusive(skin, 44, 16, 51, 19);
            ETFPlayerTexture.stripAlphaInclusive(skin, 20, 48, 27, 51);
            ETFPlayerTexture.stripAlphaInclusive(skin, 36, 48, 43, 51);
            ETFPlayerTexture.stripAlphaInclusive(skin, 16, 52, 47, 63);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static NativeImage getCoatTexture(NativeImage skin, int lengthOfCoat, boolean ignoreTopTexture) {
        NativeImage coat = new NativeImage(64, 64, false);
        coat.fillRect(0, 0, 64, 64, 0);
        if (!ignoreTopTexture) {
            ETFPlayerTexture.copyToPixels(skin, coat, 4, 32, 7, 35 + lengthOfCoat, 20, 32);
            ETFPlayerTexture.copyToPixels(skin, coat, 4, 48, 7, 51 + lengthOfCoat, 24, 32);
        }
        ETFPlayerTexture.copyToPixels(skin, coat, 0, 36, 7, 36 + lengthOfCoat, 16, 36);
        ETFPlayerTexture.copyToPixels(skin, coat, 12, 36, 15, 36 + lengthOfCoat, 36, 36);
        ETFPlayerTexture.copyToPixels(skin, coat, 4, 52, 15, 52 + lengthOfCoat, 24, 36);
        return coat;
    }

    private static void copyToPixels(NativeImage source, NativeImage dest, int[] bounds, int copyToX, int CopyToY) {
        ETFPlayerTexture.copyToPixels(source, dest, bounds[0], bounds[1], bounds[2], bounds[3], copyToX, CopyToY);
    }

    private static void copyToPixels(NativeImage source, NativeImage dest, int x1, int y1, int x2, int y2, int copyToX, int copyToY) {
        int copyToXRelative = copyToX - x1;
        int copyToYRelative = copyToY - y1;
        for (int x = x1; x <= x2; ++x) {
            for (int y = y1; y <= y2; ++y) {
                ETFUtils2.setPixel(dest, x + copyToXRelative, y + copyToYRelative, ETFUtils2.getPixel(source, x, y));
            }
        }
    }

    private static void deletePixels(NativeImage source, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; ++x) {
            for (int y = y1; y <= y2; ++y) {
                ETFUtils2.setPixel(source, x, y, 0);
            }
        }
    }

    public static int getSkinPixelColourToNumber(int color) {
        return switch (color) {
            case -65281 -> 1;
            case -256 -> 2;
            case -16776961 -> 3;
            case -16711936 -> 4;
            case -16760705 -> 5;
            case -65536 -> 6;
            case -16744449 -> 7;
            case -14483457 -> 8;
            case -12362096 -> 666;
            default -> color;
        };
    }

    public static int getSkinNumberToPixelColour(int color) {
        return switch (color) {
            case 1 -> -65281;
            case 2 -> -256;
            case 3 -> -16776961;
            case 4 -> -16711936;
            case 5 -> -16760705;
            case 6 -> -65536;
            case 7 -> -16744449;
            case 8 -> -14483457;
            case 666 -> -12362096;
            default -> color;
        };
    }

    private static void stripAlphaInclusive(NativeImage image, int x1, int y1, int x2, int y2) {
        for (int i = x1; i <= x2; ++i) {
            for (int j = y1; j <= y2; ++j) {
                ETFUtils2.setPixel(image, i, j, ETFUtils2.getPixel(image, i, j) | 0xFF000000);
            }
        }
    }

    public boolean isCorrectObjectForThisSkin(ResourceLocation check) {
        return check.equals((Object)this.normalVanillaSkinIdentifier);
    }

    @Nullable
    public ResourceLocation getBaseTextureIdentifierOrNullForVanilla(Player player) {
        return this.getBaseTextureIdentifierOrNullForVanilla(ETFEntityRenderState.forEntity((ETFPlayerEntity)player));
    }

    @Nullable
    public ResourceLocation getBaseTextureIdentifierOrNullForVanilla(ETFEntityRenderState player) {
        this.player = (ETFPlayerEntity)player.entity();
        if (this.etfTextureOfFinalBaseSkin != null && this.canUseFeaturesForThisPlayer()) {
            return this.etfTextureOfFinalBaseSkin.getTextureIdentifier(player);
        }
        return null;
    }

    @Nullable
    public ResourceLocation getBaseHeadTextureIdentifierOrNullForVanilla() {
        if (this.etfTextureOfFinalBaseSkin != null && this.canUseFeaturesForThisPlayer()) {
            return this.etfTextureOfFinalBaseSkin.getTextureIdentifier(null);
        }
        return null;
    }

    @Nullable
    public ResourceLocation getBaseTextureEmissiveIdentifierOrNullForNone() {
        if (this.hasEmissives && this.canUseFeaturesForThisPlayer() && this.etfTextureOfFinalBaseSkin != null) {
            return this.etfTextureOfFinalBaseSkin.getEmissiveIdentifierOfCurrentState();
        }
        return null;
    }

    public boolean canUseFeaturesForThisPlayer() {
        return this.isTextureReady && this.hasFeatures && (ETF.config().getConfig().enableEnemyTeamPlayersSkinFeatures || this.player.etf$isTeammate((Player)Minecraft.getInstance().player) || this.player.etf$getScoreboardTeam() == null);
    }

    private void skinFailed(String reason) {
        this.skinFailed(reason, false);
    }

    private void skinFailed(@Nullable String reason, boolean retryLater) {
        if (!(Minecraft.getInstance().screen instanceof ETFConfigScreenSkinTool)) {
            ETFManager.getInstance().PLAYER_TEXTURE_MAP.put(this.player.etf$getUuid(), new ETFPlayerTexture(this.normalVanillaSkinIdentifier, retryLater));
        } else if (reason != null) {
            ETFUtils2.logError("something went wrong applying skin in tool, or skin features are not added: " + reason);
        }
    }

    public void checkTexture(boolean skipSkinLoad) {
        if (!skipSkinLoad) {
            try {
                HttpTexture skin = (HttpTexture)Minecraft.getInstance().getSkinManager().skinTextures.textureManager.getTexture(this.normalVanillaSkinIdentifier, null);
                assert (skin.file != null);
                FileInputStream fileInputStream = new FileInputStream(skin.file);
                NativeImage img = NativeImage.read((InputStream)fileInputStream);
                remappingETFSkin = true;
                this.originalSkin = skin.processLegacySkin(img);
                remappingETFSkin = false;
                fileInputStream.close();
                if (Minecraft.getInstance().player != null && this.player.etf$getUuid().equals(Minecraft.getInstance().player.getUUID())) {
                    clientPlayerOriginalSkinImageForTool = this.originalSkin;
                }
            }
            catch (ETFException e) {
                if (e == TRY_AGAIN_LATER) {
                    this.skinFailed(null, true);
                } else {
                    this.skinFailed("skin pre load failure: " + e.getMessage());
                }
                return;
            }
            catch (Exception e) {
                this.skinFailed("skin pre load failure: " + e.getMessage());
                return;
            }
        }
        UUID id = this.player.etf$getUuid();
        NativeImage modifiedSkin = ETFUtils2.emptyNativeImage(this.originalSkin.getWidth(), this.originalSkin.getHeight());
        modifiedSkin.copyFrom(this.originalSkin);
        if (this.originalSkin != null) {
            if (ETFUtils2.getPixel(this.originalSkin, 1, 16) == -16776961 && ETFUtils2.getPixel(this.originalSkin, 0, 16) == -16777089 && ETFUtils2.getPixel(this.originalSkin, 0, 17) == -16776961 && ETFUtils2.getPixel(this.originalSkin, 2, 16) == -16711936 && ETFUtils2.getPixel(this.originalSkin, 3, 16) == -16744704 && ETFUtils2.getPixel(this.originalSkin, 3, 17) == -16711936 && ETFUtils2.getPixel(this.originalSkin, 0, 18) == -65536 && ETFUtils2.getPixel(this.originalSkin, 0, 19) == -8454144 && ETFUtils2.getPixel(this.originalSkin, 1, 19) == -65536 && ETFUtils2.getPixel(this.originalSkin, 3, 18) == -1 && ETFUtils2.getPixel(this.originalSkin, 2, 19) == -1 && ETFUtils2.getPixel(this.originalSkin, 3, 18) == -1) {
                int[] boxChosenBounds;
                int blinkChoice;
                this.hasFeatures = true;
                ETFUtils2.logMessage("Found Player {" + this.player.etf$getName().getString() + "} with ETF texture features in skin.", false);
                int[] choiceBoxChoices = new int[]{ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 16)), ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 17)), ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 18)), ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 19)), ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 53, 16)), ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 53, 17)), ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 53, 18))};
                if (choiceBoxChoices[2] < 1 || choiceBoxChoices[2] > 8) {
                    choiceBoxChoices[2] = 1;
                }
                boolean noseUpper = ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 43, 13)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 44, 13)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 43, 14)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 44, 14)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 43, 15)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 44, 15)) == 666;
                boolean noseLower = ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 11, 13)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 12, 13)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 11, 14)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 12, 14)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 11, 15)) == 666 && ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 12, 15)) == 666;
                this.hasVillagerNose = noseLower || noseUpper;
                boolean removeNosePixels = noseUpper;
                if (noseUpper) {
                    ETFPlayerTexture.deletePixels(modifiedSkin, 43, 13, 44, 15);
                }
                NativeImage noseTexture = null;
                int noseChoice = choiceBoxChoices[5];
                if (noseChoice >= 1 && noseChoice <= 9) {
                    if (noseChoice == 1 || noseChoice == 7 || noseChoice == 8 || noseChoice == 9) {
                        this.hasVillagerNose = true;
                        this.noseType = ETFConfigScreenSkinTool.NoseType.NONE.getByColorId(noseChoice);
                        if (noseChoice > 7) {
                            removeNosePixels = true;
                            ETFPlayerTexture.deletePixels(modifiedSkin, 43, 13, 44, 15);
                        }
                    } else {
                        int y;
                        int x;
                        noseTexture = ETFUtils2.emptyNativeImage(8, 8);
                        int[] bounds = switch (noseChoice) {
                            case 3 -> {
                                this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_2;
                                yield ETFPlayerTexture.getSkinPixelBounds("cape2");
                            }
                            case 4 -> {
                                this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_3;
                                yield ETFPlayerTexture.getSkinPixelBounds("cape3");
                            }
                            case 5 -> {
                                this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_4;
                                yield ETFPlayerTexture.getSkinPixelBounds("cape4");
                            }
                            case 6 -> {
                                this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_5;
                                yield ETFPlayerTexture.getSkinPixelBounds("cape5");
                            }
                            default -> {
                                this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_1;
                                yield ETFPlayerTexture.getSkinPixelBounds("cape1");
                            }
                        };
                        int noseY = 0;
                        for (x = bounds[0]; x <= bounds[2]; ++x) {
                            int noseX = 0;
                            for (int y2 = bounds[1]; y2 <= bounds[3]; ++y2) {
                                ETFUtils2.setPixel(noseTexture, noseX, noseY, ETFUtils2.getPixel(this.originalSkin, x, y2));
                                ++noseX;
                            }
                            ++noseY;
                        }
                        for (x = 4; x < 8; ++x) {
                            for (y = 0; y < 8; ++y) {
                                ETFUtils2.setPixel(noseTexture, x, y, ETFUtils2.getPixel(noseTexture, 7 - x, y));
                            }
                        }
                        for (x = 0; x < 8; ++x) {
                            for (y = 0; y < 4; ++y) {
                                int lowerColour = ETFUtils2.getPixel(noseTexture, x, y + 4);
                                ETFUtils2.setPixel(noseTexture, x, y + 4, ETFUtils2.getPixel(noseTexture, x, y));
                                ETFUtils2.setPixel(noseTexture, x, y, lowerColour);
                            }
                        }
                        this.texturedNoseIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_nose.png");
                        ETFUtils2.registerNativeImageToIdentifier(noseTexture, this.texturedNoseIdentifier);
                    }
                }
                NativeImage coatSkin = null;
                int controllerCoat = choiceBoxChoices[1];
                if (controllerCoat >= 1 && controllerCoat <= 8) {
                    this.coatStyle = controllerCoat;
                    int lengthOfCoat = choiceBoxChoices[2] - 1;
                    this.coatLength = lengthOfCoat + 1;
                    this.coatIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_coat.png");
                    coatSkin = ETFPlayerTexture.getCoatTexture(this.originalSkin, lengthOfCoat, controllerCoat >= 5);
                    ETFUtils2.registerNativeImageToIdentifier(coatSkin, this.coatIdentifier);
                    if (controllerCoat == 2 || controllerCoat == 4 || controllerCoat == 6 || controllerCoat == 8) {
                        ETFPlayerTexture.deletePixels(modifiedSkin, 4, 32, 7, 35);
                        ETFPlayerTexture.deletePixels(modifiedSkin, 4, 48, 7, 51);
                        ETFPlayerTexture.deletePixels(modifiedSkin, 0, 36, 15, 36 + lengthOfCoat);
                        ETFPlayerTexture.deletePixels(modifiedSkin, 0, 52, 15, 52 + lengthOfCoat);
                    }
                    this.hasFatCoat = controllerCoat == 3 || controllerCoat == 4 || controllerCoat == 7 || controllerCoat == 8;
                } else {
                    this.coatIdentifier = null;
                }
                boolean bl = this.wasForcedSolid = choiceBoxChoices[6] == 1;
                if (this.wasForcedSolid) {
                    ETFPlayerTexture.forceSolidLowerSkin(modifiedSkin);
                }
                NativeImage blinkSkinFile = null;
                NativeImage blinkSkinFile2 = null;
                ResourceLocation blinkIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink.png");
                ResourceLocation blink2Identifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink2.png");
                this.blinkType = blinkChoice = choiceBoxChoices[0];
                if (blinkChoice >= 1 && blinkChoice <= 5) {
                    if (blinkChoice <= 2) {
                        if (removeNosePixels) {
                            ETFPlayerTexture.deletePixels(modifiedSkin, 35, 5, 36, 7);
                        }
                        blinkSkinFile = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("face1"), 1, ETFPlayerTexture.getSkinPixelBounds("face3"));
                        ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                        if (blinkChoice == 2) {
                            if (removeNosePixels) {
                                ETFPlayerTexture.deletePixels(modifiedSkin, 59, 5, 60, 7);
                            }
                            blinkSkinFile2 = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("face2"), 1, ETFPlayerTexture.getSkinPixelBounds("face4"));
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                        }
                    } else {
                        int eyeHeightTopDown;
                        this.blinkHeight = eyeHeightTopDown = choiceBoxChoices[3];
                        if (eyeHeightTopDown > 8 || eyeHeightTopDown < 1) {
                            eyeHeightTopDown = 1;
                        }
                        if (blinkChoice == 3) {
                            blinkSkinFile = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("optimizedEyeSmall"), eyeHeightTopDown);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                        } else if (blinkChoice == 4) {
                            blinkSkinFile = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                            blinkSkinFile2 = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                        } else {
                            blinkSkinFile = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                            blinkSkinFile2 = ETFPlayerTexture.returnOptimizedBlinkFace(modifiedSkin, ETFPlayerTexture.getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                        }
                    }
                }
                if (blinkSkinFile == null) {
                    blinkIdentifier = null;
                }
                if (blinkSkinFile2 == null) {
                    blink2Identifier = null;
                }
                List<Integer> markerChoices = List.of(Integer.valueOf(ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 1, 17))), Integer.valueOf(ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 1, 18))), Integer.valueOf(ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 2, 17))), Integer.valueOf(ETFPlayerTexture.getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 2, 18))));
                NativeImage emissiveImage = null;
                NativeImage emissiveBlinkImage = null;
                NativeImage emissiveBlink2Image = null;
                ResourceLocation emissiveIdentifier = null;
                ResourceLocation blinkEmissiveIdentifier = null;
                ResourceLocation blink2EmissiveIdentifier = null;
                this.hasEmissives = markerChoices.contains(1);
                if (this.hasEmissives) {
                    boxChosenBounds = ETFPlayerTexture.getSkinPixelBounds("marker" + (markerChoices.indexOf(1) + 1));
                    emissiveImage = ETFPlayerTexture.returnMatchPixels(modifiedSkin, boxChosenBounds, false);
                    if (emissiveImage != null) {
                        NativeImage checkNose;
                        NativeImage checkCoat;
                        emissiveIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_e.png");
                        ETFUtils2.registerNativeImageToIdentifier(emissiveImage, emissiveIdentifier);
                        if (blinkSkinFile != null && (emissiveBlinkImage = ETFPlayerTexture.returnMatchPixels(blinkSkinFile, boxChosenBounds, false)) != null) {
                            blinkEmissiveIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink_e.png");
                            ETFUtils2.registerNativeImageToIdentifier(emissiveBlinkImage, blinkEmissiveIdentifier);
                        }
                        if (blinkSkinFile2 != null && (emissiveBlink2Image = ETFPlayerTexture.returnMatchPixels(blinkSkinFile2, boxChosenBounds, false)) != null) {
                            blink2EmissiveIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink2_e.png");
                            ETFUtils2.registerNativeImageToIdentifier(emissiveBlink2Image, blink2EmissiveIdentifier);
                        }
                        if (coatSkin != null && (checkCoat = ETFPlayerTexture.returnMatchPixels(modifiedSkin, boxChosenBounds, coatSkin, false)) != null) {
                            this.coatEmissiveIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_coat_e.png");
                            ETFUtils2.registerNativeImageToIdentifier(checkCoat, this.coatEmissiveIdentifier);
                        }
                        if (noseTexture != null && (checkNose = ETFPlayerTexture.returnMatchPixels(modifiedSkin, boxChosenBounds, noseTexture, false)) != null) {
                            this.texturedNoseIdentifierEmissive = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_nose_e.png");
                            ETFUtils2.registerNativeImageToIdentifier(checkNose, this.texturedNoseIdentifierEmissive);
                        }
                    } else {
                        this.hasEmissives = false;
                    }
                }
                this.hasEnchant = markerChoices.contains(2);
                if (this.hasEnchant) {
                    boxChosenBounds = ETFPlayerTexture.getSkinPixelBounds("marker" + (markerChoices.indexOf(2) + 1));
                    NativeImage check = ETFPlayerTexture.returnMatchPixels(modifiedSkin, boxChosenBounds, false);
                    if (check != null) {
                        NativeImage checkNose;
                        NativeImage checkCoat;
                        NativeImage checkBlink;
                        this.baseEnchantIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_enchant.png");
                        ETFUtils2.registerNativeImageToIdentifier(check, this.baseEnchantIdentifier);
                        if (blinkSkinFile != null && (checkBlink = ETFPlayerTexture.returnMatchPixels(blinkSkinFile, boxChosenBounds, false)) != null) {
                            this.baseEnchantBlinkIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink_enchant.png");
                            ETFUtils2.registerNativeImageToIdentifier(checkBlink, this.baseEnchantBlinkIdentifier);
                        }
                        if (blinkSkinFile2 != null && (checkBlink = ETFPlayerTexture.returnMatchPixels(blinkSkinFile2, boxChosenBounds, false)) != null) {
                            this.baseEnchantBlink2Identifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink2_enchant.png");
                            ETFUtils2.registerNativeImageToIdentifier(checkBlink, this.baseEnchantBlink2Identifier);
                        }
                        if (coatSkin != null && (checkCoat = ETFPlayerTexture.returnMatchPixels(modifiedSkin, boxChosenBounds, coatSkin, false)) != null) {
                            this.coatEnchantedIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_coat_enchant.png");
                            ETFUtils2.registerNativeImageToIdentifier(checkCoat, this.coatEnchantedIdentifier);
                        }
                        if (noseTexture != null && (checkNose = ETFPlayerTexture.returnMatchPixels(modifiedSkin, boxChosenBounds, noseTexture, false)) != null) {
                            this.texturedNoseIdentifierEnchanted = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_nose_enchant.png");
                            ETFUtils2.registerNativeImageToIdentifier(checkNose, this.texturedNoseIdentifierEnchanted);
                        }
                    } else {
                        this.hasEnchant = false;
                    }
                }
                ResourceLocation modifiedSkinBlinkPatchedIdentifier = null;
                ResourceLocation modifiedSkinPatchedIdentifier = null;
                ResourceLocation modifiedSkinBlink2PatchedIdentifier = null;
                if (this.hasEmissives && emissiveImage != null) {
                    modifiedSkinPatchedIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_e_patched.png");
                    ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(modifiedSkin, emissiveImage);
                    ETFUtils2.registerNativeImageToIdentifier(modifiedSkin, modifiedSkinPatchedIdentifier);
                    ETFManager.getInstance().ETF_TEXTURE_CACHE.put(modifiedSkinPatchedIdentifier, ETFTexture.manual(modifiedSkinPatchedIdentifier, emissiveIdentifier, this.baseEnchantIdentifier));
                    if (blinkSkinFile != null) {
                        modifiedSkinBlinkPatchedIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink_e_patched.png");
                        ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(blinkSkinFile, emissiveBlinkImage);
                        ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, modifiedSkinBlinkPatchedIdentifier);
                        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(modifiedSkinBlinkPatchedIdentifier, ETFTexture.manual(modifiedSkinBlinkPatchedIdentifier, blinkEmissiveIdentifier, this.baseEnchantBlinkIdentifier));
                    }
                    if (blinkSkinFile2 != null) {
                        modifiedSkinBlink2PatchedIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + "_blink2_e_patched.png");
                        ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(blinkSkinFile2, emissiveBlink2Image);
                        ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, modifiedSkinBlink2PatchedIdentifier);
                        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(modifiedSkinBlink2PatchedIdentifier, ETFTexture.manual(modifiedSkinBlink2PatchedIdentifier, blink2EmissiveIdentifier, this.baseEnchantBlink2Identifier));
                    }
                }
                ResourceLocation modifiedSkinIdentifier = ETFUtils2.res(SKIN_NAMESPACE, String.valueOf(id) + ".png");
                ETFUtils2.registerNativeImageToIdentifier(modifiedSkin, modifiedSkinIdentifier);
                this.etfTextureOfFinalBaseSkin = ETFTexture.manual(modifiedSkinIdentifier, blinkIdentifier, blink2Identifier, emissiveIdentifier, blinkEmissiveIdentifier, blink2EmissiveIdentifier, this.baseEnchantIdentifier, this.baseEnchantBlinkIdentifier, this.baseEnchantBlink2Identifier, modifiedSkinPatchedIdentifier, modifiedSkinBlinkPatchedIdentifier, modifiedSkinBlink2PatchedIdentifier);
                if (this.normalVanillaSkinIdentifier != null) {
                    ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.normalVanillaSkinIdentifier, this.etfTextureOfFinalBaseSkin);
                }
            } else {
                this.skinFailed("no marker");
            }
        } else {
            this.skinFailed("null skin");
        }
        this.isTextureReady = true;
    }

    public void changeSkinToThisForTool(NativeImage image) {
        if (this.player == null) {
            this.player = (ETFPlayerEntity)Minecraft.getInstance().player;
        }
        this.baseEnchantBlinkIdentifier = null;
        this.baseEnchantIdentifier = null;
        this.coatEmissiveIdentifier = null;
        this.coatEnchantedIdentifier = null;
        this.baseEnchantBlink2Identifier = null;
        this.etfTextureOfFinalBaseSkin = null;
        this.coatIdentifier = null;
        this.hasEmissives = false;
        this.hasEnchant = false;
        this.hasFatCoat = false;
        this.hasFeatures = false;
        this.hasVillagerNose = false;
        this.isTextureReady = false;
        this.coatStyle = 0;
        this.coatLength = 1;
        this.blinkHeight = 1;
        this.blinkType = 0;
        this.texturedNoseIdentifier = null;
        this.texturedNoseIdentifierEmissive = null;
        this.texturedNoseIdentifierEnchanted = null;
        this.noseType = ETFConfigScreenSkinTool.NoseType.NONE;
        this.originalSkin = image;
        this.checkTexture(true);
        if (this.etfTextureOfFinalBaseSkin != null) {
            this.etfTextureOfFinalBaseSkin.setGUIBlink();
        }
    }
}

