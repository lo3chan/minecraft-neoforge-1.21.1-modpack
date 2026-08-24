package net.blay09.mods.inventoryessentials;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum InventorySorting implements StringRepresentable {
   CONSOLIDATE_ONLY,
   RETAIN_ORDER,
   ALPHABETICAL,
   CREATIVE;

   public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
