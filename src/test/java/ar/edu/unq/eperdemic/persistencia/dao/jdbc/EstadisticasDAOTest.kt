package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.persistence.NoResultException

class EstadisticasDAOTest {
    lateinit var estadisticasDAO : EstadisticasDAO
    lateinit var vectorService : VectorService
    lateinit var ubicacionService : UbicacionService
    lateinit var vector : Vector
    lateinit var tipo : TipoVector
    lateinit var estado : EstadoVector
    lateinit var especie : Especie
    lateinit var dataDAO : DataDAO
    lateinit var ubicacionDAO : UbicacionDAO
    lateinit var vectorDAO : VectorDAO
    lateinit var ubicacion0 : Ubicacion
    lateinit var ubicacion1 : Ubicacion
    lateinit var ubicacion2 : Ubicacion
    lateinit var patogeno : Patogeno
    var dataDaoNeo4j=Neo4jDataService()
    @Before
    fun setUp(){
        this.eliminarTodo()
        estadisticasDAO = HibernateEstadisticasDAO()
        dataDAO = HibernateDataDAO()
        vector = Vector()
        dataDAO = HibernateDataDAO()
        ubicacionDAO = HibernateUbicacionDAO()
        vectorDAO = HibernateVectorDAO()
        vectorService = VectorServiceImpl(vectorDAO, ubicacionDAO)
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        ubicacion2 = ubicacionService.crearUbicacion("Alemania")
        tipo = Humano()
        estado = Sano()
        especie = Especie()
        especie.cantidadInfectadosParaADN = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        patogeno = Patogeno()
        patogeno.tipo = ""
        especie.patogeno = patogeno
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
        vector.tipo = tipo
        vector.estado = estado
        vector.agregarEspecie(especie)

        ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())
        ubicacion0 = ubicacionService.crearUbicacion("Quilmes")
        ubicacion1 = ubicacionService.crearUbicacion("Mar del Plata")
        vector.ubicacion = ubicacion0
        vectorService.crearVector(vector)
        //dataDaoNeo4j.datosParaEstadisticaService()
        ubicacionService.conectar(ubicacion1.nombreUbicacion,ubicacion0.nombreUbicacion,"Terrestre")
    }

    private fun crearNConEstadoEn(cant : Int, estado : EstadoVector, ubicacion : String){
        val ubicacionModelo = ubicacionService.recuperarUbicacion(ubicacion)
        repeat(cant){
            var vectorHumano = Vector()
            vectorHumano.tipo = tipo
            vectorHumano.ubicacion = ubicacionModelo
            val nuevaEspecie = Especie()
            nuevaEspecie.nombre = "HibernateEsBasura"
            nuevaEspecie.cantidadInfectadosParaADN = 50
            nuevaEspecie.paisDeOrigen = "Jamaica"
            if (estado is Infectado) {
                vectorHumano.agregarEspecie(nuevaEspecie)
            }
            vectorService.crearVector(vectorHumano)
            ubicacionModelo.vectores.add(vectorHumano)
            TransactionRunner.addHibernate().addNeo4j().runTrx {
                ubicacionDAO.actualizar(ubicacionModelo)
            }
        }
    }

    @Test
    fun elEstadisticasDAODevuelve0CuandoNoHayNingunVectorEnEsaUbicacion(){
        var res = 0
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresPresentes("Mar del Plata")
        }
        Assert.assertEquals(0, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorEnEsaUbicacion(){
        var res = 0
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresPresentes("Quilmes")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve2CuandoHayDosVectoresEnEsaUbicacion(){
        var res = 0
        ubicacionService.crearUbicacion("Jamaica")
        this.crearNConEstadoEn(2, Infectado(), "Jamaica")
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresPresentes("Jamaica")
        }
        Assert.assertEquals(2, res)
    }
    @Test
    fun elEstadisticasDAODevuelve0CuandoNoHayNingunVectorInfectadoEnEsaUbicacion(){
        var res = 0
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresInfectados("Mar del Plata")
        }
        Assert.assertEquals(0, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorInfectadoEnEsaUbicacionMDP(){
        var res = 0
        this.crearNConEstadoEn(1, Infectado(),"Mar del Plata")
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresInfectados("Mar del Plata")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorInfectadoEnMarDelPlataCuandoHayOtrosVectoresSanos(){
        var res = 0
        this.crearNConEstadoEn(1, Infectado(), "Mar del Plata")
        this.crearNConEstadoEn(5, Sano(), "Mar del Plata")
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresInfectados("Mar del Plata")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorInfectadoEnEsaUbicacion(){
        var res = 0
        ubicacionService.crearUbicacion("Jamaica")
        this.crearNConEstadoEn(1, Infectado(), "Jamaica")
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresInfectados("Jamaica")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve2CuandoHayDosVectoresInfectadosEnEsaUbicacion(){
        var res = 0
        ubicacionService.crearUbicacion("Jamaica")
        this.crearNConEstadoEn(2, Infectado(),"Jamaica")
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.vectoresInfectados("Jamaica")
        }
        Assert.assertEquals(2, res)
    }

    @Test
    fun  laEspecieMasInfecciosaEsLaUnicaEspecieQueHayEnQuilmesYEsAlgo(){
        var res = ""
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.especieQueInfectaAMasVectoresEn("Quilmes")
        }
        Assert.assertEquals("Algo", res)
    }

    @Test(expected= NoResultException::class)
    fun  elNombreDeLaEspecieMasInfecciosaArrojaUnaExcepcionCuandoNoQueHayNingunaEspecieEnLaUbicacion(){
        TransactionRunner.addHibernate().runTrx {
            estadisticasDAO.especieQueInfectaAMasVectoresEn("Mar del Plata")
        }
    }

    @Test(expected= NoResultException::class)
    fun  elEstadisticasDAOArrojaUnaExcepcionCuandoLaUbicacionNoExiste(){
        TransactionRunner.addHibernate().runTrx {
            estadisticasDAO.especieQueInfectaAMasVectoresEn("The twilight zone")
        }
    }

    @Test
    fun enUnaUbicacionConMasDeUnaEspecieLaEspecieMasInfecciosaEsLaQueInfectaAMasVectores(){
        val ubicacionFinal = ubicacionService.crearUbicacion("Maeame")
        val paperas = Especie()
        val unaGripe = Especie()
        unaGripe.paisDeOrigen = "Jamaica"
        unaGripe.nombre = "GripeJ"
        paperas.paisDeOrigen = "Rusia"
        paperas.nombre = "Paperas"
        val ubi = "Maeame"
        val vectorRandom = Vector()
        val vectorAlfa = Vector()
        val vectorBeta = Vector()
        vectorRandom.tipo = Humano()
        vectorAlfa.tipo = Insecto()
        vectorBeta.tipo = Animal()
        vectorRandom.infectarse(paperas)
        vectorAlfa.infectarse(paperas)
        vectorBeta.infectarse(paperas)
        vectorBeta.infectarse(unaGripe)
        vectorRandom.ubicacion = ubicacionFinal
        vectorAlfa.ubicacion = ubicacionFinal
        vectorBeta.ubicacion= ubicacionFinal
        vectorService.crearVector(vectorRandom)
        vectorService.crearVector(vectorAlfa)
        vectorService.crearVector(vectorBeta)
        var res = ""
        TransactionRunner.addHibernate().runTrx {
            res = estadisticasDAO.especieQueInfectaAMasVectoresEn("Maeame")
        }
        Assert.assertEquals("Paperas", res)
    }



    @After
    fun eliminarTodo(){
        HibernateDataService().eliminarTodo()
        Neo4jDataService().eliminarTodo()
    }
}