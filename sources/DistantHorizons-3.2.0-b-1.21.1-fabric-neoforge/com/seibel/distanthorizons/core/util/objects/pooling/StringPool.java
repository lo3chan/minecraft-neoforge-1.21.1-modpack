package com.seibel.distanthorizons.core.util.objects.pooling;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

public class StringPool {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final StringPool INSTANCE = new StringPool();
   private final StringPool.TrieNode root = new StringPool.TrieNode();

   private StringPool() {
   }

   public String getPooledString(CharArrayList chars) {
      return this.getPooledString(chars, 0, chars.size());
   }

   public String getPooledString(CharArrayList chars, int offset, int length) {
      if (length == 0) {
         return "";
      } else {
         StringPool.TrieNode currentNode = this.root;

         for (int i = 0; i < length; i++) {
            char c = chars.getChar(offset + i);
            currentNode = currentNode.getOrCreateChild(c);
         }

         return currentNode.getOrSetString(chars, offset, length);
      }
   }

   public void clear() {
      this.root.clear();
   }

   public long approximateSize() {
      return this.root.countStrings();
   }

   private static class TrieNode {
      private volatile ConcurrentHashMap<Character, StringPool.TrieNode> children;
      private volatile String value;
      private final StampedLock lock = new StampedLock();

      private TrieNode() {
      }

      StringPool.TrieNode getOrCreateChild(char inputChar) {
         long stamp = this.lock.tryOptimisticRead();
         ConcurrentHashMap<Character, StringPool.TrieNode> currentChildren = this.children;
         if (stamp != 0L && this.lock.validate(stamp) && currentChildren != null) {
            StringPool.TrieNode child = currentChildren.get(inputChar);
            if (child != null) {
               return child;
            }
         }

         stamp = this.lock.readLock();

         try {
            if (this.children != null) {
               StringPool.TrieNode child = this.children.get(inputChar);
               if (child != null) {
                  return child;
               }
            }

            long writeStamp = this.lock.tryConvertToWriteLock(stamp);
            if (writeStamp == 0L) {
               this.lock.unlockRead(stamp);
               writeStamp = this.lock.writeLock();
            }

            stamp = writeStamp;
            if (this.children == null) {
               this.children = new ConcurrentHashMap<>();
            }

            return this.children.computeIfAbsent(inputChar, newChar -> new StringPool.TrieNode());
         } finally {
            this.lock.unlock(stamp);
         }
      }

      String getOrSetString(CharArrayList chars, int offset, int length) {
         long stamp = this.lock.tryOptimisticRead();
         String currentValue = this.value;
         if (stamp != 0L && this.lock.validate(stamp) && currentValue != null) {
            return currentValue;
         } else {
            stamp = this.lock.readLock();

            String var9;
            try {
               if (this.value != null) {
                  return this.value;
               }

               long writeStamp = this.lock.tryConvertToWriteLock(stamp);
               if (writeStamp == 0L) {
                  this.lock.unlockRead(stamp);
                  writeStamp = this.lock.writeLock();
               }

               stamp = writeStamp;
               if (this.value == null) {
                  this.value = new String(chars.elements(), offset, length);
                  return this.value;
               }

               var9 = this.value;
            } finally {
               this.lock.unlock(stamp);
            }

            return var9;
         }
      }

      void clear() {
         long stamp = this.lock.writeLock();

         try {
            if (this.children != null) {
               this.children.clear();
            }

            this.children = null;
            this.value = null;
         } finally {
            this.lock.unlock(stamp);
         }
      }

      long countStrings() {
         long stamp = this.lock.tryOptimisticRead();
         ConcurrentHashMap<Character, StringPool.TrieNode> currentChildren = this.children;
         String currentValue = this.value;
         if (!this.lock.validate(stamp)) {
            stamp = this.lock.readLock();

            try {
               currentChildren = this.children;
               currentValue = this.value;
            } finally {
               this.lock.unlockRead(stamp);
            }
         }

         long count = currentValue != null ? 1L : 0L;
         if (currentChildren != null) {
            for (StringPool.TrieNode child : currentChildren.values()) {
               count += child.countStrings();
            }
         }

         return count;
      }
   }
}
