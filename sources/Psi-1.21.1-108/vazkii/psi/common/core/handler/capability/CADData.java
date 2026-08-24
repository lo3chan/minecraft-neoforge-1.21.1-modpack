package vazkii.psi.common.core.handler.capability;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADData;
import vazkii.psi.api.cad.IPsiBarDisplay;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.base.ModDataComponents;

public class CADData implements ICapabilityProvider<ItemCapability<?, Void>, Void, CADData>, ICADData, ISpellAcceptor, ISocketable, IPsiBarDisplay {
   private final ItemStack cad;
   private final ComponentItemHandler cadHandler;
   private final CADData.Data data;

   public CADData(ItemStack cad) {
      this.cad = cad;
      this.cadHandler = (ComponentItemHandler)cad.getCapability(ItemHandler.ITEM);
      CADData.Data cadData = (CADData.Data)cad.get(ModDataComponents.CAD_DATA);
      if (cadData == null) {
         cadData = new CADData.Data(0, 0, new ArrayList<>());
         cad.set(ModDataComponents.CAD_DATA, cadData);
      }

      this.data = cadData;
   }

   @Nullable
   public CADData getCapability(@NotNull ItemCapability<?, Void> capability, @Nullable Void facing) {
      return capability != PsiAPI.SOCKETABLE_CAPABILITY
            && capability != PsiAPI.CAD_DATA_CAPABILITY
            && capability != PsiAPI.PSI_BAR_DISPLAY_CAPABILITY
            && capability != PsiAPI.SPELL_ACCEPTOR_CAPABILITY
         ? null
         : this;
   }

   @Override
   public int getTime() {
      return this.data.time;
   }

   @Override
   public void setTime(int time) {
      if (this.data.time != time) {
         this.data.time = time;
      }
   }

   @Override
   public int getBattery() {
      return this.data.battery;
   }

   @Override
   public void setBattery(int battery) {
      this.data.battery = battery;
   }

   @Override
   public Vector3 getSavedVector(int memorySlot) {
      if (this.data.vectors.size() <= memorySlot) {
         return Vector3.zero.copy();
      } else {
         Vector3 vec = this.data.vectors.get(memorySlot);
         return (vec == null ? Vector3.zero : vec).copy();
      }
   }

   @Override
   public void setSavedVector(int memorySlot, Vector3 value) {
      while (this.data.vectors.size() <= memorySlot) {
         this.data.vectors.add(null);
      }

      this.data.vectors.set(memorySlot, value);
   }

   @Override
   public void setSpell(Player player, Spell spell) {
      int slot = this.getSelectedSlot();
      ItemStack bullet = this.getBulletInSocket(slot);
      if (!bullet.isEmpty() && ISpellAcceptor.isAcceptor(bullet)) {
         ISpellAcceptor.acceptor(bullet).setSpell(player, spell);
         this.setBulletInSocket(slot, bullet);
         player.getCooldowns().addCooldown(this.cad.getItem(), 10);
      }
   }

   @Override
   public boolean requiresSneakForSpellSet() {
      return true;
   }

   @Override
   public boolean isSocketSlotAvailable(int slot) {
      if (!(this.cad.getItem() instanceof ICAD)) {
         return false;
      } else {
         int sockets = ((ICAD)this.cad.getItem()).getStatValue(this.cad, EnumCADStat.SOCKETS);
         if (sockets == -1 || sockets > 12) {
            sockets = 12;
         }

         return slot < sockets && slot >= 0;
      }
   }

   @Override
   public ItemStack getBulletInSocket(int slot) {
      return this.isSocketSlotAvailable(slot) ? this.cadHandler.getStackInSlot(slot) : ItemStack.EMPTY;
   }

   @Override
   public void setBulletInSocket(int slot, ItemStack bullet) {
      if (this.isSocketSlotAvailable(slot)) {
         this.cadHandler.setStackInSlot(slot, bullet);
      }
   }

   @Override
   public int getSelectedSlot() {
      return (Integer)this.cad.getOrDefault(ModDataComponents.SELECTED_SLOT, 0);
   }

   @Override
   public void setSelectedSlot(int slot) {
      this.cad.set(ModDataComponents.SELECTED_SLOT, slot);
   }

   @Override
   public int getLastSlot() {
      int sockets = ((ICAD)this.cad.getItem()).getStatValue(this.cad, EnumCADStat.SOCKETS);
      if (sockets == -1 || sockets > 12) {
         sockets = 12;
      }

      return sockets - 1;
   }

   @Override
   public CompoundTag serializeForSynchronization() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("Time", this.data.time);
      compound.putInt("Battery", this.data.battery);
      return compound;
   }

   public CompoundTag serializeNBT(@NotNull Provider provider) {
      CompoundTag compound = this.serializeForSynchronization();
      ListTag memory = new ListTag();

      for (Vector3 vector : this.data.vectors) {
         if (vector == null) {
            memory.add(new ListTag());
         } else {
            ListTag vec = new ListTag();
            vec.add(DoubleTag.valueOf(vector.x));
            vec.add(DoubleTag.valueOf(vector.y));
            vec.add(DoubleTag.valueOf(vector.z));
            memory.add(vec);
         }
      }

      compound.put("Memory", memory);
      return compound;
   }

   public void deserializeNBT(@NotNull Provider provider, CompoundTag nbt) {
      if (nbt.contains("Time", 99)) {
         this.data.time = nbt.getInt("Time");
      }

      if (nbt.contains("Battery", 99)) {
         this.data.battery = nbt.getInt("Battery");
      }

      if (nbt.contains("Memory", 9)) {
         ListTag memory = nbt.getList("Memory", 9);
         List<Vector3> newVectors = Lists.newArrayList();

         for (Tag tag : memory) {
            ListTag vec = (ListTag)tag;
            if (vec.getElementType() == 6 && vec.size() >= 3) {
               newVectors.add(new Vector3(vec.getDouble(0), vec.getDouble(1), vec.getDouble(2)));
            } else {
               newVectors.add(null);
            }
         }

         this.data.vectors = newVectors;
      }
   }

   @Override
   public boolean shouldShow(IPlayerData data) {
      return true;
   }

   public static class Data {
      public static final Codec<CADData.Data> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               Codec.INT.fieldOf("Time").forGetter(data -> data.time),
               Codec.INT.fieldOf("Battery").forGetter(data -> data.battery),
               Codec.list(Vector3.CODEC).fieldOf("Memory").forGetter(data -> data.vectors)
            )
            .apply(builder, CADData.Data::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CADData.Data> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.INT,
         data -> data.time,
         ByteBufCodecs.INT,
         data -> data.battery,
         Vector3.STREAM_CODEC.apply(ByteBufCodecs.list()),
         data -> data.vectors,
         CADData.Data::new
      );
      public int time;
      public int battery;
      public List<Vector3> vectors;

      public Data(int time, int battery, List<Vector3> vectors) {
         this.time = time;
         this.battery = battery;
         this.vectors = vectors;
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else if (other == null) {
            return false;
         } else {
            return !(other instanceof CADData.Data data) ? false : data.time == this.time && data.battery == this.battery && data.vectors.equals(this.vectors);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.time, this.battery, this.vectors);
      }
   }
}
