package net.joefoxe.hexerei.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.message.CrowWhitelistSyncToServer;
import net.joefoxe.hexerei.util.message.PlayerWhitelistingForCrowSyncToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber({Dist.CLIENT})
public class CrowWhitelistEvent {
   public static CrowEntity whiteListingCrow = null;
   public static boolean pressed = false;
   public static List<Player> playersActivelyWhitelisting = new ArrayList<>();

   @SubscribeEvent
   public static void selectBlockPosition(Pre event) {
      if (whiteListingCrow != null
         && (
            whiteListingCrow.isDeadOrDying()
               || whiteListingCrow.getRemovalReason() == RemovalReason.DISCARDED
               || whiteListingCrow.getRemovalReason() == RemovalReason.KILLED
         )) {
         HexereiPacketHandler.sendToServer(new PlayerWhitelistingForCrowSyncToServer(false));
         whiteListingCrow = null;
      }
   }

   @SubscribeEvent
   public static void selectBlockPosition(net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre event) {
      if (event.getButton() == 1 && event.getAction() == 0) {
         pressed = false;
      }
   }

   @SubscribeEvent
   public static void logIn(PlayerLoggedInEvent event) {
      playersActivelyWhitelisting.remove(event.getEntity());
   }

   @SubscribeEvent
   public static void logOut(PlayerLoggedOutEvent event) {
      playersActivelyWhitelisting.remove(event.getEntity());
   }

   @SubscribeEvent
   public static void selectBlockPosition(RightClickBlock event) {
      if (!event.getLevel().isClientSide && playersActivelyWhitelisting.contains(event.getEntity())) {
         BlockState state = event.getLevel().getBlockState(event.getPos());
         Block block = state.getBlock();
         if (block instanceof AttachedStemBlock stemBlock) {
            block = BuiltInRegistries.BLOCK.getOptional(stemBlock.fruit).orElse(Blocks.AIR);
         }

         if (block.defaultBlockState().is(HexereiTags.Blocks.CROW_HARVESTABLE) || block.defaultBlockState().is(HexereiTags.Blocks.CROW_BLOCK_HARVESTABLE)) {
            event.setUseBlock(TriState.FALSE);
            event.getEntity().swing(InteractionHand.MAIN_HAND);
         }
      }

      if (event.getLevel().isClientSide) {
         if (event.getHand() == InteractionHand.MAIN_HAND) {
            if (whiteListingCrow != null) {
               if (!pressed) {
                  BlockState statex = event.getLevel().getBlockState(event.getPos());
                  Block blockx = statex.getBlock();
                  if (blockx instanceof AttachedStemBlock stemBlock) {
                     blockx = BuiltInRegistries.BLOCK.getOptional(stemBlock.fruit).orElse(Blocks.AIR);
                  }

                  if (blockx.defaultBlockState().is(HexereiTags.Blocks.CROW_HARVESTABLE)
                     || blockx.defaultBlockState().is(HexereiTags.Blocks.CROW_BLOCK_HARVESTABLE)) {
                     if (!whiteListingCrow.harvestWhitelist.contains(blockx)) {
                        whiteListingCrow.harvestWhitelist.add(blockx);
                        pressed = true;
                        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(statex.getBlock());
                        if (loc != null) {
                           event.getEntity().swing(InteractionHand.MAIN_HAND);
                           HexereiPacketHandler.sendToServer(new CrowWhitelistSyncToServer(whiteListingCrow, whiteListingCrow.harvestWhitelist));
                           spawnWhitelistParticles(event.getLevel(), event.getPos(), true);
                           spawnWhitelistCrowParticle(event.getLevel(), whiteListingCrow, true);
                        }
                     } else {
                        whiteListingCrow.harvestWhitelist.remove(blockx);
                        pressed = true;
                        event.getEntity().swing(InteractionHand.MAIN_HAND);
                        HexereiPacketHandler.sendToServer(new CrowWhitelistSyncToServer(whiteListingCrow, whiteListingCrow.harvestWhitelist));
                        spawnWhitelistParticles(event.getLevel(), event.getPos(), false);
                        spawnWhitelistCrowParticle(event.getLevel(), whiteListingCrow, false);
                     }

                     event.setUseBlock(TriState.FALSE);
                  }
               } else {
                  event.setUseBlock(TriState.FALSE);
               }
            }
         } else {
            event.setUseBlock(TriState.FALSE);
         }
      }
   }

