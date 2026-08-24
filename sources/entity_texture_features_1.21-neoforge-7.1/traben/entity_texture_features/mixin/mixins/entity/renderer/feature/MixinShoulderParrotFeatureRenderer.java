package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({ParrotOnShoulderLayer.class})
public abstract class MixinShoulderParrotFeatureRenderer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
   @Unique
   private ETFEntityRenderState etf$heldEntity = null;

   public MixinShoulderParrotFeatureRenderer(RenderLayerParent<T, PlayerModel<T>> context) {
      super(context);
   }

   @ModifyArg(
      method = {"Lnet/minecraft/client/renderer/entity/layers/ParrotOnShoulderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"
      )
   )
   private Consumer<EntityType<?>> etf$alterEntity(Consumer<EntityType<?>> action, @Local(argsOnly = true) T t, @Local CompoundTag nbtCompound) {
      return v -> {
         this.etf$HEADalterEntity(t, nbtCompound);
         action.accept(v);
         this.etf$TAILresetEntity();
      };
   }

   @Unique
   private void etf$HEADalterEntity(T playerEntity, CompoundTag nbtCompound) {
      if (nbtCompound != null) {
         this.etf$heldEntity = ETFRenderContext.getCurrentEntityState();
         Optional<Entity> optionalEntity = EntityType.create(nbtCompound, playerEntity.level());
         if (optionalEntity.isPresent() && optionalEntity.get() instanceof Parrot parrot) {
            ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity)parrot));
         }
      }
   }

   @Unique
   private void etf$TAILresetEntity() {
      if (this.etf$heldEntity != null) {
         ETFRenderContext.setCurrentEntity(this.etf$heldEntity);
      }

      this.etf$heldEntity = null;
   }
}
