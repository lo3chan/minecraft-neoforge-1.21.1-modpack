package cc.cosmetica.include.twelvemonkeys.util;

import java.util.Enumeration;
import java.util.Iterator;

public interface TokenIterator extends Iterator<String>, Enumeration<String> {
   boolean hasMoreTokens();

   String nextToken();

   void reset();
}
