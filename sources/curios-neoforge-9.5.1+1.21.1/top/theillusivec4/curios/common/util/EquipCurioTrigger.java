package top.theillusivec4.curios.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotPredicate;

public class EquipCurioTrigger extends SimpleCriterionTrigger<EquipCurioTrigger.TriggerInstance> {
   public static final EquipCurioTrigger INSTANCE = new EquipCurioTrigger();

   @Nonnull
   public Codec<EquipCurioTrigger.TriggerInstance> codec() {
      return EquipCurioTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack stack) {
      LootParams lootparams = new Builder(serverPlayer.serverLevel())
         .withParameter(LootContextParams.ORIGIN, serverPlayer.blockPosition().getCenter())
         .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
         .withParameter(LootContextParams.BLOCK_STATE, serverPlayer.getBlockStateOn())
         .withParameter(LootContextParams.TOOL, stack)
         .create(LootContextParamSets.ADVANCEMENT_LOCATION);
      LootContext lootcontext = new net.minecraft.world.level.storage.loot.LootContext.Builder(lootparams).create(Optional.empty());
      this.trigger(serverPlayer, instance -> instance.matches(null, stack, lootcontext));
   }

   public void trigger(SlotContext slotContext, ServerPlayer serverPlayer, ItemStack stack) {
      LootParams lootparams = new Builder(serverPlayer.serverLevel())
         .withParameter(LootContextParams.ORIGIN, serverPlayer.blockPosition().getCenter())
         .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
         .withParameter(LootContextParams.BLOCK_STATE, serverPlayer.getBlockStateOn())
         .withParameter(LootContextParams.TOOL, stack)
         .create(LootContextParamSets.ADVANCEMENT_LOCATION);
      LootContext lootcontext = new net.minecraft.world.level.storage.loot.LootContext.Builder(lootparams).create(Optional.empty());
      this.trigger(serverPlayer, instance -> instance.matches(slotContext, stack, lootcontext));
   }

   public record TriggerInstance(
      Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<LocationPredicate> location, Optional<SlotPredicate> slot
   ) implements SimpleInstance {
      public static final Codec<EquipCurioTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(EquipCurioTrigger.TriggerInstance::player),
               ItemPredicate.CODEC.optionalFieldOf("item").forGetter(EquipCurioTrigger.TriggerInstance::item),
               LocationPredicate.CODEC.optionalFieldOf("location").forGetter(EquipCurioTrigger.TriggerInstance::location),
               SlotPredicate.CODEC.optionalFieldOf("curios:slot").forGetter(EquipCurioTrigger.TriggerInstance::slot)
            )
            .apply(instance, EquipCurioTrigger.TriggerInstance::new)
      );

      public boolean matches(SlotContext slotContext, ItemStack stack, LootContext lootContext) {
         Vec3 vec3 = (Vec3)lootContext.getParam(LootContextParams.ORIGIN);
         if (slotContext != null && this.slot().map(slotPredicate -> !slotPredicate.matches(slotContext)).orElse(false)) {
            return false;
         } else {
            return !this.location.isEmpty() && !this.location.get().matches(lootContext.getLevel(), vec3.x, vec3.y, vec3.z)
               ? false
               : this.item.isEmpty() || this.item.get().test(stack);
         }
      }
   }
}
