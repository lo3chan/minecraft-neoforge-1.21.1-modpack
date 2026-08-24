package net.diebuddies.physics.settings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.ResourceManager;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.animation.AnimationSettingsScreen;
import net.diebuddies.physics.settings.blocks.BlockSettingsScreen;
import net.diebuddies.physics.settings.cloth.ClothSettingsScreen;
import net.diebuddies.physics.settings.mobs.MobSettingsScreen;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BarRenderer;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.diebuddies.physics.settings.ux.ButtonRenderer;
import net.diebuddies.physics.settings.ux.FocusSelector;
import net.diebuddies.physics.settings.ux.GUIResources;
import net.diebuddies.physics.settings.ux.ImageElement;
import net.diebuddies.physics.settings.ux.MainToolTipRenderer;
import net.diebuddies.physics.settings.ux.MouseParallaxAnimator;
import net.diebuddies.physics.settings.ux.ParallaxBackground;
import net.diebuddies.physics.settings.ux.Parallaxes;
import net.diebuddies.physics.settings.ux.ScrollAnimator;
import net.diebuddies.physics.settings.ux.TextAlignment;
import net.diebuddies.physics.settings.ux.TooltipRenderer;
import net.diebuddies.physics.settings.ux.WidthAnimator;
import net.diebuddies.physics.settings.vines.VineSettingsScreen;
import net.diebuddies.render.MainRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Math;

public class PhysicsSettingsScreen extends Screen {
   private static final int startingOffset = 17;
   private static final int offset = 30;
   private static final int buttonWidth = 120;
   private static final float widthMultiplier = 0.6F;
   private static final float widthSpeed = 0.5F;
   private static final int backButtonWidth = 70;
   private final Screen parent;
   private ScrollAnimator scrollAnimator;
   private FocusSelector focusSelector;
   private boolean renderedTooltip;
   private Button back;
   private Button supportDevelopment;
   private Button updateAvailable;
   private ResourceManager resourceManager;
   private ParallaxBackground parallax;

   public PhysicsSettingsScreen(Screen parent) {
      super(Component.translatable("physicsmod.menu.physics.title"));
      this.parent = parent;
      this.resourceManager = new ResourceManager();
      ConfigClient.firstStartup = false;
      MainRenderer.getParallaxShader();
   }

