package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class EstadoTest {
    private lateinit var estadoSano : EstadoVector
    private lateinit var estadoInfectado: EstadoVector
    val vectorMocked1 = Mockito.mock(Vector::class.java)
    val vectorMocked2 = Mockito.mock(Vector::class.java)
    val vectorMocked3 = Mockito.mock(Vector::class.java)

    @Before
    fun setUp(){
        estadoSano = Sano()
        estadoInfectado = Infectado()
    }

    @Test
    fun sanoContagiar(){
        estadoSano.contagiar(vectorMocked1, listOf(vectorMocked2, vectorMocked3))
        verify(vectorMocked2, never()).contagiarsePor(vectorMocked1)
        verify(vectorMocked3, never()).contagiarsePor(vectorMocked1)
    }

    @Test
    fun infectadoContagiar(){
        estadoInfectado.contagiar(vectorMocked1, listOf(vectorMocked2, vectorMocked3))
        verify(vectorMocked2).contagiarsePor(vectorMocked1)
        verify(vectorMocked3).contagiarsePor(vectorMocked1)
    }
}