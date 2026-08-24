package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS("kjs$")
@Mixin({Block.class})
public abstract class BlockMixin implements BlockKJS {
   @Unique
   private String kjs$id;
   @Unique
   private BlockBuilder kjs$blockBuilder;
   @Unique
   private Map<String, Object> kjs$typeData;

   @Override
   public String kjs$getId() {
      if (this.kjs$id == null) {
         this.kjs$id = this.kjs$getBlock().builtInRegistryHolder().key().location().toString();
      }

      return this.kjs$id;
   }

   @Nullable
   @Override
   public BlockBuilder kjs$getBlockBuilder() {
      return this.kjs$blockBuilder;
   }

   @Override
   public void kjs$setBlockBuilder(BlockBuilder b) {
      this.kjs$blockBuilder = b;
   }

   @Override
   public Map<String, Object> kjs$getTypeData() {
      if (this.kjs$typeData == null) {
         this.kjs$typeData = new HashMap<>();
      }

      return this.kjs$typeData;
   }

   @Accessor("descriptionId")
   @Mutable
   @Override
   public abstract void kjs$setNameKey(String key);

   @Inject(
      method = {"getName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void kjs$getName(CallbackInfoReturnable<MutableComponent> cir) {
      if (this.kjs$blockBuilder != null && this.kjs$blockBuilder.displayName != null && this.kjs$blockBuilder.formattedDisplayName) {
         cir.setReturnValue(Component.literal("").append(this.kjs$blockBuilder.displayName));
      }
   }
}
