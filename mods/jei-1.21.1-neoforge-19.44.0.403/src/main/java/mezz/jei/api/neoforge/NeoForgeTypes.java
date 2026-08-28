/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package mezz.jei.api.neoforge;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class NeoForgeTypes {
    public static final IIngredientTypeWithSubtypes<Fluid, FluidStack> FLUID_STACK = new IIngredientTypeWithSubtypes<Fluid, FluidStack>(){

        @Override
        public String getUid() {
            return "fluid_stack";
        }

        @Override
        public Class<? extends FluidStack> getIngredientClass() {
            return FluidStack.class;
        }

        @Override
        public Class<? extends Fluid> getIngredientBaseClass() {
            return Fluid.class;
        }

        @Override
        public Fluid getBase(FluidStack ingredient) {
            return ingredient.getFluid();
        }

        @Override
        public FluidStack getDefaultIngredient(Fluid base) {
            return new FluidStack(base, 1000);
        }
    };

    private NeoForgeTypes() {
    }
}

