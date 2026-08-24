package net.joefoxe.hexerei.data.owl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataInventoryPacket;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public class ClientOwlCourierDepotData {
   public static Map<GlobalPos, OwlCourierDepotData> depots = new HashMap<>();

   public static Map<GlobalPos, OwlCourierDepotData> getDepots() {
      return depots;
   }

   public static void update(ClientboundOwlCourierDepotDataPacket packet) {
      load(packet.getTag());
   }

   public static void update(ClientboundOwlCourierDepotDataInventoryPacket packet) {
      invLoad(packet.getTag());
   }

   public static void invLoad(CompoundTag pCompoundTag) {
      Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, pCompoundTag.get("Pos")).result();
      NonNullList<ItemStack> stacks = NonNullList.withSize(8, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(pCompoundTag, stacks, Hexerei.DynamicRegistries.get());
      pos.ifPresent(globalPos -> getDepots().get(globalPos).items = stacks);
   }

   public static void load(CompoundTag pCompoundTag) {
      depots.clear();
      if (pCompoundTag.contains("depots")) {
         ListTag depotList = pCompoundTag.getList("depots", 10);

         for (int i = 0; i < depotList.size(); i++) {
            CompoundTag depotTag = depotList.getCompound(i);
            String depotName = depotTag.getString("DepotName");
            Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, depotTag.get("Pos")).result();
            OwlCourierDepotData depotData = new OwlCourierDepotData(depotName);
            ContainerHelper.loadAllItems(depotTag, depotData.items, Hexerei.DynamicRegistries.get());
            pos.ifPresent(globalPos -> depots.put(globalPos, depotData));
         }
      }
   }
}
