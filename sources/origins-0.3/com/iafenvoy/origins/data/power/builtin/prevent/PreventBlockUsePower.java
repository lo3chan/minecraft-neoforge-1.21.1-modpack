package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import org.jetbrains.annotations.NotNull;

public class PreventBlockUsePower extends Power {
   public static final MapCodec<PreventBlockUsePower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.CODEC.fieldOf("block_condition").forGetter(PreventBlockUsePower::getBlockCondition)
         )
         .apply(instance, PreventBlockUsePower::new)
   );
   private final BlockCondition blockCondition;

   protected PreventBlockUsePower(Power.BaseSettings settings, BlockCondition blockCondition) {
      super(settings);
      this.blockCondition = blockCondition;
   }

   public BlockCondition getBlockCondition() {
      return this.blockCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void preventBlockInteraction(RightClickBlock event) {
      Entity entity = event.getEntity();
      if (PowerHelper.get(entity).anyActive(PreventBlockUsePower.class, x -> x.blockCondition.test(entity.level(), event.getPos()))) {
         event.setUseBlock(TriState.FALSE);
      }
   }
}
