package ar.edu.unq.eperdemic.utility.random

import ar.edu.unq.eperdemic.modelo.exception.MalosParametrosRunTimeException
import kotlin.random.Random


class RandomMasterImpl : RandomMaster {
    override fun giveMeARandonNumberBeetween(aNumber : Double, otherNumber : Double) : Double{
        try {
            return Random.nextDouble(aNumber, otherNumber)
        }
        catch(e : IllegalArgumentException) {
            throw MalosParametrosRunTimeException(aNumber, otherNumber)
        }
    }
}

