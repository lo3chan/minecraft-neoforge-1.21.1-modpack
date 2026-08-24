package snownee.jade.api.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import snownee.jade.util.CommonProxy;

public class JadeFluidObject {
   public static final Codec<JadeFluidObject> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("type").forGetter(JadeFluidObject::getType),
            Codec.LONG.fieldOf("amount").forGetter(JadeFluidObject::getAmount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(JadeFluidObject::getComponents)
         )
         .apply(instance, JadeFluidObject::of)
   );
   private final Fluid type;
   private final long amount;
   private final DataComponentPatch components;

   public static long bucketVolume() {
      return CommonProxy.bucketVolume();
   }

   public static long blockVolume() {
      return CommonProxy.blockVolume();
   }

   public static JadeFluidObject empty() {
      return of(Fluids.EMPTY, 0L);
   }

   public static JadeFluidObject of(Fluid fluid) {
      return of(fluid, blockVolume());
   }

   public static JadeFluidObject of(Fluid fluid, long amount) {
      return of(fluid, amount, DataComponentPatch.EMPTY);
   }

   public static JadeFluidObject of(Fluid fluid, long amount, DataComponentPatch components) {
      return new JadeFluidObject(fluid, amount, components);
   }

   private JadeFluidObject(Fluid type, long amount, DataComponentPatch components) {
      this.type = type;
      this.amount = amount;
      this.components = components;
      Objects.requireNonNull(type);
      Objects.requireNonNull(components);
   }

   public Fluid getType() {
      return this.type;
   }

   public long getAmount() {
      return this.amount;
   }

   public DataComponentPatch getComponents() {
      return this.components;
   }

   public boolean isEmpty() {
      return this.getType() == Fluids.EMPTY || this.getAmount() == 0L;
   }

   public static boolean isSameFluidSameComponents(JadeFluidObject first, JadeFluidObject second) {
      return first.type != second.type ? false : first.isEmpty() && second.isEmpty() || Objects.equals(first.components, second.components);
   }
}
