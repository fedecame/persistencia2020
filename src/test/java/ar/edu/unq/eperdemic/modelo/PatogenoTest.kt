package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.services.MegalodonService
import org.junit.Assert
import org.junit.Test

class PatogenoTest {

    @Test
    fun crearEspecie() {
        val patogeno = Patogeno()
        val especieCreada = patogeno.crearEspecie("nombreDeEspecie", "unPais", 3)
        Assert.assertEquals(patogeno, especieCreada.patogeno)
        Assert.assertEquals("nombreDeEspecie", especieCreada.nombre)
        Assert.assertEquals("unPais", especieCreada.paisDeOrigen)
        Assert.assertEquals(3, especieCreada.cantidadInfectadosParaADN)
        Assert.assertTrue(especieCreada.mutaciones.isEmpty())
        Assert.assertTrue(especieCreada.mutacionesDesbloqueadas.isEmpty())

        val mutacion1 = Mutacion()
        val mutacion2 = Mutacion()
        val especieCreada2 = patogeno.crearEspecie("Covid", "China", 999, mutableSetOf(mutacion1, mutacion2))
        Assert.assertEquals(patogeno, especieCreada2.patogeno)
        Assert.assertEquals("Covid", especieCreada2.nombre)
        Assert.assertEquals("China", especieCreada2.paisDeOrigen)
        Assert.assertEquals(999, especieCreada2.cantidadInfectadosParaADN)
        Assert.assertTrue(especieCreada2.mutaciones.isEmpty())
        Assert.assertTrue(especieCreada2.mutacionesDesbloqueadas.isNotEmpty())
        Assert.assertEquals(2, especieCreada2.mutacionesDesbloqueadas.size)
        Assert.assertTrue(especieCreada2.mutacionesDesbloqueadas.containsAll(mutableSetOf(mutacion1, mutacion2)))

        MegalodonService().eliminarTodo()
    }
}