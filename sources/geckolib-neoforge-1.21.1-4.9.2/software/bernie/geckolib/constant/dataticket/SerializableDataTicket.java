package software.bernie.geckolib.constant.dataticket;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;

public abstract class SerializableDataTicket<D> extends DataTicket<D> {
   public static final StreamCodec<RegistryFriendlyByteBuf, SerializableDataTicket<?>> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, DataTicket::id, DataTickets::byName
   );

   public SerializableDataTicket(String id, Class<? extends D> objectType) {
      super(id, objectType);
   }

   public abstract StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec();

   public static SerializableDataTicket<Double> ofDouble(ResourceLocation id) {
      return new SerializableDataTicket<Double>(id.toString(), Double.class) {
         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, Double> streamCodec() {
            return ByteBufCodecs.DOUBLE;
         }
      };
   }

   public static SerializableDataTicket<Float> ofFloat(ResourceLocation id) {
      return new SerializableDataTicket<Float>(id.toString(), Float.class) {
         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, Float> streamCodec() {
            return ByteBufCodecs.FLOAT;
         }
      };
   }

   public static SerializableDataTicket<Boolean> ofBoolean(ResourceLocation id) {
      return new SerializableDataTicket<Boolean>(id.toString(), Boolean.class) {
         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, Boolean> streamCodec() {
            return ByteBufCodecs.BOOL;
         }
      };
   }

   public static SerializableDataTicket<Integer> ofInt(ResourceLocation id) {
      return new SerializableDataTicket<Integer>(id.toString(), Integer.class) {
         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, Integer> streamCodec() {
            return ByteBufCodecs.VAR_INT;
         }
      };
   }

   public static SerializableDataTicket<String> ofString(ResourceLocation id) {
      return new SerializableDataTicket<String>(id.toString(), String.class) {
         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, String> streamCodec() {
            return ByteBufCodecs.STRING_UTF8;
         }
      };
   }

   public static <E extends Enum<E>> SerializableDataTicket<E> ofEnum(ResourceLocation id, final Class<E> enumClass) {
      return new SerializableDataTicket<E>(id.toString(), enumClass) {
         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, E> streamCodec() {
            return new StreamCodec<RegistryFriendlyByteBuf, E>() {
               public E decode(RegistryFriendlyByteBuf buf) {
                  return Enum.valueOf(enumClass, buf.readUtf());
               }

               public void encode(RegistryFriendlyByteBuf buf, E data) {
                  buf.writeUtf(data.toString());
               }
            };
         }
      };
   }
}
