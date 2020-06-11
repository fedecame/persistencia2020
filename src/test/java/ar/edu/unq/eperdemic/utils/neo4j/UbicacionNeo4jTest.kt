package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.springframework.data.jpa.domain.AbstractPersistable_.id

class UbicacionNeo4jTest {
    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())

       var vector = Vector()
    var ubicacionPlantalandia = Ubicacion()
    var ubicacionBichoLandia = Ubicacion()
    var ubicacionTibetDojo = Ubicacion()
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
    var id: Int = 0
    var daoNeo4j = Neo4jUbicacionDAO()
    @Before
    fun setUp() {
        ubicacionService.crearUbicacion("BichoLandia")
        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
        vector.ubicacion = ubicacionService.crearUbicacion("Plantalandia")
        vector.tipo = Humano()
        vector.estado = Sano()
        id = vectorService.crearVector(vector)?.id?.toInt()!!
    }


    @Test
    fun vectorQuiereMoverAUbicacionNoAledaña() {


          //  daoNeo4j.conectar("Plantalandia", "TibetDojo", "Terrestre")
            ubicacionService.mover(id, "TibetDojo")


    }

    @Test
    fun vectorNoPuedeMoverPorqueUbicacionEsLejana() {

              ubicacionService.conectar("Plantalandia", "FlorencioVarela", "Maritimo")
            ubicacionService.mover(id,"Bicholandia")


    }
}