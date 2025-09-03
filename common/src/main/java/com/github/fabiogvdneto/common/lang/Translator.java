package com.github.fabiogvdneto.common.lang;

import java.util.Collection;

public interface Translator {

    String language();

    Collection<String> keys();

    Collection<String> translations();

    String get(String key);

}
