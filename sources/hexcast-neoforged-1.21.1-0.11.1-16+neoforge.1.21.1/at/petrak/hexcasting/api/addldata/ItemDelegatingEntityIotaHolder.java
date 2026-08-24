package at.petrak.hexcasting.api.addldata;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ItemDelegatingEntityIotaHolder implements ADIotaHolder {
   private final Supplier<ItemStack> stackSupplier;
   private final Consumer<ItemStack> save;

   public ItemDelegatingEntityIotaHolder(Supplier<ItemStack> stackSupplier, Consumer<ItemStack> save) {
      this.stackSupplier = stackSupplier;
      this.save = save;
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag() {
      ADIotaHolder delegate = IXplatAbstractions.INSTANCE.findDataHolder(this.stackSupplier.get());
      return delegate == null ? null : delegate.readIotaTag();
   }

   @Override
   public boolean writeIota(@Nullable Iota datum, boolean simulate) {
      ItemStack stacc = this.stackSupplier.get();
      ADIotaHolder delegate = IXplatAbstractions.INSTANCE.findDataHolder(stacc);
      boolean success = delegate != null && delegate.writeIota(datum, simulate);
      if (success && !simulate) {
         this.save.accept(stacc);
      }

      return success;
   }

   @Override
   public boolean writeable() {
      ADIotaHolder delegate = IXplatAbstractions.INSTANCE.findDataHolder(this.stackSupplier.get());
      return delegate != null && delegate.writeable();
   }

   @Nullable
   @Override
   public Iota readIota(ServerLevel world) {
      ADIotaHolder delegate = IXplatAbstractions.INSTANCE.findDataHolder(this.stackSupplier.get());
      return delegate == null ? null : delegate.readIota(world);
   }

   @Nullable
   @Override
   public Iota emptyIota() {
      ADIotaHolder delegate = IXplatAbstractions.INSTANCE.findDataHolder(this.stackSupplier.get());
      return delegate == null ? null : delegate.emptyIota();
   }

   public static class ToItemEntity extends ItemDelegatingEntityIotaHolder {
      public ToItemEntity(ItemEntity entity) {
         super(entity::getItem, stack -> {
            entity.setItem(ItemStack.EMPTY);
            entity.setItem(stack);
            entity.setUnlimitedLifetime();
         });
      }
   }

   public static class ToItemFrame extends ItemDelegatingEntityIotaHolder {
      public ToItemFrame(ItemFrame entity) {
         super(entity::getItem, entity::setItem);
      }
   }

   public static class ToWallScroll extends ItemDelegatingEntityIotaHolder {
      public ToWallScroll(EntityWallScroll entity) {
         super(() -> entity.scroll.copy(), stack -> {});
      }

      @Override
      public boolean writeIota(@Nullable Iota datum, boolean simulate) {
         return false;
      }

      @Override
      public boolean writeable() {
         return false;
      }
   }
}
