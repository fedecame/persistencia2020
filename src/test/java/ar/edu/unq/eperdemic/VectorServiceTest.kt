package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import javax.transaction.Transactional
@Transactional
open class VectorServiceTest {

    private var vectorDao = HibernateVectorDAO()
    private var vectorService = VectorServiceImpl(vectorDao)
    private val hibernateDataDao = HibernateDataDAO()

    fun setVector(vector : Vector, id : Int){
        vector.id = id
    }

    @Test
    fun testSeCreaUnVector(){
        val id = vectorService.crearVector(Vector())
        //val vectorRecuperado = vectorService.recuperarVector(0)
        Assert.assertEquals(0, id)
    }

    @Test
    open fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
        val id1 = vectorService.crearVector(Vector())
        Assert.assertEquals(1, id1)
        val id2 = vectorService.crearVector(Vector())
        Assert.assertEquals(2, id2)

    }

    @Test
    open fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores2(){
        val id = vectorService.crearVector(Vector())
        //val vectorRecuperado = vectorService.recuperarVector(0)
        Assert.assertEquals(1, id)
    }



    @Before
    fun eliminarTodo(){
        //Esto no funciona pero deberia estar
       // hibernateDataDao.clear()
    }

}