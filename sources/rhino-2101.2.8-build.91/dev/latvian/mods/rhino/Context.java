package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.ast.AstRoot;
import dev.latvian.mods.rhino.ast.ScriptNode;
import dev.latvian.mods.rhino.classfile.ClassFileWriter;
import dev.latvian.mods.rhino.regexp.RegExp;
import dev.latvian.mods.rhino.type.ArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.ArrayValueProvider;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.JavaSetWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.Nullable;

public class Context {
   public static final int CONVERSION_EXACT = 0;
   public static final int CONVERSION_TRIVIAL = 1;
   public static final int CONVERSION_NONE = 99;
   public static final int JSTYPE_UNDEFINED = 0;
   public static final int JSTYPE_NULL = 1;
   public static final int JSTYPE_BOOLEAN = 2;
   public static final int JSTYPE_NUMBER = 3;
   public static final int JSTYPE_STRING = 4;
   public static final int JSTYPE_JAVA_CLASS = 5;
   public static final int JSTYPE_JAVA_OBJECT = 6;
   public static final int JSTYPE_JAVA_ARRAY = 7;
   public static final int JSTYPE_OBJECT = 8;
   public final ContextFactory factory;
   public final Object lock = new Object();
   public boolean generateObserverCount = false;
   private Scriptable topCallScope;
   boolean isContinuationsTopCall;
   NativeCall currentActivationCall;
   BaseFunction typeErrorThrower;
   RegExp regExp;
   Object lastInterpreterFrame;
   ObjArray previousInterpreterInvocations;
   int instructionCount;
   int instructionThreshold;
   long scratchUint32;
   private Scriptable scratchScriptable;
   boolean isTopLevelStrict;
   private Map<Object, Object> threadLocalMap;
   private ClassLoader applicationClassLoader;
   private final ArrayDeque<Runnable> microtasks = new ArrayDeque<>();
   private final UnhandledRejectionTracker unhandledPromises = new UnhandledRejectionTracker();
   private transient Map<Class<?>, JavaMembers> classTable;
   private transient Map<JavaAdapter.JavaAdapterSignature, Class<?>> classAdapterCache;
   private transient Map<Class<?>, Object> interfaceAdapterCache;
   private int generatedClassSerial;

   public static void reportWarning(Context cx, String message, String sourceName, int lineno, String lineSource, int lineOffset) {
      cx.getErrorReporter().warning(message, sourceName, lineno, lineSource, lineOffset);
   }

   public static void reportWarning(String message, Context cx) {
      int[] linep = new int[]{0};
      String filename = getSourcePositionFromStack(cx, linep);
      reportWarning(cx, message, filename, linep[0], null, 0);
   }

   public static void reportError(Context cx, String message, int lineno, String lineSource, int lineOffset, String sourceName) {
      if (cx != null) {
         cx.getErrorReporter().error(cx, message, sourceName, lineno, lineSource, lineOffset);
      } else {
         throw new EvaluatorException(cx, message, sourceName, lineno, lineSource, lineOffset);
      }
   }

   public static void reportError(Context cx, String message) {
      int[] linep = new int[]{0};
      String filename = getSourcePositionFromStack(cx, linep);
      reportError(cx, message, linep[0], null, 0, filename);
   }

   public static EvaluatorException reportRuntimeError(Context cx, String message, String sourceName, int lineno, String lineSource, int lineOffset) {
      if (cx != null) {
         return cx.getErrorReporter().runtimeError(cx, message, sourceName, lineno, lineSource, lineOffset);
      } else {
         throw new EvaluatorException(cx, message, sourceName, lineno, lineSource, lineOffset);
      }
   }

   public static EvaluatorException reportRuntimeError0(String messageId, Context cx) {
      String msg = ScriptRuntime.getMessage0(messageId);
      return reportRuntimeError(msg, cx);
   }

   public static EvaluatorException reportRuntimeError1(String messageId, Object arg1, Context cx) {
      String msg = ScriptRuntime.getMessage1(messageId, arg1);
      return reportRuntimeError(msg, cx);
   }

   public static EvaluatorException reportRuntimeError2(String messageId, Object arg1, Object arg2, Context cx) {
      String msg = ScriptRuntime.getMessage2(messageId, arg1, arg2);
      return reportRuntimeError(msg, cx);
   }

   public static EvaluatorException reportRuntimeError3(String messageId, Object arg1, Object arg2, Object arg3, Context cx) {
      String msg = ScriptRuntime.getMessage3(messageId, arg1, arg2, arg3);
      return reportRuntimeError(msg, cx);
   }

   public static EvaluatorException reportRuntimeError4(String messageId, Object arg1, Object arg2, Object arg3, Object arg4, Context cx) {
      String msg = ScriptRuntime.getMessage4(messageId, arg1, arg2, arg3, arg4);
      return reportRuntimeError(msg, cx);
   }

   public static EvaluatorException reportRuntimeError(String message, Context cx) {
      int[] linep = new int[]{0};
      String filename = getSourcePositionFromStack(cx, linep);
      return reportRuntimeError(cx, message, filename, linep[0], null, 0);
   }

   public static Object getUndefinedValue() {
      return Undefined.INSTANCE;
   }

   public static RuntimeException throwAsScriptRuntimeEx(Throwable e, Context cx) {
      while (e instanceof InvocationTargetException) {
         e = ((InvocationTargetException)e).getTargetException();
      }

      switch (e) {
         case Error err:
            throw err;
         case RhinoException errx:
            throw errx;
         case null:
         default:
            throw new WrappedException(cx, e);
      }
   }

   static Evaluator createInterpreter() {
      return new Interpreter();
   }

   public static String getSourcePositionFromStack(Context cx, int[] linep) {
      if (cx == null) {
         return null;
      } else {
         if (cx.lastInterpreterFrame != null) {
            Evaluator evaluator = createInterpreter();
            if (evaluator != null) {
               return evaluator.getSourcePositionFromStack(cx, linep);
            }
         }

         StackTraceElement[] stackTrace = new Throwable().getStackTrace();

         for (StackTraceElement st : stackTrace) {
            String file = st.getFileName();
            if (file != null && !file.endsWith(".java")) {
               int line = st.getLineNumber();
               if (line >= 0) {
                  linep[0] = line;
                  return file;
               }
            }
         }

         return null;
      }
   }

