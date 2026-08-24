package corgitaco.corgilib.shadow.blue.endless.jankson;

import corgitaco.corgilib.shadow.blue.endless.jankson.api.DeserializationException;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.DeserializerFunction;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.Marshaller;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.SyntaxError;
import corgitaco.corgilib.shadow.blue.endless.jankson.impl.AnnotatedElement;
import corgitaco.corgilib.shadow.blue.endless.jankson.impl.ElementParserContext;
import corgitaco.corgilib.shadow.blue.endless.jankson.impl.MarshallerImpl;
import corgitaco.corgilib.shadow.blue.endless.jankson.impl.ObjectParserContext;
import corgitaco.corgilib.shadow.blue.endless.jankson.impl.ParserContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class Jankson {
   private Deque<Jankson.ParserFrame<?>> contextStack = new ArrayDeque<>();
   private JsonObject root;
   private int line = 0;
   private int column = 0;
   private int withheldCodePoint = -1;
   private Marshaller marshaller = MarshallerImpl.getFallback();
   private boolean allowBareRootObject = false;
   private int retries = 0;
   private SyntaxError delayedError = null;
   private AnnotatedElement rootElement;

   private Jankson(Jankson.Builder builder) {
   }

   @Nonnull
   public JsonObject load(String s) throws SyntaxError {
      ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));

      try {
         return this.load(in);
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   @Nonnull
   public JsonObject load(File f) throws IOException, SyntaxError {
      InputStream in = new FileInputStream(f);

      JsonObject var3;
      try {
         var3 = this.load(in);
      } catch (Throwable var6) {
         try {
            in.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      in.close();
      return var3;
   }

   @Nonnull
   public JsonObject load(InputStream in) throws IOException, SyntaxError {
      InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      this.withheldCodePoint = -1;
      this.root = null;
      this.push(new ObjectParserContext(this.allowBareRootObject), it -> this.root = it);

      while (this.root == null) {
         if (this.delayedError != null) {
            throw this.delayedError;
         }

         if (this.withheldCodePoint != -1) {
            this.retries++;
            if (this.retries > 25) {
               throw new IOException("Parser got stuck near line " + this.line + " column " + this.column);
            }

            this.processCodePoint(this.withheldCodePoint);
         } else {
            int inByte = reader.read();
            if (inByte == -1) {
               while (!this.contextStack.isEmpty()) {
                  Jankson.ParserFrame<?> frame = this.contextStack.pop();

                  try {
                     frame.context.eof();
                     if (frame.context.isComplete()) {
                        frame.supply();
                     }
                  } catch (SyntaxError var6) {
                     var6.setStartParsing(frame.startLine, frame.startCol);
                     var6.setEndParsing(this.line, this.column);
                     throw var6;
                  }
               }

               if (this.root == null) {
                  this.root = new JsonObject();
                  this.root.marshaller = this.marshaller;
               }

               return this.root;
            }

            this.processCodePoint(inByte);
         }
      }

      return this.root;
   }

   @Nonnull
   public JsonElement loadElement(String s) throws SyntaxError {
      ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));

      try {
         return this.loadElement(in);
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   @Nonnull
   public JsonElement loadElement(File f) throws IOException, SyntaxError {
      InputStream in = new FileInputStream(f);

      JsonElement var3;
      try {
         var3 = this.loadElement(in);
      } catch (Throwable var6) {
         try {
            in.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      in.close();
      return var3;
   }

   @Nonnull
   public JsonElement loadElement(InputStream in) throws IOException, SyntaxError {
      InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      this.withheldCodePoint = -1;
      this.rootElement = null;
      this.push(new ElementParserContext(), it -> this.rootElement = it);

      while (this.rootElement == null) {
         if (this.delayedError != null) {
            throw this.delayedError;
         }

         if (this.withheldCodePoint != -1) {
            this.retries++;
            if (this.retries > 25) {
               throw new IOException("Parser got stuck near line " + this.line + " column " + this.column);
            }

            this.processCodePoint(this.withheldCodePoint);
         } else {
            int inByte = reader.read();
            if (inByte == -1) {
               while (!this.contextStack.isEmpty()) {
                  Jankson.ParserFrame<?> frame = this.contextStack.pop();

                  try {
                     frame.context.eof();
                     if (frame.context.isComplete()) {
                        frame.supply();
                     }
                  } catch (SyntaxError var6) {
                     var6.setStartParsing(frame.startLine, frame.startCol);
                     var6.setEndParsing(this.line, this.column);
                     throw var6;
                  }
               }

               if (this.rootElement == null) {
                  return JsonNull.INSTANCE;
               }

               return this.rootElement.getElement();
            }

            this.processCodePoint(inByte);
         }
      }

      return this.rootElement.getElement();
   }

   public <T> T fromJson(JsonObject obj, Class<T> clazz) {
      return this.marshaller.marshall(clazz, obj);
   }

   public <T> T fromJson(String json, Class<T> clazz) throws SyntaxError {
      JsonObject obj = this.load(json);
      return this.fromJson(obj, clazz);
   }

   public <T> T fromJsonCarefully(String json, Class<T> clazz) throws SyntaxError, DeserializationException {
      JsonObject obj = this.load(json);
      return this.fromJsonCarefully(obj, clazz);
   }

   public <T> T fromJsonCarefully(JsonObject obj, Class<T> clazz) throws DeserializationException {
      return this.marshaller.marshallCarefully(clazz, obj);
   }

   public <T> JsonElement toJson(T t) {
      return this.marshaller.serialize(t);
   }

   public <T> JsonElement toJson(T t, Marshaller alternateMarshaller) {
      return alternateMarshaller.serialize(t);
   }

   private void processCodePoint(int codePoint) throws SyntaxError {
      Jankson.ParserFrame<?> frame = this.contextStack.peek();
      if (frame == null) {
         throw new IllegalStateException("Parser problem! The ParserContext stack underflowed! (line " + this.line + ", col " + this.column + ")");
      } else {
         try {
            if (frame.context().isComplete()) {
               this.contextStack.pop();
               frame.supply();
               frame = this.contextStack.peek();
            }
         } catch (SyntaxError var5) {
            var5.setStartParsing(frame.startLine, frame.startCol);
            var5.setEndParsing(this.line, this.column);
            throw var5;
         }

         try {
            if (frame == null) {
               return;
            }

            boolean consumed = frame.context().consume(codePoint, this);
            if (frame.context.isComplete()) {
               this.contextStack.pop();
               frame.supply();
            }

            if (consumed) {
               this.withheldCodePoint = -1;
               this.retries = 0;
            } else {
               this.withheldCodePoint = codePoint;
            }
         } catch (SyntaxError var4) {
            var4.setStartParsing(frame.startLine, frame.startCol);
            var4.setEndParsing(this.line, this.column);
            throw var4;
         }

         this.column++;
         if (codePoint == 10) {
            this.line++;
            this.column = 0;
         }
      }
   }

   public <T> void push(ParserContext<T> t, Consumer<T> consumer) {
      Jankson.ParserFrame<T> frame = new Jankson.ParserFrame<>(t, consumer);
      frame.startLine = this.line;
      frame.startCol = this.column;
      this.contextStack.push(frame);
   }

   public Marshaller getMarshaller() {
      return this.marshaller;
   }

   public static Jankson.Builder builder() {
      return new Jankson.Builder();
   }

   public void throwDelayed(SyntaxError syntaxError) {
      syntaxError.setEndParsing(this.line, this.column);
      this.delayedError = syntaxError;
   }

   public static class Builder {
      MarshallerImpl marshaller = new MarshallerImpl();
      boolean allowBareRootObject = false;

      @Deprecated
      public <T> Jankson.Builder registerTypeAdapter(Class<T> clazz, Function<JsonObject, T> adapter) {
         this.marshaller.registerTypeAdapter(clazz, adapter);
         return this;
      }

      @Deprecated
      public <T> Jankson.Builder registerPrimitiveTypeAdapter(Class<T> clazz, Function<Object, T> adapter) {
         this.marshaller.register(clazz, adapter);
         return this;
      }

      public <T> Jankson.Builder registerSerializer(Class<T> clazz, BiFunction<T, Marshaller, JsonElement> serializer) {
         this.marshaller.registerSerializer(clazz, serializer);
         return this;
      }

      public <A, B> Jankson.Builder registerDeserializer(Class<A> sourceClass, Class<B> targetClass, DeserializerFunction<A, B> function) {
         this.marshaller.registerDeserializer(sourceClass, targetClass, function);
         return this;
      }

      public <T> Jankson.Builder registerTypeFactory(Class<T> clazz, Supplier<T> factory) {
         this.marshaller.registerTypeFactory(clazz, factory);
         return this;
      }

      public Jankson.Builder allowBareRootObject() {
         this.allowBareRootObject = true;
         return this;
      }

      public Jankson build() {
         Jankson result = new Jankson(this);
         result.marshaller = this.marshaller;
         result.allowBareRootObject = this.allowBareRootObject;
         return result;
      }
   }

   private static class ParserFrame<T> {
      private ParserContext<T> context;
      private Consumer<T> consumer;
      private int startLine = 0;
      private int startCol = 0;

      public ParserFrame(ParserContext<T> context, Consumer<T> consumer) {
         this.context = context;
         this.consumer = consumer;
      }

      public ParserContext<T> context() {
         return this.context;
      }

      public void supply() throws SyntaxError {
         this.consumer.accept(this.context.getResult());
      }
   }
}
