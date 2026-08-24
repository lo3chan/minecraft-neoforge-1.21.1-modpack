package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

public class FluidBucketItemBuilder extends ItemBuilder {
   public final FluidBuilder fluidBuilder;

   public FluidBucketItemBuilder(FluidBuilder b) {
      super(b.newID("", "_bucket"));
      this.fluidBuilder = b;
      this.maxStackSize(1);
   }

   public BucketItem createObject() {
      return new BucketItem((Fluid)this.fluidBuilder.get(), this.createItemProperties());
   }

   @Override
   public void generateAssets(KubeAssetGenerator generator) {
   }
}
