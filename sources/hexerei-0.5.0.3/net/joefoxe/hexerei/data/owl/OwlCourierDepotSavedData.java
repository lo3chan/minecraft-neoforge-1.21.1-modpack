package net.joefoxe.hexerei.data.owl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataInventoryPacket;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class OwlCourierDepotSavedData extends SavedData {
   protected static final String DATA_NAME = "hexerei_owl_courier_depot";
   Map<GlobalPos, OwlCourierDepotData> depots = new HashMap<>();

   public OwlCourierDepotSavedData addOwlCourierDepot(String name, GlobalPos pos) {
      for (Entry<GlobalPos, OwlCourierDepotData> entry : this.depots.entrySet()) {
         if (entry.getValue().name.equals(name)) {
            return this;
         }

         if (entry.getKey().equals(pos)) {
            return this;
         }
      }

      this.depots.put(pos, new OwlCourierDepotData(name));
      this.syncToClient();
      this.setDirty();
      return this;
   }

   public Map<GlobalPos, OwlCourierDepotData> getDepots() {
      return this.depots;
   }

   public void syncToClient() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         HexereiPacketHandler.sendToAllPlayers(new ClientboundOwlCourierDepotDataPacket(this.save(new CompoundTag(), server.registryAccess())), server);
      }
   }

   public void syncToClient(Player player) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null && player instanceof ServerPlayer serverPlayer) {
         HexereiPacketHandler.sendToPlayerClient(new ClientboundOwlCourierDepotDataPacket(this.save(new CompoundTag(), server.registryAccess())), serverPlayer);
      }
   }

   public void syncInvToClient(GlobalPos pos) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         HexereiPacketHandler.sendToAllPlayers(new ClientboundOwlCourierDepotDataInventoryPacket(this.invNbt(pos, new CompoundTag())), server);
      }
   }

   public void clearOwlCourierDepot(GlobalPos pos) {
      for (Entry<GlobalPos, OwlCourierDepotData> entry : this.depots.entrySet()) {
         GlobalPos depotPos = entry.getKey();
         if (pos.equals(depotPos)) {
            this.depots.remove(entry.getKey());
            this.setDirty();
            this.syncToClient();
            break;
         }
      }
   }

   public void tick(ServerLevel serverLevel) {
   }

   private static OwlCourierDepotSavedData create(CompoundTag tag, Provider registries) {
      OwlCourierDepotSavedData data = new OwlCourierDepotSavedData();
      data.load(tag, registries);
      return data;
   }

   public void load(CompoundTag pCompoundTag, Provider registries) {
      if (pCompoundTag.contains("depots")) {
         ListTag depotList = pCompoundTag.getList("depots", 10);

         for (int i = 0; i < depotList.size(); i++) {
            CompoundTag depotTag = depotList.getCompound(i);
            String depotName = depotTag.getString("DepotName");
            Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, depotTag.get("Pos")).result();
            OwlCourierDepotData depotData = new OwlCourierDepotData(depotName);
            ContainerHelper.loadAllItems(depotTag, depotData.items, registries);
            pos.ifPresent(globalPos -> this.depots.put(globalPos, depotData));
         }
      }
   }

   public CompoundTag save(CompoundTag pCompoundTag, Provider registries) {
      ListTag depotList = new ListTag();

      for (Entry<GlobalPos, OwlCourierDepotData> entry : this.depots.entrySet()) {
         CompoundTag depotTag = new CompoundTag();
         depotTag.putString("DepotName", entry.getValue().name);
         Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result();
         tag.ifPresent(value -> depotTag.put("Pos", value));
         ContainerHelper.saveAllItems(depotTag, entry.getValue().items, registries);
         depotList.add(depotTag);
      }

      pCompoundTag.put("depots", depotList);
      return pCompoundTag;
   }

   public CompoundTag invNbt(GlobalPos pos, CompoundTag pCompoundTag) {
      Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result();
      tag.ifPresent(value -> pCompoundTag.put("Pos", value));
      ContainerHelper.saveAllItems(pCompoundTag, this.depots.get(pos).items, Hexerei.DynamicRegistries.get());
      return pCompoundTag;
   }

   public static Factory<OwlCourierDepotSavedData> factory() {
      return new Factory(OwlCourierDepotSavedData::new, OwlCourierDepotSavedData::create, null);
   }

   public static OwlCourierDepotSavedData get(ServerLevel world) {
      return (OwlCourierDepotSavedData)world.getServer().overworld().getDataStorage().computeIfAbsent(factory(), "hexerei_owl_courier_depot");
   }

   public static OwlCourierDepotSavedData get() {
      return (OwlCourierDepotSavedData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(factory(), "hexerei_owl_courier_depot");
   }
}
