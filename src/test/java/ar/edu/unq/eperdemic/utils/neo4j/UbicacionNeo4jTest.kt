package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.Test
import org.mockito.Mockito.mock

class UbicacionNeo4jTest {
    var ubicacionService=UbicacionServiceImpl(HibernateUbicacionDAO())
    var mockUbicacionService = mock(UbicacionServiceImpl(HibernateUbicacionDAO()).javaClass)
    var mockVectorService=mock(VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO()).javaClass)
    var vector= Vector()
    var ubicacionPlantalandia= Ubicacion()
    var ubicacionBichoLandia=Ubicacion()
    var ubicacionTibetDojo=Ubicacion()
    var vectorService=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO())

    @Test
    fun vectorQuiereMoverAUbicacionNoAledaña() {
        var daoNeo4j = Neo4jUbicacionDAO()
        runTrx {
            daoNeo4j.conectar("Plantalandia", "TibetDojo", "Terrestre")
        }
        ubicacionService.crearUbicacion("BichoLandia")

        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
        vector.ubicacion = ubicacionService.crearUbicacion("Plantalandia")
        vector.tipo = Humano()
        vector.estado = Sano()
        var id = vectorService.crearVector(vector).id
        if (id != null) {
            ubicacionService.mover(id.toInt(), "TibetDojo")
        }


    }}