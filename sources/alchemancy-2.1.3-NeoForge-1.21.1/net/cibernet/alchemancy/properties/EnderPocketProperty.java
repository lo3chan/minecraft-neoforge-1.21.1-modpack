package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class EnderPocketProperty extends Property {
   private static final Component CONTAINER_TITLE = Component.translatable("container.enderchest");

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      openMenu(user);
      return ItemInteractionResult.SUCCESS;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled()) {
         openMenu(event.getEntity());
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.2F, 4940892, 4940892, 4940892, 7649131, 13369594);
   }

   public static void openMenu(Player player) {
      player.openMenu(getMenuProvider());
   }

   protected static MenuProvider getMenuProvider() {
      return new SimpleMenuProvider(
         (containerId, playerInventory, player) -> ChestMenu.threeRows(containerId, playerInventory, player.getEnderChestInventory()), CONTAINER_TITLE
      );
   }
}
