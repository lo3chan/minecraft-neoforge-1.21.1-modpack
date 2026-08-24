package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class AnvilEvents {
   public static final EventInvoker<AnvilEvents.Update> UPDATE = EventInvoker.lookup(AnvilEvents.Update.class);
   public static final EventInvoker<AnvilEvents.Use> USE = EventInvoker.lookup(AnvilEvents.Use.class);

   private AnvilEvents() {
   }

   @FunctionalInterface
   public interface Update {
      EventResult onAnvilUpdate(
         ItemStack var1, ItemStack var2, MutableValue<ItemStack> var3, @Nullable String var4, MutableInt var5, MutableInt var6, Player var7
      );
   }

   @FunctionalInterface
   public interface Use {
      void onAnvilUse(Player var1, ItemStack var2, ItemStack var3, ItemStack var4, MutableFloat var5);
   }
}
