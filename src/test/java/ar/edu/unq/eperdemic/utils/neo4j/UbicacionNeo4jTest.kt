package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.modelo.exception.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class UbicacionNeo4jTest {
    lateinit var neo4jData: Neo4jDataService
    lateinit var hibernateDataService: HibernateDataService

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
        vector.tipo = Humano()
        neo4jData = Neo4jDataService()
        hibernateDataService = HibernateDataService()
        neo4jData.eliminarTodo()
        hibernateDataService.eliminarTodo()
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
        fakeLocation.nombreUbicacion="CalleFalsa123"
        val narnia = ubicacionService.recuperarUbicacion("Narnia")
        vector.ubicacion = narnia
        vectorService.crearVector(vector)
        ubicacionService.moverMasCorto(vector.id!!, fakeLocation.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun intentaMoverMasCortoPeroNoHayCaminosQueLleguenAEsaUbicacion() {
        val quilmes = ubicacionService.recuperarUbicacion("Quilmes")
        val nonePlace = ubicacionService.recuperarUbicacion("elNodoSolitario")
        vector.ubicacion = quilmes
        quilmes.vectores.add(vector)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(quilmes)
        }
        ubicacionService.moverMasCorto(vector.id!!, nonePlace.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun unVectorHumanoIntentaMoverMasCortoPeroNoHayCaminosDeSusTiposViables() {
        val mordor = ubicacionService.recuperarUbicacion("Mordor")
        val zion = ubicacionService.recuperarUbicacion("Zion")
        vector.ubicacion = mordor
        mordor.vectores.add(vector)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(mordor)
        }
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
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(zion)
        }
        ubicacionService.moverMasCorto(vectorInsecto.id!!, ezpeleta.nombreUbicacion)
    }

    @Test
    fun unVectorHumanoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosible() {
        /**
         *  TODO Fede
         * */
        val zion = ubicacionService.recuperarUbicacion("Zion")
        val mordor = ubicacionService.recuperarUbicacion("Mordor")
        vector.ubicacion = zion
        zion.vectores.add(vector)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(zion)
        }
        ubicacionService.moverMasCorto(vector.id!!, mordor.nombreUbicacion)
        //crear un spy de lo que sepa que se movio el vector a la nueva ubicacion

        //de zion a mordor
        //la combinacion mas corta es: zion>babilonia>ezpeleta>mordor
    }

    @Test
    fun unVectorInsectoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosible() {
        /**
         *  TODO Fede
         * */
        val narnia = ubicacionService.recuperarUbicacion("Narnia")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")
        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = narnia
        narnia.vectores.add(vectorInsecto)
        val neo4jDataDao = Neo4jDataDAO()
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            HibernateUbicacionDAO().actualizar(narnia)
            neo4jDataDao.conectUni("Narnia", "Quilmes", "Aereo")
        }
        ubicacionService.moverMasCorto(vectorInsecto.id!!, babilonia.nombreUbicacion)
        //crear un spy de lo que sepa que se movio el vector a la nueva ubicacion

        //de narnia a babilonia
        //el camino mas corto seria: narnia>quilmes>babilonia
    }

    @Test
    fun unVectorAnimalIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosible() {
        /**
         *  TODO Fede
         * */
        val mordor = ubicacionService.recuperarUbicacion("Mordor")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")
        val vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorAnimal.ubicacion = mordor
        mordor.vectores.add(vectorAnimal)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(mordor)
        }
        ubicacionService.moverMasCorto(vectorAnimal.id!!, babilonia.nombreUbicacion)
        //crear un spy de lo que sepa que se movio el vector a la nueva ubicacion

        //de mordor a babilonia
        //tiene que hacer: mordor>zion>babilonia
    }

    @After
    fun eliminarTodo() {
        neo4jData.eliminarTodo()
        hibernateDataService.eliminarTodo()
    }
}