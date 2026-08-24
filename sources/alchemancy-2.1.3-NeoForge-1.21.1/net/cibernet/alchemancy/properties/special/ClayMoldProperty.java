package net.cibernet.alchemancy.properties.special;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;

public class ClayMoldProperty extends Property implements IDataHolder<ItemStack> {
   public static final InnatePropertyItem.Tooltip ITEM_TOOLTIP = (stack, context, tooltipComponents, tooltipFlag) -> {
      ItemStack storedItem = ((ClayMoldProperty)AlchemancyProperties.CLAY_MOLD.value()).getData(stack);
      if (!storedItem.isEmpty()) {
         tooltipComponents.add(
            Component.translatable("item.alchemancy.unshaped_clay.tooltip", new Object[]{storedItem.getHoverName()})
               .withColor(((ClayMoldProperty)AlchemancyProperties.CLAY_MOLD.get()).getColor(stack))
         );
      }
   };
   private static final BlockParticleOption PARTICLE_OPTIONS = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CLAY.defaultBlockState());

   @Override
   public int getColor(ItemStack stack) {
      return 11516374;
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem,
      ItemStack stackedOnItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (carriedItem.is(AlchemancyTags.Items.REPAIRS_UNSHAPED_CLAY)) {
         ItemStack storedItem = repair(this.getData(stackedOnItem));
         stackedOnItem.shrink(1);
         carriedItem.shrink(1);
         if (stackedOnItem.isEmpty()) {
            stackedOnSlot.set(storedItem);
         } else if (carriedItem.isEmpty()) {
            carriedSlot.set(storedItem);
         } else if (!player.addItem(storedItem)) {
            player.drop(storedItem, true);
         }

         playRepairEffects(player);
         isCancelled.set(true);
      }
   }

   public static void playRepairEffects(Entity source) {
      source.level()
         .playSound(null, source.position().x, source.position().y, source.position().z, AlchemancySoundEvents.CLAY_MOLD, SoundSource.PLAYERS, 1.0F, 0.75F);
      if (source.level() instanceof ServerLevel serverLevel) {
         for (int i = 0; i < 20; i++) {
            serverLevel.sendParticles(
               PARTICLE_OPTIONS,
               source.position().x,
               source.getEyeY() - 0.20000000298023224,
               source.position().z,
               1,
               0.0,
               0.0,
               0.0,
               source.getRandom().nextDouble() * 0.25
            );
         }
      }
   }

   public static ItemStack repair(ItemStack storedItem) {
      if (storedItem.isDamageableItem()) {
         storedItem.setDamageValue(Math.min(storedItem.getDamageValue(), (int)(storedItem.getMaxDamage() * 0.8F)));
      }

      return storedItem;
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      return List.of();
   }

   @Override
   public boolean hasJournalEntry() {
      return false;
   }

   public ItemStack readData(CompoundTag tag) {
      return tag.isEmpty() ? this.getDefaultData() : ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(this.getDefaultData());
   }

   public CompoundTag writeData(final ItemStack data) {
      return new CompoundTag() {
         {
            if (!data.isEmpty()) {
               this.put("item", data.save(CommonUtils.registryAccessStatic()));
            }
         }
      };
   }

   public ItemStack getDefaultData() {
      return Items.CLAY_BALL.getDefaultInstance();
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      ItemStack storedStack = this.getData(stack);
      return (Component)(!storedStack.isEmpty()
         ? Component.translatable("property.detail", new Object[]{name, storedStack.getHoverName()}).withColor(this.getColor(stack))
         : name);
   }
}
