/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.world.level.material.Fluid
 */
package mezz.jei.library.plugins.vanilla.ingredients.fluid;

import java.util.List;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;

public final class FluidStackListFactory {
    private FluidStackListFactory() {
    }

    public static <T> List<T> create(Registry<Fluid> registry, IPlatformFluidHelper<T> helper) {
        return registry.holders().filter(holder -> {
            Fluid fluid = (Fluid)holder.value();
            return fluid.isSource(fluid.defaultFluidState());
        }).map(holder -> helper.create((Holder<Fluid>)holder, helper.bucketVolume())).toList();
    }
}

