package ar.edu.unq.eperdemic.utils.neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class capacidadDeExpancionTest {
    private lateinit var sut : UbicacionService
    private lateinit var dataService : DataService
    private lateinit var vectorService : VectorService
    private lateinit var hibernatUbicacionService : UbicacionService
    private lateinit var vectorAnimal : Vector
    private lateinit var vectorHumano : Vector
    private lateinit var vectorInsectoA : Vector
    private lateinit var vectorInsectoB : Vector
    private lateinit var vectores : List<Vector>
    private lateinit var quilmes : Ubicacion
    private lateinit var mordor : Ubicacion
    private lateinit var narnia : Ubicacion
    private lateinit var elNodoSolitario : Ubicacion

    @Before
    fun setUp(){
        //Aca elimino lo que haya
//        this.eliminarTodo()
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            Neo4jDataDAO().clear()
        }
        //
        val ubicacionDao = HibernateUbicacionDAO()
        val vectorDao = HibernateVectorDAO()
        sut = UbicacionServiceImpl(ubicacionDao)
        dataService = Neo4jDataService()
        vectorService = VectorServiceImpl(vectorDao, ubicacionDao)
        hibernatUbicacionService = UbicacionServiceImpl(ubicacionDao)

        //No llega a ningun lado. A el llegan por Aereo
        narnia = hibernatUbicacionService.crearUbicacion( "Narnia")

        val testNarnia =  hibernatUbicacionService.recuperarUbicacion("Narnia")
//        if (testNarnia.nombreUbicacion == "Narnia") {
//            print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ VAMOS LOS PIBES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//        } else {
//            print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ASDASDASDASDDASDSD ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//        }

        quilmes = hibernatUbicacionService.crearUbicacion("Quilmes")
        mordor = hibernatUbicacionService.crearUbicacion("Mordor")
        elNodoSolitario = hibernatUbicacionService.crearUbicacion("elNodoSolitario")
        vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorAnimal.ubicacion = quilmes
        vectorHumano = Vector()
        vectorHumano.ubicacion = narnia
        vectorHumano.tipo = Humano()
        vectorInsectoA = Vector()
        vectorInsectoA.tipo = Insecto()
        vectorInsectoA.ubicacion = mordor
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
        val capacidad = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoB.id!!, 42)
        Assert.assertEquals(0, capacidad)
    }

    @Test
    fun laCapacidadDeExpansionDeElNodoSolitarioSiempreEs0IndependientementeDelNroDeMovimientos(){
        val capacidad0 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoB.id!!, 0)
        Assert.assertEquals(0, capacidad0)
        val capacidad1 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoB.id!!, 1)
        Assert.assertEquals(0, capacidad1)
        val capacidad2 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoB.id!!, 2)
        Assert.assertEquals(0, capacidad2)
        val capacidad3 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoB.id!!, 3)
        Assert.assertEquals(0, capacidad2)
        val capacidad4 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoB.id!!, 4)
        Assert.assertEquals(0, capacidad2)
    }

    @Test
    fun desdeMordorQueSoloSeConectaPorCaminosMaritimosUnVectorInsectoTieneCapacidadDeExpancion2(){
        val capacidad0 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoA.id!!, 3)
        Assert.assertEquals(4, capacidad0)
    }

    @Test
    fun desdeQuilmesQueSoloSeConectaPorCaminosAereosUnVectorInsectoTieneCapacidadDeExpancion1IndependientementeDeLaCantidadDeMovimientos(){
        vectorInsectoA.ubicacion = quilmes
        quilmes.vectores.add(vectorInsectoA)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(quilmes)
        }

        val capacidad0 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoA.id!!, 0)
        Assert.assertEquals(0, capacidad0)
        val capacidad1 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoA.id!!, 1)
        Assert.assertEquals(2, capacidad1)
        val capacidad2 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoA.id!!, 2)
        Assert.assertEquals(2, capacidad2)
        val capacidad3 = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoA.id!!, 3)
        Assert.assertEquals(2, capacidad3)
    }

    @Test
    fun desdeQuilmesQueSeComunicaSoloViaAereaUNHumanoTieneCapacidadDeExpancion0(){
        val otroVectorHumano = Vector()
        otroVectorHumano.tipo = Humano()
        otroVectorHumano.ubicacion = quilmes
        vectorService.crearVector(otroVectorHumano)
        val capacidad = hibernatUbicacionService.capacidadDeExpansion(otroVectorHumano.id!!, 1)
        Assert.assertEquals(0, capacidad)
    }

    @Test
    fun desdeQuilmesQueSeComunicaSoloViaAereaUnAnimalTieneCapacidadDeExpancion2(){
        val otroVectorHumano = Vector()
        otroVectorHumano.tipo = Animal()
        otroVectorHumano.ubicacion = quilmes
        vectorService.crearVector(otroVectorHumano)
        val capacidad = hibernatUbicacionService.capacidadDeExpansion(vectorInsectoA.id!!, 1)
        Assert.assertEquals(2, capacidad)
    }

    @Test
    fun desdeQuilmesQueSeComunicaSoloViaAereaUnInsectoTieneCapacidadDeExpancion2(){
        val otroVectorInsecto = Vector()
        otroVectorInsecto.tipo = Insecto()
        otroVectorInsecto.ubicacion = quilmes
        vectorService.crearVector(otroVectorInsecto)
        val capacidad = hibernatUbicacionService.capacidadDeExpansion(otroVectorInsecto.id!!, 1)
        Assert.assertEquals(2, capacidad)
    }

    @Test
    fun desdeNarniaQueSeComunicaPorViaTerrestreyMaritimaUnAnimalTiene2PosiblesLocacionesCon1Movimiento(){
        val otroVectorInsecto = Vector()
        otroVectorInsecto.tipo = Insecto()
        otroVectorInsecto.ubicacion = narnia
        vectorService.crearVector(otroVectorInsecto)
        val capacidad0 = hibernatUbicacionService.capacidadDeExpansion(otroVectorInsecto.id!!, 1)
        Assert.assertEquals(1, capacidad0)
    }

    @Test
    fun desdeNarniaQueSeComunicaPorViaTerrestreyMaritimaUnInsectoTiene2PosiblesLocaciones(){
        val otroVectorInsecto = Vector()
        otroVectorInsecto.tipo = Insecto()
        otroVectorInsecto.ubicacion = narnia
        vectorService.crearVector(otroVectorInsecto)
        val capacidad0 = hibernatUbicacionService.capacidadDeExpansion(otroVectorInsecto.id!!, 1)
        Assert.assertEquals(1, capacidad0)
    }


    @After
    fun eliminarTodo() {
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            Neo4jDataDAO().clear()
        }
    }
}
