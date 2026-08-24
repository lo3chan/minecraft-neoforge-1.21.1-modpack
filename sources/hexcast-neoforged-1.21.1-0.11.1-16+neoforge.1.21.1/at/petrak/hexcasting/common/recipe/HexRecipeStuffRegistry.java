package at.petrak.hexcasting.common.recipe;

import at.petrak.hexcasting.api.HexAPI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class HexRecipeStuffRegistry {
   private static final Map<ResourceLocation, RecipeSerializer<?>> SERIALIZERS = new LinkedHashMap<>();
   private static final Map<ResourceLocation, RecipeType<?>> TYPES = new LinkedHashMap<>();
   public static final RecipeSerializer<?> BRAINSWEEP = registerSerializer("brainsweep", new BrainsweepRecipe.Serializer());
   public static final RecipeSerializer<SealThingsRecipe> SEAL_FOCUS = registerSerializer("seal_focus", SealThingsRecipe.FOCUS_SERIALIZER);
   public static final RecipeSerializer<SealThingsRecipe> SEAL_SPELLBOOK = registerSerializer("seal_spellbook", SealThingsRecipe.SPELLBOOK_SERIALIZER);
   public static RecipeType<BrainsweepRecipe> BRAINSWEEP_TYPE = registerType("brainsweep");

   public static void registerSerializers(BiConsumer<RecipeSerializer<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, RecipeSerializer<?>> e : SERIALIZERS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   public static void registerTypes(BiConsumer<RecipeType<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, RecipeType<?>> e : TYPES.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String name, RecipeSerializer<T> rs) {
      RecipeSerializer<?> old = SERIALIZERS.put(HexAPI.modLoc(name), rs);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return rs;
      }
   }

   private static <T extends Recipe<?>> RecipeType<T> registerType(final String name) {
      var type = new RecipeType<T>() {
         @Override
         public String toString() {
            return "hexcasting:" + name;
         }
      };
      TYPES.put(HexAPI.modLoc(name), type);
      return type;
   }
}
