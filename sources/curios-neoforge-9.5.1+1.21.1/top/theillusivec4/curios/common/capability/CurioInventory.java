package top.theillusivec4.curios.common.capability;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioStacksHandler;

public class CurioInventory implements INBTSerializable<CompoundTag> {
   final Map<String, ICurioStacksHandler> curios = new LinkedHashMap<>();
   ICuriosItemHandler curiosItemHandler;
   NonNullList<ItemStack> invalidStacks = NonNullList.create();
   Set<ICurioStacksHandler> updates = new HashSet<>();
   CompoundTag deserialized = new CompoundTag();
   boolean markDeserialized = false;
   final Cache<String, Pair<Long, Optional<SlotResult>>> firstCurioCache = CacheBuilder.newBuilder()
      .maximumSize(100L)
      .expireAfterWrite(1L, TimeUnit.SECONDS)
      .build();
   final Cache<String, Pair<Long, List<SlotResult>>> findCuriosCache = CacheBuilder.newBuilder()
      .maximumSize(100L)
      .expireAfterWrite(1L, TimeUnit.SECONDS)
      .build();

   public void init(ICuriosItemHandler curiosItemHandler) {
      this.curiosItemHandler = curiosItemHandler;
      this.curios.clear();
      LivingEntity livingEntity = curiosItemHandler.getWearer();
      if (!this.markDeserialized) {
         for (ISlotType slotType : new TreeSet<>(CuriosApi.getEntitySlots(livingEntity).values())) {
            this.curios
               .put(
                  slotType.getIdentifier(),
                  new CurioStacksHandler(
                     curiosItemHandler,
                     slotType.getIdentifier(),
                     slotType.getSize(),
                     slotType.useNativeGui(),
                     slotType.hasCosmetic(),
                     slotType.canToggleRendering(),
                     slotType.getDropRule()
                  )
               );
         }
      } else {
         this.markDeserialized = false;
         ListTag tagList = this.deserialized.getList("Curios", 10);
         Map<String, ICurioStacksHandler> curios = new LinkedHashMap<>();
         SortedMap<ISlotType, ICurioStacksHandler> sortedCurios = new TreeMap<>();

         for (ISlotType slotType : new TreeSet<>(CuriosApi.getEntitySlots(livingEntity).values())) {
            sortedCurios.put(
               slotType,
               new CurioStacksHandler(
                  curiosItemHandler,
                  slotType.getIdentifier(),
                  slotType.getSize(),
                  slotType.useNativeGui(),
                  slotType.hasCosmetic(),
                  slotType.canToggleRendering(),
                  slotType.getDropRule()
               )
            );
         }

         for (int i = 0; i < tagList.size(); i++) {
            CompoundTag tag = tagList.getCompound(i);
            String identifier = tag.getString("Identifier");
            CurioStacksHandler prevStacksHandler = new CurioStacksHandler(curiosItemHandler, identifier);
            prevStacksHandler.deserializeNBT(tag.getCompound("StacksHandler"));
            Optional<ISlotType> optionalType = Optional.ofNullable(CuriosApi.getEntitySlots(livingEntity).get(identifier));
            optionalType.ifPresent(
               slotType -> {
                  CurioStacksHandler newStacksHandler = new CurioStacksHandler(
                     curiosItemHandler,
                     slotType.getIdentifier(),
                     slotType.getSize(),
                     slotType.useNativeGui(),
                     slotType.hasCosmetic(),
                     slotType.canToggleRendering(),
                     slotType.getDropRule()
                  );
                  newStacksHandler.copyModifiers(prevStacksHandler);

                  int index;
                  for (index = 0; index < newStacksHandler.getSlots() && index < prevStacksHandler.getSlots(); index++) {
                     ItemStack prevStack = prevStacksHandler.getStacks().getStackInSlot(index);
                     if (!prevStack.isEmpty()) {
                        if (newStacksHandler.getStacks().isItemValid(index, prevStack)) {
                           newStacksHandler.getStacks().setStackInSlot(index, prevStack);
                        } else {
                           this.curiosItemHandler.loseInvalidStack(prevStack);
                        }
                     }

                     ItemStack prevCosmetic = prevStacksHandler.getCosmeticStacks().getStackInSlot(index);
                     if (!prevCosmetic.isEmpty()) {
                        if (newStacksHandler.getStacks().isItemValid(index, prevCosmetic)) {
                           newStacksHandler.getCosmeticStacks().setStackInSlot(index, prevStacksHandler.getCosmeticStacks().getStackInSlot(index));
                        } else {
                           this.curiosItemHandler.loseInvalidStack(prevCosmetic);
                        }
                     }
                  }

                  while (index < prevStacksHandler.getSlots()) {
                     this.curiosItemHandler.loseInvalidStack(prevStacksHandler.getStacks().getStackInSlot(index));
                     this.curiosItemHandler.loseInvalidStack(prevStacksHandler.getCosmeticStacks().getStackInSlot(index));
                     index++;
                  }

                  sortedCurios.put(slotType, newStacksHandler);

                  for (int jxx = 0; jxx < newStacksHandler.getRenders().size() && jxx < prevStacksHandler.getRenders().size(); jxx++) {
                     newStacksHandler.getRenders().set(jxx, (Boolean)prevStacksHandler.getRenders().get(jxx));
                  }

                  for (int jx = 0; jx < newStacksHandler.getActiveStates().size() && jx < prevStacksHandler.getActiveStates().size(); jx++) {
                     newStacksHandler.getActiveStates().set(jx, (Boolean)prevStacksHandler.getActiveStates().get(jx));
                  }
               }
            );
            if (optionalType.isEmpty()) {
               IDynamicStackHandler stackHandler = prevStacksHandler.getStacks();
               IDynamicStackHandler cosmeticStackHandler = prevStacksHandler.getCosmeticStacks();

               for (int j = 0; j < stackHandler.getSlots(); j++) {
                  ItemStack stack = stackHandler.getStackInSlot(j);
                  if (!stack.isEmpty()) {
                     this.curiosItemHandler.loseInvalidStack(stack);
                  }

                  ItemStack cosmeticStack = cosmeticStackHandler.getStackInSlot(j);
                  if (!cosmeticStack.isEmpty()) {
                     this.curiosItemHandler.loseInvalidStack(cosmeticStack);
                  }
               }
            }
         }

         sortedCurios.forEach((slotType, stacksHandler) -> curios.put(slotType.getIdentifier(), stacksHandler));
         this.curios.putAll(curios);
         this.deserialized = new CompoundTag();
      }
   }

   public Map<String, ICurioStacksHandler> asMap() {
      return this.curios;
   }

   public void replace(Map<String, ICurioStacksHandler> curios) {
      this.curios.clear();
      this.curios.putAll(curios);
   }

   public CompoundTag serializeNBT(@Nonnull Provider provider) {
      if (!this.deserialized.isEmpty()) {
         return this.deserialized;
      } else {
         CompoundTag compound = new CompoundTag();
         ListTag taglist = new ListTag();
         this.curios.forEach((key, stacksHandler) -> {
            CompoundTag tag = new CompoundTag();
            tag.put("StacksHandler", stacksHandler.serializeNBT());
            tag.putString("Identifier", key);
            taglist.add(tag);
         });
         compound.put("Curios", taglist);
         return compound;
      }
   }

   public void deserializeNBT(@Nonnull Provider provider, @Nonnull CompoundTag nbt) {
      this.deserialized = nbt;
      this.markDeserialized = true;
   }
}
