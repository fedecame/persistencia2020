package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.modelo.exception.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

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
        neo4jData.crearSetDeDatosIniciales()
    }

    @Test
    fun vectorQuiereMoverAUbicacionNoAledaña() {
        var daoNeo4j = Neo4jUbicacionDAO()
        TransactionRunner.addNeo4j().runTrx {
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


    }

    @Test(expected = NullPointerException::class)
    fun unVectorNoPersistidoIntentaMoverMasCorto() {
        val quilmes = ubicacionService.recuperarUbicacion("Quilmes")
        val vectorsito = Vector()
        ubicacionService.moverMasCorto(vectorsito.id!!, quilmes.nombreUbicacion)
    }

    @Test(expected = NoExisteUbicacion::class)
    fun intentaMoverMasCortoAUnaUbicacionNoPersistida() {
        val fakeLocation = Ubicacion()
        ubicacionService.moverMasCorto(vector.id!!, fakeLocation.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun intentaMoverMasCortoPeroNoHayCaminosQueLleguenAEsaUbicacion() {
        val quilmes = ubicacionService.recuperarUbicacion("Quilmes")
        val nonePlace = ubicacionService.recuperarUbicacion("NonePlace")
        vector.ubicacion = quilmes
        quilmes.vectores.add(vector)
        HibernateUbicacionDAO().actualizar(quilmes)
        ubicacionService.moverMasCorto(vector.id!!, nonePlace.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun unVectorHumanoIntentaMoverMasCortoPeroNoHayCaminosDeSusTiposViables() {
        val mordor = ubicacionService.recuperarUbicacion("Mordor")
        val zion = ubicacionService.recuperarUbicacion("Zion")
        vector.ubicacion = mordor
        mordor.vectores.add(vector)
        HibernateUbicacionDAO().actualizar(mordor)
        ubicacionService.moverMasCorto(vector.id!!, zion.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun unVectorInsectoIntentaMoverMasCortoPeroNoHayCaminosDeSusTiposViables() {
        val zion = ubicacionService.recuperarUbicacion("Zion")
        val ezpeleta = ubicacionService.recuperarUbicacion("Ezpeleta")
        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = zion
        zion.vectores.add(vectorInsecto)
        vectorService.crearVector(vectorInsecto)
        HibernateUbicacionDAO().actualizar(zion)
        ubicacionService.moverMasCorto(vectorInsecto.id!!, ezpeleta.nombreUbicacion)
    }

    @Test
    fun unVectorHumanoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosible() {
        /**
         *  TODO Fede
         * */
    }

    @Test
    fun unVectorInsectoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosible() {
        /**
         *  TODO Fede
         * */
    }

    @Test
    fun unVectorAnimalIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosible() {
        /**
         *  TODO Fede
         * */
    }

    @After
    fun eliminarTodo() {
        neo4jData.eliminarTodo()
    }
}