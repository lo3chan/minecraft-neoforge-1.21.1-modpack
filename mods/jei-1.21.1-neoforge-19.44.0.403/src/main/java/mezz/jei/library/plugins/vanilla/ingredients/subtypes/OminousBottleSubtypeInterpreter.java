/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OminousBottleSubtypeInterpreter
implements ISubtypeInterpreter<ItemStack> {
    public static final OminousBottleSubtypeInterpreter INSTANCE = new OminousBottleSubtypeInterpreter();

    private OminousBottleSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
        Integer amplifier = (Integer)itemStack.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
        if (amplifier == null) {
            return "";
        }
        return amplifier.toString();
    }
}