   public static void spawnParticlesOnBlockFaces(Level p_216314_, BlockPos p_216315_, ParticleOptions p_216316_, IntProvider p_216317_, boolean whitelisted) {
      RandomSource randomSource = RandomSource.create();

      for (Direction direction : Direction.values()) {
         spawnParticlesOnBlockFace(randomSource, p_216315_, p_216316_, p_216317_, direction, () -> getRandomSpeedRanges(randomSource), 0.55, whitelisted);
      }
   }

   public static void spawnParticlesOnBlockFace(
      RandomSource randomSource,
      BlockPos p_216320_,
      ParticleOptions p_216321_,
      IntProvider p_216322_,
      Direction p_216323_,
      Supplier<Vec3> p_216324_,
      double p_216325_,
      boolean whitelisted
   ) {
      int i = p_216322_.sample(randomSource);

      for (int j = 0; j < i; j++) {
         spawnParticleOnFace(randomSource, p_216320_, p_216323_, p_216321_, p_216324_.get(), p_216325_, whitelisted);
      }
   }

   private static Vec3 getRandomSpeedRanges(RandomSource p_216303_) {
      return new Vec3(Mth.nextDouble(p_216303_, -0.5, 0.5), Mth.nextDouble(p_216303_, -0.5, 0.5), Mth.nextDouble(p_216303_, -0.5, 0.5));
   }

   public static void spawnParticleOnFace(
      RandomSource randomSource, BlockPos p_216308_, Direction p_216309_, ParticleOptions p_216310_, Vec3 p_216311_, double p_216312_, boolean whitelisted
   ) {
      ParticleEngine pe = Minecraft.getInstance().particleEngine;
      Vec3 vec3 = Vec3.atCenterOf(p_216308_);
      int i = p_216309_.getStepX();
      int j = p_216309_.getStepY();
      int k = p_216309_.getStepZ();
      double d0 = vec3.x + (i == 0 ? Mth.nextDouble(randomSource, -0.5, 0.5) : i * p_216312_);
      double d1 = vec3.y + (j == 0 ? Mth.nextDouble(randomSource, -0.5, 0.5) : j * p_216312_);
      double d2 = vec3.z + (k == 0 ? Mth.nextDouble(randomSource, -0.5, 0.5) : k * p_216312_);
      double d3 = i == 0 ? p_216311_.x() : 0.0;
      double d4 = j == 0 ? p_216311_.y() : 0.0;
      double d5 = k == 0 ? p_216311_.z() : 0.0;
      Particle p = pe.createParticle(p_216310_, d0, d1, d2, d3 / 200.0, d4 / 200.0, d5 / 200.0);
      if (p != null) {
         p.setLifetime(20);
         p.setColor(0.55F, 0.1F, 0.1F);
         if (whitelisted) {
            p.setColor(0.1F, 0.5F, 0.1F);
         }
      }
   }

   public static void spawnWhitelistParticles(Level worldIn, BlockPos pos, boolean whitelisted) {
      SimpleParticleType basicparticletype = ParticleTypes.ELECTRIC_SPARK;
      spawnParticlesOnBlockFaces(worldIn, pos, basicparticletype, UniformInt.of(3, 5), whitelisted);
   }

   public static void spawnWhitelistCrowParticle(Level worldIn, CrowEntity crow, boolean whitelisted) {
      RandomSource random = crow.getRandom();
      SimpleParticleType basicparticletype = ParticleTypes.ELECTRIC_SPARK;
      ParticleEngine pe = Minecraft.getInstance().particleEngine;
      Vec3 offset = new Vec3(0.0, 0.0, 0.0);
      Particle p = pe.createParticle(
         basicparticletype,
         crow.getX() + offset.x,
         crow.getY() + random.nextDouble() * 0.15000000596046448,
         crow.getZ() + offset.z,
         0.0,
         random.nextDouble() * 0.1 + 0.15,
         0.0
      );
      if (p != null) {
         p.setLifetime(20);
         p.setColor(0.55F, 0.1F, 0.1F);
         if (whitelisted) {
            p.setColor(0.1F, 0.5F, 0.1F);
         }
      }
   }
}
