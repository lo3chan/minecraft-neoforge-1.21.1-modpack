package traben.entity_texture_features.features.player;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

            assert skin.file != null;

            FileInputStream fileInputStream = new FileInputStream(skin.file);
            NativeImage vanilla = NativeImage.read(fileInputStream);
            fileInputStream.close();
            this.originalSkin = ETFUtils2.emptyNativeImage(64, 64);
            this.originalSkin.copyFrom(vanilla);
            vanilla.close();
            this.checkTexture(true);
         } catch (Exception var6) {
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

      assert this.player != null;

      if (this.player.etf$getEntity() != null) {
         assert Minecraft.getInstance().player != null;

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
      return returnMatchPixels(baseSkin, boundsToCheck, null, invertMatch);
   }

   @Nullable
   private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck, @Nullable NativeImage second, boolean invertMatch) {
      if (baseSkin != null && boundsToCheck != null) {
         boolean hasSecondImageToBeUsedAsBase = second != null;
         Set<Integer> matchColors = new HashSet<>();

         for (int x = boundsToCheck[0]; x <= boundsToCheck[2]; x++) {
            for (int y = boundsToCheck[1]; y <= boundsToCheck[3]; y++) {
               if (baseSkin.getLuminanceOrAlpha(x, y) != 0) {
                  matchColors.add(ETFUtils2.getPixel(baseSkin, x, y));
               }
            }
         }

         if (matchColors.isEmpty()) {
            return null;
         } else {
            NativeImage texture;
            if (!hasSecondImageToBeUsedAsBase) {
               texture = new NativeImage(baseSkin.getWidth(), baseSkin.getHeight(), false);
               texture.copyFrom(baseSkin);
            } else {
               texture = new NativeImage(second.getWidth(), second.getHeight(), false);
               texture.copyFrom(second);
            }

            for (int x = 0; x < texture.getWidth(); x++) {
               for (int yx = 0; yx < texture.getHeight(); yx++) {
                  if (invertMatch) {
                     if (matchColors.contains(ETFUtils2.getPixel(texture, x, yx))) {
                        ETFUtils2.setPixel(texture, x, yx, 0);
                     }
                  } else if (!matchColors.contains(ETFUtils2.getPixel(texture, x, yx))) {
                     ETFUtils2.setPixel(texture, x, yx, 0);
                  }
               }
            }

            return returnNullIfEmptyImage(texture);
         }
      } else {
         return null;
      }
   }

   @Nullable
   private static NativeImage returnNullIfEmptyImage(NativeImage imageToCheck) {
      boolean foundAPixel = false;

      for (int x = 0; x < imageToCheck.getWidth(); x++) {
         for (int y = 0; y < imageToCheck.getHeight(); y++) {
            if (ETFUtils2.getPixel(imageToCheck, x, y) != 0) {
               foundAPixel = true;
               return foundAPixel ? imageToCheck : null;
            }
         }
      }

      return foundAPixel ? imageToCheck : null;
   }

   private static int[] getSkinPixelBounds(String choiceKey) {
      return switch (choiceKey) {
         case "marker1" -> new int[]{56, 16, 63, 23};
         case "marker2" -> new int[]{56, 24, 63, 31};
         case "marker3" -> new int[]{56, 32, 63, 39};
         case "marker4" -> new int[]{56, 40, 63, 47};
         case "optimizedEyeSmall" -> new int[]{12, 16, 19, 16};
         case "optimizedEye2High" -> new int[]{12, 16, 19, 17};
         case "optimizedEye2High_second" -> new int[]{12, 18, 19, 19};
         case "optimizedEye4High" -> new int[]{12, 16, 19, 19};
         case "optimizedEye4High_second" -> new int[]{36, 16, 43, 19};
         case "face1" -> new int[]{0, 0, 7, 7};
         case "face2" -> new int[]{24, 0, 31, 7};
         case "face3" -> new int[]{32, 0, 39, 7};
         case "face4" -> new int[]{56, 0, 63, 7};
         case "cape1" -> new int[]{12, 32, 19, 35};
         case "cape2" -> new int[]{36, 32, 43, 35};
         case "cape3" -> new int[]{12, 48, 19, 51};
         case "cape4" -> new int[]{28, 48, 35, 51};
         case "cape5" -> new int[]{44, 48, 51, 51};
         case "cape5.1" -> new int[]{44, 48, 45, 51};
         case "cape5.2" -> new int[]{46, 48, 47, 51};
         case "cape5.3" -> new int[]{48, 48, 49, 51};
         case "cape5.4" -> new int[]{50, 48, 51, 51};
         case "capeVertL" -> new int[]{1, 1, 1, 16};
         case "capeVertR" -> new int[]{10, 1, 10, 16};
         case "capeHorizL" -> new int[]{1, 1, 10, 1};
         case "capeHorizR" -> new int[]{1, 16, 10, 16};
         default -> new int[]{0, 0, 0, 0};
      };
   }

   private static NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown) {
      return returnOptimizedBlinkFace(baseSkin, eyeBounds, eyeHeightFromTopDown, null);
   }

   private static NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown, int[] secondLayerBounds) {
      NativeImage texture = new NativeImage(64, 64, false);
      texture.copyFrom(baseSkin);
      copyToPixels(baseSkin, texture, eyeBounds, 8, 8 + (eyeHeightFromTopDown - 1));
      if (secondLayerBounds != null) {
         copyToPixels(baseSkin, texture, secondLayerBounds, 40, 8 + (eyeHeightFromTopDown - 1));
      }

      return texture;
   }

   private static void forceSolidLowerSkin(NativeImage skin) {
      try {
         stripAlphaInclusive(skin, 8, 0, 23, 15);
         stripAlphaInclusive(skin, 0, 20, 55, 31);
         stripAlphaInclusive(skin, 0, 8, 7, 15);
         stripAlphaInclusive(skin, 24, 8, 31, 15);
         stripAlphaInclusive(skin, 0, 16, 11, 19);
         stripAlphaInclusive(skin, 20, 16, 35, 19);
         stripAlphaInclusive(skin, 44, 16, 51, 19);
         stripAlphaInclusive(skin, 20, 48, 27, 51);
         stripAlphaInclusive(skin, 36, 48, 43, 51);
         stripAlphaInclusive(skin, 16, 52, 47, 63);
      } catch (Exception var2) {
      }
   }

   private static NativeImage getCoatTexture(NativeImage skin, int lengthOfCoat, boolean ignoreTopTexture) {
      NativeImage coat = new NativeImage(64, 64, false);
      coat.fillRect(0, 0, 64, 64, 0);
      if (!ignoreTopTexture) {
         copyToPixels(skin, coat, 4, 32, 7, 35 + lengthOfCoat, 20, 32);
         copyToPixels(skin, coat, 4, 48, 7, 51 + lengthOfCoat, 24, 32);
      }

      copyToPixels(skin, coat, 0, 36, 7, 36 + lengthOfCoat, 16, 36);
      copyToPixels(skin, coat, 12, 36, 15, 36 + lengthOfCoat, 36, 36);
      copyToPixels(skin, coat, 4, 52, 15, 52 + lengthOfCoat, 24, 36);
      return coat;
   }

   private static void copyToPixels(NativeImage source, NativeImage dest, int[] bounds, int copyToX, int CopyToY) {
      copyToPixels(source, dest, bounds[0], bounds[1], bounds[2], bounds[3], copyToX, CopyToY);
   }

   private static void copyToPixels(NativeImage source, NativeImage dest, int x1, int y1, int x2, int y2, int copyToX, int copyToY) {
      int copyToXRelative = copyToX - x1;
      int copyToYRelative = copyToY - y1;

      for (int x = x1; x <= x2; x++) {
         for (int y = y1; y <= y2; y++) {
            ETFUtils2.setPixel(dest, x + copyToXRelative, y + copyToYRelative, ETFUtils2.getPixel(source, x, y));
         }
      }
   }

   private static void deletePixels(NativeImage source, int x1, int y1, int x2, int y2) {
      for (int x = x1; x <= x2; x++) {
         for (int y = y1; y <= y2; y++) {
            ETFUtils2.setPixel(source, x, y, 0);
         }
      }
   }

   public static int getSkinPixelColourToNumber(int color) {
      return switch (color) {
         case -16776961 -> 3;
         case -16760705 -> 5;
         case -16744449 -> 7;
         case -16711936 -> 4;
         case -14483457 -> 8;
         case -12362096 -> 666;
         case -65536 -> 6;
         case -65281 -> 1;
         case -256 -> 2;
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
      for (int i = x1; i <= x2; i++) {
         for (int j = y1; j <= y2; j++) {
            ETFUtils2.setPixel(image, i, j, ETFUtils2.getPixel(image, i, j) | 0xFF000000);
         }
      }
   }

   public boolean isCorrectObjectForThisSkin(ResourceLocation check) {
      return check.equals(this.normalVanillaSkinIdentifier);
   }

   @Nullable
   public ResourceLocation getBaseTextureIdentifierOrNullForVanilla(Player player) {
      return this.getBaseTextureIdentifierOrNullForVanilla(ETFEntityRenderState.forEntity((ETFPlayerEntity)player));
   }

   @Nullable
   public ResourceLocation getBaseTextureIdentifierOrNullForVanilla(ETFEntityRenderState player) {
      this.player = (ETFPlayerEntity)player.entity();
      return this.etfTextureOfFinalBaseSkin != null && this.canUseFeaturesForThisPlayer() ? this.etfTextureOfFinalBaseSkin.getTextureIdentifier(player) : null;
   }

   @Nullable
   public ResourceLocation getBaseHeadTextureIdentifierOrNullForVanilla() {
      return this.etfTextureOfFinalBaseSkin != null && this.canUseFeaturesForThisPlayer() ? this.etfTextureOfFinalBaseSkin.getTextureIdentifier(null) : null;
   }

   @Nullable
   public ResourceLocation getBaseTextureEmissiveIdentifierOrNullForNone() {
      return this.hasEmissives && this.canUseFeaturesForThisPlayer() && this.etfTextureOfFinalBaseSkin != null
         ? this.etfTextureOfFinalBaseSkin.getEmissiveIdentifierOfCurrentState()
         : null;
   }

   public boolean canUseFeaturesForThisPlayer() {
      return this.isTextureReady
         && this.hasFeatures
         && (
            ETF.config().getConfig().enableEnemyTeamPlayersSkinFeatures
               || this.player.etf$isTeammate(Minecraft.getInstance().player)
               || this.player.etf$getScoreboardTeam() == null
         );
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
            HttpTexture skin = (HttpTexture)Minecraft.getInstance()
               .getSkinManager()
               .skinTextures
               .textureManager
               .getTexture(this.normalVanillaSkinIdentifier, null);

            assert skin.file != null;

            FileInputStream fileInputStream = new FileInputStream(skin.file);
            NativeImage img = NativeImage.read(fileInputStream);
            remappingETFSkin = true;
            this.originalSkin = skin.processLegacySkin(img);
            remappingETFSkin = false;
            fileInputStream.close();
            if (Minecraft.getInstance().player != null && this.player.etf$getUuid().equals(Minecraft.getInstance().player.getUUID())) {
               clientPlayerOriginalSkinImageForTool = this.originalSkin;
            }
         } catch (ETFException var28) {
            if (var28 == TRY_AGAIN_LATER) {
               this.skinFailed(null, true);
            } else {
               this.skinFailed("skin pre load failure: " + var28.getMessage());
            }

            return;
         } catch (Exception var29) {
            this.skinFailed("skin pre load failure: " + var29.getMessage());
            return;
         }
      }

      UUID id = this.player.etf$getUuid();
      NativeImage modifiedSkin = ETFUtils2.emptyNativeImage(this.originalSkin.getWidth(), this.originalSkin.getHeight());
      modifiedSkin.copyFrom(this.originalSkin);
      if (this.originalSkin != null) {
         if (ETFUtils2.getPixel(this.originalSkin, 1, 16) == -16776961
            && ETFUtils2.getPixel(this.originalSkin, 0, 16) == -16777089
            && ETFUtils2.getPixel(this.originalSkin, 0, 17) == -16776961
            && ETFUtils2.getPixel(this.originalSkin, 2, 16) == -16711936
            && ETFUtils2.getPixel(this.originalSkin, 3, 16) == -16744704
            && ETFUtils2.getPixel(this.originalSkin, 3, 17) == -16711936
            && ETFUtils2.getPixel(this.originalSkin, 0, 18) == -65536
            && ETFUtils2.getPixel(this.originalSkin, 0, 19) == -8454144
            && ETFUtils2.getPixel(this.originalSkin, 1, 19) == -65536
            && ETFUtils2.getPixel(this.originalSkin, 3, 18) == -1
            && ETFUtils2.getPixel(this.originalSkin, 2, 19) == -1
            && ETFUtils2.getPixel(this.originalSkin, 3, 18) == -1) {
            this.hasFeatures = true;
            ETFUtils2.logMessage("Found Player {" + this.player.etf$getName().getString() + "} with ETF texture features in skin.", false);
            int[] choiceBoxChoices = new int[]{
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 16)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 17)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 18)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 52, 19)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 53, 16)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 53, 17)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 53, 18))
            };
            if (choiceBoxChoices[2] < 1 || choiceBoxChoices[2] > 8) {
               choiceBoxChoices[2] = 1;
            }

            boolean noseUpper = getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 43, 13)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 44, 13)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 43, 14)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 44, 14)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 43, 15)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 44, 15)) == 666;
            boolean noseLower = getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 11, 13)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 12, 13)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 11, 14)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 12, 14)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 11, 15)) == 666
               && getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 12, 15)) == 666;
            this.hasVillagerNose = noseLower || noseUpper;
            boolean removeNosePixels = noseUpper;
            if (noseUpper) {
               deletePixels(modifiedSkin, 43, 13, 44, 15);
            }

            NativeImage noseTexture = null;
            int noseChoice = choiceBoxChoices[5];
            if (noseChoice >= 1 && noseChoice <= 9) {
               if (noseChoice != 1 && noseChoice != 7 && noseChoice != 8 && noseChoice != 9) {
                  noseTexture = ETFUtils2.emptyNativeImage(8, 8);

                  int[] coatSkin = switch (noseChoice) {
                     case 3 -> {
                        this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_2;
                        yield getSkinPixelBounds("cape2");
                     }
                     case 4 -> {
                        this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_3;
                        yield getSkinPixelBounds("cape3");
                     }
                     case 5 -> {
                        this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_4;
                        yield getSkinPixelBounds("cape4");
                     }
                     case 6 -> {
                        this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_5;
                        yield getSkinPixelBounds("cape5");
                     }
                     default -> {
                        this.noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_1;
                        yield getSkinPixelBounds("cape1");
                     }
                  };
                  int noseY = 0;

                  for (int x = coatSkin[0]; x <= coatSkin[2]; x++) {
                     int noseX = 0;

                     for (int y = coatSkin[1]; y <= coatSkin[3]; y++) {
                        ETFUtils2.setPixel(noseTexture, noseX, noseY, ETFUtils2.getPixel(this.originalSkin, x, y));
                        noseX++;
                     }

                     noseY++;
                  }

                  for (int x = 4; x < 8; x++) {
                     for (int y = 0; y < 8; y++) {
                        ETFUtils2.setPixel(noseTexture, x, y, ETFUtils2.getPixel(noseTexture, 7 - x, y));
                     }
                  }

                  for (int x = 0; x < 8; x++) {
                     for (int y = 0; y < 4; y++) {
                        int lowerColour = ETFUtils2.getPixel(noseTexture, x, y + 4);
                        ETFUtils2.setPixel(noseTexture, x, y + 4, ETFUtils2.getPixel(noseTexture, x, y));
                        ETFUtils2.setPixel(noseTexture, x, y, lowerColour);
                     }
                  }

                  this.texturedNoseIdentifier = ETFUtils2.res("etf_skin", id + "_nose.png");
                  ETFUtils2.registerNativeImageToIdentifier(noseTexture, this.texturedNoseIdentifier);
               } else {
                  this.hasVillagerNose = true;
                  this.noseType = ETFConfigScreenSkinTool.NoseType.NONE.getByColorId(noseChoice);
                  if (noseChoice > 7) {
                     removeNosePixels = true;
                     deletePixels(modifiedSkin, 43, 13, 44, 15);
                  }
               }
            }

            NativeImage coatSkin = null;
            int controllerCoat = choiceBoxChoices[1];
            if (controllerCoat >= 1 && controllerCoat <= 8) {
               this.coatStyle = controllerCoat;
               int lengthOfCoat = choiceBoxChoices[2] - 1;
               this.coatLength = lengthOfCoat + 1;
               this.coatIdentifier = ETFUtils2.res("etf_skin", id + "_coat.png");
               coatSkin = getCoatTexture(this.originalSkin, lengthOfCoat, controllerCoat >= 5);
               ETFUtils2.registerNativeImageToIdentifier(coatSkin, this.coatIdentifier);
               if (controllerCoat == 2 || controllerCoat == 4 || controllerCoat == 6 || controllerCoat == 8) {
                  deletePixels(modifiedSkin, 4, 32, 7, 35);
                  deletePixels(modifiedSkin, 4, 48, 7, 51);
                  deletePixels(modifiedSkin, 0, 36, 15, 36 + lengthOfCoat);
                  deletePixels(modifiedSkin, 0, 52, 15, 52 + lengthOfCoat);
               }

               this.hasFatCoat = controllerCoat == 3 || controllerCoat == 4 || controllerCoat == 7 || controllerCoat == 8;
            } else {
               this.coatIdentifier = null;
            }

            this.wasForcedSolid = choiceBoxChoices[6] == 1;
            if (this.wasForcedSolid) {
               forceSolidLowerSkin(modifiedSkin);
            }

            NativeImage blinkSkinFile = null;
            NativeImage blinkSkinFile2 = null;
            ResourceLocation blinkIdentifier = ETFUtils2.res("etf_skin", id + "_blink.png");
            ResourceLocation blink2Identifier = ETFUtils2.res("etf_skin", id + "_blink2.png");
            int blinkChoice = choiceBoxChoices[0];
            this.blinkType = blinkChoice;
            if (blinkChoice >= 1 && blinkChoice <= 5) {
               if (blinkChoice <= 2) {
                  if (removeNosePixels) {
                     deletePixels(modifiedSkin, 35, 5, 36, 7);
                  }

                  blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("face1"), 1, getSkinPixelBounds("face3"));
                  ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                  if (blinkChoice == 2) {
                     if (removeNosePixels) {
                        deletePixels(modifiedSkin, 59, 5, 60, 7);
                     }

                     blinkSkinFile2 = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("face2"), 1, getSkinPixelBounds("face4"));
                     ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                  }
               } else {
                  int eyeHeightTopDown = choiceBoxChoices[3];
                  this.blinkHeight = eyeHeightTopDown;
                  if (eyeHeightTopDown > 8 || eyeHeightTopDown < 1) {
                     eyeHeightTopDown = 1;
                  }

                  if (blinkChoice == 3) {
                     blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEyeSmall"), eyeHeightTopDown);
                     ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                  } else if (blinkChoice == 4) {
                     blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                     blinkSkinFile2 = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                     ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                     ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                  } else {
                     blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                     blinkSkinFile2 = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
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

            List<Integer> markerChoices = List.of(
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 1, 17)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 1, 18)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 2, 17)),
               getSkinPixelColourToNumber(ETFUtils2.getPixel(this.originalSkin, 2, 18))
            );
            NativeImage emissiveImage = null;
            NativeImage emissiveBlinkImage = null;
            NativeImage emissiveBlink2Image = null;
            ResourceLocation emissiveIdentifier = null;
            ResourceLocation blinkEmissiveIdentifier = null;
            ResourceLocation blink2EmissiveIdentifier = null;
            this.hasEmissives = markerChoices.contains(1);
            if (this.hasEmissives) {
               int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(1) + 1));
               emissiveImage = returnMatchPixels(modifiedSkin, boxChosenBounds, false);
               if (emissiveImage != null) {
                  emissiveIdentifier = ETFUtils2.res("etf_skin", id + "_e.png");
                  ETFUtils2.registerNativeImageToIdentifier(emissiveImage, emissiveIdentifier);
                  if (blinkSkinFile != null) {
                     emissiveBlinkImage = returnMatchPixels(blinkSkinFile, boxChosenBounds, false);
                     if (emissiveBlinkImage != null) {
                        blinkEmissiveIdentifier = ETFUtils2.res("etf_skin", id + "_blink_e.png");
                        ETFUtils2.registerNativeImageToIdentifier(emissiveBlinkImage, blinkEmissiveIdentifier);
                     }
                  }

                  if (blinkSkinFile2 != null) {
                     emissiveBlink2Image = returnMatchPixels(blinkSkinFile2, boxChosenBounds, false);
                     if (emissiveBlink2Image != null) {
                        blink2EmissiveIdentifier = ETFUtils2.res("etf_skin", id + "_blink2_e.png");
                        ETFUtils2.registerNativeImageToIdentifier(emissiveBlink2Image, blink2EmissiveIdentifier);
                     }
                  }

                  if (coatSkin != null) {
                     NativeImage checkCoat = returnMatchPixels(modifiedSkin, boxChosenBounds, coatSkin, false);
                     if (checkCoat != null) {
                        this.coatEmissiveIdentifier = ETFUtils2.res("etf_skin", id + "_coat_e.png");
                        ETFUtils2.registerNativeImageToIdentifier(checkCoat, this.coatEmissiveIdentifier);
                     }
                  }

                  if (noseTexture != null) {
                     NativeImage checkNose = returnMatchPixels(modifiedSkin, boxChosenBounds, noseTexture, false);
                     if (checkNose != null) {
                        this.texturedNoseIdentifierEmissive = ETFUtils2.res("etf_skin", id + "_nose_e.png");
                        ETFUtils2.registerNativeImageToIdentifier(checkNose, this.texturedNoseIdentifierEmissive);
                     }
                  }
               } else {
                  this.hasEmissives = false;
               }
            }

            this.hasEnchant = markerChoices.contains(2);
            if (this.hasEnchant) {
               int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(2) + 1));
               NativeImage check = returnMatchPixels(modifiedSkin, boxChosenBounds, false);
               if (check != null) {
                  this.baseEnchantIdentifier = ETFUtils2.res("etf_skin", id + "_enchant.png");
                  ETFUtils2.registerNativeImageToIdentifier(check, this.baseEnchantIdentifier);
                  if (blinkSkinFile != null) {
                     NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds, false);
                     if (checkBlink != null) {
                        this.baseEnchantBlinkIdentifier = ETFUtils2.res("etf_skin", id + "_blink_enchant.png");
                        ETFUtils2.registerNativeImageToIdentifier(checkBlink, this.baseEnchantBlinkIdentifier);
                     }
                  }

                  if (blinkSkinFile2 != null) {
                     NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds, false);
                     if (checkBlink != null) {
                        this.baseEnchantBlink2Identifier = ETFUtils2.res("etf_skin", id + "_blink2_enchant.png");
                        ETFUtils2.registerNativeImageToIdentifier(checkBlink, this.baseEnchantBlink2Identifier);
                     }
                  }

                  if (coatSkin != null) {
                     NativeImage checkCoat = returnMatchPixels(modifiedSkin, boxChosenBounds, coatSkin, false);
                     if (checkCoat != null) {
                        this.coatEnchantedIdentifier = ETFUtils2.res("etf_skin", id + "_coat_enchant.png");
                        ETFUtils2.registerNativeImageToIdentifier(checkCoat, this.coatEnchantedIdentifier);
                     }
                  }

                  if (noseTexture != null) {
                     NativeImage checkNose = returnMatchPixels(modifiedSkin, boxChosenBounds, noseTexture, false);
                     if (checkNose != null) {
                        this.texturedNoseIdentifierEnchanted = ETFUtils2.res("etf_skin", id + "_nose_enchant.png");
                        ETFUtils2.registerNativeImageToIdentifier(checkNose, this.texturedNoseIdentifierEnchanted);
                     }
                  }
               } else {
                  this.hasEnchant = false;
               }
            }

            ResourceLocation modifiedSkinBlinkPatchedIdentifier = null;
            ResourceLocation modifiedSkinPatchedIdentifier = null;
            ResourceLocation modifiedSkinBlink2PatchedIdentifier = null;
            if (this.hasEmissives && emissiveImage != null) {
               modifiedSkinPatchedIdentifier = ETFUtils2.res("etf_skin", id + "_e_patched.png");
               ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(modifiedSkin, emissiveImage);
               ETFUtils2.registerNativeImageToIdentifier(modifiedSkin, modifiedSkinPatchedIdentifier);
               ETFManager.getInstance()
                  .ETF_TEXTURE_CACHE
                  .put(modifiedSkinPatchedIdentifier, ETFTexture.manual(modifiedSkinPatchedIdentifier, emissiveIdentifier, this.baseEnchantIdentifier));
               if (blinkSkinFile != null) {
                  modifiedSkinBlinkPatchedIdentifier = ETFUtils2.res("etf_skin", id + "_blink_e_patched.png");
                  ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(blinkSkinFile, emissiveBlinkImage);
                  ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, modifiedSkinBlinkPatchedIdentifier);
                  ETFManager.getInstance()
                     .ETF_TEXTURE_CACHE
                     .put(
                        modifiedSkinBlinkPatchedIdentifier,
                        ETFTexture.manual(modifiedSkinBlinkPatchedIdentifier, blinkEmissiveIdentifier, this.baseEnchantBlinkIdentifier)
                     );
               }

               if (blinkSkinFile2 != null) {
                  modifiedSkinBlink2PatchedIdentifier = ETFUtils2.res("etf_skin", id + "_blink2_e_patched.png");
                  ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(blinkSkinFile2, emissiveBlink2Image);
                  ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, modifiedSkinBlink2PatchedIdentifier);
                  ETFManager.getInstance()
                     .ETF_TEXTURE_CACHE
                     .put(
                        modifiedSkinBlink2PatchedIdentifier,
                        ETFTexture.manual(modifiedSkinBlink2PatchedIdentifier, blink2EmissiveIdentifier, this.baseEnchantBlink2Identifier)
                     );
               }
            }

            ResourceLocation modifiedSkinIdentifier = ETFUtils2.res("etf_skin", id + ".png");
            ETFUtils2.registerNativeImageToIdentifier(modifiedSkin, modifiedSkinIdentifier);
            this.etfTextureOfFinalBaseSkin = ETFTexture.manual(
               modifiedSkinIdentifier,
               blinkIdentifier,
               blink2Identifier,
               emissiveIdentifier,
               blinkEmissiveIdentifier,
               blink2EmissiveIdentifier,
               this.baseEnchantIdentifier,
               this.baseEnchantBlinkIdentifier,
               this.baseEnchantBlink2Identifier,
               modifiedSkinPatchedIdentifier,
               modifiedSkinBlinkPatchedIdentifier,
               modifiedSkinBlink2PatchedIdentifier
            );
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
