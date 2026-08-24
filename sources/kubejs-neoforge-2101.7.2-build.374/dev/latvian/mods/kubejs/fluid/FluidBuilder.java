package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

@ReturnsSelf
public class FluidBuilder extends BuilderBase<FlowingFluid> {
   public static final KubeColor WATER_COLOR = new SimpleColor(-12618012);
   private static final ResourceLocation GENERATED_BUCKET_MODEL = KubeJS.id("item/generated_bucket");
   public transient int slopeFindDistance = 4;
   public transient int levelDecreasePerBlock = 1;
   public transient float explosionResistance = 1.0F;
   public transient int tickRate = 5;
   public FluidTypeBuilder fluidType = new FluidTypeBuilder(this.id);
   public FlowingFluidBuilder flowingFluid = new FlowingFluidBuilder(this);
   public FluidBlockBuilder block = new FluidBlockBuilder(this);
   public FluidBucketItemBuilder bucketItem = new FluidBucketItemBuilder(this);
   private Properties properties;

   public FluidBuilder(ResourceLocation i) {
      super(i);
   }

   @Override
   public BuilderBase<FlowingFluid> displayName(Component name) {
      if (this.block != null) {
         this.block.displayName(name);
      }

      if (this.bucketItem != null) {
         this.bucketItem.displayName(Component.literal("").append(name).append(" Bucket"));
      }

      return super.displayName(name);
   }

   public Properties createProperties() {
      if (this.properties == null) {
         this.properties = new Properties(this.fluidType, this, this.flowingFluid);
         this.properties.bucket(this.bucketItem);
         this.properties.block(this.block);
         this.properties.slopeFindDistance(this.slopeFindDistance);
         this.properties.levelDecreasePerBlock(this.levelDecreasePerBlock);
         this.properties.explosionResistance(this.explosionResistance);
         this.properties.tickRate(this.tickRate);
      }

      return this.properties;
   }

   public FlowingFluid createObject() {
      return new Source(this.createProperties());
   }

   @Override
   public void createAdditionalObjects(AdditionalObjectRegistry registry) {
      registry.add(Keys.FLUID_TYPES, this.fluidType);
      registry.add(Registries.FLUID, this.flowingFluid);
      if (this.block != null) {
         registry.add(Registries.BLOCK, this.block);
      }

      if (this.bucketItem != null) {
         registry.add(Registries.ITEM, this.bucketItem);
      }
   }

   @Override
   public BuilderBase<FlowingFluid> tag(ResourceLocation[] tag) {
      this.flowingFluid.tag(tag);
      return super.tag(tag);
   }

   public FluidBuilder type(Consumer<FluidTypeBuilder> builder) {
      builder.accept(this.fluidType);
      return this;
   }

   public FluidBuilder tint(KubeColor c) {
      this.fluidType.tint = c;
      return this;
   }

   public FluidBuilder stillTexture(ResourceLocation id) {
      this.fluidType.stillTexture = id;
      return this;
   }

   public FluidBuilder flowingTexture(ResourceLocation id) {
      this.fluidType.flowingTexture = id;
      return this;
   }

   public FluidBuilder renderType(BlockRenderType l) {
      this.fluidType.renderType = l;
      return this;
   }

   public FluidBuilder translucent() {
      return this.renderType(BlockRenderType.TRANSLUCENT);
   }

   public FluidBuilder slopeFindDistance(int slopeFindDistance) {
      this.slopeFindDistance = slopeFindDistance;
      return this;
   }

   public FluidBuilder levelDecreasePerBlock(int levelDecreasePerBlock) {
      this.levelDecreasePerBlock = levelDecreasePerBlock;
      return this;
   }

   public FluidBuilder explosionResistance(float explosionResistance) {
      this.explosionResistance = explosionResistance;
      return this;
   }

   public FluidBuilder tickRate(int tickRate) {
      this.tickRate = tickRate;
      return this;
   }

   public FluidBuilder noBucket() {
      this.bucketItem = null;
      return this;
   }

   public FluidBuilder noBlock() {
      this.block = null;
      return this;
   }

   @Override
   public void generateAssets(KubeAssetGenerator generator) {
      LoadedTexture stillTexture = generator.loadTexture(this.fluidType.stillTexture);
      if (stillTexture.width > 0 && stillTexture.height > 0) {
         generator.texture(this.fluidType.actualStillTexture, stillTexture.tint(this.fluidType.tint));
      }

      LoadedTexture flowingTexture = generator.loadTexture(this.fluidType.flowingTexture);
      if (flowingTexture.width > 0 && flowingTexture.height > 0) {
         generator.texture(this.fluidType.actualFlowingTexture, flowingTexture.tint(this.fluidType.tint));
      }

      generator.blockState(this.id, m -> m.simpleVariant("", this.id.withPath(ID.BLOCK)));
      generator.blockModel(this.id, m -> {
         m.parent(null);
         m.texture("particle", this.fluidType.actualStillTexture.toString());
      });
      if (this.bucketItem != null) {
         ResourceLocation fluidPath = this.newID("item/generated/", "_bucket_fluid");
         generator.mask(fluidPath, KubeJS.id("item/bucket_mask"), this.fluidType.actualStillTexture);
         generator.itemModel(this.bucketItem.id, m -> {
            m.parent(this.bucketItem.parentModel == null ? GENERATED_BUCKET_MODEL : this.bucketItem.parentModel);
            m.texture("bucket_fluid", fluidPath.toString());
            m.textures(this.bucketItem.textures);
         });
      }
   }
}
