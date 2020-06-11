package ar.edu.unq.eperdemic.utils.neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Before
import org.junit.Test

class capacidadDeExpancionTest {
    private lateinit var sut : UbicacionService
    private lateinit var dataService : DataService
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
        val ubicacionDao = HibernateUbicacionDAO()
        val vectorDao = HibernateVectorDAO()
        sut = UbicacionServiceImpl(ubicacionDao)
        dataService = Neo4jDataService()
        vectorService = VectorServiceImpl(vectorDao, ubicacionDao)
        ubicacion0 = Ubicacion()
        //No llega a ningun lado. A el llegan por Aereo
        ubicacion0.nombreUbicacion = "WonderLand"
        ubicacion1 = Ubicacion()
        ubicacion1.nombreUbicacion = ""
        ubicacion2 = Ubicacion()
        ubicacion2.nombreUbicacion = ""

        //No llega a nadie ni pueden llegar a el
        elNodoSolitario.nombreUbicacion = "elNodoSolitario"
        vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorHumano = Vector()
        vectorHumano.tipo = Humano()
        vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()




    }

    @Test
    fun laCapacidadDeExpansionDeElNodoSolitario(){

    }

    @After
    fun eliminarTodo(){
        TransactionRunner.addHibernate().runTrx {
                HibernateDataDAO().clear()
        }
        TransactionRunner.addNeo4j().runTrx {
            HibernateDataDAO().clear()
        }
    }
}