package ar.edu.unq.eperdemic.utility

import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Mutacion
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EventoFactoryTest {
    lateinit var sut : EventoFactory

    @Before
    fun setUp(){
        sut = EventoFactory
    }

    @Test
    fun eventoFactoryDevuelveUnEventoConsistenteAlRecibirElMensajeEventoContagioPorPandemia(){
        val evento = sut.eventoContagioPorPandemia("algo", "alguna especie")
        Assert.assertNotNull(evento)
        Assert.assertTrue(evento.tipoEvento is Contagio)
        Assert.assertEquals("algo", evento.tipoPatogeno)
        Assert.assertEquals("PATOGENO_ES_PANDEMIA", evento.accionQueLoDesencadena)
        Assert.assertEquals("alguna especie", evento.nombreEspecie)
        Assert.assertNull(evento.ubicacionContagio)
    }

    @Test
    fun eventoFactoryDevuelveUnEventoConsistenteAlRecibirElMensajeEventoContagioPorPrimeraVezEnUbicacion(){
        val evento = sut.eventoContagioPorPrimeraVezEnUbicacion("algo", "un nombre de ubicacion", "un nombre de especie")
        Assert.assertNotNull(evento)
        Assert.assertTrue(evento.tipoEvento is Contagio)
        Assert.assertEquals("algo", evento.tipoPatogeno)
        Assert.assertEquals("PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION", evento.accionQueLoDesencadena)
        Assert.assertEquals("un nombre de especie", evento.nombreEspecie)
        Assert.assertEquals( "un nombre de ubicacion", evento.ubicacionContagio)
    }

    @Test
    fun eventoFactoryDevuelveUnEventoConsistenteAlRecibirElMensajeEventoEspecieCreada(){
        val evento = sut.eventoEspecieCreada("algo","un nombre de especie")
        Assert.assertNotNull(evento)
        Assert.assertTrue(evento.tipoEvento is Mutacion)
        Assert.assertEquals("algo", evento.tipoPatogeno)
        Assert.assertEquals("ESPECIE_CREADA", evento.accionQueLoDesencadena)
        Assert.assertEquals("un nombre de especie", evento.nombreEspecie)
        Assert.assertNull(evento.ubicacionContagio)
    }

    @Test
    fun eventoFactoryDevuelveUnEventoConsistenteAlRecibirElMensajeEventoEspecieDePatogenoMuta(){
        val evento = sut.eventoEspecieDePatogenoMuta("algo","un nombre de especie")
        Assert.assertNotNull(evento)
        Assert.assertTrue(evento.tipoEvento is Mutacion)
        Assert.assertEquals("algo", evento.tipoPatogeno)
        Assert.assertEquals("ESPECIE_MUTADA", evento.accionQueLoDesencadena)
        Assert.assertEquals("un nombre de especie", evento.nombreEspecie)
        Assert.assertNull(evento.ubicacionContagio)
    }
}