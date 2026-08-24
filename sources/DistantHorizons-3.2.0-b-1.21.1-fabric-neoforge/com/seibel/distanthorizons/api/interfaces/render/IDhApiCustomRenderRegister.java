package com.seibel.distanthorizons.api.interfaces.render;

public interface IDhApiCustomRenderRegister {
   void add(IDhApiRenderableBoxGroup iDhApiRenderableBoxGroup) throws IllegalArgumentException;

   IDhApiRenderableBoxGroup remove(long l);
}
