package snownee.jade.api;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public abstract class AccessorImpl<T extends HitResult> implements Accessor<T> {
   private final Level level;
   private final Player player;
   private final CompoundTag serverData;
   private final Supplier<T> hit;
   private final boolean serverConnected;
   private final boolean showDetails;
   protected boolean verify;
   private DynamicOps<Tag> ops;
   private RegistryFriendlyByteBuf buffer;

   public AccessorImpl(Level level, Player player, CompoundTag serverData, Supplier<T> hit, boolean serverConnected, boolean showDetails) {
      this.level = level;
      this.player = player;
      this.hit = hit;
      this.serverConnected = serverConnected;
      this.showDetails = showDetails;
      this.serverData = serverData == null ? new CompoundTag() : serverData.copy();
   }

   @Override
   public Level getLevel() {
      return this.level;
   }

   @Override
   public Player getPlayer() {
      return this.player;
   }

   @NotNull
   @Override
   public final CompoundTag getServerData() {
      return this.serverData;
   }

   @Override
   public DynamicOps<Tag> nbtOps() {
      if (this.ops == null) {
         this.ops = RegistryOps.create(NbtOps.INSTANCE, this.level.registryAccess());
      }

      return this.ops;
   }

   @Override
   public <D> Optional<D> readData(MapDecoder<D> codec) {
      MapLike<Tag> mapLike = (MapLike<Tag>)this.nbtOps().getMap(this.serverData).getOrThrow();
      return codec.decode(this.nbtOps(), mapLike).result();
   }

   @Override
   public <D> void writeData(MapEncoder<D> codec, D value) {
      Tag tag = (Tag)codec.encode(value, this.nbtOps(), this.nbtOps().mapBuilder()).build(new CompoundTag()).getOrThrow();
      this.serverData.merge((CompoundTag)tag);
   }

   private RegistryFriendlyByteBuf buffer() {
      if (this.buffer == null) {
         this.buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), this.level.registryAccess());
      }

      this.buffer.clear();
      return this.buffer;
   }

   @Override
   public <D> Optional<D> decodeFromNbt(StreamDecoder<RegistryFriendlyByteBuf, D> codec, Tag tag) {
      Optional decoded;
      try {
         RegistryFriendlyByteBuf buffer = this.buffer();
         buffer.writeBytes(((ByteArrayTag)tag).getAsByteArray());
         D decodedx = (D)codec.decode(buffer);
         return Optional.of(decodedx);
      } catch (Exception var9) {
         decoded = Optional.empty();
      } finally {
         this.buffer.clear();
      }

      return decoded;
   }

   @Override
   public <D> Tag encodeAsNbt(StreamEncoder<RegistryFriendlyByteBuf, D> streamCodec, D value) {
      RegistryFriendlyByteBuf buffer = this.buffer();
      streamCodec.encode(buffer, value);
      ByteArrayTag tag = new ByteArrayTag(ArrayUtils.subarray(buffer.array(), 0, buffer.readableBytes()));
      buffer.clear();
      return tag;
   }

   @Override
   public T getHitResult() {
      return this.hit.get();
   }

   @Override
   public boolean isServerConnected() {
      return this.serverConnected;
   }

   @Override
   public boolean showDetails() {
      return this.showDetails;
   }

   @Override
   public abstract ItemStack getPickedResult();

   public void requireVerification() {
      this.verify = true;
   }

   @Override
   public float tickRate() {
      return this.getLevel().tickRateManager().tickrate();
   }
}
