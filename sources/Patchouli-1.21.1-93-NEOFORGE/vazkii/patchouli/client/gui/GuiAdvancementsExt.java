package vazkii.patchouli.client.gui;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.resources.ResourceLocation;

public class GuiAdvancementsExt extends AdvancementsScreen {
   Screen parent;

   public GuiAdvancementsExt(ClientAdvancements manager, Screen parent, ResourceLocation tab) {
      super(manager);
      this.parent = parent;
      AdvancementHolder start = manager.get(tab);
      if (start != null && vazkii.patchouli.client.base.ClientAdvancements.hasDone(start.id().toString())) {
         manager.setSelectedTab(start, false);
      }
   }

   public boolean keyPressed(int key, int scanCode, int modifiers) {
      if (!this.minecraft.options.keyAdvancements.matches(key, scanCode) && scanCode != 1) {
         return super.keyPressed(key, scanCode, modifiers);
      } else {
         this.minecraft.setScreen(this.parent);
         return true;
      }
   }
}
