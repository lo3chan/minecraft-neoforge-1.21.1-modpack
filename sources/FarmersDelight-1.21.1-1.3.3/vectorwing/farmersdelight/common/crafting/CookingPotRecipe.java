package vectorwing.farmersdelight.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public class CookingPotRecipe implements Recipe<RecipeWrapper> {
   public static final int INPUT_SLOTS = 6;
   private final String group;
   private final CookingPotRecipeBookTab tab;
   private final NonNullList<Ingredient> inputItems;
   private final ItemStack output;
   private final ItemStack container;
   private final ItemStack containerOverride;
   private final float experience;
   private final int cookTime;

   public CookingPotRecipe(
      String group,
      @Nullable CookingPotRecipeBookTab tab,
      NonNullList<Ingredient> inputItems,
      ItemStack output,
      ItemStack container,
      float experience,
      int cookTime
   ) {
      this.group = group;
      this.tab = tab;
      this.inputItems = inputItems;
      this.output = output;
      if (!container.isEmpty()) {
         this.container = container;
      } else if (!output.getCraftingRemainingItem().isEmpty()) {
         this.container = output.getCraftingRemainingItem();
      } else {
         this.container = ItemStack.EMPTY;
      }

      this.containerOverride = container;
      this.experience = experience;
      this.cookTime = cookTime;
   }

   public String getGroup() {
      return this.group;
   }

   @Nullable
   public CookingPotRecipeBookTab getRecipeBookTab() {
      return this.tab;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.inputItems;
   }

   public ItemStack getResultItem(Provider provider) {
      return this.output;
   }

   public ItemStack getOutputContainer() {
      return this.container;
   }

   public ItemStack getContainerOverride() {
      return this.containerOverride;
   }

   public ItemStack assemble(RecipeWrapper inv, Provider provider) {
      return this.output.copy();
   }

   public float getExperience() {
      return this.experience;
   }

   public int getCookTime() {
      return this.cookTime;
   }

   public boolean matches(RecipeWrapper inv, Level level) {
      List<ItemStack> inputs = new ArrayList<>();
      int i = 0;

      for (int j = 0; j < 6; j++) {
         ItemStack itemstack = inv.getItem(j);
         if (!itemstack.isEmpty()) {
            i++;
            inputs.add(itemstack);
         }
      }

      return i == this.inputItems.size() && RecipeMatcher.findMatches(inputs, this.inputItems) != null;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.inputItems.size();
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipeSerializers.COOKING.get();
   }

   public RecipeType<?> getType() {
      return ModRecipeTypes.COOKING.get();
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModItems.COOKING_POT.get());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CookingPotRecipe that = (CookingPotRecipe)o;
         if (Float.compare(that.getExperience(), this.getExperience()) != 0) {
            return false;
         } else if (this.getCookTime() != that.getCookTime()) {
            return false;
         } else if (!this.getGroup().equals(that.getGroup())) {
            return false;
         } else if (this.tab != that.tab) {
            return false;
         } else if (!this.inputItems.equals(that.inputItems)) {
            return false;
         } else {
            return !this.output.equals(that.output) ? false : this.container.equals(that.container);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.getGroup().hashCode();
      result = 31 * result + (this.getRecipeBookTab() != null ? this.getRecipeBookTab().hashCode() : 0);
      result = 31 * result + this.inputItems.hashCode();
      result = 31 * result + this.output.hashCode();
      result = 31 * result + this.container.hashCode();
      result = 31 * result + (this.getExperience() != 0.0F ? Float.floatToIntBits(this.getExperience()) : 0);
      return 31 * result + this.getCookTime();
   }

   public static class Serializer implements RecipeSerializer<CookingPotRecipe> {
      private static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(CookingPotRecipe::getGroup),
               CookingPotRecipeBookTab.CODEC.optionalFieldOf("recipe_book_tab", CookingPotRecipeBookTab.MISC).forGetter(CookingPotRecipe::getRecipeBookTab),
               Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").xmap(ingredients -> {
                  NonNullList<Ingredient> nonNullList = NonNullList.create();
                  nonNullList.addAll(ingredients);
                  return nonNullList;
               }, ingredients -> ingredients).forGetter(CookingPotRecipe::getIngredients),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output),
               ItemStack.STRICT_CODEC.optionalFieldOf("container", ItemStack.EMPTY).forGetter(CookingPotRecipe::getContainerOverride),
               Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(CookingPotRecipe::getExperience),
               Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(CookingPotRecipe::getCookTime)
            )
            .apply(inst, CookingPotRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC = StreamCodec.of(
         CookingPotRecipe.Serializer::toNetwork, CookingPotRecipe.Serializer::fromNetwork
      );

      public MapCodec<CookingPotRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static CookingPotRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String group = buffer.readUtf();
         CookingPotRecipeBookTab tab = CookingPotRecipeBookTab.findByName(buffer.readUtf());
         int i = buffer.readVarInt();
         NonNullList<Ingredient> inputItems = NonNullList.withSize(i, Ingredient.EMPTY);
         inputItems.replaceAll(ignored -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         ItemStack container = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
         float experience = buffer.readFloat();
         int cookTime = buffer.readVarInt();
         return new CookingPotRecipe(group, tab, inputItems, output, container, experience, cookTime);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, CookingPotRecipe recipe) {
         buffer.writeUtf(recipe.group);
         buffer.writeUtf(recipe.tab != null ? recipe.tab.toString() : "");
         buffer.writeVarInt(recipe.inputItems.size());

         for (Ingredient ingredient : recipe.inputItems) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.container);
         buffer.writeFloat(recipe.experience);
         buffer.writeVarInt(recipe.cookTime);
      }
   }
}
