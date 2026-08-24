package me.lucko.spark.common.sampler.async.jfr;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.lucko.spark.common.sampler.async.AsyncStackTraceElement;

public class JfrReader implements Closeable {
   private static final int BUFFER_SIZE = 2097152;
   private static final int CHUNK_HEADER_SIZE = 68;
   private static final int CHUNK_SIGNATURE = 1179406848;
   private static final byte STATE_NEW_CHUNK = 0;
   private static final byte STATE_READING = 1;
   private static final byte STATE_EOF = 2;
   private static final byte STATE_INCOMPLETE = 3;
   private final FileChannel ch;
   private ByteBuffer buf;
   private final long fileSize;
   private long filePosition;
   private byte state;
   public long startNanos = 9223372036854775807L;
   public long endNanos = -9223372036854775808L;
   public long startTicks = 9223372036854775807L;
   public long chunkStartNanos;
   public long chunkEndNanos;
   public long chunkStartTicks;
   public long ticksPerSec;
   public boolean stopAtNewChunk;
   public final Dictionary<JfrReader.JfrClass> types = new Dictionary<>();
   public final Map<String, JfrReader.JfrClass> typesByName = new HashMap<>();
   public final Map<Long, String> threads = new HashMap<>();
   public final Dictionary<JfrReader.ClassRef> classes = new Dictionary<>();
   public final Dictionary<String> strings = new Dictionary<>();
   public final Dictionary<byte[]> symbols = new Dictionary<>();
   public final Dictionary<JfrReader.MethodRef> methods = new Dictionary<>();
   public final Dictionary<JfrReader.StackTrace> stackTraces = new Dictionary<>();
   public final Dictionary<AsyncStackTraceElement> stackFrames = new Dictionary<>();
   public final Map<String, String> settings = new HashMap<>();
   public final Map<String, Map<Integer, String>> enums = new HashMap<>();
   private final Dictionary<Constructor<? extends JfrReader.Event>> customEvents = new Dictionary<>();
   private int executionSample;
   private int nativeMethodSample;
   private int wallClockSample;
   private int allocationInNewTLAB;
   private int allocationOutsideTLAB;
   private int allocationSample;
   private int liveObject;
   private int monitorEnter;
   private int threadPark;
   private int activeSetting;

   public JfrReader(Path path) throws IOException {
      this.ch = FileChannel.open(path, StandardOpenOption.READ);
      this.buf = ByteBuffer.allocateDirect(2097152);
      this.fileSize = this.ch.size();
      ((Buffer)this.buf).flip();
      this.ensureBytes(68);
      if (!this.readChunk(0)) {
         throw new IOException("Incomplete JFR file");
      }
   }

   public JfrReader(ByteBuffer buf) throws IOException {
      this.ch = null;
      this.buf = buf;
      this.fileSize = buf.limit();
      buf.order(ByteOrder.BIG_ENDIAN);
      if (!this.readChunk(0)) {
         throw new IOException("Incomplete JFR file");
      }
   }

   @Override
   public void close() throws IOException {
      if (this.ch != null) {
         this.ch.close();
      }
   }

   public boolean eof() {
      return this.state >= 2;
   }

   public boolean incomplete() {
      return this.state == 3;
   }

   public long durationNanos() {
      return this.endNanos - this.startNanos;
   }

   public <E extends JfrReader.Event> void registerEvent(String name, Class<E> eventClass) {
      JfrReader.JfrClass type = this.typesByName.get(name);
      if (type != null) {
         try {
            this.customEvents.put(type.id, eventClass.getConstructor(JfrReader.class));
         } catch (NoSuchMethodException var5) {
            throw new IllegalArgumentException("No suitable constructor found");
         }
      }
   }

   public boolean hasMoreChunks() throws IOException {
      return this.state == 0 ? this.readChunk(this.buf.position()) : this.state == 1;
   }

   public List<JfrReader.Event> readAllEvents() throws IOException {
      return this.readAllEvents(null);
   }

   public <E extends JfrReader.Event> List<E> readAllEvents(Class<E> cls) throws IOException {
      ArrayList<E> events = new ArrayList<>();

      E event;
      while ((event = this.readEvent(cls)) != null) {
         events.add(event);
      }

      Collections.sort(events);
      return events;
   }

