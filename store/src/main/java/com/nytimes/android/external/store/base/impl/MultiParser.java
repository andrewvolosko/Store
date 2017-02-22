package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.util.KeyParseFunc;
import com.nytimes.android.external.store.util.ParserException;

import java.util.ArrayList;
import java.util.List;

import static com.nytimes.android.external.cache.Preconditions.checkArgument;
import static com.nytimes.android.external.cache.Preconditions.checkNotNull;

public class MultiParser<Key, Raw, Parsed> implements KeyParseFunc<Key, Raw, Parsed> {

    private final List<KeyParseFunc> parsers = new ArrayList<>();

    public MultiParser(List<KeyParseFunc> parsers) {
        checkNotNull(parsers, "Parsers can't be null.");
        checkArgument(!parsers.isEmpty(), "Parsers can't be empty.");
        for (KeyParseFunc parser : parsers) {
            checkNotNull(parser, "Parser can't be null.");
        }
        this.parsers.addAll(parsers);
    }

    private ParserException createParserException() {
        return new ParserException("One of the provided parsers has a wrong typing. " +
                "Make sure that parsers are passed in a correct order and the fromTypes match each other.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Parsed call(Key key, Raw raw) {
        Object parsed = raw;
        for (KeyParseFunc parser : parsers) {
            try {
                parsed = parser.call(key, parsed);
            } catch (ClassCastException exception) {
                throw createParserException();
            }
        }
        return (Parsed) parsed;
    }
}
