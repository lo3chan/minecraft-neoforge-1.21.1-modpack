package net.mehvahdjukaar.moonlight.core.mixins;

import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacement;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.misc.IExtendedItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Item.class})
public abstract class ItemMixin implements IExtendedItem {
   @Unique
   @Nullable
   private AdditionalItemPlacement moonlight$additionalBehavior;
   @Unique
   @OnlyIn(Dist.CLIENT)
   @Nullable
   Object moonlight$clientAnimationProvider;

   @Inject(
      method = {"useOn"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onUseOnBlock(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
      AdditionalItemPlacement behavior = this.moonlight$getAdditionalBehavior();
      if (behavior != null) {
         InteractionResult result = behavior.overrideUseOn(pContext, PlatHelper.getFoodProperties(pContext.getItemInHand(), pContext.getPlayer()));
         if (result.consumesAction()) {
            cir.setReturnValue(result);
         }
      }
   }

   @Nullable
   @Override
   public AdditionalItemPlacement moonlight$getAdditionalBehavior() {
      return this.moonlight$additionalBehavior;
   }

   @Override
   public void moonlight$setAdditionalBehavior(AdditionalItemPlacement placementOverride) {
      this.moonlight$additionalBehavior = placementOverride;
   }

   @Nullable
   @Override
   public Object moonlight$getClientAnimationExtension() {
      return this.moonlight$clientAnimationProvider;
   }

   @Override
   public void moonlight$setClientAnimationExtension(Object obj) {
      this.moonlight$clientAnimationProvider = obj;
   }
}
