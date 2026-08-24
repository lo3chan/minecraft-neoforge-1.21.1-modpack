package net.joefoxe.hexerei.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class CauldronParticleData extends ParticleType<CauldronParticleData> implements ParticleOptions {
   ParticleType<CauldronParticleData> type = (ParticleType<CauldronParticleData>)ModParticleTypes.CAULDRON.get();
   FluidStack fluid;
   public static final MapCodec<CauldronParticleData> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(FluidStack.CODEC.fieldOf("fluid").forGetter(d -> d.fluid)).apply(instance, CauldronParticleData::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, CauldronParticleData> STREAM_CODEC = StreamCodec.of(
      CauldronParticleData::toNetwork, CauldronParticleData::fromNetwork
   );

   public CauldronParticleData(FluidStack fluid) {
      super(true);
      this.fluid = fluid;
   }

   @Nonnull
   public ParticleType<CauldronParticleData> getType() {
      return this.type;
   }

   public static void toNetwork(RegistryFriendlyByteBuf buf, CauldronParticleData data) {
      FluidStack.STREAM_CODEC.encode(buf, data.fluid);
   }

   public static CauldronParticleData fromNetwork(RegistryFriendlyByteBuf buffer) {
      return new CauldronParticleData((FluidStack)FluidStack.STREAM_CODEC.decode(buffer));
   }

   @NotNull
   public MapCodec<CauldronParticleData> codec() {
      return CODEC;
   }

   @NotNull
   public StreamCodec<? super RegistryFriendlyByteBuf, CauldronParticleData> streamCodec() {
      return STREAM_CODEC;
   }
}
