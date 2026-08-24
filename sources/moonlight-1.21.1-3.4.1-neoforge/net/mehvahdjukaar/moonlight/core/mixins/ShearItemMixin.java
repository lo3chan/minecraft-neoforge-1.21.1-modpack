package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.MoonlightTags;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({ShearsItem.class})
public class ShearItemMixin {
   @ModifyArg(
      method = {"createToolProperties"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/component/Tool;<init>(Ljava/util/List;FI)V"
      )
   )
   private static List<Rule> ml$addShearableTag(List<Rule> rules) {
      List<Rule> list = new ArrayList<>(rules);
      list.add(Rule.minesAndDrops(MoonlightTags.SHEARABLE_TAG, 2.0F));
      return list;
   }

   @ModifyReturnValue(
      method = {"mineBlock"},
      at = {@At("RETURN")}
   )
   public boolean m$mineBlock(boolean original, @Local(argsOnly = true) BlockState state) {
      return !original && state.is(MoonlightTags.SHEARABLE_TAG) ? true : original;
   }
}
