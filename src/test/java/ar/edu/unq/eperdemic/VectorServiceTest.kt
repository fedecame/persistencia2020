package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.transaction.Transactional

class VectorServiceTest {

    lateinit var vectorService : VectorService
    lateinit var vector : Vector


    @Before
    fun setUp(){
        vector = Vector()
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())
        vectorService.crearVector(vector)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarConSuID(){
        val recuperado = vectorService.recuperarVector(1)
        Assert.assertEquals(1, recuperado.id!!)
    }

    @Test
    fun testAlCrearUnVectorElModeloQuedaConsistente(){
        val vector0 = Vector()
        Assert.assertEquals(null, vector0.id)
        vectorService.crearVector(vector0)
        Assert.assertNotEquals(null, vector0.id)
        Assert.assertEquals(1, vector.id!!.toInt())
    }

    @Test()
    fun testAlIntentarRecuperarUnVectorConUNIdInexistenteSeLanzaUNaIDVectorNoEncontradoException(){
        vectorService.recuperarVector(420)
    }

     @Test
   fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
        val id1 = vectorService.crearVector(Vector()).id!!
        val id2 = vectorService.crearVector(Vector()).id!!
        Assert.assertTrue(id1 < id2)
         Assert.assertEquals(id1+1, id2)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarPorSuID(){
        val vectorAGuardar = Vector()
        vectorService.crearVector(vectorAGuardar)
        val vectorRecuperado = vectorService.recuperarVector(vectorAGuardar.id!!.toInt())
        Assert.assertEquals(2, vectorAGuardar.id!!)
    }


    @After
    open fun eliminarTodo(){
       vectorService.borrarTodo()
    }

}