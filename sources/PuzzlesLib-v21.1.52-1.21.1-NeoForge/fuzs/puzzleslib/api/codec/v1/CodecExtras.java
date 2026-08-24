package fuzs.puzzleslib.api.codec.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class CodecExtras {
   public static final Codec<NonNullList<ItemStack>> NON_NULL_ITEM_STACK_LIST_CODEC = fuzs.puzzleslib.api.util.v1.CodecExtras.NON_NULL_ITEM_STACK_LIST_CODEC;

   private CodecExtras() {
   }

   public static <T> Codec<NonNullList<T>> nonNullList(Codec<T> codec, Predicate<T> filter, @Nullable T defaultValue) {
      return fuzs.puzzleslib.api.util.v1.CodecExtras.nonNullList(codec, filter, defaultValue);
   }

   public static Function<Tag, DataResult<CompoundTag>> mapCompoundTag() {
      return fuzs.puzzleslib.api.util.v1.CodecExtras.mapCompoundTag();
   }
}
