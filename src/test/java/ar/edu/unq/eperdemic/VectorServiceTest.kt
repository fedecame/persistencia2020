package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Humano
import ar.edu.unq.eperdemic.modelo.TipoVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorServiceTest {

    lateinit var vectorService : VectorService
    lateinit var vector : Vector
    lateinit var tipo : TipoVector


    @Before
    fun setUp(){
        vector = Vector()
        tipo = Humano()
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())
        vector.tipo = tipo
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
        vector0.tipo = tipo
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
         val vector0 = Vector()
         vector0.tipo = tipo
         val vector1 = Vector()
         vector1.tipo = tipo
        val id1 = vectorService.crearVector(vector0).id!!
        val id2 = vectorService.crearVector(vector1).id!!
        Assert.assertTrue(id1 < id2)
         Assert.assertEquals(id1+1, id2)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarPorSuID(){
        val vectorAGuardar = Vector()
        vectorAGuardar.tipo = tipo
        vectorService.crearVector(vectorAGuardar)
        val vectorRecuperado = vectorService.recuperarVector(vectorAGuardar.id!!.toInt())
        Assert.assertEquals(2, vectorRecuperado.id!!)
    }


    @After
    open fun eliminarTodo(){
        vectorService.borrarTodo()
    }

}