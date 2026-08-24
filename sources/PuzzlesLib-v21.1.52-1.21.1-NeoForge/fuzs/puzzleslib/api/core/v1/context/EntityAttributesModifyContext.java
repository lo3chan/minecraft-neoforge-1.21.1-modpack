package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

@Deprecated
public interface EntityAttributesModifyContext {
   default void registerAttributeModification(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
      this.registerAttributeModification(entityType, attribute, ((Attribute)attribute.value()).getDefaultValue());
   }

   void registerAttributeModification(EntityType<? extends LivingEntity> var1, Holder<Attribute> var2, double var3);
}
