/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnegative
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.api.recipe.vanilla;

import java.util.List;
import javax.annotation.Nonnegative;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

public interface IJeiFuelingRecipe {
    public @Unmodifiable List<ItemStack> getInputs();

    @Nonnegative
    public int getBurnTime();
}

