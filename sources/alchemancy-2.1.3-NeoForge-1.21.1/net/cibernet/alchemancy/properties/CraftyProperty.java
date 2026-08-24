package net.cibernet.alchemancy.properties;

import java.util.Optional;
import java.util.function.BiFunction;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftyProperty extends Property {
   private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      openCraftingMenu(user, root.getItem());
      return ItemInteractionResult.SUCCESS;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled()) {
         openCraftingMenu(event.getEntity(), event.getItemStack());
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 11430204;
   }

   public static void openCraftingMenu(Player player, ItemStack sourceItem) {
      player.openMenu(getMenuProvider(sourceItem));
   }

   protected static MenuProvider getMenuProvider(ItemStack sourceItem) {
      return new SimpleMenuProvider(
         (containerId, playerInventory, player) -> new CraftingMenu(containerId, playerInventory, new CraftyProperty.PlayerContainerLevelAccess(player)) {
            public boolean stillValid(Player player) {
               return player.getInventory().getSelected().equals(sourceItem);
            }
         }, CONTAINER_TITLE
      );
   }

   public record PlayerContainerLevelAccess(Player player) implements ContainerLevelAccess {
      @NotNull
      public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer) {
         return Optional.ofNullable(levelPosConsumer.apply(this.player.level(), this.player.blockPosition()));
      }
   }
}
