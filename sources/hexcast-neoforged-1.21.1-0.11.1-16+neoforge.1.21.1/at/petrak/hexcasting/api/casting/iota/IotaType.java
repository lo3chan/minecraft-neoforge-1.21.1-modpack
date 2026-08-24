package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;

public abstract class IotaType<T extends Iota> {
   @Nullable
   public abstract T deserialize(Tag var1, ServerLevel var2) throws IllegalArgumentException;

   public abstract Component display(Tag var1);

   public abstract int color();

   public Component typeName() {
      ResourceLocation key = HexIotaTypes.REGISTRY.getKey(this);
      return Component.translatable("hexcasting.iota." + key).withStyle(style -> style.withColor(TextColor.fromRgb(this.color())));
   }

   public static CompoundTag serialize(Iota iota) {
      IotaType<?> type = iota.getType();
      ResourceLocation typeId = HexIotaTypes.REGISTRY.getKey(type);
      if (typeId == null) {
         throw new IllegalStateException("Tried to serialize an unregistered iota type. Iota: " + iota + " ; Type" + type.getClass().getTypeName());
      } else if (isTooLargeToSerialize(List.of(iota), 0)) {
         return serialize(new GarbageIota());
      } else {
         Tag dataTag = iota.serialize();
         CompoundTag out = new CompoundTag();
         out.putString("hexcasting:type", typeId.toString());
         out.put("hexcasting:data", dataTag);
         return out;
      }
   }

   public static boolean isTooLargeToSerialize(Iterable<Iota> examinee) {
      return isTooLargeToSerialize(examinee, 1);
   }

   private static boolean isTooLargeToSerialize(Iterable<Iota> examinee, int startingCount) {
      ArrayDeque<Pair<Iterable<Iota>, Integer>> listsToExamine = new ArrayDeque<>(Collections.singleton(new Pair(examinee, 0)));
      int totalEltsFound = startingCount;

      while (!listsToExamine.isEmpty()) {
         Pair<Iterable<Iota>, Integer> iotaPair = listsToExamine.removeFirst();
         Iterable<Iota> sublist = (Iterable<Iota>)iotaPair.getFirst();
         int depth = (Integer)iotaPair.getSecond();

         for (Iota iota : sublist) {
            totalEltsFound += iota.size();
            if (totalEltsFound >= 1024) {
               return true;
            }

            Iterable<Iota> subIotas = iota.subIotas();
            if (subIotas != null) {
               if (depth + 1 >= 256) {
                  return true;
               }

               listsToExamine.addLast(new Pair(subIotas, depth + 1));
            }
         }
      }

      return false;
   }

   @org.jetbrains.annotations.Nullable
   public static IotaType<?> getTypeFromTag(CompoundTag tag) {
      if (!tag.contains("hexcasting:type", 8)) {
         return null;
      } else {
         String typeKey = tag.getString("hexcasting:type");
         if (ResourceLocation.tryParse(typeKey) == null) {
            return null;
         } else {
            ResourceLocation typeLoc = ResourceLocation.parse(typeKey);
            return (IotaType<?>)HexIotaTypes.REGISTRY.get(typeLoc);
         }
      }
   }

   public static Iota deserialize(CompoundTag tag, ServerLevel world) {
      IotaType<?> type = getTypeFromTag(tag);
      if (type == null) {
         return new GarbageIota();
      } else {
         Tag data = tag.get("hexcasting:data");
         if (data == null) {
            return new GarbageIota();
         } else {
            Iota deserialized;
            try {
               deserialized = Objects.requireNonNullElse(type.deserialize(data, world), new NullIota());
            } catch (IllegalArgumentException var6) {
               HexAPI.LOGGER.warn("Caught an exception deserializing an iota", var6);
               deserialized = new GarbageIota();
            }

            return deserialized;
         }
      }
   }

   private static Component brokenIota() {
      return Component.translatable("hexcasting.spelldata.unknown").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
   }

   public static Component getDisplay(CompoundTag tag) {
      IotaType<?> type = getTypeFromTag(tag);
      if (type == null) {
         return brokenIota();
      } else {
         Tag data = tag.get("hexcasting:data");
         return data == null ? brokenIota() : type.display(data);
      }
   }

   public static FormattedCharSequence getDisplayWithMaxWidth(CompoundTag tag, int maxWidth, Font font) {
      IotaType<?> type = getTypeFromTag(tag);
      if (type == null) {
         return brokenIota().getVisualOrderText();
      } else {
         Tag data = tag.get("hexcasting:data");
         if (data == null) {
            return brokenIota().getVisualOrderText();
         } else {
            Component display = type.display(data);
            List<FormattedCharSequence> splitted = font.split(display, maxWidth - font.width("..."));
            if (splitted.isEmpty()) {
               return FormattedCharSequence.EMPTY;
            } else if (splitted.size() == 1) {
               return splitted.get(0);
            } else {
               FormattedCharSequence first = splitted.get(0);
               return FormattedCharSequence.fromPair(first, Component.literal("...").withStyle(ChatFormatting.GRAY).getVisualOrderText());
            }
         }
      }
   }

   public static int getColor(CompoundTag tag) {
      IotaType<?> type = getTypeFromTag(tag);
      return type == null ? -524040 : type.color();
   }
}
