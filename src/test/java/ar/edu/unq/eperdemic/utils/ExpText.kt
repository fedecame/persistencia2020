package ar.edu.unq.eperdemic.utils

import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.utility.Exp
import org.junit.Assert
import org.junit.Test

class ExpText {
    var a = Animal()
    var sut = Exp(mutableListOf(a))

    @Test
    fun foo(){
        val name = a.javaClass.simpleName
        print("TEST: $name")
        var x = sut.get(name)
        Assert.assertTrue(x!!.esAnimal())
    }
}