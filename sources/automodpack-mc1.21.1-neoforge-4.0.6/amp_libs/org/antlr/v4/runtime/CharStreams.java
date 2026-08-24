package amp_libs.org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CharStreams {
   private static final int DEFAULT_BUFFER_SIZE = 4096;

   private CharStreams() {
   }

   public static CharStream fromPath(Path path) throws IOException {
      return fromPath(path, StandardCharsets.UTF_8);
   }

   public static CharStream fromPath(Path path, Charset charset) throws IOException {
      long size = Files.size(path);
      ReadableByteChannel channel = Files.newByteChannel(path);

      CodePointCharStream var5;
      try {
         var5 = fromChannel(channel, charset, 4096, CodingErrorAction.REPLACE, path.toString(), size);
      } catch (Throwable var8) {
         if (channel != null) {
            try {
               channel.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (channel != null) {
         channel.close();
      }

      return var5;
   }

   public static CharStream fromFileName(String fileName) throws IOException {
      return fromPath(Paths.get(fileName), StandardCharsets.UTF_8);
   }

   public static CharStream fromFileName(String fileName, Charset charset) throws IOException {
      return fromPath(Paths.get(fileName), charset);
   }

   public static CharStream fromStream(InputStream is) throws IOException {
      return fromStream(is, StandardCharsets.UTF_8);
   }

   public static CharStream fromStream(InputStream is, Charset charset) throws IOException {
      return fromStream(is, charset, -1L);
   }

   public static CharStream fromStream(InputStream is, Charset charset, long inputSize) throws IOException {
      ReadableByteChannel channel = Channels.newChannel(is);

      CodePointCharStream var5;
      try {
         var5 = fromChannel(channel, charset, 4096, CodingErrorAction.REPLACE, "<unknown>", inputSize);
      } catch (Throwable var8) {
         if (channel != null) {
            try {
               channel.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (channel != null) {
         channel.close();
      }

      return var5;
   }

   public static CharStream fromChannel(ReadableByteChannel channel) throws IOException {
      return fromChannel(channel, StandardCharsets.UTF_8);
   }

   public static CharStream fromChannel(ReadableByteChannel channel, Charset charset) throws IOException {
      return fromChannel(channel, 4096, CodingErrorAction.REPLACE, "<unknown>");
   }

   public static CodePointCharStream fromReader(Reader r) throws IOException {
      return fromReader(r, "<unknown>");
   }

   public static CodePointCharStream fromReader(Reader r, String sourceName) throws IOException {
      CodePointCharStream var4;
      try {
         CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder(4096);
         CharBuffer charBuffer = CharBuffer.allocate(4096);

         while (r.read(charBuffer) != -1) {
            ((Buffer)charBuffer).flip();
            codePointBufferBuilder.append(charBuffer);
            charBuffer.compact();
         }

         var4 = CodePointCharStream.fromBuffer(codePointBufferBuilder.build(), sourceName);
      } finally {
         r.close();
      }

      return var4;
   }

   public static CodePointCharStream fromString(String s) {
      return fromString(s, "<unknown>");
   }

   public static CodePointCharStream fromString(String s, String sourceName) {
      CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder(s.length());
      CharBuffer cb = CharBuffer.allocate(s.length());
      cb.put(s);
      ((Buffer)cb).flip();
      codePointBufferBuilder.append(cb);
      return CodePointCharStream.fromBuffer(codePointBufferBuilder.build(), sourceName);
   }

   public static CodePointCharStream fromChannel(ReadableByteChannel channel, int bufferSize, CodingErrorAction decodingErrorAction, String sourceName) throws IOException {
      return fromChannel(channel, StandardCharsets.UTF_8, bufferSize, decodingErrorAction, sourceName, -1L);
   }

   public static CodePointCharStream fromChannel(
      ReadableByteChannel channel, Charset charset, int bufferSize, CodingErrorAction decodingErrorAction, String sourceName, long inputSize
   ) throws IOException {
      CodePointCharStream var14;
      try {
         ByteBuffer utf8BytesIn = ByteBuffer.allocate(bufferSize);
         CharBuffer utf16CodeUnitsOut = CharBuffer.allocate(bufferSize);
         if (inputSize == -1L) {
            inputSize = bufferSize;
         } else if (inputSize > 2147483647L) {
            throw new IOException(String.format("inputSize %d larger than max %d", inputSize, 2147483647));
         }

         CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder((int)inputSize);
         CharsetDecoder decoder = charset.newDecoder().onMalformedInput(decodingErrorAction).onUnmappableCharacter(decodingErrorAction);
         boolean endOfInput = false;

         while (!endOfInput) {
            int bytesRead = channel.read(utf8BytesIn);
            endOfInput = bytesRead == -1;
            ((Buffer)utf8BytesIn).flip();
            CoderResult result = decoder.decode(utf8BytesIn, utf16CodeUnitsOut, endOfInput);
            if (result.isError() && decodingErrorAction.equals(CodingErrorAction.REPORT)) {
               result.throwException();
            }

            ((Buffer)utf16CodeUnitsOut).flip();
            codePointBufferBuilder.append(utf16CodeUnitsOut);
            utf8BytesIn.compact();
            utf16CodeUnitsOut.compact();
         }

         CoderResult flushResult = decoder.flush(utf16CodeUnitsOut);
         if (flushResult.isError() && decodingErrorAction.equals(CodingErrorAction.REPORT)) {
            flushResult.throwException();
         }

         ((Buffer)utf16CodeUnitsOut).flip();
         codePointBufferBuilder.append(utf16CodeUnitsOut);
         CodePointBuffer codePointBuffer = codePointBufferBuilder.build();
         var14 = CodePointCharStream.fromBuffer(codePointBuffer, sourceName);
      } finally {
         channel.close();
      }

      return var14;
   }
}
