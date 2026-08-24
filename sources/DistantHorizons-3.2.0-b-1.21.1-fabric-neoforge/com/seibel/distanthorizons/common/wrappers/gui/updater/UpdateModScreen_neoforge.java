package com.seibel.distanthorizons.common.wrappers.gui.updater;

import com.seibel.distanthorizons.api.enums.config.EDhApiUpdateBranch;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreen_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.TexturedButtonWidget_neoforge;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.jar.ModJarInfo;
import com.seibel.distanthorizons.core.jar.installer.ModrinthGetter;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.ModInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class UpdateModScreen_neoforge extends DhScreen_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private Screen parent;
   private String newVersionID;
   private String currentVer;
   private String nextVer;

   public UpdateModScreen_neoforge(Screen parent, String newVersionID) throws IllegalArgumentException {
      super(GuiHelper_neoforge.Translatable("distanthorizons.updater.title"));
      this.parent = parent;
      this.newVersionID = newVersionID;
      EDhApiUpdateBranch updateBranch = EDhApiUpdateBranch.convertAutoToStableOrNightly(Config.Client.Advanced.AutoUpdater.updateBranch.get());
      if (updateBranch == EDhApiUpdateBranch.STABLE) {
         this.currentVer = "3.2.0-b";
         this.nextVer = ModrinthGetter.releaseNames.get(this.newVersionID);
      } else {
         this.currentVer = ModJarInfo.Git_Commit.substring(0, 7);
         this.nextVer = this.newVersionID.substring(0, 7);
      }

      if (this.nextVer == null) {
         throw new IllegalArgumentException("No new version found with the ID [" + newVersionID + "].");
      }
   }

   protected void init() {
      super.init();

      try {
         this.addBtn(
            new TexturedButtonWidget_neoforge(
               this.width / 2 - 95,
               this.height / 2 - 110,
               195,
               65,
               0,
               0,
               0,
               ResourceLocation.fromNamespaceAndPath("distanthorizons", "logo.png"),
               195,
               65,
               buttonWidget -> LOGGER.info("Nice, you found an Easter egg :)"),
               GuiHelper_neoforge.Translatable("distanthorizons.updater.title"),
               false
            )
         );
      } catch (Exception var2) {
         LOGGER.error("Failed to setup update mod screen, error: [" + var2.getMessage() + "].", var2);
      }

      if (!ModInfo.IS_DEV_BUILD) {
         this.addBtn(
            new TexturedButtonWidget_neoforge(
               this.width / 2 - 97,
               this.height / 2 + 8,
               20,
               20,
               0,
               0,
               0,
               ResourceLocation.fromNamespaceAndPath("distanthorizons", "textures/gui/changelog.png"),
               20,
               20,
               buttonWidget -> DhScreenUtil_neoforge.setScreen(new ChangelogScreen_neoforge(this, this.newVersionID)),
               GuiHelper_neoforge.Translatable("distanthorizons.updater.title")
            )
         );
      }

      this.addBtn(
         GuiHelper_neoforge.MakeBtn(
            GuiHelper_neoforge.Translatable("distanthorizons.updater.update"), this.width / 2 - 75, this.height / 2 + 8, 150, 20, btn -> {
               SelfUpdater.updateMod();
               this.onClose();
            }
         )
      );
      this.addBtn(
         GuiHelper_neoforge.MakeBtn(
            GuiHelper_neoforge.Translatable("distanthorizons.updater.silent"), this.width / 2 - 75, this.height / 2 + 30, 150, 20, btn -> {
               Config.Client.Advanced.AutoUpdater.enableSilentUpdates.set(true);
               SelfUpdater.updateMod();
               this.onClose();
            }
         )
      );
      this.addBtn(
         GuiHelper_neoforge.MakeBtn(
            GuiHelper_neoforge.Translatable("distanthorizons.updater.later"), this.width / 2 + 2, this.height / 2 + 70, 100, 20, btn -> this.onClose()
         )
      );
      this.addBtn(
         GuiHelper_neoforge.MakeBtn(
            GuiHelper_neoforge.Translatable("distanthorizons.updater.never"), this.width / 2 - 102, this.height / 2 + 70, 100, 20, btn -> {
               Config.Client.Advanced.AutoUpdater.enableAutoUpdater.set(false);
               this.onClose();
            }
         )
      );
   }

   public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices, mouseX, mouseY, delta);
      super.render(matrices, mouseX, mouseY, delta);
      this.DhDrawCenteredString(
         matrices, this.font, GuiHelper_neoforge.Translatable("distanthorizons.updater.updateAvailable"), this.width / 2, this.height / 2 - 35, 16777215
      );
      this.DhDrawCenteredString(
         matrices,
         this.font,
         GuiHelper_neoforge.Translatable("distanthorizons.updater.updateConfirmation", this.currentVer, this.nextVer),
         this.width / 2,
         this.height / 2 - 20,
         5438802
      );
   }

   public void onClose() {
      DhScreenUtil_neoforge.setScreen(this.parent);
   }
}
