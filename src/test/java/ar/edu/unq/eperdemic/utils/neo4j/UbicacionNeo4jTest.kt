package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.modelo.exception.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UbicacionNeo4jTest {
    lateinit var neo4jData: Neo4jDataService
    lateinit var hibernateDataService: HibernateDataService

    var ubicacionService=UbicacionServiceImpl(HibernateUbicacionDAO())
    var ubicacionTibetDojo=Ubicacion()
    var vectorService=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO())
    var vector= Vector()
    var vectorAnimal=Vector()
    @Before
    fun setUp() {
        neo4jData = Neo4jDataService()
        hibernateDataService = HibernateDataService()
        neo4jData.crearSetDeDatosIniciales()
        ubicacionService.crearUbicacion("BichoLandia")
        ubicacionService.crearUbicacion("Florencio Varela")
        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
        val mdq = ubicacionService.crearUbicacion("Mar Del Plata")
        vector.ubicacion = mdq
        vectorAnimal.ubicacion = mdq
        vector.tipo = Humano()
        vectorAnimal.tipo = Animal()
        vector.estado = Sano()
        vectorAnimal.estado = Sano()
        vectorService.crearVector(vector)
        vectorService.crearVector(vectorAnimal)
    }

//    @Test
//    fun vectorQuiereMoverAUbicacionNoAledaña() {
//        var daoNeo4j = Neo4jUbicacionDAO()
//        TransactionRunner.addNeo4j().runTrx {
//            daoNeo4j.conectar("Plantalandia", "TibetDojo", "Terrestre")
//        }
//        ubicacionService.crearUbicacion("BichoLandia")
//        ubicacionService.crearUbicacion("Florencio Varela")
//
//        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
//        vector.ubicacion = ubicacionService.crearUbicacion("Plantalandia")
//        vector.tipo = Humano()
//        vector.estado = Sano()
//        var id = vectorService.crearVector(vector).id
//        if (id != null) {
//            ubicacionService.mover(id.toInt(), "TibetDojo")
//        }

    @Test
    fun vectorMueveAUbicacionAledania() {
        ubicacionService.conectar("Mar Del Plata","Quilmes","Terrestre")
        ubicacionService.mover(vector.id?.toInt()!!,"Quilmes")

        Assert.assertEquals("Quilmes", vectorService.recuperarVector(vector.id!!.toInt()).ubicacion!!.nombreUbicacion)
    }

    @Test(expected = UbicacionMuyLejana::class)
    fun vectorNoPuedeMoverPorqueUbicacionEsLejana() {
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")
        vector.ubicacion = babilonia
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(babilonia)
        }
        ubicacionService.mover(vector.id!!.toInt(),"Narnia")
    }

    @Test(expected = NoExisteUbicacion::class)
    fun vectorNoPuedeMoverAUnaUbicacionQueNoEstaPersistida() {
        ubicacionService.mover(vector.id!!.toInt(),"")
    }

    @Test(expected = CaminoNoSoportado::class)
    fun vectorHumanoNoPuedeMoversePorCaminoAereoNoSoportado() {
        ubicacionService.conectar("Mar Del Plata","Florencio Varela", "Aereo")
        ubicacionService.mover(vector.id!!.toInt(), "Florencio Varela")
    }

    @Test
    fun vectorAnimalMuevePorCaminoMaritimo(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        vectorAnimal.ubicacion = jamaica
        jamaica.vectores.add(vectorAnimal)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(jamaica)
        }
        ubicacionService.conectar("Jamaica", "Florencio Varela","Maritimo")
        ubicacionService.mover(vectorAnimal.id!!.toInt(),"Florencio Varela")
        val vectorAnimalActualizado = vectorService.recuperarVector(vectorAnimal.id!!.toInt())

        val jamaicaActualizada = ubicacionService.recuperarUbicacion("Jamaica")
        Assert.assertTrue(jamaicaActualizada.vectores.isEmpty())
        Assert.assertEquals("Florencio Varela", vectorAnimalActualizado.ubicacion!!.nombreUbicacion)
        val florencioVarelaActualizado = ubicacionService.recuperarUbicacion("Florencio Varela")
        Assert.assertEquals(1, florencioVarelaActualizado.vectores.size)
        Assert.assertNotNull(florencioVarelaActualizado.vectores.find { it.id!! == vectorAnimal.id!! })
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
//        ubicacionService.moverMasCorto(vector.id!!, mordor.nombreUbicacion)
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
//        ubicacionService.moverMasCorto(vectorInsecto.id!!, babilonia.nombreUbicacion)
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
        //Assert.assertEquals("Mordor", vectorAnimal.ubicacion!!.nombreUbicacion)
        //Assert.assertNotNull(vectorAnimal.id)
//        ubicacionService.moverMasCorto(vectorAnimal.id!!, babilonia.nombreUbicacion)
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
