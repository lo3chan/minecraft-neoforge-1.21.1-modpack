package com.aetherteam.aether.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class WhirlwindSpawnEntity extends LootItemConditionalFunction {
   public static final MapCodec<WhirlwindSpawnEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance)
         .and(EntityTypePredicate.CODEC.fieldOf("entity").forGetter(whirlwindSpawnEntity -> whirlwindSpawnEntity.entityType))
         .and(IntProvider.CODEC.fieldOf("count").forGetter(whirlwindSpawnEntity -> whirlwindSpawnEntity.count))
         .apply(instance, WhirlwindSpawnEntity::new)
   );
   private final EntityTypePredicate entityType;
   private final IntProvider count;

   protected WhirlwindSpawnEntity(List<LootItemCondition> conditions, EntityTypePredicate entityType, IntProvider count) {
      super(conditions);
      this.entityType = entityType;
      this.count = count;
   }

   protected ItemStack run(ItemStack stack, LootContext context) {
      ServerLevel serverLevel = context.getLevel();
      Vec3 originVec = (Vec3)context.getParamOrNull(LootContextParams.ORIGIN);
      if (originVec != null) {
         for (int i = 0; i < this.count.sample(serverLevel.getRandom()); i++) {
            HolderSet<EntityType<?>> holderSet = this.entityType.types();
            if (holderSet.size() > 0) {
               Entity entity = ((EntityType)this.entityType.types().get(serverLevel.getRandom().nextInt(holderSet.size())).value()).create(serverLevel);
               if (entity != null) {
                  entity.moveTo(originVec.x(), originVec.y() + 0.5, originVec.z(), (float)Math.random() * 360.0F, 0.0F);
                  entity.setDeltaMovement((Math.random() - Math.random()) * 0.125, entity.getDeltaMovement().y(), (Math.random() - Math.random()) * 0.125);
                  serverLevel.addFreshEntity(entity);
               }
            }
         }
      }

      return stack;
   }

   public static Builder<?> builder(EntityTypePredicate entityType, IntProvider count) {
      return LootItemConditionalFunction.simpleBuilder(lootItemConditions -> new WhirlwindSpawnEntity(lootItemConditions, entityType, count));
   }

   public LootItemFunctionType<WhirlwindSpawnEntity> getType() {
      return (LootItemFunctionType<WhirlwindSpawnEntity>)AetherLootFunctions.WHIRLWIND_SPAWN_ENTITY.get();
   }
}
