package dev.tr7zw.waveycapes;

import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen.OptionInstance;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WListPanel;
import dev.tr7zw.trender.gui.widget.WPlayerPreview;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.config.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class WaveyCapesConfigScreen {
   public static Screen createConfigScreen(Screen parent) {
      return new WaveyCapesConfigScreen.CustomConfigScreen(parent).createScreen();
   }

   private static class CustomConfigScreen extends AbstractConfigScreen {
      public CustomConfigScreen(Screen previous) {
         super(ComponentProvider.translatable("text.wc.title"), previous);
         WGridPanel root = new WGridPanel(8);
         root.setInsets(Insets.ROOT_PANEL);
         this.setRootPanel(root);
         List<OptionInstance> options = new ArrayList<>();
         options.add(this.getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> ModBase.config.capeStyle, v -> ModBase.config.capeStyle = v));
         options.add(this.getEnumOption("text.wc.setting.windmode", WindMode.class, () -> ModBase.config.windMode, v -> ModBase.config.windMode = v));
         options.add(
            this.getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> ModBase.config.capeMovement, v -> ModBase.config.capeMovement = v)
         );
         options.add(this.getIntOption("text.wc.setting.gravity", 5, 32, () -> ModBase.config.gravity, v -> ModBase.config.gravity = v));
         options.add(
            this.getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> ModBase.config.heightMultiplier, v -> ModBase.config.heightMultiplier = v)
         );
         WListPanel<OptionInstance, WGridPanel> optionList = this.createOptionList(options);
         optionList.setGap(-1);
         optionList.setSize(280, 180);
         root.add(optionList, 0, 1, 29, 25);
         WButton doneButton = new WButton(CommonComponents.GUI_DONE);
         doneButton.setOnClick(() -> {
            this.save();
            GeneralUtil.setScreen(previous);
         });
         root.add(doneButton, 0, 26, 6, 2);
         WPlayerPreview playerPreview = new WPlayerPreview();
         playerPreview.setRotationX(164);
         playerPreview.setRotationY(5);
         playerPreview.setShowBackground(true);
         root.add(playerPreview, 10, 14);
         WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
         resetButton.setOnClick(() -> {
            this.reset();
            root.layout();
         });
         root.add(resetButton, 23, 26, 6, 2);
         root.setBackgroundPainter(BackgroundPainter.VANILLA);
         root.validate(this);
         root.setHost(this);
      }

      public void reset() {
         ModBase.config = new Config();
      }

      public void save() {
         WaveyCapesBase.INSTANCE.writeConfig();
      }
   }
}
