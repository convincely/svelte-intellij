package dev.blachut.svelte.lang.parsing.html

import com.intellij.lexer.HtmlLexer
import dev.blachut.svelte.lang.parsing.js.SvelteJSScriptContentProvider

class SvelteHtmlLexer : HtmlLexer(BaseSvelteHtmlLexer(), false) {
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        // TODO Verify if those masks don't clash with ones used in BaseHtmlLexer.initState
        val baseState = initialState and 0xffff
        val nestingLevel = initialState shr 16
        (delegate as BaseSvelteHtmlLexer).flexLexer.bracesNestingLevel = nestingLevel
        super.start(buffer, startOffset, endOffset, baseState)
    }

    override fun getState(): Int {
        val nestingLevel = (delegate as BaseSvelteHtmlLexer).flexLexer.bracesNestingLevel
        return (nestingLevel shl 16) or (super.getState() and 0xffff)
    }

    override fun findScriptContentProvider(mimeType: String?) = SvelteJSScriptContentProvider

    override fun isHtmlTagState(state: Int): Boolean {
        return state == _SvelteHtmlLexer.START_TAG_NAME || state == _SvelteHtmlLexer.END_TAG_NAME
    }
}
