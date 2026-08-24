package io.github.razordevs.deep_aether.block.building;

import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.entity.EntityUtil;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DoorwayPillarBlock extends Block {
   public static final VoxelShape INVISIBLE_SHAPE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
   private final Supplier<EntityType<?>> blockedEntityTypeSupplier;

   public DoorwayPillarBlock(Supplier<EntityType<?>> blockedEntityTypeSupplier, Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(DoorwayBlock.INVISIBLE, false));
      this.blockedEntityTypeSupplier = blockedEntityTypeSupplier;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{DoorwayBlock.INVISIBLE});
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (player.isCreative()) {
         BlockState newState = (BlockState)state.cycle(DoorwayBlock.INVISIBLE);
         level.setBlock(pos, newState, 3);
         return InteractionResult.SUCCESS;
      } else {
         return super.useWithoutItem(state, level, pos, player, hit);
      }
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
      boolean flag = super.canBeReplaced(state, context);
      if (!flag) {
         Level level = context.getLevel();
         BlockPos pos = context.getClickedPos();

         for (int i = 0; i < 2; i++) {
            EntityUtil.spawnRemovalParticles(level, pos);
         }
      }

      return flag;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() == GameType.CREATIVE && minecraft.player != null && minecraft.level != null) {
         ItemStack itemStack = minecraft.player.getMainHandItem();
         if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == this && (Boolean)state.getValue(DoorwayBlock.INVISIBLE)) {
            minecraft.level
               .addParticle((ParticleOptions)AetherParticleTypes.BOSS_DOORWAY_BLOCK.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
         }
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if ((Boolean)state.getValue(DoorwayBlock.INVISIBLE)) {
         return context instanceof EntityCollisionContext entity && entity.getEntity() instanceof Player player && player.isCreative()
            ? INVISIBLE_SHAPE
            : Shapes.empty();
      } else {
         return super.getShape(state, level, pos, context);
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (context instanceof EntityCollisionContext entity
         && entity.getEntity() != null
         && this.blockedEntityTypeSupplier.get() != null
         && entity.getEntity().getType() == this.blockedEntityTypeSupplier.get()) {
         return Shapes.block();
      } else {
         return state.getValue(DoorwayBlock.INVISIBLE) ? Shapes.empty() : super.getCollisionShape(state, level, pos, context);
      }
   }

   public RenderShape getRenderShape(BlockState state) {
      return state.getValue(DoorwayBlock.INVISIBLE) ? RenderShape.INVISIBLE : super.getRenderShape(state);
   }

   @Nullable
   public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
      return PathType.BLOCKED;
   }
}
