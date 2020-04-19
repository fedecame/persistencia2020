package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoDAOTest {
    private val dao = JDBCPatogenoDAO()
    //lateinit var patogeno: Patogeno

    @Before
    fun crearModelo() {
    //    patogeno = Patogeno("Bacteria", 8)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilares() {
        dao.crear(Patogeno("Bacteria", 8))

        val patogenoRecuperado = dao.recuperar(1)
        Assert.assertEquals(1, patogenoRecuperado.id)
        Assert.assertEquals("Bacteria", patogenoRecuperado.tipo)
        Assert.assertEquals(8, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilaresHabiendoMasDeUnoEnLaTabla() {
        dao.crear(Patogeno("Hongo"))
        val patogenoRecuperado = dao.recuperar(2)
        Assert.assertEquals(2, patogenoRecuperado.id)
        Assert.assertEquals("Hongo", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun seModificanLosValoresDelPatogenoAlActualizarlo() {
        val patogenoOriginal = dao.recuperar(2)
        Assert.assertEquals(2, patogenoOriginal.id)
        Assert.assertEquals("Hongo", patogenoOriginal.tipo)
        Assert.assertEquals(0, patogenoOriginal.cantidadDeEspecies)

        val patogenoActualizado = Patogeno("Caca", 42,  2)
        dao.actualizar(patogenoActualizado)
        val patogenoRecuperado = dao.recuperar(2)
        Assert.assertEquals(2, patogenoRecuperado.id)
        Assert.assertEquals("Caca", patogenoRecuperado.tipo)
        Assert.assertEquals(42, patogenoRecuperado.cantidadDeEspecies)
    }
    //@After
    //fun emilinarModelo() {
        //dao.eliminar(patogeno)
    //}


}