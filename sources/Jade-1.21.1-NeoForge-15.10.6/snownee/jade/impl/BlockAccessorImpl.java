package snownee.jade.impl;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.AccessorImpl;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.network.RequestBlockPacket;
import snownee.jade.network.ServerPayloadContext;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.WailaExceptionHandler;

public class BlockAccessorImpl extends AccessorImpl<BlockHitResult> implements BlockAccessor {
   private final BlockState blockState;
   @Nullable
   private final Supplier<BlockEntity> blockEntity;
   private ItemStack fakeBlock;

   private BlockAccessorImpl(BlockAccessorImpl.Builder builder) {
      super(builder.level, builder.player, builder.serverData, Suppliers.ofInstance(builder.hit), builder.connected, builder.showDetails);
      this.blockState = builder.blockState;
      this.blockEntity = builder.blockEntity;
      this.fakeBlock = builder.fakeBlock;
   }

   public static void handleRequest(RequestBlockPacket message, ServerPayloadContext context, Consumer<CompoundTag> responseSender) {
      ServerPlayer player = context.player();
      context.execute(
         () -> {
            BlockAccessor accessor = message.data().unpack(player);
            if (accessor != null) {
               BlockPos pos = accessor.getPosition();
               ServerLevel world = player.serverLevel();
               double maxDistance = Mth.square(player.blockInteractionRange() + 21.0);
               if (!(pos.distSqr(player.blockPosition()) > maxDistance) && world.isLoaded(pos)) {
                  List<IServerDataProvider<BlockAccessor>> providers = WailaCommonRegistration.instance()
                     .getBlockNBTProviders(accessor.getBlock(), accessor.getBlockEntity());
                  CompoundTag tag = accessor.getServerData();

                  for (IServerDataProvider<BlockAccessor> provider : providers) {
                     if (message.dataProviders().contains(provider)) {
                        try {
                           provider.appendServerData(tag, accessor);
                        } catch (Exception var13) {
                           WailaExceptionHandler.handleErr(var13, provider, null);
                        }
                     }
                  }

                  tag.putInt("x", pos.getX());
                  tag.putInt("y", pos.getY());
                  tag.putInt("z", pos.getZ());
                  tag.putString("BlockId", CommonProxy.getId(accessor.getBlock()).toString());
                  responseSender.accept(tag);
               }
            }
         }
      );
   }

   @Override
   public Block getBlock() {
      return this.getBlockState().getBlock();
   }

   @Override
   public BlockState getBlockState() {
      return this.blockState;
   }

   @Override
   public BlockEntity getBlockEntity() {
      return this.blockEntity == null ? null : this.blockEntity.get();
   }

   @Override
   public BlockPos getPosition() {
      return this.getHitResult().getBlockPos();
   }

   @Override
   public Direction getSide() {
      return this.getHitResult().getDirection();
   }

   @Override
   public ItemStack getPickedResult() {
      return CommonProxy.getBlockPickedResult(this.blockState, this.getPlayer(), this.getHitResult());
   }

   @Nullable
   @Override
   public Object getTarget() {
      return this.getBlockEntity();
   }

   @Override
   public boolean isFakeBlock() {
      return !this.fakeBlock.isEmpty();
   }

   @Override
   public ItemStack getFakeBlock() {
      return this.fakeBlock;
   }

   public void setFakeBlock(ItemStack fakeBlock) {
      this.fakeBlock = fakeBlock;
   }

   @Override
   public boolean verifyData(CompoundTag data) {
      if (!this.verify) {
         return true;
      } else {
         int x = data.getInt("x");
         int y = data.getInt("y");
         int z = data.getInt("z");
         BlockPos hitPos = this.getPosition();
         return x == hitPos.getX() && y == hitPos.getY() && z == hitPos.getZ();
      }
   }

   public static class Builder implements BlockAccessor.Builder {
      private Level level;
      private Player player;
      private CompoundTag serverData;
      private boolean connected;
      private boolean showDetails;
      private BlockHitResult hit;
      private BlockState blockState = Blocks.AIR.defaultBlockState();
      private Supplier<BlockEntity> blockEntity;
      private ItemStack fakeBlock = ItemStack.EMPTY;
      private boolean verify;

      public BlockAccessorImpl.Builder level(Level level) {
         this.level = level;
         return this;
      }

      public BlockAccessorImpl.Builder player(Player player) {
         this.player = player;
         return this;
      }

      public BlockAccessorImpl.Builder serverData(CompoundTag serverData) {
         this.serverData = serverData;
         return this;
      }

      public BlockAccessorImpl.Builder serverConnected(boolean connected) {
         this.connected = connected;
         return this;
      }

      public BlockAccessorImpl.Builder showDetails(boolean showDetails) {
         this.showDetails = showDetails;
         return this;
      }

      public BlockAccessorImpl.Builder hit(BlockHitResult hit) {
         this.hit = hit;
         return this;
      }

      public BlockAccessorImpl.Builder blockState(BlockState blockState) {
         this.blockState = blockState;
         return this;
      }

      public BlockAccessorImpl.Builder blockEntity(Supplier<BlockEntity> blockEntity) {
         this.blockEntity = blockEntity;
         return this;
      }

      public BlockAccessorImpl.Builder fakeBlock(ItemStack stack) {
         this.fakeBlock = stack;
         return this;
      }

      public BlockAccessorImpl.Builder from(BlockAccessor accessor) {
         this.level = accessor.getLevel();
         this.player = accessor.getPlayer();
         this.serverData = accessor.getServerData();
         this.connected = accessor.isServerConnected();
         this.showDetails = accessor.showDetails();
         this.hit = accessor.getHitResult();
         this.blockEntity = accessor::getBlockEntity;
         this.blockState = accessor.getBlockState();
         this.fakeBlock = accessor.getFakeBlock();
         return this;
      }

      @Override
      public BlockAccessor.Builder requireVerification() {
         this.verify = true;
         return this;
      }

      @Override
      public BlockAccessor build() {
         BlockAccessorImpl accessor = new BlockAccessorImpl(this);
         if (this.verify) {
            accessor.requireVerification();
         }

         return accessor;
      }
   }

   public record SyncData(boolean showDetails, BlockHitResult hit, BlockState blockState, ItemStack fakeBlock) {
      public static final StreamCodec<RegistryFriendlyByteBuf, BlockAccessorImpl.SyncData> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.BOOL,
         BlockAccessorImpl.SyncData::showDetails,
         StreamCodec.of(FriendlyByteBuf::writeBlockHitResult, FriendlyByteBuf::readBlockHitResult),
         BlockAccessorImpl.SyncData::hit,
         ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
         BlockAccessorImpl.SyncData::blockState,
         ItemStack.OPTIONAL_STREAM_CODEC,
         BlockAccessorImpl.SyncData::fakeBlock,
         BlockAccessorImpl.SyncData::new
      );

      public SyncData(BlockAccessor accessor) {
         this(accessor.showDetails(), accessor.getHitResult(), accessor.getBlockState(), accessor.getFakeBlock());
      }

      public BlockAccessor unpack(ServerPlayer player) {
         Supplier<BlockEntity> blockEntity = null;
         if (this.blockState.hasBlockEntity()) {
            blockEntity = Suppliers.memoize(() -> player.level().getBlockEntity(this.hit.getBlockPos()));
         }

         return new BlockAccessorImpl.Builder()
            .level(player.level())
            .player(player)
            .showDetails(this.showDetails)
            .hit(this.hit)
            .blockState(this.blockState)
            .blockEntity(blockEntity)
            .fakeBlock(this.fakeBlock)
            .build();
      }
   }
}
