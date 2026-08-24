package snownee.jade.impl.config;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Style.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.HumanoidArm;
import snownee.jade.Jade;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.theme.Theme;
import snownee.jade.util.ClientProxy;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.JadeCodecs;
import snownee.jade.util.ModIdentification;

public class WailaConfig implements IWailaConfig {
   public static final Codec<WailaConfig> CODEC = RecordCodecBuilder.create(
      i -> i.group(
            WailaConfig.ConfigGeneral.CODEC
               .fieldOf("general")
               .orElseGet(() -> JadeCodecs.createFromEmptyMap(WailaConfig.ConfigGeneral.CODEC))
               .forGetter(WailaConfig::getGeneral),
            WailaConfig.ConfigOverlay.CODEC
               .fieldOf("overlay")
               .orElseGet(() -> JadeCodecs.createFromEmptyMap(WailaConfig.ConfigOverlay.CODEC))
               .forGetter(WailaConfig::getOverlay),
            WailaConfig.ConfigFormatting.CODEC
               .fieldOf("formatting")
               .orElseGet(() -> JadeCodecs.createFromEmptyMap(WailaConfig.ConfigFormatting.CODEC))
               .forGetter(WailaConfig::getFormatting),
            WailaConfig.ConfigHistory.CODEC
               .fieldOf("history")
               .orElseGet(() -> JadeCodecs.createFromEmptyMap(WailaConfig.ConfigHistory.CODEC))
               .forGetter(WailaConfig::getHistory)
         )
         .apply(i, WailaConfig::new)
   );
   private final WailaConfig.ConfigGeneral general;
   private final WailaConfig.ConfigOverlay overlay;
   private final WailaConfig.ConfigFormatting formatting;
   private final WailaConfig.ConfigHistory history;

   public WailaConfig(
      WailaConfig.ConfigGeneral general, WailaConfig.ConfigOverlay overlay, WailaConfig.ConfigFormatting formatting, WailaConfig.ConfigHistory history
   ) {
      this.general = general;
      this.overlay = overlay;
      this.formatting = formatting;
      this.history = history;
   }

   public WailaConfig.ConfigGeneral getGeneral() {
      return this.general;
   }

   public WailaConfig.ConfigOverlay getOverlay() {
      return this.overlay;
   }

   public WailaConfig.ConfigFormatting getFormatting() {
      return this.formatting;
   }

   public WailaConfig.ConfigHistory getHistory() {
      return this.history;
   }

   @Override
   public IPluginConfig getPlugin() {
      return PluginConfig.INSTANCE;
   }

