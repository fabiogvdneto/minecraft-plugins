package com.github.fabiogvdneto.common.lang;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractTranslator implements Translator {

    protected final Map<String, String> translations;

    protected AbstractTranslator(Map<String, String> map) {
        this.translations = map;
    }

    @Override
    public Collection<String> keys() {
        return translations.keySet();
    }

    @Override
    public Collection<String> translations() {
        return translations.values();
    }

    @Override
    public String get(String key) {
        return translations.get(key);
    }
}