   public Context(ContextFactory factory) {
      this.factory = factory;
   }

   public final String getImplementationVersion() {
      return ImplementationVersion.get();
   }

   public final ErrorReporter getErrorReporter() {
      return DefaultErrorReporter.instance;
   }

   public final ScriptableObject initStandardObjects() {
      return this.initStandardObjects(null, false);
   }

   public final ScriptableObject initSafeStandardObjects() {
      return this.initSafeStandardObjects(null, false);
   }

   public final Scriptable initStandardObjects(ScriptableObject scope) {
      return this.initStandardObjects(scope, false);
   }

   public final Scriptable initSafeStandardObjects(ScriptableObject scope) {
      return this.initSafeStandardObjects(scope, false);
   }

   public ScriptableObject initStandardObjects(ScriptableObject scope, boolean sealed) {
      return ScriptRuntime.initStandardObjects(this, scope, sealed);
   }

   public ScriptableObject initSafeStandardObjects(ScriptableObject scope, boolean sealed) {
      return ScriptRuntime.initSafeStandardObjects(this, scope, sealed);
   }

   public final Object evaluateString(Scriptable scope, String source, String sourceName, int lineno, Object securityDomain) {
      Script script = this.compileString(source, sourceName, lineno, securityDomain);
      return script != null ? script.exec(this, scope) : null;
   }

   public final Object evaluateReader(Scriptable scope, Reader in, String sourceName, int lineno, Object securityDomain) throws IOException {
      Script script = this.compileReader(in, sourceName, lineno, securityDomain);
      return script != null ? script.exec(this, scope) : null;
   }

   public final Script compileReader(Reader in, String sourceName, int lineno, Object securityDomain) throws IOException {
      if (lineno < 0) {
         lineno = 0;
      }

      return (Script)this.compileImpl(null, Kit.readReader(in), sourceName, lineno, securityDomain, false, null, null);
   }

   public final Script compileString(String source, String sourceName, int lineno, Object securityDomain) {
      if (lineno < 0) {
         lineno = 0;
      }

      return this.compileString(source, null, null, sourceName, lineno, securityDomain);
   }

   final Script compileString(String source, Evaluator compiler, ErrorReporter compilationErrorReporter, String sourceName, int lineno, Object securityDomain) {
      try {
         return (Script)this.compileImpl(null, source, sourceName, lineno, securityDomain, false, compiler, compilationErrorReporter);
      } catch (IOException var8) {
         throw new RuntimeException(var8);
      }
   }

   final Function compileFunction(
      Scriptable scope, String source, Evaluator compiler, ErrorReporter compilationErrorReporter, String sourceName, int lineno, Object securityDomain
   ) {
      try {
         return (Function)this.compileImpl(scope, source, sourceName, lineno, securityDomain, true, compiler, compilationErrorReporter);
      } catch (IOException var9) {
         throw new RuntimeException(var9);
      }
   }

   public Scriptable newObject(Scriptable scope) {
      NativeObject result = new NativeObject(this.factory);
      ScriptRuntime.setBuiltinProtoAndParent(this, scope, result, TopLevel.Builtins.Object);
      return result;
   }

   public Scriptable newObject(Scriptable scope, String constructorName) {
      return this.newObject(scope, constructorName, ScriptRuntime.EMPTY_OBJECTS);
   }

   public Scriptable newObject(Scriptable scope, String constructorName, Object[] args) {
      return ScriptRuntime.newObject(this, scope, constructorName, args);
   }

   public Scriptable newArray(Scriptable scope, int length) {
      NativeArray result = new NativeArray(this, length);
      ScriptRuntime.setBuiltinProtoAndParent(this, scope, result, TopLevel.Builtins.Array);
      return result;
   }

   public Scriptable newArray(Scriptable scope, Object[] elements) {
      if (elements.getClass().getComponentType() != ScriptRuntime.ObjectClass) {
         throw new IllegalArgumentException();
      } else {
         NativeArray result = new NativeArray(this, elements);
         ScriptRuntime.setBuiltinProtoAndParent(this, scope, result, TopLevel.Builtins.Array);
         return result;
      }
   }

   public boolean toBoolean(Object value) {
      return ScriptRuntime.toBoolean(this, value);
   }

   public double toNumber(Object value) {
      return ScriptRuntime.toNumber(this, value);
   }

   public String toString(Object value) {
      return ScriptRuntime.toString(this, value);
   }

   public Scriptable toObject(Object value, Scriptable scope) {
      return ScriptRuntime.toObject(this, scope, value);
   }

   public final Object getThreadLocal(Object key) {
      return this.threadLocalMap == null ? null : this.threadLocalMap.get(key);
   }

   public final synchronized void putThreadLocal(Object key, Object value) {
      if (this.threadLocalMap == null) {
         this.threadLocalMap = new HashMap<>();
      }

      this.threadLocalMap.put(key, value);
   }

   public final void removeThreadLocal(Object key) {
      if (this.threadLocalMap != null) {
         this.threadLocalMap.remove(key);
      }
   }

   public final int getInstructionObserverThreshold() {
      return this.instructionThreshold;
   }

   public final void setInstructionObserverThreshold(int threshold) {
      if (threshold < 0) {
         throw new IllegalArgumentException();
      } else {
         this.instructionThreshold = threshold;
         this.setGenerateObserverCount(threshold > 0);
      }
   }

   public void setGenerateObserverCount(boolean generateObserverCount) {
      this.generateObserverCount = generateObserverCount;
   }

   protected void observeInstructionCount(int instructionCount) {
   }

   public final ClassLoader getApplicationClassLoader() {
      if (this.applicationClassLoader == null) {
         ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
         if (threadLoader != null && Kit.testIfCanLoadRhinoClasses(threadLoader)) {
            return threadLoader;
         }

         this.applicationClassLoader = this.getClass().getClassLoader();
      }

      return this.applicationClassLoader;
   }

