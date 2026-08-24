package com.mrcrayfish.configured.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.configured.api.ActionResult;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.Environment;
import com.mrcrayfish.configured.api.ExecutionContext;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.ClientConfigHelper;
import com.mrcrayfish.configured.client.screen.widget.IconButton;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import com.mrcrayfish.configured.util.ConfigHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

public class ModConfigSelectionScreen extends ListMenuScreen {
   private final Map<ConfigType, Set<IModConfig>> configMap;

   public ModConfigSelectionScreen(Screen parent, Component title, Map<ConfigType, Set<IModConfig>> configMap) {
      super(parent, title, 30);
      this.configMap = configMap;
   }

   @Override
   protected void constructEntries(List<ListMenuScreen.Item> entries) {
      Set<IModConfig> localConfigs = this.getLocalConfigs();
      if (!localConfigs.isEmpty()) {
         entries.add(new ListMenuScreen.TitleItem(Component.translatable("configured.gui.title.client_configuration").getString()));
         List<ListMenuScreen.Item> localEntries = new ArrayList<>();
         localConfigs.forEach(config -> localEntries.add(new ModConfigSelectionScreen.FileItem(this, config)));
         Collections.sort(localEntries);
         entries.addAll(localEntries);
      }

      Player player = Minecraft.getInstance().player;
      ExecutionContext context = new ExecutionContext(player);
      Set<IModConfig> remoteConfigs = this.getRemoteConfigs();
      if (!remoteConfigs.isEmpty() && (context.isMainMenu() || context.isConfiguredInstalledRemotely())) {
         if (context.isPlayingGame() && context.isPlayingOnRemoteServer()) {
            if (context.isPlayingOnLan() && !context.isIntegratedServerOwnedByPlayer()) {
               entries.add(new ListMenuScreen.TitleItem(Component.translatable("configured.gui.title.server_configuration").getString()));
               entries.add(new ListMenuScreen.TitleItem(Component.translatable("configured.gui.lan_server")));
               return;
            }

            if (!context.isPlayerAnOperator()) {
               return;
            }

            if (!context.isDeveloperPlayer()) {
               entries.add(new ListMenuScreen.TitleItem(Component.translatable("configured.gui.title.server_configuration").getString()));
               entries.add(
                  new ListMenuScreen.MultiTextItem(
                     Component.translatable("configured.gui.no_developer_status"),
                     Component.translatable(
                           "configured.gui.developer_details",
                           new Object[]{
                              Component.literal("configured.developer.toml").withStyle(ChatFormatting.GOLD).withStyle(Style.EMPTY.withUnderlined(true))
                           }
                        )
                        .withStyle(ChatFormatting.GRAY)
                        .withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.translatable("configured.gui.developer_file"))))
                  )
               );
               return;
            }
         }

         entries.add(new ListMenuScreen.TitleItem(Component.translatable("configured.gui.title.server_configuration").getString()));
         List<ListMenuScreen.Item> remoteEntries = new ArrayList<>();
         remoteConfigs.forEach(config -> remoteEntries.add(new ModConfigSelectionScreen.FileItem(this, config)));
         Collections.sort(remoteEntries);
         entries.addAll(remoteEntries);
      }
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(
         ScreenUtil.button(this.width / 2 - 75, this.height - 29, 150, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.parent))
      );
   }

   private Set<IModConfig> getLocalConfigs() {
      return this.configMap
         .entrySet()
         .stream()
         .filter(entry -> !entry.getKey().isServer())
         .flatMap(entry -> entry.getValue().stream())
         .collect(Collectors.toSet());
   }

   private Set<IModConfig> getRemoteConfigs() {
      return this.configMap.entrySet().stream().filter(entry -> {
         ConfigType type = entry.getKey();
         return type.isServer() && type.getEnv().orElse(null) != Environment.DEDICATED_SERVER;
      }).flatMap(entry -> entry.getValue().stream()).collect(Collectors.toSet());
   }

   public static String createLabelFromModConfig(IModConfig config) {
      if (config.getTranslationKey() != null) {
         return I18n.get(config.getTranslationKey(), new Object[0]);
      } else {
         String fileName = config.getFileName();
         fileName = fileName.replace(config.getModId() + "-", "");
         if (fileName.endsWith(".toml")) {
            fileName = fileName.substring(0, fileName.length() - ".toml".length());
         }

         fileName = FilenameUtils.getName(fileName);
         return ConfigScreen.createLabel(fileName);
      }
   }

   public static boolean isRunningUnpublishedLan() {
      return Minecraft.getInstance().getSingleplayerServer() != null && !Minecraft.getInstance().getSingleplayerServer().isPublished();
   }

   public class FileItem extends ListMenuScreen.Item {
      protected final TooltipScreen screen;
      protected final IModConfig config;
      protected final Component title;
      protected final Component fileName;
      protected final Component modifyTooltip;
      protected final Button modifyButton;
      @Nullable
      protected final Button restoreButton;

      public FileItem(TooltipScreen screen, IModConfig config) {
         super(ModConfigSelectionScreen.createLabelFromModConfig(config));
         this.screen = screen;
         this.config = config;
         this.title = this.createTrimmedFileName(ModConfigSelectionScreen.createLabelFromModConfig(config));
         this.fileName = this.createTrimmedFileName(config.getFileName()).withStyle(ChatFormatting.DARK_GRAY);
         this.modifyButton = this.createModifyButton(config);
         ActionResult result = config.canPlayerEdit(Minecraft.getInstance().player);
         this.modifyButton.active = result.asBoolean();
         this.modifyTooltip = result.message().orElse(Component.translatable("configured.gui.no_permission"));
         this.restoreButton = this.createRestoreButton(config);
         this.updateRestoreDefaultButton();
      }

      private void showRestoreScreen() {
         ConfirmationScreen confirmScreen = new ConfirmationScreen(
            ModConfigSelectionScreen.this, Component.translatable("configured.gui.restore_message"), ConfirmationScreen.Icon.WARNING, result -> {
               if (!result) {
                  return true;
               } else {
                  this.config.restoreDefaultsTask().ifPresent(Runnable::run);
                  this.updateRestoreDefaultButton();
                  return true;
               }
            }
         );
         confirmScreen.setPositiveText(
            Component.translatable("configured.gui.restore").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
         );
         confirmScreen.setNegativeText(CommonComponents.GUI_CANCEL);
         Minecraft.getInstance().setScreen(confirmScreen);
      }

      private MutableComponent createTrimmedFileName(String fileName) {
         MutableComponent trimmedFileName = Component.literal(fileName);
         if (Minecraft.getInstance().font.width(fileName) > 150) {
            trimmedFileName = Component.literal(Minecraft.getInstance().font.plainSubstrByWidth(fileName, 140) + "...");
         }

         return trimmedFileName;
      }

      private Button createModifyButton(IModConfig config) {
         int width = ConfigHelper.canRestoreConfig(config, Minecraft.getInstance().player) ? 60 : 82;
         return new IconButton(
            0,
            0,
            this.getModifyIconU(config),
            this.getModifyIconV(config),
            width,
            this.getModifyLabel(config),
            button -> {
               if (button.isActive() && button.visible) {
                  if (config.canPlayerEdit(Minecraft.getInstance().player).asBoolean()) {
                     if (config.getType() != ConfigType.DEDICATED_SERVER) {
                        ExecutionContext context = new ExecutionContext(Minecraft.getInstance().player);
                        if (context.isMainMenu()) {
                           if (config.getType().isWorld()) {
                              Minecraft.getInstance().setScreen(new WorldSelectionScreen(ModConfigSelectionScreen.this, config, this.title));
                              return;
                           }

                           if (config.getType().isServer()) {
                              Component newTitle = ModConfigSelectionScreen.this.title
                                 .copy()
                                 .append(Component.literal(" > ").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                                 .append(this.title);
                              Minecraft.getInstance().setScreen(new ConfigScreen(ModConfigSelectionScreen.this, newTitle, config));
                              return;
                           }
                        }

                        if (context.isPlayingOnRemoteServer()
                           && context.isConfiguredInstalledRemotely()
                           && config.requestFromServerTask().isPresent()
                           && context.isPlayerAnOperator()
                           && context.isDeveloperPlayer()) {
                           Component newTitle = ModConfigSelectionScreen.this.title
                              .copy()
                              .append(Component.literal(" > ").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                              .append(this.title);
                           Minecraft.getInstance().setScreen(new RequestScreen(ModConfigSelectionScreen.this, newTitle, config));
                        } else {
                           Component newTitle = ModConfigSelectionScreen.this.title
                              .copy()
                              .append(Component.literal(" > ").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                              .append(this.title);
                           Minecraft.getInstance().setScreen(new ConfigScreen(ModConfigSelectionScreen.this, newTitle, config));
                        }
                     }
                  }
               }
            }
         );
      }

      private int getModifyIconU(IModConfig config) {
         return !ConfigHelper.isPlayingGame() && config.getType().isWorld() ? 11 : 0;
      }

      private int getModifyIconV(IModConfig config) {
         if (ConfigHelper.isPlayingGame()) {
            if (config.isReadOnly()) {
               return 33;
            }
         } else if (config.isReadOnly() && !config.getType().isWorld()) {
            return 33;
         }

         return 22;
      }

      private Component getModifyLabel(IModConfig config) {
         if (ClientConfigHelper.isMainMenu() && config.getType().isWorld()) {
            return Component.translatable("configured.gui.select_world");
         } else {
            return config.isReadOnly() ? Component.translatable("configured.gui.view") : Component.translatable("configured.gui.modify");
         }
      }

      private Button createRestoreButton(IModConfig config) {
         if (!ConfigHelper.canRestoreConfig(config, Minecraft.getInstance().player)) {
            return null;
         } else {
            IconButton restoreButton = new IconButton(0, 0, 0, 0, onPress -> this.showRestoreScreen());
            restoreButton.active = !config.isReadOnly() && config.isChanged();
            return restoreButton;
         }
      }

      @Override
      public void render(
         GuiGraphics graphics, int x, int top, int left, int width, int p_230432_6_, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks
      ) {
         graphics.drawString(Minecraft.getInstance().font, this.title, left + 28, top + 2, 16777215);
         graphics.drawString(Minecraft.getInstance().font, this.fileName, left + 28, top + 12, 16777215);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(IconButton.ICONS, left + 4, top, 18, 22, this.getIconU(), this.getIconV(), 9, 11, 64, 64);
         if (this.config.isReadOnly()) {
            graphics.blit(IconButton.ICONS, left + 1, top + 15, 11, 11, 0.0F, 33.0F, 11, 11, 64, 64);
         }

         this.modifyButton.setX(left + width - 83);
         this.modifyButton.setY(top);
         this.modifyButton.render(graphics, mouseX, mouseY, partialTicks);
         if (this.restoreButton != null) {
            this.restoreButton.setX(left + width - 21);
            this.restoreButton.setY(top);
            this.restoreButton.render(graphics, mouseX, mouseY, partialTicks);
         }

         if (this.config.isReadOnly() && ScreenUtil.isMouseWithin(left - 1, top + 15, 11, 11, mouseX, mouseY)) {
            ModConfigSelectionScreen.this.setActiveTooltip(Component.translatable("configured.gui.read_only_config"), -14785178);
         }

         if (!this.modifyButton.active && this.modifyButton.isHoveredOrFocused()) {
            this.screen.setActiveTooltip(this.modifyTooltip, -1428357120);
         }
      }

      private int getIconU() {
         return this.config.getType().ordinal() % 3 * 9 + 33;
      }

      private int getIconV() {
         return this.config.getType().ordinal() / 3 * 11;
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return this.restoreButton != null ? ImmutableList.of(this.modifyButton, this.restoreButton) : ImmutableList.of(this.modifyButton);
      }

      private void updateRestoreDefaultButton() {
         if (this.config != null && this.restoreButton != null && ConfigHelper.canRestoreConfig(this.config, Minecraft.getInstance().player)) {
            this.restoreButton.active = !this.config.isReadOnly() && this.config.isChanged();
         }
      }
   }
}
