package vectorwing.farmersdelight.common.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.item.component.ItemStackWrapper;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class SkilletItem extends BlockItem {
   public static final float FLIP_TIME = 12.0F;
   public static final Tiers SKILLET_TIER = Tiers.IRON;
   protected static final ResourceLocation FD_ATTACK_KNOCKBACK_UUID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "base_attack_knockback");

   public SkilletItem(Block block, Properties properties) {
      super(block, properties.durability(SKILLET_TIER.getUses()));
      float attackDamage = 5.0F + SKILLET_TIER.getAttackDamageBonus();
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return oldStack.get((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get())
               == newStack.get((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get())
            && oldStack.get((DataComponentType)ModDataComponents.COOKING_TIME_LENGTH.get())
               == newStack.get((DataComponentType)ModDataComponents.COOKING_TIME_LENGTH.get())
            && oldStack.get((DataComponentType)ModDataComponents.SKILLET_INGREDIENT.get())
               == newStack.get((DataComponentType)ModDataComponents.SKILLET_INGREDIENT.get())
         ? super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
         : false;
   }

   public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(FD_ATTACK_KNOCKBACK_UUID, 1.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
      return !player.isCreative();
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
   }

   private static boolean isPlayerNearHeatSource(Player player, LevelReader level) {
      if (player.isOnFire()) {
         return true;
      } else {
         BlockPos pos = player.blockPosition();

         for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (level.getBlockState(nearbyPos).is(ModTags.Blocks.HEAT_SOURCES)) {
               return true;
            }
         }

         return false;
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
      tooltip.add(TextUtils.PLACEABLE_SNEAKING);
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      int fireAspectLevel = ItemUtils.getValidatedEnchantmentLevel(Enchantments.FIRE_ASPECT, entity.level().registryAccess(), stack);
      int cookingTime = (Integer)stack.getOrDefault(ModDataComponents.COOKING_TIME_LENGTH, 0);
      return SkilletBlock.getSkilletCookingTime(cookingTime, fireAspectLevel);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack skilletStack = player.getItemInHand(hand);
      if (isPlayerNearHeatSource(player, level)) {
         InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         ItemStack cookingStack = player.getItemInHand(otherHand);
         if (!((ItemStackWrapper)skilletStack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY)).getStack().isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.pass(skilletStack);
         }

         Optional<RecipeHolder<CampfireCookingRecipe>> recipe = getCookingRecipe(cookingStack, level);
         if (recipe.isPresent()) {
            if (player.isUnderWater()) {
               player.displayClientMessage(TextUtils.item("skillet.underwater"), true);
               return InteractionResultHolder.pass(skilletStack);
            }

            ItemStack cookingStackCopy = cookingStack.copy();
            ItemStack cookingStackUnit = cookingStackCopy.split(1);
            skilletStack.set(ModDataComponents.SKILLET_INGREDIENT, new ItemStackWrapper(cookingStackUnit));
            skilletStack.set(ModDataComponents.COOKING_TIME_LENGTH, ((CampfireCookingRecipe)recipe.get().value()).getCookingTime());
            player.startUsingItem(hand);
            player.setItemInHand(otherHand, cookingStackCopy);
            return InteractionResultHolder.consume(skilletStack);
         }

         player.displayClientMessage(TextUtils.item("skillet.how_to_cook"), true);
      }

      return InteractionResultHolder.pass(skilletStack);
   }

   public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
      if (entity instanceof Player player) {
         if (stack.has((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get())) {
            long flipTimeStamp = (Long)stack.get((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get());
            long l = level.getGameTime() - flipTimeStamp;
            if ((float)l > 12.0F) {
               stack.remove((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get());
               stack.set(
                  (DataComponentType)ModDataComponents.SKILLET_FLIPPED.get(),
                  !(Boolean)stack.getOrDefault((DataComponentType)ModDataComponents.SKILLET_FLIPPED.get(), false)
               );
            } else if (level.isClientSide && (float)l == 4.0F) {
               level.playSound(player, entity, ModSounds.BLOCK_SKILLET_ADD_FOOD.get(), SoundSource.PLAYERS, 0.4F, level.random.nextFloat() * 0.2F + 0.9F);
            } else if (level.isClientSide && level.random.nextInt(50) == 0 && (float)l < 4.0F || (float)l > 9.0F) {
               level.playSound(null, entity, ModSounds.BLOCK_SKILLET_SIZZLE.get(), SoundSource.PLAYERS, 0.4F, level.random.nextFloat() * 0.2F + 0.9F);
            }
         } else if (level.isClientSide && level.random.nextInt(50) == 0) {
            level.playSound(null, entity, ModSounds.BLOCK_SKILLET_SIZZLE.get(), SoundSource.PLAYERS, 0.4F, level.random.nextFloat() * 0.2F + 0.9F);
         }
      }
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
      if (entity instanceof Player player) {
         ItemStackWrapper storedStack = (ItemStackWrapper)stack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY);
         if (!storedStack.getStack().isEmpty()) {
            ItemStack cookingStack = storedStack.getStack();
            player.getInventory().placeItemBackInInventory(cookingStack);
            stack.remove(ModDataComponents.SKILLET_INGREDIENT);
            stack.remove(ModDataComponents.COOKING_TIME_LENGTH);
            stack.remove((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get());
            stack.remove((DataComponentType)ModDataComponents.SKILLET_FLIPPED.get());
         }
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      if (entity instanceof Player player) {
         ItemStackWrapper storedStack = (ItemStackWrapper)stack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY);
         if (!storedStack.getStack().isEmpty()) {
            ItemStack cookingStack = storedStack.getStack();
            Optional<RecipeHolder<CampfireCookingRecipe>> cookingRecipe = getCookingRecipe(cookingStack, level);
            cookingRecipe.ifPresent(recipe -> {
               ItemStack resultStack = ((CampfireCookingRecipe)recipe.value()).assemble(new SingleRecipeInput(cookingStack), level.registryAccess());
               if (!player.getInventory().add(resultStack)) {
                  player.drop(resultStack, false);
               }

               if (player instanceof ServerPlayer) {
                  CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
               }
            });
            stack.remove(ModDataComponents.SKILLET_INGREDIENT);
            stack.remove(ModDataComponents.COOKING_TIME_LENGTH);
            stack.remove((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get());
            stack.remove((DataComponentType)ModDataComponents.SKILLET_FLIPPED.get());
         }
      }

      return stack;
   }

   public int getBarWidth(ItemStack stack) {
      return stack.has((DataComponentType)ModDataComponents.COOKING_TIME_LENGTH.get())
         ? Math.round(
            13.0F
               - ClientRenderUtils.getClientPlayerHack().getUseItemRemainingTicks()
                  * 13.0F
                  / this.getUseDuration(stack, ClientRenderUtils.getClientPlayerHack())
         )
         : super.getBarWidth(stack);
   }

   public int getBarColor(ItemStack stack) {
      return stack.has((DataComponentType)ModDataComponents.COOKING_TIME_LENGTH.get()) ? 16747343 : super.getBarColor(stack);
   }

   public boolean isBarVisible(ItemStack stack) {
      return super.isBarVisible(stack) || stack.has((DataComponentType)ModDataComponents.COOKING_TIME_LENGTH.get());
   }

   public static Optional<RecipeHolder<CampfireCookingRecipe>> getCookingRecipe(ItemStack stack, Level level) {
      return stack.isEmpty() ? Optional.empty() : level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(stack), level);
   }

   protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
      super.updateCustomBlockEntityTag(pos, level, player, stack, state);
      if (level.getBlockEntity(pos) instanceof SkilletBlockEntity skillet) {
         skillet.setSkilletItem(stack);
         return true;
      } else {
         return false;
      }
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return SKILLET_TIER.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
   }

   public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
      if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
         stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
      }

      return true;
   }

   public InteractionResult place(BlockPlaceContext context) {
      Player player = context.getPlayer();
      return player != null && player.isShiftKeyDown() ? super.place(context) : InteractionResult.PASS;
   }

   public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment.is(Enchantments.SWEEPING_EDGE) ? false : super.isPrimaryItemFor(stack, enchantment);
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment.is(Enchantments.SWEEPING_EDGE) ? false : super.supportsEnchantment(stack, enchantment);
   }

   public int getEnchantmentValue() {
      return SKILLET_TIER.getEnchantmentValue();
   }

   @EventBusSubscriber(
      modid = "farmersdelight"
   )
   public static class SkilletEvents {
      @SubscribeEvent
      public static void playSkilletAttackSound(Pre event) {
         DamageSource damageSource = event.getSource();
         if (damageSource.getDirectEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.SKILLET.get())) {
               float pitch = 0.9F + livingEntity.getRandom().nextFloat() * 0.2F;
               if (livingEntity instanceof Player player) {
                  float attackPower = player.getAttackStrengthScale(0.0F);
                  if (attackPower > 0.8F) {
                     player.getCommandSenderWorld()
                        .playSound(
                           null, player.getX(), player.getY(), player.getZ(), ModSounds.ITEM_SKILLET_ATTACK_STRONG.get(), SoundSource.PLAYERS, 1.0F, pitch
                        );
                  } else {
                     player.getCommandSenderWorld()
                        .playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ITEM_SKILLET_ATTACK_WEAK.get(), SoundSource.PLAYERS, 0.8F, 0.9F);
                  }
               } else {
                  livingEntity.getCommandSenderWorld()
                     .playSound(
                        null,
                        livingEntity.getX(),
                        livingEntity.getY(),
                        livingEntity.getZ(),
                        ModSounds.ITEM_SKILLET_ATTACK_STRONG.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        pitch
                     );
               }
            }
         }
      }
   }
}
