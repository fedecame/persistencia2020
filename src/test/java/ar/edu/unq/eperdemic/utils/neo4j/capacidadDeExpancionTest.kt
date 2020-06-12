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
    private lateinit var ubicacionService : UbicacionService
    private lateinit var vectorAnimal : Vector
    private lateinit var vectorHumano : Vector
    private lateinit var vectorInsectoA : Vector
    private lateinit var vectorInsectoB : Vector
    private lateinit var vectores : List<Vector>

    private lateinit var ubicacion0 : Ubicacion
    private lateinit var ubicacion1 : Ubicacion
    private lateinit var ubicacion2 : Ubicacion
    private lateinit var elNodoSolitario : Ubicacion

    @Before
    fun setUp(){
        //Aca elimino lo que haya
        this.eliminarTodo()
        val ubicacionDao = HibernateUbicacionDAO()
        val vectorDao = HibernateVectorDAO()
        sut = UbicacionServiceImpl(ubicacionDao)
        dataService = Neo4jDataService()
        vectorService = VectorServiceImpl(vectorDao, ubicacionDao)
        ubicacionService = UbicacionServiceImpl(ubicacionDao)
        ubicacion0 = Ubicacion()

        //No llega a ningun lado. A el llegan por Aereo
        ubicacion0 = ubicacionService.crearUbicacion( "WonderLand")
        ubicacion1 = ubicacionService.crearUbicacion("Mordor")
        ubicacion2 = ubicacionService.crearUbicacion("Remedios de Escalada")
        elNodoSolitario = ubicacionService.crearUbicacion("elNodoSolitario")
        vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorAnimal.ubicacion = ubicacion0
        vectorHumano = Vector()
        vectorHumano.ubicacion = ubicacion1
        vectorHumano.tipo = Humano()
        vectorInsectoA = Vector()
        vectorInsectoA.tipo = Insecto()
        vectorInsectoA.ubicacion = ubicacion2
        vectorInsectoB = Vector()
        vectorInsectoB.tipo = Insecto()
        vectorInsectoB.ubicacion = elNodoSolitario

        vectorService.crearVector(vectorAnimal)
        vectorService.crearVector(vectorHumano)
        vectorService.crearVector(vectorInsectoA)
        vectorService.crearVector(vectorInsectoB)
        dataService.crearSetDeDatosIniciales()
    }

    @Test
    fun laCapacidadDeExpansionDeElNodoSolitarioEs0(){
        val capacidad = 0

    }

    @After
    fun eliminarTodo(){
        /*TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            HibernateDataDAO().clear()
        }*/
    }
}