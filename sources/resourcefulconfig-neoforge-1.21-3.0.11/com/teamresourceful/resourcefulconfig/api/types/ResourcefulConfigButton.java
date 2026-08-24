package com.teamresourceful.resourcefulconfig.api.types;

import com.teamresourceful.resourcefulconfig.api.types.options.Position;

public interface ResourcefulConfigButton {
   String title();

   String description();

   String target();

   Position position();

   String text();

   boolean invoke();
}
