/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.SuspiciousStewEffects
 *  net.minecraft.world.item.component.SuspiciousStewEffects$Entry
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.jetbrains.annotations.Nullable;

public class SuspiciousStewSubtypeInterpreter
implements ISubtypeInterpreter<ItemStack> {
    public static final SuspiciousStewSubtypeInterpreter INSTANCE = new SuspiciousStewSubtypeInterpreter();

    private SuspiciousStewSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
        SuspiciousStewEffects suspiciousStewEffects = (SuspiciousStewEffects)itemStack.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
        if (suspiciousStewEffects == null) {
            return "";
        }
        List effects = suspiciousStewEffects.effects();
        ArrayList<CallSite> strings = new ArrayList<CallSite>();
        for (SuspiciousStewEffects.Entry e : effects) {
            String string = e.effect().getRegisteredName();
            int duration = e.duration();
            strings.add((CallSite)((Object)(string + "." + duration)));
        }
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        strings.sort(null);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}

