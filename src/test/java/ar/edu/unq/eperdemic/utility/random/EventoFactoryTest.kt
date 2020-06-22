package ar.edu.unq.eperdemic.utility.random

import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import junit.framework.Assert
import org.junit.Before
import org.junit.Test

class EventoFactoryTest {
    lateinit var sut : EventoFactory

    @Before
    fun setUp(){
        sut = EventoFactory()
    }

    @Test
    fun eventoFactoryDevuelveUnEventoConsistenteAlRecibirElMensajeEventoContagioPorPandemia(){
        val evento = sut.eventoContagioPorPandemia("algo")
        Assert.assertNotNull(evento)
        Assert.assertTrue(evento.tipoEvento is Contagio)
        Assert.assertEquals("algo", evento.tipoPatogeno)
        Assert.assertEquals("PATOGENO_ES_PANDEMIA", evento.accionQueLoDesencadena)
    }

    @Test
    fun eventoFactoryDevuelveUnEventoConsistenteAlRecibirElMensajeEventoContagioPorPrimeraVezEnUbicacion(){
        val evento = sut.eventoContagioPorPrimeraVezEnUbicacion("algo")
        Assert.assertNotNull(evento)
        Assert.assertTrue(evento.tipoEvento is Contagio)
        Assert.assertEquals("algo", evento.tipoPatogeno)
        Assert.assertEquals("PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION", evento.accionQueLoDesencadena)
    }
}