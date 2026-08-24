package com.iafenvoy.jupiter.render.screen;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.config.container.FileConfigContainer;
import com.iafenvoy.jupiter.config.container.wrapper.RemoteConfigWrapper;
import com.iafenvoy.jupiter.network.ClientConfigNetwork;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.TextUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigSelectScreen extends Screen implements JupiterScreen {
   private final Screen parent;
   @Nullable
   private final AbstractConfigContainer common;
   @Nullable
   private final AbstractConfigContainer client;
   @Nullable
   private final AbstractConfigContainer server;
   private final boolean displayCommon;

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ConfigSelectScreen(Component title, Screen parent, @Nullable FileConfigContainer serverConfig, @Nullable FileConfigContainer clientConfig) {
      super(title);
      this.parent = parent;
      this.server = serverConfig;
      this.client = clientConfig;
      this.common = null;
      this.displayCommon = false;
   }

   protected ConfigSelectScreen(
      Component title,
      Screen parent,
      @Nullable AbstractConfigContainer common,
      @Nullable AbstractConfigContainer client,
      @Nullable AbstractConfigContainer server,
      boolean displayCommon
   ) {
      super(title);
      this.parent = parent;
      this.common = common;
      this.client = client;
      this.server = server;
      this.displayCommon = displayCommon;
   }

   protected void init() {
      super.init();
      int x = this.width / 2;
      int y = this.height / 2;
      this.addRenderableWidget(
         JupiterScreen.createButton(x - 100, y - (this.displayCommon ? 60 : 45), 200, 20, TextUtil.translatable("jupiter.screen.back"), button -> {
            assert this.minecraft != null;

            this.minecraft.setScreen(this.parent);
         })
      );
      boolean connectedToRemote = JupiterScreen.connectedToDedicatedServer();
      if (this.displayCommon) {
         ((Button)this.addRenderableWidget(
               JupiterScreen.createButtonWithTooltip(this, x - 100, y - 30, 95, 20, TextUtil.translatable("jupiter.screen.local_common_config"), button -> {
                  assert this.minecraft != null;

                  assert this.common != null;

                  this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, this.common, true));
               }, TextUtil.translatable(this.common != null ? "jupiter.screen.open_local_common" : "jupiter.screen.unavailable"))
            ))
            .active = this.common != null;
         Pair<Button, Consumer<Component>> commonPair = JupiterScreen.createButtonWithDynamicTooltip(
            this, x + 5, y - 30, 95, 20, TextUtil.translatable("jupiter.screen.remote_common_config"), button -> {
               assert this.minecraft != null && this.common != null;

               this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, new RemoteConfigWrapper(this.common), false));
            }, TextUtil.translatable("jupiter.screen.unavailable")
         );
         ((Button)this.addRenderableWidget((Button)commonPair.getFirst())).active = this.common != null && connectedToRemote;
         if (this.common != null) {
            if (connectedToRemote) {
               handleRemoteConfig(
                  this.common,
                  "jupiter.screen.open_remote_common",
                  b -> ((Button)commonPair.getFirst()).active = b,
                  (Consumer<Component>)commonPair.getSecond()
               );
            } else {
               ((Consumer)commonPair.getSecond()).accept(TextUtil.translatable("jupiter.screen.need_remote_server"));
            }
         }
      }

      ((Button)this.addRenderableWidget(
            JupiterScreen.createButtonWithTooltip(
               this, x - 100, y - (this.displayCommon ? 0 : 15), 95, 20, TextUtil.translatable("jupiter.screen.local_server_config"), button -> {
                  assert this.minecraft != null;

                  assert this.server != null;

                  this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, this.server, true));
               }, TextUtil.translatable(this.server != null ? "jupiter.screen.open_local_server" : "jupiter.screen.unavailable")
            )
         ))
         .active = this.server != null;
      Pair<Button, Consumer<Component>> serverPair = JupiterScreen.createButtonWithDynamicTooltip(
         this, x + 5, y - (this.displayCommon ? 0 : 15), 95, 20, TextUtil.translatable("jupiter.screen.remove_server_config"), button -> {
            assert this.minecraft != null && this.server != null;

            this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, new RemoteConfigWrapper(this.server), false));
         }, TextUtil.translatable("jupiter.screen.unavailable")
      );
      ((Button)this.addRenderableWidget((Button)serverPair.getFirst())).active = this.server != null && connectedToRemote;
      if (this.server != null) {
         if (connectedToRemote) {
            handleRemoteConfig(
               this.server, "jupiter.screen.open_remote_server", b -> ((Button)serverPair.getFirst()).active = b, (Consumer<Component>)serverPair.getSecond()
            );
         } else {
            ((Consumer)serverPair.getSecond()).accept(TextUtil.translatable("jupiter.screen.need_remote_server"));
         }
      }

      ((Button)this.addRenderableWidget(
            JupiterScreen.createButtonWithTooltip(
               this, x - 100, y + (this.displayCommon ? 30 : 15), 200, 20, TextUtil.translatable("jupiter.screen.client_config"), button -> {
                  assert this.minecraft != null;

                  assert this.client != null;

                  this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, this.client, true));
               }, TextUtil.translatable(this.client != null ? "jupiter.screen.open_client" : "jupiter.screen.unavailable")
            )
         ))
         .active = this.client != null;
   }

   private static void handleRemoteConfig(AbstractConfigContainer container, String openKey, BooleanConsumer buttonActive, Consumer<Component> tooltip) {
      tooltip.accept(TextUtil.translatable("jupiter.screen.check_server"));
      buttonActive.accept(false);
      ClientConfigNetwork.syncConfig(container.getConfigId(), nbt -> {
         if (nbt == null) {
            tooltip.accept(TextUtil.translatable("jupiter.screen.disable_server"));
         } else {
            try {
               container.deserializeNbt(nbt);
               tooltip.accept(TextUtil.translatable(openKey));
               buttonActive.accept(true);
            } catch (Exception var6) {
               Jupiter.LOGGER.error("Failed to parse config data from server: {}", container.getConfigId(), var6);
               tooltip.accept(TextUtil.translatable("jupiter.screen.error_server"));
            }
         }
      });
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.render(graphics, mouseX, mouseY, delta);

      assert this.minecraft != null;

      graphics.drawCenteredString(this.minecraft.font, this.title, this.width / 2, this.height / 2 - (this.displayCommon ? 80 : 65), -1);
   }

   public void onClose() {
      assert this.minecraft != null;

      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return true;
   }

   public static ConfigSelectScreen.Builder builder(String titleKey, Screen parent) {
      return new ConfigSelectScreen.Builder(titleKey, parent);
   }

   public static ConfigSelectScreen.Builder builder(Component title, Screen parent) {
      return new ConfigSelectScreen.Builder(title, parent);
   }

   public static class Builder {
      private final Component title;
      private final Screen parent;
      private AbstractConfigContainer common;
      private AbstractConfigContainer client;
      private AbstractConfigContainer server;
      private boolean displayCommon = false;

      public Builder(String titleKey, Screen parent) {
         this(TextUtil.translatable(titleKey), parent);
      }

      public Builder(Component title, Screen parent) {
         this.title = title;
         this.parent = parent;
      }

      public ConfigSelectScreen.Builder common(AbstractConfigContainer common) {
         this.common = common;
         return this.displayCommon();
      }

      public ConfigSelectScreen.Builder displayCommon() {
         this.displayCommon = true;
         return this;
      }

      public ConfigSelectScreen.Builder client(AbstractConfigContainer client) {
         this.client = client;
         return this;
      }

      public ConfigSelectScreen.Builder server(AbstractConfigContainer server) {
         this.server = server;
         return this;
      }

      public ConfigSelectScreen build() {
         return new ConfigSelectScreen(this.title, this.parent, this.common, this.client, this.server, this.displayCommon);
      }
   }
}
