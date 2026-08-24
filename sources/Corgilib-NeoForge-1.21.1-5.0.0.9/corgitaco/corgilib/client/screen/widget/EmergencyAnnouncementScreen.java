package corgitaco.corgilib.client.screen.widget;

import corgitaco.corgilib.CorgiLib;
import corgitaco.corgilib.client.AnnouncementInfo;
import corgitaco.corgilib.config.AnnouncementConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;

public class EmergencyAnnouncementScreen extends Screen {
   private static final ResourceLocation ICE = ResourceLocation.withDefaultNamespace("textures/block/ice.png");

   public EmergencyAnnouncementScreen(Component title) {
      super(title);
   }

   protected void init() {
      if (AnnouncementInfo.getInstance() == null) {
         this.onClose();
      } else {
         super.init();
         int width3rd = this.width / 3;
         int height3rd = this.height / 3;
         Component learnMore = AnnouncementInfo.getInstance().actionButtonText();
         Component dismiss = Component.literal("Don't Show Again");
         Component back = Component.literal("Back");
         this.addRenderableWidget(
            new Builder(learnMore, ConfirmLinkScreen.confirmLink(Minecraft.getInstance().screen, AnnouncementInfo.getInstance().url()))
               .pos(width3rd + 5, height3rd * 2 - 50)
               .size(width3rd - 10, 20)
               .build()
         );
         this.addRenderableWidget(new Builder(dismiss, button -> {
            AnnouncementInfo.saveStoredAnnouncementInfo();
            Minecraft.getInstance().setScreen(null);
         }).pos(width3rd * 2 - 150 - 5, height3rd * 2 - 25).build());
         this.addRenderableWidget(new Builder(back, button -> Minecraft.getInstance().setScreen(null)).pos(width3rd + 5, height3rd * 2 - 25).build());
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      int width3rd = this.width / 3;
      int height3rd = this.height / 3;
      guiGraphics.fill(width3rd - 5, height3rd - 5, width3rd * 2 + 5, height3rd * 2 + 5, ARGB32.color(255, 0, 0, 150));
      guiGraphics.blit(ICE, width3rd, height3rd, 0.0F, 0.0F, width3rd, height3rd, 16, 16);
      guiGraphics.fill(width3rd, height3rd, width3rd * 2, height3rd * 2, ARGB32.color(150, 0, 0, 0));
      guiGraphics.drawCenteredString(
         Minecraft.getInstance().font, AnnouncementInfo.getInstance().title(), width3rd + width3rd / 2, height3rd + 5, ARGB32.color(255, 255, 255, 255)
      );
      guiGraphics.drawWordWrap(
         Minecraft.getInstance().font, AnnouncementInfo.getInstance().desc(), width3rd + 5, height3rd + 25, width3rd - 10, ARGB32.color(255, 255, 255, 255)
      );
   }

   public static void createAlertWidget(Screen screen, int scaledWidth, int scaledHeight) {
      if (!(screen instanceof EmergencyAnnouncementScreen)) {
         if (AnnouncementInfo.getInstance() != null
            && AnnouncementConfig.INSTANCE.get().announcementDelivery() == AnnouncementConfig.AnnouncementDelivery.WIDGET) {
            MutableComponent helpCorgiTaco = Component.literal("Help Corgi Taco");
            SpriteIconButton build = new net.minecraft.client.gui.components.SpriteIconButton.Builder(
                  helpCorgiTaco, button -> Minecraft.getInstance().setScreen(new EmergencyAnnouncementScreen(Component.literal("Announcement Info"))), true
               )
               .sprite(CorgiLib.createLocation("alert"), 32, 32)
               .size(32, 32)
               .build();
            build.setPosition(scaledWidth - 50, scaledHeight - 50);
            build.setTooltip(Tooltip.create(helpCorgiTaco));
            screen.addRenderableWidget(build);
         }
      }
   }
}
