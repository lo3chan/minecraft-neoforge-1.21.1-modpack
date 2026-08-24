package DistantHorizons.libraries.electronwill.nightconfig.core.io;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

class CharsetUnicodeBom extends Charset {
   private boolean utf8Only;

   protected CharsetUnicodeBom(boolean utf8Only) {
      super(utf8Only ? "UTF-8" : "UTF-8-autodetect", new String[]{"UTF-8"});
   }

   @Override
   public boolean canEncode() {
      return true;
   }

   @Override
   public boolean contains(Charset cs) {
      return StandardCharsets.UTF_8.contains(cs);
   }

   @Override
   public CharsetDecoder newDecoder() {
      return new CharsetUnicodeBom.Decoder(this);
   }

   @Override
   public CharsetEncoder newEncoder() {
      return new CharsetUnicodeBom.Encoder(this);
   }

   private static final class Decoder extends CharsetDecoder {
      private boolean utf8Only;
      private CharsetDecoder decoder = null;

      Decoder(CharsetUnicodeBom cs) {
         super(cs, 0.5F, 1.0F);
         this.utf8Only = cs.utf8Only;
      }

      private void setupDecoder(Charset detectedCharset) {
         this.decoder = detectedCharset.newDecoder()
            .onMalformedInput(this.malformedInputAction())
            .onUnmappableCharacter(this.unmappableCharacterAction())
            .replaceWith(this.replacement());
      }

      @Override
      public boolean isAutoDetecting() {
         return true;
      }

      @Override
      public boolean isCharsetDetected() {
         return this.decoder != null;
      }

      @Override
      public Charset detectedCharset() {
         return this.decoder.charset();
      }

      @Override
      protected CoderResult implFlush(CharBuffer out) {
         return this.decoder.flush(out);
      }

      @Override
      protected void implReset() {
         this.decoder.reset();
      }

      @Override
      protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
         if (this.decoder == null) {
            int newPosition = in.position();

            CoderResult b3;
            try {
               if (in.remaining() < 2) {
                  return CoderResult.UNDERFLOW;
               }

               int b1 = in.get() & 255;
               int b2 = in.get() & 255;
               if (b1 == 254 && b2 == 255) {
                  if (this.utf8Only) {
                     throw new ParsingException("Invalid input: it begins with an UTF-16 BE byte-order mark, but it should be plain UTF-8.");
                  }

                  this.setupDecoder(StandardCharsets.UTF_16BE);
                  newPosition += 2;
                  return this.decoder.decode(in, out, false);
               }

               if (b1 == 255 && b2 == 254) {
                  if (this.utf8Only) {
                     throw new ParsingException("Invalid input: it begins with an UTF-16 LE byte-order mark, but it should be plain UTF-8.");
                  }

                  this.setupDecoder(StandardCharsets.UTF_16LE);
                  newPosition += 2;
                  return this.decoder.decode(in, out, false);
               }

               if (b1 != 239 || b2 != 187) {
                  this.setupDecoder(StandardCharsets.UTF_8);
                  return this.decoder.decode(in, out, false);
               }

               if (in.hasRemaining()) {
                  int b3x = in.get() & 255;
                  if (b3x == 191) {
                     newPosition += 3;
                  }

                  this.setupDecoder(StandardCharsets.UTF_8);
                  return this.decoder.decode(in, out, false);
               }

               b3 = CoderResult.UNDERFLOW;
            } finally {
               ((Buffer)in).position(newPosition);
            }

            return b3;
         } else {
            return this.decoder.decode(in, out, false);
         }
      }
   }

   private static final class Encoder extends CharsetEncoder {
      private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

      Encoder(CharsetUnicodeBom cs) {
         super(cs, 1.1F, 3.0F);
      }

      @Override
      public boolean canEncode(char c) {
         return this.encoder.canEncode(c);
      }

      @Override
      public boolean canEncode(CharSequence cs) {
         return this.encoder.canEncode(cs);
      }

      @Override
      public boolean isLegalReplacement(byte[] repl) {
         return this.encoder.isLegalReplacement(repl);
      }

      @Override
      protected void implReset() {
         this.encoder.reset();
      }

      @Override
      protected CoderResult implFlush(ByteBuffer out) {
         return this.encoder.flush(out);
      }

      @Override
      protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
         return this.encoder.encode(in, out, false);
      }
   }
}