   public final void setApplicationClassLoader(ClassLoader loader) {
      if (loader == null) {
         this.applicationClassLoader = null;
      } else if (!Kit.testIfCanLoadRhinoClasses(loader)) {
         throw new IllegalArgumentException("Loader can not resolve Rhino classes");
      } else {
         this.applicationClassLoader = loader;
      }
   }

   public void enqueueMicrotask(Runnable task) {
      this.microtasks.add(task);
   }

   public void processMicrotasks() {
      Runnable head;
      do {
         head = this.microtasks.poll();
         if (head != null) {
            head.run();
         }
      } while (head != null);
   }

   public void setTrackUnhandledPromiseRejections(boolean track) {
      this.unhandledPromises.enable(track);
   }

   public UnhandledRejectionTracker getUnhandledPromiseTracker() {
      return this.unhandledPromises;
   }

   private Object compileImpl(
      Scriptable scope,
      String sourceString,
      String sourceName,
      int lineno,
      Object securityDomain,
      boolean returnFunction,
      Evaluator compiler,
      ErrorReporter compilationErrorReporter
   ) throws IOException {
      if (sourceName == null) {
         sourceName = "unnamed script";
      }

      if (securityDomain != null) {
         throw new IllegalArgumentException("securityDomain should be null if setSecurityController() was never called");
      } else {
         if (scope == null == returnFunction) {
            Kit.codeBug();
         }

         CompilerEnvirons compilerEnv = new CompilerEnvirons();
         compilerEnv.initFromContext(this);
         if (compilationErrorReporter == null) {
            compilationErrorReporter = compilerEnv.getErrorReporter();
         }

         ScriptNode tree = this.parse(sourceString, sourceName, lineno, compilerEnv, compilationErrorReporter, returnFunction);

         Object bytecode;
         try {
            if (compiler == null) {
               compiler = this.createCompiler();
            }

            bytecode = compiler.compile(compilerEnv, tree, returnFunction, this);
         } catch (ClassFileWriter.ClassFileFormatException var13) {
            tree = this.parse(sourceString, sourceName, lineno, compilerEnv, compilationErrorReporter, returnFunction);
            compiler = createInterpreter();
            bytecode = compiler.compile(compilerEnv, tree, returnFunction, this);
         }

         Object result;
         if (returnFunction) {
            result = compiler.createFunctionObject(this, scope, bytecode, securityDomain);
         } else {
            result = compiler.createScriptObject(bytecode, securityDomain);
         }

         return result;
      }
   }

   private ScriptNode parse(
      String sourceString, String sourceName, int lineno, CompilerEnvirons compilerEnv, ErrorReporter compilationErrorReporter, boolean returnFunction
   ) throws IOException {
      Parser p = new Parser(this, compilerEnv, compilationErrorReporter);
      if (returnFunction) {
         p.calledByCompileFunction = true;
      }

      if (this.isStrictMode()) {
         p.setDefaultUseStrictDirective(true);
      }

      AstRoot ast = p.parse(sourceString, sourceName, lineno);
      if (!returnFunction || ast.getFirstChild() != null && ast.getFirstChild().getType() == 111) {
         return new IRFactory(this, compilerEnv, compilationErrorReporter).transformTree(ast);
      } else {
         throw new IllegalArgumentException("compileFunction only accepts source with single JS function: " + sourceString);
      }
   }

   private Evaluator createCompiler() {
      return createInterpreter();
   }

   public RegExp getRegExp() {
      if (this.regExp == null) {
         this.regExp = new RegExp();
      }

      return this.regExp;
   }

   public final boolean isStrictMode() {
      return this.isTopLevelStrict || this.currentActivationCall != null && this.currentActivationCall.isStrict;
   }

   public void addToScope(Scriptable scope, String name, Object value) {
      if (value instanceof Class<?> c) {
         ScriptableObject.putProperty(scope, name, new NativeJavaClass(this, scope, c), this);
      } else {
         ScriptableObject.putProperty(scope, name, this.javaToJS(value, scope), this);
      }
   }

   Map<Class<?>, JavaMembers> getClassCacheMap() {
      if (this.classTable == null) {
         this.classTable = new ConcurrentHashMap<>(16, 0.75F, 1);
      }

      return this.classTable;
   }

   Map<JavaAdapter.JavaAdapterSignature, Class<?>> getInterfaceAdapterCacheMap() {
      if (this.classAdapterCache == null) {
         this.classAdapterCache = new ConcurrentHashMap<>(16, 0.75F, 1);
      }

      return this.classAdapterCache;
   }

   public final synchronized int newClassSerialNumber() {
      return ++this.generatedClassSerial;
   }

   Object getInterfaceAdapter(Class<?> cl) {
      return this.interfaceAdapterCache == null ? null : this.interfaceAdapterCache.get(cl);
   }

   synchronized void cacheInterfaceAdapter(Class<?> cl, Object iadapter) {
      if (this.interfaceAdapterCache == null) {
         this.interfaceAdapterCache = new ConcurrentHashMap<>(16, 0.75F, 1);
      }

      this.interfaceAdapterCache.put(cl, iadapter);
   }

   public boolean visibleToScripts(String fullClassName, ClassVisibilityContext type) {
      return true;
   }

   public Object wrap(Scriptable scope, Object obj, TypeInfo target) {
      if (obj == null || obj == Undefined.INSTANCE || obj instanceof Scriptable) {
         return obj;
      } else if (target.isVoid()) {
         return Undefined.INSTANCE;
      } else if (target.isCharacter()) {
         return Integer.valueOf((Character)obj);
      } else if (target.isPrimitive()) {
         return obj;
      } else {
         return target instanceof ArrayTypeInfo array ? new NativeJavaArray(scope, obj, array, this) : this.wrapAsJavaObject(scope, obj, target);
      }
   }

