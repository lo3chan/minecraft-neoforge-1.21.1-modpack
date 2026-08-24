package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFConfigScreenSkinTool extends ETFScreenOldCompat {
   private static final ResourceLocation APPLY_OVERLAY = ETFUtils2.res("entity_texture_features:textures/skin_feature_printout.png");
   private static final ResourceLocation REMOVE_OVERLAY = ETFUtils2.res("entity_texture_features:textures/skin_feature_remove.png");
   private static final ResourceLocation WHOLE_FACE_OVERLAY = ETFUtils2.res("entity_texture_features:textures/skin_feature_whole_face.png");
   private static final ResourceLocation SMALL_EYE_OVERLAY = ETFUtils2.res("entity_texture_features:textures/skin_feature_small_eyes.png");
   private static final ResourceLocation BOXES_OVERLAY = ETFUtils2.res("entity_texture_features:textures/skin_feature_orange_areas.png");
   public Boolean originalEnableBlinking;
   public ETFPlayerTexture thisETFPlayerTexture = null;
   public NativeImage currentEditorSkin = null;
   public boolean flipView = false;
   Button printSkinFileButton = null;
   Button villagerNoseButton = null;
   Button coatButton = null;
   Button coatLengthButton = null;
   Button blinkButton = null;
   Button blinkHeightButton = null;
   Button emissiveButton = null;
   Button emissiveSelectButton = null;
   Button enchantButton = null;
   Button enchantSelectButton = null;
   Button transparencyButton = null;
   private Button overridesButton = null;
   private Boolean allowOverrides = null;

   public ETFConfigScreenSkinTool(Screen parent) {
      super("config.entity_texture_features.player_skin_features.title", parent, false);
   }

   public static int getPixelColour(int choice) {
      switch (choice) {
         case 1:
            return -65281;
         case 2:
            return -256;
         case 3:
            return -16776961;
         case 4:
            return -16711936;
         case 5:
            return -16760705;
         case 6:
            return -65536;
         case 7:
            return -16744449;
         case 8:
            return -14483457;
         default:
            return choice;
      }
   }

   private void onExit() {
      ETF.config().getConfig().enableBlinking = this.originalEnableBlinking;
      if (Minecraft.getInstance().player != null) {
         ETFManager.getInstance().PLAYER_TEXTURE_MAP.removeEntryOnly(Minecraft.getInstance().player.getUUID());
      }

      this.thisETFPlayerTexture = null;
   }

   @Override
   public void onClose() {
      this.onExit();
      super.onClose();
   }

   @Override
   protected void init() {
      super.init();
      if (this.originalEnableBlinking == null) {
         this.originalEnableBlinking = ETF.config().getConfig().enableBlinking;
         ETF.config().getConfig().enableBlinking = true;
      }

      if (Minecraft.getInstance().player != null && this.thisETFPlayerTexture == null) {
         this.thisETFPlayerTexture = ETFManager.getInstance().PLAYER_TEXTURE_MAP.get(Minecraft.getInstance().player.getUUID());
         if (this.thisETFPlayerTexture == null) {
            ETFPlayerTexture etfPlayerTexture = new ETFPlayerTexture();
            ETFManager.getInstance().PLAYER_TEXTURE_MAP.put(Minecraft.getInstance().player.getUUID(), etfPlayerTexture);
            this.thisETFPlayerTexture = etfPlayerTexture;
         } else if (this.thisETFPlayerTexture.etfTextureOfFinalBaseSkin != null) {
            this.thisETFPlayerTexture.etfTextureOfFinalBaseSkin.setGUIBlink();
         }
      }

      if (this.currentEditorSkin == null) {
         this.currentEditorSkin = ETFUtils2.emptyNativeImage(64, 64);
         NativeImage skin = ETFPlayerTexture.clientPlayerOriginalSkinImageForTool;
         if (skin != null) {
            this.currentEditorSkin.copyFrom(ETFPlayerTexture.clientPlayerOriginalSkinImageForTool);
         } else {
            this.onExit();
            ETFUtils2.logError("could not load tool as skin could not be loaded");
            Objects.requireNonNull(this.minecraft).setScreen(this.parent);
         }
      }

      this.addRenderableWidget(this.getETFButton(this.width / 2 - 210, (int)(this.height * 0.9), 200, 20, CommonComponents.GUI_CANCEL, button -> {
         this.onExit();
         Objects.requireNonNull(this.minecraft).setScreen(this.parent);
      }));
      this.addRenderableWidget(
         this.getETFButton((int)(this.width * 0.024), (int)(this.height * 0.2), 20, 20, Component.nullToEmpty("⟳"), button -> this.flipView = !this.flipView)
      );
      this.printSkinFileButton = this.getETFButton(
         this.width / 2 + 10, (int)(this.height * 0.9), 200, 20, ETF.getTextFromTranslation("selectWorld.edit.save"), button -> {
            boolean result = false;
            if (Minecraft.getInstance().player != null) {
               result = this.printPlayerSkinCopy();
            }

            Objects.requireNonNull(this.minecraft).setScreen(new ETFConfigScreenSkinToolOutcome(this.parent, result, this.currentEditorSkin));
            this.onExit();
         }
      );
      this.addRenderableWidget(this.printSkinFileButton);
      if (Minecraft.getInstance().player != null) {
         this.addRenderableWidget(
            this.getETFButton(
               (int)(this.width * 0.25),
               (int)(this.height * 0.2),
               (int)(this.width * 0.42),
               20,
               this.thisETFPlayerTexture.hasFeatures
                  ? ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.remove_features")
                  : ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.add_features"),
               button -> {
                  if (this.thisETFPlayerTexture.hasFeatures) {
                     this.applyExistingOverlayToSkin(REMOVE_OVERLAY);
                  } else {
                     this.applyExistingOverlayToSkin(APPLY_OVERLAY);
                  }

                  this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
                  button.setMessage(
                     this.thisETFPlayerTexture.hasFeatures
                        ? ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.remove_features")
                        : ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.add_features")
                  );
                  this.updateButtons();
               }
            )
         );
         this.overridesButton = (Button)this.addRenderableWidget(
            this.getETFButton(
               (int)(this.width * 0.695),
               (int)(this.height * 0.2),
               (int)(this.width * 0.275),
               20,
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.allow_examples"),
               button -> {
                  this.allowOverrides = false;
                  button.active = false;
                  button.setMessage(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.allow_examples.off"));
               },
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.allow_examples.tooltip")
            )
         );
         this.villagerNoseButton = this.getETFButton(
            (int)(this.width * 0.25),
            (int)(this.height * 0.7),
            (int)(this.width * 0.2),
            20,
            Component.nullToEmpty(
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.button").getString()
                  + this.thisETFPlayerTexture.noseType.getButtonText().getString()
            ),
            button -> {
               int colour = this.thisETFPlayerTexture.noseType.next().getNosePixelColour();
               ETFUtils2.setPixel(this.currentEditorSkin, 53, 17, colour);
               if (this.thisETFPlayerTexture.noseType.next().appliesTextureOverlay()) {
                  this.applyExistingOverlayToSkin(BOXES_OVERLAY);
               }

               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               button.setMessage(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.button").getString()
                        + this.thisETFPlayerTexture.noseType.getButtonText().getString()
                  )
               );
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.tooltip")
         );
         this.transparencyButton = this.getETFButton(
            (int)(this.width * 0.695),
            (int)(this.height * 0.7),
            (int)(this.width * 0.275),
            20,
            Component.nullToEmpty(
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.transparency.button").getString()
                  + booleanAsOnOff(!this.thisETFPlayerTexture.wasForcedSolid)
            ),
            button -> {
               button.setMessage(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.transparency.button").getString()
                        + booleanAsOnOff(this.thisETFPlayerTexture.wasForcedSolid)
                  )
               );
               ETFUtils2.setPixel(this.currentEditorSkin, 53, 18, getPixelColour(this.thisETFPlayerTexture.wasForcedSolid ? 0 : 1));
               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               this.updateButtons();
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.transparency.tooltip")
         );
         this.coatButton = this.getETFButton(
            (int)(this.width * 0.25),
            (int)(this.height * 0.3),
            (int)(this.width * 0.42),
            20,
            ETFConfigScreenSkinTool.CoatStyle.get(this.thisETFPlayerTexture.coatStyle).getTitle(),
            button -> {
               ETFConfigScreenSkinTool.CoatStyle coat = ETFConfigScreenSkinTool.CoatStyle.get(this.thisETFPlayerTexture.coatStyle).next();
               button.setMessage(coat.getTitle());
               ETFUtils2.setPixel(this.currentEditorSkin, 52, 17, coat.getCoatPixelColour());
               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               this.updateButtons();
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.tooltip")
         );
         this.coatLengthButton = this.getETFButton(
            (int)(this.width * 0.695),
            (int)(this.height * 0.3),
            (int)(this.width * 0.275),
            20,
            Component.nullToEmpty(
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_length.title").getString()
                  + this.thisETFPlayerTexture.coatLength
            ),
            button -> {
               int lengthChoice;
               if (this.thisETFPlayerTexture.coatLength == 8) {
                  lengthChoice = 1;
               } else {
                  lengthChoice = this.thisETFPlayerTexture.coatLength + 1;
               }

               ETFUtils2.setPixel(this.currentEditorSkin, 52, 18, getPixelColour(lengthChoice));
               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               button.setMessage(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_length.title").getString()
                        + this.thisETFPlayerTexture.coatLength
                  )
               );
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_length.tooltip")
         );
         this.blinkButton = this.getETFButton(
            (int)(this.width * 0.25),
            (int)(this.height * 0.4),
            (int)(this.width * 0.42),
            20,
            ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType).getTitle(),
            button -> {
               ETFConfigScreenSkinTool.BlinkType blink = ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType).next();
               button.setMessage(blink.getTitle());
               if (blink != ETFConfigScreenSkinTool.BlinkType.NONE
                  && blink != ETFConfigScreenSkinTool.BlinkType.WHOLE_FACE_TWO
                  && blink != ETFConfigScreenSkinTool.BlinkType.WHOLE_FACE) {
                  ETFUtils2.setPixel(this.currentEditorSkin, 52, 19, getPixelColour(0));
               } else if (ETFUtils2.getPixel(this.currentEditorSkin, 52, 19) > blink.getMaxEyePixelHeight()) {
                  ETFUtils2.setPixel(this.currentEditorSkin, 52, 19, getPixelColour(blink.getMaxEyePixelHeight()));
               }

               ResourceLocation overlay = blink.getExampleOverlay();
               if (overlay != null) {
                  this.applyExistingOverlayToSkin(overlay);
               }

               ETFUtils2.setPixel(this.currentEditorSkin, 52, 16, blink.getBlinkPixelColour());
               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               this.updateButtons();
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.tooltip")
         );
         this.blinkHeightButton = this.getETFButton(
            (int)(this.width * 0.695),
            (int)(this.height * 0.4),
            (int)(this.width * 0.275),
            20,
            Component.nullToEmpty(
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_height.title").getString()
                  + this.thisETFPlayerTexture.blinkHeight
            ),
            button -> {
               int heightChoice;
               if (this.thisETFPlayerTexture.blinkHeight == ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType).getMaxEyePixelHeight()) {
                  heightChoice = 1;
               } else {
                  heightChoice = this.thisETFPlayerTexture.blinkHeight + 1;
               }

               ETFUtils2.setPixel(this.currentEditorSkin, 52, 19, getPixelColour(heightChoice));
               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               button.setMessage(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_height.title").getString()
                        + this.thisETFPlayerTexture.blinkHeight
                  )
               );
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_height.tooltip")
         );
         this.emissiveButton = this.getETFButton(
            (int)(this.width * 0.25),
            (int)(this.height * 0.5),
            (int)(this.width * 0.42),
            20,
            Component.nullToEmpty(
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.emissive_enable.button").getString()
                  + (ETFUtils2.getPixel(this.currentEditorSkin, 1, 17) == getPixelColour(1) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF)
                     .getString()
            ),
            button -> {
               if (this.thisETFPlayerTexture.hasEmissives) {
                  ETFUtils2.setPixel(this.currentEditorSkin, 1, 17, 0);
               } else {
                  ETFUtils2.setPixel(this.currentEditorSkin, 1, 17, getPixelColour(1));
               }

               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               button.setMessage(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.emissive_enable.button").getString()
                        + (ETFUtils2.getPixel(this.currentEditorSkin, 1, 17) == getPixelColour(1) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF)
                           .getString()
                  )
               );
               this.updateButtons();
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.emissive_enable.tooltip")
         );
         this.emissiveSelectButton = this.getETFButton(
            (int)(this.width * 0.695),
            (int)(this.height * 0.5),
            (int)(this.width * 0.275),
            20,
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.emissive_select.button"),
            button -> Objects.requireNonNull(this.minecraft)
               .setScreen(new ETFConfigScreenSkinToolPixelSelection(this, ETFConfigScreenSkinToolPixelSelection.SelectionMode.EMISSIVE))
         );
         this.enchantButton = this.getETFButton(
            (int)(this.width * 0.25),
            (int)(this.height * 0.6),
            (int)(this.width * 0.42),
            20,
            Component.nullToEmpty(
               ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.enchant_enable.button").getString()
                  + (ETFUtils2.getPixel(this.currentEditorSkin, 1, 18) == getPixelColour(2) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF)
                     .getString()
            ),
            button -> {
               if (this.thisETFPlayerTexture.hasEnchant) {
                  ETFUtils2.setPixel(this.currentEditorSkin, 1, 18, 0);
               } else {
                  ETFUtils2.setPixel(this.currentEditorSkin, 1, 18, getPixelColour(2));
               }

               this.thisETFPlayerTexture.changeSkinToThisForTool(this.currentEditorSkin);
               button.setMessage(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.enchant_enable.button").getString()
                        + (ETFUtils2.getPixel(this.currentEditorSkin, 1, 18) == getPixelColour(2) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF)
                           .getString()
                  )
               );
               this.updateButtons();
            },
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.enchant_enable.tooltip")
         );
         this.enchantSelectButton = this.getETFButton(
            (int)(this.width * 0.695),
            (int)(this.height * 0.6),
            (int)(this.width * 0.275),
            20,
            ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.enchant_select.button"),
            button -> Objects.requireNonNull(this.minecraft)
               .setScreen(new ETFConfigScreenSkinToolPixelSelection(this, ETFConfigScreenSkinToolPixelSelection.SelectionMode.ENCHANTED))
         );
         this.updateButtons();
         this.addRenderableWidget(this.villagerNoseButton);
         this.addRenderableWidget(this.coatButton);
         this.addRenderableWidget(this.coatLengthButton);
         this.addRenderableWidget(this.blinkButton);
         this.addRenderableWidget(this.blinkHeightButton);
         this.addRenderableWidget(this.emissiveButton);
         this.addRenderableWidget(this.emissiveSelectButton);
         this.addRenderableWidget(this.enchantButton);
         this.addRenderableWidget(this.enchantSelectButton);
         this.addRenderableWidget(this.transparencyButton);
      }
   }

   private void updateButtons() {
      boolean activeFeatures = this.thisETFPlayerTexture.hasFeatures;
      if (this.villagerNoseButton != null) {
         this.villagerNoseButton.active = activeFeatures;
         this.villagerNoseButton
            .setMessage(
               Component.nullToEmpty(
                  ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.button").getString()
                     + this.thisETFPlayerTexture.noseType.getButtonText().getString()
               )
            );
      }

      if (this.coatButton != null) {
         this.coatButton.active = activeFeatures;
         this.coatButton.setMessage(ETFConfigScreenSkinTool.CoatStyle.get(this.thisETFPlayerTexture.coatStyle).getTitle());
      }

      if (this.coatLengthButton != null) {
         this.coatLengthButton.active = activeFeatures
            && ETFConfigScreenSkinTool.CoatStyle.get(this.thisETFPlayerTexture.coatStyle) != ETFConfigScreenSkinTool.CoatStyle.NONE;
         this.coatLengthButton
            .setMessage(
               Component.nullToEmpty(
                  ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_length.title").getString()
                     + this.thisETFPlayerTexture.coatLength
               )
            );
      }

      if (this.blinkButton != null) {
         this.blinkButton.active = activeFeatures;
         this.blinkButton.setMessage(ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType).getTitle());
      }

      if (this.blinkHeightButton != null) {
         this.blinkHeightButton.active = activeFeatures
            && ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType) != ETFConfigScreenSkinTool.BlinkType.NONE
            && ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType) != ETFConfigScreenSkinTool.BlinkType.WHOLE_FACE_TWO
            && ETFConfigScreenSkinTool.BlinkType.get(this.thisETFPlayerTexture.blinkType) != ETFConfigScreenSkinTool.BlinkType.WHOLE_FACE;
         this.blinkHeightButton
            .setMessage(
               Component.nullToEmpty(
                  ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_height.title").getString()
                     + this.thisETFPlayerTexture.blinkHeight
               )
            );
      }

      if (this.emissiveButton != null) {
         this.emissiveButton.active = activeFeatures;
         this.emissiveButton
            .setMessage(
               Component.nullToEmpty(
                  ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.emissive_enable.button").getString()
                     + (ETFUtils2.getPixel(this.currentEditorSkin, 1, 17) == getPixelColour(1) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF)
                        .getString()
               )
            );
      }

      if (this.emissiveSelectButton != null) {
         this.emissiveSelectButton.active = activeFeatures && ETFUtils2.getPixel(this.currentEditorSkin, 1, 17) == getPixelColour(1);
      }

      if (this.enchantButton != null) {
         this.enchantButton.active = activeFeatures;
         this.enchantButton
            .setMessage(
               Component.nullToEmpty(
                  ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.enchant_enable.button").getString()
                     + (ETFUtils2.getPixel(this.currentEditorSkin, 1, 18) == getPixelColour(2) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF)
                        .getString()
               )
            );
      }

      if (this.enchantSelectButton != null) {
         this.enchantSelectButton.active = activeFeatures && ETFUtils2.getPixel(this.currentEditorSkin, 1, 18) == getPixelColour(2);
      }

      if (this.transparencyButton != null) {
         this.transparencyButton.active = activeFeatures;
         this.transparencyButton
            .setMessage(
               Component.nullToEmpty(
                  ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.transparency.button").getString()
                     + booleanAsOnOff(!this.thisETFPlayerTexture.wasForcedSolid)
               )
            );
      }
   }

   @Override
   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         int height = (int)(this.height * 0.75);
         int playerX = (int)(this.width * 0.14);
         this.drawEntity(context, playerX, height, (int)(this.height * 0.3), -mouseX + playerX, (float)(-mouseY + this.height * 0.3), player);
      } else {
         context.drawString(this.font, Component.nullToEmpty("Player is null for some reason!"), this.width / 7, (int)(this.height * 0.4), 16777215);
         context.drawString(this.font, Component.nullToEmpty("Cannot load player to render!"), this.width / 7, (int)(this.height * 0.45), 16777215);
      }

      context.drawString(
         this.font,
         ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.crouch_message"),
         this.width / 40,
         (int)(this.height * 0.8),
         5592405
      );
      context.drawString(
         this.font,
         ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_message"),
         this.width / 40,
         (int)(this.height * 0.1),
         5592405
      );
   }

   public void applyExistingOverlayToSkin(ResourceLocation overlayTexture) {
      if (ETF.isFabric() == ETF.FABRIC_API) {
         NativeImage overlayImage = ETFUtils2.getNativeImageElseNull(overlayTexture);

         assert overlayImage != null;

         if (!overlayTexture.equals(REMOVE_OVERLAY) && !overlayTexture.equals(APPLY_OVERLAY)) {
            if (this.allowOverrides == null) {
               Minecraft.getInstance().setScreen(new ETFConfigScreenSkinTool.ConfirmScreen(Component.nullToEmpty(""), this));
               if (this.allowOverrides == null) {
                  this.allowOverrides = false;
               }
            }

            if (!this.allowOverrides) {
               ETFUtils2.logMessage("Skin example overlay [" + overlayTexture + "] not applied.", false);
               return;
            }
         }

         try {
            for (int x = 0; x < this.currentEditorSkin.getWidth(); x++) {
               for (int y = 0; y < this.currentEditorSkin.getHeight(); y++) {
                  if (ETFUtils2.getPixel(overlayImage, x, y) != 0) {
                     ETFUtils2.setPixel(this.currentEditorSkin, x, y, ETFUtils2.getPixel(overlayImage, x, y));
                  }
               }
            }
         } catch (Exception var5) {
            ETFUtils2.logMessage("Skin feature layout could not be applied to a copy of your skin. Error written to log.", false);
            ETFUtils2.logError(var5.toString(), false);
         }
      } else {
         ETFUtils2.logError("Fabric API required for skin processing, cancelling.", false);
      }
   }

   public boolean printPlayerSkinCopy() {
      if (ETF.isFabric() == ETF.FABRIC_API && ETF.getConfigDirectory() != null) {
         Path outputDirectory = Path.of(ETF.getConfigDirectory().toFile().getParent(), "\\ETF_player_skin_printout.png");

         try {
            this.currentEditorSkin.writeToFile(outputDirectory);
            ETFUtils2.logMessage(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.print_skin.result.success").getString(), false);
            return true;
         } catch (Exception var3) {
            ETFUtils2.logError(var3.toString(), false);
         }
      }

      return false;
   }

   public void drawEntity(GuiGraphics context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
      float f = (float)Math.atan(mouseX / 40.0F);
      float g = (float)Math.atan(mouseY / 40.0F);
      Quaternionf quaternionf = new Quaternionf().rotateZ(3.1415927F);
      Quaternionf quaternionf2 = new Quaternionf().rotateX(0.0F);
      quaternionf.mul(quaternionf2);
      context.pose().pushPose();
      context.pose().translate(x, y, 150.0);
      context.pose().mulPose(new Matrix4f().scaling(size, size, -size));
      context.pose().mulPose(quaternionf);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      if (quaternionf2 != null) {
         quaternionf2.conjugate();
         entityRenderDispatcher.overrideCameraOrientation(quaternionf2);
      }

      entityRenderDispatcher.setRenderShadow(false);
      float h = entity.yBodyRot;
      float i = entity.getYRot();
      float j = entity.getXRot();
      float k = entity.yHeadRotO;
      float l = entity.yHeadRot;
      entity.yBodyRot = (this.flipView ? 0.0F : 180.0F) + f * 20.0F;
      entity.setYRot((this.flipView ? 0.0F : 180.0F) + f * 40.0F);
      entity.setXRot(-g * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.pose(), context.bufferSource(), 15728880));
      context.flush();
      entityRenderDispatcher.setRenderShadow(true);
      context.pose().popPose();
      Lighting.setupFor3DItems();
      entity.yBodyRot = h;
      entity.setYRot(i);
      entity.setXRot(j);
      entity.yHeadRotO = k;
      entity.yHeadRot = l;
   }

   public static enum BlinkType {
      ONE_PIXEL,
      TWO_PIXEL,
      FOUR_PIXEL,
      WHOLE_FACE,
      WHOLE_FACE_TWO,
      NONE;

      public static ETFConfigScreenSkinTool.BlinkType get(int id) {
         switch (id) {
            case 1:
               return WHOLE_FACE;
            case 2:
               return WHOLE_FACE_TWO;
            case 3:
               return ONE_PIXEL;
            case 4:
               return TWO_PIXEL;
            case 5:
               return FOUR_PIXEL;
            default:
               return NONE;
         }
      }

      public ResourceLocation getExampleOverlay() {
         return switch (this) {
            case WHOLE_FACE, WHOLE_FACE_TWO -> ETFConfigScreenSkinTool.WHOLE_FACE_OVERLAY;
            case NONE -> null;
            default -> ETFConfigScreenSkinTool.SMALL_EYE_OVERLAY;
         };
      }

      public Component getTitle() {
         switch (this) {
            case ONE_PIXEL:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.1");
            case TWO_PIXEL:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.2");
            case FOUR_PIXEL:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.4");
            case WHOLE_FACE:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.whole.1");
            case WHOLE_FACE_TWO:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.whole.2");
            default:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.blink_type.none");
         }
      }

      public int getBlinkPixelColour() {
         switch (this) {
            case ONE_PIXEL:
               return ETFConfigScreenSkinTool.getPixelColour(3);
            case TWO_PIXEL:
               return ETFConfigScreenSkinTool.getPixelColour(4);
            case FOUR_PIXEL:
               return ETFConfigScreenSkinTool.getPixelColour(5);
            case WHOLE_FACE:
               return ETFConfigScreenSkinTool.getPixelColour(1);
            case WHOLE_FACE_TWO:
               return ETFConfigScreenSkinTool.getPixelColour(2);
            default:
               return 0;
         }
      }

      public int getMaxEyePixelHeight() {
         switch (this) {
            case ONE_PIXEL:
               return 8;
            case TWO_PIXEL:
               return 7;
            case FOUR_PIXEL:
               return 5;
            default:
               return 1;
         }
      }

      public ETFConfigScreenSkinTool.BlinkType next() {
         switch (this) {
            case ONE_PIXEL:
               return TWO_PIXEL;
            case TWO_PIXEL:
               return FOUR_PIXEL;
            case FOUR_PIXEL:
               return WHOLE_FACE;
            case WHOLE_FACE:
               return WHOLE_FACE_TWO;
            case WHOLE_FACE_TWO:
            default:
               return NONE;
            case NONE:
               return ONE_PIXEL;
         }
      }
   }

   public static enum CoatStyle {
      COPIED_THIN_TOP,
      MOVED_THIN_TOP,
      COPIED_FAT_TOP,
      MOVED_FAT_TOP,
      COPIED_THIN,
      MOVED_THIN,
      COPIED_FAT,
      MOVED_FAT,
      NONE;

      public static ETFConfigScreenSkinTool.CoatStyle get(int id) {
         switch (id) {
            case 1:
               return COPIED_THIN_TOP;
            case 2:
               return MOVED_THIN_TOP;
            case 3:
               return COPIED_FAT_TOP;
            case 4:
               return MOVED_FAT_TOP;
            case 5:
               return COPIED_THIN;
            case 6:
               return MOVED_THIN;
            case 7:
               return COPIED_FAT;
            case 8:
               return MOVED_FAT;
            default:
               return NONE;
         }
      }

      public Component getTitle() {
         switch (this) {
            case COPIED_THIN_TOP:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.1");
            case MOVED_THIN_TOP:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.2");
            case COPIED_FAT_TOP:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.3");
            case MOVED_FAT_TOP:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.4");
            case COPIED_THIN:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.5");
            case MOVED_THIN:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.6");
            case COPIED_FAT:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.7");
            case MOVED_FAT:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.8");
            default:
               return ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.coat_style.none");
         }
      }

      public int getCoatPixelColour() {
         switch (this) {
            case COPIED_THIN_TOP:
               return -65281;
            case MOVED_THIN_TOP:
               return -256;
            case COPIED_FAT_TOP:
               return -16776961;
            case MOVED_FAT_TOP:
               return -16711936;
            case COPIED_THIN:
               return -16760705;
            case MOVED_THIN:
               return -65536;
            case COPIED_FAT:
               return -16744449;
            case MOVED_FAT:
               return -14483457;
            default:
               return 0;
         }
      }

      public ETFConfigScreenSkinTool.CoatStyle next() {
         switch (this) {
            case COPIED_THIN_TOP:
               return MOVED_THIN_TOP;
            case MOVED_THIN_TOP:
               return COPIED_FAT_TOP;
            case COPIED_FAT_TOP:
               return MOVED_FAT_TOP;
            case MOVED_FAT_TOP:
               return COPIED_THIN;
            case COPIED_THIN:
               return MOVED_THIN;
            case MOVED_THIN:
               return COPIED_FAT;
            case COPIED_FAT:
               return MOVED_FAT;
            case MOVED_FAT:
            default:
               return NONE;
            case NONE:
               return COPIED_THIN_TOP;
         }
      }
   }

   private class ConfirmScreen extends Screen {
      final Screen parent;

      protected ConfirmScreen(final Component title, Screen parent) {
         super(title);
         this.parent = parent;
      }

      public void onClose() {
         if (ETFConfigScreenSkinTool.this.overridesButton == null) {
            ETFConfigScreenSkinTool.this.allowOverrides = false;
         } else {
            ETFConfigScreenSkinTool.this.overridesButton.onPress();
         }

         Minecraft.getInstance().setScreen(this.parent);
      }

      public boolean shouldCloseOnEsc() {
         return true;
      }

      protected void init() {
         super.init();
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_YES, button -> {
            ETFConfigScreenSkinTool.this.allowOverrides = true;
            Minecraft.getInstance().setScreen(this.parent);
         }).bounds(this.width / 2 - 210, this.height / 2 + 50, 200, 20).build());
         this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_NO, button -> this.onClose()).bounds(this.width / 2 + 10, this.height / 2 + 50, 200, 20).build()
         );
      }

      public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
         super.render(context, mouseX, mouseY, delta);
         context.drawCenteredString(
            this.font,
            ETF.getTextFromTranslation("config.entity_texture_features.skin_editor.overlays.-2_extra_space_for_other_lang"),
            this.width / 2,
            this.height / 2 - 33,
            -1
         );
         context.drawCenteredString(
            this.font,
            ETF.getTextFromTranslation("config.entity_texture_features.skin_editor.overlays.-1_extra_space_for_other_lang"),
            this.width / 2,
            this.height / 2 - 22,
            -1
         );
         context.drawCenteredString(
            this.font,
            ETF.getTextFromTranslation("config.entity_texture_features.skin_editor.overlays.0_extra_space_for_other_lang"),
            this.width / 2,
            this.height / 2 - 11,
            -1
         );
         context.drawCenteredString(
            this.font, ETF.getTextFromTranslation("config.entity_texture_features.skin_editor.overlays.1"), this.width / 2, this.height / 2, -1
         );
         context.drawCenteredString(
            this.font, ETF.getTextFromTranslation("config.entity_texture_features.skin_editor.overlays.2"), this.width / 2, this.height / 2 + 11, -1
         );
         context.drawCenteredString(
            this.font, ETF.getTextFromTranslation("config.entity_texture_features.skin_editor.overlays.3"), this.width / 2, this.height / 2 + 22, -1
         );
      }
   }

   public static enum NoseType {
      VILLAGER(1, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.villager")),
      VILLAGER_TEXTURED(7, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.villager2")),
      VILLAGER_REMOVE(8, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.villager3")),
      VILLAGER_TEXTURED_REMOVE(9, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.villager4")),
      TEXTURED_1(2, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.textured.1")),
      TEXTURED_2(3, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.textured.2")),
      TEXTURED_3(4, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.textured.3")),
      TEXTURED_4(5, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.textured.4")),
      TEXTURED_5(6, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.textured.5")),
      NONE(0, ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.nose.none"));

      public final int id;
      private final Component buttonText;

      private NoseType(int i, Component buttonText) {
         this.id = i;
         this.buttonText = buttonText;
      }

      public ETFConfigScreenSkinTool.NoseType getByColorId(int id) {
         for (ETFConfigScreenSkinTool.NoseType nose : values()) {
            if (nose.id == id) {
               return nose;
            }
         }

         return NONE;
      }

      public boolean appliesTextureOverlay() {
         return this == TEXTURED_1 || this == TEXTURED_2 || this == TEXTURED_3 || this == TEXTURED_4 || this == TEXTURED_5;
      }

      public Component getButtonText() {
         return this.buttonText;
      }

      public ETFConfigScreenSkinTool.NoseType next() {
         switch (this) {
            case VILLAGER:
               return VILLAGER_TEXTURED;
            case VILLAGER_TEXTURED:
               return VILLAGER_REMOVE;
            case VILLAGER_REMOVE:
               return VILLAGER_TEXTURED_REMOVE;
            case VILLAGER_TEXTURED_REMOVE:
               return TEXTURED_1;
            case TEXTURED_1:
               return TEXTURED_2;
            case TEXTURED_2:
               return TEXTURED_3;
            case TEXTURED_3:
               return TEXTURED_4;
            case TEXTURED_4:
               return TEXTURED_5;
            case TEXTURED_5:
            default:
               return NONE;
            case NONE:
               return VILLAGER;
         }
      }

      public int getNosePixelColour() {
         return ETFPlayerTexture.getSkinNumberToPixelColour(this.id);
      }
   }
}
