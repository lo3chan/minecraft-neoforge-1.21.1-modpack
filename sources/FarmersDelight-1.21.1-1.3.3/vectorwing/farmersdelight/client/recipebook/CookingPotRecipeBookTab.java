package vectorwing.farmersdelight.client.recipebook;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.EnumSet;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum CookingPotRecipeBookTab implements StringRepresentable {
   MEALS("meals"),
   DRINKS("drinks"),
   MISC("misc");

   public static final Codec<CookingPotRecipeBookTab> CODEC = Codec.STRING
      .flatXmap(
         s -> {
            CookingPotRecipeBookTab tab = findByName(s);
            return tab == null
               ? DataResult.error(
                  () -> "Optional field 'recipe_book_tab' does not match any valid tab. If defined, must be one of the following: "
                     + EnumSet.<CookingPotRecipeBookTab>allOf(CookingPotRecipeBookTab.class)
               )
               : DataResult.success(tab);
         },
         tab -> DataResult.success(tab.toString())
      );
   public final String name;

   private CookingPotRecipeBookTab(String name) {
      this.name = name;
   }

   public static CookingPotRecipeBookTab findByName(String name) {
      for (CookingPotRecipeBookTab value : values()) {
         if (value.name.equals(name)) {
            return value;
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }
}
