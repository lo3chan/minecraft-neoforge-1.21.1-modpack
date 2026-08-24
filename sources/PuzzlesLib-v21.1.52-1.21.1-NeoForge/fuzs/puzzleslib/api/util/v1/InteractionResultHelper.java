package fuzs.puzzleslib.api.util.v1;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;

@Deprecated
public final class InteractionResultHelper {
   public static final InteractionResult SUCCESS = InteractionResult.SUCCESS;
   public static final InteractionResult CONSUME = InteractionResult.CONSUME;
   public static final InteractionResult PASS = InteractionResult.PASS;
   public static final InteractionResult FAIL = InteractionResult.FAIL;
   public static final ItemInteractionResult PASS_TO_DEFAULT_BLOCK_INTERACTION = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   public static final ItemInteractionResult SKIP_DEFAULT_BLOCK_INTERACTION = ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

   private InteractionResultHelper() {
   }

   public static boolean consumesAction(InteractionResult interactionResult) {
      return interactionResult.consumesAction();
   }

   public static boolean shouldSwing(InteractionResult interactionResult) {
      return interactionResult.shouldSwing();
   }

   public static boolean indicateItemUse(InteractionResult interactionResult) {
      return interactionResult.indicateItemUse();
   }

   public static InteractionResult sidedSuccess(boolean isClientSide) {
      return InteractionResult.sidedSuccess(isClientSide);
   }

   public static ItemStack getObject(InteractionResultHolder<ItemStack> interactionResult) {
      return (ItemStack)interactionResult.getObject();
   }

   public static InteractionResultHolder<ItemStack> success(ItemStack itemStack) {
      return InteractionResultHolder.success(itemStack);
   }

   public static InteractionResultHolder<ItemStack> consume(ItemStack itemStack) {
      return InteractionResultHolder.consume(itemStack);
   }

   public static InteractionResultHolder<ItemStack> pass(ItemStack itemStack) {
      return InteractionResultHolder.pass(itemStack);
   }

   public static InteractionResultHolder<ItemStack> fail(ItemStack itemStack) {
      return InteractionResultHolder.fail(itemStack);
   }

   public static InteractionResultHolder<ItemStack> sidedSuccess(ItemStack itemStack, boolean isClientSide) {
      return InteractionResultHolder.sidedSuccess(itemStack, isClientSide);
   }
}
