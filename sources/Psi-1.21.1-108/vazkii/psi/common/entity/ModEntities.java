package vazkii.psi.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import vazkii.psi.common.Psi;

@EventBusSubscriber(
   modid = "psi"
)
public final class ModEntities {
   public static EntityType<EntitySpellProjectile> spellProjectile;
   public static EntityType<EntitySpellCircle> spellCircle;
   public static EntityType<EntitySpellGrenade> spellGrenade;
   public static EntityType<EntitySpellCharge> spellCharge;
   public static EntityType<EntitySpellMine> spellMine;

   @SubscribeEvent
   public static void register(RegisterEvent evt) {
      evt.register(
         Registries.ENTITY_TYPE,
         helper -> {
            spellProjectile = Builder.of(EntitySpellProjectile::new, MobCategory.MISC)
               .setTrackingRange(256)
               .setUpdateInterval(10)
               .setShouldReceiveVelocityUpdates(true)
               .sized(0.0F, 0.0F)
               .build("");
            spellCircle = Builder.of(EntitySpellCircle::new, MobCategory.MISC)
               .setTrackingRange(256)
               .setUpdateInterval(10)
               .setShouldReceiveVelocityUpdates(false)
               .sized(3.0F, 0.3F)
               .fireImmune()
               .build("");
            spellGrenade = Builder.of(EntitySpellGrenade::new, MobCategory.MISC)
               .setTrackingRange(256)
               .setUpdateInterval(10)
               .setShouldReceiveVelocityUpdates(true)
               .sized(0.0F, 0.0F)
               .build("");
            spellCharge = Builder.of(EntitySpellCharge::new, MobCategory.MISC)
               .setTrackingRange(256)
               .setUpdateInterval(10)
               .setShouldReceiveVelocityUpdates(true)
               .sized(0.0F, 0.0F)
               .build("");
            spellMine = Builder.of(EntitySpellMine::new, MobCategory.MISC)
               .setTrackingRange(256)
               .setUpdateInterval(10)
               .setShouldReceiveVelocityUpdates(true)
               .sized(0.0F, 0.0F)
               .build("");
            helper.register(Psi.location("spell_projectile"), spellProjectile);
            helper.register(Psi.location("spell_circle"), spellCircle);
            helper.register(Psi.location("spell_grenade"), spellGrenade);
            helper.register(Psi.location("spell_charge"), spellCharge);
            helper.register(Psi.location("spell_mine"), spellMine);
         }
      );
   }
}