   public Object wrapAny(Scriptable scope, Object obj) {
      if (obj instanceof String
         || obj instanceof Boolean
         || obj instanceof Integer
         || obj instanceof Byte
         || obj instanceof Short
         || obj instanceof Long
         || obj instanceof Float
         || obj instanceof Double) {
         return obj;
      } else if (obj instanceof Character) {
         return String.valueOf(((Character)obj).charValue());
      } else {
         Class<?> cls = obj.getClass();
         TypeInfo ti = TypeInfo.NONE;
         if (cls.isArray()) {
            ti = TypeInfo.of(cls);
         }

         return this.wrap(scope, obj, ti);
      }
   }

   public Object wrap(Scriptable scope, Object obj) {
      return this.wrap(scope, obj, TypeInfo.NONE);
   }

   public boolean hasTopCallScope() {
      synchronized (this.lock) {
         return this.topCallScope != null;
      }
   }

   public Scriptable getTopCallScope() {
      synchronized (this.lock) {
         return this.topCallScope;
      }
   }

   public Scriptable getTopCallOrThrow() {
      synchronized (this.lock) {
         if (this.topCallScope == null) {
            throw new IllegalStateException();
         } else {
            return this.topCallScope;
         }
      }
   }

   public void setTopCall(Scriptable scope) {
      synchronized (this.lock) {
         this.topCallScope = scope;
      }
   }

   public void storeScriptable(Scriptable value) {
      synchronized (this.lock) {
         if (this.scratchScriptable != null) {
            throw new IllegalStateException();
         } else {
            this.scratchScriptable = value;
         }
      }
   }

   public Scriptable lastStoredScriptable() {
      synchronized (this.lock) {
         Scriptable result = this.scratchScriptable;
         this.scratchScriptable = null;
         return result;
      }
   }

   public Object callSync(Callable callable, Scriptable scope, Scriptable thisObj, Object[] args) {
      synchronized (this.lock) {
         return callable.call(this, scope, thisObj, args);
      }
   }

   public Object doTopCall(Scriptable scope, Callable callable, Scriptable thisObj, Object[] args, boolean isTopLevelStrict) {
      if (scope == null) {
         throw new IllegalArgumentException();
      } else if (this.hasTopCallScope()) {
         throw new IllegalStateException();
      } else {
         this.setTopCall(ScriptableObject.getTopLevelScope(scope));
         boolean previousTopLevelStrict = this.isTopLevelStrict;
         this.isTopLevelStrict = isTopLevelStrict;

         Object result;
         try {
            result = this.callSync(callable, scope, thisObj, args);
         } finally {
            this.setTopCall(null);
            this.isTopLevelStrict = previousTopLevelStrict;
            if (this.currentActivationCall != null) {
               throw new IllegalStateException();
            }
         }

         return result;
      }
   }

   public Scriptable wrapJavaClass(Scriptable scope, Class<?> javaClass) {
      return new NativeJavaClass(this, scope, javaClass);
   }

   public Scriptable wrapAsJavaObject(Scriptable scope, Object javaObject, TypeInfo target) {
      if (javaObject instanceof CustomJavaToJsWrapper w) {
         return w.convertJavaToJs(this, scope, target);
      } else if (javaObject instanceof Map map) {
         return new NativeJavaMap(this, scope, map, map, target);
      } else if (javaObject instanceof List list) {
         return new NativeJavaList(this, scope, list, list, target);
      } else {
         return (Scriptable)(javaObject instanceof Set<?> set
            ? new NativeJavaList(this, scope, set, new JavaSetWrapper<>(set), target)
            : new NativeJavaObject(scope, javaObject, target, this));
      }
   }

   public Scriptable wrapNewObject(Scriptable scope, Object obj, TypeInfo objType) {
      if (obj instanceof Scriptable) {
         return (Scriptable)obj;
      } else {
         return (Scriptable)(objType instanceof ArrayTypeInfo
            ? new NativeJavaArray(scope, obj, objType, this)
            : this.wrapAsJavaObject(scope, obj, TypeInfo.NONE));
      }
   }

   public int internalConversionWeight(Object fromObj, TypeInfo target) {
      return this.factory.getTypeWrappers().hasWrapper(fromObj, target) ? 0 : 99;
   }

   public int internalConversionWeightLast(Object fromObj, TypeInfo target) {
      return 99;
   }

   public GeneratedClassLoader createClassLoader(ClassLoader parent) {
      return new DefiningClassLoader(parent);
   }

   public int getMaximumInterpreterStackDepth() {
      return 2147483647;
   }

