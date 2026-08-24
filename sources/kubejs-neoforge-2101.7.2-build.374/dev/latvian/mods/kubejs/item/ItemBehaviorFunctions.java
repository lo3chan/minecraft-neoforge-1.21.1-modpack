package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;

@ReturnsSelf
public interface ItemBehaviorFunctions {
   @HideFromJS
   ItemBehavior kjs$getOrCreateBehavior();

   @HideFromJS
   default ItemBehaviorFunctions displayName(Component component, boolean formattedDisplayName) {
      ItemBehavior behavior = this.kjs$getOrCreateBehavior();
      behavior.displayName = component;
      behavior.formattedDisplayName = formattedDisplayName;
      return this;
   }

   @Info("Makes the item glow like enchanted, even if it's not enchanted.")
   default ItemBehaviorFunctions glow(boolean glow) {
      this.kjs$getOrCreateBehavior().glow = glow;
      return this;
   }

   @Info("Adds a tooltip to the item.")
   default ItemBehaviorFunctions tooltip(Component component) {
      this.kjs$getOrCreateBehavior().tooltip.add(component);
      return this;
   }

   @Info("Determines the color of the item's durability bar. Defaulted to vanilla behavior.")
   default ItemBehaviorFunctions barColor(Function<ItemStack, KubeColor> barColor) {
      this.kjs$getOrCreateBehavior().barColor = barColor;
      return this;
   }

   @Info("Determines the width of the item's durability bar. Defaulted to vanilla behavior.\n\nThe function should return a value between 0 and 13 (max width of the bar).\n")
   default ItemBehaviorFunctions barWidth(ToIntFunction<ItemStack> barWidth) {
      this.kjs$getOrCreateBehavior().barWidth = barWidth;
      return this;
   }

   @Info("Sets the item's name dynamically.\n")
   default ItemBehaviorFunctions name(ItemBehavior.NameCallback name) {
      this.kjs$getOrCreateBehavior().nameGetter = name;
      return this;
   }

   @Info("Determines the animation of the item when used, e.g. eating food.")
   default ItemBehaviorFunctions useAnimation(UseAnim anim) {
      this.kjs$getOrCreateBehavior().anim = anim;
      return this;
   }

   @Info("The duration when the item is used.\n\nFor example, when eating food, this is the time it takes to eat the food.\nThis can change the eating speed, or be used for other things (like making a custom bow).\n")
   default ItemBehaviorFunctions useDuration(ToIntBiFunction<ItemStack, LivingEntity> useDuration) {
      this.kjs$getOrCreateBehavior().useDuration = useDuration;
      return this;
   }

   @Info("Determines if player will start using the item.\n\nFor example, when eating food, returning true will make the player start eating the food.\n")
   default ItemBehaviorFunctions use(ItemBehavior.UseCallback use) {
      this.kjs$getOrCreateBehavior().use = use;
      return this;
   }

   @Info("When players finish using the item.\n\nThis is called only when `useDuration` ticks have passed.\n\nFor example, when eating food, this is called when the player has finished eating the food, so hunger is restored.\n")
   default ItemBehaviorFunctions finishUsing(ItemBehavior.FinishUsingCallback finishUsing) {
      this.kjs$getOrCreateBehavior().finishUsing = finishUsing;
      return this;
   }

   @Info("When players did not finish using the item but released the right mouse button halfway through.\n\nAn example is the bow, where the arrow is shot when the player releases the right mouse button.\n\nTo ensure the bow won't finish using, Minecraft sets the `useDuration` to a very high number (1h).\n")
   default ItemBehaviorFunctions releaseUsing(ItemBehavior.ReleaseUsingCallback releaseUsing) {
      this.kjs$getOrCreateBehavior().releaseUsing = releaseUsing;
      return this;
   }

   @Info("Gets called when the item is used to hurt an entity.\n\nFor example, when using a sword to hit a mob, this is called.\n")
   default ItemBehaviorFunctions hurtEnemy(Predicate<ItemBehavior.HurtEnemyContext> hurtEnemy) {
      this.kjs$getOrCreateBehavior().hurtEnemy = hurtEnemy;
      return this;
   }

   @HideFromJS
   default ItemBehaviorFunctions foodEaten(Consumer<FoodEatenKubeEvent> foodEaten) {
      this.kjs$getOrCreateBehavior().foodEaten = foodEaten;
      return this;
   }

   @Info("Determines if piglins will give an item or something in exchange for the item.\n")
   default ItemBehaviorFunctions isPiglinCurrency(Predicate<ItemStack> isPiglinCurrency) {
      this.kjs$getOrCreateBehavior().isPiglinCurrency = isPiglinCurrency;
      return this;
   }

   default ItemBehaviorFunctions isPiglinCurrency(boolean isPiglinCurrency) {
      return this.isPiglinCurrency(stack -> isPiglinCurrency);
   }

   @Info("Whether this item can be used to hide player head for enderman\n")
   default ItemBehaviorFunctions isEnderMask(ItemBehavior.EndermanMaskTest isEnderMask) {
      this.kjs$getOrCreateBehavior().isEnderMask = isEnderMask;
      return this;
   }

   default ItemBehaviorFunctions isEnderMask(boolean isEnderMask) {
      return this.isEnderMask((stack, player, enderman) -> isEnderMask);
   }

