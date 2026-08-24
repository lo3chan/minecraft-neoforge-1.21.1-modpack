package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.apache.logging.log4j.util.TriConsumer;

public class ToolProperty extends Property {
   public final int color;
   public final List<ToolProperty.RuleFunc> rules;
   public final Set<ItemAbility> abilities;
   private final Tool DEFAULT;
   public static final HashMap<ItemAbility, TriConsumer<Player, Level, BlockPos>> INTERACTION_EFFECTS = new HashMap<ItemAbility, TriConsumer<Player, Level, BlockPos>>() {
      {
         this.put(ItemAbilities.AXE_STRIP, (player, level, pos) -> level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F));
         this.put(ItemAbilities.AXE_SCRAPE, (player, level, pos) -> {
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, pos, 0);
         });
         this.put(ItemAbilities.AXE_WAX_OFF, (player, level, pos) -> {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
         });
         this.put(
            ItemAbilities.SHOVEL_FLATTEN, (player, level, pos) -> level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F)
         );
         this.put(ItemAbilities.SHOVEL_DOUSE, (player, level, pos) -> {
            if (!level.isClientSide()) {
               level.levelEvent(null, 1009, pos, 0);
            }
         });
         this.put(ItemAbilities.HOE_TILL, (player, level, pos) -> level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F));
         this.put(
            ItemAbilities.FIRESTARTER_LIGHT,
            (player, level, pos) -> level.playSound(
               player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F
            )
         );
      }
   };

   public ToolProperty(int color, TagKey<Block> allowedBlocks, Set<ItemAbility> abilities) {
      this(color, List.of(miningSpeed -> Rule.minesAndDrops(allowedBlocks, miningSpeed)), abilities);
   }

   public ToolProperty(int color, List<ToolProperty.RuleFunc> toolRules, Set<ItemAbility> abilities) {
      this.color = color;
      this.abilities = abilities;
      this.rules = toolRules;
      List<Rule> defaultRules = new ArrayList<>(this.rules.stream().map(ruleFunc -> ruleFunc.apply(2.0F)).toList());
      defaultRules.add(Rule.deniesDrops(BlockTags.INCORRECT_FOR_WOODEN_TOOL));
      this.DEFAULT = new Tool(defaultRules, 1.0F, 1);
   }

   public static List<ToolProperty.RuleFunc> getRulesFor(Tool tool) {
      ArrayList<ToolProperty.RuleFunc> result = new ArrayList<>();

      for (Rule rule : tool.rules()) {
         result.add(miningSpeed -> rule);
      }

      return result;
   }

   public static List<ToolProperty.RuleFunc> getShearsRules() {
      return getRulesFor(ShearsItem.createToolProperties());
   }

   public static List<ToolProperty.RuleFunc> getSwordRules() {
      return getRulesFor(SwordItem.createToolProperties());
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.BLOCK && rayTraceResult instanceof BlockHitResult blockHitResult) {
         Level level = projectile.level();
         Tool tool = (Tool)stack.get(DataComponents.TOOL);
         BlockState blockHit = level.getBlockState(blockHitResult.getBlockPos());
         double projectileSpeed = projectile.getKnownMovement().length();
         if (tool != null
            && tool.isCorrectForDrops(blockHit)
            && projectileSpeed > 0.3
            && tool.getMiningSpeed(blockHit) / blockHit.getDestroySpeed(level, blockHitResult.getBlockPos()) > 1.0 / projectileSpeed) {
            if (!level.isClientSide()) {
               level.destroyBlock(blockHitResult.getBlockPos(), false, projectile);
               Block.dropResources(blockHit, level, blockHitResult.getBlockPos(), level.getBlockEntity(blockHitResult.getBlockPos()), projectile, stack);
            }

            event.setCanceled(true);
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.5));
            projectile.hasImpulse = true;
            this.damageItem(projectile, stack, EquipmentSlot.MAINHAND, 2);
         } else {
            for (int i = 0; i < 16 && !level.noCollision(projectile, projectile.getBoundingBox().deflate(1.0E-7)); i++) {
               projectile.setPos(projectile.position().subtract(projectile.getKnownMovement()));
            }
         }
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (!event.isCanceled()) {
         Level level = event.getLevel();
         BlockPos pos = event.getPos();
         BlockState state = level.getBlockState(pos);
         Player player = event.getPlayer();

         for (ItemAbility ability : this.abilities) {
            BlockState modifiedState = state.getToolModifiedState(event.getUseOnContext(), ability, false);
            if (modifiedState != null) {
               ItemStack itemstack = event.getItemStack();
               if (player instanceof ServerPlayer) {
                  CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, itemstack);
               }

               if (INTERACTION_EFFECTS.containsKey(ability)) {
                  INTERACTION_EFFECTS.get(ability).accept(player, level, pos);
               }

               level.setBlock(pos, modifiedState, 11);
               level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, modifiedState));
               this.damageItem(player, itemstack, LivingEntity.getSlotForHand(event.getHand()), 1);
               event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide));
               event.setCanceled(true);
               return;
            }
         }
      }
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      if (!this.rules.isEmpty() && dataType == DataComponents.TOOL) {
         if (data instanceof Tool tool) {
            float miningSpeed = 1.0F;

            for (Rule rule : tool.rules()) {
               if (rule.correctForDrops().isPresent()
                  && (Boolean)rule.correctForDrops().get()
                  && rule.speed().isPresent()
                  && (Float)rule.speed().get() > miningSpeed) {
                  miningSpeed = (Float)rule.speed().get();
               }
            }

            List<Rule> rules = new ArrayList<>(tool.rules());
            float finalMiningSpeed = miningSpeed;
            rules.addAll(this.rules.stream().map(ruleFunc -> ruleFunc.apply(finalMiningSpeed)).toList());
            return new Tool(rules, tool.defaultMiningSpeed(), tool.damagePerBlock());
         } else {
            return this.DEFAULT;
         }
      } else {
         return super.modifyDataComponent(stack, dataType, data);
      }
   }

   @Override
   public boolean modifyAcceptAbility(ItemStack stack, ItemAbility itemAbility, boolean original, boolean result) {
      return result || this.abilities.contains(itemAbility);
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.color;
   }

   public interface RuleFunc extends Function<Float, Rule> {
   }
}
