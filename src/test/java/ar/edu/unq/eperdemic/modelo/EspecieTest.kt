package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.EspecieNoCumpleRequisitosParaMutarException
import ar.edu.unq.eperdemic.modelo.tipoMutacion.MutacionFactorContagioAnimal
import ar.edu.unq.eperdemic.modelo.tipoMutacion.MutacionFactorContagioInsecto
import ar.edu.unq.eperdemic.modelo.tipoMutacion.MutacionLetalidad
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

class EspecieTest {
    private lateinit var especie: Especie
    val patogenoMocked = Mockito.mock(Patogeno::class.java)

    lateinit var mutacion: Mutacion
    lateinit var mutacion1: Mutacion
    lateinit var mutacion2: Mutacion
    lateinit var mutacion3: Mutacion

    @Before
    fun setUp(){
        especie = Especie()
        especie.patogeno = patogenoMocked
        especie.nombre = "corona"
        especie.paisDeOrigen = "China"
        especie.cantidadInfectadosParaADN = 12

        mutacion = Mutacion()
        mutacion1 = Mutacion()
        mutacion2 = Mutacion()
        mutacion3 = Mutacion()

        mutacion.adnNecesario = 3
        mutacion.tipo = MutacionLetalidad()
        mutacion.mutacionesNecesarias.addAll(mutableListOf(mutacion1))
        mutacion.mutacionesDesbloqueables.addAll(mutableListOf(mutacion3, mutacion2))

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
    }

    @Test
    fun factoresDeContagioYGettersDeLosOtros2Atributos(){
        Mockito.`when`(patogenoMocked.factorContagioAnimal()).thenReturn(5)
        Mockito.`when`(patogenoMocked.factorContagioInsecto()).thenReturn(14)
        Mockito.`when`(patogenoMocked.factorContagioHumano()).thenReturn(33)
        Mockito.`when`(patogenoMocked.defensaContraMicroorganismos()).thenReturn(22)
        Mockito.`when`(patogenoMocked.letalidad()).thenReturn(4)

        Assert.assertEquals(5, especie.factorContagioAnimal())
        Assert.assertEquals(14, especie.factorContagioInsecto())
        Assert.assertEquals(33, especie.factorContagioHumano())
        Assert.assertEquals(22, especie.defensaContraMicroorganismos())
        Assert.assertEquals(4, especie.letalidad())
    }

    @Test
    fun especieNoTieneMutacionesRepetidasNiDesbloqueadasRepetidas() {
        Assert.assertTrue(especie.mutaciones.isEmpty())
        Assert.assertTrue(especie.mutacionesDesbloqueadas.isEmpty())
        especie.mutaciones.add(mutacion1)
        especie.mutaciones.add(mutacion1)
        especie.mutaciones.add(mutacion)
        especie.mutaciones.add(mutacion)
        especie.mutacionesDesbloqueadas.add(mutacion1)
        especie.mutacionesDesbloqueadas.add(mutacion1)
        especie.mutacionesDesbloqueadas.add(mutacion)
        especie.mutacionesDesbloqueadas.add(mutacion)

        Assert.assertTrue(especie.mutaciones.size == 2)
        Assert.assertTrue(especie.mutacionesDesbloqueadas.size == 2)
        Assert.assertNotNull(especie.mutaciones.find { it == mutacion })
        Assert.assertNotNull(especie.mutaciones.find { it == mutacion1 })
        Assert.assertNotNull(especie.mutacionesDesbloqueadas.find { it == mutacion })
        Assert.assertNotNull(especie.mutacionesDesbloqueadas.find { it == mutacion1 })
    }

    @Test
    fun agregarInfectadoParaADN() {
        Assert.assertEquals(12, especie.cantidadInfectadosParaADN)
        especie.agregarInfectadoParaADN()
        Assert.assertEquals(13, especie.cantidadInfectadosParaADN)
        especie.agregarInfectadoParaADN()
        Assert.assertEquals(14, especie.cantidadInfectadosParaADN)
    }

    @Test
    fun aumentarFactorContagioAnimal() {
        val patogenoSpy = Mockito.spy(Patogeno())
        Assert.assertEquals(0, patogenoSpy.factorContagioAnimal)
        especie.patogeno = patogenoSpy
        especie.aumentarFactorContagioAnimal()
        Mockito.verify(patogenoSpy, times(2)).factorContagioAnimal
        Assert.assertEquals(1, patogenoSpy.factorContagioAnimal)
    }

    @Test
    fun aumentarFactorContagioInsecto() {
        //TODO Mati
    }

    @Test
    fun aumentarFactorContagioHumano() {
        //TODO Mati
    }

    @Test
    fun aumentarDefensaContraMicroorganismos() {
        //TODO Mati
    }

    @Test
    fun aumentarLetalidad() {
        //TODO Mati
    }

    @Test
    fun cantidadDeADNEsLaDivisionEnteraDeLaCantidadDeInfectadosParaADNPor5() {
        Assert.assertEquals(12, especie.cantidadInfectadosParaADN)
        Assert.assertEquals(2, especie.cantidadDeADN())
        especie.agregarInfectadoParaADN()
        especie.agregarInfectadoParaADN()
        Assert.assertEquals(14, especie.cantidadInfectadosParaADN)
        Assert.assertEquals(2, especie.cantidadDeADN())
        especie.agregarInfectadoParaADN()
        Assert.assertEquals(15, especie.cantidadInfectadosParaADN)
        Assert.assertEquals(3, especie.cantidadDeADN())
        especie.cantidadInfectadosParaADN = 0
        Assert.assertEquals(0, especie.cantidadDeADN())
        especie.agregarInfectadoParaADN()
        Assert.assertEquals(0, especie.cantidadDeADN())
    }

