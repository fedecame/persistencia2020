package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EventoTest {
    lateinit var eventoPandemia : Evento
    lateinit var eventoPrimeraVez : Evento
    lateinit var contagioPandemia : TipoEvento
    lateinit var contagioPrimeraVez : TipoEvento
    lateinit var eventoFactory : EventoFactory

    @Before
    fun setUp(){
        eventoFactory = EventoFactory()
        eventoPandemia = eventoFactory.eventoContagioPorPandemia("sarasa")
        eventoPrimeraVez = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion("otra sarasa")
    }

    @Test
    fun losEventosDeContagioPorPandemiaDelPatogenoSonConsistentes(){
        Assert.assertTrue(eventoPandemia.tipoEvento is Contagio)
        Assert.assertEquals("sarasa", eventoPandemia.tipoPatogeno)
        Assert.assertEquals("PATOGENO_ES_PANDEMIA", eventoPandemia.accionQueLoDesencadena)
    }

    @Test
    fun losEventosDeContagioPorPContagioPorPrimeraVezEnUbicacionDelPatogenoSonConsistentes(){
        Assert.assertTrue(eventoPrimeraVez.tipoEvento is Contagio)
        Assert.assertEquals("otra sarasa", eventoPrimeraVez.tipoPatogeno)
        Assert.assertEquals("PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION", eventoPrimeraVez.accionQueLoDesencadena)
    }

    @Test
    fun elLogDelEventoDeContagioPorPandemiaDelPatogenoEsElIndicado(){
        Assert.assertEquals("", eventoPandemia.log())
    }


    @Test
    fun elLogDelEventoDeContagioContagioPorPrimeraVezEnUbicacionDelPatogenoEsElIndicado(){
        Assert.assertEquals("", eventoPandemia.log())
    }
}