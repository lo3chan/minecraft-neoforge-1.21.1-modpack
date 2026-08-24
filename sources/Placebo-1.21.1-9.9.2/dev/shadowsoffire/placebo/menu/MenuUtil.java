package dev.shadowsoffire.placebo.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.neoforged.neoforge.network.IContainerFactory;

public class MenuUtil {
   public static <T extends AbstractContainerMenu> MenuType<T> type(MenuSupplier<T> factory) {
      return new MenuType(factory, FeatureFlags.DEFAULT_FLAGS);
   }

   public static <T extends AbstractContainerMenu> MenuType<T> bufType(IContainerFactory<T> factory) {
      return new MenuType(factory, FeatureFlags.DEFAULT_FLAGS);
   }

   public static <T extends AbstractContainerMenu> MenuType<T> posType(MenuUtil.PosFactory<T> factory) {
      return new MenuType(factory, FeatureFlags.DEFAULT_FLAGS);
   }

   public static <M extends AbstractContainerMenu> InteractionResult openGui(Player player, BlockPos pos, MenuUtil.PosFactory<M> factory) {
      if (player.level().isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         player.openMenu(new SimplerMenuProvider<>(player.level(), pos, factory), pos);
         return InteractionResult.CONSUME;
      }
   }

   @FunctionalInterface
   public interface PosFactory<T extends AbstractContainerMenu> extends IContainerFactory<T> {
      T create(int var1, Inventory var2, BlockPos var3);

      default T create(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
         return this.create(id, inv, buf.readBlockPos());
      }
   }
}
