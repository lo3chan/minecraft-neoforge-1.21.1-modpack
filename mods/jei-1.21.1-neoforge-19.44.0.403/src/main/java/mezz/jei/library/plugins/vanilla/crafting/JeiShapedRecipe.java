/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.ShapedRecipePattern
 *  net.minecraft.world.level.Level
 */
package mezz.jei.library.plugins.vanilla.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import mezz.jei.library.recipes.RecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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

public class JeiShapedRecipe
implements CraftingRecipe {
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

    public ItemStack getResultItem(HolderLookup.Provider registries) {
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

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
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
        return nonNullList.isEmpty() || nonNullList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getItems().length == 0);
    }

    public static class Serializer
    implements RecipeSerializer<JeiShapedRecipe> {
        public static final MapCodec<JeiShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter(shapedRecipe -> shapedRecipe.group), (App)CraftingBookCategory.CODEC.fieldOf("category").orElse((Object)CraftingBookCategory.MISC).forGetter(shapedRecipe -> shapedRecipe.category), (App)ShapedRecipePattern.MAP_CODEC.forGetter(shapedRecipe -> shapedRecipe.pattern), (App)Codec.list((Codec)ItemStack.STRICT_CODEC).fieldOf("result").forGetter(shapedRecipe -> shapedRecipe.results)).apply((Applicative)instance, JeiShapedRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, JeiShapedRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

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
            ShapedRecipePattern shapedRecipePattern = (ShapedRecipePattern)ShapedRecipePattern.STREAM_CODEC.decode((Object)buffer);
            List results = (List)ItemStack.LIST_STREAM_CODEC.decode((Object)buffer);
            return new JeiShapedRecipe(string, craftingBookCategory, shapedRecipePattern, results);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, JeiShapedRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeEnum((Enum)recipe.category);
            ShapedRecipePattern.STREAM_CODEC.encode((Object)buffer, (Object)recipe.pattern);
            ItemStack.LIST_STREAM_CODEC.encode((Object)buffer, recipe.results);
        }
    }
}

