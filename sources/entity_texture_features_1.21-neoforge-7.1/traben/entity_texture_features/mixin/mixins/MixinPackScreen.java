package traben.entity_texture_features.mixin.mixins;

import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin({PackSelectionScreen.class})
public abstract class MixinPackScreen extends Screen {
   @Unique
   private static final ResourceLocation etf$FOCUSED = ETFUtils2.res("entity_features", "textures/gui/settings_focused.png");
   @Unique
   private static final ResourceLocation etf$UNFOCUSED = ETFUtils2.res("entity_features", "textures/gui/settings_unfocused.png");
   @Shadow
   @Final
   private Path packDir;
   @Shadow
   private Button doneButton;
   @Unique
   private ImageButton etf$button = null;

   protected MixinPackScreen(Component title) {
      super(title);
   }

   @Inject(
      method = {"repositionElements"},
      at = {@At("TAIL")}
   )
   private void etf$etfButtonResize(CallbackInfo ci) {
      if (this.etf$button != null) {
         int[] vals = this.etf$etfButtonReSize();
         if (vals != null) {
            int x = vals[0];
            int y = vals[1];
            this.etf$button.setX(x);
            this.etf$button.setY(y);
         }
      }
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   private void etf$etfButton(CallbackInfo ci) {
      if (ETF.config().getConfig().configButtonLoc != ETFConfig.SettingsButtonLocation.OFF) {
         if (this.minecraft != null && this.packDir.equals(this.minecraft.getResourcePackDirectory()) && ETF.isFabric() == ETF.FABRIC_API) {
            int[] vals = this.etf$etfButtonReSize();
            if (vals != null) {
               int x = vals[0];
               int y = vals[1];
               this.etf$button = (ImageButton)this.addRenderableWidget(
                  new ImageButton(
                     x,
                     y,
                     24,
                     20,
                     new WidgetSprites(etf$UNFOCUSED, etf$FOCUSED),
                     button -> Objects.requireNonNull(this.minecraft).setScreen(new ETFConfigScreenMain(this)),
                     Component.nullToEmpty("")
                  ) {
                     {
                        this.setTooltip(Tooltip.create(ETF.getTextFromTranslation("config.entity_features.button_tooltip")));
                     }

                     public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
                        ResourceLocation identifier = this.isHoveredOrFocused() ? MixinPackScreen.etf$FOCUSED : MixinPackScreen.etf$UNFOCUSED;
                        context.blit(identifier, this.getX(), this.getY(), 0.0F, 0.0F, this.width, this.height, this.width, this.height);
                     }
                  }
               );
            }
         }
      }
   }

   @Unique
   @Nullable
   private int[] etf$etfButtonReSize() {
      int x;
      int y;
      switch (ETF.config().getConfig().configButtonLoc) {
         case BOTTOM_RIGHT:
            x = this.doneButton.getX() + this.doneButton.getWidth() + 8;
            y = this.doneButton.getY();
            break;
         case BOTTOM_LEFT: {
            int middle = this.width / 2;
            int bottomRight = this.doneButton.getX() + this.doneButton.getWidth() + 8;
            int offset = bottomRight - middle;
            x = middle - offset - 24;
            y = this.doneButton.getY();
            break;
         }
         case TOP_RIGHT:
            x = this.doneButton.getX() + this.doneButton.getWidth() + 8;
            y = this.height - this.doneButton.getY() - this.doneButton.getHeight();
            break;
         case TOP_LEFT: {
            int middle = this.width / 2;
            int bottomRight = this.doneButton.getX() + this.doneButton.getWidth() + 8;
            int offset = bottomRight - middle;
            x = middle - offset - 24;
            y = this.height - this.doneButton.getY() - this.doneButton.getHeight();
            break;
         }
         default:
            return null;
      }

      return new int[]{x, y};
   }
}
