package DistantHorizons.libraries.electronwill.nightconfig.core.concurrent;

import DistantHorizons.libraries.electronwill.nightconfig.core.AbstractCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.AbstractConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.InMemoryCommentedFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.IncompatibleIntermediaryLevelException;
import DistantHorizons.libraries.electronwill.nightconfig.core.NullObject;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingSet;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class StampedConfig implements ConcurrentCommentedConfig {
   private final ConfigFormat<?> configFormat;
   private final Supplier<Map<String, Object>> mapSupplier;
   private Map<String, Object> values;
   private Map<String, String> comments;
   private final StampedLock lock = new StampedLock();
   private final ThreadLocal<StampedConfig.ThreadConfigState> state = ThreadLocal.withInitial(() -> StampedConfig.ThreadConfigState.NORMAL);

   public StampedConfig() {
      this(InMemoryCommentedFormat.defaultInstance(), Config.getDefaultMapCreator(false));
   }

   public StampedConfig(ConfigFormat<?> configFormat, Supplier<Map<String, Object>> mapSupplier) {
      this.configFormat = configFormat;
      this.mapSupplier = mapSupplier;
      this.values = mapSupplier.get();
      this.comments = mapSupplier.get();
   }

   StampedConfig(ConfigFormat<?> configFormat, Supplier<Map<String, Object>> mapSupplier, Map<String, Object> values, Map<String, String> comments) {
      this.configFormat = configFormat;
      this.mapSupplier = mapSupplier;
      this.values = values;
      this.comments = comments;
   }

   public void replaceContentBy(StampedConfig newContent) {
      this.checkStateForNormalOp();
      long stamp = this.lock.writeLock();

      try {
         long otherVS = newContent.lock.writeLock();

         try {
            this.values = newContent.values;
            this.comments = newContent.comments;
            newContent.values = null;
            newContent.comments = null;
         } finally {
            newContent.lock.unlockWrite(otherVS);
         }
      } finally {
         this.lock.unlockWrite(stamp);
      }
   }

   public void replaceContentBy(StampedConfig.Accumulator newContent) {
      this.checkStateForNormalOp();
      long stamp = this.lock.writeLock();

      try {
         newContent.prepareReplacement();
         this.values = newContent.values();
         this.comments = newContent.comments();
         newContent.invalidate();
      } finally {
         this.lock.unlockWrite(stamp);
      }
   }

   public StampedConfig.Accumulator newAccumulator() {
      return new StampedConfig.Accumulator(this.configFormat, this.mapSupplier);
   }

   public StampedConfig.Accumulator newAccumulatorCopy() {
      return (StampedConfig.Accumulator)this.copyValueInAccumulator(this);
   }

   private Object copyValueInAccumulator(Object v) {
      if (v instanceof StampedConfig) {
         StampedConfig stamped = (StampedConfig)v;
         stamped.checkStateForNormalOp();
         long stamp = stamped.lock.readLock();

         StampedConfig.Accumulator var7;
         try {
            Map<String, Object> valuesCopy = this.mapSupplier.get();
            valuesCopy.putAll(stamped.values);
            valuesCopy.replaceAll((k, w) -> this.copyValueInAccumulator(w));
            Map<String, String> commentsCopy = this.mapSupplier.get();
            commentsCopy.putAll(stamped.comments);
            var7 = new StampedConfig.Accumulator(valuesCopy, commentsCopy, this.mapSupplier, this.configFormat);
         } finally {
            stamped.lock.unlockRead(stamp);
         }

         return var7;
      } else if (v instanceof List) {
         List<Object> l = (List<Object>)v;
         List<Object> copy = new ArrayList<>(l);
         copy.replaceAll(elem -> this.copyValueInAccumulator(elem));
         return copy;
      } else {
         return v;
      }
   }

   private <V> V mapLockGet(Map<String, V> map, StampedLock lock, String key) {
      long stamp = lock.tryOptimisticRead();
      V value = map.get(key);
      if (!lock.validate(stamp)) {
         this.checkStateForNormalOp();
         stamp = lock.readLock();

         try {
            value = map.get(key);
         } finally {
            lock.unlockRead(stamp);
         }
      } else {
         assert this.state.get() == StampedConfig.ThreadConfigState.NORMAL : "invalid state "
            + this.state.get()
            + " are you using bulk operations / iterators properly?";
      }

      return value;
   }

   private <V> boolean mapLockContains(Map<String, V> map, StampedLock lock, String key) {
      long stamp = lock.tryOptimisticRead();
      boolean contains = map.containsKey(key);
      if (!lock.validate(stamp)) {
         this.checkStateForNormalOp();
         stamp = lock.readLock();

         try {
            contains = map.containsKey(key);
         } finally {
            lock.unlockRead(stamp);
         }
      }

      assert this.state.get() == StampedConfig.ThreadConfigState.NORMAL : "invalid state "
         + this.state.get()
         + " are you using bulk operations / iterators properly?";

      return contains;
   }

   private <V> V mapLockRemove(Map<String, V> map, StampedLock lock, String key) {
      long stamp = lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = lock.writeLock();
      }

      assert this.state.get() == StampedConfig.ThreadConfigState.NORMAL : "invalid state "
         + this.state.get()
         + " are you using bulk operations / iterators properly?";

      Object var6;
      try {
         var6 = map.remove(key);
      } finally {
         lock.unlockWrite(stamp);
      }

      return (V)var6;
   }

   private <V> V mapLockPut(Map<String, V> map, StampedLock lock, String key, V value) {
      long stamp = lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = lock.writeLock();
      }

      assert this.state.get() == StampedConfig.ThreadConfigState.NORMAL : "invalid state "
         + this.state.get()
         + " are you using bulk operations / iterators properly?";

      Object var7;
      try {
         var7 = map.put(key, value);
      } finally {
         lock.unlockWrite(stamp);
      }

      return (V)var7;
   }

   private <V> V mapLockPutIfAbsent(Map<String, V> map, StampedLock lock, String key, V value) {
      long stamp = lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = lock.writeLock();
      }

      assert this.state.get() == StampedConfig.ThreadConfigState.NORMAL : "invalid state "
         + this.state.get()
         + " are you using bulk operations / iterators properly?";

      Object var7;
      try {
         var7 = map.putIfAbsent(key, value);
      } finally {
         lock.unlockWrite(stamp);
      }

      return (V)var7;
   }

   private StampedConfig getExistingConfig(List<String> configPath, boolean failIfIncompatibleLevel) {
      StampedConfig current = this;

      for (String key : configPath) {
         Object level = this.mapLockGet(current.values, current.lock, key);
         if (level == null) {
            return null;
         }

         if (!(level instanceof StampedConfig)) {
            if (failIfIncompatibleLevel) {
               throw new IncompatibleIntermediaryLevelException(
                  "Cannot get entry with parent path " + configPath + " because of an incompatible intermediary value of type: " + level.getClass()
               );
            }

            return null;
         }

         current = (StampedConfig)level;
      }

      return current;
   }

   private StampedConfig getOrCreateConfig(List<String> configPath) {
      assert this.state.get() == StampedConfig.ThreadConfigState.NORMAL : "invalid state "
         + this.state.get()
         + " are you using bulk operations / iterators properly?";

      StampedConfig current = this;

      for (String key : configPath) {
         StampedLock lock = current.lock;
         Map<String, Object> values = current.values;
         long stamp = lock.tryOptimisticRead();
         boolean isLock = false;

         try {
            Object level = values.get(key);
            if (!lock.validate(stamp)) {
               this.checkStateForNormalOp();
               stamp = lock.readLock();
               isLock = true;
               level = values.get(key);
            }

            if (level == null) {
               stamp = lock.tryConvertToWriteLock(stamp);
               if (stamp == 0L) {
                  this.checkStateForNormalOp();
                  stamp = lock.writeLock();
               }

               isLock = true;
               current = this.createSubConfig();
               values.put(key, current);
            } else {
               if (!(level instanceof StampedConfig)) {
                  throw new IncompatibleIntermediaryLevelException(
                     "Cannot get/create entry with parent path " + configPath + " because of an incompatible intermediary value of type: " + level.getClass()
                  );
               }

               current = (StampedConfig)level;
            }
         } finally {
            if (isLock) {
               lock.unlock(stamp);
            }
         }
      }

      return current;
   }

   @Override
   public int size() {
      long stamp = this.lock.tryOptimisticRead();
      int size = this.values.size();
      if (!this.lock.validate(stamp)) {
         this.checkStateForNormalOp();
         stamp = this.lock.readLock();

         try {
            size = this.values.size();
         } finally {
            this.lock.unlockRead(stamp);
         }
      }

      return size;
   }

   public StampedConfig createSubConfig() {
      return new StampedConfig(this.configFormat, this.mapSupplier);
   }

   @Override
   public ConfigFormat<?> configFormat() {
      return this.configFormat;
   }

   @Override
   public Map<String, Object> valueMap() {
      return new StampedConfig.ValueMap(this);
   }

   @Override
   public void clear() {
      long stamp = this.lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = this.lock.writeLock();
      }

      try {
         this.values.clear();
      } finally {
         this.lock.unlockWrite(stamp);
      }
   }

   @Override
   public <T> T getRaw(List<String> path) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockGet((Map<String, T>)this.values, this.lock, path.get(0));
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getExistingConfig(parentPath, false);
            return parent == null ? null : this.mapLockGet((Map<String, T>)parent.values, parent.lock, path.get(lastIndex));
      }
   }

   @Override
   public boolean contains(List<String> path) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockContains(this.values, this.lock, path.get(0));
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getExistingConfig(parentPath, false);
            return parent != null && this.mapLockContains(parent.values, parent.lock, path.get(lastIndex));
      }
   }

   @Override
   public boolean add(List<String> path, Object value) {
      Object nnValue = value == null ? NullObject.NULL_OBJECT : value;
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockPutIfAbsent(this.values, this.lock, path.get(0), nnValue) == null;
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getOrCreateConfig(parentPath);
            Object prev = this.mapLockPutIfAbsent(parent.values, parent.lock, path.get(lastIndex), nnValue);
            return prev == null;
      }
   }

   @Override
   public <T> T remove(List<String> path) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockRemove((Map<String, T>)this.values, this.lock, path.get(0));
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getExistingConfig(parentPath, false);
            return parent == null ? null : this.mapLockRemove((Map<String, T>)parent.values, parent.lock, path.get(lastIndex));
      }
   }

   @Override
   public <T> T set(List<String> path, Object value) {
      Object nnValue = value == null ? NullObject.NULL_OBJECT : value;
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockPut((Map<String, T>)this.values, this.lock, path.get(0), (T)nnValue);
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getOrCreateConfig(parentPath);
            return this.mapLockPut((Map<String, T>)parent.values, parent.lock, path.get(lastIndex), (T)nnValue);
      }
   }

   private void convertSubConfigs(Config c) {
      if (c instanceof AbstractConfig) {
         AbstractConfig conf = (AbstractConfig)c;

         try {
            conf.valueMap().replaceAll((k, v) -> this.convertValue(v));
         } catch (UnsupportedOperationException var6) {
            conf.entrySet().forEach(entryx -> entryx.setValue(this.convertValue(entryx.getRawValue())));
         }
      } else {
         for (Config.Entry entry : c.entrySet()) {
            Object value = entry.getRawValue();
            Object converted = this.convertValue(value);
            if (value != converted) {
               entry.setValue(converted);
            }
         }
      }
   }

   private Object convertValue(Object v) {
      if (v instanceof StampedConfig) {
         return v;
      } else if (v instanceof Config) {
         Config c = (Config)v;
         StampedConfig converted = this.createSubConfig();
         this.convertSubConfigs(c);
         converted.putAll(c);
         if (c instanceof CommentedConfig) {
            converted.putAllComments((CommentedConfig)c);
         }

         return converted;
      } else if (v instanceof List) {
         List<Object> l = (List<Object>)v;
         l.replaceAll(elem -> this.convertValue(elem));
         return l;
      } else {
         return v;
      }
   }

   @Override
   public void putAll(UnmodifiableConfig other) {
      long stamp = this.lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = this.lock.writeLock();
      }

      try {
         this.unsafePutAll(other);
      } finally {
         this.lock.unlockWrite(stamp);
      }
   }

   private void unsafePutAll(UnmodifiableConfig other) {
      if (other == this) {
         throw new IllegalArgumentException("I cannot putAll() into myself.");
      } else {
         if (other instanceof StampedConfig) {
            StampedConfig stamped = (StampedConfig)other;
            long stamp = stamped.lock.tryReadLock();
            if (stamp == 0L) {
               stamped.checkStateForNormalOp();
               stamp = stamped.lock.readLock();
            }

            try {
               this.values.putAll(stamped.values);
            } finally {
               stamped.lock.unlockRead(stamp);
            }
         } else {
            this.convertSubConfigs((Config)other);
            other.entrySet().forEach(entry -> this.values.put(entry.getKey(), entry.getRawValue()));
         }
      }
   }

   private void unsafeRemoveAll(UnmodifiableConfig other) {
      if (other == this) {
         throw new IllegalArgumentException("I cannot removeAll() from myself.");
      } else {
         if (other instanceof StampedConfig) {
            StampedConfig stamped = (StampedConfig)other;
            long stamp = stamped.lock.tryReadLock();
            if (stamp == 0L) {
               stamped.checkStateForNormalOp();
               stamp = stamped.lock.readLock();
            }

            try {
               this.values.keySet().removeAll(stamped.values.keySet());
            } finally {
               stamped.lock.unlockRead(stamp);
            }
         } else {
            try {
               Set<String> values = other.valueMap().keySet();
               this.values.keySet().removeAll(values);
            } catch (UnsupportedOperationException var8) {
               other.entrySet().forEach(entry -> this.values.remove(entry.getKey()));
            }
         }
      }
   }

   @Override
   public void removeAll(UnmodifiableConfig other) {
      long stamp = this.lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = this.lock.writeLock();
      }

      try {
         this.unsafeRemoveAll(other);
      } finally {
         this.lock.unlockWrite(stamp);
      }
   }

   @Override
   public void clearComments() {
      this.checkStateForNormalOp();
      this.bulkCommentedUpdate(view -> view.clearComments());
   }

   @Override
   public String removeComment(List<String> path) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockRemove(this.comments, this.lock, path.get(0));
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getExistingConfig(parentPath, false);
            return parent == null ? null : this.mapLockRemove(parent.comments, parent.lock, path.get(lastIndex));
      }
   }

   @Override
   public String setComment(List<String> path, String value) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockPut(this.comments, this.lock, path.get(0), value);
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getOrCreateConfig(parentPath);
            return this.mapLockPut(parent.comments, parent.lock, path.get(lastIndex), value);
      }
   }

   @Override
   public boolean containsComment(List<String> path) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockContains(this.comments, this.lock, path.get(0));
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getExistingConfig(parentPath, false);
            return parent != null && this.mapLockContains(parent.comments, parent.lock, path.get(lastIndex));
      }
   }

   @Override
   public String getComment(List<String> path) {
      switch (path.size()) {
         case 0:
            throw new IllegalArgumentException("empty entry path");
         case 1:
            return this.mapLockGet(this.comments, this.lock, path.get(0));
         default:
            int lastIndex = path.size() - 1;
            List<String> parentPath = path.subList(0, lastIndex);
            StampedConfig parent = this.getExistingConfig(parentPath, false);
            return parent == null ? null : this.mapLockGet(parent.comments, parent.lock, path.get(lastIndex));
      }
   }

   @Override
   public Map<String, String> commentMap() {
      throw new UnsupportedOperationException("StampedConfig does not support commentMap() yet, please use entrySet() instead.");
   }

   @Override
   public void putAllComments(UnmodifiableCommentedConfig other) {
      if (other == this) {
         throw new IllegalArgumentException("I cannot putAllComments() into myself.");
      } else {
         this.bulkUpdate(view -> {
            if (other instanceof StampedConfig) {
               StampedConfig otherStamped = (StampedConfig)other;
               long otherStamp = otherStamped.lock.tryReadLock();
               if (otherStamp == 0L) {
                  otherStamped.checkStateForNormalOp();
                  otherStamp = otherStamped.lock.readLock();
               }

               try {
                  this.comments.putAll(otherStamped.comments);

                  for (CommentedConfig.Entry entry : otherStamped.entrySet()) {
                     Object value = entry.getRawValue();
                     if (value instanceof StampedConfig) {
                        Object config = this.values.get(entry.getKey());
                        if (config instanceof StampedConfig && config != value) {
                           ((StampedConfig)config).putAllComments((StampedConfig)value);
                        }
                     }
                  }
               } finally {
                  otherStamped.lock.unlockRead(otherStamp);
               }
            } else {
               try {
                  Map<String, String> comments = other.commentMap();
                  this.comments.putAll(comments);

                  for (UnmodifiableCommentedConfig.Entry entryx : other.entrySet()) {
                     Object value = entryx.getRawValue();
                     if (value instanceof UnmodifiableCommentedConfig) {
                        Object config = this.values.get(entryx.getKey());
                        if (config instanceof StampedConfig && config != value) {
                           ((StampedConfig)config).putAllComments((UnmodifiableCommentedConfig)value);
                        }
                     }
                  }
               } catch (UnsupportedOperationException var13) {
                  other.entrySet().forEach(entryxx -> {
                     this.comments.put(entryxx.getKey(), entryxx.getComment());
                     Object valuex = entryxx.getRawValue();
                     if (valuex instanceof UnmodifiableCommentedConfig) {
                        Object configx = this.values.get(entryxx.getKey());
                        if (configx instanceof StampedConfig) {
                           ((StampedConfig)configx).putAllComments((UnmodifiableCommentedConfig)valuex);
                        }
                     }
                  });
               }
            }
         });
      }
   }

   @Override
   public void putAllComments(Map<String, UnmodifiableCommentedConfig.CommentNode> comments) {
      long stamp = this.lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForNormalOp();
         stamp = this.lock.writeLock();
      }

      try {
         comments.forEach((key, node) -> {
            this.comments.put(key, node.getComment());
            Map<String, UnmodifiableCommentedConfig.CommentNode> children = node.getChildren();
            if (children != null) {
               Object config = this.values.get(key);
               if (config instanceof StampedConfig) {
                  ((StampedConfig)config).putAllComments(children);
               }
            }
         });
      } finally {
         this.lock.unlockWrite(stamp);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj instanceof StampedConfig) {
         return this.bulkCommentedRead(view -> ((StampedConfig)obj).bulkCommentedRead(objView -> view.equals(objView)));
      } else {
         return obj instanceof UnmodifiableConfig ? this.bulkRead(view -> view.equals(obj)) : false;
      }
   }

   @Override
   public String toString() {
      return this.bulkRead(view -> {
         StringBuilder builder = new StringBuilder();
         builder.append("StampedConfig{");

         for (UnmodifiableConfig.Entry entry : view.entrySet()) {
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(String.valueOf(entry.getRawValue()));
            builder.append(", ");
         }

         builder.append('}');
         return builder.toString();
      });
   }

   @Override
   public Set<? extends CommentedConfig.Entry> entrySet() {
      return new StampedConfig.EntrySet();
   }

   private void checkStateForBulkOp() {
      switch ((StampedConfig.ThreadConfigState)this.state.get()) {
         case IN_BULK_OP:
            throw new IllegalStateException("StampedConfig.{bulkRead, bulkUpdate, bulkCommentedRead, bulkCommentedUpdate} cannot be nested.");
         case IN_ITER_OP:
            throw new IllegalStateException(
               "Entries provided by StampedConfig.entrySet() cannot be used during another operation on the config nor on its entrySet, for thread-safety reasons (and to avoid deadlocks)."
            );
         case CONSUMED:
            throw new IllegalStateException("This StampedConfig has been given to otherConfig.replaceContentBy() and cannot be used anymore.");
         case NORMAL:
      }
   }

   private void checkStateForNormalOp() {
      switch ((StampedConfig.ThreadConfigState)this.state.get()) {
         case IN_BULK_OP:
            throw new IllegalStateException(
               "StampedConfig cannot be used inside of bulk operations, you must use the argument provided to your function by bulk, for example: bulkUpdate(bulkedConf -> {/* use bulkedConf here*/})."
            );
         case IN_ITER_OP:
            throw new IllegalStateException(
               "Entries provided by StampedConfig.entrySet() cannot be used during another operation on the config nor on its entrySet, for thread-safety reasons (and to avoid deadlocks)."
            );
         case CONSUMED:
            throw new IllegalStateException("This StampedConfig has been given to otherConfig.replaceContentBy() and cannot be used anymore.");
         case NORMAL:
      }
   }

   @Override
   public <R> R bulkRead(Function<? super UnmodifiableConfig, R> action) {
      long stamp = this.lock.tryReadLock();
      if (stamp == 0L) {
         this.checkStateForBulkOp();
         stamp = this.lock.readLock();
      }

      try {
         this.checkStateForBulkOp();
      } catch (IllegalStateException var10) {
         this.lock.unlockRead(stamp);
         throw var10;
      }

      this.state.set(StampedConfig.ThreadConfigState.IN_BULK_OP);
      StampedConfig.ReadOnlyLockedView view = new StampedConfig.ReadOnlyLockedView();

      Object var5;
      try {
         var5 = action.apply(view);
      } finally {
         view.invalidate();
         this.state.set(StampedConfig.ThreadConfigState.NORMAL);
         this.lock.unlockRead(stamp);
      }

      return (R)var5;
   }

   @Override
   public <R> R bulkUpdate(Function<? super Config, R> action) {
      long stamp = this.lock.tryWriteLock();
      if (stamp == 0L) {
         this.checkStateForBulkOp();
         stamp = this.lock.writeLock();
      }

      try {
         this.checkStateForBulkOp();
      } catch (IllegalStateException var10) {
         this.lock.unlockWrite(stamp);
         throw var10;
      }

      this.state.set(StampedConfig.ThreadConfigState.IN_BULK_OP);
      StampedConfig.WritableLockedView view = new StampedConfig.WritableLockedView();

      Object var5;
      try {
         var5 = action.apply(view);
      } finally {
         view.invalidate();
         this.state.set(StampedConfig.ThreadConfigState.NORMAL);
         this.lock.unlockWrite(stamp);
      }

      return (R)var5;
   }

   @Override
   public <R> R bulkCommentedRead(Function<? super UnmodifiableCommentedConfig, R> action) {
      return this.bulkRead(action);
   }

   @Override
   public <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> action) {
      return this.bulkUpdate(action);
   }

   public static final class Accumulator extends AbstractCommentedConfig {
      private final StampedConfig mirror;
      private boolean valid = true;

      Accumulator(Map<String, Object> values, Map<String, String> comments, Supplier<Map<String, Object>> mapSupplier, ConfigFormat<?> configFormat) {
         super(values, comments);
         this.mirror = new StampedConfig(configFormat, mapSupplier, values, comments);
      }

      Accumulator(ConfigFormat<?> configFormat, Supplier<Map<String, Object>> mapSupplier) {
         super(mapSupplier);
         this.mirror = new StampedConfig(configFormat, mapSupplier, this.map, this.commentMap);
      }

      private void checkValid() {
         if (!this.valid) {
            throw new IllegalStateException("This StampedConfig.Accumulator is no longer valid after a call to replaceContentBy().");
         }
      }

      void invalidate() {
         this.valid = false;
      }

      Map<String, Object> values() {
         return this.map;
      }

      Map<String, String> comments() {
         return this.commentMap;
      }

      Supplier<Map<String, Object>> mapSupplier() {
         return this.mapCreator;
      }

      void prepareReplacement() {
         this.checkValid();
         this.map.replaceAll((k, v) -> this.replaceValue(v));
      }

      private Object replaceValue(Object v) {
         if (v instanceof StampedConfig.Accumulator) {
            StampedConfig.Accumulator acc = (StampedConfig.Accumulator)v;
            acc.prepareReplacement();
            return acc.mirror;
         } else if (v instanceof UnmodifiableConfig) {
            throw new IllegalStateException(
               "Invalid sub-configuration of type "
                  + v.getClass().getSimpleName()
                  + " in the Accumulator. Sub-configurations must always be created with createSubConfig()."
            );
         } else if (v instanceof List) {
            List<?> l = (List<?>)v;
            List<Object> newList = new ArrayList<>((Collection<? extends Object>)l);
            newList.replaceAll(elem -> this.replaceValue(elem));
            return newList;
         } else {
            return v;
         }
      }

      @Override
      public AbstractCommentedConfig clone() {
         StampedConfig.Accumulator copy = new StampedConfig.Accumulator(this.configFormat(), this.mapCreator);
         copy.map.putAll(this.map);
         copy.commentMap.putAll(this.commentMap);
         return copy;
      }

      @Override
      public CommentedConfig createSubConfig() {
         return new StampedConfig.Accumulator(this.configFormat(), this.mapCreator);
      }

      @Override
      public ConfigFormat<?> configFormat() {
         this.checkValid();
         return this.mirror.configFormat();
      }
   }

   private class EntryIterator implements Iterator<StampedConfig.LazyEntry> {
      private final StampedConfig.LazyEntry[] entries;
      private int nextPosition;
      private boolean removed;

      EntryIterator(StampedConfig.LazyEntry[] entries) {
         this.entries = entries;
      }

      @Override
      public boolean hasNext() {
         return this.nextPosition < this.entries.length;
      }

      public StampedConfig.LazyEntry next() {
         this.removed = false;
         return this.entries[this.nextPosition++];
      }

      @Override
      public void remove() {
         if (this.removed) {
            throw new IllegalStateException("remove() can be called only once per call to next()");
         } else if (this.nextPosition == 0) {
            throw new IllegalStateException("next() must be called before remove()");
         } else if (this.nextPosition - 1 >= this.entries.length) {
            throw new IllegalStateException("No more elements in this iterator");
         } else {
            this.removed = true;
            StampedConfig.LazyEntry entry = this.entries[this.nextPosition - 1];
            StampedConfig.this.remove(Collections.singletonList(entry.key));
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super StampedConfig.LazyEntry> action) {
         long stamp = StampedConfig.this.lock.tryWriteLock();
         if (stamp == 0L) {
            StampedConfig.this.checkStateForNormalOp();
            stamp = StampedConfig.this.lock.writeLock();
         }

         try {
            StampedConfig.this.state.set(StampedConfig.ThreadConfigState.IN_ITER_OP);

            for (int i = this.nextPosition; i < this.entries.length; i++) {
               StampedConfig.LazyEntry entry = this.entries[i];
               StampedConfig.InLockLazyEntry inLockEntry;
               if (entry instanceof StampedConfig.InLockLazyEntry) {
                  inLockEntry = (StampedConfig.InLockLazyEntry)entry;
               } else {
                  inLockEntry = StampedConfig.this.new InLockLazyEntry(entry.key);
               }

               try {
                  action.accept(inLockEntry);
               } finally {
                  inLockEntry.invalidate();
               }
            }
         } finally {
            StampedConfig.this.state.set(StampedConfig.ThreadConfigState.NORMAL);
            StampedConfig.this.lock.unlockWrite(stamp);
         }
      }
   }

   private class EntrySet extends AbstractCollection<StampedConfig.LazyEntry> implements Set<StampedConfig.LazyEntry> {
      private EntrySet() {
      }

      @Override
      public Iterator<StampedConfig.LazyEntry> iterator() {
         StampedConfig.this.checkStateForNormalOp();
         long stamp = StampedConfig.this.lock.readLock();

         StampedConfig.EntryIterator var10;
         try {
            StampedConfig.this.state.set(StampedConfig.ThreadConfigState.IN_ITER_OP);
            StampedConfig.LazyEntry[] snapshot = new StampedConfig.LazyEntry[StampedConfig.this.values.size()];
            int i = 0;

            for (Map.Entry<String, Object> entry : StampedConfig.this.values.entrySet()) {
               snapshot[i++] = StampedConfig.this.new LockingLazyEntry(entry.getKey(), this);
            }

            var10 = StampedConfig.this.new EntryIterator(snapshot);
         } finally {
            StampedConfig.this.state.set(StampedConfig.ThreadConfigState.NORMAL);
            StampedConfig.this.lock.unlockRead(stamp);
         }

         return var10;
      }

      @Override
      public int size() {
         return StampedConfig.this.size();
      }

      @Override
      public void forEach(Consumer<? super StampedConfig.LazyEntry> action) {
         long stamp = StampedConfig.this.lock.tryWriteLock();
         if (stamp == 0L) {
            StampedConfig.this.checkStateForNormalOp();
            stamp = StampedConfig.this.lock.writeLock();
         }

         try {
            StampedConfig.this.state.set(StampedConfig.ThreadConfigState.IN_ITER_OP);
            StampedConfig.this.values.forEach((key, value) -> {
               StampedConfig.InLockLazyEntry entry = StampedConfig.this.new InLockLazyEntry(key);

               try {
                  action.accept(entry);
               } finally {
                  entry.invalidate();
               }
            });
         } finally {
            StampedConfig.this.state.set(StampedConfig.ThreadConfigState.NORMAL);
            StampedConfig.this.lock.unlockWrite(stamp);
         }
      }

      public boolean add(StampedConfig.LazyEntry e) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         StampedConfig.this.bulkCommentedUpdate(view -> {
            view.clear();
            view.clearComments();
         });
      }

      @Override
      public boolean contains(Object o) {
         if (o instanceof UnmodifiableConfig.Entry) {
            UnmodifiableConfig.Entry entry = (UnmodifiableConfig.Entry)o;
            Object entryValue = entry.getRawValue();
            Object value = StampedConfig.this.getRaw(Collections.singletonList(entry.getKey()));
            return entryValue == null ? value == null : entryValue.equals(value);
         } else {
            return false;
         }
      }

      @Override
      public boolean isEmpty() {
         return StampedConfig.this.isEmpty();
      }

      @Override
      public boolean remove(Object o) {
         throw new UnsupportedOperationException();
      }
   }

   private final class InLockLazyEntry extends StampedConfig.LazyEntry {
      private volatile boolean valid = true;

      private void checkValid() {
         if (!this.valid) {
            throw new IllegalStateException(
               "Entries provided by StampedConfig.entrySet().forEach() are only valid in the scope of the forEach call, for thread-safety reasons (and to avoid deadlocks)."
            );
         }
      }

      void invalidate() {
         this.valid = false;
      }

      protected InLockLazyEntry(String key) {
         super(key);
      }

      @Override
      public String removeComment() {
         this.checkValid();
         return StampedConfig.this.comments.remove(this.key);
      }

      @Override
      public String setComment(String comment) {
         this.checkValid();
         return StampedConfig.this.comments.put(this.key, comment);
      }

      @Override
      public <T> T setValue(Object value) {
         this.checkValid();
         return (T)StampedConfig.this.values.put(this.key, value);
      }

      @Override
      public String getKey() {
         this.checkValid();
         return this.key;
      }

      @Override
      public <T> T getRawValue() {
         this.checkValid();
         return (T)StampedConfig.this.values.get(this.key);
      }

      @Override
      public String getComment() {
         this.checkValid();
         return StampedConfig.this.comments.get(this.key);
      }

      @Override
      public String toString() {
         this.checkValid();
         return "StampedConfig.InLockLazyEntry{key=\"" + this.key + "\"}";
      }
   }

   private abstract class LazyEntry implements CommentedConfig.Entry {
      protected final String key;

      protected LazyEntry(String key) {
         this.key = key;
      }
   }

   private final class LockingLazyEntry extends StampedConfig.LazyEntry {
      protected LockingLazyEntry(String key, StampedConfig.EntrySet set) {
         super(key);
      }

      @Override
      public String removeComment() {
         return StampedConfig.this.mapLockRemove(StampedConfig.this.comments, StampedConfig.this.lock, this.key);
      }

      @Override
      public String setComment(String comment) {
         return StampedConfig.this.mapLockPut(StampedConfig.this.comments, StampedConfig.this.lock, this.key, comment);
      }

      @Override
      public <T> T setValue(Object value) {
         return StampedConfig.this.mapLockPut((Map<String, T>)StampedConfig.this.values, StampedConfig.this.lock, this.key, (T)value);
      }

      @Override
      public String getKey() {
         StampedConfig.this.checkStateForNormalOp();
         return this.key;
      }

      @Override
      public <T> T getRawValue() {
         return StampedConfig.this.mapLockGet((Map<String, T>)StampedConfig.this.values, StampedConfig.this.lock, this.key);
      }

      @Override
      public String getComment() {
         return StampedConfig.this.mapLockGet(StampedConfig.this.comments, StampedConfig.this.lock, this.key);
      }

      @Override
      public String toString() {
         return "StampedConfig.LockingLazyEntry{key=\"" + this.key + "\"}";
      }
   }

   private class ReadOnlyLockedView implements UnmodifiableCommentedConfig {
      private final AtomicBoolean valid;

      ReadOnlyLockedView() {
         this(new AtomicBoolean(true));
      }

      ReadOnlyLockedView(AtomicBoolean valid) {
         this.valid = valid;
      }

      void invalidate() {
         this.valid.set(false);
      }

      protected void checkValid() {
         if (!this.valid.get()) {
            throw new IllegalStateException(
               "View provided by bulk operations are only valid in the scope of the bulkRead or bulkWrite method.To use the config elsewhere, use the actual config variable (not the one provided to your bulk action)."
            );
         }
      }

      @Override
      public Map<String, String> commentMap() {
         throw new UnsupportedOperationException(
            "The view provided by bulk operations on StampedConfig does not support commentMap(), please use entrySet() instead."
         );
      }

      @Override
      public Set<? extends UnmodifiableCommentedConfig.Entry> entrySet() {
         this.checkValid();
         Set<StampedConfig.ReadOnlyLockedView.Entry> set = new TransformingSet<>(StampedConfig.this.values.entrySet(), r -> {
            this.checkValid();
            return new StampedConfig.ReadOnlyLockedView.Entry((Map.Entry<String, Object>)r);
         }, w -> {
            this.checkValid();
            return null;
         }, s -> {
            this.checkValid();
            return s instanceof Map.Entry ? new StampedConfig.ReadOnlyLockedView.Entry((Map.Entry<String, Object>)s) : s;
         });
         return Collections.unmodifiableSet(set);
      }

      @Override
      public boolean containsComment(List<String> path) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1:
               String key = path.get(0);
               return StampedConfig.this.comments.containsKey(key);
            default:
               Object maybeParent = StampedConfig.this.values.get(path.get(0));
               if (maybeParent instanceof StampedConfig) {
                  StampedConfig parent = (StampedConfig)maybeParent;
                  return parent.containsComment(path.subList(1, path.size()));
               } else {
                  return false;
               }
         }
      }

      @Override
      public String getComment(List<String> path) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1:
               String key = path.get(0);
               return StampedConfig.this.comments.get(key);
            default:
               Object maybeParent = StampedConfig.this.values.get(path.get(0));
               if (maybeParent instanceof StampedConfig) {
                  StampedConfig parent = (StampedConfig)maybeParent;
                  return parent.getComment(path.subList(1, path.size()));
               } else {
                  return null;
               }
         }
      }

      @Override
      public ConfigFormat<?> configFormat() {
         this.checkValid();
         return StampedConfig.this.configFormat;
      }

      @Override
      public boolean contains(List<String> path) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1:
               String key = path.get(0);
               return StampedConfig.this.values.containsKey(key);
            default:
               Object maybeParent = StampedConfig.this.values.get(path.get(0));
               if (maybeParent instanceof StampedConfig) {
                  StampedConfig parent = (StampedConfig)maybeParent;
                  return parent.contains(path.subList(1, path.size()));
               } else {
                  return false;
               }
         }
      }

      @Override
      public <T> T getRaw(List<String> path) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1:
               String key = path.get(0);
               return (T)StampedConfig.this.values.get(key);
            default:
               Object maybeParent = StampedConfig.this.values.get(path.get(0));
               if (maybeParent instanceof StampedConfig) {
                  StampedConfig parent = (StampedConfig)maybeParent;
                  return parent.getRaw(path.subList(1, path.size()));
               } else {
                  return null;
               }
         }
      }

      @Override
      public int size() {
         this.checkValid();
         return StampedConfig.this.values.size();
      }

      @Override
      public Map<String, Object> valueMap() {
         StampedConfig.WritableLockedView writable = StampedConfig.this.new WritableLockedView(this.valid);
         return Collections.unmodifiableMap(writable.valueMap());
      }

      @Override
      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append("StampedConfig#LockedView{");

         for (UnmodifiableCommentedConfig.Entry entry : this.entrySet()) {
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(String.valueOf(entry.getRawValue()));
            builder.append(", ");
         }

         builder.append("}");
         return builder.toString();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof UnmodifiableConfig)) {
            return false;
         } else {
            UnmodifiableConfig conf = (UnmodifiableConfig)obj;
            if (conf.size() != this.size()) {
               return false;
            } else {
               for (UnmodifiableConfig.Entry entry : this.entrySet()) {
                  Object value = entry.getValue();
                  Object otherEntry = conf.get(Collections.singletonList(entry.getKey()));
                  if (value == null) {
                     if (otherEntry != null) {
                        return false;
                     }
                  } else if (!value.equals(otherEntry)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      protected class Entry implements UnmodifiableCommentedConfig.Entry {
         protected final Map.Entry<String, Object> mapEntry;

         Entry(Map.Entry<String, Object> entry) {
            this.mapEntry = entry;
            ReadOnlyLockedView.this.checkValid();
         }

         @Override
         public String getComment() {
            ReadOnlyLockedView.this.checkValid();
            return StampedConfig.this.comments.get(this.mapEntry.getKey());
         }

         @Override
         public String getKey() {
            ReadOnlyLockedView.this.checkValid();
            return this.mapEntry.getKey();
         }

         @Override
         public <T> T getRawValue() {
            ReadOnlyLockedView.this.checkValid();
            return (T)this.mapEntry.getValue();
         }
      }
   }

   private static enum ThreadConfigState {
      NORMAL,
      IN_BULK_OP,
      IN_ITER_OP,
      CONSUMED;
   }

   private static final class ValueMap implements Map<String, Object> {
      private final CommentedConfig config;

      ValueMap(CommentedConfig config) {
         this.config = config;
      }

      @Override
      public void clear() {
         this.config.clear();
      }

      @Override
      public boolean containsKey(Object key) {
         return !(key instanceof String) ? false : this.config.contains(Collections.singletonList((String)key));
      }

      @Override
      public boolean containsValue(Object value) {
         return this.config.entrySet().stream().anyMatch(e -> Objects.equals(value, e.getRawValue()));
      }

      @Override
      public Set<java.util.Map.Entry<String, Object>> entrySet() {
         return new Set<java.util.Map.Entry<String, Object>>() {
            @Override
            public int size() {
               return ValueMap.this.config.size();
            }

            @Override
            public boolean isEmpty() {
               return ValueMap.this.config.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
               if (!(o instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  final java.util.Map.Entry<?, ?> search = (java.util.Map.Entry<?, ?>)o;
                  return !(search.getKey() instanceof String) ? false : ValueMap.this.config.entrySet().contains(new UnmodifiableConfig.Entry() {
                     @Override
                     public String getKey() {
                        return (String)search.getKey();
                     }

                     @Override
                     public <T> T getRawValue() {
                        return (T)search.getValue();
                     }
                  });
               }
            }

            @Override
            public Iterator<java.util.Map.Entry<String, Object>> iterator() {
               final Iterator<? extends Config.Entry> it = ValueMap.this.config.entrySet().iterator();
               return new Iterator<java.util.Map.Entry<String, Object>>() {
                  @Override
                  public boolean hasNext() {
                     return it.hasNext();
                  }

                  public java.util.Map.Entry<String, Object> next() {
                     final Config.Entry entry = it.next();
                     return new java.util.Map.Entry<String, Object>() {
                        public String getKey() {
                           return entry.getKey();
                        }

                        @Override
                        public Object getValue() {
                           return entry.getRawValue();
                        }

                        @Override
                        public Object setValue(Object value) {
                           return entry.setValue(value);
                        }
                     };
                  }

                  @Override
                  public void remove() {
                     it.remove();
                  }
               };
            }

            @Override
            public Object[] toArray() {
               throw new UnsupportedOperationException("Unimplemented method 'toArray'");
            }

            @Override
            public <T> T[] toArray(T[] a) {
               throw new UnsupportedOperationException("Unimplemented method 'toArray'");
            }

            public boolean add(java.util.Map.Entry<String, Object> e) {
               return ValueMap.this.config.add(Collections.singletonList(e.getKey()), e.getValue());
            }

            @Override
            public boolean remove(Object o) {
               if (!(o instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry<?, ?> entry = (java.util.Map.Entry<?, ?>)o;
                  Object key = entry.getKey();
                  return !(key instanceof String) ? false : ValueMap.this.config.remove(Collections.singletonList((String)key)) != null;
               }
            }

            @Override
            public boolean containsAll(Collection<?> c) {
               for (Object o : c) {
                  if (!this.contains(o)) {
                     return false;
                  }
               }

               return true;
            }

            @Override
            public boolean addAll(Collection<? extends java.util.Map.Entry<String, Object>> c) {
               boolean changed = false;

               for (java.util.Map.Entry<String, Object> o : c) {
                  changed |= this.add(o);
               }

               return changed;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
               boolean changed = false;
               Iterator<java.util.Map.Entry<String, Object>> it = this.iterator();

               while (it.hasNext()) {
                  if (!c.contains(it.next())) {
                     it.remove();
                     changed = true;
                  }
               }

               return changed;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
               boolean changed = false;

               for (Object o : c) {
                  changed |= this.remove(o);
               }

               return changed;
            }

            @Override
            public void clear() {
               ValueMap.this.config.clear();
            }
         };
      }

      @Override
      public Object get(Object key) {
         return !(key instanceof String) ? false : this.config.get(Collections.singletonList((String)key));
      }

      @Override
      public boolean isEmpty() {
         return this.config.isEmpty();
      }

      @Override
      public Set<String> keySet() {
         return this.config.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toSet());
      }

      @Override
      public Collection<Object> values() {
         return this.config.entrySet().stream().map(e -> e.getRawValue()).collect(Collectors.toList());
      }

      public Object put(String key, Object value) {
         return this.config.set(Collections.singletonList(key), value);
      }

      @Override
      public void putAll(Map<? extends String, ? extends Object> m) {
         for (java.util.Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            this.config.set(Collections.singletonList(entry.getKey()), entry.getValue());
         }
      }

      @Override
      public Object remove(Object key) {
         return !(key instanceof String) ? null : this.config.remove(Collections.singletonList((String)key));
      }

      @Override
      public int size() {
         return this.config.size();
      }
   }

   private final class WritableLockedView extends StampedConfig.ReadOnlyLockedView implements CommentedConfig {
      WritableLockedView() {
      }

      WritableLockedView(AtomicBoolean valid) {
         super(valid);
      }

      @Override
      public void clear() {
         this.checkValid();
         StampedConfig.this.values.clear();
      }

      @Override
      public void removeAll(UnmodifiableConfig config) {
         this.checkValid();
         StampedConfig.this.unsafeRemoveAll(config);
      }

      @Override
      public void putAll(UnmodifiableConfig other) {
         this.checkValid();
         StampedConfig.this.unsafePutAll(other);
      }

      @Override
      public void clearComments() {
         this.checkValid();
         StampedConfig.this.comments.clear();

         for (Object o : StampedConfig.this.values.values()) {
            if (o instanceof StampedConfig) {
               ((StampedConfig)o).clearComments();
            }
         }
      }

      public StampedConfig createSubConfig() {
         this.checkValid();
         return new StampedConfig(StampedConfig.this.configFormat, StampedConfig.this.mapSupplier);
      }

      @Override
      public Map<String, Object> valueMap() {
         return new StampedConfig.ValueMap(this);
      }

      @Override
      public Set<? extends CommentedConfig.Entry> entrySet() {
         this.checkValid();
         return new TransformingSet<>(StampedConfig.this.values.entrySet(), r -> {
            this.checkValid();
            return new StampedConfig.WritableLockedView.Entry((Map.Entry<String, Object>)r);
         }, w -> {
            this.checkValid();
            return new Map.Entry<String, Object>() {
               public String getKey() {
                  WritableLockedView.this.checkValid();
                  return w.getKey();
               }

               @Override
               public Object getValue() {
                  WritableLockedView.this.checkValid();
                  return w.getRawValue();
               }

               @Override
               public Object setValue(Object value) {
                  WritableLockedView.this.checkValid();
                  return w.setValue(value);
               }
            };
         }, s -> {
            this.checkValid();
            return s instanceof Map.Entry ? new StampedConfig.WritableLockedView.Entry((Map.Entry<String, Object>)s) : s;
         });
      }

      @Override
      public <T> T remove(List<String> path) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1:
               String key = path.get(0);
               return (T)StampedConfig.this.values.remove(key);
            default:
               int lastIndex = path.size() - 1;
               Object maybeParent = this.getRaw(path.subList(0, lastIndex));
               if (maybeParent instanceof StampedConfig) {
                  StampedConfig parent = (StampedConfig)maybeParent;
                  String keyx = path.get(lastIndex);
                  return (T)parent.values.remove(keyx);
               } else {
                  return null;
               }
         }
      }

      @Override
      public String removeComment(List<String> path) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1:
               String key = path.get(0);
               return StampedConfig.this.comments.remove(key);
            default:
               int lastIndex = path.size() - 1;
               Object maybeParent = this.getRaw(path.subList(0, lastIndex));
               if (maybeParent instanceof StampedConfig) {
                  StampedConfig parent = (StampedConfig)maybeParent;
                  String keyx = path.get(lastIndex);
                  return parent.comments.remove(keyx);
               } else {
                  return null;
               }
         }
      }

      @Override
      public <T> T set(List<String> path, Object value) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1: {
               String key = path.get(0);
               Object nnValue = value == null ? NullObject.NULL_OBJECT : value;
               return (T)StampedConfig.this.values.put(key, nnValue);
            }
            default: {
               String key = path.get(0);
               List<String> subPath = path.subList(1, path.size());
               Object currentParent = StampedConfig.this.values.get(key);
               if (currentParent == null) {
                  StampedConfig subConfig = this.createSubConfig();
                  StampedConfig.this.values.put(key, subConfig);
                  return subConfig.set(subPath, value);
               } else if (currentParent instanceof StampedConfig) {
                  return ((StampedConfig)currentParent).set(subPath, value);
               } else {
                  throw new IncompatibleIntermediaryLevelException("Cannot add an element to an intermediary value of type: " + currentParent.getClass());
               }
            }
         }
      }

      @Override
      public String setComment(List<String> path, String value) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1: {
               String key = path.get(0);
               return StampedConfig.this.comments.put(key, value);
            }
            default: {
               String key = path.get(0);
               List<String> subPath = path.subList(1, path.size());
               Object currentParent = StampedConfig.this.values.get(key);
               if (currentParent == null) {
                  StampedConfig subConfig = this.createSubConfig();
                  StampedConfig.this.values.put(key, subConfig);
                  return subConfig.setComment(subPath, value);
               } else if (currentParent instanceof StampedConfig) {
                  return ((StampedConfig)currentParent).setComment(subPath, value);
               } else {
                  throw new IncompatibleIntermediaryLevelException("Cannot add a comment to an intermediary value of type: " + currentParent.getClass());
               }
            }
         }
      }

      @Override
      public boolean add(List<String> path, Object value) {
         this.checkValid();
         switch (path.size()) {
            case 0:
               throw new IllegalArgumentException("empty entry path");
            case 1: {
               String key = path.get(0);
               Object nnValue = value == null ? NullObject.NULL_OBJECT : value;
               return StampedConfig.this.values.putIfAbsent(key, nnValue) == null;
            }
            default: {
               String key = path.get(0);
               List<String> subPath = path.subList(1, path.size());
               Object currentParent = StampedConfig.this.values.get(key);
               if (currentParent == null) {
                  StampedConfig subConfig = this.createSubConfig();
                  StampedConfig.this.values.put(key, subConfig);
                  return subConfig.add(subPath, value);
               } else if (currentParent instanceof StampedConfig) {
                  return ((StampedConfig)currentParent).add(subPath, value);
               } else {
                  throw new IncompatibleIntermediaryLevelException("Cannot add an element to an intermediary value of type: " + currentParent.getClass());
               }
            }
         }
      }

      protected class Entry extends StampedConfig.ReadOnlyLockedView.Entry implements CommentedConfig.Entry {
         Entry(Map.Entry<String, Object> entry) {
            super(entry);
            WritableLockedView.this.checkValid();
         }

         @Override
         public String removeComment() {
            WritableLockedView.this.checkValid();
            return StampedConfig.this.comments.remove(this.mapEntry.getKey());
         }

         @Override
         public String setComment(String comment) {
            WritableLockedView.this.checkValid();
            return StampedConfig.this.comments.put(this.mapEntry.getKey(), comment);
         }

         @Override
         public <T> T setValue(Object value) {
            WritableLockedView.this.checkValid();
            return (T)this.mapEntry.setValue(StampedConfig.this.convertValue(value));
         }
      }
   }
}