   public JfrReader.Event readEvent() throws IOException {
      return this.readEvent(null);
   }

   public <E extends JfrReader.Event> E readEvent(Class<E> cls) throws IOException {
      while (this.ensureBytes(68)) {
         int pos = this.buf.position();
         int size = this.getVarint();
         int type = this.getVarint();
         if (type == 76 && this.buf.getInt(pos) == 1179406848) {
            if (this.state != 0 && this.stopAtNewChunk) {
               ((Buffer)this.buf).position(pos);
               this.state = 0;
            } else if (this.readChunk(pos)) {
               continue;
            }

            return null;
         } else {
            if (type != this.executionSample && type != this.nativeMethodSample) {
               if (type == this.wallClockSample) {
                  if (cls == null || cls == JfrReader.ExecutionSample.class) {
                     return (E)this.readExecutionSample(true);
                  }
               } else if (type == this.allocationInNewTLAB) {
                  if (cls == null || cls == JfrReader.AllocationSample.class) {
                     return (E)this.readAllocationSample(true);
                  }
               } else if (type != this.allocationOutsideTLAB && type != this.allocationSample) {
                  if (type == this.liveObject) {
                     if (cls == null || cls == JfrReader.LiveObject.class) {
                        return (E)this.readLiveObject();
                     }
                  } else if (type == this.monitorEnter) {
                     if (cls == null || cls == JfrReader.ContendedLock.class) {
                        return (E)this.readContendedLock(false);
                     }
                  } else if (type == this.threadPark) {
                     if (cls == null || cls == JfrReader.ContendedLock.class) {
                        return (E)this.readContendedLock(true);
                     }
                  } else if (type == this.activeSetting) {
                     this.readActiveSetting();
                  } else {
                     Constructor<? extends JfrReader.Event> customEvent = this.customEvents.get(type);
                     if (customEvent != null && (cls == null || cls == customEvent.getDeclaringClass())) {
                        JfrReader.Event e;
                        try {
                           e = customEvent.newInstance(this);
                        } catch (ReflectiveOperationException var10) {
                           throw new IllegalStateException(var10);
                        } finally {
                           this.seek(this.filePosition + pos + size);
                        }

                        return (E)e;
                     }
                  }
               } else if (cls == null || cls == JfrReader.AllocationSample.class) {
                  return (E)this.readAllocationSample(false);
               }
            } else if (cls == null || cls == JfrReader.ExecutionSample.class) {
               return (E)this.readExecutionSample(false);
            }

            this.seek(this.filePosition + pos + size);
         }
      }

      this.state = 2;
      return null;
   }

   private JfrReader.ExecutionSample readExecutionSample(boolean hasSamples) {
      long time = this.getVarlong();
      int tid = this.getVarint();
      int stackTraceId = this.getVarint();
      int threadState = this.getVarint();
      int samples = hasSamples ? this.getVarint() : 1;
      return new JfrReader.ExecutionSample(time, tid, stackTraceId, threadState, samples);
   }

   private JfrReader.AllocationSample readAllocationSample(boolean tlab) {
      long time = this.getVarlong();
      int tid = this.getVarint();
      int stackTraceId = this.getVarint();
      int classId = this.getVarint();
      long allocationSize = this.getVarlong();
      long tlabSize = tlab ? this.getVarlong() : 0L;
      return new JfrReader.AllocationSample(time, tid, stackTraceId, classId, allocationSize, tlabSize);
   }

   private JfrReader.LiveObject readLiveObject() {
      long time = this.getVarlong();
      int tid = this.getVarint();
      int stackTraceId = this.getVarint();
      int classId = this.getVarint();
      long allocationSize = this.getVarlong();
      long allocatimeTime = this.getVarlong();
      return new JfrReader.LiveObject(time, tid, stackTraceId, classId, allocationSize, allocatimeTime);
   }

   private JfrReader.ContendedLock readContendedLock(boolean hasTimeout) {
      long time = this.getVarlong();
      long duration = this.getVarlong();
      int tid = this.getVarint();
      int stackTraceId = this.getVarint();
      int classId = this.getVarint();
      if (hasTimeout) {
         this.getVarlong();
      }

      long until = this.getVarlong();
      long address = this.getVarlong();
      return new JfrReader.ContendedLock(time, tid, stackTraceId, duration, classId);
   }

