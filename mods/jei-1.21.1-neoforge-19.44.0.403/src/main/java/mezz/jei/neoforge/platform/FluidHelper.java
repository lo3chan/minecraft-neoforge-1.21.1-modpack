/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 */
package mezz.jei.neoforge.platform;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class FluidHelper
implements IPlatformFluidHelperInternal<FluidStack> {
    @Override
    public IIngredientTypeWithSubtypes<Fluid, FluidStack> getFluidIngredientType() {
        return NeoForgeTypes.FLUID_STACK;
    }

    @Override
    public int getColorTint(FluidStack ingredient) {
        Fluid fluid = ingredient.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of((Fluid)fluid);
        return FluidHelper.normalizeColor(renderProperties.getTintColor(ingredient));
    }

    private static int normalizeColor(int color) {
        if ((color & 0xFF000000) == 0) {
            return color | 0xFF000000;
        }
        return color;
    }

    @Override
    public long getAmount(FluidStack ingredient) {
        return ingredient.getAmount();
    }

    @Override
    public FluidStack copyWithAmount(FluidStack ingredient, long amount) {
        FluidStack copy = ingredient.copy();
        int intAmount = Math.toIntExact(amount);
        copy.setAmount(intAmount);
        return copy;
    }

    @Override
    public DataComponentPatch getComponentsPatch(FluidStack ingredient) {
        return ingredient.getComponentsPatch();
    }

    @Override
    public void getTooltip(List<Component> tooltip, FluidStack ingredient, TooltipFlag tooltipFlag) {
        Registry fluidRegistry;
        ResourceLocation resourceLocation;
        Fluid fluid = ingredient.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }
        Component displayName = this.getDisplayName(ingredient);
        tooltip.add(displayName);
        if (tooltipFlag.isAdvanced() && (resourceLocation = (fluidRegistry = RegistryUtil.getRegistry(Registries.FLUID)).getKey((Object)fluid)) != null && resourceLocation != BuiltInRegistries.FLUID.getDefaultKey()) {
            MutableComponent advancedId = Component.literal((String)resourceLocation.toString()).withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add((Component)advancedId);
        }
    }

    @Override
    public long bucketVolume() {
        return 1000L;
    }

    @Override
    public Optional<TextureAtlasSprite> getStillFluidSprite(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of((Fluid)fluid);
        ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);
        return Optional.ofNullable(fluidStill).map(f -> (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(f)).filter(s -> s.atlasLocation() != MissingTextureAtlasSprite.getLocation());
    }

    @Override
    public Component getDisplayName(FluidStack ingredient) {
        Component displayName = ingredient.getHoverName();
        Fluid fluid = ingredient.getFluid();
        if (!fluid.isSource(fluid.defaultFluidState())) {
            return Component.translatable((String)"jei.tooltip.liquid.flowing", (Object[])new Object[]{displayName});
        }
        return displayName;
    }

    @Override
    public FluidStack create(Holder<Fluid> fluid, long amount, DataComponentPatch components) {
        int intAmount = (int)Math.min(amount, Integer.MAX_VALUE);
        return new FluidStack(fluid, intAmount, components);
    }

    @Override
    public FluidStack create(Holder<Fluid> fluid, long amount) {
        int intAmount = (int)Math.min(amount, Integer.MAX_VALUE);
        return new FluidStack(fluid, intAmount);
    }

    @Override
    public FluidStack copy(FluidStack ingredient) {
        return ingredient.copy();
    }

    @Override
    public FluidStack normalize(FluidStack ingredient) {
        if (ingredient.getAmount() == 1000) {
            return ingredient;
        }
        return ingredient.copyWithAmount(1000);
    }

    @Override
    public Optional<FluidStack> getContainedFluid(ITypedIngredient<?> ingredient) {
        return ingredient.getItemStack().flatMap(i -> Optional.ofNullable((IFluidHandlerItem)i.getCapability(Capabilities.FluidHandler.ITEM))).map(c -> c.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE));
    }

    @Override
    public Codec<FluidStack> getCodec() {
        return Codec.withAlternative((Codec)FluidStack.fixedAmountCodec((int)1000), (Codec)FluidStack.CODEC);
    }
}

