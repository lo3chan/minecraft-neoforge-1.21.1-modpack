package net.joefoxe.hexerei.item;

import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.joefoxe.hexerei.item.data_components.BookColorData;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.item.data_components.DyeColorData;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.item.data_components.PotionBottleTypeData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
   public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "hexerei");
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> CANDLE_DATA = register(
      "candle_data", (UnaryOperator<Builder<CustomData>>)(builder -> builder.persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC))
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluteData>> FLUTE = register(
      "flute", (UnaryOperator<Builder<FluteData>>)(builder -> builder.persistent(FluteData.CODEC).networkSynchronized(FluteData.STREAM_CODEC))
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<BookData>> BOOK = register(
      "book", (UnaryOperator<Builder<BookData>>)(builder -> builder.persistent(BookData.CODEC).networkSynchronized(BookData.STREAM_CODEC))
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<BookColorData>> BOOK_COLORS = register(
      "book_colors",
      (UnaryOperator<Builder<BookColorData>>)(builder -> builder.persistent(BookColorData.CODEC).networkSynchronized(BookColorData.STREAM_CODEC))
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<DyeColorData>> DYE_COLOR = register(
      "dye_color", (UnaryOperator<Builder<DyeColorData>>)(builder -> builder.persistent(DyeColorData.CODEC).networkSynchronized(DyeColorData.STREAM_CODEC))
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionBottleTypeData>> POTION_BOTTLE_TYPE = register(
      "potion_bottle_type",
      (UnaryOperator<Builder<PotionBottleTypeData>>)(builder -> builder.persistent(PotionBottleTypeData.CODEC)
         .networkSynchronized(PotionBottleTypeData.STREAM_CODEC))
   );

   static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<Builder<T>> builder) {
      return COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
   }

   static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Codec<T> codec) {
      return COMPONENTS.register(name, () -> DataComponentType.builder().persistent(codec).networkSynchronized(ByteBufCodecs.fromCodec(codec)).build());
   }
}