   private void readActiveSetting() {
      for (JfrReader.JfrField field : this.typesByName.get("jdk.ActiveSetting").fields) {
         this.getVarlong();
         if ("id".equals(field.name)) {
            break;
         }
      }

      String name = this.getString();
      String value = this.getString();
      this.settings.put(name, value);
   }

   private boolean readChunk(int pos) throws IOException {
      if (pos + 68 <= this.buf.limit() && this.buf.getInt(pos) == 1179406848) {
         int version = this.buf.getInt(pos + 4);
         if (version >= 131072 && version <= 196607) {
            long chunkStart = this.filePosition + pos;
            long chunkSize = this.buf.getLong(pos + 8);
            if (chunkStart + chunkSize > this.fileSize) {
               this.state = 3;
               return false;
            } else {
               long cpOffset = this.buf.getLong(pos + 16);
               long metaOffset = this.buf.getLong(pos + 24);
               if (cpOffset != 0L && metaOffset != 0L) {
                  this.chunkStartNanos = this.buf.getLong(pos + 32);
                  this.chunkEndNanos = this.buf.getLong(pos + 32) + this.buf.getLong(pos + 40);
                  this.chunkStartTicks = this.buf.getLong(pos + 48);
                  this.ticksPerSec = this.buf.getLong(pos + 56);
                  this.startNanos = Math.min(this.startNanos, this.chunkStartNanos);
                  this.endNanos = Math.max(this.endNanos, this.chunkEndNanos);
                  this.startTicks = Math.min(this.startTicks, this.chunkStartTicks);
                  this.types.clear();
                  this.typesByName.clear();
                  this.readMeta(chunkStart + metaOffset);
                  this.readConstantPool(chunkStart + cpOffset);
                  this.cacheEventTypes();
                  this.seek(chunkStart + 68L);
                  this.state = 1;
                  return true;
               } else {
                  this.state = 3;
                  return false;
               }
            }
         } else {
            throw new IOException("Unsupported JFR version: " + (version >>> 16) + "." + (version & 65535));
         }
      } else {
         throw new IOException("Not a valid JFR file");
      }
   }

   private void readMeta(long metaOffset) throws IOException {
      this.seek(metaOffset);
      this.ensureBytes(5);
      int posBeforeSize = this.buf.position();
      this.ensureBytes(this.getVarint() - (this.buf.position() - posBeforeSize));
      this.getVarint();
      this.getVarlong();
      this.getVarlong();
      this.getVarlong();
      String[] strings = new String[this.getVarint()];

      for (int i = 0; i < strings.length; i++) {
         strings[i] = this.getString();
      }

      this.readElement(strings);
   }

   private JfrReader.Element readElement(String[] strings) {
      String name = strings[this.getVarint()];
      int attributeCount = this.getVarint();
      Map<String, String> attributes = new HashMap<>(attributeCount);

      for (int i = 0; i < attributeCount; i++) {
         attributes.put(strings[this.getVarint()], strings[this.getVarint()]);
      }

      JfrReader.Element e = this.createElement(name, attributes);
      int childCount = this.getVarint();

      for (int i = 0; i < childCount; i++) {
         e.addChild(this.readElement(strings));
      }

      return e;
   }

   private JfrReader.Element createElement(String name, Map<String, String> attributes) {
      switch (name) {
         case "class":
            JfrReader.JfrClass type = new JfrReader.JfrClass(attributes);
            if (!attributes.containsKey("superType")) {
               this.types.put(type.id, type);
            }

            this.typesByName.put(type.name, type);
            return type;
         case "field":
            return new JfrReader.JfrField(attributes);
         default:
            return new JfrReader.Element();
      }
   }

   private void readConstantPool(long cpOffset) throws IOException {
      long delta;
      do {
         this.seek(cpOffset);
         this.ensureBytes(5);
         int posBeforeSize = this.buf.position();
         this.ensureBytes(this.getVarint() - (this.buf.position() - posBeforeSize));
         this.getVarint();
         this.getVarlong();
         this.getVarlong();
         delta = this.getVarlong();
         this.getVarint();
         int poolCount = this.getVarint();

         for (int i = 0; i < poolCount; i++) {
            int type = this.getVarint();
            this.readConstants(this.types.get(type));
         }
      } while (delta != 0L && (cpOffset += delta) > 0L);
   }

