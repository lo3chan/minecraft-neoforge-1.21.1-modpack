package vectorwing.farmersdelight.common.block.state;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum CookingPotSupport implements StringRepresentable {
   NONE("none"),
   TRAY("tray"),
   HANDLE("handle");

   private final String supportName;

   private CookingPotSupport(String name) {
      this.supportName = name;
   }

   @Override
   public String toString() {
      return this.getSerializedName();
   }

   @NotNull
   public String getSerializedName() {
      return this.supportName;
   }
}
