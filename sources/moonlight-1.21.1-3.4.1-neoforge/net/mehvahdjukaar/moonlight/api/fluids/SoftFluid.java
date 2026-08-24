package net.mehvahdjukaar.moonlight.api.fluids;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.fluids.platform.SoftFluidImpl;
import net.mehvahdjukaar.moonlight.api.misc.Triplet;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.codec.CodecUtils;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class SoftFluid {
   private final Component name;
   private final HolderSet<Fluid> equivalentFluids;
   private final FluidContainerList containerList;
   private final FoodProvider food;
   private final HolderSet<DataComponentType<?>> preservedComponentsFromItem;
   public final boolean isGenerated;
   private final ResourceLocation stillTexture;
   private final ResourceLocation flowingTexture;
   @Nullable
   private final ResourceLocation useTexturesFrom;
   private final int luminosity;
   private final int emissivity;
   private final int tintColor;
   private final SoftFluid.TintMethod tintMethod;
   protected int averageTextureTint = -1;
   public static final int BOTTLE_COUNT = SoftFluid.Capacity.BOTTLE.getValue();
   public static final int BOWL_COUNT = SoftFluid.Capacity.BOWL.getValue();
   public static final int BUCKET_COUNT = SoftFluid.Capacity.BUCKET.getValue();
   public static final int WATER_BUCKET_COUNT = 3;
   @Internal
   public static final Codec<Holder<SoftFluid>> HOLDER_CODEC = RegistryFileCodec.create(SoftFluidRegistry.KEY, Codec.lazyInitialized(() -> SoftFluid.CODEC));
   public static final Codec<Holder<SoftFluid>> REFERENCE_CODEC = RegistryFixedCodec.create(SoftFluidRegistry.KEY);
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoftFluid>> STREAM_CODEC = ByteBufCodecs.holderRegistry(SoftFluidRegistry.KEY);
   public static final Codec<Component> TRANSLATABLE_COMPONENT = Codec.STRING.xmap(Component::translatable, Component::getString);
   public static final Codec<SoftFluid> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("still_texture").forGetter(SoftFluid::getStillTexture),
            ResourceLocation.CODEC.fieldOf("flowing_texture").forGetter(SoftFluid::getFlowingTexture),
            TRANSLATABLE_COMPONENT.optionalFieldOf("translation_key", Component.translatable("fluid.moonlight.generic_fluid"))
               .forGetter(SoftFluid::getTranslatedName),
            Codec.intRange(0, 15).optionalFieldOf("luminosity", 0).forGetter(SoftFluid::getLuminosity),
            Codec.intRange(0, 15).optionalFieldOf("emissivity", 0).forGetter(SoftFluid::getEmissivity),
            ColorUtils.CODEC.optionalFieldOf("color", -1).forGetter(SoftFluid::getTintColor),
            SoftFluid.TintMethod.CODEC.optionalFieldOf("tint_method", SoftFluid.TintMethod.STILL_AND_FLOWING).forGetter(SoftFluid::getTintMethod),
            FoodProvider.CODEC.optionalFieldOf("food", FoodProvider.EMPTY).forGetter(SoftFluid::getFoodProvider),
            CodecUtils.lenientHomogeneousList(Registries.DATA_COMPONENT_TYPE)
               .optionalFieldOf("preserved_components_from_item", HolderSet.empty())
               .forGetter(SoftFluid::getPreservedComponents),
            FluidContainerList.CODEC.optionalFieldOf("containers", new FluidContainerList()).forGetter(SoftFluid::getContainerList),
            CodecUtils.lenientHomogeneousList(Registries.FLUID).optionalFieldOf("equivalent_fluids", HolderSet.empty()).forGetter(s -> s.equivalentFluids),
            ResourceLocation.CODEC.optionalFieldOf("use_texture_from").forGetter(s -> Optional.ofNullable(s.getTextureOverride()))
         )
         .apply(instance, SoftFluid::new)
   );

   protected SoftFluid(
      ResourceLocation still,
      ResourceLocation flowing,
      Component name,
      int luminosity,
      int emissivity,
      int color,
      SoftFluid.TintMethod tintMethod,
      FoodProvider food,
      HolderSet<DataComponentType<?>> components,
      FluidContainerList containers,
      HolderSet<Fluid> equivalent,
      Optional<ResourceLocation> textureFrom
   ) {
      this.tintMethod = tintMethod;
      this.equivalentFluids = equivalent;
      this.luminosity = luminosity;
      this.emissivity = Math.max(emissivity, luminosity);
      this.containerList = containers;
      this.food = food;
      this.name = name;
      this.preservedComponentsFromItem = components;
      this.useTexturesFrom = textureFrom.orElse(null);
      int tint = color;
      if (this.useTexturesFrom != null && PlatHelper.getPhysicalSide().isClient()) {
         Triplet<ResourceLocation, ResourceLocation, Integer> data = getRenderingData(this.useTexturesFrom);
         if (data != null) {
            still = data.left();
            flowing = data.middle();
            tint = data.right();
         }
      }

      this.stillTexture = still;
      this.flowingTexture = flowing;
      this.tintColor = tint;
      this.isGenerated = false;
   }

   public SoftFluid(Holder<Fluid> fluid) {
      ResourceLocation still = ResourceLocation.parse("block/water_still");
      ResourceLocation flowing = ResourceLocation.parse("block/water_flowing");
      this.tintMethod = SoftFluid.TintMethod.STILL_AND_FLOWING;
      this.containerList = new FluidContainerList();
      this.food = FoodProvider.EMPTY;
      this.preservedComponentsFromItem = HolderSet.empty();
      this.useTexturesFrom = ((ResourceKey)fluid.unwrapKey().get()).location();
      this.equivalentFluids = HolderSet.direct(new Holder[]{fluid});
      Pair<Integer, Component> pair = getFluidSpecificAttributes((Fluid)fluid.value());
      this.name = (Component)(pair.getSecond() == null ? Component.literal("generic fluid") : (Component)pair.getSecond());
      this.luminosity = (Integer)pair.getFirst();
      this.emissivity = (Integer)pair.getFirst();
      int tint = -1;
      if (this.useTexturesFrom != null && PlatHelper.getPhysicalSide().isClient()) {
         Triplet<ResourceLocation, ResourceLocation, Integer> data = getRenderingData(this.useTexturesFrom);
         if (data != null) {
            still = data.left();
            flowing = data.middle();
            tint = data.right();
         }
      }

      this.stillTexture = still;
      this.flowingTexture = flowing;
      this.tintColor = tint;
      this.isGenerated = true;
   }

   public void afterInit() {
      for (Holder<Fluid> f : this.equivalentFluids) {
         Item filled = ((Fluid)f.value()).getBucket();
         if (filled != Items.AIR && filled != Items.BUCKET) {
            this.containerList.add(Items.BUCKET, filled, BUCKET_COUNT, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
         }
      }
   }

   @Nullable
   public ResourceLocation getTextureOverride() {
      return this.useTexturesFrom;
   }

   public FoodProvider getFoodProvider() {
      return this.food;
   }

   public Component getTranslatedName() {
      return this.name;
   }

   public boolean isEnabled() {
      return this.equivalentFluids.size() != 0 || !this.containerList.getPossibleFilled().isEmpty();
   }

   public Holder<Fluid> getVanillaFluid() {
      Iterator var1 = this.getEquivalentFluids().iterator();
      return (Holder<Fluid>)(var1.hasNext() ? (Holder)var1.next() : Fluids.EMPTY.builtInRegistryHolder());
   }

   public HolderSet<DataComponentType<?>> getPreservedComponents() {
      return this.preservedComponentsFromItem;
   }

   public HolderSet<Fluid> getEquivalentFluids() {
      return this.equivalentFluids;
   }

   public boolean isEquivalent(Holder<Fluid> fluid) {
      return this.equivalentFluids.contains(fluid);
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean isEmptyFluid() {
      return this == SoftFluidRegistry.empty();
   }

   public Optional<Item> getFilledContainer(Item emptyContainer) {
      return this.containerList.getFilled(emptyContainer);
   }

   public Optional<Item> getEmptyContainer(Item filledContainer) {
      return this.containerList.getEmpty(filledContainer);
   }

   public FluidContainerList getContainerList() {
      return this.containerList;
   }

   public int getLuminosity() {
      return this.luminosity;
   }

   public int getEmissivity() {
      return this.emissivity;
   }

   public int getTintColor() {
      return this.tintColor;
   }

   public int getAverageTextureTintColor() {
      return this.averageTextureTint;
   }

   public SoftFluid.TintMethod getTintMethod() {
      return this.tintMethod;
   }

   public boolean isColored() {
      return this.tintColor != -1;
   }

   public ResourceLocation getFlowingTexture() {
      return this.flowingTexture;
   }

   public ResourceLocation getStillTexture() {
      return this.stillTexture;
   }

   public boolean isFood() {
      return !this.food.isEmpty();
   }

   public static Pair<Integer, Component> getFluidSpecificAttributes(Fluid var0) {
      return SoftFluidImpl.getFluidSpecificAttributes(var0);
   }

   public static Triplet<ResourceLocation, ResourceLocation, Integer> getRenderingData(ResourceLocation var0) {
      return SoftFluidImpl.getRenderingData(var0);
   }

   public static enum Capacity implements StringRepresentable {
      BOTTLE(1, 1),
      BOWL(2, 1),
      BUCKET(4, 3),
      BLOCK(4, 4);

      public final int value;
      public static final Codec<SoftFluid.Capacity> CODEC = StringRepresentable.fromEnum(SoftFluid.Capacity::values);
      public static final Codec<Integer> INT_CODEC = Codec.either(Codec.INT, CODEC)
         .xmap(either -> (Integer)either.map(i -> i, SoftFluid.Capacity::getValue), Either::left);

      private Capacity(int forge, int fabric) {
         this.value = PlatHelper.getPlatform().isForge() ? forge : fabric;
      }

      public String getSerializedName() {
         return this.name().toUpperCase(Locale.ROOT);
      }

      public int getValue() {
         return this.value;
      }
   }

   public static enum TintMethod implements StringRepresentable {
      NO_TINT,
      FLOWING,
      STILL_AND_FLOWING;

      public static final Codec<SoftFluid.TintMethod> CODEC = StringRepresentable.fromEnum(SoftFluid.TintMethod::values);

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      public boolean appliesToFlowing() {
         return this == FLOWING || this == STILL_AND_FLOWING;
      }

      public boolean appliesToStill() {
         return this == STILL_AND_FLOWING;
      }
   }
}
