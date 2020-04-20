package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.utils.jdbc.DataDAOImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoDAOTest {
    private val dao = JDBCPatogenoDAO()
    private val dataDao = DataDAOImpl()

    @Before
    fun crearModelo() {

    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilares() {
        val idCreado = dao.crear(Patogeno("Bacteria"))
        val patogenoRecuperado = dao.recuperar(idCreado)
        Assert.assertEquals(1, idCreado)
        Assert.assertEquals(idCreado, patogenoRecuperado.id)
        Assert.assertEquals("Bacteria", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilaresHabiendoMasDeUnoEnLaTabla() {
        val idCreado = dao.crear(Patogeno("Hongo"))
        val patogenoRecuperado = dao.recuperar(idCreado)
        Assert.assertEquals(1, idCreado)
        Assert.assertEquals(idCreado, patogenoRecuperado.id)
        Assert.assertEquals("Hongo", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
        dao.eliminar(patogenoRecuperado)
    }

    @Test
    fun seModificanLosValoresDelPatogenoAlActualizarlo() {
        dao.crear(Patogeno("Virus"))
        val patogenoOriginal = dao.recuperar(1)
        Assert.assertEquals(1, patogenoOriginal.id)
        Assert.assertEquals("Virus", patogenoOriginal.tipo)
        Assert.assertEquals(0, patogenoOriginal.cantidadDeEspecies)

        val patogenoActualizado = Patogeno("Caca", 42, 1)
        dao.actualizar(patogenoActualizado)
        val patogenoRecuperado = dao.recuperar(1)
        Assert.assertEquals(1, patogenoRecuperado.id)
        Assert.assertEquals("Caca", patogenoRecuperado.tipo)
        Assert.assertEquals(42, patogenoRecuperado.cantidadDeEspecies)
        dao.eliminar(patogenoActualizado)
    }

    @After
    fun emilinarModelo() {
        dataDao.eliminarTodo()
    }


}