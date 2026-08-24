package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

public final class ItemHelper {
   private ItemHelper() {
   }

   public static void hurtAndBreak(ItemStack itemStack, int amount, LivingEntity livingEntity, InteractionHand interactionHand) {
      hurtAndBreak(itemStack, amount, livingEntity, LivingEntity.getSlotForHand(interactionHand));
   }

   public static void hurtAndBreak(ItemStack itemStack, int amount, LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
      if (livingEntity.level() instanceof ServerLevel serverLevel) {
         ServerPlayer serverPlayer = livingEntity instanceof ServerPlayer ? (ServerPlayer)livingEntity : null;
         hurtAndBreak(itemStack, amount, serverLevel, serverPlayer, item -> livingEntity.onEquippedItemBroken(item, equipmentSlot));
      }
   }

   public static void hurtAndBreak(ItemStack itemStack, int amount, ServerLevel serverLevel, @Nullable ServerPlayer serverPlayer, Consumer<Item> onBreak) {
      ItemStack originalItemStack = copyItemStackIfNecessary(itemStack, serverPlayer);
      itemStack.hurtAndBreak(amount, serverLevel, serverPlayer, item -> {
         onBreak.accept(item);
         if (serverPlayer != null) {
            onPlayerDestroyItem(serverPlayer, originalItemStack, null);
         }
      });
   }

   private static ItemStack copyItemStackIfNecessary(ItemStack itemStack, @Nullable ServerPlayer serverPlayer) {
      return serverPlayer != null && ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike() ? itemStack.copy() : itemStack;
   }

   public static void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand) {
      Objects.requireNonNull(player, "player is null");
      Objects.requireNonNull(originalItemStack, "original item stack is null");
      ProxyImpl.get().onPlayerDestroyItem(player, originalItemStack, interactionHand);
   }

   public static Style getRarityStyle(Rarity rarity) {
      Objects.requireNonNull(rarity, "rarity is null");
      return ProxyImpl.get().getRarityStyle(rarity);
   }

   public Component getStyledHoverName(ItemStack itemStack) {
      MutableComponent hoverName = Component.empty().append(itemStack.getHoverName()).withStyle(getRarityStyle(itemStack.getRarity()));
      return itemStack.has(DataComponents.CUSTOM_NAME) ? hoverName.withStyle(ChatFormatting.ITALIC) : hoverName;
   }
}
