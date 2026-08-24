package net.conczin.immersive_gateways.block;

import com.mojang.serialization.MapCodec;
import net.conczin.immersive_gateways.BlockEntityTypes;
import net.conczin.immersive_gateways.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GatewayBlock extends BaseEntityBlock {
   public static final MapCodec<GatewayBlock> CODEC = simpleCodec(GatewayBlock::new);
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 2.0, 16.0, 16.0, 14.0);
   protected static final VoxelShape Y_AXIS_AABB = Block.box(0.0, 2.0, 0.0, 16.0, 14.0, 16.0);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(2.0, 0.0, 0.0, 14.0, 16.0, 16.0);

   public GatewayBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Axis.X));
   }

   protected MapCodec<GatewayBlock> codec() {
      return CODEC;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new GatewayBlockEntity(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return createTickerHelper(blockEntityType, BlockEntityTypes.GATEWAY, this.getTicker(level));
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level) {
      return level.isClientSide
         ? (l, p, s, b) -> GatewayBlockEntity.clientTick(l, p, s, (GatewayBlockEntity)b)
         : (l, p, s, b) -> GatewayBlockEntity.serverTick((ServerLevel)l, p, s, (GatewayBlockEntity)b);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Axis)state.getValue(AXIS)) {
         case X -> X_AXIS_AABB;
         case Y -> Y_AXIS_AABB;
         case Z -> Z_AXIS_AABB;
         default -> throw new MatchException(null, null);
      };
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (level instanceof ServerLevel serverLevel && canEntityTeleport(entity)) {
         if (!entity.getRootVehicle().isOnPortalCooldown()) {
            GatewayBlockEntity.teleportEntity(serverLevel, pos, entity);
         }

         entity.getRootVehicle().setPortalCooldown(20);
      }
   }

   public static boolean canEntityTeleport(Entity entity) {
      return EntitySelector.NO_SPECTATORS.test(entity) && (!Config.getInstance().onlyPlayersCanTeleport || entity instanceof Player);
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      for (int i = 0; i < 3; i++) {
         double x = pos.getX() + random.nextDouble();
         double y = pos.getY() + random.nextDouble();
         double z = pos.getZ() + random.nextDouble();
         double ox = (random.nextFloat() - 0.5) * 0.5;
         double oy = (random.nextFloat() - 0.5) * 0.5;
         double oz = (random.nextFloat() - 0.5) * 0.5;
         int k = random.nextInt(2) * 2 - 1;
         if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
            x = pos.getX() + 0.5 + 0.25 * k;
            ox = random.nextFloat() * 2.0F * k;
         } else {
            z = pos.getZ() + 0.5 + 0.25 * k;
            oz = random.nextFloat() * 2.0F * k;
         }

         level.addParticle(ParticleTypes.PORTAL, x, y, z, ox, oy, oz);
      }
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return switch (rotation) {
         case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> {
            switch ((Axis)state.getValue(AXIS)) {
               case X:
                  yield (BlockState)state.setValue(AXIS, Axis.Z);
               case Z:
                  yield (BlockState)state.setValue(AXIS, Axis.X);
               default:
                  yield state;
            }
         }
         default -> state;
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AXIS});
   }
}
