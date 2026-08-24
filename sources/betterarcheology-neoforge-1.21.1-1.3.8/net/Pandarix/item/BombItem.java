package net.Pandarix.item;

import java.util.Objects;
import net.Pandarix.BACommon;
import net.Pandarix.entity.BombEntity;
import net.Pandarix.util.ServerPlayerHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BombItem extends Item {
   ResourceLocation ADVANCEMENT_ID = BACommon.createResource("used_bomb_item");

   public BombItem(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
      pLevel.playSound(
         null,
         pPlayer.getX(),
         pPlayer.getY(),
         pPlayer.getZ(),
         SoundEvents.SNOWBALL_THROW,
         SoundSource.NEUTRAL,
         0.5F,
         0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      BombEntity bombEntity = new BombEntity(pLevel, pPlayer);
      pPlayer.getCooldowns().addCooldown(this, 10);
      if (!pLevel.isClientSide()) {
         bombEntity.setItem(itemStack);
         bombEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 0.75F, 1.0F);
         pLevel.addFreshEntity(bombEntity);
         AdvancementHolder advancement = Objects.requireNonNull(pLevel.getServer()).getAdvancements().get(this.ADVANCEMENT_ID);
         if (advancement != null) {
            ServerPlayerHelper.tryGetServerPlayer(pPlayer).ifPresent(sp -> sp.getAdvancements().award(advancement, "criteria"));
         }
      }

      pLevel.playSound(null, bombEntity, SoundEvents.CREEPER_PRIMED, SoundSource.NEUTRAL, 1.0F, (float)pLevel.getRandom().nextDouble() * 0.5F + 0.5F);
      itemStack.shrink(1);
      return InteractionResultHolder.consume(itemStack);
   }
}
