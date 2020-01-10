package dev.blachut.svelte.lang.codeInsight

import com.intellij.psi.FileViewProvider
import dev.blachut.svelte.lang.SvelteLanguage
import dev.blachut.svelte.lang.getJsEmbeddedContent
import dev.blachut.svelte.lang.psi.SvelteHtmlFile

object SveltePropsProvider {
    fun getComponentProps(viewProvider: FileViewProvider): List<String>? {
        val psiFile = viewProvider.getPsi(SvelteLanguage.INSTANCE) ?: return null
        if (psiFile !is SvelteHtmlFile) return null
        val jsElement = getJsEmbeddedContent(psiFile.instanceScript) ?: return null

        val propsVisitor = SveltePropsVisitor()
        jsElement.accept(propsVisitor)

        return propsVisitor.props
    }
}
