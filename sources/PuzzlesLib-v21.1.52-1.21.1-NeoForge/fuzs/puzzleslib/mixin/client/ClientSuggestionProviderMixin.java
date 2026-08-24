package fuzs.puzzleslib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ClientSuggestionProvider.class})
abstract class ClientSuggestionProviderMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @ModifyReturnValue(
      method = {"getSelectedEntities"},
      at = {@At("RETURN")}
   )
   public Collection<String> getSelectedEntities(Collection<String> entities) {
      return (Collection<String>)(entities.isEmpty() && this.minecraft.level != null && this.minecraft.player != null
         ? this.minecraft
            .level
            .getEntities(this.minecraft.player, this.minecraft.player.getBoundingBox().inflate(5.0), Entity::isPickable)
            .stream()
            .<String>map(Entity::getStringUUID)
            .toList()
         : entities);
   }
}
