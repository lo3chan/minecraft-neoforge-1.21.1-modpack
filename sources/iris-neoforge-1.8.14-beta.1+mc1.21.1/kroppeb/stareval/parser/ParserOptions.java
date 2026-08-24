package kroppeb.stareval.parser;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

public final class ParserOptions {
   private final Char2ObjectMap<? extends OpResolver<? extends UnaryOp>> unaryOpResolvers;
   private final Char2ObjectMap<? extends OpResolver<? extends BinaryOp>> binaryOpResolvers;
   private final ParserOptions.TokenRules tokenRules;

   private ParserOptions(
      Char2ObjectMap<? extends OpResolver<? extends UnaryOp>> unaryOpResolvers,
      Char2ObjectMap<? extends OpResolver<? extends BinaryOp>> binaryOpResolvers,
      ParserOptions.TokenRules tokenRules
   ) {
      this.unaryOpResolvers = unaryOpResolvers;
      this.binaryOpResolvers = binaryOpResolvers;
      this.tokenRules = tokenRules;
   }

   ParserOptions.TokenRules getTokenRules() {
      return this.tokenRules;
   }

   OpResolver<? extends UnaryOp> getUnaryOpResolver(char c) {
      return (OpResolver<? extends UnaryOp>)this.unaryOpResolvers.get(c);
   }

   OpResolver<? extends BinaryOp> getBinaryOpResolver(char c) {
      return (OpResolver<? extends BinaryOp>)this.binaryOpResolvers.get(c);
   }

   public static class Builder {
      private final Char2ObjectMap<OpResolver.Builder<UnaryOp>> unaryOpResolvers = new Char2ObjectOpenHashMap();
      private final Char2ObjectMap<OpResolver.Builder<BinaryOp>> binaryOpResolvers = new Char2ObjectOpenHashMap();
      private ParserOptions.TokenRules tokenRules = ParserOptions.TokenRules.DEFAULT;

      private static <T> Char2ObjectMap<? extends OpResolver<? extends T>> buildOpResolvers(Char2ObjectMap<OpResolver.Builder<T>> ops) {
         Char2ObjectMap<OpResolver<T>> result = new Char2ObjectOpenHashMap();
         ops.char2ObjectEntrySet().forEach(entry -> result.put(entry.getCharKey(), ((OpResolver.Builder)entry.getValue()).build()));
         return result;
      }

      public void addUnaryOp(String s, UnaryOp op) {
         char first = s.charAt(0);
         String trailing = s.substring(1);
         ((OpResolver.Builder)this.unaryOpResolvers.computeIfAbsent(first, c -> new OpResolver.Builder())).multiChar(trailing, op);
      }

      public void addBinaryOp(String s, BinaryOp op) {
         char first = s.charAt(0);
         String trailing = s.substring(1);
         ((OpResolver.Builder)this.binaryOpResolvers.computeIfAbsent(first, c -> new OpResolver.Builder())).multiChar(trailing, op);
      }

      public void setTokenRules(ParserOptions.TokenRules tokenRules) {
         this.tokenRules = tokenRules;
      }

      public ParserOptions build() {
         return new ParserOptions(buildOpResolvers(this.unaryOpResolvers), buildOpResolvers(this.binaryOpResolvers), this.tokenRules);
      }
   }

   public interface TokenRules {
      ParserOptions.TokenRules DEFAULT = new ParserOptions.TokenRules() {};

      static boolean isNumber(char c) {
         return c >= '0' && c <= '9';
      }

      static boolean isLowerCaseLetter(char c) {
         return c >= 'a' && c <= 'z';
      }

      static boolean isUpperCaseLetter(char c) {
         return c >= 'A' && c <= 'Z';
      }

      static boolean isLetter(char c) {
         return isLowerCaseLetter(c) || isUpperCaseLetter(c);
      }

      default boolean isIdStart(char c) {
         return isLetter(c) || c == '_';
      }

      default boolean isIdPart(char c) {
         return this.isIdStart(c) || isNumber(c);
      }

      default boolean isNumberStart(char c) {
         return isNumber(c) || c == '.';
      }

      default boolean isNumberPart(char c) {
         return this.isNumberStart(c) || isLetter(c);
      }

      default boolean isAccessStart(char c) {
         return this.isIdStart(c) || isNumber(c);
      }

      default boolean isAccessPart(char c) {
         return this.isAccessStart(c);
      }
   }
}
