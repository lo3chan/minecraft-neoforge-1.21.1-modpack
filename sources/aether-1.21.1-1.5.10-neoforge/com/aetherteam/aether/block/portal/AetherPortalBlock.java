package com.aetherteam.aether.block.portal;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.client.sound.FadeOutSoundInstance;
import com.aetherteam.aether.world.LevelUtil;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.DimensionTransition.PostDimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AetherPortalBlock extends Block implements Portal {
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

   public AetherPortalBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(AXIS, Axis.X));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AXIS});
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity.canUsePortal(false)) {
         entity.setAsInsidePortal(this, pos);
      }
   }

   public int getPortalTransitionTime(ServerLevel pLevel, Entity pEntity) {
      return this.getLevelPortalTransitionTime(pLevel, pEntity);
   }

   private int getLevelPortalTransitionTime(Level level, Entity entity) {
      return entity instanceof Player player
         ? Math.max(
            1,
            level.getGameRules()
               .getInt(
                  player.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY
               )
         )
         : 0;
   }

   @Nullable
   public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
      ResourceKey<Level> resourcekey = entity.level().dimension() == LevelUtil.destinationDimension()
         ? LevelUtil.returnDimension()
         : LevelUtil.destinationDimension();
      ServerLevel serverlevel = level.getServer().getLevel(resourcekey);
      if (serverlevel == null) {
         return null;
      } else {
         WorldBorder worldborder = serverlevel.getWorldBorder();
         double d0 = DimensionType.getTeleportationScale(level.dimensionType(), serverlevel.dimensionType());
         BlockPos blockpos = worldborder.clampToBounds(entity.getX() * d0, entity.getY(), entity.getZ() * d0);
         return this.getExitPortal(serverlevel, entity, pos, blockpos, worldborder);
      }
   }

   @Nullable
   private DimensionTransition getExitPortal(ServerLevel pLevel, Entity pEntity, BlockPos pPos, BlockPos pExitPos, WorldBorder pWorldBorder) {
      AetherPortalForcer portalForcer = new AetherPortalForcer(pLevel);
      Optional<BlockPos> optional = portalForcer.findClosestPortalPosition(pExitPos, pWorldBorder);
      FoundRectangle blockutil$foundrectangle;
      PostDimensionTransition dimensiontransition$postdimensiontransition;
      if (optional.isPresent()) {
         BlockPos blockpos = optional.get();
         BlockState blockstate = pLevel.getBlockState(blockpos);
         blockutil$foundrectangle = BlockUtil.getLargestRectangleAround(
            blockpos,
            (Axis)blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS),
            21,
            Axis.Y,
            21,
            p_351970_ -> pLevel.getBlockState(p_351970_) == blockstate
         );
         dimensiontransition$postdimensiontransition = AetherPortalForcer.PLAY_PORTAL_SOUND.then(p_351967_ -> p_351967_.placePortalTicket(blockpos));
      } else {
         Axis direction$axis = pEntity.level().getBlockState(pPos).getOptionalValue(AXIS).orElse(Axis.X);
         Optional<FoundRectangle> optional1 = portalForcer.createPortal(pExitPos, direction$axis);
         if (optional1.isEmpty()) {
            Aether.LOGGER.error("Unable to create a portal, likely target out of worldborder");
            return null;
         }

         blockutil$foundrectangle = optional1.get();
         dimensiontransition$postdimensiontransition = AetherPortalForcer.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET);
      }

      return getDimensionTransitionFromExit(pEntity, pPos, blockutil$foundrectangle, pLevel, dimensiontransition$postdimensiontransition);
   }

   private static DimensionTransition getDimensionTransitionFromExit(
      Entity pEntity, BlockPos pPos, FoundRectangle pRectangle, ServerLevel pLevel, PostDimensionTransition pPostDimensionTransition
   ) {
      BlockState blockstate = pEntity.level().getBlockState(pPos);
      Axis direction$axis;
      Vec3 vec3;
      if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
         direction$axis = (Axis)blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
         FoundRectangle blockutil$foundrectangle = BlockUtil.getLargestRectangleAround(
            pPos, direction$axis, 21, Axis.Y, 21, p_351016_ -> pEntity.level().getBlockState(p_351016_) == blockstate
         );
         vec3 = pEntity.getRelativePortalPosition(direction$axis, blockutil$foundrectangle);
      } else {
         direction$axis = Axis.X;
         vec3 = new Vec3(0.5, 0.0, 0.0);
      }

      return createDimensionTransition(
         pLevel, pRectangle, direction$axis, vec3, pEntity, pEntity.getDeltaMovement(), pEntity.getYRot(), pEntity.getXRot(), pPostDimensionTransition
      );
   }

   private static DimensionTransition createDimensionTransition(
      ServerLevel pLevel,
      FoundRectangle pRectangle,
      Axis pAxis,
      Vec3 pOffset,
      Entity pEntity,
      Vec3 pSpeed,
      float pYRot,
      float pXRot,
      PostDimensionTransition pPostDimensionTransition
   ) {
      BlockPos blockpos = pRectangle.minCorner;
      BlockState blockstate = pLevel.getBlockState(blockpos);
      Axis direction$axis = blockstate.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Axis.X);
      double d0 = pRectangle.axis1Size;
      double d1 = pRectangle.axis2Size;
      EntityDimensions entitydimensions = pEntity.getDimensions(pEntity.getPose());
      int i = pAxis == direction$axis ? 0 : 90;
      Vec3 vec3 = pAxis == direction$axis ? pSpeed : new Vec3(pSpeed.z, pSpeed.y, -pSpeed.x);
      double d2 = entitydimensions.width() / 2.0 + (d0 - entitydimensions.width()) * pOffset.x();
      double d3 = (d1 - entitydimensions.height()) * pOffset.y();
      double d4 = 0.5 + pOffset.z();
      boolean flag = direction$axis == Axis.X;
      Vec3 vec31 = new Vec3(blockpos.getX() + (flag ? d2 : d4), blockpos.getY() + d3, blockpos.getZ() + (flag ? d4 : d2));
      Vec3 vec32 = AetherPortalShape.findCollisionFreePosition(vec31, pLevel, pEntity, entitydimensions);
      return new DimensionTransition(pLevel, vec32, vec3, pYRot + i, pXRot, pPostDimensionTransition);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (random.nextInt(100) == 0) {
         Minecraft.getInstance()
            .getSoundManager()
            .play(
               new FadeOutSoundInstance(
                  (SoundEvent)AetherSoundEvents.BLOCK_AETHER_PORTAL_AMBIENT.get(),
                  SoundSource.BLOCKS,
                  0.5F,
                  random.nextFloat() * 0.4F + 0.8F,
                  RandomSource.create(random.nextLong()),
                  false,
                  0,
                  Attenuation.LINEAR,
                  pos.getX() + 0.5,
                  pos.getY() + 0.5,
                  pos.getZ() + 0.5,
                  false
               )
            );
      }

      for (int i = 0; i < 4; i++) {
         double x = pos.getX() + random.nextDouble();
         double y = pos.getY() + random.nextDouble();
         double z = pos.getZ() + random.nextDouble();
         double xSpeed = (random.nextFloat() - 0.5) * 0.5;
         double ySpeed = (random.nextFloat() - 0.5) * 0.5;
         double zSpeed = (random.nextFloat() - 0.5) * 0.5;
         int j = random.nextInt(2) * 2 - 1;
         if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
            x = pos.getX() + 0.5 + 0.25 * j;
            xSpeed = random.nextFloat() * 2.0F * j;
         } else {
            z = pos.getZ() + 0.5 + 0.25 * j;
            zSpeed = random.nextFloat() * 2.0F * j;
         }

         level.addParticle((ParticleOptions)AetherParticleTypes.AETHER_PORTAL.get(), x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return switch (rotation) {
         case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> {
            switch ((Axis)state.getValue(AXIS)) {
               case Z:
                  yield (BlockState)state.setValue(AXIS, Axis.X);
               case X:
                  yield (BlockState)state.setValue(AXIS, Axis.Z);
               default:
                  yield state;
            }
         }
         default -> state;
      };
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.getValue(AXIS) == Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      Axis directionAxis = direction.getAxis();
      Axis blockAxis = (Axis)state.getValue(AXIS);
      boolean flag = blockAxis != directionAxis && directionAxis.isHorizontal();
      return !flag && !facingState.is(this) && !new AetherPortalShape(level, currentPos, blockAxis).isComplete()
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, direction, facingState, level, currentPos, facingPos);
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }
}
