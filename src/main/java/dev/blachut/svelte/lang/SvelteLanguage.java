package dev.blachut.svelte.lang;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.Nullable;

public class SvelteLanguage extends HTMLLanguage {
    public static final SvelteLanguage INSTANCE = new SvelteLanguage();

    private SvelteLanguage() {
        super(HTMLLanguage.INSTANCE, "Svelte");
    }

    @Nullable
    @Override
    public LanguageFileType getAssociatedFileType() {
        return SvelteHtmlFileType.INSTANCE;
    }
}
