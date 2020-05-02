package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.transaction.Transactional

class VectorServiceTest {

    private var vectorDao = HibernateVectorDAO()
    private val dataDao = HibernateDataDAO()
    private var vectorService = VectorServiceImpl(vectorDao, dataDao)
    private var vector = Vector()


    @Test
    fun testSeCreaSatifactoriamente(){
        vectorService.crearVector(Vector())
    }

    @Test
    fun testAlCrearUnVectorElModeloQuedaConsistente(){
        Assert.assertEquals(null, vector.id)
        vectorService.crearVector(vector)
        Assert.assertNotEquals(null, vector.id)
        Assert.assertEquals(1, vector.id)
    }

    @Test
    fun testAlCrearUnVectorPuedeSerRecuperadoPorElIDQueRetorna(){
        Assert.assertEquals(null, vector.id)
        vectorService.crearVector(vector)
        Assert.assertNotEquals(null, vector.id)
        Assert.assertEquals(1, vector.id)

        //     val vectorRecuperado = vectorService.recuperarVector(vector.id!!)
 //       Assert.assertEquals(1, vector.id!!)
    }

    @Test
    fun testAlRecuperarUnVectorSeConsigueElIndicado(){
        val vectorRecuperado = vectorService.recuperarVector(1)
        Assert.assertEquals(1, vectorRecuperado.id!!)
    }

     @Test
   fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
   //   val id1 = vectorService.crearVector(Vector())
    //   val id2 = vectorService.crearVector(Vector())
    //  Assert.assertEquals(id1+1, id2)

    }

    @Before
    fun eliminarTodo(){
       vectorService.borrarTodo()
    }

}