/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public class PaintingSubtypeInterpreter
implements ISubtypeInterpreter<ItemStack> {
    public static final PaintingSubtypeInterpreter INSTANCE = new PaintingSubtypeInterpreter();

    private PaintingSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        CustomData properties = (CustomData)ingredient.get(DataComponents.ENTITY_DATA);
        if (properties == null) {
            return null;
        }
        CompoundTag compoundTag = properties.copyTag();
        return compoundTag.get("variant");
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        CustomData properties = (CustomData)ingredient.get(DataComponents.ENTITY_DATA);
        if (properties == null) {
            return "";
        }
        CompoundTag compoundTag = properties.copyTag();
        Tag variant = compoundTag.get("variant");
        if (variant == null) {
            return "";
        }
        return variant.getAsString();
    }
}

