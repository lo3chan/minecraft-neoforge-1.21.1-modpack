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
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidHelper implements IPlatformFluidHelperInternal<FluidStack> {
   @Override
   public IIngredientTypeWithSubtypes<Fluid, FluidStack> getFluidIngredientType() {
      return NeoForgeTypes.FLUID_STACK;
   }

   public int getColorTint(FluidStack ingredient) {
      Fluid fluid = ingredient.getFluid();
      IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
      return normalizeColor(renderProperties.getTintColor(ingredient));
   }

   private static int normalizeColor(int color) {
      return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
   }

   public long getAmount(FluidStack ingredient) {
      return ingredient.getAmount();
   }

   public FluidStack copyWithAmount(FluidStack ingredient, long amount) {
      FluidStack copy = ingredient.copy();
      int intAmount = Math.toIntExact(amount);
      copy.setAmount(intAmount);
      return copy;
   }

   public DataComponentPatch getComponentsPatch(FluidStack ingredient) {
      return ingredient.getComponentsPatch();
   }

   public void getTooltip(List<Component> tooltip, FluidStack ingredient, TooltipFlag tooltipFlag) {
      Fluid fluid = ingredient.getFluid();
      if (!fluid.isSame(Fluids.EMPTY)) {
         Component displayName = this.getDisplayName(ingredient);
         tooltip.add(displayName);
         if (tooltipFlag.isAdvanced()) {
            Registry<Fluid> fluidRegistry = RegistryUtil.getRegistry(Registries.FLUID);
            ResourceLocation resourceLocation = fluidRegistry.getKey(fluid);
            if (resourceLocation != null && resourceLocation != BuiltInRegistries.FLUID.getDefaultKey()) {
               MutableComponent advancedId = Component.literal(resourceLocation.toString()).withStyle(ChatFormatting.DARK_GRAY);
               tooltip.add(advancedId);
            }
         }
      }
   }

   @Override
   public long bucketVolume() {
      return 1000L;
   }

   public Optional<TextureAtlasSprite> getStillFluidSprite(FluidStack fluidStack) {
      Fluid fluid = fluidStack.getFluid();
      IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
      ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);
      return Optional.ofNullable(fluidStill)
         .map(f -> (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(f))
         .filter(s -> s.atlasLocation() != MissingTextureAtlasSprite.getLocation());
   }

   public Component getDisplayName(FluidStack ingredient) {
      Component displayName = ingredient.getHoverName();
      Fluid fluid = ingredient.getFluid();
      return (Component)(!fluid.isSource(fluid.defaultFluidState())
         ? Component.translatable("jei.tooltip.liquid.flowing", new Object[]{displayName})
         : displayName);
   }

   public FluidStack create(Holder<Fluid> fluid, long amount, DataComponentPatch components) {
      int intAmount = (int)Math.min(amount, 2147483647L);
      return new FluidStack(fluid, intAmount, components);
   }

   public FluidStack create(Holder<Fluid> fluid, long amount) {
      int intAmount = (int)Math.min(amount, 2147483647L);
      return new FluidStack(fluid, intAmount);
   }

   public FluidStack copy(FluidStack ingredient) {
      return ingredient.copy();
   }

   public FluidStack normalize(FluidStack ingredient) {
      return ingredient.getAmount() == 1000 ? ingredient : ingredient.copyWithAmount(1000);
   }

   @Override
   public Optional<FluidStack> getContainedFluid(ITypedIngredient<?> ingredient) {
      return ingredient.getItemStack()
         .flatMap(i -> Optional.ofNullable(i.getCapability(FluidHandler.ITEM)))
         .map(c -> c.drain(2147483647, FluidAction.SIMULATE));
   }

   @Override
   public Codec<FluidStack> getCodec() {
      return Codec.withAlternative(FluidStack.fixedAmountCodec(1000), FluidStack.CODEC);
   }
}