   @Info("Determines if piglins will be neutral to the wearer of the item and will not attack on sight.\n\nHowever, this does not prevent piglins from being hostile due to other actions, or make piglins\nstop being hostile if they are already hostile.\n")
   default ItemBehaviorFunctions makesPiglinsNeutral(BiPredicate<ItemStack, LivingEntity> makesPiglinsNeutral) {
      this.kjs$getOrCreateBehavior().makesPiglinsNeutral = makesPiglinsNeutral;
      return this;
   }

   default ItemBehaviorFunctions makesPiglinsNeutral(boolean makesPiglinsNeutral) {
      return this.makesPiglinsNeutral((stack, wearer) -> makesPiglinsNeutral);
   }

   @Info("Returns the item that remains in the crafting grid (or furnace fuel) after crafting with this item programatically.\n\nReturning an empty stack or null will make the item be consumed as normal.\n\nAn example would be durability-consuming items, e.g. hammers or wrenches.\n")
   default ItemBehaviorFunctions craftingRemainingItem(UnaryOperator<ItemStack> craftingRemainingItem) {
      this.kjs$getOrCreateBehavior().craftingRemainingItem = craftingRemainingItem;
      return this;
   }

   @Info("Determines the lifespan in ticks of the item when it's dropped on the ground as an entity.\n\nUsed for items like Fluix Seeds to prevent them from despawning.\n")
   default ItemBehaviorFunctions getEntityLifespan(ToIntBiFunction<ItemStack, Level> getEntityLifespan) {
      this.kjs$getOrCreateBehavior().getEntityLifespan = getEntityLifespan;
      return this;
   }

   default ItemBehaviorFunctions getEntityLifespan(int lifespan) {
      return this.getEntityLifespan((stack, level) -> lifespan);
   }

   @Info("Determines if the item can disable shield when attacking like axe does.\n")
   default ItemBehaviorFunctions canDisableShield(ItemBehavior.DisableShieldTest canDisableShield) {
      this.kjs$getOrCreateBehavior().canDisableShield = canDisableShield;
      return this;
   }

   default ItemBehaviorFunctions canDisableShield(boolean canDisableShield) {
      return this.canDisableShield((stack, shield, attacker) -> canDisableShield);
   }

   @Info("Determines if the item allows player to do elytra flying when equipped in the chest slot.\n")
   default ItemBehaviorFunctions canElytraFly(BiPredicate<ItemStack, LivingEntity> canElytraFly) {
      this.kjs$getOrCreateBehavior().canElytraFly = canElytraFly;
      return this;
   }

   default ItemBehaviorFunctions canElytraFly(boolean canElytraFly) {
      return this.canElytraFly((stack, entity) -> canElytraFly);
   }

   @Info("Called every tick when the player is flying with elytra with this item equipped in the chest slot.\n\nReturning false will stop the player from flying.\n")
   default ItemBehaviorFunctions elytraFlightTick(ItemBehavior.ElytraFlightTickCallback elytraFlightTick) {
      this.kjs$getOrCreateBehavior().elytraFlightTick = elytraFlightTick;
      return this;
   }

   @Info("Determines if the player can walk on powdered snow with this item worn in the feet slot.\n")
   default ItemBehaviorFunctions canWalkOnPowderedSnow(BiPredicate<ItemStack, LivingEntity> canWalkOnPowderedSnow) {
      this.kjs$getOrCreateBehavior().canWalkOnPowderedSnow = canWalkOnPowderedSnow;
      return this;
   }

   default ItemBehaviorFunctions canWalkOnPowderedSnow(boolean canWalkOnPowderedSnow) {
      return this.canWalkOnPowderedSnow((stack, wearer) -> canWalkOnPowderedSnow);
   }

   @Info("Determines if the item can perform corresponding action. E.g. shearing sheep, stripping logs, etc.\n")
   default ItemBehaviorFunctions canPerformAction(BiPredicate<ItemStack, ItemAbility> canPerformAction) {
      this.kjs$getOrCreateBehavior().canPerformAction = canPerformAction;
      return this;
   }

   @Info("Determines if the item entity will be destroyed by the damage source.\n\nFor example, netherite items will not be destroyed by lava.\n")
   default ItemBehaviorFunctions canBeHurtBy(BiPredicate<ItemStack, DamageSource> canBeHurtBy) {
      this.kjs$getOrCreateBehavior().canBeHurtBy = canBeHurtBy;
      return this;
   }

   @Info("Determines if the item entity will be destroyed by listed damages types.\n\nAll other damages will be treated as normal. Passing [] will make the item immune to all damage.\n")
   default ItemBehaviorFunctions onlyHurtBy(List<ResourceKey<DamageType>> damageTypes) {
      return this.canBeHurtBy((stack, source) -> {
         for (ResourceKey<DamageType> type : damageTypes) {
            if (source.is(type)) {
               return true;
            }
         }

         return false;
      });
   }

   @Info("Determines if the item entity will be immune to listed damages types.\n\nAll other damages will be treated as normal. Passing [] will have no effect.\n")
   default ItemBehaviorFunctions immuneTo(List<ResourceKey<DamageType>> damageTypes) {
      return damageTypes.isEmpty() ? this : this.canBeHurtBy((stack, source) -> {
         for (ResourceKey<DamageType> type : damageTypes) {
            if (source.is(type)) {
               return false;
            }
         }

         return true;
      });
   }

   @Info("Returns the enchanted item stack after applying enchantments to the item.\n\nFor example, books will be transformed into enchanted books by using this.\n")
   default ItemBehaviorFunctions applyEnchantments(BiFunction<ItemStack, List<EnchantmentInstance>, ItemStack> applyEnchantments) {
      this.kjs$getOrCreateBehavior().applyEnchantments = applyEnchantments;
      return this;
   }
}
