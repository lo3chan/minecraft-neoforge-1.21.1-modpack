package at.petrak.hexcasting.api.casting.eval.env;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PackagedItemCastEnv extends PlayerBasedCastEnv {
   protected EvalSound sound = HexEvalSounds.NOTHING;

   public PackagedItemCastEnv(ServerPlayer caster, InteractionHand castingHand) {
      super(caster, castingHand);
   }

   @Override
   public void postExecution(CastResult result) {
      super.postExecution(result);
      if (result.component1() instanceof PatternIota patternIota) {
         MsgNewSpiralPatternsS2C packet = new MsgNewSpiralPatternsS2C(this.caster.getUUID(), List.of(patternIota.getPattern()), 140);
         IXplatAbstractions.INSTANCE.sendPacketToPlayer(this.caster, packet);
         IXplatAbstractions.INSTANCE.sendPacketTracking(this.caster, packet);
      }

      this.sound = this.sound.greaterOf(result.getSound());
   }

   @Override
   public long extractMediaEnvironment(long costLeft) {
      if (this.caster.isCreative()) {
         return 0L;
      } else {
         ItemStack casterStack = this.caster.getItemInHand(this.castingHand);
         ADHexHolder casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(casterStack);
         if (casterHexHolder == null) {
            return costLeft;
         } else {
            boolean canCastFromInv = casterHexHolder.canDrawMediaFromInventory();
            ADMediaHolder casterMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(casterStack);
            if (casterMediaHolder != null) {
               long extracted = casterMediaHolder.withdrawMedia((int)costLeft, false);
               costLeft -= extracted;
            }

            if (canCastFromInv && costLeft > 0L) {
               costLeft = this.extractMediaFromInventory(costLeft, this.canOvercast());
            }

            return costLeft;
         }
      }
   }

   @Override
   public InteractionHand getCastingHand() {
      return this.castingHand;
   }

   @Override
   public FrozenPigment getPigment() {
      ItemStack casterStack = this.caster.getItemInHand(this.castingHand);
      ADHexHolder casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(casterStack);
      if (casterHexHolder == null) {
         return IXplatAbstractions.INSTANCE.getPigment(this.caster);
      } else {
         FrozenPigment hexHolderPigment = casterHexHolder.getPigment();
         return hexHolderPigment != null ? hexHolderPigment : IXplatAbstractions.INSTANCE.getPigment(this.caster);
      }
   }

   public EvalSound getSound() {
      return this.sound;
   }
}
