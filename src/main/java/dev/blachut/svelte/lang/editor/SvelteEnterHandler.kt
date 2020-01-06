// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
// Copyright 2019 Tomasz Błachut
package dev.blachut.svelte.lang.editor

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import dev.blachut.svelte.lang.SvelteFileViewProvider
import dev.blachut.svelte.lang.parsing.html.endTokens
import dev.blachut.svelte.lang.parsing.html.innerTokens
import dev.blachut.svelte.lang.parsing.html.startTokens
import dev.blachut.svelte.lang.parsing.html.tailTokens

/**
 * Handler for custom plugin actions when `Enter` is typed by the user
 *
 * Based on Handlebars plugin
 */
class SvelteEnterHandler : EnterHandlerDelegateAdapter() {
    /**
     * if we are between start and end tags, we ensure the caret ends up in the "logical" place on Enter.
     * i.e. "{#if x}<caret>{/if}" becomes the following on Enter:
     *
     * {#if x}
     * <caret>
     * {/if}
     *
     * (Note: <caret> may be indented depending on formatter settings.)
     */
    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffset: Ref<Int>,
        caretAdvance: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): EnterHandlerDelegate.Result {
        if (file.viewProvider is SvelteFileViewProvider && isBetweenSvelteTags(editor, file, caretOffset.get())) {
            originalHandler!!.execute(editor, editor.caretModel.currentCaret, dataContext)
            return EnterHandlerDelegate.Result.Default
        }
        return EnterHandlerDelegate.Result.Continue
    }

    /**
     * Checks to see if `Enter` has been typed while the caret is between an start and end tag pair
     */
    private fun isBetweenSvelteTags(editor: Editor, file: PsiFile, offset: Int): Boolean {
        if (offset == 0) return false
        val chars = editor.document.charsSequence
        if (chars[offset - 1] != '}') return false

        val highlighter = (editor as EditorEx).highlighter
        val iterator = highlighter.createIterator(offset - 1)

        PsiDocumentManager.getInstance(file.project).commitDocument(editor.document)
        val openerElement = file.findElementAt(iterator.start)

        val afterStartTag = when {
            PsiTreeUtil.findFirstParent(openerElement, true) { startTokens.contains(it.elementType) } != null -> true
            PsiTreeUtil.findFirstParent(openerElement, true) { innerTokens.contains(it.elementType) } != null -> false
            else -> return false
        }

        iterator.advance()

        if (iterator.atEnd()) {
            // no more tokens, so certainly no next tag
            return false
        }

        val closerElement = file.findElementAt(iterator.start)

        val endTag = if (afterStartTag) {
            PsiTreeUtil.findFirstParent(closerElement, true) { tailTokens.contains(it.elementType) }
        } else {
            PsiTreeUtil.findFirstParent(closerElement, true) { endTokens.contains(it.elementType) }
        }

        // if we got this far, we're between matching tags if required tag is found
        return endTag != null
    }
}
