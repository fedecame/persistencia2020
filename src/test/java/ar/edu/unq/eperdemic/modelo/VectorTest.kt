package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.tipo.Animal
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorTest {
    private lateinit var vectorSUT : Vector

    @Before
    fun setUp(){
        vectorSUT = Vector()
        vectorSUT.estado = Sano()
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
        Assert.assertEquals("Sano", vectorSUT.estado.nombre())
    }

    @Test
    fun testelVectorPasaAEstadoSanoAlrecuperarse(){
        Assert.assertEquals("Sano", vectorSUT.estado.nombre())
        vectorSUT.infectarse()
        Assert.assertEquals("Infectado", vectorSUT.estado.nombre())
    }
}