   public ArrayValueProvider arrayValueProviderOf(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 1
      // 01: instanceof [Ljava/lang/Object;
      // 04: ifeq 20
      // 07: aload 1
      // 08: checkcast [Ljava/lang/Object;
      // 0b: astore 2
      // 0c: aload 2
      // 0d: arraylength
      // 0e: ifne 17
      // 11: getstatic dev/latvian/mods/rhino/util/ArrayValueProvider.EMPTY Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // 14: goto 1f
      // 17: new dev/latvian/mods/rhino/util/ArrayValueProvider$FromPlainJavaArray
      // 1a: dup
      // 1b: aload 2
      // 1c: invokespecial dev/latvian/mods/rhino/util/ArrayValueProvider$FromPlainJavaArray.<init> ([Ljava/lang/Object;)V
      // 1f: areturn
      // 20: aload 1
      // 21: ifnull 47
      // 24: aload 1
      // 25: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 28: invokevirtual java/lang/Class.isArray ()Z
      // 2b: ifeq 47
      // 2e: aload 1
      // 2f: invokestatic java/lang/reflect/Array.getLength (Ljava/lang/Object;)I
      // 32: istore 3
      // 33: iload 3
      // 34: ifne 3d
      // 37: getstatic dev/latvian/mods/rhino/util/ArrayValueProvider.EMPTY Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // 3a: goto 46
      // 3d: new dev/latvian/mods/rhino/util/ArrayValueProvider$FromJavaArray
      // 40: dup
      // 41: aload 1
      // 42: iload 3
      // 43: invokespecial dev/latvian/mods/rhino/util/ArrayValueProvider$FromJavaArray.<init> (Ljava/lang/Object;I)V
      // 46: areturn
      // 47: aload 1
      // 48: astore 2
      // 49: bipush 0
      // 4a: istore 3
      // 4b: aload 2
      // 4c: iload 3
      // 4d: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/rhino/NativeArray, dev/latvian/mods/rhino/NativeJavaList, dev/latvian/mods/rhino/NativeJavaArray, dev/latvian/mods/rhino/Wrapper, java/util/List, java/lang/Iterable ]
      // 52: tableswitch 143 -1 5 143 42 56 75 93 113 129
      // 7c: aload 2
      // 7d: checkcast dev/latvian/mods/rhino/NativeArray
      // 80: astore 4
      // 82: aload 4
      // 84: invokestatic dev/latvian/mods/rhino/util/ArrayValueProvider.fromNativeArray (Ldev/latvian/mods/rhino/NativeArray;)Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // 87: goto f3
      // 8a: aload 2
      // 8b: checkcast dev/latvian/mods/rhino/NativeJavaList
      // 8e: astore 5
      // 90: aload 5
      // 92: getfield dev/latvian/mods/rhino/NativeJavaList.list Ljava/util/List;
      // 95: aload 5
      // 97: invokestatic dev/latvian/mods/rhino/util/ArrayValueProvider.fromJavaList (Ljava/util/List;Ljava/lang/Object;)Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // 9a: goto f3
      // 9d: aload 2
      // 9e: checkcast dev/latvian/mods/rhino/NativeJavaArray
      // a1: astore 6
      // a3: aload 0
      // a4: aload 6
      // a6: getfield dev/latvian/mods/rhino/NativeJavaArray.array Ljava/lang/Object;
      // a9: invokevirtual dev/latvian/mods/rhino/Context.arrayValueProviderOf (Ljava/lang/Object;)Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // ac: goto f3
      // af: aload 2
      // b0: checkcast dev/latvian/mods/rhino/Wrapper
      // b3: astore 7
      // b5: aload 0
      // b6: aload 7
      // b8: invokeinterface dev/latvian/mods/rhino/Wrapper.unwrap ()Ljava/lang/Object; 1
      // bd: invokevirtual dev/latvian/mods/rhino/Context.arrayValueProviderOf (Ljava/lang/Object;)Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // c0: goto f3
      // c3: aload 2
      // c4: checkcast java/util/List
      // c7: astore 8
      // c9: aload 8
      // cb: aload 8
      // cd: invokestatic dev/latvian/mods/rhino/util/ArrayValueProvider.fromJavaList (Ljava/util/List;Ljava/lang/Object;)Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // d0: goto f3
      // d3: aload 2
      // d4: checkcast java/lang/Iterable
      // d7: astore 9
      // d9: aload 9
      // db: invokestatic dev/latvian/mods/rhino/util/ArrayValueProvider.fromIterable (Ljava/lang/Iterable;)Ldev/latvian/mods/rhino/util/ArrayValueProvider;
      // de: goto f3
      // e1: aload 1
      // e2: ifnonnull eb
      // e5: getstatic dev/latvian/mods/rhino/util/ArrayValueProvider$FromObject.FROM_NULL Ldev/latvian/mods/rhino/util/ArrayValueProvider$FromObject;
      // e8: goto f3
      // eb: new dev/latvian/mods/rhino/util/ArrayValueProvider$FromObject
      // ee: dup
      // ef: aload 1
      // f0: invokespecial dev/latvian/mods/rhino/util/ArrayValueProvider$FromObject.<init> (Ljava/lang/Object;)V
      // f3: areturn
   }

   public Object arrayOf(@Nullable Object from, TypeInfo target) {
      if (from instanceof Object[] arr) {
         if (target == null) {
            return from;
         } else {
            return arr.length == 0 ? target.newArray(0) : new ArrayValueProvider.FromPlainJavaArray(arr).createArray(this, target);
         }
      } else if (from == null || !from.getClass().isArray()) {
         return this.arrayValueProviderOf(from).createArray(this, target);
      } else if (target == null) {
         return from;
      } else {
         int len = Array.getLength(from);
         return len == 0 ? target.newArray(0) : new ArrayValueProvider.FromJavaArray(from, len).createArray(this, target);
      }
   }

   public Object listOf(@Nullable Object from, TypeInfo target) {
      if (!(from instanceof NativeJavaList n)) {
         return this.arrayValueProviderOf(from).createList(this, target);
      } else if (target == null) {
         return n.list;
      } else if (target.equals(n.listType)) {
         return n.list;
      } else {
         ArrayList<Object> list = new ArrayList<>(n.list.size());

         for (Object o : n.list) {
            list.add(this.jsToJava(o, target));
         }

         return list;
      }
   }

   public boolean isListLike(Object from) {
      return from instanceof NativeJavaList || from instanceof NativeJavaArray || from instanceof List;
   }

   @Nullable
   public <K> List<K> optionalListOf(@Nullable Object from, TypeInfo target) {
      return this.isListLike(from) ? (List)this.listOf(from, target) : null;
   }

   @Nullable
   public List<Object> optionalListOf(@Nullable Object from) {
      return this.optionalListOf(from, TypeInfo.NONE);
   }

   public Object setOf(@Nullable Object from, TypeInfo target) {
      if (!(from instanceof NativeJavaList n)) {
         return this.arrayValueProviderOf(from).createSet(this, target);
      } else if (target == null) {
         return new LinkedHashSet(n.list);
      } else if (target.equals(n.listType)) {
         return new LinkedHashSet(n.list);
      } else {
         LinkedHashSet<Object> set = new LinkedHashSet<>(n.list.size());

         for (Object o : n.list) {
            set.add(this.jsToJava(o, target));
         }

         return set;
      }
   }

