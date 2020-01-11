package dev.blachut.svelte.lang.format

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.formatter.xml.AbstractXmlBlock
import com.intellij.psi.formatter.xml.XmlBlock
import com.intellij.psi.formatter.xml.XmlFormattingPolicy
import com.intellij.psi.formatter.xml.XmlTagBlock
import dev.blachut.svelte.lang.parsing.html.psi.SvelteBlock
import dev.blachut.svelte.lang.psi.SvelteEndTag
import java.util.*

private interface SvelteBlockOverrides {
    val xmlFormattingPolicy: XmlFormattingPolicy?

    fun isPreserveSpace(): Boolean

    fun processSimpleChildSuper(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?)

    fun processSimpleChild(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        if (isSvelteBlock(child)) {
            result.add(createSvelteBlockBlock(child, indent ?: Indent.getNoneIndent(), wrap, alignment))
        } else {
            processSimpleChildSuper(child, indent, result, wrap, alignment)
        }
    }

    private fun isSvelteBlock(child: ASTNode): Boolean {
        return child.psi is SvelteBlock
    }

    fun createSimpleChild(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlBlock {
        return SvelteXmlBlock(child, wrap, alignment, xmlFormattingPolicy, indent, null, isPreserveSpace())
    }

    fun createTagBlock(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
        val newIndent = indent ?: Indent.getNoneIndent()
        return SvelteXmlTagBlock(child, wrap, alignment, xmlFormattingPolicy, newIndent, isPreserveSpace())
    }

    private fun createSvelteBlockBlock(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
        val newIndent = indent ?: Indent.getNoneIndent()
        return SvelteBlockBlock(child, wrap, alignment, xmlFormattingPolicy, newIndent, isPreserveSpace())
    }
}

class SvelteXmlBlock(
    node: ASTNode?,
    wrap: Wrap?,
    alignment: Alignment?,
    policy: XmlFormattingPolicy?,
    indent: Indent?,
    textRange: TextRange?,
    preserveSpace: Boolean
) : XmlBlock(node, wrap, alignment, policy, indent, textRange, preserveSpace), SvelteBlockOverrides {
    override val xmlFormattingPolicy: XmlFormattingPolicy? get() = myXmlFormattingPolicy

    override fun processSimpleChildSuper(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        super<XmlBlock>.processSimpleChild(child, indent, result, wrap, alignment)
    }

    override fun processSimpleChild(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        super<SvelteBlockOverrides>.processSimpleChild(child, indent, result, wrap, alignment)
    }

    override fun createSimpleChild(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlBlock {
        return super<SvelteBlockOverrides>.createSimpleChild(child, indent, wrap, alignment)
    }

    override fun createTagBlock(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
        return super<SvelteBlockOverrides>.createTagBlock(child, indent, wrap, alignment)
    }
}

open class SvelteXmlTagBlock(
    node: ASTNode?,
    wrap: Wrap?,
    alignment: Alignment?,
    policy: XmlFormattingPolicy?,
    indent: Indent?,
    preserveSpace: Boolean
) : XmlTagBlock(node, wrap, alignment, policy, indent, preserveSpace), SvelteBlockOverrides {
    override val xmlFormattingPolicy: XmlFormattingPolicy? get() = myXmlFormattingPolicy

    override fun processSimpleChildSuper(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        super<XmlTagBlock>.processSimpleChild(child, indent, result, wrap, alignment)
    }

    override fun processSimpleChild(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        super<SvelteBlockOverrides>.processSimpleChild(child, indent, result, wrap, alignment)
    }

    override fun createSimpleChild(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlBlock {
        return super<SvelteBlockOverrides>.createSimpleChild(child, indent, wrap, alignment)
    }

    override fun createTagBlock(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
        return super<SvelteBlockOverrides>.createTagBlock(child, indent, wrap, alignment)
    }
}

class SvelteBlockBlock(
    node: ASTNode?,
    wrap: Wrap?,
    alignment: Alignment?,
    policy: XmlFormattingPolicy?,
    indent: Indent?,
    preserveSpace: Boolean
) : SvelteXmlTagBlock(node, wrap, alignment, policy, indent, preserveSpace) {
    override fun buildChildren(): MutableList<Block> {
        val attrWrap = Wrap.createWrap(AbstractXmlBlock.getWrapType(myXmlFormattingPolicy.attributesWrap), false)
        val textWrap = Wrap.createWrap(AbstractXmlBlock.getWrapType(myXmlFormattingPolicy.getTextWrap(tag)), true)
        val tagBeginWrap = Wrap.createWrap(WrapType.NORMAL, false)
        val attrAlignment = Alignment.createAlignment()
        val textAlignment = Alignment.createAlignment()

        val result = ArrayList<Block>(3)
//        var localResult = ArrayList<Block?>(1)
//        var insideTag = true

        var child = myNode.firstChildNode
        while (child != null) {
            if (!AbstractXmlBlock.containsWhiteSpacesOnly(child) && child.textLength > 0) {
                val wrap = chooseWrap(child, tagBeginWrap, attrWrap, textWrap)
                val alignment = chooseAlignment(child, attrAlignment, textAlignment)

                if (child.psi is SvelteEndTag) {
//                    child = processChild(localResult, child, wrap, alignment, null)
//                    result.add(createSyntheticBlock(localResult, null))

//                    result.add(createSimpleChild(child, Indent.getNoneIndent(), wrap, alignment))
                    result.add(createSyntheticBlock(arrayListOf(createSimpleChild(child, Indent.getNoneIndent(), wrap, alignment)), Indent.getNoneIndent()))
                } else {
                    val tag = child.firstChildNode
//                    result.add(createSimpleChild(tag, Indent.getNoneIndent(), wrap, alignment))
                    result.add(createSyntheticBlock(arrayListOf(createSimpleChild(tag, Indent.getNoneIndent(), wrap, alignment)), Indent.getNoneIndent()))

                    val fragment = child.lastChildNode
//                    result.add(createSimpleChild(fragment, Indent.getNormalIndent(), wrap, alignment))
                    result.add(createSyntheticBlock(arrayListOf(createSimpleChild(fragment, Indent.getNormalIndent(), wrap, alignment)), Indent.getNoneIndent()))
                }
            }

//            if (child != null) {
            child = child.treeNext
//            }
        }

        return result
    }

//    getChildAttributes
}
