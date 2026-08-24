package dev.isxander.yacl3.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractSelectionList.Entry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractSelectionList.class})
public abstract class AbstractSelectionListMixin<E extends Entry<E>> {
   @Shadow
   public abstract List<E> children();

   @WrapOperation(
      method = {"nextEntry(Lnet/minecraft/client/gui/navigation/ScreenDirection;Ljava/util/function/Predicate;Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;children:Ljava/util/List;",
         opcode = 180
      )}
   )
   private List<E> modifyChildrenCall(AbstractSelectionList<E> instance, Operation<List<E>> original) {
      return this.children();
   }
}