   protected void init() {
      this.focusSelector = new FocusSelector();
      this.addRenderableOnly(this.parallax = new ParallaxBackground(0.0F, 0.0F, this.width, this.height, this.focusSelector));
      int buttonHeight = 24;
      int count = 0;
      this.scrollAnimator = new ScrollAnimator(0.0F, 0.0F, 60.0F, this.height);
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.general.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new GeneralSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.general.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_GENERAL_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_GENERAL_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.114F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_GENERAL_LAYER_2, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.14F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_GENERAL_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.16F, false))
         )
      );
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.mobs.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new MobSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.mobs.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_MOBS_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_MOBS_MIDDLE, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_MOBS_FRONT, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.16F, false))
         )
      );
      Button blocks = (Button)this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.blocks.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new BlockSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.blocks.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(new ImageElement(GUIResources.PARALLAX_BLOCKS_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(new ImageElement(GUIResources.PARALLAX_BLOCKS_CLOUDS, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.103F, false)),
            (ImageElement)((Animatable)(new ImageElement(GUIResources.PARALLAX_BLOCKS_RUBBLE, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.12F, false))
         )
      );
      this.parallax.addImageElements(((Animatable)blocks).getAnimator(Parallaxes.class).elements);
      Button oceanButton = (Button)this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.ocean.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new OceanSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.ocean.title.main.info"),
            Component.translatable("physicsmod.menu.physics.ocean.error"),
            true,
            (ImageElement)((Animatable)(new ImageElement(GUIResources.PARALLAX_OCEAN_OLD, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.117F, false)),
            (ImageElement)((Animatable)new ImageElement(GUIResources.PARALLAX_OCEAN_NEW, 0.0F, 0.0F, this.width, this.height, true)
                  .setShader(MainRenderer.getParallaxSlideShader()))
               .addAnimator(new MouseParallaxAnimator(0.117F, false))
         )
      );
      if (oceanButton.active) {
         oceanButton.active = !StarterClient.immersivePortals;
      }

      Button dynamicButton = (Button)this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.dynamicblocks.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new VineSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.dynamicblocks.title.main.info"),
            Component.translatable("physicsmod.menu.physics.dynamicblocks.error"),
            true,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_DYNAMIC_BLOCKS_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_DYNAMIC_BLOCKS_MIDDLE, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_DYNAMIC_BLOCKS_FRONT, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.14F, false))
         )
      );
      if (dynamicButton.active) {
         dynamicButton.active = !StarterClient.immersivePortals;
      }

      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.cloth.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new ClothSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.cloth.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_CLOTH_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_CLOTH_MIDDLE, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.117F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_CLOTH_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.13F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_CLOTH_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.15F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_CLOTH_LAYER_2, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.185F, false))
         )
      );
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.items.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new ProjectileSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.items.title.main.info"),
            null,
            true,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_PROJECTILES_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_PROJECTILES_MIDDLE, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_PROJECTILES_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.13F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_PROJECTILES_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.16F, false))
         )
      );
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.liquid.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new LiquidSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.liquid.title.main.info"),
            null,
            true,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_LIQUID_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_LIQUID_LAYER_2, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.114F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_LIQUID_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.14F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_LIQUID_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.16F, false))
         )
      );
      Button snowButton = (Button)this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.snow.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new SnowSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.snow.title.main.info"),
            Component.translatable("physicsmod.menu.physics.snow.error"),
            true,
            (ImageElement)((Animatable)(new ImageElement(GUIResources.PARALLAX_SNOW_OLD, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.117F, false)),
            (ImageElement)((Animatable)new ImageElement(GUIResources.PARALLAX_SNOW_NEW, 0.0F, 0.0F, this.width, this.height, true)
                  .setShader(MainRenderer.getParallaxSlideShader()))
               .addAnimator(new MouseParallaxAnimator(0.117F, false))
         )
      );
      if (snowButton.active) {
         snowButton.active = !StarterClient.immersivePortals;
      }

      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.sound.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new SoundSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.sound.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_SOUND_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_SOUND_LAYER_2, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_SOUND_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.14F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_SOUND_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.16F, false))
         )
      );
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.animation.settings.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new AnimationSettingsScreen(this),
            Component.translatable("physicsmod.menu.animation.settings.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_ANIMATIONS_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_ANIMATIONS_MIDDLE, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_ANIMATIONS_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_ANIMATIONS_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.13F, false))
         )
      );
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.smoke.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new SmokeSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.smoke.title.main.info"),
            null,
            true,
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_SMOKE, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.114F, false))
         )
      );
      this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.weather.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new WeatherSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.weather.title.main.info"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_WEATHER_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_WEATHER_LAYER_0, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.123F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_WEATHER_LAYER_1, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.14F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_WEATHER_LAYER_2, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.17F, false))
         )
      );
      Button serverButton = (Button)this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.collapse.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> new ServerSettingsScreen(this, this.minecraft.options),
            Component.translatable("physicsmod.menu.collapse.title.main.info"),
            Component.translatable("physicsmod.menu.physics.collapse.error"),
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_COLLAPSE_BLOCKS_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_COLLAPSE_BLOCKS_MIDDLE, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.12F, false)),
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_COLLAPSE_BLOCKS_FRONT, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.14F, false))
         )
      );
      if (serverButton.active) {
         serverButton.active = Minecraft.getInstance().hasSingleplayerServer() || Minecraft.getInstance().getCurrentServer() == null;
      }

      Button credits = (Button)this.addRenderableWidget(
         this.createMenuButton(
            Component.translatable("physicsmod.menu.credits.title.main"),
            0,
            17 + 30 * count++,
            120,
            buttonHeight,
            () -> null,
            Component.translatable("physicsmod.credits"),
            null,
            false,
            (ImageElement)((Animatable)(
                  new ImageElement(this.resourceManager, GUIResources.PARALLAX_CREDITS_BACKGROUND, 0.0F, 0.0F, this.width, this.height, true)
               ))
               .addAnimator(new MouseParallaxAnimator(0.107F, false)),
            (ImageElement)((Animatable)(new ImageElement(this.resourceManager, GUIResources.PARALLAX_CREDITS_DRAGON, 0.0F, 0.0F, this.width, this.height, true)))
               .addAnimator(new MouseParallaxAnimator(0.14F, false))
         )
      );
      ((Animatable)credits).getAnimator(BarRenderer.class).setBarColor(BaseColors.WARNING_COLOR).setHoveredColor(BaseColors.WARNING_COLOR);
      int offsetCounter = 0;
      this.supportDevelopment = (Button)this.addRenderableWidget(
         (Button)((Animatable)ButtonSettings.builderNoStyle(
               this.width - 154,
               17 + 30 * offsetCounter++,
               154,
               buttonHeight,
               Component.translatable("physicsmod.menu.main.support"),
               button -> Util.getPlatform().openUri("https://minecraftphysicsmod.com/development")
            ))
            .addAnimator(this.createWarningRightSideAnimators())
      );
      this.back = (Button)this.addRenderableWidget(
         (Button)((Animatable)ButtonSettings.builderNoStyle(
               this.width - 70,
               17 + 30 * offsetCounter++,
               70,
               buttonHeight,
               Component.translatable("physicsmod.gui.back"),
               button -> this.minecraft.setScreen(this.parent)
            ))
            .addAnimator(this.createRightSideAnimators())
      );
      if (StarterClient.newUpdateAvailable) {
         this.updateAvailable = (Button)this.addRenderableWidget(
            (Button)((Animatable)ButtonSettings.builderNoStyle(
                  this.width - 70,
                  17 + 30 * offsetCounter++,
                  70,
                  buttonHeight,
                  Component.translatable("physicsmod.menu.main.update"),
                  button -> Util.getPlatform().openUri("https://minecraftphysicsmod.com/")
               ))
               .addAnimator(this.createWarningRightSideAnimators())
         );
      }

      this.addRenderableWidget(
         (Button)((Animatable)ButtonSettings.builderNoStyle(
               this.width - 70,
               17 + 30 * offsetCounter++,
               70,
               buttonHeight,
               Component.translatable("physicsmod.menu.main.getpro"),
               button -> Util.getPlatform().openUri("https://minecraftphysicsmod.com/pro")
            ))
            .addAnimator(this.createWarningRightSideAnimators())
            .addAnimator(new PhysicsSettingsScreen.GetProAnimator(this.focusSelector))
      );
      this.scrollAnimator.setMinOffset(-(34 + 30 * count - this.height - (30 - buttonHeight)));
      this.scrollAnimator.setMaxOffset(0.0F);
   }

   private List<Animator> createRightSideAnimators() {
      return Arrays.asList(
         new ButtonRenderer(TextAlignment.RIGHT, ChatFormatting.BOLD),
         new BarRenderer(BarRenderer.BarAlignment.LEFT, false, 2.4F),
         new WidthAnimator(0.6F, 0.5F, true)
      );
   }

   private List<Animator> createWarningRightSideAnimators() {
      return Arrays.asList(
         new ButtonRenderer(TextAlignment.RIGHT, ChatFormatting.BOLD),
         new BarRenderer(BarRenderer.BarAlignment.LEFT, false, 2.4F).setBarColor(BaseColors.WARNING_COLOR).setHoveredColor(BaseColors.WARNING_COLOR),
         new WidthAnimator(0.6F, 0.5F, true)
      );
   }

   public Button createMenuButton(
      Component title,
      int buttonX,
      int buttonY,
      int buttonWidth,
      int buttonHeight,
      Supplier<Screen> supplier,
      @Nullable Component activeTooltip,
      @Nullable Component inactiveTooltip,
      boolean requiresPro,
      ImageElement... parallaxes
   ) {
      if (requiresPro) {
      }

      boolean needsProButHasnt = false;
      if (needsProButHasnt && activeTooltip != null) {
         inactiveTooltip = Component.translatable("physicsmod.menu.main.prorequired").copy().append(activeTooltip);
      }

      Component pInactiveTooltip = inactiveTooltip;
      Button menuButton = (Button)((Animatable)ButtonSettings.builderNoStyle(buttonX, buttonY, buttonWidth, buttonHeight, title, button -> {
            Screen screen = supplier.get();
            if (screen != null) {
               this.minecraft.setScreen(screen);
            }
         }, null))
         .addAnimator(
            this.scrollAnimator,
            new ButtonRenderer(TextAlignment.LEFT, ChatFormatting.BOLD),
            new BarRenderer(BarRenderer.BarAlignment.RIGHT, false, 2.4F),
            new WidthAnimator(0.6F, 0.5F),
            this.focusSelector,
            new PhysicsSettingsScreen.ProRequirement(requiresPro),
            new Parallaxes(parallaxes),
            new TooltipRenderer(
               (animatable, guiGraphics, mouseX, mouseY, renderPercent, delta) -> this.renderTooltip(
                  animatable, guiGraphics, activeTooltip, pInactiveTooltip, buttonHeight
               )
            )
         );
      if (needsProButHasnt) {
         menuButton.active = false;
      }

      return menuButton;
   }

   private void renderTooltip(
      Animatable animatable, GuiGraphics guiGraphics, @Nullable Component activeTooltip, @Nullable Component inactiveTooltip, int buttonHeight
   ) {
      float x = 120.0F + buttonHeight * 0.6F + 17.0F;
      if (!this.renderedTooltip) {
         boolean active = true;
         if (animatable instanceof AbstractWidget widget) {
            active = widget.active;
         }

         if (!active) {
            if (inactiveTooltip != null) {
               MainToolTipRenderer.renderToolTip(animatable, inactiveTooltip, guiGraphics, x, this.width - x - 17.0F, (float)(this.height - 17), 2.4F);
               this.renderedTooltip = true;
            }
         } else if (activeTooltip != null) {
            MainToolTipRenderer.renderToolTip(animatable, activeTooltip, guiGraphics, x, this.width - x - 17.0F, (float)(this.height - 17), 2.4F);
            this.renderedTooltip = true;
         }
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double f, double scrollAmount) {
      if (this.scrollAnimator != null) {
         this.scrollAnimator.scroll((float)scrollAmount);
      }

      return true;
   }

   public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
   }

   public boolean keyPressed(int key, int j, int k) {
      if (this.scrollAnimator != null) {
         if (key == 267) {
            this.scrollAnimator.scroll(-4.0F);
         }

         if (key == 266) {
            this.scrollAnimator.scroll(4.0F);
         }
      }

      return super.keyPressed(key, j, k);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseKey) {
      return this.scrollAnimator != null && this.scrollAnimator.mouseClicked(mouseX, mouseY, mouseKey) ? true : super.mouseClicked(mouseX, mouseY, mouseKey);
   }

   public void onClose() {
      super.onClose();
      this.resourceManager.destroy();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.renderedTooltip = false;
      super.render(guiGraphics, mouseX, mouseY, delta);
      if (this.back.isHoveredOrFocused() || this.updateAvailable != null && this.updateAvailable.isHoveredOrFocused()) {
         this.focusSelector.deselectAll();
      }
   }

   public class GetProAnimator extends Animator {
      private FocusSelector focusSelector;
      private boolean visible;
      private float baseX;
      private float baseWidth;
      private float oldX;
      private float currentX;

      public GetProAnimator(FocusSelector focusSelector) {
         this.focusSelector = focusSelector;
      }

      @Override
      public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
         super.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
         if (this.focusSelector.getFocusedElement() == null) {
            this.visible = false;
         } else if (this.focusSelector.getFocusedElement().getAnimator(PhysicsSettingsScreen.ProRequirement.class).requiresPro) {
            this.visible = false;
         } else {
            this.visible = false;
         }

         float newX = Math.lerp(this.oldX, this.currentX, renderPercent);
         animatable.setAnimX(newX - animatable.getAnimator(WidthAnimator.class).getOffset());
         return false;
      }

      @Override
      public void tick(Animatable animatable) {
         super.tick(animatable);
         float target = this.baseX;
         if (!this.visible) {
            target += this.baseWidth;
         }

         this.oldX = this.currentX;
         this.currentX = Math.lerp(this.currentX, target, 0.5F);
      }

      @Override
      public void init(Animatable animatable) {
         super.init(animatable);
         this.baseX = animatable.getAnimX();
         this.baseWidth = animatable.getAnimWidth() + 14.400001F;
         this.oldX = this.baseX + this.baseWidth;
         this.currentX = this.baseX + this.baseWidth;
         animatable.setAnimX(this.baseX + this.baseWidth);
      }
   }

   public class ProRequirement extends Animator {
      public boolean requiresPro;

      public ProRequirement(boolean requiresPro) {
         this.requiresPro = requiresPro;
      }
   }
}
