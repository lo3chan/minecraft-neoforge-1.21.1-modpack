package net.cibernet.alchemancy.properties;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StonecuttingProperty extends Property {
   private static final Component CONTAINER_TITLE = Component.translatable("container.stonecutter");

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      openStonecuttingMenu(user, root.getItem());
      return ItemInteractionResult.SUCCESS;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled()) {
         openStonecuttingMenu(event.getEntity(), event.getItemStack());
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      Player player = event.getPlayer();
      if (!event.getLevel().isClientSide()
         && !event.isCanceled()
         && (player == null || !player.isShiftKeyDown())
         && InfusedPropertiesHelper.hasProperty(event.getItemStack(), AlchemancyProperties.INTERACTABLE)) {
         Level level = event.getLevel();
         BlockPos blockPos = event.getPos();
         BlockState blockState = level.getBlockState(blockPos);
         List<RecipeHolder<StonecutterRecipe>> recipes = level.getRecipeManager()
            .getRecipesFor(RecipeType.STONECUTTING, new SingleRecipeInput(blockState.getBlock().asItem().getDefaultInstance()), level);
         Collections.shuffle(recipes);

         for (; !recipes.isEmpty(); recipes.removeFirst()) {
            ItemStack item = ((StonecutterRecipe)((RecipeHolder)recipes.getFirst()).value()).getResultItem(level.registryAccess());
            if (item.getItem() instanceof BlockItem blockItem) {
               BlockState resultState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(event.getUseOnContext()));
               if (resultState != null) {
                  level.destroyBlock(blockPos, false, null);
                  level.setBlock(blockPos, resultState, 3);
                  this.damageItem(event.getLevel(), player, event.getItemStack(), LivingEntity.getSlotForHand(event.getHand()), 1);
                  Vec3 center = blockPos.getCenter();
                  level.playSound(null, center.x, center.y, center.z, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  event.setCanceled(true);
                  event.setCancellationResult(ItemInteractionResult.SUCCESS);
                  return;
               }
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.2F, 11053224, 14211288);
   }

   public static void openStonecuttingMenu(Player player, ItemStack sourceItem) {
      player.openMenu(getMenuProvider(sourceItem));
   }

   protected static MenuProvider getMenuProvider(ItemStack sourceItem) {
      return new SimpleMenuProvider(
         (containerId, playerInventory, player) -> new StonecutterMenu(
            containerId, playerInventory, new StonecuttingProperty.PlayerContainerLevelAccess(player)
         ) {
            public boolean stillValid(Player player) {
               return player.getInventory().getSelected().equals(sourceItem);
            }
         },
         CONTAINER_TITLE
      );
   }

   public record PlayerContainerLevelAccess(Player player) implements ContainerLevelAccess {
      @NotNull
      public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer) {
         return Optional.ofNullable(levelPosConsumer.apply(this.player.level(), this.player.blockPosition()));
      }
   }
}
