package snownee.jade.api;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Accessor<T extends HitResult> {
   Level getLevel();

   Player getPlayer();

   @NotNull
   CompoundTag getServerData();

   DynamicOps<Tag> nbtOps();

   <D> Optional<D> readData(MapDecoder<D> var1);

   <D> void writeData(MapEncoder<D> var1, D var2);

   <D> Optional<D> decodeFromNbt(StreamDecoder<RegistryFriendlyByteBuf, D> var1, Tag var2);

   <D> Tag encodeAsNbt(StreamEncoder<RegistryFriendlyByteBuf, D> var1, D var2);

   T getHitResult();

   boolean isServerConnected();

   ItemStack getPickedResult();

   boolean showDetails();

   @Nullable
   Object getTarget();

   Class<? extends Accessor<?>> getAccessorType();

   boolean verifyData(CompoundTag var1);

   float tickRate();
}
