package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigScreenList extends TConfigScreen {
   private final TConfigEntry[] options;
   private final TConfigScreenList.Align align;
   protected boolean fullWidthBackgroundEvenIfSmaller = false;
   private TConfigScreenList.Renderable renderFeature = null;
   private TConfigEntryListWidget list;
   private final EditBox search;

   public TConfigScreenList(
      @Translatable String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges, TConfigScreenList.Align align
   ) {
      super(title, parent, true);
      this.options = options;
      this.parent = parent;
      this.resetDefaultValuesRunnable = resetValuesToDefault;
      this.undoChangesRunnable = undoChanges;
      this.align = align;
      if (options.length > 12) {
         this.search = new EditBox(Minecraft.getInstance().font, 4, 0, 120, 20, Component.literal(""));
         this.search.setResponder(this::updateSearch);
         this.search.setHint(ETF.getTextFromTranslation("config.entity_features.search"));
      } else {
         this.search = null;
      }
   }

   public TConfigScreenList(@Translatable String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges) {
      this(title, parent, options, resetValuesToDefault, undoChanges, TConfigScreenList.Align.CENTER);
   }

   public void setRenderFeature(TConfigScreenList.Renderable renderFeature) {
      this.renderFeature = renderFeature;
   }

   @Override
   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      if (this.renderFeature != null) {
         this.renderFeature.render(context, mouseX, mouseY);
      }
   }

   public void setWidgetBackgroundToFullWidth() {
      this.fullWidthBackgroundEvenIfSmaller = true;
   }

   @Override
   protected void init() {
      super.init();
      if (this.search != null) {
         int y = (int)(this.height * 0.15) - 24;
         int width = (int)(this.width * 0.2);
         this.search.setY(y);
         this.search.setWidth(width);
         this.addRenderableWidget(this.search);
      }

      this.initList(this.options);
   }

   private void updateSearch(String searchText) {
      assert this.search != null;

      this.removeWidget(this.list);
      ArrayList<TConfigEntry> list = new ArrayList<>();
      if (searchText.isBlank()) {
         this.initList(this.options);
      } else {
         Arrays.stream(this.options).filter(it -> it.getText().getString().contains(searchText)).forEach(list::add);
         this.initList(list.toArray(new TConfigEntry[0]));
      }
   }

   private void initList(TConfigEntry[] subList) {
      int width;
      int x;
      switch (this.align) {
         case LEFT:
            width = (int)(this.width * 0.3);
            x = (int)(this.width * 0.1);
            break;
         case RIGHT:
            width = (int)(this.width * 0.3);
            x = (int)(this.width * 0.6);
            break;
         default:
            width = this.width;
            x = 0;
      }

      this.list = (TConfigEntryListWidget)this.addRenderableWidget(
         new TConfigEntryListWidget(width, (int)(this.height * 0.7), (int)(this.height * 0.15), x, 24, subList)
      );
      if (this.fullWidthBackgroundEvenIfSmaller) {
         this.list.setWidgetBackgroundToFullWidth();
      }
   }

   public static enum Align {
      LEFT,
      CENTER,
      RIGHT;
   }

   public interface Renderable {
      void render(GuiGraphics var1, int var2, int var3);
   }
}
