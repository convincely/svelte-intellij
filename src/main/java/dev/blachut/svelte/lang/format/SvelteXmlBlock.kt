package dev.blachut.svelte.lang.format

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.formatter.xml.*
import dev.blachut.svelte.lang.psi.SvelteElementTypes
import dev.blachut.svelte.lang.psi.SvelteEndTag
import dev.blachut.svelte.lang.psi.blocks.SvelteBlock

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
        val attrAlignment = Alignment.createAlignment()
        val textAlignment = Alignment.createAlignment()
        val result = ArrayList<Block>(3)

        var child = myNode.firstChildNode
        var localResult = ArrayList<Block>(1)

        while (child != null) {
            if (!AbstractXmlBlock.containsWhiteSpacesOnly(child) && child.textLength > 0) {
                if (SvelteElementTypes.BRANCHES.contains(child.elementType)) {
                    if (!localResult.isEmpty()) {
                        result.add(createTagContentNode(localResult))
                        localResult = ArrayList(1)
                    }

                    val tag = child.firstChildNode
                    val fragment = child.lastChildNode

                    result.add(processTag(tag, attrWrap, textWrap, attrAlignment, textAlignment))
                    result.add(processFragment(fragment, attrWrap, textWrap, attrAlignment, textAlignment))
                } else if (child.psi is SvelteEndTag) {
                    if (!localResult.isEmpty()) {
                        result.add(createTagContentNode(localResult))
                        localResult = ArrayList(1)
                    }
                    result.add(processTag(child, attrWrap, textWrap, attrAlignment, textAlignment))
                } else {
                    val wrap = chooseWrap(child, textWrap, attrWrap, textWrap)
                    val alignment = chooseAlignment(child, attrAlignment, textAlignment)

                    child = processChild(localResult, child, wrap, alignment, childrenIndent)
                }
            }

            if (child != null) {
                child = child.treeNext
            }
        }

        if (!localResult.isEmpty()) {
            result.add(createTagContentNode(localResult))
        }

        return result
    }

    private fun processTag(tag: ASTNode, attrWrap: Wrap?, textWrap: Wrap?, attrAlignment: Alignment?, textAlignment: Alignment?): Block {
        return createSyntheticBlock(arrayListOf(ReadOnlyBlock(tag)), Indent.getNoneIndent())
//                    result.add(createSimpleChild(tag, null, wrap, alignment))

//        val localResult = ArrayList<Block>(4)
//
//        var child = tag.firstChildNode
//        while (child != null) {
//            if (!AbstractXmlBlock.containsWhiteSpacesOnly(child) && child.textLength > 0) {
//                val wrap = chooseWrap(child, textWrap, attrWrap, textWrap)
//                val alignment = chooseAlignment(child, attrAlignment, textAlignment)
//
//                child = processChild(localResult, child, wrap, alignment, null)
////                localResult.add(createSimpleChild(child, null, wrap, alignment))
//            }
//
//            if (child != null) {
//                child = child.treeNext
//            }
//        }
//
//        return createSyntheticBlock(localResult, Indent.getNoneIndent())
    }

    private fun processFragment(fragment: ASTNode, attrWrap: Wrap?, textWrap: Wrap?, attrAlignment: Alignment?, textAlignment: Alignment?): Block {
        //                    result.add(createSimpleChild(fragment, Indent.getNoneIndent(), wrap, alignment))

        val localResult = ArrayList<Block>(1)

        var child = fragment.firstChildNode
        while (child != null) {
            if (!AbstractXmlBlock.containsWhiteSpacesOnly(child) && child.textLength > 0) {
                val wrap = chooseWrap(child, textWrap, attrWrap, textWrap)
                val alignment = chooseAlignment(child, attrAlignment, textAlignment)

                child = processChild(localResult, child, wrap, alignment, childrenIndent)
            }

            if (child != null) {
                child = child.treeNext
            }
        }

        return createSyntheticBlock(localResult, childrenIndent)
    }

    private fun processBranch(result: MutableList<Block>, child: ASTNode, wrap: Wrap?, alignment: Alignment?, indent: Indent?) {
        val tag = child.firstChildNode
        val fragment = child.lastChildNode

        result.add(createSimpleChild(tag, null, wrap, alignment))
        result.add(createSimpleChild(fragment, indent, wrap, alignment))
    }

    private fun createTagDescriptionNode(localResult: ArrayList<Block>): Block {
        return createSyntheticBlock(localResult, null)
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

//class X: AbstractXmlBlock() {
//    override fun insertLineBreakBeforeTag(): Boolean {
//        TODO("not implemented")
//    }
//
//    override fun buildChildren(): MutableList<Block> {
//        TODO("not implemented")
//    }
//
//    override fun isTextElement(): Boolean {
//        TODO("not implemented")
//    }
//
//    override fun removeLineBreakBeforeTag(): Boolean {
//        TODO("not implemented")
//    }
//
//    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
//        TODO("not implemented")
//    }
//}
