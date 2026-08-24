package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import java.util.TreeMap;
import net.cibernet.alchemancy.events.handler.GeneralEventHandler;
import net.cibernet.alchemancy.network.S2CAddPlayerMovementPayload;
import net.cibernet.alchemancy.network.S2CPlayGustBasketEffectsPayload;
import net.cibernet.alchemancy.properties.special.GustJetProperty;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber({Dist.CLIENT})
public class GustBasketBlock extends DirectionalBlock {
   public static final MapCodec<GustBasketBlock> CODEC = simpleCodec(GustBasketBlock::new);
   private static final float DISTANCE = 6.0F;
   private static final TreeMap<Direction, VoxelShape> SHAPES = VoxelShapeUtils.createDirectionMap(
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0),
         new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0),
            Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0)
         }
      )
   );
   private static int gustSounds = 0;

   public GustBasketBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
      GeneralEventHandler.registerTickingBlockFunction(this, GustBasketBlock::tick);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(FACING));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      Direction facing = (Direction)state.getValue(FACING);
      int amount = random.nextInt(5);

      for (int i = 0; i < amount; i++) {
         double xOff = facing.getStepX() < 0 ? 0.0 : random.nextDouble() * (1 - facing.getStepX());
         double yOff = facing.getStepY() < 0 ? 0.0 : random.nextDouble() * (1 - facing.getStepY());
         double zOff = facing.getStepZ() < 0 ? 0.0 : random.nextDouble() * (1 - facing.getStepZ());
         level.addParticle(
            ParticleTypes.SMALL_GUST,
            pos.getX() + xOff + Math.max(0, facing.getStepX()),
            pos.getY() + yOff + Math.max(0, facing.getStepY()),
            pos.getZ() + zOff + Math.max(0, facing.getStepZ()),
            0.0,
            0.0,
            0.0
         );
      }
   }

   public static void tick(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      Direction facing = (Direction)state.getValue(FACING);
      Vec3 facingStep = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
      Vec3 offset = facingStep.scale(0.5);
      Vec3 startVec = pos.getCenter().add(offset.x(), offset.y(), offset.z());
      BlockPos clipPos = level.clip(
            new ClipContext(
               startVec, startVec.add(facingStep.scale(6.0)), net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, CollisionContext.empty()
            )
         )
         .getBlockPos();
      double distance = pos.distSqr(clipPos);
      if (!(distance <= 0.0)) {
         distance = Math.min(6.0, Math.sqrt(distance));
         boolean playEffects = false;

         for (Entity target : level.getEntities(
            (Entity)null,
            new AABB(pos).expandTowards(facingStep.scale(distance - 1.0)),
            EntitySelector.NO_SPECTATORS.and(entity -> !entity.getType().is(AlchemancyTags.EntityTypes.UNMOVABLE))
         )) {
            playEffects = true;
            Vec3 movement = facingStep.scale((1.0 - target.position().distanceTo(pos.getCenter()) / 6.0) * 0.25);
            if (facing != Direction.UP && target instanceof ServerPlayer player) {
               PacketDistributor.sendToPlayer(player, new S2CAddPlayerMovementPayload(movement), new CustomPacketPayload[0]);
            }

            target.setDeltaMovement(target.getDeltaMovement().add(movement));
            target.hasImpulse = true;
            target.fallDistance = Math.max(0.0F, target.fallDistance - 2.0F);
         }

         if (playEffects) {
            PacketDistributor.sendToPlayersTrackingChunk(
               level, level.getChunk(pos).getPos(), new S2CPlayGustBasketEffectsPayload(pos, distance), new CustomPacketPayload[0]
            );
         }
      }
   }

   public static void clientPlayerTick(Player player) {
      if (!player.isSpectator() && !player.getType().is(AlchemancyTags.EntityTypes.UNMOVABLE)) {
         Level level = player.level();
         BlockPos.betweenClosedStream(CommonUtils.boundingBoxAroundPoint(player.position(), player.getBbWidth() * 0.45F).expandTowards(0.0, -6.0, 0.0))
            .forEach(
               pos -> {
                  BlockState state = level.getBlockState(pos);
                  if (state.is(AlchemancyBlocks.GUST_BASKET) && state.getValue(FACING) == Direction.UP) {
                     Direction facing = Direction.UP;
                     Vec3 facingStep = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
                     Vec3 offset = facingStep.scale(0.5);
                     Vec3 startVec = pos.getCenter().add(offset.x(), offset.y(), offset.z());
                     BlockPos clipPos = level.clip(
                           new ClipContext(
                              startVec,
                              startVec.add(facingStep.scale(6.0)),
                              net.minecraft.world.level.ClipContext.Block.COLLIDER,
                              Fluid.NONE,
                              CollisionContext.empty()
                           )
                        )
                        .getBlockPos();
                     float distance = Mth.sqrt((float)pos.distSqr(clipPos));
                     if (!(pos.getY() + distance < player.getY())) {
                        Vec3 movement = facingStep.scale((1.0 - player.position().distanceTo(pos.getCenter()) / 6.0) * 0.25);
                        player.setDeltaMovement(player.getDeltaMovement().add(movement));
                        player.hasImpulse = true;
                     }
                  }
               }
            );
      }
   }

   @SubscribeEvent
   private static void resetGustSounds(Post event) {
      gustSounds = 0;
   }

   public static void playGustEffects(Level level, BlockPos pos, double distance) {
      RandomSource random = level.getRandom();
      Direction facing = (Direction)level.getBlockState(pos).getValue(FACING);
      double speed = 0.33000001311302185;
      int amount = random.nextInt(5);

      for (int i = 0; i < amount; i++) {
         double xOff = facing.getStepX() < 0 ? 0.0 : random.nextDouble() * (1 - facing.getStepX());
         double yOff = facing.getStepY() < 0 ? 0.0 : random.nextDouble() * (1 - facing.getStepY());
         double zOff = facing.getStepZ() < 0 ? 0.0 : random.nextDouble() * (1 - facing.getStepZ());
         level.addParticle(
            GustJetProperty.PARTICLES,
            pos.getX() + xOff + Math.max(0, facing.getStepX()),
            pos.getY() + yOff + Math.max(0, facing.getStepY()),
            pos.getZ() + zOff + Math.max(0, facing.getStepZ()),
            facing.getStepX() * speed,
            facing.getStepY() * speed,
            facing.getStepZ() * speed
         );
      }

      if (random.nextFloat() < 0.15F && gustSounds <= 2) {
         level.playLocalSound(pos, (SoundEvent)AlchemancySoundEvents.GUST_BASKET.value(), SoundSource.BLOCKS, 0.25F, (float)(distance / 6.0), false);
         gustSounds++;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace());
   }

   protected MapCodec<? extends DirectionalBlock> codec() {
      return CODEC;
   }
}
