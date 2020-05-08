package ar.edu.unq.eperdemic.modelo

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class EspecieTest {
    private lateinit var especie: Especie
    val patogenoMocked = Mockito.mock(Patogeno::class.java)

    @Before
    fun setUp(){
        especie = Especie()
        especie.patogeno = patogenoMocked
        especie.nombre = "corona"
        especie.paisDeOrigen = "China"
    }

    @Test
    fun factoresDeContagio(){
        Mockito.`when`(patogenoMocked.factorContagioAnimal()).thenReturn(5)
        Mockito.`when`(patogenoMocked.factorContagioInsecto()).thenReturn(14)
        Mockito.`when`(patogenoMocked.factorContagioHumano()).thenReturn(33)

        Assert.assertEquals(5, especie.factorContagioAnimal())
        Assert.assertEquals(14, especie.factorContagioInsecto())
        Assert.assertEquals(33, especie.factorContagioHumano())
    }

}