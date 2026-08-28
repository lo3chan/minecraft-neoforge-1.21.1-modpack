/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.api.recipe.vanilla;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface IJeiAnvilRecipe {
    public @Unmodifiable List<ItemStack> getLeftInputs();

    public @Unmodifiable List<ItemStack> getRightInputs();

    public @Unmodifiable List<ItemStack> getOutputs();

    @Nullable
    public ResourceLocation getUid();
}

