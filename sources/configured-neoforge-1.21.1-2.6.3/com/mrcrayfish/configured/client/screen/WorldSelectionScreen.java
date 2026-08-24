package com.mrcrayfish.configured.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.configured.Constants;
import com.mrcrayfish.configured.api.ActionResult;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.screen.widget.IconButton;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import com.mrcrayfish.configured.platform.Services;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.apache.commons.io.file.PathUtils;

public class WorldSelectionScreen extends ListMenuScreen {
   private static final LevelResource SERVER_CONFIG_FOLDER = Services.CONFIG.getServerConfigResource();
   private static final ResourceLocation MISSING_ICON = ResourceLocation.withDefaultNamespace("textures/misc/unknown_server.png");
   private final IModConfig config;

   public WorldSelectionScreen(Screen parent, IModConfig config, Component title) {
      super(
         parent,
         Component.translatable(
            "configured.gui.edit_world_config", new Object[]{title.plainCopy().withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.GOLD})}
         ),
         30
      );
      this.config = config;
   }

   @Override
   protected void constructEntries(List<ListMenuScreen.Item> entries) {
      try {
         LevelStorageSource source = Minecraft.getInstance().getLevelSource();
         List<LevelSummary> levels = new ArrayList<>((Collection<? extends LevelSummary>)source.loadLevelSummaries(source.findLevelCandidates()).join());
         if (levels.size() > 6) {
            entries.add(
               new ListMenuScreen.TitleItem(
                  Component.translatable("configured.gui.title.recently_played").withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.YELLOW})
               )
            );
            List<LevelSummary> recent = levels.stream().sorted(Comparator.comparing(s -> -s.getLastPlayed())).limit(3L).toList();
            recent.forEach(summary -> entries.add(new WorldSelectionScreen.WorldItem(summary)));
            levels.removeAll(recent);
            entries.add(
               new ListMenuScreen.TitleItem(
                  Component.translatable("configured.gui.title.other_worlds").withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.YELLOW})
               )
            );
         }

         levels.stream().sorted(Comparator.comparing(LevelSummary::getLevelName)).forEach(summary -> entries.add(new WorldSelectionScreen.WorldItem(summary)));
      } catch (LevelStorageException var5) {
         var5.printStackTrace();
      }
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(
         ScreenUtil.button(this.width / 2 - 75, this.height - 29, 150, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.parent))
      );
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      graphics.pose().pushPose();
      graphics.pose().translate(this.width - 30, 15.0F, 0.0F);
      graphics.pose().scale(2.5F, 2.5F, 2.5F);
      graphics.drawString(this.font, Component.literal("?").withStyle(ChatFormatting.BOLD), 0, 0, 16777215);
      graphics.pose().popPose();
   }

   @Override
   protected void updateTooltip(int mouseX, int mouseY) {
      super.updateTooltip(mouseX, mouseY);
      if (ScreenUtil.isMouseWithin(this.width - 30, 15, 23, 23, mouseX, mouseY)) {
         this.setActiveTooltip(Component.translatable("configured.gui.server_config_info"));
      }
   }

   public void onClose() {
      super.onClose();
      this.entries.forEach(item -> {
         if (item instanceof WorldSelectionScreen.WorldItem) {
            ((WorldSelectionScreen.WorldItem)item).disposeIcon();
         }
      });
   }

   public class WorldItem extends ListMenuScreen.Item {
      private final Component worldName;
      private final Component folderName;
      private final ResourceLocation iconId;
      private Path iconFile;
      private final DynamicTexture texture;
      private final Button modifyButton;

      public WorldItem(LevelSummary summary) {
         super(summary.getLevelName());
         this.worldName = Component.literal(summary.getLevelName());
         this.folderName = Component.literal(summary.getLevelId()).withStyle(ChatFormatting.DARK_GRAY);
         this.iconId = ResourceLocation.withDefaultNamespace(
            "worlds/"
               + Util.sanitizeName(summary.getLevelId(), ResourceLocation::validPathChar)
               + "/"
               + Hashing.sha1().hashUnencodedChars(summary.getLevelId())
               + "/icon"
         );
         this.iconFile = summary.getIcon();
         if (!Files.isRegularFile(this.iconFile)) {
            this.iconFile = null;
         }

         this.texture = this.loadWorldIcon();
         this.modifyButton = new IconButton(
            0, 0, 0, this.getIconV(), 60, this.getButtonLabel(), onPress -> this.loadWorldConfig(summary.getLevelId(), summary.getLevelName())
         );
      }

      private Component getButtonLabel() {
         return WorldSelectionScreen.this.config.isReadOnly() ? Component.translatable("configured.gui.view") : Component.translatable("configured.gui.select");
      }

      private int getIconV() {
         return WorldSelectionScreen.this.config.isReadOnly() ? 33 : 22;
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.modifyButton);
      }

      @Override
      public void render(
         GuiGraphics graphics, int x, int top, int left, int width, int p_230432_6_, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks
      ) {
         if (x % 2 != 0) {
            graphics.fill(left, top, left + width, top + 24, 1426063360);
         }

         if (this.modifyButton.isMouseOver(mouseX, mouseY)) {
            graphics.fill(left - 1, top - 1, left + 25, top + 25, -1);
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(this.texture != null ? this.iconId : WorldSelectionScreen.MISSING_ICON, left, top, 24, 24, 0.0F, 0.0F, 64, 64, 64, 64);
         graphics.drawString(WorldSelectionScreen.this.minecraft.font, this.worldName, left + 30, top + 3, 16777215);
         graphics.drawString(WorldSelectionScreen.this.minecraft.font, this.folderName, left + 30, top + 13, 16777215);
         this.modifyButton.setX(left + width - 61);
         this.modifyButton.setY(top + 2);
         this.modifyButton.render(graphics, mouseX, mouseY, partialTicks);
      }

      private DynamicTexture loadWorldIcon() {
         if (this.iconFile == null) {
            return null;
         } else {
            try {
               DynamicTexture texture;
               try (InputStream is = Files.newInputStream(this.iconFile)) {
                  NativeImage image = NativeImage.read(is);

                  label96: {
                     DynamicTexture var4;
                     try {
                        if (image.getWidth() != 64 || image.getHeight() != 64) {
                           texture = null;
                           break label96;
                        }

                        texture = new DynamicTexture(image);
                        WorldSelectionScreen.this.minecraft.getTextureManager().register(this.iconId, texture);
                        var4 = texture;
                     } catch (Throwable var7) {
                        if (image != null) {
                           try {
                              image.close();
                           } catch (Throwable var6) {
                              var7.addSuppressed(var6);
                           }
                        }

                        throw var7;
                     }

                     if (image != null) {
                        image.close();
                     }

                     return var4;
                  }

                  if (image != null) {
                     image.close();
                  }
               }

               return texture;
            } catch (IOException var9) {
               return null;
            }
         }
      }

      public void disposeIcon() {
         if (this.texture != null) {
            this.texture.close();
         }
      }

      private void loadWorldConfig(String worldFileName, String worldName) {
         try {
            LevelStorageAccess storageAccess = Minecraft.getInstance().getLevelSource().createAccess(worldFileName);

            label58: {
               try {
                  Path worldConfigPath = storageAccess.getLevelPath(WorldSelectionScreen.SERVER_CONFIG_FOLDER);
                  PathUtils.createParentDirectories(worldConfigPath, new FileAttribute[0]);
                  if (!Files.isDirectory(worldConfigPath)) {
                     Files.createDirectory(worldConfigPath);
                  }

                  ActionResult result = WorldSelectionScreen.this.config.loadWorldConfig(worldConfigPath);
                  if (result.asBoolean()) {
                     Component configName = Component.literal(ModConfigSelectionScreen.createLabelFromModConfig(WorldSelectionScreen.this.config));
                     Component newTitle = Component.literal(worldName)
                        .copy()
                        .append(Component.literal(" > ").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                        .append(configName);
                     WorldSelectionScreen.this.minecraft
                        .setScreen(new ConfigScreen(WorldSelectionScreen.this.parent, newTitle, WorldSelectionScreen.this.config));
                     break label58;
                  }

                  Component message = result.message().orElse(Component.translatable("configured.gui.load_world_config_failed"));
                  ConfirmationScreen.showError(WorldSelectionScreen.this.minecraft, WorldSelectionScreen.this, message);
               } catch (Throwable var9) {
                  if (storageAccess != null) {
                     try {
                        storageAccess.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (storageAccess != null) {
                  storageAccess.close();
               }

               return;
            }

            if (storageAccess != null) {
               storageAccess.close();
            }
         } catch (IOException var10) {
            Constants.LOG.error("Failed to load world config", var10);
            ConfirmationScreen.showError(
               WorldSelectionScreen.this.minecraft, WorldSelectionScreen.this, Component.translatable("configured.gui.load_world_config_exception")
            );
         }
      }
   }
}
