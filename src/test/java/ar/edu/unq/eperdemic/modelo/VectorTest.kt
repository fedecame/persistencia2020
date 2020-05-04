package ar.edu.unq.eperdemic.modelo

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorTest {
    private lateinit var vectorSUT : Vector

    @Before
    fun setUp(){
        vectorSUT = Vector()
    }

    @Test
    fun elVectorNaceSinId(){
        Assert.assertEquals(null, vectorSUT.id)
    }

    @Test
    fun elVectorSeCreaConUnEstadoSano(){
        Assert.assertEquals("Sano", vectorSUT.estado)
    }

    @Test
    fun elVectorPasaAEstadoSanoAlrecuperarse(){
        Assert.assertEquals("Sano", vectorSUT.estado)
        vectorSUT.infectarse()
        Assert.assertEquals("Infectado", vectorSUT.estado)
    }

}