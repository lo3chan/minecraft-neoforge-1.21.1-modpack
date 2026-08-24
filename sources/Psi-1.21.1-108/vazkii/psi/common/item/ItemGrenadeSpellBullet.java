package vazkii.psi.common.item;

import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.entity.EntitySpellGrenade;
import vazkii.psi.common.entity.EntitySpellProjectile;

public class ItemGrenadeSpellBullet extends ItemSpellBullet {
   public ItemGrenadeSpellBullet(Properties properties) {
      super(properties);
   }

   @Override
   public ArrayList<Entity> castSpell(ItemStack stack, SpellContext context) {
      ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
      ItemStack colorizer = ((ICAD)cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
      EntitySpellProjectile projectile = new EntitySpellGrenade(context.caster.getCommandSenderWorld(), context.caster);
      projectile.setInfo(context.caster, colorizer, stack);
      projectile.context = context;
      projectile.getCommandSenderWorld().addFreshEntity(projectile);
      ArrayList<Entity> spellEntities = new ArrayList<>();
      spellEntities.add(projectile);
      return spellEntities;
   }

   @Override
   public double getCostModifier(ItemStack stack) {
      return 1.05;
   }

   @Override
   public String getBulletType() {
      return "grenade";
   }
}
