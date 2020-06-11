package ar.edu.unq.eperdemic.utils.neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import org.junit.After
import org.junit.Before
import org.junit.Test

class capacidadDeExpancionTest {
    private lateinit var sut : UbicacionService
    private lateinit var vectorService : VectorService
    private lateinit var vectorAnimal : Vector
    private lateinit var vectorHumano : Vector
    private lateinit var vectorInsecto : Vector


    private lateinit var ubicacion0 : Ubicacion
    private lateinit var ubicacion1 : Ubicacion
    private lateinit var ubicacion2 : Ubicacion
    private lateinit var elNodoSolitario : Ubicacion

    @Before
    fun setUp(){
        sut = UbicacionServiceImpl(HibernateUbicacionDAO())
        vectorService : VectorService
        vectorAnimal : Vector
        vectorHumano : Vector
        vectorInsecto : Vector
        ubicacion0 : Ubicacion
        private lateinit var ubicacion1 : Ubicacion
        private lateinit var ubicacion2 : Ubicacion
        private lateinit var elNodoSolitario : Ubicacion


    }

    @Test
    fun laCapacidadDeExpansionDeElNodoSolitario(){

    }

    @After
    fun eliminarTodo(){

    }
}