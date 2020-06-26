package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Mutacion
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoPatogeno
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EventoTest {
    lateinit var eventoPandemia: Evento
    lateinit var eventoPrimeraVez: Evento
    lateinit var eventoMutacion: Evento
    lateinit var contagioPandemia: TipoEvento
    lateinit var contagioPrimeraVez: TipoEvento
    lateinit var eventoFactory: EventoFactory

    @Before
    fun setUp() {
        eventoFactory = EventoFactory
        eventoPandemia = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.BACTERIA.name, "Gripe")
        eventoPrimeraVez = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(TipoPatogeno.HONGO.name, "alguna ubicacion", "algun nombre de especie")
        eventoMutacion = eventoFactory.eventoEspecieCreada(TipoPatogeno.BACTERIA.name, "Corona" )
    }

    @Test
    fun losEventosDeContagioPorPandemiaDelPatogenoSonConsistentes() {
        Assert.assertTrue(eventoPandemia.tipoEvento is Contagio)
        Assert.assertEquals(TipoPatogeno.BACTERIA.name, eventoPandemia.tipoPatogeno)
        Assert.assertEquals("PATOGENO_ES_PANDEMIA", eventoPandemia.accionQueLoDesencadena)
        Assert.assertEquals("Gripe", eventoPandemia.nombreEspecie)
        Assert.assertNull(eventoPandemia.ubicacionContagio)

        @Test
        fun losEventosDeContagioPorPContagioPorPrimeraVezEnUbicacionDelPatogenoSonConsistentes() {
            Assert.assertTrue(eventoPrimeraVez.tipoEvento is Contagio)
            Assert.assertEquals(TipoPatogeno.HONGO.name, eventoPrimeraVez.tipoPatogeno)
            Assert.assertEquals("PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION", eventoPrimeraVez.accionQueLoDesencadena)
            Assert.assertNull(eventoPrimeraVez.nombreEspecie)
            Assert.assertEquals("alguna ubicacion", eventoPrimeraVez.ubicacionContagio)
        }

        @Test
        fun elLogDelEventoDeContagioPorPandemiaDelPatogenoEsElIndicado() {
            Assert.assertEquals("", eventoPandemia.log())
        }


        @Test
        fun elLogDelEventoDeContagioContagioPorPrimeraVezEnUbicacionDelPatogenoEsElIndicado() {
            Assert.assertEquals("", eventoPandemia.log())
        }
    }

    @Test
    fun losEventosDeMutacionAlCrearUnaEspecieSonConsistentes(){
        Assert.assertTrue(eventoMutacion.tipoEvento is Mutacion)
        Assert.assertEquals(TipoPatogeno.BACTERIA.name, eventoMutacion.tipoPatogeno)
        Assert.assertEquals("ESPECIE_CREADA", eventoMutacion.accionQueLoDesencadena)
        Assert.assertEquals("Corona", eventoMutacion.nombreEspecie)
        Assert.assertNull(eventoMutacion.ubicacionContagio)
    }

    @Test
    fun elLogDelEventoDeMutacionPorCrearUnaEspecieEsElIndicado(){
        Assert.assertEquals("Se crea la especie Corona del patogeno BACTERIA ", eventoMutacion.log())
    }
}