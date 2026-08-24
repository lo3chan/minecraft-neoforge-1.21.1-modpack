package fuzs.puzzleslib.api.network.v4.codec;

import io.netty.buffer.ByteBuf;
import java.time.Instant;
import java.util.BitSet;
import java.util.Date;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class ExtraStreamCodecs {
   public static final StreamCodec<ByteBuf, Character> CHAR = StreamCodec.of((buf, character) -> buf.writeChar(character), ByteBuf::readChar);
   public static final StreamCodec<FriendlyByteBuf, Date> DATE = StreamCodec.of(FriendlyByteBuf::writeDate, FriendlyByteBuf::readDate);
   public static final StreamCodec<FriendlyByteBuf, Instant> INSTANT = StreamCodec.of(FriendlyByteBuf::writeInstant, FriendlyByteBuf::readInstant);
   public static final StreamCodec<FriendlyByteBuf, BlockHitResult> BLOCK_HIT_RESULT = StreamCodec.of(
      FriendlyByteBuf::writeBlockHitResult, FriendlyByteBuf::readBlockHitResult
   );
   public static final StreamCodec<FriendlyByteBuf, BitSet> BIT_SET = StreamCodec.of(FriendlyByteBuf::writeBitSet, FriendlyByteBuf::readBitSet);
   public static final StreamCodec<ByteBuf, BlockState> BLOCK_STATE = ByteBufCodecs.VAR_INT.map(Block::stateById, Block::getId);

   private ExtraStreamCodecs() {
   }

   public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Class<E> clazz) {
      return fromEnum(clazz::getEnumConstants);
   }

   public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Supplier<E[]> enumValues) {
      return fromEnumWithMapping(enumValues, Enum::ordinal);
   }

   public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnumWithMapping(Supplier<E[]> enumValues, ToIntFunction<E> keyFunction) {
      E[] enums = (E[])enumValues.get();
      IntFunction<E> idMapper = ByIdMap.continuous(keyFunction, enums, OutOfBoundsStrategy.ZERO);
      return ByteBufCodecs.idMapper(idMapper, keyFunction);
   }
}
