package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.springframework.data.jpa.domain.AbstractPersistable_.id

class UbicacionNeo4jTest {
    lateinit var neo4jData: Neo4jDataService


    var ubicacionService=UbicacionServiceImpl(HibernateUbicacionDAO())
    var mockUbicacionService = mock(UbicacionServiceImpl(HibernateUbicacionDAO()).javaClass)
    var mockVectorService=mock(VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO()).javaClass)
    var vector= Vector()
    var ubicacionPlantalandia= Ubicacion()
    var ubicacionBichoLandia=Ubicacion()
    var ubicacionTibetDojo=Ubicacion()
    var vectorService=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO())

    @Before
    fun setUp() {
        neo4jData = Neo4jDataService()
    }

    @Test
    fun vectorQuiereMoverAUbicacionNoAledaña() {
        var daoNeo4j = Neo4jUbicacionDAO()
        TransactionRunner.addNeo4j().runTrx {
            daoNeo4j.conectar("Plantalandia", "TibetDojo", "Terrestre")
        }
    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())

       var vector = Vector()
    var ubicacionPlantalandia = Ubicacion()
    var ubicacionBichoLandia = Ubicacion()
    var ubicacionTibetDojo = Ubicacion()
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
    var id: Int = 0
    @Before
    fun setUp() {
        ubicacionService.crearUbicacion("BichoLandia")
        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
        vector.ubicacion = ubicacionService.crearUbicacion("Plantalandia")
        vector.tipo = Humano()
        vector.estado = Sano()
        id = vectorService.crearVector(vector)?.id?.toInt()!!
        var id = vectorService.crearVector(vector).id
        if (id != null) {
            ubicacionService.mover(id.toInt(), "TibetDojo")
        }


    @Test
    fun vectorQuiereMoverAUbicacionNoAledaña() {


          //  daoNeo4j.conectar("Plantalandia", "TibetDojo", "Terrestre")
//            ubicacionService.mover(id, "TibetDojo")


    }

    @Test(expected = UbicacionMuyLejana::class)
    fun vectorNoPuedeMoverPorqueUbicacionEsLejana() {

//        ubicacionService.mover(id,"")
    }
}
    }

    @After
    fun eliminarTodo() {
        neo4jData.eliminarTodo()
    }
}