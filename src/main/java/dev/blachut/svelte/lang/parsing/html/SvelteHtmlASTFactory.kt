package dev.blachut.svelte.lang.parsing.html

import com.intellij.lang.xml.XmlASTFactory
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.tree.IElementType
import dev.blachut.svelte.lang.psi.SVELTE_HTML_TAG
import dev.blachut.svelte.lang.psi.SvelteHtmlTag

class SvelteHtmlASTFactory : XmlASTFactory() {
    override fun createComposite(type: IElementType): CompositeElement? {
        if (type == SVELTE_HTML_TAG) {
            return SvelteHtmlTag()
        }

        return super.createComposite(type)
    }
}
