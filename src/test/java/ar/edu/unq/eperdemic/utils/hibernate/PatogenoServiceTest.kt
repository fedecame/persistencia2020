package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.impl.HibernatePatogenoServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoServiceTest {

    lateinit var patogeno : Patogeno
    lateinit var patogenoService : PatogenoService
    lateinit var especieDAO : EspecieDAO
    lateinit var patogenoDAO : PatogenoDAO

    @Before
    fun setUp(){
        patogeno = Patogeno()
        patogeno.tipo = "Nisman"
        especieDAO = HibernateEspecieDAO()
        patogenoDAO = HibernatePatogenoDAO()
        patogenoService = HibernatePatogenoServiceImpl(patogenoDAO, especieDAO)
        patogenoService.crearPatogeno(patogeno)
     }

    @Test
    fun alCrearUnPatogenoEsteQuedaConEstadoCOnsistente(){
        print("ACA>>>>>>>>>>>>>>>>>>> " + patogeno.id)
        val patogenoRecuperado = patogenoService.recuperarPatogeno(patogeno.id!!.toInt())
        Assert.assertEquals(1, patogenoRecuperado.id!!)
        Assert.assertEquals("Nisman", patogenoRecuperado.tipo)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioAnimal)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioInsecto)
        Assert.assertEquals(0, patogenoRecuperado.factorContagioHumano)
        Assert.assertEquals(0, patogenoRecuperado.cantidadDeEspecies)
    }



    @After
    fun clean(){
        TransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}