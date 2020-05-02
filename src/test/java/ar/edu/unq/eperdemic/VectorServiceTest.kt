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

    lateinit  var vectorService : VectorService
    lateinit var vector : Vector


    @After
    fun setUp(){
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())
        vectorService.crearVector(vector)

    }

    @Test
    fun testAlCrearUnVectorElModeloQuedaConsistente(){
        Assert.assertEquals(null, vector.id)
        vectorService.crearVector(vector)
        Assert.assertNotEquals(null, vector.id)
        Assert.assertEquals(1, vector.id)
    }

    @Test(expected = IDVectorNoEncontradoException::class)
    fun testAlIntentarRecuperarUnVectorConUNIdInexistenteSeLanzaUNaIDVectorNoEncontradoException(){
        vectorService.recuperarVector(42)
    }

     @Test
   fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
        val id1 = vectorService.crearVector(Vector()).id!!
        val id2 = vectorService.crearVector(Vector()).id!!
        Assert.assertTrue(id1 < id2)
        Assert.assertEquals(id1+1, id2)
        Assert.assertEquals(1, id1)
        Assert.assertEquals(2, id2)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarPorSuID(){
        val vectorCreado = vectorService.crearVector(Vector())
        val vectorRecuperado = vectorService.recuperarVector(vectorCreado.id!!)
        Assert.assertEquals(1, vectorCreado.id)
    }


    @Before
    open fun eliminarTodo(){
       vectorService.borrarTodo()
    }

}