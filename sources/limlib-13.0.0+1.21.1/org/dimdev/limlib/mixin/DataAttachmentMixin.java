package org.dimdev.limlib.mixin;

import java.util.function.UnaryOperator;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.dimdev.limlib.util.DataValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({AttachmentType.class})
public abstract class DataAttachmentMixin<T> implements DataValue<T> {
   @Override
   public T get(Object object) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      return (T)(holder == null ? null : holder.getExistingDataOrNull(this.self()));
   }

   @Override
   public T getOrCreate(Object object) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      return (T)(holder == null ? null : holder.getData(this.self()));
   }

   @Override
   public void set(Object object, T value) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      if (holder != null) {
         this.set(holder, value);
      }
   }

   @Override
   public void update(Object object, T defaultValue, UnaryOperator<T> operator) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      if (holder != null) {
         T data = (T)holder.getExistingDataOrNull(this.self());
         this.set(holder, operator.apply(data == null ? defaultValue : data));
      }
   }

   @Override
   public void update(Object object, UnaryOperator<T> operator) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      if (holder != null) {
         this.set(holder, operator.apply((T)holder.getData(this.self())));
      }
   }

   @Override
   public void remove(Object object) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      if (holder != null) {
         holder.removeData(this.self());
      }
   }

   @Override
   public boolean has(Object object) {
      IAttachmentHolder holder = this.asAttachmentHolder(object);
      return holder != null && holder.hasData(this.self());
   }

   @Unique
   private AttachmentType<T> self() {
      return (AttachmentType<T>)this;
   }

   @Unique
   private void set(IAttachmentHolder holder, T value) {
      if (value == null) {
         holder.removeData(this.self());
      } else {
         holder.setData(this.self(), value);
      }
   }

   @Unique
   private IAttachmentHolder asAttachmentHolder(Object object) {
      return object instanceof IAttachmentHolder holder ? holder : null;
   }
}
