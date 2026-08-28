/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.library.plugins.vanilla.brewing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.collect.SetMultiMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;

public class BrewingRecipeUtil {
    public static final ItemStack POTION = new ItemStack((ItemLike)Items.POTION);
    public static final ItemStack WATER_BOTTLE = PotionContents.createItemStack((Item)POTION.getItem(), (Holder)Potions.WATER);
    private final Map<Object, Integer> brewingStepCache = new HashMap<Object, Integer>();
    private final SetMultiMap<Object, Object> potionMap = new SetMultiMap();
    private final IIngredientHelper<ItemStack> itemStackHelper;

    public BrewingRecipeUtil(IIngredientHelper<ItemStack> itemStackHelper) {
        this.itemStackHelper = itemStackHelper;
        this.clearCache();
    }

    public void addRecipe(List<ItemStack> inputPotions, ItemStack outputPotion) {
        Object potionOutputUid = this.itemStackHelper.getUid(outputPotion, UidContext.Recipe);
        for (ItemStack inputPotion : inputPotions) {
            Object potionInputUid = this.itemStackHelper.getUid(inputPotion, UidContext.Recipe);
            this.potionMap.put(potionOutputUid, potionInputUid);
        }
        this.clearCache();
    }

    public int getBrewingSteps(ItemStack outputPotion) {
        Object potionInputUid = this.itemStackHelper.getUid(outputPotion, UidContext.Recipe);
        return this.getBrewingSteps(potionInputUid, new HashSet<Object>());
    }

    private void clearCache() {
        if (this.brewingStepCache.size() != 1) {
            this.brewingStepCache.clear();
            Object waterBottleUid = this.itemStackHelper.getUid(WATER_BOTTLE, UidContext.Recipe);
            this.brewingStepCache.put(waterBottleUid, 0);
        }
    }

    private int getBrewingSteps(Object potionOutputUid, Set<Object> previousSteps) {
        Integer cachedBrewingSteps = this.brewingStepCache.get(potionOutputUid);
        if (cachedBrewingSteps != null) {
            return cachedBrewingSteps;
        }
        if (!previousSteps.add(potionOutputUid)) {
            return Integer.MAX_VALUE;
        }
        Collection prevPotionUids = this.potionMap.get(potionOutputUid);
        int minPrevSteps = prevPotionUids.stream().mapToInt(prevPotion -> this.getBrewingSteps(prevPotion, previousSteps)).min().orElse(Integer.MAX_VALUE);
        int brewingSteps = minPrevSteps == Integer.MAX_VALUE ? Integer.MAX_VALUE : minPrevSteps + 1;
        this.brewingStepCache.put(potionOutputUid, brewingSteps);
        return brewingSteps;
    }
}

