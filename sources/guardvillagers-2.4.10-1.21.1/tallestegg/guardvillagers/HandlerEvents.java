package tallestegg.guardvillagers;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import tallestegg.guardvillagers.client.GuardSounds;
import tallestegg.guardvillagers.common.entities.Guard;
import tallestegg.guardvillagers.common.entities.ai.goals.AttackEntityDaytimeGoal;
import tallestegg.guardvillagers.common.entities.ai.goals.GetOutOfWaterGoal;
import tallestegg.guardvillagers.common.entities.ai.goals.GolemFloatWaterGoal;
import tallestegg.guardvillagers.configuration.GuardConfig;

@EventBusSubscriber(
   modid = "guardvillagers"
)
public class HandlerEvents {
   private static final Predicate<LivingEntity> ISNT_BABY = mob -> !mob.isBaby();

   @SubscribeEvent
   public static void onEntityTarget(LivingChangeTargetEvent event) {
      LivingEntity entity = event.getEntity();
      LivingEntity target = event.getNewAboutToBeSetTarget();
      if (target != null && entity.getType() != GuardEntityType.GUARD.get() && !(entity instanceof IronGolem)) {
         boolean isVillager = ((List)GuardConfig.COMMON.mobsGuardsProtectTargeted.get()).contains(target.getEncodeId());
         if (isVillager) {
            for (Mob mob : entity.level()
               .getEntitiesOfClass(
                  Mob.class,
                  entity.getBoundingBox()
                     .inflate((Double)GuardConfig.COMMON.GuardVillagerHelpRange.get(), 5.0, (Double)GuardConfig.COMMON.GuardVillagerHelpRange.get())
               )) {
               if (mob.getTarget() == null && (mob.getType() == GuardEntityType.GUARD.get() || mob.getType() == EntityType.IRON_GOLEM)) {
                  if (mob.getTeam() != null && entity.getTeam() != null && entity.getTeam().isAlliedTo(mob.getTeam())) {
                     return;
                  }

                  mob.setTarget(entity);
               }
            }
         }

         if (entity instanceof IronGolem golem && target instanceof Guard) {
            golem.setTarget(null);
         }
      }
   }

