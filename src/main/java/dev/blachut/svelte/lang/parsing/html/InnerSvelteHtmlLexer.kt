package dev.blachut.svelte.lang.parsing.html

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.xml.XmlTokenType
import dev.blachut.svelte.lang.psi.SvelteTypes

/**
 * Braces are differentiated to highlight them differently than JS ones
 * and still support IElementType based brace matching built-in code
 */
class InnerSvelteHtmlLexer(leftBrace: IElementType, rightBrace: IElementType)
    : MergingLexerAdapter(FlexAdapter(_SvelteHtmlLexer(leftBrace, rightBrace)), TOKENS_TO_MERGE) {
    val flexLexer: _SvelteHtmlLexer
        get() = (original as FlexAdapter).flex as _SvelteHtmlLexer
}

internal val TOKENS_TO_MERGE = TokenSet.create(
    XmlTokenType.XML_COMMENT_CHARACTERS, XmlTokenType.XML_WHITE_SPACE, XmlTokenType.XML_REAL_WHITE_SPACE,
    XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN, XmlTokenType.XML_DATA_CHARACTERS,
    XmlTokenType.XML_TAG_CHARACTERS,
    SvelteTypes.CODE_FRAGMENT
)