   private void readConstants(JfrReader.JfrClass type) {
      String var2 = type.name;
      switch (var2) {
         case "jdk.types.ChunkHeader":
            ((Buffer)this.buf).position(this.buf.position() + 71);
            break;
         case "java.lang.Thread":
            this.readThreads(type.fields.size());
            break;
         case "java.lang.Class":
            this.readClasses(type.fields.size());
            break;
         case "java.lang.String":
            this.readStrings();
            break;
         case "jdk.types.Symbol":
            this.readSymbols();
            break;
         case "jdk.types.Method":
            this.readMethods();
            break;
         case "jdk.types.StackTrace":
            this.readStackTraces();
            break;
         default:
            if (type.simpleType && type.fields.size() == 1) {
               this.readEnumValues(type.name);
            } else {
               this.readOtherConstants(type.fields);
            }
      }
   }

   private void readThreads(int fieldCount) {
      int count = this.getVarint();

      for (int i = 0; i < count; i++) {
         long id = this.getVarlong();
         String osName = this.getString();
         int osThreadId = this.getVarint();
         String javaName = this.getString();
         long javaThreadId = this.getVarlong();
         this.readFields(fieldCount - 4);
         this.threads.put(id, javaName != null ? javaName : osName);
      }
   }

   private void readClasses(int fieldCount) {
      int count = this.classes.preallocate(this.getVarint());

      for (int i = 0; i < count; i++) {
         long id = this.getVarlong();
         long loader = this.getVarlong();
         long name = this.getVarlong();
         long pkg = this.getVarlong();
         int modifiers = this.getVarint();
         this.readFields(fieldCount - 4);
         this.classes.put(id, new JfrReader.ClassRef(name));
      }
   }

   private void readMethods() {
      int count = this.methods.preallocate(this.getVarint());

      for (int i = 0; i < count; i++) {
         long id = this.getVarlong();
         long cls = this.getVarlong();
         long name = this.getVarlong();
         long sig = this.getVarlong();
         int modifiers = this.getVarint();
         int hidden = this.getVarint();
         this.methods.put(id, new JfrReader.MethodRef(cls, name, sig));
      }

      this.stackFrames.preallocate(count);
   }

   private void readStackTraces() {
      int count = this.stackTraces.preallocate(this.getVarint());

      for (int i = 0; i < count; i++) {
         long id = this.getVarlong();
         int truncated = this.getVarint();
         JfrReader.StackTrace stackTrace = this.readStackTrace();
         this.stackTraces.put(id, stackTrace);
      }
   }

   private JfrReader.StackTrace readStackTrace() {
      int depth = this.getVarint();
      long[] methods = new long[depth];
      byte[] types = new byte[depth];
      int[] locations = new int[depth];

      for (int i = 0; i < depth; i++) {
         methods[i] = this.getVarlong();
         int line = this.getVarint();
         int bci = this.getVarint();
         locations[i] = line << 16 | bci & 65535;
         types[i] = this.buf.get();
      }

      return new JfrReader.StackTrace(methods, types, locations);
   }

   private void readStrings() {
      int count = this.strings.preallocate(this.getVarint());

      for (int i = 0; i < count; i++) {
         this.strings.put(this.getVarlong(), this.getString());
      }
   }

   private void readSymbols() {
      int count = this.symbols.preallocate(this.getVarint());

      for (int i = 0; i < count; i++) {
         long id = this.getVarlong();
         if (this.buf.get() != 3) {
            throw new IllegalArgumentException("Invalid symbol encoding");
         }

         this.symbols.put(id, this.getBytes());
      }
   }

   private void readEnumValues(String typeName) {
      HashMap<Integer, String> map = new HashMap<>();
      int count = this.getVarint();

      for (int i = 0; i < count; i++) {
         map.put((int)this.getVarlong(), this.getString());
      }

      this.enums.put(typeName, map);
   }

