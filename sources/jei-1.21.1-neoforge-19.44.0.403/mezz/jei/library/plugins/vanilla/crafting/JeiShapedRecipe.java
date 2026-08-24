package mezz.jei.library.plugins.vanilla.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import mezz.jei.library.recipes.RecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public class JeiShapedRecipe implements CraftingRecipe {
   private final ShapedRecipePattern pattern;
   private final List<ItemStack> results;
   private final String group;
   private final CraftingBookCategory category;

   public JeiShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, List<ItemStack> results) {
      this.group = group;
      this.category = category;
      this.pattern = pattern;
      this.results = results;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializers.getJeiShapedRecipeSerializer();
   }

   public String getGroup() {
      return this.group;
   }

   public CraftingBookCategory category() {
      return this.category;
   }

   public ItemStack getResultItem(Provider registries) {
      return (ItemStack)this.results.getFirst();
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.pattern.ingredients();
   }

   public boolean showNotification() {
      return false;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width >= this.pattern.width() && height >= this.pattern.height();
   }

   public boolean matches(CraftingInput input, Level level) {
      return this.pattern.matches(input);
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      return this.getResultItem(registries).copy();
   }

   public int getWidth() {
      return this.pattern.width();
   }

   public int getHeight() {
      return this.pattern.height();
   }

   public boolean isIncomplete() {
      NonNullList<Ingredient> nonNullList = this.getIngredients();
      return nonNullList.isEmpty()
         || nonNullList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getItems().length == 0);
   }

   public static class Serializer implements RecipeSerializer<JeiShapedRecipe> {
      public static final MapCodec<JeiShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(shapedRecipe -> shapedRecipe.group),
               CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(shapedRecipe -> shapedRecipe.category),
               ShapedRecipePattern.MAP_CODEC.forGetter(shapedRecipe -> shapedRecipe.pattern),
               Codec.list(ItemStack.STRICT_CODEC).fieldOf("result").forGetter(shapedRecipe -> shapedRecipe.results)
            )
            .apply(instance, JeiShapedRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, JeiShapedRecipe> STREAM_CODEC = StreamCodec.of(
         JeiShapedRecipe.Serializer::toNetwork, JeiShapedRecipe.Serializer::fromNetwork
      );

      public MapCodec<JeiShapedRecipe> codec() {
         return CODEC;
      }

      @Deprecated
      public StreamCodec<RegistryFriendlyByteBuf, JeiShapedRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static JeiShapedRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String string = buffer.readUtf();
         CraftingBookCategory craftingBookCategory = (CraftingBookCategory)buffer.readEnum(CraftingBookCategory.class);
         ShapedRecipePattern shapedRecipePattern = (ShapedRecipePattern)ShapedRecipePattern.STREAM_CODEC.decode(buffer);
         List<ItemStack> results = (List<ItemStack>)ItemStack.LIST_STREAM_CODEC.decode(buffer);
         return new JeiShapedRecipe(string, craftingBookCategory, shapedRecipePattern, results);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, JeiShapedRecipe recipe) {
         buffer.writeUtf(recipe.group);
         buffer.writeEnum(recipe.category);
         ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
         ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.results);
      }
   }
}
