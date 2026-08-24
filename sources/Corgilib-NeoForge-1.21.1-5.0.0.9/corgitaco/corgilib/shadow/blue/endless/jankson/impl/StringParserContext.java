package corgitaco.corgilib.shadow.blue.endless.jankson.impl;

import corgitaco.corgilib.shadow.blue.endless.jankson.Jankson;
import corgitaco.corgilib.shadow.blue.endless.jankson.JsonPrimitive;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.SyntaxError;
import java.util.Locale;

public class StringParserContext implements ParserContext<JsonPrimitive> {
   private static final String HEX_DIGITS = "0123456789abcdefABCDEF";
   private int quote;
   private boolean escape = false;
   private int unicodeUs = 0;
   private StringBuilder builder = new StringBuilder();
   private boolean complete = false;
   private String unicodeSequence = "";

   public StringParserContext(int quote) {
      this.quote = quote;
   }

   @Override
   public boolean consume(int codePoint, Jankson loader) {
      if (this.escape) {
         if (this.unicodeUs > 0) {
            if (codePoint != 117 && codePoint != 85) {
               if ("0123456789abcdefABCDEF".indexOf(codePoint) != -1) {
                  this.unicodeSequence = this.unicodeSequence + (char)codePoint;
                  if (this.unicodeSequence.length() == 4) {
                     this.emitUnicodeSequence(loader);
                     this.escape = false;
                  }

                  return true;
               } else {
                  this.emitUnicodeSequence(loader);
                  this.escape = false;
                  return false;
               }
            } else {
               this.unicodeUs++;
               return true;
            }
         } else {
            this.escape = false;
            switch (codePoint) {
               case 10:
                  return true;
               case 34:
                  this.builder.append('"');
                  return true;
               case 39:
                  this.builder.append('\'');
                  return true;
               case 85:
               case 117:
                  this.escape = true;
                  this.unicodeUs = 1;
                  return true;
               case 92:
                  this.builder.append('\\');
                  return true;
               case 98:
                  this.builder.append('\b');
                  return true;
               case 102:
                  this.builder.append('\f');
                  return true;
               case 110:
                  this.builder.append('\n');
                  return true;
               case 114:
                  this.builder.append('\r');
                  return true;
               case 116:
                  this.builder.append('\t');
                  return true;
               default:
                  this.builder.append((char)codePoint);
                  return true;
            }
         }
      } else if (codePoint == this.quote) {
         this.complete = true;
         return true;
      } else if (codePoint == 92) {
         this.escape = true;
         return true;
      } else if (codePoint == 10) {
         this.complete = true;
         return false;
      } else if (codePoint < 65535) {
         this.builder.append((char)codePoint);
         return true;
      } else {
         int temp = codePoint - 65536;
         int highSurrogate = (temp >>> 10) + 55296;
         int lowSurrogate = (temp & 1023) + 56320;
         this.builder.append((char)highSurrogate);
         this.builder.append((char)lowSurrogate);
         return true;
      }
   }

   private void emitUnicodeSequence(Jankson loader) {
      if (this.unicodeUs > 1) {
         this.unicodeUs--;
         this.builder.append("\\");

         for (int i = 0; i < this.unicodeUs; i++) {
            this.builder.append('u');
         }

         while (this.unicodeSequence.length() < 4) {
            this.unicodeSequence = "0" + this.unicodeSequence;
         }

         this.builder.append(this.unicodeSequence.toLowerCase(Locale.ROOT));
      } else {
         int sequence = (int)Long.parseLong(this.unicodeSequence, 16);
         char[] chars = Character.toChars(sequence);

         for (char ch : chars) {
            this.builder.append(ch);
         }
      }

      this.unicodeUs = 0;
      this.unicodeSequence = "";
      this.escape = false;
   }

   @Override
   public boolean isComplete() {
      return this.complete;
   }

   public JsonPrimitive getResult() {
      return JsonPrimitive.of(this.builder.toString());
   }

   @Override
   public void eof() throws SyntaxError {
      throw new SyntaxError("Expected to find '" + (char)this.quote + "' to end a String, found EOF instead.");
   }
}
