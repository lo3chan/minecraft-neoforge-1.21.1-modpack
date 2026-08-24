package snownee.jade.api.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext.Fluid;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import snownee.jade.JadeInternals;
import snownee.jade.api.SimpleStringRepresentable;
import snownee.jade.api.theme.Theme;

@NonExtendable
public interface IWailaConfig {
   static IWailaConfig get() {
      return JadeInternals.getWailaConfig();
   }

   IWailaConfig.IConfigGeneral getGeneral();

   IWailaConfig.IConfigOverlay getOverlay();

   IWailaConfig.IConfigFormatting getFormatting();

   IPluginConfig getPlugin();

   public static enum BossBarOverlapMode implements SimpleStringRepresentable {
      NO_OPERATION,
      HIDE_BOSS_BAR,
      HIDE_TOOLTIP,
      PUSH_DOWN;
   }

   public static enum DisplayMode implements SimpleStringRepresentable {
      HOLD_KEY,
      TOGGLE,
      LITE;
   }

   public static enum FluidMode implements SimpleStringRepresentable {
      NONE(Fluid.NONE),
      ANY(Fluid.ANY),
      SOURCE_ONLY(Fluid.SOURCE_ONLY),
      FALLBACK(Fluid.NONE);

      public final Fluid ctx;

      private FluidMode(Fluid ctx) {
         this.ctx = ctx;
      }
   }

   public static enum HandlerDisplayStyle implements SimpleStringRepresentable {
      PROGRESS_BAR,
      ICON,
      PLAIN_TEXT;
   }

   @NonExtendable
   public interface IConfigFormatting {
      Style getItemModNameStyle();

      void setItemModNameStyle(Style var1);

      Component registryName(String var1);
   }

   @NonExtendable
   public interface IConfigGeneral {
      void setDisplayTooltip(boolean var1);

      boolean getDisplayEntities();

      void setDisplayEntities(boolean var1);

      boolean getDisplayBosses();

      void setDisplayBosses(boolean var1);

      boolean getDisplayBlocks();

      void setDisplayBlocks(boolean var1);

      void setHideFromTabList(boolean var1);

      void setHideFromGUIs(boolean var1);

      void toggleTTS();

      void setItemModNameTooltip(boolean var1);

      boolean shouldDisplayTooltip();

      IWailaConfig.DisplayMode getDisplayMode();

      void setDisplayMode(IWailaConfig.DisplayMode var1);

      boolean shouldHideFromTabList();

      boolean shouldHideFromGUIs();

      boolean shouldEnableTextToSpeech();

      IWailaConfig.TTSMode getTTSMode();

      void setTTSMode(IWailaConfig.TTSMode var1);

      boolean shouldDisplayFluids();

      IWailaConfig.FluidMode getDisplayFluids();

      void setDisplayFluids(boolean var1);

      void setDisplayFluids(IWailaConfig.FluidMode var1);

      boolean showItemModNameTooltip();

      float getExtendedReach();

      void setExtendedReach(float var1);

      IWailaConfig.BossBarOverlapMode getBossBarOverlapMode();

      void setBossBarOverlapMode(IWailaConfig.BossBarOverlapMode var1);

      boolean isDebug();

      void setDebug(boolean var1);

      boolean getBuiltinCamouflage();

      void setBuiltinCamouflage(boolean var1);

      boolean getAccessibilityModMemory();

      void setAccessibilityModMemory(boolean var1);

      boolean getEnableAccessibilityPlugin();

      void setEnableAccessibilityPlugin(boolean var1);

      IWailaConfig.PerspectiveMode getPerspectiveMode();

      void setPerspectiveMode(IWailaConfig.PerspectiveMode var1);
   }

   @NonExtendable
   public interface IConfigOverlay {
      static int applyAlpha(int color, float alpha) {
         if (alpha == 0.0F) {
            return 0;
         } else {
            int prevAlphaChannel = color >> 24 & 0xFF;
            if (prevAlphaChannel > 0) {
               alpha *= prevAlphaChannel / 256.0F;
            }

            int alphaChannel = Mth.clamp((int)(255.0F * alpha), 4, 255);
            return color & 16777215 | alphaChannel << 24;
         }
      }

      float getOverlayPosX();

      void setOverlayPosX(float var1);

      float getOverlayPosY();

      void setOverlayPosY(float var1);

      float getOverlayScale();

      void setOverlayScale(float var1);

      float getAnchorX();

      void setAnchorX(float var1);

      float getAnchorY();

      void setAnchorY(float var1);

      boolean getFlipMainHand();

      void setFlipMainHand(boolean var1);

      float tryFlip(float var1);

      boolean getSquare();

      void setSquare(boolean var1);

      float getAutoScaleThreshold();

      float getAlpha();

      void setAlpha(float var1);

      Theme getTheme();

      void applyTheme(ResourceLocation var1);

      boolean shouldShowIcon();

      IWailaConfig.IconMode getIconMode();

      void setIconMode(IWailaConfig.IconMode var1);

      boolean getAnimation();

      void setAnimation(boolean var1);

      float getDisappearingDelay();

      void setDisappearingDelay(float var1);
   }

   public static enum IconMode implements SimpleStringRepresentable {
      TOP,
      CENTERED,
      INLINE,
      HIDE;
   }

   public static enum PerspectiveMode implements SimpleStringRepresentable {
      CAMERA,
      EYE;
   }

   public static enum TTSMode implements SimpleStringRepresentable {
      TOGGLE,
      PRESS;
   }
}