   public Object mapOf(@Nullable Object from, TypeInfo kTarget, TypeInfo vTarget) {
      if (from instanceof NativeJavaMap n) {
         if (!kTarget.shouldConvert() && !vTarget.shouldConvert()) {
            return n.map;
         } else if (kTarget.equals(n.mapKeyType) && vTarget.equals(n.mapValueType)) {
            return n.map;
         } else if (n.map.isEmpty()) {
            return Map.of();
         } else {
            LinkedHashMap<Object, Object> map = new LinkedHashMap<>(n.map.size());

            for (Entry<?, ?> entry : n.map.entrySet()) {
               map.put(this.jsToJava(entry.getKey(), kTarget), this.jsToJava(entry.getValue(), vTarget));
            }

            return map;
         }
      } else if (from instanceof NativeObject obj) {
         Object[] keys = obj.getIds(this);
         LinkedHashMap<Object, Object> map = new LinkedHashMap<>(keys.length);

         for (Object key : keys) {
            map.put(this.jsToJava(key, kTarget), this.jsToJava(obj.get(this, key), vTarget));
         }

         return map;
      } else if (!(from instanceof Map<?, ?> m)) {
         return this.reportConversionError(from, TypeInfo.RAW_MAP);
      } else if (!kTarget.shouldConvert() && !vTarget.shouldConvert()) {
         return m;
      } else {
         LinkedHashMap<Object, Object> map = new LinkedHashMap<>(m.size());

         for (Entry<?, ?> entry : m.entrySet()) {
            map.put(this.jsToJava(entry.getKey(), kTarget), this.jsToJava(entry.getValue(), vTarget));
         }

         return map;
      }
   }

   public boolean isMapLike(Object from) {
      return from instanceof NativeJavaMap || from instanceof Map;
   }

   @Nullable
   public <K, V> Map<K, V> optionalMapOf(@Nullable Object from, TypeInfo kTarget, TypeInfo vTarget) {
      return this.isMapLike(from) ? (Map)this.mapOf(from, kTarget, vTarget) : null;
   }

   @Nullable
   public Map<String, Object> optionalMapOf(@Nullable Object from) {
      return this.optionalMapOf(from, TypeInfo.STRING, TypeInfo.NONE);
   }

   public Object classOf(Object from) {
      if (from instanceof NativeJavaClass n) {
         return n.getClassObject();
      } else if (from instanceof Class<?> c) {
         if (this.visibleToScripts(c.getName(), ClassVisibilityContext.ARGUMENT)) {
            return c;
         } else {
            throw reportRuntimeError("Class " + c.getName() + " not allowed", this);
         }
      } else {
         String s = ScriptRuntime.toString(this, from);
         if (this.visibleToScripts(s, ClassVisibilityContext.ARGUMENT)) {
            try {
               return Class.forName(s);
            } catch (ClassNotFoundException var6) {
               throw reportRuntimeError("Failed to load class " + s, this);
            }
         } else {
            throw reportRuntimeError("Class " + from + " not allowed", this);
         }
      }
   }

   public Object createInterfaceAdapter(TypeInfo type, ScriptableObject so) {
      Class<?> cl = type.asClass();
      if (cl == null) {
         throw new NullPointerException("type.asClass() must not be null");
      } else {
         Object key = Kit.makeHashKeyFromPair("Coerced Interface", cl);
         Object old = so.getAssociatedValue(key);
         if (old != null) {
            return old;
         } else {
            Object glue = InterfaceAdapter.create(this, cl, so);
            return so.associateValue(key, glue);
         }
      }
   }

   public Object javaToJS(Object value, Scriptable scope) {
      return this.javaToJS(value, scope, TypeInfo.NONE);
   }

   public Object javaToJS(Object value, Scriptable scope, TypeInfo target) {
      if (value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Scriptable) {
         return value;
      } else {
         return value instanceof Character ? String.valueOf(((Character)value).charValue()) : this.wrap(scope, value, target);
      }
   }

   public final Object jsToJava(@Nullable Object from, TypeInfo target) throws EvaluatorException {
      if (target == null || !target.shouldConvert()) {
         return Wrapper.unwrapped(from);
      } else if (target.is(TypeInfo.RAW_SET)) {
         return this.setOf(from, target.param(0));
      } else if (target.is(TypeInfo.RAW_MAP)) {
         return this.mapOf(from, target.param(0), target.param(1));
      } else if (target instanceof ArrayTypeInfo) {
         return this.arrayOf(from, target.componentType());
      } else if (List.class.isAssignableFrom(target.asClass())) {
         return this.listOf(from, target.param(0));
      } else if (target.is(TypeInfo.CLASS)) {
         return this.classOf(from);
      } else {
         return from != null && from.getClass() != target.asClass() ? this.internalJsToJava(from, target) : from;
      }
   }

   private static int getJSTypeCode(Object from) {
      if (from == null) {
         return 1;
      } else if (from == Undefined.INSTANCE) {
         return 0;
      } else if (from instanceof CharSequence) {
         return 4;
      } else if (from instanceof Number) {
         return 3;
      } else if (from instanceof Boolean) {
         return 2;
      } else if (from instanceof Scriptable) {
         return switch (from) {
            case NativeJavaClass ignore -> 5;
            case NativeJavaArray ignorex -> 7;
            case Wrapper ignorexx -> 6;
            default -> 8;
         };
      } else if (from instanceof Class) {
         return 5;
      } else {
         Class<?> valueClass = from.getClass();
         return valueClass.isArray() ? 7 : 6;
      }
   }