   private void readOtherConstants(List<JfrReader.JfrField> fields) {
      int stringType = this.getTypeId("java.lang.String");
      boolean[] numeric = new boolean[fields.size()];

      for (int i = 0; i < numeric.length; i++) {
         JfrReader.JfrField f = fields.get(i);
         numeric[i] = f.constantPool || f.type != stringType;
      }

      int count = this.getVarint();

      for (int i = 0; i < count; i++) {
         this.getVarlong();
         this.readFields(numeric);
      }
   }

   private void readFields(boolean[] numeric) {
      for (boolean n : numeric) {
         if (n) {
            this.getVarlong();
         } else {
            this.getString();
         }
      }
   }

   private void readFields(int count) {
      while (count-- > 0) {
         this.getVarlong();
      }
   }

   private void cacheEventTypes() {
      this.executionSample = this.getTypeId("jdk.ExecutionSample");
      this.nativeMethodSample = this.getTypeId("jdk.NativeMethodSample");
      this.wallClockSample = this.getTypeId("profiler.WallClockSample");
      this.allocationInNewTLAB = this.getTypeId("jdk.ObjectAllocationInNewTLAB");
      this.allocationOutsideTLAB = this.getTypeId("jdk.ObjectAllocationOutsideTLAB");
      this.allocationSample = this.getTypeId("jdk.ObjectAllocationSample");
      this.liveObject = this.getTypeId("profiler.LiveObject");
      this.monitorEnter = this.getTypeId("jdk.JavaMonitorEnter");
      this.threadPark = this.getTypeId("jdk.ThreadPark");
      this.activeSetting = this.getTypeId("jdk.ActiveSetting");
      this.registerEvent("jdk.CPULoad", JfrReader.CPULoad.class);
      this.registerEvent("jdk.GCHeapSummary", JfrReader.GCHeapSummary.class);
      this.registerEvent("jdk.ObjectCount", JfrReader.ObjectCount.class);
      this.registerEvent("jdk.ObjectCountAfterGC", JfrReader.ObjectCount.class);
   }

   private int getTypeId(String typeName) {
      JfrReader.JfrClass type = this.typesByName.get(typeName);
      return type != null ? type.id : -1;
   }

   public int getEnumKey(String typeName, String value) {
      Map<Integer, String> enumValues = this.enums.get(typeName);
      if (enumValues != null) {
         for (Entry<Integer, String> entry : enumValues.entrySet()) {
            if (value.equals(entry.getValue())) {
               return entry.getKey();
            }
         }
      }

      return -1;
   }

   public String getEnumValue(String typeName, int key) {
      return this.enums.get(typeName).get(key);
   }

   public int getVarint() {
      int result = 0;
      int shift = 0;

      while (true) {
         byte b = this.buf.get();
         result |= (b & 127) << shift;
         if (b >= 0) {
            return result;
         }

         shift += 7;
      }
   }

   public long getVarlong() {
      long result = 0L;

      for (int shift = 0; shift < 56; shift += 7) {
         byte b = this.buf.get();
         result |= (b & 127L) << shift;
         if (b >= 0) {
            return result;
         }
      }

      return result | (this.buf.get() & 255L) << 56;
   }

   public float getFloat() {
      return this.buf.getFloat();
   }

   public double getDouble() {
      return this.buf.getDouble();
   }

   public String getString() {
      switch (this.buf.get()) {
         case 0:
            return null;
         case 1:
            return "";
         case 2:
            return this.strings.get(this.getVarlong());
         case 3:
            return new String(this.getBytes(), StandardCharsets.UTF_8);
         case 4:
            char[] chars = new char[this.getVarint()];

            for (int i = 0; i < chars.length; i++) {
               chars[i] = (char)this.getVarint();
            }

            return new String(chars);
         case 5:
            return new String(this.getBytes(), StandardCharsets.ISO_8859_1);
         default:
            throw new IllegalArgumentException("Invalid string encoding");
      }
   }

   public byte[] getBytes() {
      byte[] bytes = new byte[this.getVarint()];
      this.buf.get(bytes);
      return bytes;
   }

   private void seek(long pos) throws IOException {
      long bufPosition = pos - this.filePosition;
      if (bufPosition >= 0L && bufPosition <= this.buf.limit()) {
         ((Buffer)this.buf).position((int)bufPosition);
      } else {
         this.filePosition = pos;
         this.ch.position(pos);
         ((Buffer)this.buf).rewind().flip();
      }
   }

