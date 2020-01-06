package dev.blachut.svelte.lang

import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.lang.javascript.dialects.JSLanguageLevel
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.psi.tree.IElementType
import dev.blachut.svelte.lang.parsing.top.SvelteHighlightingLexer
import dev.blachut.svelte.lang.psi.SvelteTokenTypes

internal class SvelteSyntaxHighlighter(private val jsLanguageLevel: JSLanguageLevel) : HtmlFileHighlighter() {
    override fun getHighlightingLexer(): Lexer {
        return SvelteHighlightingLexer(jsLanguageLevel)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            SvelteTokenTypes.START_MUSTACHE,
            SvelteTokenTypes.START_MUSTACHE_TEMP,
            SvelteTokenTypes.END_MUSTACHE -> KEY_KEYS
            else -> super.getTokenHighlights(tokenType)
        }
    }

    companion object {
        //        private val MUSTACHES = createTextAttributesKey("SVELTE_MUSTACHES", DefaultLanguageHighlighterColors.BRACES)
        private val KEY = createTextAttributesKey("SVELTE_KEY", DefaultLanguageHighlighterColors.KEYWORD)
        private val KEY_KEYS = arrayOf(KEY)
    }
}
