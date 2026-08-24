package com.seibel.distanthorizons.common.wrappers.gui.updater;

import com.seibel.distanthorizons.api.enums.config.EDhApiUpdateBranch;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreen_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.TexturedButtonWidget_fabric;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.jar.ModJarInfo;
import com.seibel.distanthorizons.core.jar.installer.ModrinthGetter;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.ModInfo;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class UpdateModScreen_fabric extends DhScreen_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private class_437 parent;
   private String newVersionID;
   private String currentVer;
   private String nextVer;

   public UpdateModScreen_fabric(class_437 parent, String newVersionID) throws IllegalArgumentException {
      super(GuiHelper_fabric.Translatable("distanthorizons.updater.title"));
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

   protected void method_25426() {
      super.method_25426();

      try {
         this.addBtn(
            new TexturedButtonWidget_fabric(
               this.field_22789 / 2 - 95,
               this.field_22790 / 2 - 110,
               195,
               65,
               0,
               0,
               0,
               class_2960.method_60655("distanthorizons", "logo.png"),
               195,
               65,
               buttonWidget -> LOGGER.info("Nice, you found an Easter egg :)"),
               GuiHelper_fabric.Translatable("distanthorizons.updater.title"),
               false
            )
         );
      } catch (Exception var2) {
         LOGGER.error("Failed to setup update mod screen, error: [" + var2.getMessage() + "].", var2);
      }

      if (!ModInfo.IS_DEV_BUILD) {
         this.addBtn(
            new TexturedButtonWidget_fabric(
               this.field_22789 / 2 - 97,
               this.field_22790 / 2 + 8,
               20,
               20,
               0,
               0,
               0,
               class_2960.method_60655("distanthorizons", "textures/gui/changelog.png"),
               20,
               20,
               buttonWidget -> DhScreenUtil_fabric.setScreen(new ChangelogScreen_fabric(this, this.newVersionID)),
               GuiHelper_fabric.Translatable("distanthorizons.updater.title")
            )
         );
      }

      this.addBtn(
         GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.updater.update"), this.field_22789 / 2 - 75, this.field_22790 / 2 + 8, 150, 20, btn -> {
               SelfUpdater.updateMod();
               this.method_25419();
            }
         )
      );
      this.addBtn(
         GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.updater.silent"), this.field_22789 / 2 - 75, this.field_22790 / 2 + 30, 150, 20, btn -> {
               Config.Client.Advanced.AutoUpdater.enableSilentUpdates.set(true);
               SelfUpdater.updateMod();
               this.method_25419();
            }
         )
      );
      this.addBtn(
         GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.updater.later"),
            this.field_22789 / 2 + 2,
            this.field_22790 / 2 + 70,
            100,
            20,
            btn -> this.method_25419()
         )
      );
      this.addBtn(
         GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.updater.never"), this.field_22789 / 2 - 102, this.field_22790 / 2 + 70, 100, 20, btn -> {
               Config.Client.Advanced.AutoUpdater.enableAutoUpdater.set(false);
               this.method_25419();
            }
         )
      );
   }

   public void method_25394(class_332 matrices, int mouseX, int mouseY, float delta) {
      this.method_25420(matrices, mouseX, mouseY, delta);
      super.method_25394(matrices, mouseX, mouseY, delta);
      this.DhDrawCenteredString(
         matrices,
         this.field_22793,
         GuiHelper_fabric.Translatable("distanthorizons.updater.updateAvailable"),
         this.field_22789 / 2,
         this.field_22790 / 2 - 35,
         16777215
      );
      this.DhDrawCenteredString(
         matrices,
         this.field_22793,
         GuiHelper_fabric.Translatable("distanthorizons.updater.updateConfirmation", this.currentVer, this.nextVer),
         this.field_22789 / 2,
         this.field_22790 / 2 - 20,
         5438802
      );
   }

   public void method_25419() {
      DhScreenUtil_fabric.setScreen(this.parent);
   }
}
