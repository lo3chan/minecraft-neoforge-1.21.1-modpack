package fuzs.puzzleslib.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.puzzleslib.impl.content.ItemDataAccessor;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.commands.data.DataCommands.DataProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({DataCommands.class})
abstract class DataCommandsMixin {
   @ModifyExpressionValue(
      method = {"<clinit>"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/server/commands/data/DataCommands;ALL_PROVIDERS:Ljava/util/List;",
         opcode = 178
      )}
   )
   private static List<Function<String, DataProvider>> clinit(List<Function<String, DataProvider>> allProviders) {
      return ImmutableList.builder().addAll(allProviders).add(ItemDataAccessor.PROVIDER).build();
   }
}
