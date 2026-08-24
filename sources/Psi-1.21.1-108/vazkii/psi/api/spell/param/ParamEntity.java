package vazkii.psi.api.spell.param;

import net.minecraft.world.entity.Entity;

public class ParamEntity extends ParamSpecific<Entity> {
   public ParamEntity(String name, int color, boolean canDisable, boolean constant) {
      super(name, color, canDisable, constant);
   }

   @Override
   public Class<Entity> getRequiredType() {
      return Entity.class;
   }
}
