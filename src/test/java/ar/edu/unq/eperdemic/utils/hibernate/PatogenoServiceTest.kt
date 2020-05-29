package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.EspecieNotFoundRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoServiceTest {

    lateinit var patogeno : Patogeno
    lateinit var patogenoService : PatogenoService
    lateinit var especieDAO : EspecieDAO
    lateinit var patogenoDAO : PatogenoDAO
    lateinit var especie : Especie

    lateinit var vector : Vector
    lateinit var vector1 : Vector
    lateinit var vector2 : Vector
    lateinit var vector3 : Vector
    lateinit var vectorService : VectorService
    lateinit var dataDAO : DataDAO

    lateinit var ubicacion : Ubicacion
    lateinit var ubicacion1 : Ubicacion
    lateinit var ubicacion2 : Ubicacion
    lateinit var ubicacionService : UbicacionService

    @Before
    fun setUp(){
        patogeno = Patogeno()
        patogeno.tipo = "Nisman"
        especie = Especie()
        especie.nombre = "NombrePendiente"
        especie.paisDeOrigen = "Mi casa"
        especie.patogeno = patogeno
        especieDAO = HibernateEspecieDAO()
        patogenoDAO = HibernatePatogenoDAO()
        patogenoService = PatogenoServiceImpl(patogenoDAO, especieDAO)
        patogenoService.crearPatogeno(patogeno)
        patogenoService.crearEspecie(especie)

        dataDAO = HibernateDataDAO()

        ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO(), dataDAO)
        ubicacion = ubicacionService.crearUbicacion("Japon")
        ubicacion1 = ubicacionService.crearUbicacion("Australia")
        ubicacion2 = ubicacionService.crearUbicacion("Rusia")

        vectorService = VectorServiceImpl(HibernateVectorDAO(), dataDAO, HibernateUbicacionDAO())
        vector = Vector()
        vector.tipo = Humano()
        vector.estado = Sano()
        vector.ubicacion = ubicacion
        vectorService.crearVector(vector)

        vector1 = Vector()
        vector1.tipo = Animal()
        vector1.estado = Sano()
        vector1.ubicacion = ubicacion1
        vectorService.crearVector(vector1)

        vector2 = Vector()
        vector2.tipo = Humano()
        vector2.estado = Sano()
        vector2.ubicacion = ubicacion2
        vectorService.crearVector(vector2)

        vector3 = Vector()
        vector3.tipo = Insecto()
        vector3.estado = Sano()
        vector3.ubicacion = ubicacion
        vectorService.crearVector(vector3)
     }

    @Test
    fun alCrearUnPatogenoEsteQuedaConEstadoCOnsistente(){
        val patogenoRecuperado = patogenoService.recuperarPatogeno(patogeno.id!!.toInt())
        Assert.assertEquals(1, patogenoRecuperado.id!!)
        Assert.assertEquals("Nisman", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioAnimal)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioInsecto)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioHumano)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test
    fun unPatogenoEsActualizable(){
        val patogenoRecuperado1 = patogenoService.recuperarPatogeno(patogeno.id!!.toInt())
        Assert.assertEquals("Nisman", patogenoRecuperado1.tipo)
        patogenoRecuperado1.tipo = "Maradona"
        runTrx {
            patogenoDAO.actualizar(patogenoRecuperado1)
        }
        val patogenoRecuperado2 = patogenoService.recuperarPatogeno(patogeno.id!!.toInt())
        Assert.assertEquals("Maradona", patogenoRecuperado1.tipo)
        Assert.assertEquals("Maradona", patogenoRecuperado2.tipo)
    }

    @Test(expected= PatogenoNotFoundRunTimeException::class)
    fun alIntentarRecuperarUnPatogenoConUNIDInexistenteEsteLanzaLaExcepcionAcorde(){
        val patogenoRecuperado = patogenoService.recuperarPatogeno(42)
    }

    @Test
    fun alCrearUnaEspecieEstaQuedaConEstadoConsistente(){
        val especieRecuperada = patogenoService.recuperarEspecie(especie.id!!.toInt())
        Assert.assertEquals(1, especieRecuperada.id!!)
        especie.nombre = "NombrePendiente"
        especie.paisDeOrigen = "Mi casa"
        Assert.assertEquals("NombrePendiente", especieRecuperada.nombre)
        Assert.assertEquals("Mi casa", especieRecuperada.paisDeOrigen)
        val patogenoRecuperado = especieRecuperada.patogeno
        Assert.assertEquals(1, patogenoRecuperado.id!!)
        Assert.assertEquals("Nisman", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioAnimal)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioInsecto)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioHumano)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }

    @Test(expected= EspecieNotFoundRunTimeException::class)
    fun alIntentarRecuperarUnaEspecieConUnIDInexistenteEsteLanzaLaExcepcionAcorde(){
        val patogenoRecuperado = patogenoService.recuperarEspecie(42)
    }

    @Test
    fun unaEspecieEsActualizable(){
        val especieRecuperada1 = patogenoService.recuperarEspecie(especie.id!!.toInt())
        Assert.assertEquals("NombrePendiente", especieRecuperada1.nombre)
        Assert.assertEquals("Mi casa", especieRecuperada1.paisDeOrigen)
        especieRecuperada1.nombre = "Sarasa"
        especieRecuperada1.paisDeOrigen = "Springfield"
        runTrx {
            especieDAO.actualizar(especieRecuperada1)
        }
        val especieRecuperada2 = patogenoService.recuperarEspecie(especie.id!!.toInt())
        Assert.assertEquals("Sarasa", especieRecuperada2.nombre)
        Assert.assertEquals("Springfield", especieRecuperada2.paisDeOrigen)
    }

    @Test
    fun alUnPatogenoAgregarUnaespecieEstaSeCrea(){
        val especieCreada = patogenoService.agregarEspecie(patogeno.id!!,"Covid-19","China")
        Assert.assertTrue(especieCreada.id != null)
        Assert.assertEquals("Covid-19", especieCreada.nombre)
        Assert.assertEquals("China", especieCreada.paisDeOrigen)
        Assert.assertEquals(patogeno.id!!, especieCreada.patogeno.id!!)
        Assert.assertEquals(patogeno.id!!, patogenoService.recuperarEspecie(especieCreada.id!!).patogeno.id!!)
    }

    @Test
    fun cantidadDeVectoresInfectadosParaUnaEsepecieCreada() {
        Assert.assertEquals(0, patogenoService.cantidadDeInfectados(especie.id!!))

        vectorService.infectar(vector, especie)
        vectorService.infectar(vector, especie)
        vectorService.infectar(vector, especie)
        Assert.assertEquals(1, patogenoService.cantidadDeInfectados(especie.id!!))

        vectorService.infectar(vector1, especie)
        Assert.assertEquals(2, patogenoService.cantidadDeInfectados(especie.id!!))
    }

    @Test(expected = KotlinNullPointerException::class)
    fun cantidadDeVectoresInfectadosParaUnaEsepecieQueNoExiste() {
        val especieSinPersistir = Especie()
        especieSinPersistir.paisDeOrigen = "Arg"
        especieSinPersistir.nombre = "azul"
        especieSinPersistir.cantidadInfectadosParaADN = 12
        especieSinPersistir.patogeno = patogeno

        patogenoService.cantidadDeInfectados(especieSinPersistir.id!!)
    }

    @Test
    fun noEsPandemiaPorqueNoSeEncuentraEnNingunaUbicacion() {
        val especieQueNoInfecto = Especie()
        especieQueNoInfecto.paisDeOrigen = "Arg"
        especieQueNoInfecto.nombre = "azul"
        especieQueNoInfecto.cantidadInfectadosParaADN = 12
        especieQueNoInfecto.patogeno = patogeno
        patogenoService.crearEspecie(especieQueNoInfecto)

        Assert.assertFalse(patogenoService.esPandemia(especieQueNoInfecto.id!!))
    }

    @Test
    fun esPandemiaUnaEspecieQueSeEncuentraEnMasDeLaMitadDeLasUbicacionesYValidoQueNoSeFijePorPatogenoSinoPorEspecie() {
        // Tengo 3 ubicaciones cargadas en el setup de los tests, por lo tanto
        // necesito una especie en 2 ubicaciones para que sea pandemia

        // Con especie2 se verifica que "esPandemia" no mira a nivel de patogeno, sino a nivel de especie.
        // Ya que las 2 especies tienen el mismo patogeno
        val especie2 = Especie()
        especie2.nombre = "Especie0km"
        especie2.paisDeOrigen = "Somewhere"
        especie2.patogeno = patogeno
        patogenoService.crearEspecie(especie2)
        Assert.assertFalse(patogenoService.esPandemia(especie.id!!))
        Assert.assertFalse(patogenoService.esPandemia(especie2.id!!))

        vectorService.infectar(vector, especie)
        vectorService.infectar(vector1, especie)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        Assert.assertFalse(patogenoService.esPandemia(especie2.id!!))
    }

    @Test
    fun noEsPandemiaPorqueSoloSeEncuentraEnAlgunaUbicacionPeroMenosDeLaMitadDelTotalYValidoCon2VectoresEnLaMismaUbicacion() {
        // Tengo 3 ubicaciones cargadas en el setup de los tests, por lo tanto
        // necesito una especie en 2 ubicaciones para que sea pandemia
        Assert.assertFalse(patogenoService.esPandemia(especie.id!!))

        vectorService.infectar(vector, especie)
        Assert.assertFalse(patogenoService.esPandemia(especie.id!!))

        vectorService.infectar(vector3, especie)
        Assert.assertFalse(patogenoService.esPandemia(especie.id!!)) // sigue dando false porque los 2 vectores estan en la misma ubicacion
    }

    @Test
    fun noEsPandemiaPorqueNoExistenUbicaciones() {
        runTrx {
            HibernateDataDAO().clear()
        }
        patogeno = Patogeno()
        patogeno.tipo = "Nisman"
        especie = Especie()
        especie.nombre = "NombrePendiente"
        especie.paisDeOrigen = "Mi casa"
        especie.patogeno = patogeno
        especieDAO = HibernateEspecieDAO()
        patogenoDAO = HibernatePatogenoDAO()
        patogenoService = PatogenoServiceImpl(patogenoDAO, especieDAO)
        patogenoService.crearPatogeno(patogeno)
        patogenoService.crearEspecie(especie)

        Assert.assertFalse(patogenoService.esPandemia(especie.id!!))
    }

    @Test(expected = KotlinNullPointerException::class)
    fun noEsPandemiaArrojaExcepcionPorqueLaEspecieNoExiste() {
        val especieSinPersistir = Especie()
        especieSinPersistir.paisDeOrigen = "Arg"
        especieSinPersistir.nombre = "azul"
        especieSinPersistir.cantidadInfectadosParaADN = 12
        especieSinPersistir.patogeno = patogeno

        patogenoService.esPandemia(especieSinPersistir.id!!)
    }

    @After
    fun clean(){
        runTrx {
            HibernateDataDAO().clear()
        }
    }
}