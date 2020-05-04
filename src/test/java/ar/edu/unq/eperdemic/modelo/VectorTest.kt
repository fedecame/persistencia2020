package ar.edu.unq.eperdemic.modelo

import org.junit.Assert
import org.junit.Test

class VectorTest {
    private val vectorSUT = Vector()

    @Test
    fun elVectorNaceSinId(){
        val vector = Vector()
        Assert.assertEquals(null, vector.id)
    }
}