package vazkii.psi.common.item;

import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.phys.HitResult;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.entity.EntitySpellCircle;
import vazkii.psi.common.entity.ModEntities;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorRaycast;

public class ItemCircleSpellBullet extends ItemSpellBullet {
   public ItemCircleSpellBullet(Properties properties) {
      super(properties);
   }

   @Override
   public ArrayList<Entity> castSpell(ItemStack stack, SpellContext context) {
      ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
      ItemStack colorizer = ((ICAD)cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
      HitResult pos = PieceOperatorVectorRaycast.raycast(context.caster, 32.0);
      ArrayList<Entity> spellEntities = new ArrayList<>();
      EntitySpellCircle circle = new EntitySpellCircle(ModEntities.spellCircle, context.caster.getCommandSenderWorld());
      circle.setInfo(context.caster, colorizer, stack);
      circle.setPos(pos.getLocation().x, pos.getLocation().y, pos.getLocation().z);
      circle.getCommandSenderWorld().addFreshEntity(circle);
      spellEntities.add(circle);
      return spellEntities;
   }

   @Override
   public double getCostModifier(ItemStack stack) {
      return 15.0;
   }

   @Override
   public String getBulletType() {
      return "circle";
   }

   @Override
   public boolean isCADOnlyContainer(ItemStack stack) {
      return true;
   }
}
