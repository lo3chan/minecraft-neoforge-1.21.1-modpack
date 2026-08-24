package vazkii.psi.common.core.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.spell.detonator.IDetonationHandler;

public record CapabilityTriggerSensor(Player player)
   implements IDetonationHandler,
   ICapabilityProvider<EntityCapability<?, Void>, Void, CapabilityTriggerSensor> {
   public static final String TRIGGER_TICK = "psi:LastTriggeredDetonation";

   @Nullable
   public CapabilityTriggerSensor getCapability(@NotNull EntityCapability<?, Void> capability, @Nullable Void facing) {
      return capability == PsiAPI.DETONATION_HANDLER_CAPABILITY ? this : null;
   }

   @Override
   public void detonate() {
      CompoundTag playerData = this.player.getPersistentData();
      long detonated = playerData.getLong("psi:LastTriggeredDetonation");
      long worldTime = this.player.level().getGameTime();
      if (detonated != worldTime) {
         playerData.putLong("psi:LastTriggeredDetonation", worldTime);
         PsiArmorEvent.post(new PsiArmorEvent(this.player, "psi.event.spell_detonate"));
      }
   }

   @Override
   public Vec3 objectLocus() {
      return this.player.position();
   }
}