   protected Object internalJsToJava(Object from, TypeInfo target) {
      if (target instanceof ArrayTypeInfo) {
         TypeInfo arrayType = target.componentType();
         if (!(from instanceof NativeArray array)) {
            Object result = arrayType.newArray(1);
            Array.set(result, 0, this.jsToJava(from, arrayType));
            return result;
         } else {
            long length = array.getLength();
            Object result = arrayType.newArray((int)length);

            for (int i = 0; i < length; i++) {
               try {
                  Array.set(result, i, this.jsToJava(array.get(this, i, array), arrayType));
               } catch (EvaluatorException var10) {
                  return this.reportConversionError(from, target);
               }
            }

            return result;
         }
      } else {
         Object unwrappedValue = Wrapper.unwrapped(from);
         TypeWrapperFactory<?> typeWrapper = this.factory.getTypeWrappers().getWrapperFactory(unwrappedValue, target);
         if (typeWrapper != null) {
            return typeWrapper.wrap(this, unwrappedValue, target);
         } else {
            switch (getJSTypeCode(from)) {
               case 0:
                  if (target != TypeInfo.STRING && target != TypeInfo.OBJECT) {
                     return this.reportConversionError(from, target);
                  }

                  return "undefined";
               case 1:
                  if (target.isPrimitive()) {
                     return this.reportConversionError(from, target);
                  }

                  return null;
               case 2:
                  if (!target.isBoolean() && target != TypeInfo.OBJECT) {
                     if (target == TypeInfo.STRING) {
                        return from.toString();
                     }

                     return this.internalJsToJavaLast(from, target);
                  }

                  return from;
               case 3:
                  if (target == TypeInfo.STRING) {
                     return ScriptRuntime.toString(this, from);
                  } else if (target == TypeInfo.OBJECT) {
                     return this.coerceToNumber(TypeInfo.DOUBLE, from);
                  } else {
                     return (!target.isPrimitive() || target.isBoolean())
                           && !ScriptRuntime.NumberClass.isAssignableFrom(target.asClass())
                           && !target.isCharacter()
                        ? this.internalJsToJavaLast(from, target)
                        : this.coerceToNumber(target, from);
                  }
               case 4:
                  if (target != TypeInfo.STRING && !target.asClass().isInstance(from)) {
                     if (target.isCharacter()) {
                        if (((CharSequence)from).length() == 1) {
                           return ((CharSequence)from).charAt(0);
                        }

                        return this.coerceToNumber(target, from);
                     }

                     if ((!target.isPrimitive() || target.isBoolean()) && !ScriptRuntime.NumberClass.isAssignableFrom(target.asClass())) {
                        return this.internalJsToJavaLast(from, target);
                     }

                     return this.coerceToNumber(target, from);
                  }

                  return from.toString();
               case 5:
                  if (target != TypeInfo.CLASS && target != TypeInfo.OBJECT) {
                     if (target == TypeInfo.STRING) {
                        return unwrappedValue.toString();
                     }

                     return this.internalJsToJavaLast(unwrappedValue, target);
                  }

                  return unwrappedValue;
               case 6:
               case 7:
                  if (target.isPrimitive()) {
                     if (target.isBoolean()) {
                        return this.internalJsToJavaLast(unwrappedValue, target);
                     }

                     return this.coerceToNumber(target, unwrappedValue);
                  } else if (target == TypeInfo.STRING) {
                     return unwrappedValue.toString();
                  } else {
                     if (target.asClass().isInstance(unwrappedValue)) {
                        return unwrappedValue;
                     }

                     return this.internalJsToJavaLast(unwrappedValue, target);
                  }
               case 8:
                  if (target == TypeInfo.STRING) {
                     return ScriptRuntime.toString(this, from);
                  } else if (target.isPrimitive()) {
                     if (target.isBoolean()) {
                        return this.internalJsToJavaLast(from, target);
                     }

                     return this.coerceToNumber(target, from);
                  } else if (target.asClass().isInstance(from)) {
                     return from;
                  } else if (target == TypeInfo.DATE && from instanceof NativeDate) {
                     double time = ((NativeDate)from).getJSTimeValue();
                     return new Date((long)time);
                  } else if (from instanceof Wrapper) {
                     if (target.asClass().isInstance(unwrappedValue)) {
                        return unwrappedValue;
                     }

                     return this.internalJsToJavaLast(unwrappedValue, target);
                  } else {
                     if (!target.asClass().isInterface()
                        || !(from instanceof NativeObject) && (!(from instanceof Callable) || !(from instanceof ScriptableObject))) {
                        return this.internalJsToJavaLast(from, target);
                     }

                     return this.createInterfaceAdapter(target, (ScriptableObject)from);
                  }
               default:
                  return this.internalJsToJavaLast(from, target);
            }
         }
      }
   }

   protected Object internalJsToJavaLast(Object from, TypeInfo target) {
      return target instanceof TypeWrapperFactory<?> f ? f.wrap(this, from, target) : this.reportConversionError(from, target);
   }

   public final boolean canConvert(Object from, TypeInfo target) {
      return this.getConversionWeight(from, target) < 99;
   }

   public final int getConversionWeight(Object from, TypeInfo target) {
      int fcw = this.internalConversionWeight(from, target);
      if (fcw != 99) {
         return fcw;
      } else if (target instanceof ArrayTypeInfo || Collection.class.isAssignableFrom(target.asClass())) {
         return 0;
      } else if (target.is(TypeInfo.CLASS)) {
         return !(from instanceof Class) && !(from instanceof NativeJavaClass) ? 0 : 1;
      } else {
         if (from == null) {
            if (!target.isPrimitive()) {
               return 1;
            }
         } else if (from == Undefined.INSTANCE) {
            if (target == TypeInfo.STRING || target == TypeInfo.OBJECT) {
               return 1;
            }
         } else if (from instanceof CharSequence) {
            if (target == TypeInfo.STRING) {
               return 1;
            }

            if (target.asClass().isInstance(from)) {
               return 2;
            }

            if (target.isPrimitive()) {
               if (target.isCharacter()) {
                  return 3;
               }

               if (!target.isBoolean()) {
                  return 4;
               }
            }
         } else if (from instanceof Number) {
            if (target.isPrimitive()) {
               if (target.isDouble()) {
                  return 1;
               }

               if (!target.isBoolean()) {
                  return 1 + getSizeRank(target);
               }
            } else {
               if (target == TypeInfo.STRING) {
                  return 9;
               }

               if (target.asClass() == Object.class) {
                  return 10;
               }

               if (ScriptRuntime.NumberClass.isAssignableFrom(target.asClass())) {
                  return 2;
               }
            }
         } else if (from instanceof Boolean) {
            if (target.isBoolean()) {
               return 1;
            }

            if (target.asClass() == Object.class) {
               return 3;
            }

            if (target == TypeInfo.STRING) {
               return 4;
            }
         } else if (from instanceof Class || from instanceof NativeJavaClass) {
            if (target.is(TypeInfo.CLASS)) {
               return 0;
            }

            if (target == TypeInfo.OBJECT) {
               return 3;
            }

            if (target == TypeInfo.STRING) {
               return 4;
            }
         }

         int fromCode = getJSTypeCode(from);
         switch (fromCode) {
            case 6:
            case 7:
               Object javaObj = Wrapper.unwrapped(from);
               if (target.asClass().isInstance(javaObj)) {
                  return 0;
               } else if (target == TypeInfo.STRING) {
                  return 2;
               } else if (target.isPrimitive() && !target.isBoolean()) {
                  return fromCode == 7 ? 99 : 2 + getSizeRank(target);
               } else {
                  if (target instanceof ArrayTypeInfo) {
                     return 3;
                  }

                  return this.internalConversionWeightLast(from, target);
               }
            case 8:
               if (target != TypeInfo.OBJECT && target.asClass().isInstance(from)) {
                  return 1;
               } else if (target instanceof ArrayTypeInfo) {
                  if (from instanceof NativeArray) {
                     return 2;
                  }

                  return 1;
               } else if (target == TypeInfo.OBJECT) {
                  return 3;
               } else if (target == TypeInfo.STRING) {
                  return 4;
               } else if (target == TypeInfo.DATE) {
                  if (from instanceof NativeDate) {
                     return 1;
                  }
               } else if (target.isFunctionalInterface()) {
                  if (from instanceof NativeFunction) {
                     return 1;
                  } else {
                     if (from instanceof NativeObject) {
                        return 2;
                     }

                     return 12;
                  }
               } else if (target.asClass().isInterface() && from instanceof NativeObject) {
                  return 3;
               } else if (target.isPrimitive() && !target.isBoolean()) {
                  return 4 + getSizeRank(target);
               }
            default:
               return this.internalConversionWeightLast(from, target);
         }
      }
   }