    @Test
    fun agregarMutacion() {
        //TODO Mati
    }

    @Test
    fun desbloquearMutaciones() {
        //TODO Mati
    }

    @Test
    fun especieFueMutadaEnMutacion() {
        Assert.assertTrue(especie.mutaciones.isEmpty())
        Assert.assertFalse(especie.fueMutadaEn(mutacion))
        Assert.assertFalse(especie.fueMutadaEn(mutacion1))
        Assert.assertFalse(especie.fueMutadaEn(mutacion3))
        especie.mutaciones.add(mutacion)
        especie.mutaciones.add(mutacion1)
        Assert.assertTrue(especie.mutaciones.isNotEmpty())
        Assert.assertEquals(2, especie.mutaciones.size)
        Assert.assertTrue(especie.fueMutadaEn(mutacion))
        Assert.assertTrue(especie.fueMutadaEn(mutacion1))
        Assert.assertFalse(especie.fueMutadaEn(mutacion3))
    }

    @Test
    fun especieTieneDesbloqueadaLaMutacion() {
        //TODO Mati (casi igual al test de arriba)
    }

    @Test
    fun especieIntentaMutarYPuedeMutarCumpliendoLosRequisitosDeLaMutacion() {
        val mutacionSpy = Mockito.spy(mutacion)
        especie.mutaciones.add(mutacion1)
        especie.mutacionesDesbloqueadas.add(mutacionSpy)
        Assert.assertTrue(especie.mutaciones.size == 1)
        Assert.assertTrue(mutacionSpy.mutacionesNecesarias.size == 1)
        Assert.assertEquals(mutacion1, mutacionSpy.mutacionesNecesarias.first())
        Assert.assertTrue(especie.tieneDesbloqueadaLaMutacion(mutacionSpy))
        Assert.assertEquals(3, mutacionSpy.adnNecesario)
        especie.cantidadInfectadosParaADN = 15
        Assert.assertEquals(3, especie.cantidadDeADN())
        val especieSpy = Mockito.spy(especie)

        especieSpy.mutar(mutacionSpy)
        Mockito.verify(especieSpy).cantidadDeADN()
        Mockito.verify(mutacionSpy, times(3)).adnNecesario
        Mockito.verify(especieSpy).tieneDesbloqueadaLaMutacion(mutacionSpy)
        Mockito.verify(mutacionSpy).validaMutacionesNecesarias(especieSpy)
        Mockito.verify(especieSpy).agregarMutacion(mutacionSpy)
        Mockito.verify(especieSpy, times(2)).cantidadInfectadosParaADN
        Mockito.verify(especieSpy).desbloquearMutaciones(mutacionSpy.mutacionesDesbloqueables)
        Mockito.verify(mutacionSpy).mutarAtributoDeEspecie(especieSpy)
        Mockito.verify(especieSpy).aumentarLetalidad()

        Assert.assertTrue(especieSpy.mutaciones.contains(mutacionSpy))
        Assert.assertEquals(0, especieSpy.cantidadDeADN())
        Assert.assertTrue(especieSpy.mutacionesDesbloqueadas.containsAll(mutacionSpy.mutacionesDesbloqueables))
    }

    @Test(expected = EspecieNoCumpleRequisitosParaMutarException::class)
    fun especieIntentaMutarYNoPuedeMutarPorqueNoTieneSuficienteADN() {
        Assert.assertEquals(1, mutacion1.adnNecesario)
        especie.cantidadInfectadosParaADN = 4
        Assert.assertEquals(0, especie.cantidadDeADN())
        especie.mutacionesDesbloqueadas.add(mutacion1)
        Assert.assertTrue(especie.tieneDesbloqueadaLaMutacion(mutacion1))
        Assert.assertTrue(especie.mutaciones.containsAll(mutacion1.mutacionesNecesarias))

        especie.mutar(mutacion1)
    }

    @Test(expected = EspecieNoCumpleRequisitosParaMutarException::class)
    fun especieNoPuedeMutarEnMutacionPorqueNoTieneDesbloqueadaLaMutacion() {
        Assert.assertEquals(2, mutacion2.adnNecesario)
        Assert.assertEquals(2, especie.cantidadDeADN())
        Assert.assertFalse(especie.tieneDesbloqueadaLaMutacion(mutacion2))
        especie.mutaciones.add(mutacion1)
        Assert.assertTrue(especie.mutaciones.containsAll(mutacion2.mutacionesNecesarias))

        especie.mutar(mutacion1)
    }

    @Test(expected = EspecieNoCumpleRequisitosParaMutarException::class)
    fun especieNoPuedeMutarEnMutacionPorqueNoTieneTodasLasMutacionesDeRequisito() {
        Assert.assertEquals(1, mutacion3.adnNecesario)
        Assert.assertEquals(2, especie.cantidadDeADN())
        especie.mutacionesDesbloqueadas.add(mutacion3)
        Assert.assertTrue(especie.tieneDesbloqueadaLaMutacion(mutacion3))
        especie.mutaciones.add(mutacion1)
        Assert.assertFalse(especie.mutaciones.containsAll(mutacion3.mutacionesNecesarias))

        especie.mutar(mutacion1)
    }

    @Test
    fun descontarAdnDescuenta5ALaCantidadDeInfectaodsParaAdn() {
        //TODO Mati (parecido al de cantidad de adn)
    }
}
