package ar.edu.unq.eperdemic.modelo

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorTest {
    private lateinit var vectorSUT : Vector

    @Before
    fun setUp(){
        vectorSUT = Vector()
        vectorSUT.tipo = Animal()
    }

    @Test
    fun testelVectorNaceSinId(){
        Assert.assertEquals(null, vectorSUT.id)
    }

    @Test
    fun testtestElEstadoDelVectorEsElIndicado(){
        Assert.assertTrue(true)
    }
    @Test
    fun testelVectorSeCreaConUnEstadoSano(){
        Assert.assertEquals("Sano", vectorSUT.estado)
    }

    @Test
    fun testelVectorPasaAEstadoSanoAlrecuperarse(){
        Assert.assertEquals("Sano", vectorSUT.estado)
        vectorSUT.infectarse()
        Assert.assertEquals("Infectado", vectorSUT.estado)
    }
}