   public static int getSizeRank(TypeInfo aType) {
      if (aType.isDouble()) {
         return 1;
      } else if (aType.isFloat()) {
         return 2;
      } else if (aType.isLong()) {
         return 3;
      } else if (aType.isInt()) {
         return 4;
      } else if (aType.isShort()) {
         return 5;
      } else if (aType.isCharacter()) {
         return 6;
      } else if (aType.isByte()) {
         return 7;
      } else {
         return aType.isBoolean() ? 99 : 8;
      }
   }

   protected Object coerceToNumber(TypeInfo target, Object value) {
      Class<?> valueClass = value.getClass();
      if (target.isCharacter()) {
         return valueClass == ScriptRuntime.CharacterClass ? value : (char)this.toInteger(value, target, 0.0, 65535.0);
      } else if (target != TypeInfo.OBJECT && !target.isDouble()) {
         if (target.isFloat()) {
            if (valueClass == ScriptRuntime.FloatClass) {
               return value;
            } else {
               double number = this.toDouble(value);
               if (!Double.isInfinite(number) && !Double.isNaN(number) && number != 0.0) {
                  double absNumber = Math.abs(number);
                  if (absNumber < 1.401298464324817E-45) {
                     return number > 0.0 ? 0.0F : -0.0F;
                  } else {
                     return absNumber > 3.4028234663852886E38 ? number > 0.0 ? 1.0F / 0.0F : -1.0F / 0.0F : (float)number;
                  }
               } else {
                  return (float)number;
               }
            }
         } else if (target.isInt()) {
            return valueClass == ScriptRuntime.IntegerClass ? value : (int)this.toInteger(value, target, -2.147483648E9, 2.147483647E9);
         } else if (target.isLong()) {
            if (valueClass == ScriptRuntime.LongClass) {
               return value;
            } else {
               double max = Double.longBitsToDouble(4890909195324358655L);
               double min = Double.longBitsToDouble(-4332462841530417152L);
               return this.toInteger(value, target, min, max);
            }
         } else if (target.isShort()) {
            return valueClass == ScriptRuntime.ShortClass ? value : (short)this.toInteger(value, target, -32768.0, 32767.0);
         } else if (!target.isByte()) {
            return this.toDouble(value);
         } else {
            return valueClass == ScriptRuntime.ByteClass ? value : (byte)this.toInteger(value, target, -128.0, 127.0);
         }
      } else {
         return valueClass == ScriptRuntime.DoubleClass ? value : this.toDouble(value);
      }
   }

   protected double toDouble(Object value) {
      if (value instanceof Number) {
         return ((Number)value).doubleValue();
      } else if (value instanceof String) {
         return ScriptRuntime.toNumber(this, (String)value);
      } else if (value instanceof Scriptable) {
         return value instanceof Wrapper ? this.toDouble(((Wrapper)value).unwrap()) : ScriptRuntime.toNumber(this, value);
      } else {
         return value instanceof DoubleSupplier ? ((DoubleSupplier)value).getAsDouble() : ScriptRuntime.toNumber(this, value.toString());
      }
   }

   protected long toInteger(Object value, TypeInfo type, double min, double max) {
      double d = this.toDouble(value);
      if (Double.isInfinite(d) || Double.isNaN(d)) {
         this.reportConversionError(ScriptRuntime.toString(this, value), type);
      }

      if (d > 0.0) {
         d = Math.floor(d);
      } else {
         d = Math.ceil(d);
      }

      if (d < min || d > max) {
         this.reportConversionError(ScriptRuntime.toString(this, value), type);
      }

      return (long)d;
   }

   public Object reportConversionError(Object value, TypeInfo type) {
      throw reportRuntimeError2("msg.conversion.not.allowed", String.valueOf(value), type.signature(), this);
   }

   public String defaultObjectToSource(Scriptable scope, Scriptable thisObj, Object[] args) {
      return "not_supported";
   }

   public void initJSON(ScriptableObject scope, boolean sealed) {
      try {
         NativeGSON.initGSON(scope, sealed, this);
      } catch (Throwable var4) {
         NativeJSON.init(scope, sealed, this);
      }
   }

   public CachedClassStorage getCachedClassStorage(boolean includeProtected) {
      return includeProtected ? CachedClassStorage.GLOBAL_PROTECTED : this.factory.getCachedClassStorage();
   }
}
