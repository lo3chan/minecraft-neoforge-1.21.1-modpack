package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

public class EnumSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public String filter = "";
   private Enum<?> selectedEnum;

   public EnumSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m, Enum<?> selectedEnum) {
      super(minecraft, i, j, k, l, m);
      this.selectedEnum = selectedEnum;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      LabelEntry first = null;
      Enum<?>[] enums = (Enum<?>[])this.selectedEnum.getDeclaringClass().getEnumConstants();

      for (Enum<?> cenum : enums) {
         String name = Language.getInstance().getOrDefault(cenum.toString());
         if (name.contains(this.filter.toLowerCase())) {
            LabelEntry listEntry = new LabelEntry(this, name);
            listEntry.setUserData(cenum);
            this.addEntry(listEntry);
            if (first == null) {
               first = listEntry;
            }
         }
      }

      if (first != null) {
         this.ensureVisible(first);
      }
   }
}
