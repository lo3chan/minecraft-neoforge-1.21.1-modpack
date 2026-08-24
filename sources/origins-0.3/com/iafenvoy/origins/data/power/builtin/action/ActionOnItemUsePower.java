package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnItemUsePower extends Power implements Prioritized {
   public static final MapCodec<ActionOnItemUsePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnItemUsePower::getItemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnItemUsePower::getEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnItemUsePower::getItemAction),
            ActionOnItemUsePower.Trigger.CODEC.optionalFieldOf("trigger", ActionOnItemUsePower.Trigger.FINISH).forGetter(ActionOnItemUsePower::getTrigger),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnItemUsePower::getPriority)
         )
         .apply(i, ActionOnItemUsePower::new)
   );
   private final ItemCondition itemCondition;
   private final EntityAction entityAction;
   private final ItemAction itemAction;
   private final ActionOnItemUsePower.Trigger trigger;
   private final int priority;

   public ActionOnItemUsePower(
      Power.BaseSettings settings,
      ItemCondition itemCondition,
      EntityAction entityAction,
      ItemAction itemAction,
      ActionOnItemUsePower.Trigger trigger,
      int priority
   ) {
      super(settings);
      this.itemCondition = itemCondition;
      this.entityAction = entityAction;
      this.itemAction = itemAction;
      this.trigger = trigger;
      this.priority = priority;
   }

   public ItemCondition getItemCondition() {
      return this.itemCondition;
   }

   public EntityAction getEntityAction() {
      return this.entityAction;
   }

   public ItemAction getItemAction() {
      return this.itemAction;
   }

   public ActionOnItemUsePower.Trigger getTrigger() {
      return this.trigger;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onItemUse(LivingEntityUseItemEvent event) {
      PowerHelper.get(event.getEntity()).execute(ActionOnItemUsePower.class, p -> p.trigger.testEvent(event), (h, p) -> {
         ItemStack stack = event.getItem();
         LivingEntity entity = event.getEntity();
         if (p.itemCondition.test(entity.level(), stack)) {
            p.entityAction.execute(entity);
            p.itemAction.execute(entity.level(), entity, event.getItem());
         }
      });
   }

   public static enum Trigger implements StringRepresentable {
      INSTANT(e -> e instanceof Start start && start.getDuration() == 0),
      START(e -> e instanceof Start start && start.getDuration() > 0),
      DURING(e -> e instanceof Tick),
      STOP(e -> e instanceof Stop),
      FINISH(e -> e instanceof Finish);

      public static final Codec<ActionOnItemUsePower.Trigger> CODEC = StringRepresentable.fromValues(ActionOnItemUsePower.Trigger::values);
      private final Predicate<LivingEntityUseItemEvent> eventPredicate;

      private Trigger(Predicate<LivingEntityUseItemEvent> eventPredicate) {
         this.eventPredicate = eventPredicate;
      }

      public boolean testEvent(LivingEntityUseItemEvent event) {
         return this.eventPredicate.test(event);
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
