package vectorwing.farmersdelight.common.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUtils {
   public static ItemStack getResultItem(Recipe<?> recipe) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      if (level == null) {
         throw new NullPointerException("level must not be null.");
      } else {
         RegistryAccess registryAccess = level.registryAccess();
         return recipe.getResultItem(registryAccess);
      }
   }

   public static ResourceLocation FDLocation(String name) {
      return ResourceLocation.fromNamespaceAndPath("farmersdelight", name);
   }
}
