package at.petrak.hexcasting.api.casting.eval.env;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.mod.HexStatistics;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexDamageTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerBasedCastEnv extends CastingEnvironment {
   public static final double AMBIT_RADIUS = 32.0;
   public static final double SENTINEL_RADIUS = 16.0;
   protected final ServerPlayer caster;
   protected final InteractionHand castingHand;

   protected PlayerBasedCastEnv(ServerPlayer caster, InteractionHand castingHand) {
      super(caster.serverLevel());
      this.caster = caster;
      this.castingHand = castingHand;
   }

   @Override
   public ServerPlayer getCaster() {
      return this.caster;
   }

   @Override
   public void postExecution(CastResult result) {
      super.postExecution(result);

      for (OperatorSideEffect sideEffect : result.getSideEffects()) {
         if (sideEffect instanceof OperatorSideEffect.DoMishap doMishap) {
            this.sendMishapMsgToPlayer(doMishap);
         }
      }
   }

   @Override
   protected List<ItemStack> getUsableStacks(CastingEnvironment.StackDiscoveryMode mode) {
      return switch (mode) {
         case QUERY -> {
            ArrayList<ItemStack> out = new ArrayList<>();
            ItemStack offhand = this.caster.getItemInHand(HexUtils.otherHand(this.castingHand));
            if (!offhand.isEmpty()) {
               out.add(offhand);
            }

            int anchorSlot = this.castingHand == InteractionHand.MAIN_HAND ? (this.caster.getInventory().selected + 1) % 9 : 0;

            for (int delta = 0; delta < 9; delta++) {
               int slot = (anchorSlot + delta) % 9;
               out.add(this.caster.getInventory().getItem(slot));
            }

            yield out;
         }
         case EXTRACTION -> {
            ArrayList<ItemStack> out = new ArrayList<>();
            Inventory inv = this.caster.getInventory();

            for (int i = inv.items.size() - 1; i >= 0; i--) {
               if (i != inv.selected) {
                  out.add((ItemStack)inv.items.get(i));
               }
            }

            out.addAll(inv.offhand);
            out.add(inv.getSelected());
            yield out;
         }
      };
   }

   @Override
   protected List<CastingEnvironment.HeldItemInfo> getPrimaryStacks() {
      ItemStack primaryItem = this.caster.getItemInHand(this.castingHand);
      if (primaryItem.isEmpty()) {
         primaryItem = ItemStack.EMPTY.copy();
      }

      return List.of(
         new CastingEnvironment.HeldItemInfo(this.getAlternateItem(), this.getOtherHand()), new CastingEnvironment.HeldItemInfo(primaryItem, this.castingHand)
      );
   }

   ItemStack getAlternateItem() {
      InteractionHand otherHand = HexUtils.otherHand(this.castingHand);
      ItemStack stack = this.caster.getItemInHand(otherHand);
      return stack.isEmpty() ? ItemStack.EMPTY.copy() : stack;
   }

   @Override
   public boolean replaceItem(Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable InteractionHand hand) {
      if (this.caster == null) {
         return false;
      } else if (hand != null && stackOk.test(this.caster.getItemInHand(hand))) {
         this.caster.setItemInHand(hand, replaceWith);
         return true;
      } else {
         Inventory inv = this.caster.getInventory();

         for (int i = inv.items.size() - 1; i >= 0; i--) {
            if (i != inv.selected && stackOk.test((ItemStack)inv.items.get(i))) {
               inv.setItem(i, replaceWith);
               return true;
            }
         }

         if (stackOk.test(this.caster.getItemInHand(this.getOtherHand()))) {
            this.caster.setItemInHand(this.getOtherHand(), replaceWith);
            return true;
         } else if (stackOk.test(this.caster.getItemInHand(this.getCastingHand()))) {
            this.caster.setItemInHand(this.getCastingHand(), replaceWith);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean isVecInRangeEnvironment(Vec3 vec) {
      Sentinel sentinel = HexAPI.instance().getSentinel(this.caster);
      return sentinel != null
            && sentinel.extendsRange()
            && this.caster.level().dimension() == sentinel.dimension()
            && vec.distanceToSqr(sentinel.position()) <= 256.0
         ? true
         : vec.distanceToSqr(this.caster.position()) <= 1024.0;
   }

   @Override
   public boolean hasEditPermissionsAtEnvironment(BlockPos pos) {
      return this.caster.gameMode.getGameModeForPlayer() != GameType.ADVENTURE && this.world.mayInteract(this.caster, pos);
   }

   protected long extractMediaFromInventory(long costLeft, boolean allowOvercast) {
      List<ADMediaHolder> sources = MediaHelper.scanPlayerForMediaStuff(this.caster);
      long startCost = costLeft;

      for (ADMediaHolder source : sources) {
         long found = MediaHelper.extractMedia(source, costLeft, false, false);
         costLeft -= found;
         if (costLeft <= 0L) {
            break;
         }
      }

      if (costLeft > 0L && allowOvercast) {
         double mediaToHealth = HexConfig.common().mediaToHealthRate();
         double healthToRemove = Math.max(costLeft / mediaToHealth, 0.5);
         double mediaAbleToCastFromHP = this.caster.getHealth() * mediaToHealth;
         Mishap.trulyHurt(this.caster, HexDamageTypes.source(this.caster.level(), HexDamageTypes.OVERCAST), (float)healthToRemove);
         int actuallyTaken = Mth.ceil(mediaAbleToCastFromHP - this.caster.getHealth() * mediaToHealth);
         HexAdvancementTriggers.OVERCAST_TRIGGER.trigger(this.caster, actuallyTaken);
         this.caster.awardStat(HexStatistics.MEDIA_OVERCAST, actuallyTaken);
         costLeft -= actuallyTaken;
      }

      this.caster.awardStat(HexStatistics.MEDIA_USED, (int)(startCost - costLeft));
      HexAdvancementTriggers.SPEND_MEDIA_TRIGGER.trigger(this.caster, (int)(startCost - costLeft), (int)(costLeft < 0L ? -costLeft : 0L));
      return costLeft;
   }

   protected boolean canOvercast() {
      AdvancementHolder adv = this.world.getServer().getAdvancements().get(HexAPI.modLoc("y_u_no_cast_angy"));
      if (adv == null) {
         return false;
      } else {
         PlayerAdvancements advs = this.caster.getAdvancements();
         return advs.getOrStartProgress(adv).isDone();
      }
   }

   @Nullable
   @Override
   public FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
      return IXplatAbstractions.INSTANCE.setPigment(this.caster, pigment);
   }

   @Override
   public void produceParticles(ParticleSpray particles, FrozenPigment pigment) {
      particles.sprayParticles(this.world, pigment);
   }

   @Override
   public Vec3 mishapSprayPos() {
      return this.caster.position();
   }

   @Override
   public MishapEnvironment getMishapEnvironment() {
      return new PlayerBasedMishapEnv(this.caster);
   }

   protected void sendMishapMsgToPlayer(OperatorSideEffect.DoMishap mishap) {
      Component msg = mishap.getMishap().errorMessageWithName(this, mishap.getErrorCtx());
      if (msg != null) {
         this.caster.sendSystemMessage(msg);
      }
   }

   @Override
   protected boolean isCreativeMode() {
      return this.caster.getAbilities().instabuild;
   }

   @Override
   public void printMessage(Component message) {
      this.caster.sendSystemMessage(message);
   }
}
