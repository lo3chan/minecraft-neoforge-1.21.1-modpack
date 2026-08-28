/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.EnchantmentTags
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.library.plugins.vanilla.grindstone;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import mezz.jei.api.recipe.vanilla.IJeiGrindstoneRecipe;
import mezz.jei.common.util.MathUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class GrindstoneRecipe
implements IJeiGrindstoneRecipe {
    private final List<ItemStack> topInputs;
    private final List<ItemStack> bottomInputs;
    private final List<ItemStack> outputs;
    private int minXpReward;
    private final int maxXpReward;
    @Nullable
    private final ResourceLocation uid;

    public GrindstoneRecipe(List<ItemStack> topInputs, List<ItemStack> bottomInputs, List<ItemStack> outputs, int minXpReward, int maxXpReward, @Nullable ResourceLocation uid) {
        this.topInputs = topInputs;
        this.bottomInputs = bottomInputs;
        this.outputs = outputs;
        this.minXpReward = minXpReward;
        this.maxXpReward = maxXpReward;
        this.uid = uid;
    }

    @Override
    public @Unmodifiable List<ItemStack> getTopInputs() {
        return this.topInputs;
    }

    @Override
    public @Unmodifiable List<ItemStack> getBottomInputs() {
        return this.bottomInputs;
    }

    @Override
    public @Unmodifiable List<ItemStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getMinXpReward() {
        if (this.minXpReward < 0) {
            this.minXpReward = GrindstoneRecipe.getMinXp((ItemStack)this.topInputs.getFirst(), (ItemStack)this.bottomInputs.getFirst());
        }
        return this.minXpReward;
    }

    @Override
    public int getMaxXpReward() {
        if (this.maxXpReward < 0) {
            return this.getMinXpReward() * 2;
        }
        return this.maxXpReward;
    }

    @Override
    @Nullable
    public ResourceLocation getUid() {
        return this.uid;
    }

    @Override
    public @Unmodifiable boolean isOutputRenderOnly() {
        return true;
    }

    private static int getMinXp(ItemStack topItem, ItemStack bottomItem) {
        int topXp = GrindstoneRecipe.getExperienceFromItem(topItem);
        int bottomXp = GrindstoneRecipe.getExperienceFromItem(bottomItem);
        return MathUtil.divideCeil(topXp + bottomXp, 2);
    }

    private static int getExperienceFromItem(ItemStack stack) {
        int i = 0;
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting((ItemStack)stack);
        for (Object2IntMap.Entry entry : itemEnchantments.entrySet()) {
            Holder holder = (Holder)entry.getKey();
            int j = entry.getIntValue();
            if (holder.is(EnchantmentTags.CURSE)) continue;
            i += ((Enchantment)holder.value()).getMinCost(j);
        }
        return i;
    }
}

