package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory

import com.intellij.codeInsight.completion.CompletionSorter
import com.intellij.codeInsight.lookup.LookupElementWeigher

class ClassPropertyCompletionSorter : CompletionSorter() {
    override fun weighBefore(beforeId: String, vararg weighers: LookupElementWeigher?): CompletionSorter {
        TODO("Not yet implemented")
    }

    override fun weighAfter(afterId: String, vararg weighers: LookupElementWeigher?): CompletionSorter {
        TODO("Not yet implemented")
    }

    override fun weigh(weigher: LookupElementWeigher?): CompletionSorter {
        TODO("Not yet implemented")
    }
}