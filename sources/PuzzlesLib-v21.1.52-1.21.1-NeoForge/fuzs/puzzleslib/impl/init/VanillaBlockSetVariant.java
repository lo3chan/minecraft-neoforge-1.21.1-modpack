package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.data.BlockFamily.Builder;
import net.minecraft.data.BlockFamily.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class VanillaBlockSetVariant implements BlockSetVariant {
   private final Variant variant;
   final BiConsumer<Builder, Block> variantBuilder;

   public VanillaBlockSetVariant(Variant variant, BiConsumer<Builder, Block> variantBuilder) {
      this.variant = variant;
      this.variantBuilder = variantBuilder;
   }

   @Override
   public Variant toVanilla() {
      return this.variant;
   }

   @Override
   public String toString() {
      return "Vanilla[" + this.getSerializedName() + "]";
   }

   public String getSerializedName() {
      return this.variant.getRecipeGroup();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return obj instanceof BlockSetVariant variant ? Objects.equals(this.getSerializedName(), variant.getSerializedName()) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getSerializedName());
   }

   public static class Direct extends VanillaBlockSetVariant {
      public Direct(Variant variant, BiConsumer<Builder, Block> variantBuilder) {
         super(variant, variantBuilder);
      }

      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerSimpleBlock(
                  context.getNameWithPrefix(this.getSerializedName()), () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value())
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   }
}
