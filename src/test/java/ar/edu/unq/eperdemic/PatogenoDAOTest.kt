package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.utils.jdbc.DataDAOImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoDAOTest {
    private val dao = JDBCPatogenoDAO()
    private val dataDao = DataDAOImpl(dao)

    @Before
    fun crearModelo() {
        dataDao.crearSetDatosIniciales()
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilares() {
        val patogenoRecuperado = dao.recuperar(1)
        Assert.assertEquals(1, patogenoRecuperado.id)
        Assert.assertEquals("Bacteria", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilaresHabiendoMasDeUnoEnLaTabla() {
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

        val patogenoActualizado = Patogeno("Caca", 42, 4)
        dao.actualizar(patogenoActualizado)
        val patogenoRecuperado = dao.recuperar(4)
        Assert.assertEquals(4, patogenoRecuperado.id)
        Assert.assertEquals("Caca", patogenoRecuperado.tipo)
        Assert.assertEquals(42, patogenoRecuperado.cantidadDeEspecies)
    }

    //*Aun no lanza la excepcion
    @Test(expected = PatogenoNotFoundRunTimeException::class)
    fun alIntentarActualizarUnPatogenoQueNoExisteArrojaUnaExcepcion() {
        val fruta = Patogeno("Sarasa", 108, 666)
        dao.actualizar(fruta)
    }

    @After
    fun eliminarModelo() {
        dataDao.eliminarTodo()
    }

}