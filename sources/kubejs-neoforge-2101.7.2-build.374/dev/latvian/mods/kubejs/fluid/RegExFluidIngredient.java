package dev.latvian.mods.kubejs.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import io.netty.buffer.ByteBuf;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class RegExFluidIngredient extends FluidIngredient {
   public static final MapCodec<RegExFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(RegExpKJS.CODEC.fieldOf("regex").forGetter(i -> i.pattern)).apply(instance, RegExFluidIngredient::new)
   );
   public static final StreamCodec<ByteBuf, RegExFluidIngredient> STREAM_CODEC = RegExpKJS.STREAM_CODEC.map(RegExFluidIngredient::new, i -> i.pattern);
   public final Pattern pattern;
   public final String patternString;

   public RegExFluidIngredient(Pattern pattern) {
      this.pattern = pattern;
      this.patternString = RegExpKJS.toRegExpString(pattern);
   }

   public FluidIngredientType<?> getType() {
      return KubeJSFluidIngredients.REGEX.get();
   }

   public boolean test(FluidStack fs) {
      return this.pattern.matcher(fs.getFluid().kjs$getId()).find();
   }

   protected Stream<FluidStack> generateStacks() {
      return BuiltInRegistries.FLUID.stream().filter(fluid -> this.pattern.matcher(fluid.kjs$getId()).find()).map(fluid -> new FluidStack(fluid, 1000));
   }

   public boolean isSimple() {
      return true;
   }

   public int hashCode() {
      return this.patternString.hashCode();
   }

   public boolean equals(Object o) {
      return o == this || o instanceof RegExFluidIngredient r && this.patternString.equals(r.patternString);
   }
}