   private boolean ensureBytes(int needed) throws IOException {
      if (this.buf.remaining() >= needed) {
         return true;
      } else if (this.ch == null) {
         return false;
      } else {
         this.filePosition = this.filePosition + this.buf.position();
         if (this.buf.capacity() < needed) {
            ByteBuffer newBuf = ByteBuffer.allocateDirect(needed);
            newBuf.put(this.buf);
            this.buf = newBuf;
         } else {
            this.buf.compact();
         }

         while (this.ch.read(this.buf) > 0 && this.buf.position() < needed) {
         }

         ((Buffer)this.buf).flip();
         return this.buf.limit() > 0;
      }
   }

   public static class AllocationSample extends JfrReader.Event {
      public final int classId;
      public final long allocationSize;
      public final long tlabSize;

      public AllocationSample(long time, int tid, int stackTraceId, int classId, long allocationSize, long tlabSize) {
         super(time, tid, stackTraceId);
         this.classId = classId;
         this.allocationSize = allocationSize;
         this.tlabSize = tlabSize;
      }

      @Override
      public int hashCode() {
         return this.classId * 127 + this.stackTraceId + (this.tlabSize == 0L ? 17 : 0);
      }

      @Override
      public boolean sameGroup(JfrReader.Event o) {
         if (!(o instanceof JfrReader.AllocationSample)) {
            return false;
         } else {
            JfrReader.AllocationSample a = (JfrReader.AllocationSample)o;
            return this.classId == a.classId && this.tlabSize == 0L == (a.tlabSize == 0L);
         }
      }

      @Override
      public long classId() {
         return this.classId;
      }

      @Override
      public long value() {
         return this.tlabSize != 0L ? this.tlabSize : this.allocationSize;
      }
   }

   static class CPULoad extends JfrReader.Event {
      public final float jvmUser;
      public final float jvmSystem;
      public final float machineTotal;

      public CPULoad(JfrReader jfr) {
         super(jfr.getVarlong(), 0, 0);
         this.jvmUser = jfr.getFloat();
         this.jvmSystem = jfr.getFloat();
         this.machineTotal = jfr.getFloat();
      }
   }

   public static class ClassRef {
      public final long name;

      public ClassRef(long name) {
         this.name = name;
      }
   }

   static class ContendedLock extends JfrReader.Event {
      public final long duration;
      public final int classId;

      public ContendedLock(long time, int tid, int stackTraceId, long duration, int classId) {
         super(time, tid, stackTraceId);
         this.duration = duration;
         this.classId = classId;
      }

      @Override
      public int hashCode() {
         return this.classId * 127 + this.stackTraceId;
      }

      @Override
      public boolean sameGroup(JfrReader.Event o) {
         if (o instanceof JfrReader.ContendedLock) {
            JfrReader.ContendedLock c = (JfrReader.ContendedLock)o;
            return this.classId == c.classId;
         } else {
            return false;
         }
      }

      @Override
      public long classId() {
         return this.classId;
      }

      @Override
      public long value() {
         return this.duration;
      }
   }

   static class Element {
      void addChild(JfrReader.Element e) {
      }
   }

   public abstract static class Event implements Comparable<JfrReader.Event> {
      public final long time;
      public final int tid;
      public final int stackTraceId;

      protected Event(long time, int tid, int stackTraceId) {
         this.time = time;
         this.tid = tid;
         this.stackTraceId = stackTraceId;
      }

      public int compareTo(JfrReader.Event o) {
         return Long.compare(this.time, o.time);
      }

      @Override
      public int hashCode() {
         return this.stackTraceId;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
            .append("{time=")
            .append(this.time)
            .append(",tid=")
            .append(this.tid)
            .append(",stackTraceId=")
            .append(this.stackTraceId);

         for (Field f : this.getClass().getDeclaredFields()) {
            try {
               sb.append(',').append(f.getName()).append('=').append(f.get(this));
            } catch (ReflectiveOperationException var7) {
               break;
            }
         }

         return sb.append('}').toString();
      }

      public boolean sameGroup(JfrReader.Event o) {
         return this.getClass() == o.getClass();
      }

