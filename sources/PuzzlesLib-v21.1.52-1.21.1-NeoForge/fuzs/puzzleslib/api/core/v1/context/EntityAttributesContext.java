package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;

public interface EntityAttributesContext {
   void registerAttributes(EntityType<? extends LivingEntity> var1, Builder var2);

   default void registerAttribute(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
      this.registerAttribute(entityType, attribute, ((Attribute)attribute.value()).getDefaultValue());
   }

   void registerAttribute(EntityType<? extends LivingEntity> var1, Holder<Attribute> var2, double var3);
}
