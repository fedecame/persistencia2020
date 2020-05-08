package ar.edu.unq.eperdemic.utility

import ar.edu.unq.eperdemic.tipo.TipoVector


class Exp(ls : List<TipoVector>): Delivery<TipoVector>(ls){
    override fun myAddException(word: String): Exception {
        return Exception(word)
    }

    override fun myGetException(word: String): Exception {
        return RuntimeException()
    }

    fun provide() = things.values
}