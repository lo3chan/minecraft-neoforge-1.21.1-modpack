package traben.entity_texture_features.config.screens;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreen;

public class ETFConfigScreenWarnings extends TConfigScreen {
   final Set<ETFConfigWarning> warningsFound;

   public ETFConfigScreenWarnings(Screen parent, Set<ETFConfigWarning> warningsFound) {
      super("config.entity_texture_features.warnings.title", parent, true);
      this.warningsFound = warningsFound;
   }

   public static Set<String> getIgnoredWarnings() {
      return ETF.warningConfigHandler.getConfig().ignoredConfigIds;
   }

   @Override
   public void onClose() {
      ETF.warningConfigHandler.saveToFile();
      super.onClose();
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(Button.builder(ETF.getTextFromTranslation("config.entity_texture_features.ignore_all"), button -> {
         for (ETFConfigWarning warn : ETFConfigWarnings.getRegisteredWarnings()) {
            getIgnoredWarnings().add(warn.getID());
         }

         this.rebuildWidgets();
      }).bounds((int)(this.width * 0.25), (int)(this.height * 0.9), (int)(this.width * 0.2), 20).build());
      double offset = 0.0;

      for (ETFConfigWarning warning : this.warningsFound) {
         if (warning.doesShowDisableButton()) {
            Button butt = Button.builder(
                  Component.nullToEmpty(
                     ETF.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString()
                        + (getIgnoredWarnings().contains(warning.getID()) ? CommonComponents.GUI_YES : CommonComponents.GUI_NO).getString()
                  ),
                  button -> {
                     if (getIgnoredWarnings().contains(warning.getID())) {
                        getIgnoredWarnings().remove(warning.getID());
                     } else {
                        getIgnoredWarnings().add(warning.getID());
                     }

                     button.setMessage(
                        Component.nullToEmpty(
                           ETF.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString()
                              + (getIgnoredWarnings().contains(warning.getID()) ? CommonComponents.GUI_YES : CommonComponents.GUI_NO).getString()
                        )
                     );
                  }
               )
               .bounds((int)(this.width * 0.75), (int)(this.height * (0.25 + offset)), (int)(this.width * 0.17), 20)
               .tooltip(Tooltip.create(ETF.getTextFromTranslation("config.entity_texture_features.ignore_description")))
               .build();
            this.addRenderableWidget(butt);
         }

         offset += 0.1;
      }
   }

   @Override
   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      context.drawCenteredString(
         this.font, ETF.getTextFromTranslation("config.entity_texture_features.warn_instruction"), (int)(this.width * 0.5), (int)(this.height * 0.18), 16777215
      );
      double offset = 0.0;

      for (ETFConfigWarning warning : this.warningsFound) {
         context.drawString(this.font, ETF.getTextFromTranslation(warning.getTitle()), (int)(this.width * 0.05), (int)(this.height * (0.25 + offset)), 16777215);
         context.drawString(
            this.font, ETF.getTextFromTranslation(warning.getSubTitle()), (int)(this.width * 0.05), (int)(this.height * (0.29 + offset)), 8947848
         );
         offset += 0.1;
      }
   }

   public static class WarningConfig extends TConfig.NoGUI {
      public Set<String> ignoredConfigIds = new HashSet<>();
   }
}
