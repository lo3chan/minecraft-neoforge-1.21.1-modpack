package amp_libs.org.bouncycastle.util;

public interface Selector<T> extends Cloneable {
   boolean match(T var1);

   @Override
   Object clone();
}
