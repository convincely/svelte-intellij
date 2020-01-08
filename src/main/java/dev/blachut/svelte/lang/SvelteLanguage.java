package dev.blachut.svelte.lang;

import com.intellij.lang.Language;

// Kotlin class trips up plugin.xml inspections
public class SvelteLanguage extends Language {
    public static final SvelteLanguage INSTANCE = new SvelteLanguage();

    private SvelteLanguage() {
        super("Svelte");
    }
}
