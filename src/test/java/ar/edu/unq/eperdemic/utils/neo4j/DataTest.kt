package ar.edu.unq.eperdemic.utils.neo4j

import ar.edu.unq.eperdemic.services.Neo4jDataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DataTest {
    val neo4DataDao = Neo4jDataService()

    @Before
    fun setUp(){
        //neo4DataDao.crearSetDeDatosIniciales()
    }

    @Test
    fun falseMain(){
        //Assert.assertTrue(true)
    }

    @After
    fun eliminarTodo(){
        neo4DataDao.eliminarTodo()
    }
}