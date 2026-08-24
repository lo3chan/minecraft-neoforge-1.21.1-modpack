package amp_libs.org.bouncycastle.util;

import java.util.Iterator;

public interface Iterable<T> extends java.lang.Iterable<T> {
   @Override
   Iterator<T> iterator();
}
