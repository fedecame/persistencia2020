package ar.edu.unq.eperdemic.utils

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.utility.Exp
import org.junit.Assert
import org.junit.Test

class ExpText {
    val a = Animal()
    val sut = Exp(mutableListOf(a))

    @Test
    fun falla(){
        Assert.assertEquals(1,2)
    }

    @Test
    fun foo1(){
        val name = a.javaClass.simpleName.toUpperCase()
        print("TEST: $name")
        var x = sut.get(name)
        Assert.assertTrue(x!!.esAnimal())
    }

    @Test(expected=Exception::class)
    fun testDaLaExcepcionCorrespondienteCuandoSeLePideAgregarUnaClaseyaContenida(){
        sut.add(Animal())
    }

    @Test(expected=ClassCastException::class)
    fun testDaLaExcepcionCorrespondienteCuandoSeLePideAgregarUnaClaseDeotroTipo(){
        //Ojo esto
        val patogeno = Patogeno()
        patogeno.tipo = "Sarasa"
        sut.add(patogeno)
        print(sut.provide().map{it.javaClass.simpleName})

    }

    @Test(expected=RuntimeException::class)
    fun testDaLaExcepcionCorrespondienteCuandoSeLediceConseguirUnaClaveErronea(){
        sut.get("Sarasa")
    }
}