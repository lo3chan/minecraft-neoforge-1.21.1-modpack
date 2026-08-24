package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.accessor.EntityLinkedItemStack;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EdibleItemPower extends Power implements Prioritized {
   public static final MapCodec<EdibleItemPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(EdibleItemPower::getEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(EdibleItemPower::getItemAction),
            ItemAction.optionalCodec("result_item_action").forGetter(EdibleItemPower::getResultItemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(EdibleItemPower::getItemCondition),
            FoodProperties.DIRECT_CODEC.fieldOf("food_properties").forGetter(EdibleItemPower::getFoodProperties),
            ExtraEnumCodecs.USE_ANIM.optionalFieldOf("consume_animation", UseAnim.EAT).forGetter(EdibleItemPower::getConsumeAnimation),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("consume_sound", SoundEvents.GENERIC_EAT).forGetter(EdibleItemPower::getConsumeSound),
            CombinedCodecs.MODIFIER.optionalFieldOf("consuming_time_modifier", List.of()).forGetter(EdibleItemPower::getConsumingTimeModifier),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(EdibleItemPower::getPriority)
         )
         .apply(i, EdibleItemPower::new)
   );
   private final EntityAction entityAction;
   private final ItemAction itemAction;
   private final ItemAction resultItemAction;
   private final ItemCondition itemCondition;
   private final FoodProperties foodProperties;
   private final UseAnim consumeAnimation;
   private final SoundEvent consumeSound;
   private final List<Modifier> consumingTimeModifier;
   private final int priority;

   public EdibleItemPower(
      Power.BaseSettings settings,
      EntityAction entityAction,
      ItemAction itemAction,
      ItemAction resultItemAction,
      ItemCondition itemCondition,
      FoodProperties foodProperties,
      UseAnim consumeAnimation,
      SoundEvent consumeSound,
      List<Modifier> consumingTimeModifier,
      int priority
   ) {
      super(settings);
      this.entityAction = entityAction;
      this.itemAction = itemAction;
      this.resultItemAction = resultItemAction;
      this.itemCondition = itemCondition;
      this.foodProperties = foodProperties;
      this.consumeAnimation = consumeAnimation;
      this.consumeSound = consumeSound;
      this.consumingTimeModifier = consumingTimeModifier;
      this.priority = priority;
   }

   public EntityAction getEntityAction() {
      return this.entityAction;
   }

   public ItemAction getItemAction() {
      return this.itemAction;
   }

   public ItemAction getResultItemAction() {
      return this.resultItemAction;
   }

   public ItemCondition getItemCondition() {
      return this.itemCondition;
   }

   public FoodProperties getFoodProperties() {
      return this.foodProperties;
   }

   public UseAnim getConsumeAnimation() {
      return this.consumeAnimation;
   }

   public SoundEvent getConsumeSound() {
      return this.consumeSound;
   }

   public List<Modifier> getConsumingTimeModifier() {
      return this.consumingTimeModifier;
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

   public boolean doesApply(Level level, ItemStack stack) {
      return this.itemCondition.test(level, stack);
   }

   public void executeEntityAction(LivingEntity entity) {
      this.entityAction.execute(entity);
   }

   public SlotAccess executeItemActions(LivingEntity entity, SlotAccess consumedStackReference) {
      Level level = entity.level();
      this.itemAction.execute(level, entity, consumedStackReference);
      this.resultItemAction.execute(level, entity, consumedStackReference);
      return consumedStackReference;
   }

   public static Optional<EdibleItemPower> get(ItemStack stack, @Nullable LivingEntity entity) {
      if (entity == null) {
         if (!(EntityLinkedItemStack.getEntity(stack) instanceof LivingEntity le)) {
            return Optional.empty();
         }

         entity = le;
      }

      LivingEntity resolved = entity;
      return PowerHelper.get(resolved)
         .streamActive(EdibleItemPower.class)
         .filter(p -> p.doesApply(resolved.level(), stack))
         .max(Comparator.comparing(EdibleItemPower::getPriority))
         .filter(p -> !stack.has(DataComponents.FOOD) || p.priority > 1);
   }
}
