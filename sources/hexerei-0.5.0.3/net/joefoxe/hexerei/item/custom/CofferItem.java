package net.joefoxe.hexerei.item.custom;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CofferItem extends BlockItem {
   public CofferItem(Block block, Properties properties) {
      super(block, properties);
   }

   public static int getColorValue(DyeColor color, ItemStack stack) {
      int dyeCol = getColorStatic(stack);
      return color == null && dyeCol != -1 ? dyeCol : color.getTextureDiffuseColor();
   }

   public static int getColorStatic(ItemStack stack) {
      return ((DyedItemColor)stack.getOrDefault(
            DataComponents.DYED_COLOR, new DyedItemColor(stack.getItem() == ModItems.ENTANGLED_COFFER.get() ? 856599 : 4337438, true)
         ))
         .rgb();
   }

   public static int getDyeColorNamed(String name) {
      if (HexereiUtil.getDyeColorNamed(name) != null) {
         float f3 = ClientEvents.getClientTicks() / 10.0F * 4.0F % 16.0F / 16.0F;
         DyeColor col1 = HexereiUtil.getDyeColorNamed(name, 0);
         DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);
         float[] afloat1 = HexereiUtil.rgbIntToFloatArray(col1.getTextureDiffuseColor());
         float[] afloat2 = HexereiUtil.rgbIntToFloatArray(col2.getTextureDiffuseColor());
         float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
         float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
         float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
         return HexereiUtil.getColorValue(f, f1, f2);
      } else {
         return 0;
      }
   }

   public static DyeColor getDyeColorNamed(ItemStack stack) {
      return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
   }

   public InteractionResult place(BlockPlaceContext context) {
      return context.getPlayer() != null && !context.getPlayer().isCrouching() ? InteractionResult.PASS : super.place(context);
   }

   public ItemStackHandler createHandler() {
      return new ItemStackHandler(36) {
         public int getSlotLimit(int slot) {
            return 64;
         }
      };
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      boolean isCrouching = playerIn.isCrouching();
      playerIn.startUsingItem(handIn);
      if (playerIn instanceof ServerPlayer serverPlayer) {
         if (!isCrouching && itemstack.getCount() == 1) {
            MenuProvider containerProvider = this.createContainerProvider(itemstack, handIn);
            serverPlayer.openMenu(containerProvider, b -> b.writeBoolean(false).writeInt(handIn == InteractionHand.MAIN_HAND ? 0 : 1));
         } else if (itemstack.getCount() != 1) {
            playerIn.displayClientMessage(Component.literal("Cannot open with a stack size more than 1"), true);
         }
      }

      return !isCrouching && itemstack.getCount() == 1 ? InteractionResultHolder.success(itemstack) : InteractionResultHolder.pass(itemstack);
   }

   private MenuProvider createContainerProvider(final ItemStack itemStack, final InteractionHand hand) {
      return new MenuProvider() {
         @Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new CofferContainer(i, itemStack, playerInventory, playerEntity, hand);
         }

         public Component getDisplayName() {
            return Component.translatable("screen.hexerei.coffer");
         }
      };
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
      ItemStackHandler handler = this.createHandler();
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      return tag.contains("CofferId") && FMLEnvironment.dist.isClient()
         ? Optional.of(new CofferItem.CofferItemToolTip(new CofferTile.CofferInvWrapper(tag.getUUID("CofferId"), null), stack))
         : Optional.of(new CofferItem.CofferItemToolTip(handler, stack));
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && !(entity instanceof Player player && player.isCreative()) && stack.getCount() == 1) {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (!tag.contains("CofferId")) {
            tag.putUUID("CofferId", UUID.randomUUID());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         }
      }

      super.inventoryTick(stack, level, entity, slotId, isSelected);
   }

   public record CofferItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
   }

   public interface ItemHandlerConsumer {
      void register(ItemColor var1, ItemLike... var2);
   }
}