   public static class ConfigFormatting implements IWailaConfig.IConfigFormatting {
      public static final Codec<WailaConfig.ConfigFormatting> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               Serializer.CODEC
                  .fieldOf("itemModNameStyle")
                  .orElseGet(() -> Style.EMPTY.applyFormats(new ChatFormatting[]{ChatFormatting.BLUE, ChatFormatting.ITALIC}))
                  .forGetter(WailaConfig.ConfigFormatting::getItemModNameStyle)
            )
            .apply(i, WailaConfig.ConfigFormatting::new)
      );
      private Style itemModNameStyle;

      public ConfigFormatting(Style itemModNameStyle) {
         this.itemModNameStyle = itemModNameStyle;
      }

      @Override
      public Style getItemModNameStyle() {
         return this.itemModNameStyle;
      }

      @Override
      public void setItemModNameStyle(Style itemModNameStyle) {
         this.itemModNameStyle = itemModNameStyle;
      }

      @Override
      public Component registryName(String name) {
         return Component.literal(name).withStyle(IThemeHelper.get().isLightColorScheme() ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY);
      }
   }

   public static class ConfigGeneral implements IWailaConfig.IConfigGeneral {
      public static final Codec<WailaConfig.ConfigGeneral> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               Codec.BOOL.fieldOf("previewOverlay").orElse(true).forGetter($ -> $.previewOverlay),
               Codec.BOOL.fieldOf("displayTooltip").orElse(true).forGetter(WailaConfig.ConfigGeneral::shouldDisplayTooltip),
               Codec.BOOL.fieldOf("displayBlocks").orElse(true).forGetter(WailaConfig.ConfigGeneral::getDisplayBlocks),
               Codec.BOOL.fieldOf("displayEntities").orElse(true).forGetter(WailaConfig.ConfigGeneral::getDisplayEntities),
               Codec.BOOL.fieldOf("displayBosses").orElse(true).forGetter(WailaConfig.ConfigGeneral::getDisplayBosses),
               StringRepresentable.fromEnum(IWailaConfig.DisplayMode::values)
                  .fieldOf("displayMode")
                  .orElse(IWailaConfig.DisplayMode.TOGGLE)
                  .forGetter(WailaConfig.ConfigGeneral::getDisplayMode),
               Codec.BOOL.fieldOf("enableTextToSpeech").orElse(false).forGetter(WailaConfig.ConfigGeneral::shouldEnableTextToSpeech),
               StringRepresentable.fromEnum(IWailaConfig.TTSMode::values)
                  .fieldOf("ttsMode")
                  .orElse(IWailaConfig.TTSMode.PRESS)
                  .forGetter(WailaConfig.ConfigGeneral::getTTSMode),
               StringRepresentable.fromEnum(IWailaConfig.FluidMode::values)
                  .fieldOf("fluidMode")
                  .orElse(IWailaConfig.FluidMode.ANY)
                  .forGetter(WailaConfig.ConfigGeneral::getDisplayFluids),
               StringRepresentable.fromEnum(IWailaConfig.PerspectiveMode::values)
                  .fieldOf("perspectiveMode")
                  .orElse(IWailaConfig.PerspectiveMode.CAMERA)
                  .forGetter(WailaConfig.ConfigGeneral::getPerspectiveMode),
               Codec.floatRange(0.0F, 20.0F).fieldOf("extendedReach").orElse(0.0F).forGetter(WailaConfig.ConfigGeneral::getExtendedReach),
               Codec.BOOL.fieldOf("debug").orElse(false).forGetter(WailaConfig.ConfigGeneral::isDebug),
               Codec.BOOL.fieldOf("itemModNameTooltip").orElse(true).forGetter(WailaConfig.ConfigGeneral::showItemModNameTooltip),
               StringRepresentable.fromEnum(IWailaConfig.BossBarOverlapMode::values)
                  .fieldOf("bossBarOverlapMode")
                  .orElse(IWailaConfig.BossBarOverlapMode.PUSH_DOWN)
                  .forGetter(WailaConfig.ConfigGeneral::getBossBarOverlapMode),
               Codec.BOOL.fieldOf("builtinCamouflage").orElse(true).forGetter(WailaConfig.ConfigGeneral::getBuiltinCamouflage),
               WailaConfig.ConfigGeneral.ExtraOptions.CODEC
                  .orElseGet(() -> JadeCodecs.createFromEmptyMap(WailaConfig.ConfigGeneral.ExtraOptions.CODEC.codec()))
                  .forGetter($ -> $.extraOptions)
            )
            .apply(i, WailaConfig.ConfigGeneral::new)
      );
      public static final List<String> itemModNameTooltipDisabledByMods = Lists.newArrayList(new String[]{"emi"});
      public boolean previewOverlay;
      private boolean displayTooltip;
      private boolean displayBlocks;
      private boolean displayEntities;
      private boolean displayBosses;
      private IWailaConfig.DisplayMode displayMode;
      private boolean enableTextToSpeech;
      private IWailaConfig.TTSMode ttsMode;
      private IWailaConfig.FluidMode fluidMode;
      private float extendedReach;
      private boolean debug;
      private boolean itemModNameTooltip;
      private IWailaConfig.BossBarOverlapMode bossBarOverlapMode;
      private boolean builtinCamouflage;
      private IWailaConfig.PerspectiveMode perspectiveMode;
      private final WailaConfig.ConfigGeneral.ExtraOptions extraOptions;

      public ConfigGeneral(
         boolean previewOverlay,
         boolean displayTooltip,
         boolean displayBlocks,
         boolean displayEntities,
         boolean displayBosses,
         IWailaConfig.DisplayMode displayMode,
         boolean enableTextToSpeech,
         IWailaConfig.TTSMode ttsMode,
         IWailaConfig.FluidMode fluidMode,
         IWailaConfig.PerspectiveMode perspectiveMode,
         float extendedReach,
         boolean debug,
         boolean itemModNameTooltip,
         IWailaConfig.BossBarOverlapMode bossBarOverlapMode,
         boolean builtinCamouflage,
         WailaConfig.ConfigGeneral.ExtraOptions extraOptions
      ) {
         this.previewOverlay = previewOverlay;
         this.displayTooltip = displayTooltip;
         this.displayBlocks = displayBlocks;
         this.displayEntities = displayEntities;
         this.displayBosses = displayBosses;
         this.displayMode = displayMode;
         this.enableTextToSpeech = enableTextToSpeech;
         this.ttsMode = ttsMode;
         this.fluidMode = fluidMode;
         this.perspectiveMode = perspectiveMode;
         this.extendedReach = extendedReach;
         this.debug = debug;
         this.itemModNameTooltip = itemModNameTooltip;
         this.bossBarOverlapMode = bossBarOverlapMode;
         this.builtinCamouflage = builtinCamouflage;
         this.extraOptions = extraOptions;
      }

      public static void init() {
         List<String> names = itemModNameTooltipDisabledByMods.stream()
            .filter(CommonProxy::isModLoaded)
            .map($ -> ModIdentification.getModName($).orElse($))
            .toList();
         itemModNameTooltipDisabledByMods.clear();
         itemModNameTooltipDisabledByMods.addAll(names);
         IWailaConfig.IConfigGeneral config = IWailaConfig.get().getGeneral();
         boolean hasAccessibilityMod = ClientProxy.hasAccessibilityMod();
         if (config.getAccessibilityModMemory() != hasAccessibilityMod) {
            config.setAccessibilityModMemory(hasAccessibilityMod);
            config.setEnableAccessibilityPlugin(hasAccessibilityMod);
            Jade.CONFIG.save();
         }
      }

      @Override
      public void setDisplayTooltip(boolean displayTooltip) {
         this.displayTooltip = displayTooltip;
      }

      @Override
      public boolean getDisplayEntities() {
         return this.displayEntities;
      }

      @Override
      public void setDisplayEntities(boolean displayEntities) {
         this.displayEntities = displayEntities;
      }

      @Override
      public boolean getDisplayBlocks() {
         return this.displayBlocks;
      }

      @Override
      public void setDisplayBlocks(boolean displayBlocks) {
         this.displayBlocks = displayBlocks;
      }

      @Override
      public void toggleTTS() {
         this.enableTextToSpeech = !this.enableTextToSpeech;
      }

      @Override
      public boolean shouldDisplayTooltip() {
         return this.displayTooltip;
      }

      @Override
      public IWailaConfig.DisplayMode getDisplayMode() {
         return this.displayMode;
      }

      @Override
      public void setDisplayMode(IWailaConfig.DisplayMode displayMode) {
         this.displayMode = displayMode;
      }

      @Override
      public boolean shouldEnableTextToSpeech() {
         return this.ttsMode == IWailaConfig.TTSMode.TOGGLE && this.enableTextToSpeech;
      }

      @Override
      public IWailaConfig.TTSMode getTTSMode() {
         return this.ttsMode;
      }

      @Override
      public void setTTSMode(IWailaConfig.TTSMode ttsMode) {
         this.ttsMode = ttsMode;
      }

      @Override
      public boolean shouldDisplayFluids() {
         return this.fluidMode != IWailaConfig.FluidMode.NONE;
      }

      @Override
      public IWailaConfig.FluidMode getDisplayFluids() {
         return this.fluidMode;
      }

      @Override
      public void setDisplayFluids(boolean displayFluids) {
         this.fluidMode = displayFluids ? IWailaConfig.FluidMode.ANY : IWailaConfig.FluidMode.NONE;
      }

      @Override
      public void setDisplayFluids(IWailaConfig.FluidMode displayFluids) {
         this.fluidMode = displayFluids;
      }

      @Override
      public float getExtendedReach() {
         return this.extendedReach;
      }

      @Override
      public void setExtendedReach(float extendedReach) {
         this.extendedReach = Mth.clamp(extendedReach, 0.0F, 20.0F);
      }

      @Override
      public boolean isDebug() {
         return this.debug;
      }

      @Override
      public void setDebug(boolean debug) {
         this.debug = debug;
      }

      @Override
      public void setItemModNameTooltip(boolean itemModNameTooltip) {
         this.itemModNameTooltip = itemModNameTooltip;
      }

      @Override
      public boolean showItemModNameTooltip() {
         return this.itemModNameTooltip && itemModNameTooltipDisabledByMods.isEmpty();
      }

      @Override
      public IWailaConfig.BossBarOverlapMode getBossBarOverlapMode() {
         return this.bossBarOverlapMode;
      }

      @Override
      public void setBossBarOverlapMode(IWailaConfig.BossBarOverlapMode mode) {
         this.bossBarOverlapMode = mode;
      }

      @Override
      public void setHideFromTabList(boolean hideFromTabList) {
         this.extraOptions.setHideFromTabList(hideFromTabList);
      }

      @Override
      public void setHideFromGUIs(boolean hideFromGUIs) {
         this.extraOptions.setHideFromGUIs(hideFromGUIs);
      }

      @Override
      public boolean shouldHideFromTabList() {
         return this.extraOptions.hideFromTabList();
      }

      @Override
      public boolean shouldHideFromGUIs() {
         return this.extraOptions.hideFromGUIs();
      }

      @Override
      public boolean getDisplayBosses() {
         return this.displayBosses;
      }

      @Override
      public void setDisplayBosses(boolean displayBosses) {
         this.displayBosses = displayBosses;
      }

      @Override
      public boolean getBuiltinCamouflage() {
         return this.builtinCamouflage;
      }

      @Override
      public void setBuiltinCamouflage(boolean builtinCamouflage) {
         this.builtinCamouflage = builtinCamouflage;
      }

      @Override
      public boolean getAccessibilityModMemory() {
         return this.extraOptions.accessibilityModMemory();
      }

      @Override
      public void setAccessibilityModMemory(boolean accessibilityModMemory) {
         this.extraOptions.setAccessibilityModMemory(accessibilityModMemory);
      }

      @Override
      public boolean getEnableAccessibilityPlugin() {
         return this.extraOptions.enableAccessibilityPlugin();
      }

      @Override
      public void setEnableAccessibilityPlugin(boolean enableAccessibilityPlugin) {
         this.extraOptions.setEnableAccessibilityPlugin(enableAccessibilityPlugin);
      }

      @Override
      public IWailaConfig.PerspectiveMode getPerspectiveMode() {
         return this.perspectiveMode;
      }

      @Override
      public void setPerspectiveMode(IWailaConfig.PerspectiveMode perspectiveMode) {
         this.perspectiveMode = perspectiveMode;
      }

      public static final class ExtraOptions {
         public static final MapCodec<WailaConfig.ConfigGeneral.ExtraOptions> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                  Codec.BOOL.fieldOf("hideFromTabList").orElse(true).forGetter(WailaConfig.ConfigGeneral.ExtraOptions::hideFromTabList),
                  Codec.BOOL.fieldOf("hideFromGUIs").orElse(true).forGetter(WailaConfig.ConfigGeneral.ExtraOptions::hideFromGUIs),
                  Codec.BOOL.fieldOf("accessibilityModMemory").orElse(false).forGetter(WailaConfig.ConfigGeneral.ExtraOptions::accessibilityModMemory),
                  Codec.BOOL.fieldOf("enableAccessibilityPlugin").orElse(false).forGetter(WailaConfig.ConfigGeneral.ExtraOptions::enableAccessibilityPlugin)
               )
               .apply(i, WailaConfig.ConfigGeneral.ExtraOptions::new)
         );
         private boolean hideFromTabList;
         private boolean hideFromGUIs;
         public boolean accessibilityModMemory;
         public boolean enableAccessibilityPlugin;

         public ExtraOptions(boolean hideFromTabList, boolean hideFromGUIs, boolean accessibilityModMemory, boolean enableAccessibilityPlugin) {
            this.hideFromTabList = hideFromTabList;
            this.hideFromGUIs = hideFromGUIs;
            this.accessibilityModMemory = accessibilityModMemory;
            this.enableAccessibilityPlugin = enableAccessibilityPlugin;
         }

         public boolean hideFromTabList() {
            return this.hideFromTabList;
         }

         public boolean hideFromGUIs() {
            return this.hideFromGUIs;
         }

         public boolean accessibilityModMemory() {
            return this.accessibilityModMemory;
         }

         public boolean enableAccessibilityPlugin() {
            return this.enableAccessibilityPlugin;
         }

         public void setHideFromTabList(boolean hideFromTabList) {
            this.hideFromTabList = hideFromTabList;
         }

         public void setHideFromGUIs(boolean hideFromGUIs) {
            this.hideFromGUIs = hideFromGUIs;
         }

         public void setAccessibilityModMemory(boolean accessibilityModMemory) {
            this.accessibilityModMemory = accessibilityModMemory;
         }

         public void setEnableAccessibilityPlugin(boolean enableAccessibilityPlugin) {
            this.enableAccessibilityPlugin = enableAccessibilityPlugin;
         }
      }
   }

   public static class ConfigHistory {
      public static final Codec<WailaConfig.ConfigHistory> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               Codec.BOOL.optionalFieldOf("hintOverlayToggle", true).forGetter($ -> $.hintOverlayToggle),
               Codec.BOOL.optionalFieldOf("hintNarratorToggle", true).forGetter($ -> $.hintNarratorToggle),
               Codec.INT.optionalFieldOf("themesHash", 0).forGetter($ -> $.themesHash)
            )
            .apply(i, WailaConfig.ConfigHistory::new)
      );
      public boolean hintOverlayToggle;
      public boolean hintNarratorToggle;
      public int themesHash;

      public ConfigHistory(boolean hintOverlayToggle, boolean hintNarratorToggle, int themesHash) {
         this.hintOverlayToggle = hintOverlayToggle;
         this.hintNarratorToggle = hintNarratorToggle;
         this.themesHash = themesHash;
      }
   }

   public static class ConfigOverlay implements IWailaConfig.IConfigOverlay {
      public static final Codec<WailaConfig.ConfigOverlay> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               ResourceLocation.CODEC.fieldOf("activeTheme").orElse(Theme.DEFAULT_THEME_ID).forGetter($ -> $.activeTheme),
               Codec.FLOAT.fieldOf("overlayPosX").orElse(0.5F).forGetter(WailaConfig.ConfigOverlay::getOverlayPosX),
               Codec.FLOAT.fieldOf("overlayPosY").orElse(1.0F).forGetter(WailaConfig.ConfigOverlay::getOverlayPosY),
               Codec.floatRange(0.2F, 2.0F).fieldOf("overlayScale").orElse(1.0F).forGetter(WailaConfig.ConfigOverlay::getOverlayScale),
               Codec.FLOAT.fieldOf("overlayAnchorX").orElse(0.5F).forGetter(WailaConfig.ConfigOverlay::getAnchorX),
               Codec.FLOAT.fieldOf("overlayAnchorY").orElse(0.0F).forGetter(WailaConfig.ConfigOverlay::getAnchorY),
               Codec.BOOL.fieldOf("overlaySquare").orElse(false).forGetter(WailaConfig.ConfigOverlay::getSquare),
               Codec.BOOL.fieldOf("flipMainHand").orElse(false).forGetter(WailaConfig.ConfigOverlay::getFlipMainHand),
               Codec.floatRange(0.0F, 1.0F).fieldOf("autoScaleThreshold").orElse(0.4F).forGetter(WailaConfig.ConfigOverlay::getAutoScaleThreshold),
               Codec.floatRange(0.0F, 1.0F).fieldOf("alpha").orElse(0.7F).forGetter(WailaConfig.ConfigOverlay::getAlpha),
               StringRepresentable.fromEnum(IWailaConfig.IconMode::values)
                  .fieldOf("iconMode")
                  .orElse(IWailaConfig.IconMode.TOP)
                  .forGetter(WailaConfig.ConfigOverlay::getIconMode),
               Codec.BOOL.fieldOf("animation").orElse(true).forGetter(WailaConfig.ConfigOverlay::getAnimation),
               Codec.floatRange(0.0F, 3.4028235E38F).fieldOf("disappearingDelay").orElse(0.0F).forGetter(WailaConfig.ConfigOverlay::getDisappearingDelay)
            )
            .apply(i, WailaConfig.ConfigOverlay::new)
      );
      public ResourceLocation activeTheme;
      private float overlayPosX;
      private float overlayPosY;
      private float overlayScale;
      private float overlayAnchorX;
      private float overlayAnchorY;
      private boolean overlaySquare;
      private boolean flipMainHand;
      private float autoScaleThreshold;
      private float alpha;
      private transient Theme activeThemeInstance;
      private IWailaConfig.IconMode iconMode;
      private boolean animation;
      private float disappearingDelay;

      public ConfigOverlay(
         ResourceLocation activeTheme,
         float overlayPosX,
         float overlayPosY,
         float overlayScale,
         float overlayAnchorX,
         float overlayAnchorY,
         boolean overlaySquare,
         boolean flipMainHand,
         float autoScaleThreshold,
         float alpha,
         IWailaConfig.IconMode iconMode,
         boolean animation,
         float disappearingDelay
      ) {
         this.activeTheme = activeTheme;
         this.overlayPosX = overlayPosX;
         this.overlayPosY = overlayPosY;
         this.overlayScale = overlayScale;
         this.overlayAnchorX = overlayAnchorX;
         this.overlayAnchorY = overlayAnchorY;
         this.overlaySquare = overlaySquare;
         this.flipMainHand = flipMainHand;
         this.autoScaleThreshold = autoScaleThreshold;
         this.alpha = alpha;
         this.iconMode = iconMode;
         this.animation = animation;
         this.disappearingDelay = disappearingDelay;
      }

      @Override
      public float getOverlayPosX() {
         return Mth.clamp(this.overlayPosX, 0.0F, 1.0F);
      }

      @Override
      public void setOverlayPosX(float overlayPosX) {
         this.overlayPosX = Mth.clamp(overlayPosX, 0.0F, 1.0F);
      }

      @Override
      public float getOverlayPosY() {
         return Mth.clamp(this.overlayPosY, 0.0F, 1.0F);
      }

      @Override
      public void setOverlayPosY(float overlayPosY) {
         this.overlayPosY = Mth.clamp(overlayPosY, 0.0F, 1.0F);
      }

      @Override
      public float getOverlayScale() {
         return this.overlayScale;
      }

      @Override
      public void setOverlayScale(float overlayScale) {
         this.overlayScale = Mth.clamp(overlayScale, 0.2F, 2.0F);
      }

      @Override
      public float getAnchorX() {
         return Mth.clamp(this.overlayAnchorX, 0.0F, 1.0F);
      }

      @Override
      public void setAnchorX(float overlayAnchorX) {
         this.overlayAnchorX = Mth.clamp(overlayAnchorX, 0.0F, 1.0F);
      }

      @Override
      public float getAnchorY() {
         return Mth.clamp(this.overlayAnchorY, 0.0F, 1.0F);
      }

      @Override
      public void setAnchorY(float overlayAnchorY) {
         this.overlayAnchorY = Mth.clamp(overlayAnchorY, 0.0F, 1.0F);
      }

      @Override
      public boolean getFlipMainHand() {
         return this.flipMainHand;
      }

      @Override
      public void setFlipMainHand(boolean overlaySquare) {
         this.flipMainHand = overlaySquare;
      }

      @Override
      public float tryFlip(float f) {
         if (this.flipMainHand && Minecraft.getInstance().options.mainHand().get() == HumanoidArm.LEFT) {
            f = 1.0F - f;
         }

         return f;
      }

      @Override
      public boolean getSquare() {
         return this.overlaySquare;
      }

      @Override
      public void setSquare(boolean overlaySquare) {
         this.overlaySquare = overlaySquare;
      }

      @Override
      public float getAutoScaleThreshold() {
         return this.autoScaleThreshold;
      }

      @Override
      public float getAlpha() {
         return this.alpha;
      }

      @Override
      public void setAlpha(float alpha) {
         this.alpha = Mth.clamp(alpha, 0.0F, 1.0F);
      }

      @Override
      public Theme getTheme() {
         if (this.activeThemeInstance == null) {
            this.applyTheme(this.activeTheme);
         }

         return this.activeThemeInstance;
      }

      @Override
      public void applyTheme(ResourceLocation id) {
         this.activeThemeInstance = IThemeHelper.get().getTheme(id);
         this.activeTheme = this.activeThemeInstance.id;
      }

      @Override
      public IWailaConfig.IconMode getIconMode() {
         return this.iconMode;
      }

      @Override
      public void setIconMode(IWailaConfig.IconMode iconMode) {
         this.iconMode = iconMode;
      }

      @Override
      public boolean shouldShowIcon() {
         return this.iconMode != IWailaConfig.IconMode.HIDE;
      }

      @Override
      public boolean getAnimation() {
         return this.animation;
      }

      @Override
      public void setAnimation(boolean animation) {
         this.animation = animation;
      }

      @Override
      public float getDisappearingDelay() {
         return this.disappearingDelay;
      }

      @Override
      public void setDisappearingDelay(float delay) {
         this.disappearingDelay = delay;
      }
   }
}
