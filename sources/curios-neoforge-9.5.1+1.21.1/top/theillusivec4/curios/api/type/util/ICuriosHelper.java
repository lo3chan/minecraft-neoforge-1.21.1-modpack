package top.theillusivec4.curios.api.type.util;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Deprecated(
   since = "1.20.1",
   forRemoval = true
)
@ScheduledForRemoval(
   inVersion = "1.22"
)
public interface ICuriosHelper {
   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Optional<ICurio> getCurio(ItemStack var1);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Optional<ICuriosItemHandler> getCuriosHandler(LivingEntity var1);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Set<String> getCurioTags(Item var1);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Optional<IItemHandlerModifiable> getEquippedCurios(LivingEntity var1);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   void setEquippedCurio(@Nonnull LivingEntity var1, String var2, int var3, ItemStack var4);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Optional<SlotResult> findFirstCurio(@Nonnull LivingEntity var1, Item var2);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Optional<SlotResult> findFirstCurio(@Nonnull LivingEntity var1, Predicate<ItemStack> var2);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   List<SlotResult> findCurios(@Nonnull LivingEntity var1, Item var2);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   List<SlotResult> findCurios(@Nonnull LivingEntity var1, Predicate<ItemStack> var2);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   List<SlotResult> findCurios(@Nonnull LivingEntity var1, String... var2);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   Optional<SlotResult> findCurio(@Nonnull LivingEntity var1, String var2, int var3);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   boolean isStackValid(SlotContext var1, ItemStack var2);

   @Nonnull
   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   Optional<ImmutableTriple<String, Integer, ItemStack>> findEquippedCurio(Item var1, @Nonnull LivingEntity var2);

   @Nonnull
   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   Optional<ImmutableTriple<String, Integer, ItemStack>> findEquippedCurio(Predicate<ItemStack> var1, @Nonnull LivingEntity var2);

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   void onBrokenCurio(String var1, int var2, LivingEntity var3);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   void onBrokenCurio(SlotContext var1);

   @Deprecated(
      since = "1.20.1",
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   void setBrokenCurioConsumer(Consumer<SlotContext> var1);

   @Deprecated(
      forRemoval = true
   )
   @ScheduledForRemoval(
      inVersion = "1.21"
   )
   void setBrokenCurioConsumer(TriConsumer<String, Integer, LivingEntity> var1);
}
