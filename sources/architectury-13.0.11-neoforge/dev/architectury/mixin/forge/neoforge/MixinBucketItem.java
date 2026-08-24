package dev.architectury.mixin.forge.neoforge;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({BucketItem.class})
public class MixinBucketItem {
   @Inject(
      method = {"use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/phys/BlockHitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;",
         ordinal = 0
      )},
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   public void fillBucket(
      Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack stack, BlockHitResult target
   ) {
      CompoundEventResult<ItemStack> result = PlayerEvent.FILL_BUCKET.invoker().fill(player, level, stack, target);
      if (result.interruptsFurtherEvaluation() && result.value() != null) {
         cir.setReturnValue(result.asMinecraft());
         cir.cancel();
      }
   }
}
