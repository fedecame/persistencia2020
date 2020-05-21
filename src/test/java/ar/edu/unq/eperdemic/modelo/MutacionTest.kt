package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.tipoMutacion.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MutacionTest {
    lateinit var mutacion: Mutacion
    lateinit var mutacion1: Mutacion
    lateinit var mutacion2: Mutacion
    lateinit var mutacion3: Mutacion
    lateinit var mutacion4: Mutacion
    lateinit var mutacion5: Mutacion
    lateinit var patogeno: Patogeno
    lateinit var especie: Especie

    @Before
    fun setUp(){
        mutacion = Mutacion()
        mutacion1 = Mutacion()
        mutacion2 = Mutacion()
        mutacion3 = Mutacion()
        mutacion4 = Mutacion()
        mutacion5 = Mutacion()

        mutacion.adnNecesario = 3
        mutacion.tipo = MutacionLetalidad()
        mutacion.mutacionesNecesarias.addAll(mutableListOf(mutacion1))
        mutacion.mutacionesDesbloqueables.addAll(mutableListOf(mutacion3, mutacion5))

        mutacion1.adnNecesario = 1
        mutacion1.tipo = MutacionFactorContagioInsecto()
        mutacion1.mutacionesDesbloqueables.add(mutacion)

        mutacion2.adnNecesario = 2
        mutacion2.tipo = MutacionFactorContagioAnimal()
        mutacion2.mutacionesNecesarias.add(mutacion1)
        mutacion2.mutacionesDesbloqueables.add(mutacion)

        mutacion3.adnNecesario = 1
        mutacion3.tipo = MutacionFactorContagioAnimal()
        mutacion3.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion, mutacion2))
        mutacion3.mutacionesDesbloqueables.addAll(mutableListOf(mutacion4, mutacion5))

        mutacion4.adnNecesario = 2
        mutacion4.tipo = MutacionFactorContagioHumano()
        mutacion4.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3))
        mutacion4.mutacionesDesbloqueables.add(mutacion5)

        mutacion5.adnNecesario = 4
        mutacion5.tipo = MutacionDefensaMicroorganismos()
        mutacion5.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3, mutacion4))

        patogeno = Patogeno()
        patogeno.cantidadDeEspecies = 1
        patogeno.tipo = "Virus"
        patogeno.factorContagioHumano = 80
        patogeno.factorContagioInsecto = 15
        patogeno.factorContagioAnimal = 50
        patogeno.defensaContraMicroorganismos = 30
        patogeno.letalidad = 5

        especie = Especie()
        especie.nombre = "Corona"
        especie.paisDeOrigen = "China"
        especie.mutacionesDesbloqueadas.add(mutacion1)
        especie.cantidadInfectadosParaADN = 18
        especie.patogeno = patogeno
    }

    @Test
    fun mutacionNoTieneMutacionesNecesariasRepetidasNiDesbloqueablesRepetidas() {
        Assert.assertTrue(mutacion.mutacionesNecesarias.size == 1)
        Assert.assertTrue(mutacion.mutacionesDesbloqueables.size == 2)
        Assert.assertNotNull(mutacion.mutacionesNecesarias.find { it == mutacion1 })
        Assert.assertNotNull(mutacion.mutacionesDesbloqueables.find { it == mutacion3 })
        Assert.assertNotNull(mutacion.mutacionesDesbloqueables.find { it == mutacion5 })

        mutacion.mutacionesDesbloqueables.add(mutacion3)
        mutacion.mutacionesDesbloqueables.add(mutacion5)
        mutacion.mutacionesNecesarias.add(mutacion1)

        Assert.assertTrue(mutacion.mutacionesNecesarias.size == 1)
        Assert.assertTrue(mutacion.mutacionesDesbloqueables.size == 2)
    }

    @Test
    fun mutarAtributoDeEspecieConTipoDeMutacionLetalidad() {
        val especieSpy = Mockito.spy(especie)
        Assert.assertTrue(mutacion.tipo is MutacionLetalidad)
        mutacion.mutarAtributoDeEspecie(especieSpy)
        Mockito.verify(especieSpy).aumentarLetalidad()
    }

    @Test
    fun mutarAtributoDeEspecieConTipoDeMutacionFactorContagioInsecto() {
        val especieSpy = Mockito.spy(especie)
        Assert.assertTrue(mutacion1.tipo is MutacionFactorContagioInsecto)
        mutacion1.mutarAtributoDeEspecie(especieSpy)
        Mockito.verify(especieSpy).aumentarfactorContagioInsecto()
    }

    @Test
    fun mutarAtributoDeEspecieConTipoDeMutacionFactorContagioAnimal() {
        val especieSpy = Mockito.spy(especie)
        Assert.assertTrue(mutacion2.tipo is MutacionFactorContagioAnimal)
        mutacion2.mutarAtributoDeEspecie(especieSpy)
        Mockito.verify(especieSpy).aumentarFactorContagioAnimal()
    }

    @Test
    fun mutarAtributoDeEspecieConTipoDeMutacionFactorContagioHumano() {
        val especieSpy = Mockito.spy(especie)
        Assert.assertTrue(mutacion4.tipo is MutacionFactorContagioHumano)
        mutacion4.mutarAtributoDeEspecie(especieSpy)
        Mockito.verify(especieSpy).aumentarfactorContagioHumano()
    }

    @Test
    fun mutarAtributoDeEspecieConTipoDeMutacionDefensaMicroorganismos() {
        val especieSpy = Mockito.spy(especie)
        Assert.assertTrue(mutacion5.tipo is MutacionDefensaMicroorganismos)
        mutacion5.mutarAtributoDeEspecie(especieSpy)
        Mockito.verify(especieSpy).aumentarDefensaContraMicroorganismos()
    }

    @Test
    fun validarMutacionesNecesariasSinMutacionesEnLaEspecieYSinRequerimientoDeMutacionesParaLaMutacion() {
        Assert.assertTrue(especie.mutaciones.isEmpty())
        Assert.assertTrue(mutacion1.mutacionesNecesarias.isEmpty())
        Assert.assertTrue(mutacion1.validaMutacionesNecesarias(especie))
    }

    @Test
    fun validarMutacionesNecesariasSinMutacionesEnLaEspecieYConRequerimientoDeMutacionesParaMutar() {
        Assert.assertTrue(especie.mutaciones.isEmpty())
        Assert.assertTrue(mutacion2.mutacionesNecesarias.isNotEmpty())
        Assert.assertFalse(mutacion2.validaMutacionesNecesarias(especie))
    }

    @Test
    fun validarMutacionesNecesariasConMutacionesQueCumplenLosRequerimientosDeMutacionesParaMutar() {
        especie.mutaciones.add(mutacion1)
        Assert.assertTrue(mutacion2.mutacionesNecesarias.size == 1)
        Assert.assertEquals(mutacion1, mutacion2.mutacionesNecesarias.first())
        Assert.assertTrue(mutacion2.validaMutacionesNecesarias(especie))
    }

    @Test
    fun validarMutacionesNecesariasConMutacionesQueNoCumplenLosRequerimientosDeMutacionesParaMutarPeroSonParteDeLosRequerimientos() {
        especie.mutaciones.add(mutacion)
        Assert.assertNotNull(mutacion4.mutacionesNecesarias.find { it == mutacion })
        Assert.assertTrue(mutacion4.mutacionesNecesarias.size > 1)
        Assert.assertTrue(especie.mutaciones.size == 1)
        Assert.assertFalse(mutacion4.validaMutacionesNecesarias(especie))
    }

    @Test
    fun validarMutacionesNecesariasConMutacionesQueNoCumplenLosRequerimientosDeMutacionesParaMutarNiSonParteDeLosRequerimientos() {
        especie.mutaciones.add(mutacion5)
        Assert.assertNull(mutacion3.mutacionesNecesarias.find { it == mutacion5 })
        Assert.assertTrue(mutacion3.mutacionesNecesarias.size > 1)
        Assert.assertTrue(especie.mutaciones.size == 1)
        Assert.assertFalse(mutacion5.validaMutacionesNecesarias(especie))
    }
}