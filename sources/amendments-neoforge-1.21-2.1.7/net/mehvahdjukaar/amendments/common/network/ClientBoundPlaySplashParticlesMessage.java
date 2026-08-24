package net.mehvahdjukaar.amendments.common.network;

import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.block.BoilingWaterCauldronBlock;
import net.mehvahdjukaar.amendments.common.block.ModCauldronBlock;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.network.Message.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record ClientBoundPlaySplashParticlesMessage(Vec3 hitPos, double speed, float width) implements Message {
   public static final TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundPlaySplashParticlesMessage> TYPE = Message.makeType(
      Amendments.res("client_bound_play_splash_particles"), ClientBoundPlaySplashParticlesMessage::new
   );

   public ClientBoundPlaySplashParticlesMessage(FriendlyByteBuf buffer) {
      this(buffer.readVec3(), buffer.readDouble(), buffer.readFloat());
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVec3(this.hitPos);
      buf.writeDouble(this.speed);
      buf.writeFloat(this.width);
   }

   public void handle(Context context) {
      Level level = context.getPlayer().level();
      BlockPos pos = BlockPos.containing(this.hitPos);
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof ModCauldronBlock mc && level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile tile) {
         int color = tile.getSoftFluidTank().getCachedParticleColor(level, pos);
         int light = tile.getSoftFluidTank().getFluidValue().getEmissivity();
         this.playSplashAnimation(level, pos, color, light);
      } else if (state.getBlock() instanceof BoilingWaterCauldronBlock) {
         int color = BoilingWaterCauldronBlock.getWaterColor(state, level, pos, 1);
         this.playSplashAnimation(level, pos, color, 0);
      }
   }

   public void playSplashAnimation(Level level, BlockPos pos, int color, int light) {
      RandomSource rand = level.random;
      float radius = 1.5F;
      spawnSplashParticles(level, this.hitPos, pos, rand, color, light, (ParticleOptions)ModRegistry.BOILING_PARTICLE.get(), radius, this.width);
      spawnSplashParticles(level, this.hitPos, pos, rand, color, light, (ParticleOptions)ModRegistry.SPLASH_PARTICLE.get(), radius, this.width);
   }

   public static void spawnSplashParticles(
      Level level, Vec3 hitPos, BlockPos pos, RandomSource rand, int color, int light, ParticleOptions particleOptions, float radius, float width
   ) {
      float mx = pos.getX() + 0.125F;
      float Mx = pos.getX() + 1 - 0.125F;
      float mz = pos.getZ() + 0.125F;
      float Mz = pos.getZ() + 1 - 0.125F;
      double surface = hitPos.y();

      for (int i = 0; i < 1.0F + width * 20.0F; i++) {
         double x = hitPos.x() + (rand.nextDouble() - 0.5) * width * radius;
         double z = hitPos.z() + (rand.nextDouble() - 0.5) * width * radius;
         if (x >= mx && x <= Mx && z >= mz && z <= Mz) {
            level.addParticle(particleOptions, x, surface, z, color, surface, light);
         }
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE.type();
   }
}