      public long classId() {
         return 0L;
      }

      public long samples() {
         return 1L;
      }

      public long value() {
         return 1L;
      }
   }

   public static class ExecutionSample extends JfrReader.Event {
      public final int threadState;
      public final int samples;

      public ExecutionSample(long time, int tid, int stackTraceId, int threadState, int samples) {
         super(time, tid, stackTraceId);
         this.threadState = threadState;
         this.samples = samples;
      }

      @Override
      public long samples() {
         return this.samples;
      }

      @Override
      public long value() {
         return this.samples;
      }
   }

   static class GCHeapSummary extends JfrReader.Event {
      public final int gcId;
      public final boolean afterGC;
      public final long committed;
      public final long reserved;
      public final long used;

      public GCHeapSummary(JfrReader jfr) {
         super(jfr.getVarlong(), 0, 0);
         this.gcId = jfr.getVarint();
         this.afterGC = jfr.getVarint() > 0;
         long start = jfr.getVarlong();
         long committedEnd = jfr.getVarlong();
         this.committed = jfr.getVarlong();
         long reservedEnd = jfr.getVarlong();
         this.reserved = jfr.getVarlong();
         this.used = jfr.getVarlong();
      }
   }

   static class JfrClass extends JfrReader.Element {
      final int id;
      final boolean simpleType;
      final String name;
      final List<JfrReader.JfrField> fields;

      JfrClass(Map<String, String> attributes) {
         this.id = Integer.parseInt(attributes.get("id"));
         this.simpleType = "true".equals(attributes.get("simpleType"));
         this.name = attributes.get("name");
         this.fields = new ArrayList<>(2);
      }

      @Override
      void addChild(JfrReader.Element e) {
         if (e instanceof JfrReader.JfrField) {
            this.fields.add((JfrReader.JfrField)e);
         }
      }

      public JfrReader.JfrField field(String name) {
         for (JfrReader.JfrField field : this.fields) {
            if (field.name.equals(name)) {
               return field;
            }
         }

         return null;
      }
   }

   static class JfrField extends JfrReader.Element {
      final String name;
      final int type;
      final boolean constantPool;

      JfrField(Map<String, String> attributes) {
         this.name = attributes.get("name");
         this.type = Integer.parseInt(attributes.get("class"));
         this.constantPool = "true".equals(attributes.get("constantPool"));
      }
   }

   static class LiveObject extends JfrReader.Event {
      public final int classId;
      public final long allocationSize;
      public final long allocationTime;

      public LiveObject(long time, int tid, int stackTraceId, int classId, long allocationSize, long allocationTime) {
         super(time, tid, stackTraceId);
         this.classId = classId;
         this.allocationSize = allocationSize;
         this.allocationTime = allocationTime;
      }

      @Override
      public int hashCode() {
         return this.classId * 127 + this.stackTraceId;
      }

      @Override
      public boolean sameGroup(JfrReader.Event o) {
         if (o instanceof JfrReader.LiveObject) {
            JfrReader.LiveObject a = (JfrReader.LiveObject)o;
            return this.classId == a.classId;
         } else {
            return false;
         }
      }

      @Override
      public long classId() {
         return this.classId;
      }

      @Override
      public long value() {
         return this.allocationSize;
      }
   }

   public static class MethodRef {
      public final long cls;
      public final long name;
      public final long sig;

      public MethodRef(long cls, long name, long sig) {
         this.cls = cls;
         this.name = name;
         this.sig = sig;
      }
   }

   static class ObjectCount extends JfrReader.Event {
      public final int gcId;
      public final int classId;
      public final long count;
      public final long totalSize;

      public ObjectCount(JfrReader jfr) {
         super(jfr.getVarlong(), 0, 0);
         this.gcId = jfr.getVarint();
         this.classId = jfr.getVarint();
         this.count = jfr.getVarlong();
         this.totalSize = jfr.getVarlong();
      }
   }

   public static class StackTrace {
      public final long[] methods;
      public final byte[] types;
      public final int[] locations;

      public StackTrace(long[] methods, byte[] types, int[] locations) {
         this.methods = methods;
         this.types = types;
         this.locations = locations;
      }
   }
}
