package dev.blachut.svelte.lang.parsing.top

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import dev.blachut.svelte.lang.parsing.html.SvelteHTMLParserDefinition
import dev.blachut.svelte.lang.psi.SvelteElementTypes

class SvelteParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        throw RuntimeException("createLexer karamba")
    }

    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project): PsiParser {
        return SvelteParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return SvelteHTMLParserDefinition.FILE
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        throw RuntimeException("createFile karamba")
    }

    override fun createElement(node: ASTNode): PsiElement {
        return SvelteElementTypes.createElement(node)
    }
}
