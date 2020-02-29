package dev.blachut.svelte.lang.format

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.formatter.xml.AbstractXmlBlock
import com.intellij.psi.formatter.xml.XmlBlock
import com.intellij.psi.formatter.xml.XmlFormattingPolicy
import com.intellij.psi.formatter.xml.XmlTagBlock
import dev.blachut.svelte.lang.psi.blocks.SvelteBlock
import java.util.*

class SvelteXmlBlock(
    node: ASTNode?,
    wrap: Wrap?,
    alignment: Alignment?,
    policy: XmlFormattingPolicy?,
    indent: Indent?,
    textRange: TextRange?,
    preserveSpace: Boolean
) : XmlBlock(node, wrap, alignment, policy, indent, textRange, preserveSpace) {
    override fun processSimpleChild(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        if (isSvelteBlock(child)) {
            result.add(createTagBlock(child, indent ?: Indent.getNoneIndent(), wrap, alignment))
        } else {
            super.processSimpleChild(child, indent, result, wrap, alignment)
        }
    }

    override fun createSimpleChild(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlBlock {
        return createSimpleChild(myXmlFormattingPolicy, child, indent, wrap, alignment)
    }

    override fun createTagBlock(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
        return createTagBlock(myXmlFormattingPolicy, child, indent, wrap, alignment)
    }
}

open class SvelteXmlTagBlock(
    node: ASTNode?,
    wrap: Wrap?,
    alignment: Alignment?,
    policy: XmlFormattingPolicy?,
    indent: Indent?,
    preserveSpace: Boolean
) : XmlTagBlock(node, wrap, alignment, policy, indent, preserveSpace) {
    override fun buildChildren(): List<Block>? {
        if (!isSvelteBlock(myNode)) {
            return super.buildChildren();
        }

        val attrWrap = Wrap.createWrap(AbstractXmlBlock.getWrapType(myXmlFormattingPolicy.attributesWrap), false)
        val textWrap = Wrap.createWrap(AbstractXmlBlock.getWrapType(myXmlFormattingPolicy.getTextWrap(tag)), true)
        val tagBeginWrap = createTagBeginWrapping(tag)
        val attrAlignment = Alignment.createAlignment()
        val textAlignment = Alignment.createAlignment()
        val result = ArrayList<Block>(3)

        var child = myNode.firstChildNode
        var localResult = ArrayList<Block>(1)
        var insideTag = true

        while (child != null) {
            if (child != null) {
                child = child.treeNext
            }
        }

        if (!localResult.isEmpty()) {
            result.add(createTagContentNode(localResult))
        }

        return result
    }

    private fun createTagContentNode(localResult: ArrayList<Block>): Block {
        return createSyntheticBlock(localResult, childrenIndent)
    }

    override fun processSimpleChild(child: ASTNode, indent: Indent?, result: MutableList<in Block>, wrap: Wrap?, alignment: Alignment?) {
        if (isSvelteBlock(child)) {
            result.add(createTagBlock(child, indent ?: Indent.getNoneIndent(), wrap, alignment))
        } else {
            super.processSimpleChild(child, indent, result, wrap, alignment)
        }
    }

    override fun createSimpleChild(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlBlock {
        return createSimpleChild(myXmlFormattingPolicy, child, indent, wrap, alignment)
    }

    override fun createTagBlock(child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
        return createTagBlock(myXmlFormattingPolicy, child, indent, wrap, alignment)
    }
}

fun AbstractXmlBlock.createSimpleChild(xmlFormattingPolicy: XmlFormattingPolicy?, child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlBlock {
    return SvelteXmlBlock(child, wrap, alignment, xmlFormattingPolicy, indent, null, isPreserveSpace)
}

fun AbstractXmlBlock.createTagBlock(xmlFormattingPolicy: XmlFormattingPolicy?, child: ASTNode?, indent: Indent?, wrap: Wrap?, alignment: Alignment?): XmlTagBlock {
    val newIndent = indent ?: Indent.getNoneIndent()
    return SvelteXmlTagBlock(child, wrap, alignment, xmlFormattingPolicy, newIndent, isPreserveSpace)
}

fun isSvelteBlock(child: ASTNode): Boolean {
    return child.psi is SvelteBlock
}

class X: AbstractXmlBlock() {
    override fun insertLineBreakBeforeTag(): Boolean {
        TODO("not implemented")
    }

    override fun buildChildren(): MutableList<Block> {
        TODO("not implemented")
    }

    override fun isTextElement(): Boolean {
        TODO("not implemented")
    }

    override fun removeLineBreakBeforeTag(): Boolean {
        TODO("not implemented")
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        TODO("not implemented")
    }
}
