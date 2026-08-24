package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GarbageIota extends Iota {
   private static final Object NULL_SUBSTITUTE = new Object();
   public static final Component DISPLAY = Component.literal("arimfexendrapuse")
      .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.OBFUSCATED});
   private static final Random RANDOM = new Random();
   public static IotaType<GarbageIota> TYPE = new IotaType<GarbageIota>() {
      @Nullable
      public GarbageIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
         return new GarbageIota();
      }

      @Override
      public Component display(Tag tag) {
         return GarbageIota.DISPLAY;
      }

      @Override
      public int color() {
         return -11513776;
      }
   };

   public GarbageIota() {
      super(HexIotaTypes.GARBAGE, NULL_SUBSTITUTE);
   }

   @Override
   public boolean isTruthy() {
      return false;
   }

   @Override
   public boolean toleratesOther(Iota that) {
      return typesMatch(this, that);
   }

   @NotNull
   @Override
   public Tag serialize() {
      return new CompoundTag();
   }
}
