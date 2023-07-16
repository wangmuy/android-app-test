package com.wangmuy.testempty.feat1

import com.wangmuy.testempty.Item

data class Feat1Item(val x: Int): Item(Feat1Impl.TAG) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Feat1Item

        if (x != other.x) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + x
        return result
    }

    override fun toString(): String {
        return "Feat1Item(x=$x, name=$name, id=${System.identityHashCode(this)})"
    }


}