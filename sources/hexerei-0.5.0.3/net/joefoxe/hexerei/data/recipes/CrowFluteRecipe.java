package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nonnull;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class CrowFluteRecipe extends ShapedRecipe {
   NonNullList<Ingredient> inputs;
   ItemStack output;

   public CrowFluteRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
      super(group, category, pattern, result, showNotification);
      this.inputs = pattern.ingredients();
      this.output = result;
   }

   public boolean isSpecial() {
      return true;
   }

   @Nonnull
   public ItemStack assemble(CraftingInput inv, Provider registryAccess) {
      int first = -1;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (stack.getItem() instanceof DyeItem dye) {
            int colorId = dye.getDyeColor().getId();
            if (first != -1) {
               return CrowFluteItem.withColors(first, colorId);
            }

            first = colorId;
         }
      }

      return CrowFluteItem.withColors(first != -1 ? first : 0, 0);
   }

   public ItemStack getResultItem(Provider registryAccess) {
      return this.getOutput();
   }

   public ItemStack getOutput() {
      return this.output.copy();
   }

   public NonNullList<Ingredient> getInputs() {
      return this.inputs;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.CROW_FLUTE_DYE_SERIALIZER.get();
   }

   public static class Serializer implements RecipeSerializer<CrowFluteRecipe> {
      public static final MapCodec<CrowFluteRecipe> CODEC = RecordCodecBuilder.mapCodec(
         p_340778_ -> p_340778_.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
               CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
               ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
               Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
            )
            .apply(p_340778_, CrowFluteRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteRecipe> STREAM_CODEC = StreamCodec.of(
         CrowFluteRecipe.Serializer::toNetwork, CrowFluteRecipe.Serializer::fromNetwork
      );

      public MapCodec<CrowFluteRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CrowFluteRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static CrowFluteRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String s = buffer.readUtf();
         CraftingBookCategory craftingbookcategory = (CraftingBookCategory)buffer.readEnum(CraftingBookCategory.class);
         ShapedRecipePattern shapedrecipepattern = (ShapedRecipePattern)ShapedRecipePattern.STREAM_CODEC.decode(buffer);
         ItemStack itemstack = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         boolean flag = buffer.readBoolean();
         return new CrowFluteRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, CrowFluteRecipe recipe) {
         buffer.writeUtf(recipe.getGroup());
         buffer.writeEnum(recipe.category());
         ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
         buffer.writeBoolean(recipe.showNotification());
      }
   }
}
