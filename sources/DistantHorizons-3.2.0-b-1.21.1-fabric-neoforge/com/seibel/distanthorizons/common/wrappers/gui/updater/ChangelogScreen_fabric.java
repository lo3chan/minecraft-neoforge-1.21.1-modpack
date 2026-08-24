package com.seibel.distanthorizons.common.wrappers.gui.updater;

import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreen_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_fabric;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.installer.MarkdownFormatter;
import com.seibel.distanthorizons.core.jar.installer.ModrinthGetter;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class ChangelogScreen_fabric extends DhScreen_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private class_437 parent;
   private String versionID;
   private List<String> changelog;
   private ChangelogScreen$TextArea_fabric changelogArea;
   public boolean usable = false;

   public ChangelogScreen_fabric(class_437 parent) {
      this(parent, null);
      if (!ModrinthGetter.initted) {
         ModrinthGetter.init();
      }

      if (ModrinthGetter.initted) {
         if (ModrinthGetter.mcVersions.contains(SingletonInjector.INSTANCE.get(IVersionConstants.class).getMinecraftVersion())) {
            String versionID = ModrinthGetter.getLatestIDForVersion(SingletonInjector.INSTANCE.get(IVersionConstants.class).getMinecraftVersion());
            if (versionID != null) {
               try {
                  this.setupChangelog(versionID);
                  this.usable = true;
               } catch (Exception var4) {
                  LOGGER.error("failed to setup changelog, error: [" + var4.getMessage() + "].", var4);
               }
            }
         }
      }
   }

   public ChangelogScreen_fabric(class_437 parent, String versionID) {
      super(GuiHelper_fabric.Translatable("distanthorizons.updater.title"));
      this.parent = parent;
      this.versionID = versionID;
      if (versionID != null) {
         try {
            this.setupChangelog(versionID);
            this.usable = true;
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   private void setupChangelog(String versionID) {
      this.changelog = new ArrayList<>();
      this.changelog.add("§lChangelog for " + ModrinthGetter.releaseNames.get(versionID) + "§r");
      this.changelog.add("");
      this.changelog.add("");
      String changelog = ModrinthGetter.changeLogs.get(versionID);
      if (changelog == null) {
         changelog = "";
      }

      String[] unwrappedChangelog = new MarkdownFormatter.MinecraftFormat().convertTo(changelog).split("\\n");

      for (String str : unwrappedChangelog) {
         this.changelog.addAll(MarkdownFormatter.splitString(str, 75));
      }
   }

   protected void method_25426() {
      super.method_25426();
      if (this.usable) {
         this.addBtn(
            GuiHelper_fabric.MakeBtn(
               GuiHelper_fabric.Translatable("distanthorizons.general.back"), 5, this.field_22790 - 25, 100, 20, btn -> this.method_25419()
            )
         );
         this.changelogArea = new ChangelogScreen$TextArea_fabric(this.field_22787, this.field_22789 * 2, this.field_22790, 32, 32, 10);

         for (int i = 0; i < this.changelog.size(); i++) {
            this.changelogArea.addButton(GuiHelper_fabric.TextOrLiteral(this.changelog.get(i)));
         }
      }
   }

   public void method_25394(class_332 matrices, int mouseX, int mouseY, float delta) {
      this.method_25420(matrices, mouseX, mouseY, delta);
      if (this.usable) {
         int maxScroll = this.changelogArea.method_25331();
         double scrollAmount = (double)mouseY / this.field_22790 * 1.1 * maxScroll;
         this.changelogArea.field_22749 = scrollAmount;
         super.method_25394(matrices, mouseX, mouseY, delta);
         this.changelogArea.method_25394(matrices, mouseX, mouseY, delta);
         this.DhDrawCenteredString(matrices, this.field_22793, this.field_22785, this.field_22789 / 2, 15, 16777215);
      }
   }

   public void method_25419() {
      DhScreenUtil_fabric.setScreen(this.parent);
   }
}
