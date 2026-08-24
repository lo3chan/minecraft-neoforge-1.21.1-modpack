package vazkii.psi.api.spell;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;

public final class SpellContext {
   public static final double MAX_DISTANCE = 32.0;
   public final Map<String, Object> customData = new HashMap<>();
   public final Object[][] evaluatedObjects = new Object[9][9];
   public Player caster;
   public Entity focalPoint;
   public CompiledSpell cspell;
   public int loopcastIndex = 0;
   public InteractionHand castFrom;
   public ItemStack tool = ItemStack.EMPTY;
   public BlockHitResult positionBroken;
   public LivingEntity attackedEntity;
   public LivingEntity attackingEntity;
   public double damageTaken;
   public int targetSlot = 1;
   public boolean shiftTargetSlot = true;
   public boolean customTargetSlot = false;
   public Stack<CompiledSpell.Action> actions = null;
   public boolean stopped = false;
   public int delay = 0;

   public SpellContext setPlayer(Player player) {
      this.caster = player;
      return this.setFocalPoint(player);
   }

   public SpellContext setFocalPoint(Entity e) {
      this.focalPoint = e;
      return this;
   }

   public SpellContext setCompiledSpell(CompiledSpell spell) {
      this.cspell = spell;
      return this;
   }

   public SpellContext setSpell(Spell spell) {
      this.setCompiledSpell(PsiAPI.internalHandler.getSpellCache().getCompiledSpell(spell));
      return this;
   }

   public SpellContext setLoopcastIndex(int i) {
      this.loopcastIndex = i;
      return this;
   }

   public boolean isValid() {
      return this.cspell != null;
   }

   public boolean shouldSuppressErrors() {
      return this.isValid() && this.cspell.metadata.errorsSuppressed;
   }

   public boolean isInRadius(Vector3 vec) {
      return this.isInRadius(vec.x, vec.y, vec.z);
   }

   public boolean isInRadius(Entity e) {
      if (e == null) {
         return false;
      } else {
         return e != this.focalPoint && e != this.caster ? this.isInRadius(e.getX(), e.getY(), e.getZ()) : true;
      }
   }

   public boolean isInRadius(double x, double y, double z) {
      return MathHelper.pointDistanceSpace(x, y, z, this.focalPoint.getX(), this.focalPoint.getY(), this.focalPoint.getZ()) <= 32.0;
   }

   public void verifyEntity(Entity e) throws SpellRuntimeException {
      if (e == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else if (ISpellImmune.isImmune(e)) {
         throw new SpellRuntimeException("psi.spellerror.immunetarget");
      }
   }

   public int getTargetSlot() throws SpellRuntimeException {
      int slot = this.targetSlot;
      int cadSlot = PsiAPI.getPlayerCADSlot(this.caster);
      if (cadSlot == -1) {
         throw new SpellRuntimeException("psi.spellerror.nocad");
      } else if (this.customTargetSlot) {
         return this.targetSlot == 36 ? 40 : this.targetSlot % 36;
      } else {
         if (this.shiftTargetSlot) {
            int originSlot = Inventory.isHotbarSlot(cadSlot) ? cadSlot : this.caster.getInventory().selected;
            slot = originSlot + this.targetSlot;
         }

         if (slot == -1) {
            return 40;
         } else {
            if (slot < 0) {
               slot += 10;
            }

            return slot % 9;
         }
      }
   }

   public ItemStack getHarvestTool() throws SpellRuntimeException {
      if (!this.tool.isEmpty()) {
         return this.tool;
      } else {
         ItemStack cad = PsiAPI.getPlayerCAD(this.caster);
         if (cad.isEmpty()) {
            throw new SpellRuntimeException("psi.spellerror.nocad");
         } else {
            return cad;
         }
      }
   }
}
