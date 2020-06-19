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
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            Neo4jDataDAO().clear()
        }

        dataService = Neo4jDataService()
        dataService.crearSetDeDatosIniciales()
        val ubicacionDao = HibernateUbicacionDAO()
        val vectorDao = HibernateVectorDAO()
        sut = UbicacionServiceImpl(ubicacionDao)
        vectorService = VectorServiceImpl(vectorDao, ubicacionDao)
        hibernatUbicacionService = UbicacionServiceImpl(ubicacionDao)

//        narnia = hibernatUbicacionService.crearUbicacion( "Narnia")

        //No llega a ningun lado. A el llegan por Aereo
        narnia = hibernatUbicacionService.recuperarUbicacion("Narnia")

        quilmes = hibernatUbicacionService.recuperarUbicacion("Quilmes")
        mordor = hibernatUbicacionService.recuperarUbicacion("Mordor")
        elNodoSolitario = hibernatUbicacionService.recuperarUbicacion("elNodoSolitario")
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

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion1AnteUnCaminoDeTipoMaritimoConMovimientosnmayoresA0(){
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Maritimo")
        val vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorAnimal.ubicacion = origen
        vectorService.crearVector(vectorAnimal)
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 1))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 2))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion1AnteUnCaminoDeTipoTerrestreConMovimientosnmayoresA0(){
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Terrestre")
        val vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorAnimal.ubicacion = origen
        vectorService.crearVector(vectorAnimal)
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 1))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 2))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion1AnteUnCaminoDeTipoAereoConMovimientosnmayoresA0(){
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Aereo")
        val vectorAnimal = Vector()
        vectorAnimal.tipo = Animal()
        vectorAnimal.ubicacion = origen
        vectorService.crearVector(vectorAnimal)
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 1))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 2))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorInsectoTieneCapacidadDeExpansion1AnteUnCaminoDeTipoAereoConMovimientosnmayoresA0(){
        this.eliminarTodo()
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Aereo")
        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = origen
        vectorService.crearVector(vectorInsecto)
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorInsecto.id!!, 1))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorInsecto.id!!, 2))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorInsecto.id!!, 42))
    }

    @Test
    fun unVectorInsectoTieneCapacidadDeExpansion1AnteUnCaminoDeTipoTerrestreConMovimientosnmayoresA0(){
        this.eliminarTodo()
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Terrestre")
        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = origen
        vectorService.crearVector(vectorInsecto)
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorInsecto.id!!, 1))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorInsecto.id!!, 2))
        Assert.assertEquals(1, sut.capacidadDeExpansion(vectorInsecto.id!!, 42))
    }

    @Test
    fun unVectorInsectoTieneCapacidadDeExpansion0AnteUnCaminoDeTipoMaritimoSinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        vectorInsecto.ubicacion = origen
        vectorService.crearVector(vectorInsecto)
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Maritimo")
        Assert.assertEquals(0, sut.capacidadDeExpansion(vectorInsecto.id!!, 1))
        Assert.assertEquals(0, sut.capacidadDeExpansion(vectorInsecto.id!!, 2))
        Assert.assertEquals(0, sut.capacidadDeExpansion(vectorInsecto.id!!, 42))
    }

    @Test
    fun unVectorHumanoTieneCapacidadDeExpansion0AnteUnCaminoDeTipoAereoSinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val otroVectorHumano = Vector()
        otroVectorHumano.tipo = Humano()
        val origen = sut.crearUbicacion("Origen")
        val destino = sut.crearUbicacion("Destino")
        otroVectorHumano.ubicacion = origen
        vectorService.crearVector(otroVectorHumano)
        sut.conectar(origen.nombreUbicacion, destino.nombreUbicacion, "Aereo ")
        Assert.assertEquals(0, sut.capacidadDeExpansion(otroVectorHumano.id!!, 1))
        Assert.assertEquals(0, sut.capacidadDeExpansion(otroVectorHumano.id!!, 2))
        Assert.assertEquals(0, sut.capacidadDeExpansion(otroVectorHumano.id!!, 42))
    }

    @Test
    fun unVectorHumanoTieneCapacidadDeExpansion2AnteUnCaminoDeTipoTerrestreYAnteUnCaminoDeTipoMaritimoSinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val otroVectorHumano = Vector()
        otroVectorHumano.tipo = Humano()
        val origen = sut.crearUbicacion("Origen")
        val destinoATierra = sut.crearUbicacion("DestinoTierra")
        val destinoAMar = sut.crearUbicacion("DestinoMaritimo")
        otroVectorHumano.ubicacion = origen
        vectorService.crearVector(otroVectorHumano)
        sut.conectar(origen.nombreUbicacion, destinoATierra.nombreUbicacion, "Terrestre")
        sut.conectar(origen.nombreUbicacion, destinoAMar.nombreUbicacion, "Maritimo")
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorHumano.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorHumano.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorHumano.id!!, 42))
    }

    @Test
    fun unVectorInsectoTieneCapacidadDeExpansion2AnteUnCaminoDeTipoAereoYAnteUnCaminoDeTipoTerrestreSinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val otroVectorInsecto = Vector()
        otroVectorInsecto.tipo = Insecto()
        val origen = sut.crearUbicacion("Origen")
        val destinoATierra = sut.crearUbicacion("DestinoTierra")
        val destinoAereo = sut.crearUbicacion("DestinoAereo")
        otroVectorInsecto.ubicacion = origen
        vectorService.crearVector(otroVectorInsecto)
        sut.conectar(origen.nombreUbicacion, destinoATierra.nombreUbicacion, "Terrestre")
        sut.conectar(origen.nombreUbicacion, destinoAereo.nombreUbicacion, "Aereo")
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorInsecto.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorInsecto.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorInsecto.id!!, 42))
    }

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion2AnteUnCaminoDeTipoAereoYAnteUnCaminoDeTipoTerrestreSinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val otroVectorAnimal = Vector()
        otroVectorAnimal.tipo = Animal()
        val origen = sut.crearUbicacion("Origen")
        val destinoATierra = sut.crearUbicacion("DestinoTierra")
        val destinoAereo = sut.crearUbicacion("DestinoAereo")
        otroVectorAnimal.ubicacion = origen
        vectorService.crearVector(otroVectorAnimal)
        sut.conectar(origen.nombreUbicacion, destinoATierra.nombreUbicacion, "Terrestre")
        sut.conectar(origen.nombreUbicacion, destinoAereo.nombreUbicacion, "Aereo")
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion2AnteUnCaminoDeTipoAereoYAnteUnCaminoDeTipoMaritimoSinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val otroVectorAnimal = Vector()
        otroVectorAnimal.tipo = Animal()
        val origen = sut.crearUbicacion("Origen")
        val destinoAMar = sut.crearUbicacion("DestinoAMar")
        val destinoAereo = sut.crearUbicacion("DestinoAereo")
        otroVectorAnimal.ubicacion = origen
        vectorService.crearVector(otroVectorAnimal)
        sut.conectar(origen.nombreUbicacion, destinoAMar.nombreUbicacion, "Maritimo")
        sut.conectar(origen.nombreUbicacion, destinoAereo.nombreUbicacion, "Aereo")
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion2AnteUnCaminoDeTipoTerrestreYAnteUnCaminoDeTipoMaritimoinImportarLaCantidadDeMovimients(){
        this.eliminarTodo()
        val otroVectorAnimal = Vector()
        otroVectorAnimal.tipo = Animal()
        val origen = sut.crearUbicacion("Origen")
        val destinoAMar = sut.crearUbicacion("DestinoAMar")
        val destinoATierra = sut.crearUbicacion("DestinoATierra")
        otroVectorAnimal.ubicacion = origen
        vectorService.crearVector(otroVectorAnimal)
        sut.conectar(origen.nombreUbicacion, destinoAMar.nombreUbicacion, "Maritimo")
        sut.conectar(origen.nombreUbicacion, destinoATierra.nombreUbicacion, "Aereo")
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorAnimalTieneCapacidadDeExpansion3AnteLos3TiposDeCaminos(){
        this.eliminarTodo()
        val otroVectorAnimal = Vector()
        otroVectorAnimal.tipo = Animal()
        val origen = sut.crearUbicacion("Origen")
        val destinoAMar = sut.crearUbicacion("DestinoAMar")
        val destinoAereo = sut.crearUbicacion("DestinoAereo")
        val destinoATierra = sut.crearUbicacion("DestinoATierra")
        otroVectorAnimal.ubicacion = origen
        vectorService.crearVector(otroVectorAnimal)
        sut.conectar(origen.nombreUbicacion, destinoAMar.nombreUbicacion, "Maritimo")
        sut.conectar(origen.nombreUbicacion, destinoATierra.nombreUbicacion, "Terrestre")
        sut.conectar(origen.nombreUbicacion, destinoAereo.nombreUbicacion, "Aereo")
        Assert.assertEquals(3, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 1))
        Assert.assertEquals(3, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 2))
        Assert.assertEquals(3, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 42))
    }

    @Test
    fun unVectorNoPasa2VecesPorElMismoNodoConRelacionesBidireccionalesIndependientementeDeSuTipoIndependientementeDeLaCantidadDemovimientos(){
        this.eliminarTodo()
        val otroVectorInsecto = Vector()
        otroVectorInsecto.tipo = Insecto()

        val otroVectorAnimal = Vector()
        otroVectorAnimal.tipo = Animal()

        val otroVectorHumano = Vector()
        otroVectorHumano.tipo = Humano()

        val origen = sut.crearUbicacion("Origen")
        val destinoAMar = sut.crearUbicacion("DestinoAMar")
        val destinoATierra = sut.crearUbicacion("DestinoATierra")
        val destinoAereo = sut.crearUbicacion("DestinoAereo")
        otroVectorAnimal.ubicacion = origen
        otroVectorHumano.ubicacion = origen
        otroVectorInsecto.ubicacion = origen
        vectorService.crearVector(otroVectorAnimal)
        vectorService.crearVector(otroVectorHumano)
        vectorService.crearVector(otroVectorInsecto)

        sut.conectar(origen.nombreUbicacion, destinoAMar.nombreUbicacion, "Maritimo")
        sut.conectar(destinoAMar.nombreUbicacion,origen.nombreUbicacion, "Maritimo")

        sut.conectar(origen.nombreUbicacion, destinoATierra.nombreUbicacion, "Terrestre")
        sut.conectar(destinoATierra.nombreUbicacion, origen.nombreUbicacion, "Terrestre")

        sut.conectar(origen.nombreUbicacion, destinoAereo.nombreUbicacion, "Aereo")
        sut.conectar(destinoAereo.nombreUbicacion, origen.nombreUbicacion, "Aereo")

        Assert.assertEquals(3, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorHumano.id!!, 1))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorInsecto.id!!, 1))

        Assert.assertEquals(3, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorHumano.id!!, 2))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorInsecto.id!!, 2))

        Assert.assertEquals(3, sut.capacidadDeExpansion(otroVectorAnimal.id!!, 42))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorHumano.id!!, 42))
        Assert.assertEquals(2, sut.capacidadDeExpansion(otroVectorInsecto.id!!, 42))
    }


    @After
    fun eliminarTodo() {
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            Neo4jDataDAO().clear()
        }
    }
}
