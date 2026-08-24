package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListIota extends Iota {
   public static IotaType<ListIota> TYPE = new IotaType<ListIota>() {
      @Nullable
      public ListIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
         ListTag listTag = HexUtils.downcast(tag, ListTag.TYPE);
         ArrayList<Iota> out = new ArrayList<>(listTag.size());

         for (Tag sub : listTag) {
            CompoundTag csub = HexUtils.downcast(sub, CompoundTag.TYPE);
            Iota subiota = IotaType.deserialize(csub, world);
            if (subiota == null) {
               return null;
            }

            out.add(subiota);
         }

         return new ListIota(out);
      }

      @Override
      public Component display(Tag tag) {
         MutableComponent out = Component.empty();
         ListTag list = HexUtils.downcast(tag, ListTag.TYPE);

         for (int i = 0; i < list.size(); i++) {
            Tag sub = list.get(i);
            CompoundTag csub = HexUtils.downcast(sub, CompoundTag.TYPE);
            out.append(IotaType.getDisplay(csub));
            if (i < list.size() - 1) {
               out.append(", ");
            }
         }

         return Component.translatable("hexcasting.tooltip.list_contents", new Object[]{out}).withStyle(ChatFormatting.DARK_PURPLE);
      }

      @Override
      public int color() {
         return -5635926;
      }
   };

   public ListIota(@NotNull SpellList list) {
      super(HexIotaTypes.LIST, list);
   }

   public ListIota(@NotNull List<Iota> list) {
      this(new SpellList.LList(list));
   }

   public SpellList getList() {
      return (SpellList)this.payload;
   }

   @Override
   public boolean isTruthy() {
      return this.getList().getNonEmpty();
   }

   @Override
   public boolean toleratesOther(Iota that) {
      if (!typesMatch(this, that)) {
         return false;
      } else {
         SpellList a = this.getList();
         if (!(that instanceof ListIota list)) {
            return false;
         } else {
            SpellList b = list.getList();
            SpellList.SpellListIterator aIter = a.iterator();
            SpellList.SpellListIterator bIter = b.iterator();

            while (aIter.hasNext() || bIter.hasNext()) {
               if (aIter.hasNext() != bIter.hasNext()) {
                  return false;
               }

               Iota x = aIter.next();
               Iota y = bIter.next();
               if (!Iota.tolerates(x, y)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @NotNull
   @Override
   public Tag serialize() {
      ListTag out = new ListTag();

      for (Iota subdatum : this.getList()) {
         out.add(IotaType.serialize(subdatum));
      }

      return out;
   }

   @Nullable
   @Override
   public Iterable<Iota> subIotas() {
      return this.getList();
   }
}