   @SubscribeEvent
   public static void onEntityHurt(Pre event) {
      LivingEntity entity = event.getEntity();
      Entity trueSource = event.getContainer().getSource().getEntity();
      if (entity != null && trueSource != null) {
         boolean isVillager = ((List)GuardConfig.COMMON.mobsGuardsProtectHurt.get()).contains(entity.getEncodeId());
         if (isVillager && trueSource.getType() == GuardEntityType.GUARD.get() && !(Boolean)GuardConfig.COMMON.guardArrowsHurtVillagers.get()) {
            event.getContainer().setNewDamage(0.0F);
         }

         if (isVillager && event.getContainer().getSource().getEntity() instanceof Mob) {
            for (Mob mob : trueSource.level()
               .getEntitiesOfClass(
                  Mob.class,
                  trueSource.getBoundingBox()
                     .inflate((Double)GuardConfig.COMMON.GuardVillagerHelpRange.get(), 5.0, (Double)GuardConfig.COMMON.GuardVillagerHelpRange.get())
               )) {
               boolean type = mob.getType() == GuardEntityType.GUARD.get() || mob.getType() == EntityType.IRON_GOLEM;
               boolean trueSourceGolem = trueSource.getType() == GuardEntityType.GUARD.get() || trueSource.getType() == EntityType.IRON_GOLEM;
               if (!trueSourceGolem && type && mob.getTarget() == null) {
                  if (mob.getTeam() != null && entity.getTeam() != null && entity.getTeam().isAlliedTo(mob.getTeam())) {
                     return;
                  }

                  mob.setTarget((Mob)event.getContainer().getSource().getEntity());
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingSpawned(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (mob instanceof Raider && ((Raider)mob).hasActiveRaid() && (Boolean)GuardConfig.COMMON.RaidAnimals.get()) {
            mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal((Raider)mob, Animal.class, false));
         }

         if ((Boolean)GuardConfig.COMMON.MobsAttackGuards.get()
            && mob instanceof Enemy
            && !((List)GuardConfig.COMMON.MobBlackList.get()).contains(mob.getEncodeId())) {
            if (!(mob instanceof Spider)) {
               mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal(mob, Guard.class, false));
            } else {
               mob.targetSelector.addGoal(3, new AttackEntityDaytimeGoal((Spider)mob, Guard.class));
            }
         }

         if (mob instanceof AbstractIllager illager) {
            if ((Boolean)GuardConfig.COMMON.IllagersRunFromPolarBears.get()) {
               illager.goalSelector.addGoal(2, new AvoidEntityGoal(illager, PolarBear.class, 6.0F, 1.0, 1.2));
            }

            illager.targetSelector.addGoal(2, new NearestAttackableTargetGoal(illager, Guard.class, false));
         }

         if (mob instanceof AbstractVillager abstractvillager) {
            if ((Boolean)GuardConfig.COMMON.VillagersRunFromPolarBears.get()) {
               abstractvillager.goalSelector.addGoal(2, new AvoidEntityGoal(abstractvillager, PolarBear.class, 6.0F, 1.0, 1.2));
            }

            if ((Boolean)GuardConfig.COMMON.WitchesVillager.get()) {
               abstractvillager.goalSelector.addGoal(2, new AvoidEntityGoal(abstractvillager, Witch.class, 6.0F, 1.0, 1.2));
            }
         }

         if (mob instanceof IronGolem golem) {
            HurtByTargetGoal tolerateFriendlyFire = new HurtByTargetGoal(golem, new Class[]{Guard.class}).setAlertOthers(new Class[0]);
            golem.targetSelector
               .getAvailableGoals()
               .stream()
               .map(it -> it.getGoal())
               .filter(it -> it instanceof HurtByTargetGoal)
               .findFirst()
               .ifPresent(angerGoal -> {
                  golem.targetSelector.removeGoal(angerGoal);
                  golem.targetSelector.addGoal(2, tolerateFriendlyFire);
               });
            if ((Boolean)GuardConfig.COMMON.golemFloat.get()) {
               golem.goalSelector.addGoal(0, new GetOutOfWaterGoal(golem, 1.0));
               golem.goalSelector.addGoal(1, new GolemFloatWaterGoal(golem));
            }
         }

         if (mob instanceof Zombie zombie && !(zombie instanceof ZombifiedPiglin)) {
            zombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal(zombie, Guard.class, false));
         }

         if (mob instanceof Ravager ravager) {
            ravager.targetSelector.addGoal(2, new NearestAttackableTargetGoal(ravager, Guard.class, false));
         }

         if (mob instanceof Witch witch && (Boolean)GuardConfig.COMMON.WitchesVillager.get()) {
            witch.targetSelector.addGoal(3, new NearestAttackableWitchTargetGoal(witch, AbstractVillager.class, 10, true, false, ISNT_BABY));
            witch.targetSelector.addGoal(3, new NearestAttackableWitchTargetGoal(witch, IronGolem.class, 10, true, false, null));
            witch.targetSelector.addGoal(2, new NearestAttackableWitchTargetGoal(witch, Guard.class, 10, true, false, null));
         }

         if (mob instanceof Cat cat) {
            cat.goalSelector.addGoal(1, new AvoidEntityGoal(cat, AbstractIllager.class, 12.0F, 1.0, 1.2));
         }
      }
   }

   @SubscribeEvent
   public static void onEntityInteract(EntityInteract event) {
      Player player = event.getEntity();
      ItemStack itemstack = event.getEntity().getMainHandItem();
      if (itemstack.is(GuardVillagerTags.GUARD_CONVERT)
         && player.isCrouching()
         && event.getTarget() instanceof Villager villager
         && !villager.isBaby()
         && ((List)GuardConfig.COMMON.convertibleProfessions.get()).contains(villager.getVillagerData().getProfession().name())
         && (
            !(Boolean)GuardConfig.COMMON.ConvertVillagerIfHaveHOTV.get()
               || player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE) && (Boolean)GuardConfig.COMMON.ConvertVillagerIfHaveHOTV.get()
         )) {
         convertVillager(villager, player);
         if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
         }
      }
   }

   @SubscribeEvent
   public static void onEntityInteract(RightClickBlock event) {
      Player player = event.getEntity();
      Level level = event.getLevel();
      BlockPos pos = event.getHitVec().getBlockPos();
      BlockState originalBlock = player.level().getBlockState(pos);
      if ((Boolean)GuardConfig.COMMON.multiFollow.get()
         && originalBlock.getBlock() instanceof BellBlock
         && level.getBlockEntity(pos) instanceof BellBlockEntity bellBlockEntity
         && !bellBlockEntity.shaking) {
         for (Guard guard : player.level().getEntitiesOfClass(Guard.class, player.getBoundingBox().inflate(32.0, 32.0, 32.0))) {
            if (GuardVillagers.canFollow(player)) {
               event.setCancellationResult(InteractionResult.SUCCESS);
               guard.setFollowing(!guard.isFollowing());
               guard.playSound((SoundEvent)GuardSounds.GUARD_YES.value());
               if (guard.isFollowing()) {
                  guard.setOwnerId(player.getUUID());
                  guard.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 1));
                  level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);
               } else {
                  guard.removeEffect(MobEffects.GLOWING);
               }
            }
         }
      }
   }

   private static void convertVillager(LivingEntity entity, Player player) {
      player.swing(InteractionHand.MAIN_HAND);
      ItemStack itemstack = player.getItemBySlot(EquipmentSlot.MAINHAND);
      Guard guard = (Guard)((EntityType)GuardEntityType.GUARD.get()).create(entity.level());
      Villager villager = (Villager)entity;
      if (guard != null) {
         if (entity.level().isClientSide) {
            ParticleOptions iparticledata = ParticleTypes.HAPPY_VILLAGER;

            for (int i = 0; i < 10; i++) {
               double d0 = villager.getRandom().nextGaussian() * 0.02;
               double d1 = villager.getRandom().nextGaussian() * 0.02;
               double d2 = villager.getRandom().nextGaussian() * 0.02;
               villager.level()
                  .addParticle(
                     iparticledata,
                     villager.getX() + villager.getRandom().nextFloat() * villager.getBbWidth() * 2.0F - villager.getBbWidth(),
                     villager.getY() + 0.5 + villager.getRandom().nextFloat() * villager.getBbHeight(),
                     villager.getZ() + villager.getRandom().nextFloat() * villager.getBbWidth() * 2.0F - villager.getBbWidth(),
                     d0,
                     d1,
                     d2
                  );
            }
         }

         guard.copyPosition(villager);
         guard.playSound((SoundEvent)GuardSounds.GUARD_YES.value(), 1.0F, 1.0F);
         guard.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
         guard.setVariant(villager.getVariant().toString());
         guard.setPersistenceRequired();
         guard.setCustomName(villager.getCustomName());
         guard.setCustomNameVisible(villager.isCustomNameVisible());
         guard.setDropChance(EquipmentSlot.HEAD, 100.0F);
         guard.setDropChance(EquipmentSlot.CHEST, 100.0F);
         guard.setDropChance(EquipmentSlot.FEET, 100.0F);
         guard.setDropChance(EquipmentSlot.LEGS, 100.0F);
         guard.setDropChance(EquipmentSlot.MAINHAND, 100.0F);
         guard.setDropChance(EquipmentSlot.OFFHAND, 100.0F);
         guard.getGossips().add(player.getUUID(), GossipType.MINOR_POSITIVE, (Integer)GuardConfig.COMMON.reputationRequirement.get());
         villager.level().addFreshEntity(guard);
         villager.releasePoi(MemoryModuleType.HOME);
         villager.releasePoi(MemoryModuleType.JOB_SITE);
         villager.releasePoi(MemoryModuleType.MEETING_POINT);
         villager.discard();
         if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, guard);
            player.awardStat((ResourceLocation)GuardStats.GUARDS_MADE.get());
         }
      }
   }
}
