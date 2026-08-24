package snownee.jade.impl;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.AccessorImpl;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.network.RequestEntityPacket;
import snownee.jade.network.ServerPayloadContext;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.WailaExceptionHandler;

public class EntityAccessorImpl extends AccessorImpl<EntityHitResult> implements EntityAccessor {
   private final Supplier<Entity> entity;

   public EntityAccessorImpl(EntityAccessorImpl.Builder builder) {
      super(builder.level, builder.player, builder.serverData, builder.hit, builder.connected, builder.showDetails);
      this.entity = builder.entity;
   }

   public static void handleRequest(RequestEntityPacket message, ServerPayloadContext context, Consumer<CompoundTag> responseSender) {
      ServerPlayer player = context.player();
      context.execute(() -> {
         EntityAccessor accessor = message.data().unpack(player);
         if (accessor != null) {
            Entity entity = accessor.getEntity();
            double maxDistance = Mth.square(player.entityInteractionRange() + 21.0);
            if (entity != null && !(player.distanceToSqr(entity) > maxDistance)) {
               List<IServerDataProvider<EntityAccessor>> providers = WailaCommonRegistration.instance().getEntityNBTProviders(entity);
               CompoundTag tag = accessor.getServerData();

               for (IServerDataProvider<EntityAccessor> provider : providers) {
                  if (message.dataProviders().contains(provider)) {
                     try {
                        provider.appendServerData(tag, accessor);
                     } catch (Exception var12) {
                        WailaExceptionHandler.handleErr(var12, provider, null);
                     }
                  }
               }

               tag.putInt("EntityId", entity.getId());
               responseSender.accept(tag);
            }
         }
      });
   }

   @Override
   public Entity getEntity() {
      return CommonProxy.wrapPartEntityParent(this.getRawEntity());
   }

   @Override
   public Entity getRawEntity() {
      return this.entity.get();
   }

   @Override
   public ItemStack getPickedResult() {
      return CommonProxy.getEntityPickedResult(this.entity.get(), this.getPlayer(), this.getHitResult());
   }

   @NotNull
   @Override
   public Object getTarget() {
      return this.getEntity();
   }

   @Override
   public boolean verifyData(CompoundTag data) {
      if (!this.verify) {
         return true;
      } else {
         return !data.contains("EntityId") ? false : data.getInt("EntityId") == this.getEntity().getId();
      }
   }

   public static class Builder implements EntityAccessor.Builder {
      public boolean showDetails;
      private Level level;
      private Player player;
      private CompoundTag serverData;
      private boolean connected;
      private Supplier<EntityHitResult> hit;
      private Supplier<Entity> entity;
      private boolean verify;

      public EntityAccessorImpl.Builder level(Level level) {
         this.level = level;
         return this;
      }

      public EntityAccessorImpl.Builder player(Player player) {
         this.player = player;
         return this;
      }

      public EntityAccessorImpl.Builder serverData(CompoundTag serverData) {
         this.serverData = serverData;
         return this;
      }

      public EntityAccessorImpl.Builder serverConnected(boolean connected) {
         this.connected = connected;
         return this;
      }

      public EntityAccessorImpl.Builder showDetails(boolean showDetails) {
         this.showDetails = showDetails;
         return this;
      }

      public EntityAccessorImpl.Builder hit(Supplier<EntityHitResult> hit) {
         this.hit = hit;
         return this;
      }

      public EntityAccessorImpl.Builder entity(Supplier<Entity> entity) {
         this.entity = entity;
         return this;
      }

      public EntityAccessorImpl.Builder from(EntityAccessor accessor) {
         this.level = accessor.getLevel();
         this.player = accessor.getPlayer();
         this.serverData = accessor.getServerData();
         this.connected = accessor.isServerConnected();
         this.showDetails = accessor.showDetails();
         this.hit = accessor::getHitResult;
         this.entity = accessor::getEntity;
         return this;
      }

      @Override
      public EntityAccessor.Builder requireVerification() {
         this.verify = true;
         return this;
      }

      @Override
      public EntityAccessor build() {
         EntityAccessorImpl accessor = new EntityAccessorImpl(this);
         if (this.verify) {
            accessor.requireVerification();
         }

         return accessor;
      }
   }

   public record SyncData(boolean showDetails, int id, int partIndex, Vec3 hitVec) {
      public static final StreamCodec<RegistryFriendlyByteBuf, EntityAccessorImpl.SyncData> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.BOOL,
         EntityAccessorImpl.SyncData::showDetails,
         ByteBufCodecs.VAR_INT,
         EntityAccessorImpl.SyncData::id,
         ByteBufCodecs.VAR_INT,
         EntityAccessorImpl.SyncData::partIndex,
         ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f),
         EntityAccessorImpl.SyncData::hitVec,
         EntityAccessorImpl.SyncData::new
      );

      public SyncData(EntityAccessor accessor) {
         this(
            accessor.showDetails(),
            accessor.getEntity().getId(),
            CommonProxy.getPartEntityIndex(accessor.getRawEntity()),
            accessor.getHitResult().getLocation()
         );
      }

      public EntityAccessor unpack(ServerPlayer player) {
         Supplier<Entity> entity = Suppliers.memoize(() -> CommonProxy.getPartEntity(player.level().getEntity(this.id), this.partIndex));
         return new EntityAccessorImpl.Builder()
            .level(player.level())
            .player(player)
            .showDetails(this.showDetails)
            .entity(entity)
            .hit(Suppliers.memoize(() -> new EntityHitResult(entity.get(), this.hitVec)))
            .build();
      }
   }
}
