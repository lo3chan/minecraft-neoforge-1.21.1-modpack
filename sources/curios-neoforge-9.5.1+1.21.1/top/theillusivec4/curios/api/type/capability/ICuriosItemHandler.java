package top.theillusivec4.curios.api.type.capability;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public interface ICuriosItemHandler {
   Logger LOGGER = LogUtils.getLogger();

   Map<String, ICurioStacksHandler> getCurios();

   void setCurios(Map<String, ICurioStacksHandler> var1);

   int getSlots();

   default int getVisibleSlots() {
      return this.getSlots();
   }

   void reset();

   Optional<ICurioStacksHandler> getStacksHandler(String var1);

   IItemHandlerModifiable getEquippedCurios();

   void setEquippedCurio(String var1, int var2, ItemStack var3);

   default boolean isEquipped(Item item) {
      return this.findFirstCurio(item).isPresent();
   }

   default boolean isEquipped(Predicate<ItemStack> filter) {
      return this.findFirstCurio(filter).isPresent();
   }

   default boolean isSlotActive(String identifier, int index) {
      return this.getStacksHandler(identifier).map(stackHandler -> {
         NonNullList<Boolean> activeStates = stackHandler.getActiveStates();
         return index < activeStates.size() ? (Boolean)activeStates.get(index) : true;
      }).orElse(true);
   }

   default void setSlotActive(String identifier, int index, boolean active) {
      this.getStacksHandler(identifier).ifPresent(stackHandler -> {
         NonNullList<Boolean> activeStates = stackHandler.getActiveStates();
         if (index < activeStates.size()) {
            activeStates.set(index, active);
         }
      });
   }

   default void setSlotsActive(String identifier, boolean active) {
      this.getStacksHandler(identifier).ifPresent(stackHandler -> {
         NonNullList<Boolean> activeStates = stackHandler.getActiveStates();
         activeStates.replaceAll(ignored -> active);
      });
   }

   Optional<SlotResult> findFirstCurio(Item var1);

   Optional<SlotResult> findFirstCurio(Predicate<ItemStack> var1);

   Optional<SlotResult> findFirstCurio(Predicate<ItemStack> var1, String var2);

   default Optional<SlotResult> findFirstCurio(Predicate<ItemStack> filter, boolean includeInactive, String cacheKey) {
      return this.findFirstCurio(filter, cacheKey);
   }

   List<SlotResult> findCurios(Item var1);

   List<SlotResult> findCurios(Predicate<ItemStack> var1);

   default List<SlotResult> findCurios(Predicate<ItemStack> filter, boolean includeInactive, String cacheKey) {
      return this.findCurios(filter);
   }

   List<SlotResult> findCurios(String... var1);

   default List<SlotResult> findCurios(boolean includeInactive, String... identifiers) {
      return this.findCurios(identifiers);
   }

   Optional<SlotResult> findCurio(String var1, int var2);

   default Optional<SlotResult> findCurio(String identifier, int index, boolean includeInactive) {
      return this.findCurio(identifier, index);
   }

   LivingEntity getWearer();

   void loseInvalidStack(ItemStack var1);

   void handleInvalidStacks();

   int getFortuneLevel(@Nullable LootContext var1);

   int getLootingLevel(@Nullable LootContext var1);

   ListTag saveInventory(boolean var1);

   void loadInventory(ListTag var1);

   Set<ICurioStacksHandler> getUpdatingInventories();

   default void addTransientSlotModifier(String slot, ResourceLocation id, double amount, Operation operation) {
      LOGGER.error("Missing method implementation!");
   }

   void addTransientSlotModifiers(Multimap<String, AttributeModifier> var1);

   default void addPermanentSlotModifier(String slot, ResourceLocation id, double amount, Operation operation) {
      LOGGER.error("Missing method implementation!");
   }

   void addPermanentSlotModifiers(Multimap<String, AttributeModifier> var1);

   default void removeSlotModifier(String slot, ResourceLocation id) {
      LOGGER.error("Missing method implementation!");
   }

   void removeSlotModifiers(Multimap<String, AttributeModifier> var1);

   void clearSlotModifiers();

   Multimap<String, AttributeModifier> getModifiers();

   Tag writeTag();

   void readTag(Tag var1);

   void clearCachedSlotModifiers();

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   default Set<String> getLockedSlots() {
      return new HashSet<>();
   }

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   default void unlockSlotType(String identifier, int amount, boolean visible, boolean cosmetic) {
      this.growSlotType(identifier, amount);
   }

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   default void lockSlotType(String identifier) {
      this.shrinkSlotType(identifier, 1);
   }

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   default void processSlots() {
   }

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   default int getFortuneBonus() {
      return 0;
   }

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   void growSlotType(String var1, int var2);

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   void shrinkSlotType(String var1, int var2);
}
