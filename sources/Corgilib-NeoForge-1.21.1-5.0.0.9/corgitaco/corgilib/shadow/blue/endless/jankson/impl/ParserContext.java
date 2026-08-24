package corgitaco.corgilib.shadow.blue.endless.jankson.impl;

import corgitaco.corgilib.shadow.blue.endless.jankson.Jankson;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.SyntaxError;

public interface ParserContext<T> {
   boolean consume(int var1, Jankson var2) throws SyntaxError;

   void eof() throws SyntaxError;

   boolean isComplete();

   T getResult() throws SyntaxError;
}
