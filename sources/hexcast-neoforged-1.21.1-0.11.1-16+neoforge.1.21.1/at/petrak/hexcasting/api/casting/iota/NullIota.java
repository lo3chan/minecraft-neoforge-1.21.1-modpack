package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullIota extends Iota {
   private static final Object NULL_SUBSTITUTE = new Object();
   public static final Component DISPLAY = Component.translatable("hexcasting.tooltip.null_iota").withStyle(ChatFormatting.GRAY);
   public static IotaType<NullIota> TYPE = new IotaType<NullIota>() {
      @Nullable
      public NullIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
         return new NullIota();
      }

      @Override
      public Component display(Tag tag) {
         return NullIota.DISPLAY;
      }

      @Override
      public int color() {
         return -5592406;
      }
   };

   public NullIota() {
      super(HexIotaTypes.NULL, NULL_SUBSTITUTE);
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
