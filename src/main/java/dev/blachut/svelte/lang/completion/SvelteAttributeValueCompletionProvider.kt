package dev.blachut.svelte.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.css.CSSLanguage
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import java.util.*

class SvelteAttributeValueCompletionProvider : CompletionProvider<CompletionParameters>() {
    private val styleLanguages = styleLanguages()

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val xmlTag = PsiTreeUtil.getParentOfType(parameters.position, XmlTag::class.java, false)
        val xmlAttribute = PsiTreeUtil.getParentOfType(parameters.position, XmlAttribute::class.java, false)
        if (xmlTag == null || xmlAttribute == null) return

        for (completion in listOfCompletions(xmlTag, xmlAttribute)) {
            result.addElement(LookupElementBuilder.create(completion))
        }
    }

    private fun listOfCompletions(xmlTag: XmlTag, xmlAttribute: XmlAttribute): Set<String> {
        return if (xmlTag.name == "style" && xmlAttribute.name == "lang") {
            styleLanguages
        } else {
            emptySet()
        }
    }

    // Taken from Vue plugin
    private fun styleLanguages(): Set<String> {
        val result = mutableListOf<String>()
        result.add("css")
        CSSLanguage.INSTANCE.dialects.forEach {
            if (it.displayName != "JQuery-CSS") {
                result.add(it.displayName.toLowerCase(Locale.US))
            }
        }
        return result.toSet()
    }
}
