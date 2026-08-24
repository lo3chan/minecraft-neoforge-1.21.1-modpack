package net.cibernet.alchemancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.Arrays;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.C2SChromatizePayload;
import net.cibernet.alchemancy.network.C2SResetItemTintPayload;
import net.cibernet.alchemancy.properties.TintedProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ChromaTintingScreen extends Screen {
   private final ItemStack affectedItem;
   private final Integer[] originalTint;
   private HeaderAndFooterLayout layout;
   private ChromaTintingScreen.ColorSlider hueSlider;
   private ChromaTintingScreen.ColorSlider saturationSlider;
   private ChromaTintingScreen.ColorSlider brightnessSlider;
   private EditBox hexInput = null;
   private static final ResourceLocation HUE_SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath("alchemancy", "chromatize/slider_hue");
   private static final ResourceLocation SATURATION_SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath("alchemancy", "chromatize/slider_saturation");
   private static final ResourceLocation BRIGHTNESS_SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath("alchemancy", "chromatize/slider_brightness");
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   private static final int MAX_HUE = 720;
   private static final int MAX_SB = 200;

   public ChromaTintingScreen(ItemStack stack) {
      super(Component.translatable("screen.chromachine.title"));
      this.affectedItem = stack;
      this.originalTint = ((TintedProperty)AlchemancyProperties.TINTED.value()).getData(stack);
   }

   protected void init() {
      super.init();
      this.layout = new HeaderAndFooterLayout(this, 32, 32);
      LinearLayout footer = ((LinearLayout)this.layout.addToFooter(LinearLayout.horizontal())).spacing(5);
      footer.defaultCellSetting().alignHorizontallyCenter();
      footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, p_329727_ -> {
         if (this.originalTint != null && this.originalTint.length != 0) {
            ((TintedProperty)AlchemancyProperties.TINTED.value()).setData(this.affectedItem, this.originalTint);
         } else {
            InfusedPropertiesHelper.removeProperty(this.affectedItem, AlchemancyProperties.TINTED);
         }

         this.onClose();
      }).width(100).build());
      footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> {
         if (InfusedPropertiesHelper.hasProperty(this.affectedItem, AlchemancyProperties.TINTED)) {
            PacketDistributor.sendToServer(new C2SChromatizePayload(this.getColor()), new CustomPacketPayload[0]);
         }

         this.onClose();
      }).width(100).build());
      LinearLayout header = ((LinearLayout)this.layout.addToHeader(LinearLayout.vertical())).spacing(5);
      header.addChild(new StringWidget(this.width, 16, this.getTitle(), this.font).alignCenter());
      LinearLayout body = LinearLayout.vertical().spacing(10);
      this.layout.addToContents(body);
      Integer[] colors = ((TintedProperty)AlchemancyProperties.TINTED.get()).getData(this.affectedItem);
      int color = colors.length > 0 ? colors[0] : -1;
      float[] hsb = Color.RGBtoHSB(ARGB32.red(color), ARGB32.green(color), ARGB32.blue(color), new float[3]);
      this.hueSlider = new ChromaTintingScreen.ColorSlider(0, 0, 200, 20, 720.0, hsb[0] * 720.0F, ChromaTintingScreen.ColorComponent.HUE, HUE_SLIDER_SPRITE);
      body.addChild(this.hueSlider);
      this.saturationSlider = new ChromaTintingScreen.ColorSlider(
         0, 0, 200, 20, 200.0, hsb[1] * 200.0F, ChromaTintingScreen.ColorComponent.SATURATION, SATURATION_SLIDER_SPRITE
      );
      body.addChild(this.saturationSlider);
      this.brightnessSlider = new ChromaTintingScreen.ColorSlider(
         0, 0, 200, 20, 200.0, hsb[2] * 200.0F, ChromaTintingScreen.ColorComponent.BRIGHTNESS, BRIGHTNESS_SLIDER_SPRITE
      );
      body.addChild(this.brightnessSlider);
      LinearLayout bottom = LinearLayout.horizontal().spacing(20);
      LinearLayout bottomLeft = LinearLayout.vertical().spacing(10);
      body.addChild(bottom);
      bottom.addChild(bottomLeft);
      LinearLayout hexDiv = LinearLayout.horizontal().spacing(2);
      bottomLeft.addChild(hexDiv);
      LinearLayout hexSymbol = LinearLayout.vertical();
      hexSymbol.defaultCellSetting().alignVerticallyMiddle().paddingTop(6);
      hexSymbol.addChild(new StringWidget(Component.literal("#"), this.font));
      hexDiv.addChild(hexSymbol);
      if (this.hexInput == null) {
         this.hexInput = new EditBox(this.font, 48, 20, Component.empty()) {
            public void insertText(@NotNull String textToWrite) {
               super.insertText(textToWrite.toUpperCase().replaceAll("(?![A-F]|[0-9])[\\s\\S]", ""));
            }

            public boolean charTyped(char codePoint, int modifiers) {
               codePoint = Character.toUpperCase(codePoint);
               return (codePoint < 'A' || codePoint > 'F') && (codePoint < '0' || codePoint > '9') ? false : super.charTyped(codePoint, modifiers);
            }

            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
               if (keyCode == 257) {
                  ChromaTintingScreen.this.updateColorFromHex();
                  ChromaTintingScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                  return true;
               } else {
                  return super.keyPressed(keyCode, scanCode, modifiers);
               }
            }
         };
         this.hexInput.setMaxLength(6);
         this.hexInput.setValue(ColorUtils.colorToHexString(color).substring(2));
      }

      hexDiv.addChild(this.hexInput);
      hexDiv.addChild(Button.builder(Component.translatable("screen.chromachine.apply_hex"), p_329727_ -> this.updateColorFromHex()).width(64).build());
      bottomLeft.addChild(Button.builder(Component.translatable("screen.chromachine.reset"), button -> {
         this.hexInput.setValue("FFFFFF");
         this.hueSlider.setValue(0.0);
         this.saturationSlider.setValue(0.0);
         this.brightnessSlider.setValue(200.0);
         PacketDistributor.sendToServer(new C2SResetItemTintPayload(), new CustomPacketPayload[0]);
         InfusedPropertiesHelper.removeProperty(this.affectedItem, AlchemancyProperties.TINTED);
      }).width(64).build());
      bottom.addChild(new ChromaTintingScreen.ItemDisplayWidget(48, this.affectedItem));
      this.layout.visitWidgets(x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      });
      this.layout.arrangeElements();
   }

   protected void updateColorFromHex() {
      int hexColor = ARGB32.color(255, this.hexInput.getValue().isEmpty() ? 16777215 : Integer.parseInt(this.hexInput.getValue(), 16));
      float[] hexHsb = Color.RGBtoHSB(ARGB32.red(hexColor), ARGB32.green(hexColor), ARGB32.blue(hexColor), new float[3]);
      this.hueSlider.setValue(hexHsb[0] * 720.0F);
      this.saturationSlider.setValue(hexHsb[1] * 200.0F);
      this.brightnessSlider.setValue(hexHsb[2] * 200.0F);
      this.hexInput.setValue(ColorUtils.colorToHexString(hexColor).substring(2));
      CommonUtils.applyChromaTint(this.affectedItem, hexColor);
   }

   protected float getHue() {
      return this.hueSlider.getValueForColor();
   }

   protected float getSaturation() {
      return this.saturationSlider.getValueForColor();
   }

   protected float getBrightness() {
      return this.brightnessSlider.getValueForColor();
   }

   protected int getColor() {
      return Color.HSBtoRGB(this.getHue(), this.getSaturation(), this.getBrightness());
   }

   public void onClose() {
      super.onClose();
      if (!Arrays.equals(this.originalTint, ((TintedProperty)AlchemancyProperties.TINTED.get()).getData(this.affectedItem))) {
         PacketDistributor.sendToServer(new C2SChromatizePayload(this.getColor()), new CustomPacketPayload[0]);
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      RenderSystem.enableBlend();
      guiGraphics.blit(Screen.INWORLD_HEADER_SEPARATOR, 0, this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      guiGraphics.blit(Screen.INWORLD_FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight(), 0.0F, 0.0F, this.width, 2, 32, 2);
      guiGraphics.blit(
         INWORLD_MENU_LIST_BACKGROUND,
         0,
         this.layout.getHeaderHeight(),
         this.layout.getWidth(),
         this.layout.getHeight() - this.layout.getFooterHeight(),
         this.width,
         this.height - (this.layout.getHeaderHeight() + this.layout.getFooterHeight()),
         32,
         32
      );
      RenderSystem.disableBlend();
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   public static enum ColorComponent {
      HUE,
      SATURATION,
      BRIGHTNESS;
   }

   public class ColorSlider extends ExtendedSlider {
      private static final ResourceLocation BACK_SPRITE = ResourceLocation.fromNamespaceAndPath("alchemancy", "chromatize/slider_back");
      private static final ResourceLocation HIGHLIGHT_SPRITE = ResourceLocation.fromNamespaceAndPath("alchemancy", "chromatize/slider_highlight");
      private static final ResourceLocation NEW_ENTRY_ICON = ResourceLocation.fromNamespaceAndPath("alchemancy", "infusion_codex/new_entry_icon");
      private final ChromaTintingScreen.ColorComponent component;
      private final ResourceLocation sliderSprite;

      public ColorSlider(
         int x, int y, int width, int height, double maxValue, double currentValue, ChromaTintingScreen.ColorComponent component, ResourceLocation sliderSprite
      ) {
         super(x, y, width, height, Component.empty(), Component.empty(), 0.0, maxValue, currentValue, false);
         this.component = component;
         this.sliderSprite = sliderSprite;
      }

      public float getValueForColor() {
         return (float)this.value;
      }

      public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         int color = Color.HSBtoRGB(
            ChromaTintingScreen.this.getHue(),
            this.component.ordinal() > ChromaTintingScreen.ColorComponent.SATURATION.ordinal() ? ChromaTintingScreen.this.getSaturation() : 1.0F,
            this.component.ordinal() > ChromaTintingScreen.ColorComponent.BRIGHTNESS.ordinal() ? ChromaTintingScreen.this.getBrightness() : 1.0F
         );
         Minecraft minecraft = Minecraft.getInstance();
         guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         guiGraphics.setColor(ARGB32.red(color) / 255.0F, ARGB32.green(color) / 255.0F, ARGB32.blue(color) / 255.0F, this.alpha);
         guiGraphics.blitSprite(BACK_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
         guiGraphics.blitSprite(this.sliderSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         if (this.isFocused()) {
            guiGraphics.blitSprite(HIGHLIGHT_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         }

         guiGraphics.blitSprite(this.getHandleSprite(), this.getX() + (int)(this.value * (this.width - 8)), this.getY(), 8, this.getHeight());
         guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         int i = this.active ? 16777215 : 10526880;
         this.renderScrollingString(guiGraphics, minecraft.font, 2, i | Mth.ceil(this.alpha * 255.0F) << 24);
      }

      protected void applyValue() {
         int color = ChromaTintingScreen.this.getColor();
         CommonUtils.applyChromaTint(ChromaTintingScreen.this.affectedItem, color);
         ChromaTintingScreen.this.hexInput.setValue(ColorUtils.colorToHexString(color).substring(2));
      }
   }

   public class ItemDisplayWidget extends AbstractWidget {
      private final ItemStack stack;

      public ItemDisplayWidget(int size, ItemStack stack) {
         this(size, size, stack);
      }

      public ItemDisplayWidget(int width, int height, ItemStack stack) {
         super(0, 0, width, height, stack.getDisplayName());
         this.stack = stack;
      }

      protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         PoseStack poseStack = guiGraphics.pose();
         float xs = this.getWidth() / 16.0F;
         float ys = this.getHeight() / 16.0F;
         poseStack.pushPose();
         poseStack.scale(xs, ys, 1.0F);
         guiGraphics.renderFakeItem(this.stack, (int)(this.getX() / xs), (int)(this.getY() / ys));
         poseStack.popPose();
         if (mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight()) {
            guiGraphics.renderTooltip(ChromaTintingScreen.this.font, this.stack, mouseX, mouseY);
         }
      }

      protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
      }
   }
}
