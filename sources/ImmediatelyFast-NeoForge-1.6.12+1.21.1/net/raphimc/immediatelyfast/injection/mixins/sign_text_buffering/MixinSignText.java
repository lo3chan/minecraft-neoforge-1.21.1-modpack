package net.raphimc.immediatelyfast.injection.mixins.sign_text_buffering;

import java.util.Arrays;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.raphimc.immediatelyfast.injection.interfaces.ISignText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SignText.class})
public abstract class MixinSignText implements ISignText {
   @Shadow
   @Final
   private Component[] messages;
   @Shadow
   @Final
   private Component[] filteredMessages;
   @Shadow
   @Final
   private DyeColor color;
   @Shadow
   @Final
   private boolean hasGlowingText;
   @Shadow
   @Nullable
   private FormattedCharSequence[] renderMessages;
   @Unique
   private boolean immediatelyFast$shouldCache;
   @Unique
   private boolean immediatelyFast$checkedShouldCache;
   @Unique
   private int immediatelyFast$cachedHashCode;
   @Unique
   private boolean immediatelyFast$calculatedHashCode;

   @Inject(
      method = {"getRenderMessages"},
      at = {@At("RETURN")}
   )
   private void checkShouldCache(CallbackInfoReturnable<FormattedCharSequence[]> cir) {
      if (!this.immediatelyFast$checkedShouldCache) {
         this.immediatelyFast$checkedShouldCache = true;
         this.immediatelyFast$shouldCache = true;

         for (FormattedCharSequence orderedText : this.renderMessages) {
            if (!this.immediatelyFast$shouldCache) {
               break;
            }

            orderedText.accept((index, style, codePoint) -> {
               if (style.isObfuscated()) {
                  this.immediatelyFast$shouldCache = false;
                  return false;
               } else {
                  return true;
               }
            });
         }
      }
   }

   @Inject(
      method = {"getRenderMessages"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/world/level/block/entity/SignText;renderMessages:[Lnet/minecraft/util/FormattedCharSequence;",
         opcode = 181
      )}
   )
   private void invalidateCache(CallbackInfoReturnable<FormattedCharSequence[]> cir) {
      this.immediatelyFast$shouldCache = false;
      this.immediatelyFast$checkedShouldCache = false;
      this.immediatelyFast$cachedHashCode = 0;
      this.immediatelyFast$calculatedHashCode = false;
   }

   @Override
   public boolean immediatelyFast$shouldCache() {
      return this.immediatelyFast$shouldCache;
   }

   @Override
   public void immediatelyFast$setShouldCache(boolean shouldCache) {
      this.immediatelyFast$shouldCache = shouldCache;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MixinSignText that = (MixinSignText)o;
         return this.hasGlowingText == that.hasGlowingText
            && this.color == that.color
            && Arrays.equals((Object[])this.messages, (Object[])that.messages)
            && Arrays.equals((Object[])this.filteredMessages, (Object[])that.filteredMessages);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      if (!this.immediatelyFast$calculatedHashCode) {
         this.immediatelyFast$calculatedHashCode = true;
         int result = Objects.hash(this.color, this.hasGlowingText);
         result = 31 * result + Arrays.hashCode((Object[])this.messages);
         result = 31 * result + Arrays.hashCode((Object[])this.filteredMessages);
         this.immediatelyFast$cachedHashCode = result;
      }

      return this.immediatelyFast$cachedHashCode;
   }
}
