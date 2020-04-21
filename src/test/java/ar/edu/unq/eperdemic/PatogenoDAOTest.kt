package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.utils.jdbc.DataDAOImpl
import ar.edu.unq.eperdemic.utils.jdbc.DataServiceJDBC
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoDAOTest : ModData(){

    @Before
    override fun crearModelo() {
        super.crearModelo()
    }

    @Test(expected = PatogenoNotFoundRunTimeException::class)
    fun alRecuperarUnPatogenoNoExistenteLanzaUnaExcepcion() {
        dao.recuperar(42)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilares() {
        val patogenoRecuperado = dao.recuperar(1)
        Assert.assertEquals(1, patogenoRecuperado.id)
        Assert.assertEquals("Bacteria", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilaresHabiendoDosEnLaTabla() {
        val patogenoRecuperado = dao.recuperar(2)
        Assert.assertEquals(2, patogenoRecuperado.id)
        Assert.assertEquals("Hongo", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilaresHabiendoTresEnLaTabla() {
        val patogenoRecuperado = dao.recuperar(3)
        Assert.assertEquals(3, patogenoRecuperado.id)
        Assert.assertEquals("Protozoo", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilaresHabiendoCuatroEnLaTabla() {
        val patogenoRecuperado = dao.recuperar(4)
        Assert.assertEquals(4, patogenoRecuperado.id)
        Assert.assertEquals("Virus", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun seModificanLosValoresDelPatogenoAlActualizarlo() {
        val patogenoOriginal = dao.recuperar(4)
        Assert.assertEquals(4, patogenoOriginal.id)
        Assert.assertEquals("Virus", patogenoOriginal.tipo)
        Assert.assertEquals(0, patogenoOriginal.cantidadDeEspecies)

        val patogenoActualizado = Patogeno("Caca")
        patogenoActualizado.id = 4
        patogenoActualizado.cantidadDeEspecies = 42
        dao.actualizar(patogenoActualizado)
        val patogenoRecuperado = dao.recuperar(4)
        Assert.assertEquals(patogenoOriginal.id, patogenoRecuperado.id)
        Assert.assertEquals(4, patogenoRecuperado.id)
        Assert.assertEquals(patogenoOriginal.tipo, patogenoRecuperado.tipo)
        Assert.assertEquals(patogenoOriginal.cantidadDeEspecies, patogenoRecuperado.cantidadDeEspecies)
        Assert.assertEquals(patogenoOriginal.cantidadDeEspecies, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test(expected = PatogenoNotFoundRunTimeException::class)
    fun alIntentarActualizarUnPatogenoQueNoExisteArrojaUnaExcepcion() {
        val fruta = Patogeno("Sarasa")
        fruta.id = 666
        dao.actualizar(fruta)
    }

    @Test
    fun elRecuperarTodosTraeUnaListaVaciaCuandoNoHayNingunDatoCargado() {
        this.eliminarModelo()
        val patogenosRecuperados = dao.recuperarATodos()
        Assert.assertEquals(0, patogenosRecuperados.size)
        Assert.assertTrue(patogenosRecuperados.isEmpty())
    }

    @Test
    fun elRecuperarTodosTraeUnaListaCon4PatogenosOrdenadosAlfabeticamenteSegunSuTipo() {
        val patogenosRecuperados = dao.recuperarATodos()
        Assert.assertEquals(4, patogenosRecuperados.size)
        Assert.assertFalse(patogenosRecuperados.isEmpty())
        Assert.assertEquals("Bacteria", patogenosRecuperados.get(0).tipo)
        Assert.assertEquals("Hongo", patogenosRecuperados.get(1).tipo)
        Assert.assertEquals("Protozoo", patogenosRecuperados.get(2).tipo)
        Assert.assertEquals("Virus", patogenosRecuperados.get(3).tipo)
    }

    @After
    fun eliminarModelo() {
        super.eliminarModelo()
    }
}