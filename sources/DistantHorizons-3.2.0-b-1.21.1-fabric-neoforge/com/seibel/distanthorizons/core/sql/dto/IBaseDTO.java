package com.seibel.distanthorizons.core.sql.dto;

public interface IBaseDTO<TKey> extends AutoCloseable {
   TKey getKey();

   default String getKeyDisplayString() {
      return this.getKey().toString();
   }

   @Override
   void close();
}
