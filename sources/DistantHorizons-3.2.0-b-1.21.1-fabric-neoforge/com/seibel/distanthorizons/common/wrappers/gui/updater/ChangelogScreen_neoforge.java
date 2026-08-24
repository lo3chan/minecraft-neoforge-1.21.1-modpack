package com.seibel.distanthorizons.common.wrappers.gui.updater;

import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreen_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_neoforge;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.installer.MarkdownFormatter;
import com.seibel.distanthorizons.core.jar.installer.ModrinthGetter;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class ChangelogScreen_neoforge extends DhScreen_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private Screen parent;
   private String versionID;
   private List<String> changelog;
   private ChangelogScreen$TextArea_neoforge changelogArea;
   public boolean usable = false;

   public ChangelogScreen_neoforge(Screen parent) {
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

   public ChangelogScreen_neoforge(Screen parent, String versionID) {
      super(GuiHelper_neoforge.Translatable("distanthorizons.updater.title"));
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

   protected void init() {
      super.init();
      if (this.usable) {
         this.addBtn(
            GuiHelper_neoforge.MakeBtn(GuiHelper_neoforge.Translatable("distanthorizons.general.back"), 5, this.height - 25, 100, 20, btn -> this.onClose())
         );
         this.changelogArea = new ChangelogScreen$TextArea_neoforge(this.minecraft, this.width * 2, this.height, 32, 32, 10);

         for (int i = 0; i < this.changelog.size(); i++) {
            this.changelogArea.addButton(GuiHelper_neoforge.TextOrLiteral(this.changelog.get(i)));
         }
      }
   }

   public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices, mouseX, mouseY, delta);
      if (this.usable) {
         int maxScroll = this.changelogArea.getMaxScroll();
         double scrollAmount = (double)mouseY / this.height * 1.1 * maxScroll;
         this.changelogArea.scrollAmount = scrollAmount;
         super.render(matrices, mouseX, mouseY, delta);
         this.changelogArea.render(matrices, mouseX, mouseY, delta);
         this.DhDrawCenteredString(matrices, this.font, this.title, this.width / 2, 15, 16777215);
      }
   }

   public void onClose() {
      DhScreenUtil_neoforge.setScreen(this.parent);
   }
}
