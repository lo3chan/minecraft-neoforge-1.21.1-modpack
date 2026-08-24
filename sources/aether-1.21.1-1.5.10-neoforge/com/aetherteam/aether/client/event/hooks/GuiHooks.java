package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.component.skins.RefreshButton;
import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.client.gui.screen.perks.AetherCustomizationsScreen;
import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Client;
import io.wispforest.accessories.client.gui.AccessoriesScreen;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Tuple;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class GuiHooks {
   public static final Map<UUID, Integer> BOSS_EVENTS = new HashMap<>();
   private static boolean shouldAddButton = true;
   private static boolean generateTrivia = true;
   @Nullable
   private static Screen lastScreen = null;

   public static boolean isAccessoryButtonEnabled() {
      return !(Boolean)AetherConfig.CLIENT.disable_accessory_button.get() && !(Boolean)AetherConfig.COMMON.use_default_accessories_menu.get();
   }

   @Nullable
   public static AccessoryButton setupAccessoryButton(Screen screen, Tuple<Integer, Integer> offsets) {
      AbstractContainerScreen<?> containerScreen = canCreateAccessoryButtonForScreen(screen);
      return containerScreen != null
         ? new AccessoryButton(
            containerScreen,
            containerScreen.getGuiLeft() + (Integer)offsets.getA(),
            containerScreen.getGuiTop() + (Integer)offsets.getB(),
            AetherAccessoriesScreen.ACCESSORIES_BUTTON
         )
         : null;
   }

   @Nullable
   private static AbstractContainerScreen<?> canCreateAccessoryButtonForScreen(Screen screen) {
      if (!(screen instanceof InventoryScreen)
         && !(screen instanceof AccessoriesScreen)
         && !(screen instanceof CreativeModeInventoryScreen)
         && (!(screen instanceof AetherAccessoriesScreen) || !shouldAddButton)) {
         if (screen instanceof AetherAccessoriesScreen) {
            shouldAddButton = true;
         }

         return null;
      } else {
         return (AbstractContainerScreen<?>)screen;
      }
   }

   @Nullable
   public static GridLayout setupPerksButtons(Screen screen) {
      if (!(screen instanceof PauseScreen)) {
         return null;
      } else {
         User user = Client.getClientUser();
         int x = (Integer)AetherConfig.CLIENT.layout_perks_x.get();
         int y = (Integer)AetherConfig.CLIENT.layout_perks_y.get();
         GridLayout gridLayout = new GridLayout();
         gridLayout.defaultCellSetting().padding(58, 4, 4, 0);
         RowHelper rowHelper = gridLayout.createRowHelper(1);
         if (user != null) {
            if ((Boolean)AetherConfig.CLIENT.disable_skins_button.get() && !PerkUtil.hasAnyMoaSkins().test(user)) {
               y -= 6;
            } else {
               createSkinsButton(screen, rowHelper);
            }

            if (!PerkUtil.hasDeveloperGlow().test(user) && !PerkUtil.hasHalo().test(user)) {
               y -= 6;
            } else {
               createCustomizationsButton(screen, rowHelper);
            }
         } else {
            y -= 12;
         }

         gridLayout.arrangeElements();
         FrameLayout.alignInRectangle(gridLayout, x, y, screen.width, screen.height, 0.5F, 0.25F);
         return gridLayout;
      }
   }

   private static void createSkinsButton(Screen screen, RowHelper rowHelper) {
      ImageButton skinsButton = new ImageButton(
         0,
         0,
         20,
         20,
         AetherAccessoriesScreen.SKINS_BUTTON,
         pressed -> Minecraft.getInstance().setScreen(new MoaSkinsScreen(screen)),
         Component.translatable("gui.aether.accessories.skins_button")
      );
      skinsButton.setTooltip(Tooltip.create(Component.translatable("gui.aether.accessories.skins_button")));
      rowHelper.addChild(skinsButton);
   }

   private static void createCustomizationsButton(Screen screen, RowHelper rowHelper) {
      ImageButton customizationButton = new ImageButton(
         0,
         0,
         20,
         20,
         AetherAccessoriesScreen.CUSTOMIZATION_BUTTON,
         pressed -> Minecraft.getInstance().setScreen(new AetherCustomizationsScreen(screen)),
         Component.translatable("gui.aether.accessories.customization_button")
      );
      customizationButton.setTooltip(Tooltip.create(Component.translatable("gui.aether.accessories.customization_button")));
      rowHelper.addChild(customizationButton);
   }

   public static void drawTrivia(Screen screen, GuiGraphics guiGraphics) {
      generateTrivia(screen);
      if (screen instanceof GenericMessageScreen || screen instanceof LevelLoadingScreen || screen instanceof ReceivingLevelScreen) {
         Component triviaLine = Aether.TRIVIA_READER.getTriviaLine();
         if (triviaLine != null && (Boolean)AetherConfig.STARTUP.enable_trivia.get()) {
            Font font = Minecraft.getInstance().font;
            int y = screen.height - 7 - font.wordWrapHeight(triviaLine, screen.width);

            for (FormattedCharSequence sequence : font.split(triviaLine, screen.width)) {
               guiGraphics.drawCenteredString(font, sequence, screen.width / 2, y, 16777113);
               y += 9;
            }
         }
      }

      if ((screen instanceof TitleScreen && !(lastScreen instanceof TitleScreen) || screen instanceof PauseScreen && !(lastScreen instanceof PauseScreen))
         && !Aether.TRIVIA_READER.getTrivia().isEmpty()) {
         Aether.TRIVIA_READER.randomizeTriviaIndex();
      }

      lastScreen = screen;
   }

   private static void generateTrivia(Screen screen) {
      if (screen instanceof TitleScreen) {
         if (generateTrivia && Aether.TRIVIA_READER.getTrivia().isEmpty()) {
            Aether.TRIVIA_READER.generateTriviaList();
            generateTrivia = false;
         }
      } else if (screen instanceof LevelLoadingScreen && generateTrivia && Aether.TRIVIA_READER.getTrivia().isEmpty()) {
         Aether.TRIVIA_READER.generateTriviaList();
         generateTrivia = false;
      }
   }

   public static void drawAetherTravelMessage(Screen screen, GuiGraphics guiGraphics) {
      if (!(screen instanceof ReceivingLevelScreen) && !(screen instanceof ProgressScreen)) {
         DimensionHooks.displayAetherTravel = false;
      } else if (Minecraft.getInstance().player != null && DimensionHooks.displayAetherTravel) {
         if (DimensionHooks.playerLeavingAether) {
            guiGraphics.drawCenteredString(
               screen.getMinecraft().font,
               Component.translatable("gui.aether.descending"),
               screen.width / 2,
               (Integer)AetherConfig.CLIENT.portal_text_y.get(),
               16777215
            );
         } else {
            guiGraphics.drawCenteredString(
               screen.getMinecraft().font,
               Component.translatable("gui.aether.ascending"),
               screen.width / 2,
               (Integer)AetherConfig.CLIENT.portal_text_y.get(),
               16777215
            );
         }
      }
   }

   public static void handlePatreonRefreshRebound() {
      if (RefreshButton.reboundTimer > 0) {
         RefreshButton.reboundTimer--;
      }

      if (RefreshButton.reboundTimer < 0) {
         RefreshButton.reboundTimer = 0;
      }
   }

   public static void openAccessoryMenu() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null
         && minecraft.getOverlay() == null
         && minecraft.screen == null
         && !(Boolean)AetherConfig.CLIENT.disable_accessory_button.get()
         && AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
         if (minecraft.gameMode != null && minecraft.gameMode.isServerControlledInventory()) {
            minecraft.player.sendOpenInventory();
         } else {
            PacketDistributor.sendToServer(new OpenAccessoriesPacket(ItemStack.EMPTY), new CustomPacketPayload[0]);
            shouldAddButton = false;
         }
      }
   }

   public static void closeContainerMenu(int key, int action) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen instanceof AbstractContainerScreen<?> abstractContainerScreen
         && !(Boolean)AetherConfig.CLIENT.disable_accessory_button.get()
         && AetherKeys.OPEN_ACCESSORY_INVENTORY.getKey().getValue() == key
         && (action == 1 || action == 2)) {
         abstractContainerScreen.onClose();
      }
   }

   public static void drawBossHealthBar(GuiGraphics guiGraphics, int x, int y, LerpingBossEvent bossEvent) {
      int entityID = BOSS_EVENTS.get(bossEvent.getId());
      if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(entityID) instanceof AetherBossMob<?> aetherBossMob) {
         drawBar(guiGraphics, x + 2, y + 2, bossEvent, aetherBossMob);
         Component component = bossEvent.getName();
         int nameLength = Minecraft.getInstance().font.width(component);
         int nameX = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - nameLength / 2;
         int nameY = y - 9;
         guiGraphics.drawString(Minecraft.getInstance().font, component, nameX, nameY, 16777215);
      }
   }

   public static void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, AetherBossMob<?> aetherBossMob) {
      if (aetherBossMob.getBossBarBackgroundTexture() != null && aetherBossMob.getBossBarTexture() != null) {
         x -= 37;
         guiGraphics.blitSprite(aetherBossMob.getBossBarBackgroundTexture(), 256, 16, 0, 0, x, y, 256, 16);
         int health = (int)(bossEvent.getProgress() * 256.0F);
         if (health > 0) {
            guiGraphics.blitSprite(aetherBossMob.getBossBarTexture(), 256, 16, 0, 0, x, y, health, 16);
         }
      }
   }

   public static boolean isAetherBossBar(UUID uuid) {
      return BOSS_EVENTS.containsKey(uuid);
   }
}
