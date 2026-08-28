/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.ShapedRecipePattern
 */
package mezz.jei.library.plugins.vanilla.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import mezz.jei.library.plugins.vanilla.crafting.JeiShapedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class JeiShapedRecipeBuilder
implements IJeiShapedRecipeBuilder {
    private final CraftingBookCategory category;
    private final List<ItemStack> results;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private String group = "";

    public JeiShapedRecipeBuilder(CraftingBookCategory category, List<ItemStack> results) {
        this.category = category;
        this.results = results;
    }

    @Override
    public JeiShapedRecipeBuilder define(Character $$0, Ingredient $$1) {
        if (this.key.containsKey($$0)) {
            throw new IllegalArgumentException("Symbol '" + $$0 + "' is already defined!");
        }
        if ($$0.charValue() == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.key.put($$0, $$1);
        return this;
    }

    @Override
    public JeiShapedRecipeBuilder pattern(String $$0) {
        if (!this.rows.isEmpty() && $$0.length() != ((String)this.rows.getFirst()).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.rows.add($$0);
        return this;
    }

    @Override
    public JeiShapedRecipeBuilder group(String $$0) {
        this.group = $$0;
        return this;
    }

    @Override
    public CraftingRecipe build() {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.key, this.rows);
        return new JeiShapedRecipe(this.group, this.category, pattern, this.results);
    }
}

