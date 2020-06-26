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
    lateinit var eventoSeCreaEspecie: Evento
    lateinit var eventoSeMutaEspecie: Evento
    lateinit var contagioPandemia: TipoEvento
    lateinit var contagioPrimeraVez: TipoEvento
    lateinit var eventoFactory: EventoFactory

    @Before
    fun setUp() {
        eventoFactory = EventoFactory
        eventoPandemia = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.BACTERIA.name, "Gripe")
        eventoPrimeraVez = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(TipoPatogeno.HONGO.name, "alguna ubicacion", "algun nombre de especie")
        eventoSeCreaEspecie = eventoFactory.eventoEspecieCreada(TipoPatogeno.BACTERIA.name, "Corona" )
        eventoSeMutaEspecie = eventoFactory.eventoEspecieDePatogenoMuta(TipoPatogeno.BACTERIA.name, "fiebreDisco")
    }

    @Test
    fun losEventosDeContagioPorPandemiaDelPatogenoSonConsistentes() {
        Assert.assertTrue(eventoPandemia.tipoEvento is Contagio)
        Assert.assertEquals(TipoPatogeno.BACTERIA.name, eventoPandemia.tipoPatogeno)
        Assert.assertEquals("PATOGENO_ES_PANDEMIA", eventoPandemia.accionQueLoDesencadena)
        Assert.assertEquals("Gripe", eventoPandemia.nombreEspecie)
        Assert.assertNull(eventoPandemia.ubicacionContagio)

    }

    @Test
    fun losEventosDeContagioPorPContagioPorPrimeraVezEnUbicacionDelPatogenoSonConsistentes() {
        Assert.assertTrue(eventoPrimeraVez.tipoEvento is Contagio)
        Assert.assertEquals(TipoPatogeno.HONGO.name, eventoPrimeraVez.tipoPatogeno)
        Assert.assertEquals("PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION", eventoPrimeraVez.accionQueLoDesencadena)
        Assert.assertEquals("algun nombre de especie", eventoPrimeraVez.nombreEspecie)
        Assert.assertEquals("alguna ubicacion", eventoPrimeraVez.ubicacionContagio)
    }

    @Test
    fun elLogDelEventoDeContagioPorPandemiaDelPatogenoEsElIndicado() {
        Assert.assertEquals("El patogeno BACTERIA se volvio pandemia", eventoPandemia.log())
    }

    @Test
    fun elLogDelEventoDeContagioContagioPorPrimeraVezEnUbicacionDelPatogenoEsElIndicado() {
        eventoPrimeraVez.idVectorinfectado = 16.toLong()
        Assert.assertEquals("El patogeno HONGO contagio al vector 16 con la especie algun nombre de especie en la ubicacion alguna ubicacion  por primera vez", eventoPrimeraVez.log())
    }

    @Test
    fun losEventosDeMutacionAlCrearUnaEspecieSonConsistentes(){
        Assert.assertTrue(eventoSeCreaEspecie.tipoEvento is Mutacion)
        Assert.assertEquals(TipoPatogeno.BACTERIA.name, eventoSeCreaEspecie.tipoPatogeno)
        Assert.assertEquals("ESPECIE_CREADA", eventoSeCreaEspecie.accionQueLoDesencadena)
        Assert.assertEquals("Corona", eventoSeCreaEspecie.nombreEspecie)
        Assert.assertNull(eventoSeCreaEspecie.ubicacionContagio)
    }

    @Test
    fun elLogDelEventoDeMutacionPorCrearUnaEspecieEsElIndicado(){
        Assert.assertEquals("Se crea la especie Corona del patogeno BACTERIA ", eventoSeCreaEspecie.log())
    }

    @Test
    fun losEventosDeMutacionAlMutarUnaEspecieSonConsistentes(){
        Assert.assertTrue(eventoSeMutaEspecie.tipoEvento is Mutacion)
        Assert.assertEquals(TipoPatogeno.BACTERIA.name, eventoSeMutaEspecie.tipoPatogeno)
        Assert.assertEquals("ESPECIE_MUTADA", eventoSeMutaEspecie.accionQueLoDesencadena)
        Assert.assertEquals("fiebreDisco", eventoSeMutaEspecie.nombreEspecie)
        Assert.assertNull(eventoSeMutaEspecie.ubicacionContagio)
    }

    @Test
    fun elLogDelEventoDeMutacionPorMutarUnaEspecieEsElIndicado(){
        Assert.assertEquals("La especie fiebreDisco del patogeno BACTERIA ha mutado", eventoSeMutaEspecie.log())
    }
}