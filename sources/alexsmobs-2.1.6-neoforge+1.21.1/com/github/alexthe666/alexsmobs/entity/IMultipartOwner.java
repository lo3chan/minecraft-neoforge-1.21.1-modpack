package com.github.alexthe666.alexsmobs.entity;

import net.neoforged.neoforge.entity.PartEntity;

public interface IMultipartOwner {
   boolean isMultipartEntity();

   PartEntity<?>[] getParts();
}
