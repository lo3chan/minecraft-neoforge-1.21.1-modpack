package corgitaco.corgilib.shadow.blue.endless.jankson.impl;

import corgitaco.corgilib.shadow.blue.endless.jankson.Jankson;
import corgitaco.corgilib.shadow.blue.endless.jankson.JsonObject;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.SyntaxError;

public class ObjectParserContext implements ParserContext<JsonObject> {
   private JsonObject result = new JsonObject();
   private final boolean assumeOpen;
   private String comment;
   private boolean openBraceFound = false;
   private boolean noOpenBrace = false;
   private String key;
   private boolean colonFound = false;
   private boolean closeBraceFound = false;
   private boolean eof = false;

   public ObjectParserContext(boolean assumeOpen) {
      this.assumeOpen = assumeOpen;
   }

   @Override
   public boolean consume(int codePoint, Jankson loader) throws SyntaxError {
      this.result.setMarshaller(loader.getMarshaller());
      if (!this.openBraceFound) {
         if (Character.isWhitespace(codePoint)) {
            return true;
         }

         if (codePoint == 47 || codePoint == 35) {
            loader.push(new CommentParserContext(codePoint), it -> {
               if (this.comment == null) {
                  this.comment = it;
               } else {
                  this.comment = this.comment + "\n" + it;
               }
            });
            return true;
         }

         if (codePoint == 123) {
            this.openBraceFound = true;
            return true;
         }

         if (!this.assumeOpen) {
            throw new SyntaxError("Found character '" + (char)codePoint + "' instead of '{' while looking for the start of an object");
         }

         this.openBraceFound = true;
         this.noOpenBrace = true;
      }

      if (this.closeBraceFound) {
         return false;
      } else if (this.key == null) {
         if (!Character.isWhitespace(codePoint) && codePoint != 44) {
            switch (codePoint) {
               case 34:
               case 39:
                  loader.push(new StringParserContext(codePoint), it -> this.key = it.asString());
                  return true;
               case 35:
               case 47:
                  loader.push(new CommentParserContext(codePoint), it -> {
                     if (this.comment == null) {
                        this.comment = it;
                     } else {
                        this.comment = this.comment + "\n" + it;
                     }
                  });
                  return true;
               case 44:
                  return true;
               case 123:
                  loader.throwDelayed(new SyntaxError("Found spurious '{' while parsing an object."));
                  return true;
               case 125:
                  if (this.noOpenBrace) {
                     throw new SyntaxError("Found spurious '}' while parsing an object with no open brace.");
                  }

                  this.closeBraceFound = true;
                  return true;
               default:
                  loader.push(new TokenParserContext(codePoint), it -> this.key = it.asString());
                  return true;
            }
         } else {
            return true;
         }
      } else if (this.colonFound) {
         String elemKey = this.key;
         loader.push(new ElementParserContext(), it -> {
            String resolvedComment = "";
            if (this.comment != null) {
               resolvedComment = resolvedComment + this.comment;
            }

            if (this.comment != null && it.getComment() != null) {
               resolvedComment = resolvedComment + '\n';
            }

            if (it.getComment() != null) {
               resolvedComment = resolvedComment + it.getComment();
            }

            this.result.put(elemKey, it.getElement(), resolvedComment);
            this.key = null;
            this.colonFound = false;
            this.comment = null;
         });
         return false;
      } else if (Character.isWhitespace(codePoint)) {
         return true;
      } else if (codePoint == 58) {
         this.colonFound = true;
         return true;
      } else {
         throw new SyntaxError("Found unexpected character '" + (char)codePoint + "' while looking for the colon (':') between a key and a value in an object");
      }
   }

   @Override
   public boolean isComplete() {
      return this.closeBraceFound || this.assumeOpen && this.noOpenBrace && this.eof;
   }

   public JsonObject getResult() {
      return this.result;
   }

   @Override
   public void eof() throws SyntaxError {
      this.eof = true;
      if (!this.assumeOpen || !this.noOpenBrace) {
         if (!this.closeBraceFound) {
            throw new SyntaxError("Expected to find '}' to end an object, found EOF instead.");
         }
      }
   }
}
