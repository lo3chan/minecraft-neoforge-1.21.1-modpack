package net.cibernet.alchemancy.properties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.cibernet.alchemancy.util.RedstoneSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;

@EventBusSubscriber
public class EnergizedProperty extends AbstractTimerProperty {
   private static final ResourceLocation SPEED_MOD_KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "energized_property_modifier");
   private static final long ENERGIZED_DURATION = 1200L;

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE)) {
         return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
      } else {
         Level level = blockSource.level();
         BlockPos pos = blockSource.pos().relative(direction);
         BlockState state = level.getBlockState(pos);
         if (!state.canRedstoneConnectTo(level, pos, direction)) {
            return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
         } else {
            powerBlock(blockSource.level(), pos, 0.46666667F, direction);
            InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
            return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(Pre event) {
      Player player = event.getEntity();
      AttributeMap attributeMap = player.getAttributes();
      float energizedValue = 0.0F;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = player.getItemBySlot(slot);
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ENERGIZED)) {
            energizedValue += ((EnergizedProperty)AlchemancyProperties.ENERGIZED.get()).getEnergizedTime(stack);
         }
      }

      energizedValue *= 0.35F;
      Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
      attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(SPEED_MOD_KEY, energizedValue, Operation.ADD_MULTIPLIED_BASE));
      attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MOD_KEY, energizedValue, Operation.ADD_MULTIPLIED_BASE));
      attributes.put(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(SPEED_MOD_KEY, energizedValue, Operation.ADD_MULTIPLIED_BASE));
      attributeMap.removeAttributeModifiers(attributes);
      if (energizedValue > 0.0F) {
         attributeMap.addTransientAttributeModifiers(attributes);
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (slot == EquipmentSlot.FEET && user.level() instanceof ServerLevel serverLevel) {
         float strength = this.getEnergizedTime(stack);
         if (strength > 0.0F) {
            powerBlock(serverLevel, user.blockPosition().below(), strength, Direction.UP);
         }
      }
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.BLOCK
         && rayTraceResult instanceof BlockHitResult blockHitResult
         && projectile.level() instanceof ServerLevel serverLevel) {
         powerBlock(serverLevel, blockHitResult.getBlockPos(), 1.0F, blockHitResult.getDirection());
      }
   }

   public static void powerBlock(ServerLevel level, BlockPos pos, float strength, Direction direction) {
      RedstoneSources.createSourceAt(level, pos, (int)Math.ceil(15.0F * strength), 10, direction);
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (event.getLevel() instanceof ServerLevel serverLevel && InfusedPropertiesHelper.hasProperty(event.getItemStack(), AlchemancyProperties.INTERACTABLE)) {
         powerBlock(serverLevel, event.getPos(), 1.0F, event.getFace());
      }
   }

   @Override
   public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack) {
      if (level instanceof ServerLevel serverLevel) {
         RedstoneSources.createSourceAt(serverLevel, position, 15, 20, Direction.DOWN);
      }
   }

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if (damageSource.is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE)) {
         this.resetStartTimestamp(weapon);
      }
   }

   public float getEnergizedTime(ItemStack stack) {
      return this.getData(stack) == 0L ? 0.0F : (float)(1L - Mth.clamp(this.getElapsedTime(stack) / 1200L, 0L, 1L));
   }

   @Override
   public int getColor(ItemStack stack) {
      return ARGB32.lerp(this.getEnergizedTime(stack), 7539712, 15081480);
   }
}
