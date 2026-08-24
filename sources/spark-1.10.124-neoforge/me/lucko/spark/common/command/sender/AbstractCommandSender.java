package me.lucko.spark.common.command.sender;

public abstract class AbstractCommandSender<S> implements CommandSender {
   protected final S delegate;

   public AbstractCommandSender(S delegate) {
      this.delegate = delegate;
   }

   protected Object getObjectForComparison() {
      return this.delegate;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AbstractCommandSender<?> that = (AbstractCommandSender<?>)o;
         return this.getObjectForComparison().equals(that.getObjectForComparison());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getObjectForComparison().hashCode();
   }
}
