package com.nyfaria.nyfsspiders.registration.specialised;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SpecialisedRegistrationFactory {
   BlockRegistrationProvider block(String var1);

   ItemRegistrationProvider item(String var1);
}
