/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.FireworkExplosion$Shape
 *  net.minecraft.world.item.component.Fireworks
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import org.jetbrains.annotations.Nullable;

public class FireworkRocketSubtypeInterpreter
implements ISubtypeInterpreter<ItemStack> {
    public static final FireworkRocketSubtypeInterpreter INSTANCE = new FireworkRocketSubtypeInterpreter();

    private FireworkRocketSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(DataComponents.FIREWORKS);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
        Fireworks fireworks = (Fireworks)itemStack.get(DataComponents.FIREWORKS);
        if (fireworks == null) {
            return "";
        }
        List explosions = fireworks.explosions();
        ArrayList<String> strings = new ArrayList<String>();
        for (Object e : explosions) {
            FireworkExplosion.Shape shape = e.shape();
            strings.add(shape.getSerializedName());
        }
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        strings.sort(null);
        for (String s : strings) {
            joiner.add(s);
        }
        int flightDuration = fireworks.flightDuration();
        return flightDuration + ":" + String.valueOf(joiner);
    }
}

