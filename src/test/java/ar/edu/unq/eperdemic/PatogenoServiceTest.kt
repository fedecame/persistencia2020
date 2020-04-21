package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoServiceTest : ModData(){
    private var service = PatogenoServiceImpl(dao)

    @Before
    override fun crearModelo() {
        super.crearModelo()
    }

    @Test(expected = PatogenoNotFoundRunTimeException::class)
    fun alRecuperarUnPatogenoNoExistenteLanzaUnaExcepcion() {
        dao.recuperar(42)
    }

    @Test
    fun alCrearPatogenoYLuegoRecuperarSeObtienePatogenosSimilares() {
        val idPatogenoCreado = service.crearPatogeno(Patogeno("ProbandoService"))
        val patogenoRecuperado = service.recuperarPatogeno(idPatogenoCreado)
        Assert.assertEquals(idPatogenoCreado, patogenoRecuperado.id)
        Assert.assertEquals("ProbandoService", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun seModificanLosValoresDelPatogenoAlActualizarlo() {
        val patogenoOriginal = service.recuperarPatogeno(4)
        Assert.assertEquals(4, patogenoOriginal.id)
        Assert.assertEquals("Virus", patogenoOriginal.tipo)
        Assert.assertEquals(0, patogenoOriginal.cantidadDeEspecies)

        service.agregarEspecie(4, "Sarasa", "MiCasa")
        val patogenoRecuperado = service.recuperarPatogeno(4)
        Assert.assertEquals(4, patogenoRecuperado.id)
        Assert.assertEquals("Virus", patogenoRecuperado.tipo)
        Assert.assertEquals(1, patogenoRecuperado.cantidadDeEspecies)
        Assert.assertFalse(patogenoOriginal.cantidadDeEspecies == patogenoRecuperado.cantidadDeEspecies)
    }


    @Test
    fun elRecuperarTodosTraeUnaListaVaciaCuandoNoHayNingunDatoCargado() {
        this.eliminarModelo()
        val patogenosRecuperados = service.recuperarATodosLosPatogenos()
        Assert.assertEquals(0, patogenosRecuperados.size)
        Assert.assertTrue(patogenosRecuperados.isEmpty())
    }

    @Test
    fun elRecuperarTodosTraeUnaListaCon4PatogenosOrdenadosAlfabeticamenteSegunSuTipo() {
        val patogenosRecuperados = service.recuperarATodosLosPatogenos()
        Assert.assertEquals(4, patogenosRecuperados.size)
        Assert.assertFalse(patogenosRecuperados.isEmpty())
        Assert.assertEquals("Bacteria", patogenosRecuperados.get(0).tipo)
        Assert.assertEquals("Hongo", patogenosRecuperados.get(1).tipo)
        Assert.assertEquals("Protozoo", patogenosRecuperados.get(2).tipo)
        Assert.assertEquals("Virus", patogenosRecuperados.get(3).tipo)
    }
    @After
    override fun eliminarModelo() {
        super.eliminarModelo()
    }
}