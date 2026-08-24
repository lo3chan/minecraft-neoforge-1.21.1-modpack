package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.message.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.TransmutationData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class TileEntityTransmutationTable extends BlockEntity {
   private static final ResourceLocation COMMON_ITEMS = AMCompat.rl("alexsmobs", "gameplay/transmutation_table_common");
   private static final ResourceLocation UNCOMMON_ITEMS = AMCompat.rl("alexsmobs", "gameplay/transmutation_table_uncommon");
   private static final ResourceLocation RARE_ITEMS = AMCompat.rl("alexsmobs", "gameplay/transmutation_table_rare");
   public int ticksExisted;
   private int totalTransmuteCount = 0;
   private final Map<UUID, TransmutationData> playerToData = new HashMap<>();
   private final ItemStack[] possiblities = new ItemStack[3];
   private static final Random RANDOM = new Random();
   private UUID rerollPlayerUUID = null;

   public TileEntityTransmutationTable(BlockPos pos, BlockState state) {
      super(AMTileEntityRegistry.TRANSMUTATION_TABLE.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityTransmutationTable entity) {
      entity.tick();
   }

   private static ItemStack createFromLootTable(Player player, ResourceLocation loc) {
      if (player.level().isClientSide()) {
         return ItemStack.EMPTY;
      } else {
         LootTable loottable = AMCompat.lootTable(player.level().getServer(), loc);
         List<ItemStack> loots = loottable.getRandomItems(
            new Builder((ServerLevel)player.level()).withParameter(LootContextParams.THIS_ENTITY, player).create(LootContextParamSets.PIGLIN_BARTER)
         );
         return loots.isEmpty() ? ItemStack.EMPTY : loots.get(0);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.totalTransmuteCount = AMCompat.getInt(tag, "TotalCount");
      ListTag list = AMCompat.getList(tag, "PlayerTransmutationData", 10);
      if (!list.isEmpty()) {
         for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundtag = AMCompat.getCompound(list, i);
            UUID uuid = AMCompat.getUUID(compoundtag, "UUID");
            if (uuid != null) {
               this.playerToData.put(uuid, TransmutationData.fromNBT(provider, AMCompat.getCompound(compoundtag, "TransmutationData")));
            }
         }
      }

      for (int ix = 0; ix < 3; ix++) {
         if (AMCompat.contains(tag, "Possiblity" + ix)) {
            this.possiblities[ix] = AMCompat.loadItem(provider, AMCompat.getCompound(tag, "Possiblity" + ix));
         }
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putInt("TotalCount", this.totalTransmuteCount);
      ListTag list = new ListTag();

      for (Entry<UUID, TransmutationData> entry : this.playerToData.entrySet()) {
         CompoundTag innerTag = new CompoundTag();
         AMCompat.putUUID(innerTag, "UUID", entry.getKey());
         innerTag.put("TransmutationData", entry.getValue().saveAsNBT(provider));
         list.add(innerTag);
      }

      AMCompat.put(tag, "PlayerTransmutationData", list);

      for (int i = 0; i < 3; i++) {
         if (this.possiblities[i] != null && !this.possiblities[i].isEmpty()) {
            AMCompat.put(tag, "Possiblity" + i, AMCompat.saveItem(provider, this.possiblities[i]));
         }
      }
   }

   private void randomizeResults(Player player) {
      this.rollPossiblity(player, 0);
      this.rollPossiblity(player, 1);
      this.rollPossiblity(player, 2);
      int dataIndex = RANDOM.nextInt(2);
      if (this.playerToData.containsKey(player.getUUID()) && !AMConfig.limitTransmutingToLootTables) {
         TransmutationData data = this.playerToData.get(player.getUUID());
         if (RANDOM.nextFloat() < Math.min(0.01875000074505806 * data.getTotalWeight(), 0.20000000298023224)) {
            ItemStack stack = data.getRandomItem(RANDOM);
            if (stack != null && !stack.isEmpty()) {
               this.possiblities[dataIndex] = stack;
            }
         }
      }

      AlexsMobs.sendMSGToAll(new MessageUpdateTransmutablesToDisplay(player.getId(), this.possiblities[0], this.possiblities[1], this.possiblities[2]));
   }

   public void rollPossiblity(Player player, int i) {
      if (player != null && !player.level().isClientSide() && player.level() instanceof ServerLevel) {
         int safeIndex = Mth.clamp(i, 0, 2);

         this.possiblities[safeIndex] = createFromLootTable(player, switch (safeIndex) {
            default -> COMMON_ITEMS;
            case 1 -> UNCOMMON_ITEMS;
            case 2 -> RARE_ITEMS;
         });
      }
   }

   public boolean hasPossibilities() {
      for (int i = 0; i < 3; i++) {
         if (this.possiblities[i] == null || this.possiblities[i].isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getPossibility(int i) {
      int safeIndex = Mth.clamp(i, 0, 2);
      ItemStack possible = this.possiblities[safeIndex];
      return possible == null ? ItemStack.EMPTY : possible;
   }

   public void postTransmute(Player player, ItemStack from, ItemStack to) {
      TransmutationData data;
      if (this.playerToData.containsKey(player.getUUID())) {
         data = this.playerToData.get(player.getUUID());
      } else {
         data = new TransmutationData();
      }

      data.onTransmuteItem(from, to);
      this.playerToData.put(player.getUUID(), data);
      this.totalTransmuteCount = this.totalTransmuteCount + from.getCount();
      if (player instanceof ServerPlayer && this.totalTransmuteCount >= 1000) {
         AMAdvancementTriggerRegistry.TRANSMUTE_1000_ITEMS.trigger((ServerPlayer)player);
      }

      this.setRerollPlayerUUID(player.getUUID());
   }

   public void tick() {
      this.ticksExisted++;
      if (this.rerollPlayerUUID != null) {
         Player player = this.level.getPlayerByUUID(this.rerollPlayerUUID);
         if (player != null) {
            this.level
               .playSound(
                  null, this.getBlockPos(), AMSoundRegistry.TRANSMUTE_ITEM.get(), SoundSource.BLOCKS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F
               );
            this.randomizeResults(player);
         }

         this.rerollPlayerUUID = null;
      }
   }

   public void setRerollPlayerUUID(UUID uuid) {
      this.rerollPlayerUUID = uuid;
   }
}
