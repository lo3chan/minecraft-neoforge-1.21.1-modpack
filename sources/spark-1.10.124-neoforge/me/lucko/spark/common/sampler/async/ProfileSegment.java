package me.lucko.spark.common.sampler.async;

import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import me.lucko.spark.common.sampler.async.jfr.JfrReader;

public class ProfileSegment {
   private static final String UNKNOWN_THREAD_STATE = "<unknown>";
   private final int nativeThreadId;
   private final String threadName;
   private final AsyncStackTraceElement[] stackTrace;
   private final long value;
   private final String threadState;

   private ProfileSegment(int nativeThreadId, String threadName, AsyncStackTraceElement[] stackTrace, long value, String threadState) {
      this.nativeThreadId = nativeThreadId;
      this.threadName = threadName;
      this.stackTrace = stackTrace;
      this.value = value;
      this.threadState = threadState;
   }

   public int getNativeThreadId() {
      return this.nativeThreadId;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public AsyncStackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public long getValue() {
      return this.value;
   }

   public String getThreadState() {
      return this.threadState;
   }

   public static ProfileSegment parseSegment(JfrReader reader, JfrReader.Event sample, String threadName, long value) {
      JfrReader.StackTrace stackTrace = reader.stackTraces.get(sample.stackTraceId);
      int len = stackTrace != null ? stackTrace.methods.length : 0;
      AsyncStackTraceElement[] stack = new AsyncStackTraceElement[len];

      for (int i = 0; i < len; i++) {
         stack[i] = parseStackFrame(reader, stackTrace.methods[i]);
      }

      String threadState = "<unknown>";
      if (sample instanceof JfrReader.ExecutionSample) {
         JfrReader.ExecutionSample executionSample = (JfrReader.ExecutionSample)sample;
         Map<Integer, String> threadStateLookup = reader.enums.getOrDefault("jdk.types.ThreadState", ImmutableMap.of());
         threadState = threadStateLookup.getOrDefault(executionSample.threadState, "<unknown>");
      }

      return new ProfileSegment(sample.tid, threadName, stack, value, threadState);
   }

   private static AsyncStackTraceElement parseStackFrame(JfrReader reader, long methodId) {
      AsyncStackTraceElement result = reader.stackFrames.get(methodId);
      if (result != null) {
         return result;
      } else {
         JfrReader.MethodRef methodRef = reader.methods.get(methodId);
         JfrReader.ClassRef classRef = reader.classes.get(methodRef.cls);
         byte[] className = reader.symbols.get(classRef.name);
         byte[] methodName = reader.symbols.get(methodRef.name);
         if (className != null && className.length != 0) {
            byte[] methodDesc = reader.symbols.get(methodRef.sig);
            result = new AsyncStackTraceElement(
               new String(className, StandardCharsets.UTF_8).replace('/', '.'),
               new String(methodName, StandardCharsets.UTF_8),
               new String(methodDesc, StandardCharsets.UTF_8)
            );
         } else {
            result = new AsyncStackTraceElement("native", new String(methodName, StandardCharsets.UTF_8), null);
         }

         reader.stackFrames.put(methodId, result);
         return result;
      }
   }
}
