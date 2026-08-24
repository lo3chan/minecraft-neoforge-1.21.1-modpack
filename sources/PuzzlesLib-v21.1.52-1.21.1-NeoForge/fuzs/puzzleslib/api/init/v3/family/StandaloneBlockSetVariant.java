package fuzs.puzzleslib.api.init.v3.family;

import java.util.Objects;
import net.minecraft.data.BlockFamily.Variant;
import org.jetbrains.annotations.Nullable;

public abstract class StandaloneBlockSetVariant implements BlockSetVariant {
   private final String name;

   public StandaloneBlockSetVariant(String name) {
      this.name = name;
   }

   public StandaloneBlockSetVariant(Variant variant) {
      this(variant.getRecipeGroup());
   }

   @Nullable
   @Override
   public Variant toVanilla() {
      return null;
   }

   @Override
   public String toString() {
      return "Standalone[" + this.getSerializedName() + "]";
   }

   public String getSerializedName() {
      return this.name;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return obj instanceof BlockSetVariant variant ? Objects.equals(this.getSerializedName(), variant.getSerializedName()) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getSerializedName());
   }
}
