package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.cibernet.alchemancy.AlchemancyConfig;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ImbuedProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.ResizedProperty;
import net.cibernet.alchemancy.properties.RustyProperty;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class ItemStackMixin {
   @Unique
   private static List<Holder<Property>> RANDOMIZER_POOL = null;

   @Shadow
   public abstract int getDamageValue();

   @Shadow
   public abstract void setPopTime(int var1);

   @Unique
   public ItemStack alchemancy$self() {
      return (ItemStack)this;
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V"},
      at = {@At("RETURN")}
   )
   public void randomizeInfusions(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null
         && server.overworld() != null
         && !server.overworld().isClientSide()
         && AlchemancyConfig.Server.randomizedInfusions()
         && AlchemancyItems.Components.INFUSED_PROPERTIES.isBound()
         && AlchemancyProperties.EXPLODING.isBound()
         && !components.has((DataComponentType)AlchemancyItems.Components.INFUSED_PROPERTIES.get())
         && !components.has((DataComponentType)AlchemancyItems.Components.RANDOMIZED.get())) {
         if (RANDOMIZER_POOL == null) {
            RANDOMIZER_POOL = new ArrayList<>(AlchemancyProperties.getAllAsHolders());
            RANDOMIZER_POOL.removeIf(propertyx -> propertyx.is(AlchemancyTags.Properties.EXCLUDED_FROM_RANDOMIZER));
         }

         long seed = server.overworld().getSeed() + item.asItem().toString().hashCode();
         Random random = new Random(seed);
         int slots = InfusedPropertiesHelper.getInfusionSlots(this.alchemancy$self());

         for (int i = 0; i < slots && random.nextInt(i + 1) == 0; i++) {
            Holder<Property> property = RANDOMIZER_POOL.get(random.nextInt(RANDOMIZER_POOL.size()));
            InfusedPropertiesHelper.addProperty(this.alchemancy$self(), property);
            if (property.equals(AlchemancyProperties.RESIZED)) {
               ((ResizedProperty)AlchemancyProperties.RESIZED.get()).setData(this.alchemancy$self(), random.nextInt(5, 21) * 0.1F);
            } else if (property.equals(AlchemancyProperties.IMBUED)) {
               Optional<Reference<Potion>> potion = BuiltInRegistries.POTION.getRandom(RandomSource.create(seed));
               if (potion.isPresent() && !((Potion)potion.get().value()).getEffects().isEmpty()) {
                  ((ImbuedProperty)AlchemancyProperties.IMBUED.get())
                     .setData(this.alchemancy$self(), (MobEffectInstance)((Potion)potion.get().value()).getEffects().getFirst());
               }
            }
         }

         components.set((DataComponentType)AlchemancyItems.Components.RANDOMIZED.get(), Unit.INSTANCE);
      }
   }

   @Inject(
      at = {@At(
         value = "CONSTANT",
         shift = Shift.BEFORE,
         args = {"classValue=net/minecraft/server/level/ServerPlayer"},
         ordinal = 0
      )},
      method = {"hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"}
   )
   public void hurtAndBreak(
      int damageAmount,
      ServerLevel level,
      LivingEntity user,
      Consumer<Item> onBreak,
      CallbackInfo ci,
      @Local(ordinal = 0,argsOnly = true) LocalIntRef newDamageAmount
   ) {
      ItemStack stack = this.alchemancy$self();
      int originalResult = newDamageAmount.get();
      InfusedPropertiesHelper.forEachProperty(
         stack,
         propertyHolder -> newDamageAmount.set(
            ((Property)propertyHolder.value()).modifyDurabilityConsumed(stack, level, user, originalResult, newDamageAmount.get())
         )
      );
   }

   @ModifyReturnValue(
      method = {"is(Lnet/minecraft/tags/TagKey;)Z"},
      at = {@At("RETURN")}
   )
   public boolean isInTag(boolean original, @Local(argsOnly = true) TagKey<Item> tagKey) {
      ItemStack stack = this.alchemancy$self();

      for (Holder<Property> property : InfusedPropertiesHelper.getInfusedProperties(stack)) {
         if (tagKey.equals(AlchemancyTags.Items.IS_INFUSED)) {
            return true;
         }

         TriState result = ((Property)property.value()).isItemInTag(stack, tagKey);
         if (!result.isDefault()) {
            return result.isTrue();
         }
      }

      return original;
   }

   @ModifyReturnValue(
      method = {"is(Lnet/minecraft/core/HolderSet;)Z"},
      at = {@At("RETURN")}
   )
   public boolean isInTag(boolean original, @Local(argsOnly = true) HolderSet<Item> holder) {
      Optional<TagKey<Item>> optionalTagKey = holder.unwrapKey();
      if (optionalTagKey.isEmpty()) {
         return original;
      } else {
         ItemStack stack = this.alchemancy$self();
         TagKey<Item> tagKey = optionalTagKey.get();

         for (Holder<Property> property : InfusedPropertiesHelper.getInfusedProperties(stack)) {
            if (tagKey.equals(AlchemancyTags.Items.IS_INFUSED)) {
               return true;
            }

            TriState result = ((Property)property.value()).isItemInTag(stack, tagKey);
            if (!result.isDefault()) {
               return result.isTrue();
            }
         }

         return original;
      }
   }

   @ModifyReturnValue(
      method = {"getTags"},
      at = {@At("RETURN")}
   )
   public Stream<TagKey<Item>> getTags(Stream<TagKey<Item>> original) {
      return !InfusedPropertiesHelper.getInfusedProperties(this.alchemancy$self()).isEmpty()
         ? Stream.concat(original, Stream.of(AlchemancyTags.Items.IS_INFUSED))
         : original;
   }

   @Inject(
      method = {"finishUsingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void finishUsingItem(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
      ItemStack stack = this.alchemancy$self();
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
         if (((Property)propertyHolder.value()).onFinishUsingItem(livingEntity, level, stack)) {
            cir.setReturnValue(stack);
         }
      });
      if (stack.getFoodProperties(livingEntity) != null) {
         Property.activateByEntity(livingEntity, livingEntity, stack);
      }
   }

   @WrapOperation(
      method = {"getDestroySpeed"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;getDestroySpeed(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)F"
      )}
   )
   public float getDestroySpeed(Item instance, ItemStack stack, BlockState state, Operation<Float> original) {
      Tool tool = (Tool)stack.get(DataComponents.TOOL);
      return (Float)original.call(new Object[]{instance, stack, state})
         * (tool != null && tool.isCorrectForDrops(state) ? ((RustyProperty)AlchemancyProperties.RUSTY.get()).getMiningSpeedMultiplier(stack) : 1.0F);
   }

   @Inject(
      method = {"getUseDuration"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getUseDuration(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
      int original = (Integer)cir.getReturnValue();
      AtomicInteger result = new AtomicInteger(original);
      ItemStack stack = this.alchemancy$self();
      InfusedPropertiesHelper.forEachProperty(
         stack, propertyHolder -> result.set(((Property)propertyHolder.value()).modifyUseDuration(stack, original, result.get()))
      );
      cir.setReturnValue(result.get());
   }

   @Inject(
      method = {"getUseAnimation"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
      AtomicReference<Optional<UseAnim>> useAnim = new AtomicReference<>(Optional.empty());
      ItemStack stack = this.alchemancy$self();
      InfusedPropertiesHelper.forEachProperty(
         stack, propertyHolder -> useAnim.set(((Property)propertyHolder.value()).modifyUseAnimation(stack, (UseAnim)cir.getReturnValue(), useAnim.get()))
      );
      if (useAnim.get().isPresent()) {
         cir.setReturnValue(useAnim.get().get());
      }
   }
}
