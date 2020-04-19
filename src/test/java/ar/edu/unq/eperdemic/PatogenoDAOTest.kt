package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoDAOTest {
    private val dao = JDBCPatogenoDAO()
    lateinit var patogeno: Patogeno

    @Before
    fun crearModelo() {
        patogeno = Patogeno("Bacteria", 8)
    }

    @Test
    fun alGuardarYLuegoRecuperarSeObtienePatogenosSimilares() {
        dao.crear(patogeno)

        val patogenoRecuperado = dao.recuperar(1)
        Assert.assertEquals(1, patogenoRecuperado.id)
        Assert.assertEquals(patogeno.tipo, patogenoRecuperado.tipo)
        Assert.assertEquals(patogeno.cantidadDeEspecies, patogenoRecuperado.cantidadDeEspecies)
    }

    @After
    fun emilinarModelo() {
        //dao.eliminar(patogeno)
    }


}