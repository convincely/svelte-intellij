// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dev.blachut.svelte.lang.format

import com.intellij.formatting.Alignment
import com.intellij.formatting.Wrap
import com.intellij.formatting.templateLanguages.DataLanguageBlockWrapper
import com.intellij.formatting.templateLanguages.TemplateLanguageBlock
import com.intellij.formatting.templateLanguages.TemplateLanguageFormattingModelBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.FormattingDocumentModelImpl
import com.intellij.psi.formatter.xml.HtmlPolicy
import com.intellij.psi.tree.TokenSet

val blocks = TokenSet.create()

/**
 * I don't really understand how it works.
 *
 * Based on Handlebars plugin
 */
class OldSvelteFormattingModelBuilder : TemplateLanguageFormattingModelBuilder() {
    override fun createTemplateLanguageBlock(
        node: ASTNode,
        wrap: Wrap?,
        alignment: Alignment?,
        foreignChildren: List<DataLanguageBlockWrapper>?,
        codeStyleSettings: CodeStyleSettings
    ): TemplateLanguageBlock {
        val documentModel = FormattingDocumentModelImpl.createOn(node.psi.containingFile)
        val policy = HtmlPolicy(codeStyleSettings, documentModel)

        return OldSvelteBlock(node, wrap, alignment, this, codeStyleSettings, foreignChildren, policy)
    }
}

