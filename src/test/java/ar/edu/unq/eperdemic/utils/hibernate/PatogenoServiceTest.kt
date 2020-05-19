package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.EspecieNotFoundRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.impl.EstadisticaServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
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
    fun alUnPatogenoAgregarUnaespecieEstaSeCreaUnaEspecie(){
        patogenoService.agregarEspecie(1,"Covic-19","China")
    }

    @After
    fun clean(){
        TransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}