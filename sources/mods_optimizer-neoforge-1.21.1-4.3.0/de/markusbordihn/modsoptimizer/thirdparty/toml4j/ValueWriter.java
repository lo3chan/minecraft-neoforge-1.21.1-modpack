package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

interface ValueWriter {
   boolean canWrite(Object var1);

   void write(Object var1, WriterContext var2);

   boolean isPrimitiveType();
}
