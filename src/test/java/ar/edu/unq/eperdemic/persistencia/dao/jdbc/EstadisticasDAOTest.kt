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
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
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

    @Before
    fun setUp(){
        estadisticasDAO = HibernateEstadisticasDAO()
        dataDAO = HibernateDataDAO()
        vector = Vector()
        dataDAO = HibernateDataDAO()
        ubicacionDAO = HibernateUbicacionDAO()
        vectorDAO = HibernateVectorDAO()
        vectorService = VectorServiceImpl(vectorDAO, dataDAO, ubicacionDAO)
        ubicacionService = UbicacionServiceImpl(ubicacionDAO, dataDAO)
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
        vectorService = VectorServiceImpl(HibernateVectorDAO(), dataDAO, HibernateUbicacionDAO())
        vector.tipo = tipo
        vector.estado = estado
        vector.agregarEspecie(especie)

        ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO(), dataDAO)
        ubicacion0 = ubicacionService.crearUbicacion("Quilmes")
        ubicacion1 = ubicacionService.crearUbicacion("Mar del Plata")
        vector.ubicacion = ubicacion0
        vectorService.crearVector(vector)
        ubicacionService.mover(vector.id!!.toInt(), ubicacion0.nombreUbicacion)
    }

    private fun crearNConEstadoEn(cant : Int, estado : EstadoVector, ubicacion : String){
        repeat(cant){
            var vectorInfectado = Vector()
            vectorInfectado.tipo = tipo
            vectorInfectado.estado = estado
            vectorInfectado.ubicacion = ubicacionService.recuperarUbicacion(ubicacion)
            vectorInfectado.agregarEspecie(especie)
            vectorService.crearVector(vectorInfectado)
            ubicacionService.mover(vectorInfectado.id!!.toInt(), ubicacion)
        }
    }

    @Test
    fun elEstadisticasDAODevuelve0CuandoNoHayNingunVectorEnEsaUbicacion(){
        var res = 0
        runTrx {
            res = estadisticasDAO.vectoresPresentes("Mar del Plata")
        }
        Assert.assertEquals(0, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorEnEsaUbicacion(){
        var res = 0
        runTrx {
            res = estadisticasDAO.vectoresPresentes("Quilmes")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve2CuandoHayDosVectoresEnEsaUbicacion(){
        var res = 0
        this.crearNConEstadoEn(1, Infectado(), "Quilmes") //Uno ya habia
        ubicacionService.mover(vector.id!!.toInt(), ubicacion0.nombreUbicacion)
        runTrx {
            res = estadisticasDAO.vectoresPresentes("Quilmes")
        }
        Assert.assertEquals(2, res)
    }
    @Test
    fun elEstadisticasDAODevuelve0CuandoNoHayNingunVectorInfectadoEnEsaUbicacion(){
        var res = 0
        runTrx {
            res = estadisticasDAO.vectoresInfectados("Mar del Plata")
        }
        Assert.assertEquals(0, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorInfectadoEnEsaUbicacionMDP(){
        var res = 0
        this.crearNConEstadoEn(1, Infectado(),"Mar del Plata")
        runTrx {
            res = estadisticasDAO.vectoresInfectados("Mar del Plata")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorInfectadoEnMarDelPlataCuandoHayOtrosVectoresSanos(){
        var res = 0
        this.crearNConEstadoEn(1, Infectado(), "Mar del Plata")
        this.crearNConEstadoEn(5, Sano(), "Mar del Plata")
        runTrx {
            res = estadisticasDAO.vectoresInfectados("Mar del Plata")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve1CuandoHayUnVectorInfectadoEnEsaUbicacion(){
        var res = 0
        this.crearNConEstadoEn(1, Infectado(), "Quilmes")
        runTrx {
            res = estadisticasDAO.vectoresInfectados("Quilmes")
        }
        Assert.assertEquals(1, res)
    }

    @Test
    fun elEstadisticasDAODevuelve2CuandoHayDosVectoresInfectadosEnEsaUbicacion(){
        var res = 0
        this.crearNConEstadoEn(2, Infectado(),"Quilmes")
        runTrx {
            res = estadisticasDAO.vectoresInfectados("Quilmes")
        }
        Assert.assertEquals(2, res)
    }

    @Test
    fun  laEspecieMasInfecciosaEsLaUnicaEspecieQueHayEnQuilmesYEsAlgo(){
        var res = ""
        runTrx {
            res = estadisticasDAO.especieQueInfectaAMasVectoresEn("Quilmes")
        }
        Assert.assertEquals("Algo", res)
    }

    @Test(expected= NoResultException::class)
    fun  elNombreDeLaEspecieMasInfecciosaArrojaUnaExcepcionCuandoNoQueHayNingunaEspecieEnLaUbicacion(){
        runTrx {
            estadisticasDAO.especieQueInfectaAMasVectoresEn("Mar del Plata")
        }
    }

    @Test(expected= NoResultException::class)
    fun  elEstadisticasDAOArrojaUnaExcepcionCuandoLaUbicacionNoExiste(){
        runTrx {
            estadisticasDAO.especieQueInfectaAMasVectoresEn("The twilight zone")
        }
    }

    @Test
    fun enUnaUbicacionConMasDeUnaEspecieElNombreDeLaEspecieMasInfecciosaEsLaQueInfectaAMasVectores(){
        val paperas = Especie()
        val ubicacionFinal = ubicacionService.crearUbicacion("Maeame")
        paperas.paisDeOrigen = "Rusia"
        paperas.nombre = "Paperas"
        val ubi = "Maeame"
        val vectorRandom = Vector()
        val vectorAlfa = Vector()
        val vectorBeta = Vector()
        vectorRandom.tipo = Humano()
        vectorAlfa.tipo = Insecto()
        vectorBeta.tipo = Animal()
        vectorRandom.ubicacion = ubicacionFinal
        vectorRandom.infectarse(paperas)
        vectorAlfa.infectarse(paperas)
        vectorBeta.infectarse(paperas)
        vectorAlfa.ubicacion = ubicacionFinal
        vectorBeta.ubicacion= ubicacionFinal
        vectorService.crearVector(vectorAlfa)
        vectorService.crearVector(vectorBeta)
        vectorService.crearVector(vectorRandom)
        ubicacionService.mover(vectorRandom.id!!.toInt(),ubi)
        ubicacionService.mover(vectorAlfa.id!!.toInt(), ubi)
        ubicacionService.mover(vectorBeta.id!!.toInt(),ubi)
        var res = ""
        runTrx {
            res = estadisticasDAO.especieQueInfectaAMasVectoresEn("Maeame")
        }
        Assert.assertEquals("Paperas", res)
    }


    @After
    fun eliminarTodo(){
        TransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}