package dev.blachut.svelte.lang.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.blachut.svelte.lang.psi.SvelteHtmlFile
import dev.blachut.svelte.lang.psi.SvelteTypes

class SvelteBraceTypedHandler : TypedHandlerDelegate() {
    /**
     * Called after the specified character typed by the user has been inserted in the editor.
     *
     * @param charTyped the character that was typed
     * @param project the project in which the `file` exists
     * @param editor the editor that has the `file` open
     * @param file the file into which the `charTyped` was typed
     */
    override fun charTyped(charTyped: Char, project: Project, editor: Editor, file: PsiFile): Result {
        var result: Result = Result.CONTINUE

        if (file is SvelteHtmlFile) {
            if (charTyped == '{') {
                val caret = editor.caretModel.offset

                if (caret > 0) { // "(do|fn)<space><caret>"
                    val highlighter = (editor as EditorEx).highlighter
                    val iterator = highlighter.createIterator(caret - 1)
                    val tokenType = iterator.tokenType

                    if (tokenType === JSTokenTypes.LBRACE || tokenType === SvelteTypes.START_MUSTACHE) {
                        editor.document.insertString(caret, " }")
                        result = Result.STOP
                    }
                }
            }
        }

        return result
    }
}
