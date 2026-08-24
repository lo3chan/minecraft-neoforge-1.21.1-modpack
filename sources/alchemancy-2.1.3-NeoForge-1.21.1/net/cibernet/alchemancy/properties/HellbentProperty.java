package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;

@EventBusSubscriber
public class HellbentProperty extends Property implements IDataHolder<Holder<Block>> {
   private static final ResourceLocation SPEED_MOD_KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "hellbent_property_modifier");

   @Override
   public void modifyCriticalAttack(Player user, ItemStack weapon, CriticalHitEvent event) {
      if (!event.isCriticalHit()) {
         event.setCriticalHit(true);
         this.damageOrConsumeItem(user, weapon, EquipmentSlot.MAINHAND, 5);
      }
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult) {
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).onCriticalAttack(null, stack, entityHitResult.getEntity())
         );
         if (projectile.level().isClientSide) {
            ClientUtil.createTrackedParticles(entityHitResult.getEntity(), ParticleTypes.CRIT);
         }
      }
   }

   @Override
   public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event) {
      if (breaker instanceof Player player) {
         AttributeInstance attribute = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
         if (attribute == null) {
            return;
         }

         AttributeModifier mod = attribute.getModifier(SPEED_MOD_KEY);
         double value = Math.min(1.2, (mod == null ? 0.0 : mod.amount()) + 0.05);
         attribute.removeModifier(SPEED_MOD_KEY);
         Holder<Block> blockString = this.getData(tool);
         if (event.getState().is(blockString)) {
            attribute.addPermanentModifier(new AttributeModifier(SPEED_MOD_KEY, value, Operation.ADD_MULTIPLIED_TOTAL));
         } else {
            this.setData(tool, event.getState().getBlockHolder());
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onPlayerTick(Pre event) {
      if (!event.getEntity().level().isClientSide) {
         Player player = event.getEntity();
         AttributeInstance attribute = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
         if (attribute != null
            && attribute.hasModifier(SPEED_MOD_KEY)
            && !InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.HELLBENT)) {
            attribute.removeModifier(SPEED_MOD_KEY);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 7483954;
   }

   public Holder<Block> readData(CompoundTag tag) {
      RegistryAccess registryAccess = CommonUtils.registryAccessStatic();
      Optional<Reference<Block>> optional = registryAccess.holder(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(tag.getString("block_id"))));
      return optional.isEmpty() ? this.getDefaultData() : (Holder)optional.get();
   }

   public CompoundTag writeData(final Holder<Block> data) {
      return new CompoundTag() {
         {
            this.putString("block_id", data.getKey().location().toString());
         }
      };
   }

   public Holder<Block> getDefaultData() {
      return Blocks.AIR.builtInRegistryHolder();
   }
}
