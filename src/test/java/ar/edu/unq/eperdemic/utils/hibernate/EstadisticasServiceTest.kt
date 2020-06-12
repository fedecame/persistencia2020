package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoHayEspecieQueInfectaronHumanos
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.EstadisticasServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import ar.edu.unq.eperdemic.utils.neo4j.UbicacionNeo4jTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EstadisticasServiceTest {
    lateinit var estadisticasDAO : EstadisticasDAO
    lateinit var estadisticasService : EstadisticasService
    lateinit var vectorService : VectorService
    lateinit var ubicacionService : UbicacionService
    lateinit var vector : Vector
    lateinit var vector2 : Vector
    lateinit var vector3 : Vector
    lateinit var vector4 : Vector

    lateinit var tipo : TipoVector
    lateinit var estado : EstadoVector
    lateinit var especie : Especie
    lateinit var especie2 : Especie
    lateinit var especie3 : Especie
    lateinit var dataDAO : DataDAO
    lateinit var ubicacionDAO : UbicacionDAO
    lateinit var vectorDAO : VectorDAO
    lateinit var ubicacion0 : Ubicacion
    lateinit var ubicacion1 : Ubicacion
    lateinit var ubicacion2 : Ubicacion
    lateinit var patogeno : Patogeno
    lateinit var hibernateData : HibernateDataService
    var dataDaoNeo4j=Neo4jDataService()

    @Before
    fun setUp(){
        var ubicacionDaoNeo4j=UbicacionNeo4jTest()
        hibernateData = HibernateDataService()
        estadisticasDAO = HibernateEstadisticasDAO()
        estadisticasService = EstadisticasServiceImpl(estadisticasDAO)
        dataDAO = HibernateDataDAO()
        vector = Vector()
        vector2 = Vector()
        vector3 = Vector()
        dataDAO = HibernateDataDAO()
        ubicacionDAO = HibernateUbicacionDAO()
        vectorDAO = HibernateVectorDAO()
        vectorService = VectorServiceImpl(vectorDAO, ubicacionDAO)
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        ubicacion2 = ubicacionService.crearUbicacion("Alemania")
        tipo = Humano()
        estado = Sano()
        especie = Especie()
        especie2 = Especie()
        especie3 = Especie()
        especie.cantidadInfectadosParaADN = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        especie2.cantidadInfectadosParaADN = 42
        especie2.nombre = "Algo2"
        especie2.paisDeOrigen = "Alemania"
        especie3.cantidadInfectadosParaADN = 42
        especie3.nombre = "Algo3"
        especie3.paisDeOrigen = "Alemania"
        patogeno = Patogeno()
        patogeno.tipo = ""
        especie.patogeno = patogeno
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
        vector.tipo = tipo
        vector.estado = estado

        vector.infectarse(especie)

        vector.agregarEspecie(especie)
        vector2.tipo = tipo
        vector2.estado = estado
        vector2.agregarEspecie(especie)
        vector2.agregarEspecie(especie3)
        vector3.agregarEspecie(especie)

        vector.agregarEspecie(especie3)
        vector3.tipo = tipo
        vector3.estado = estado
        vector3.agregarEspecie(especie2)


        ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())
        ubicacion0 = ubicacionService.crearUbicacion("Quilmes")
        ubicacion1 = ubicacionService.crearUbicacion("Mar Del Plata")
        ubicacion2 = ubicacionService.crearUbicacion("Berazategui")
        vector.ubicacion = ubicacion1
        vector2.ubicacion = ubicacion1
        vector3.ubicacion = ubicacion1

        ubicacion1.vectores.add(vector)
        ubicacion1.vectores.add(vector2)
        ubicacion1.vectores.add(vector3)

        vectorService.crearVector(vector)
        vectorService.crearVector(vector2)
        vectorService.crearVector(vector3)
        //dataDaoNeo4j.datosParaEstadisticaService()
        ubicacionService.conectar(ubicacion1.nombreUbicacion.toString(),ubicacion0.nombreUbicacion.toString(),"Terrestre")
        ubicacionService.mover(vector.id!!.toInt(), ubicacion0.nombreUbicacion)
        ubicacionService.conectar("Quilmes","Quilmes","Terrestre")
        ubicacionService.conectar("Mar del Plata","Mar del Plata","Terrestre")
        ubicacionService.conectar("Maeame","Maeame","Terrestre")


    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteCon0VectoresPresentes0CuandoNoHayNingunVectorEnEsaUbicacion(){




        val reporte = estadisticasService.reporteDeContagios("Berazategui")
        Assert.assertEquals(0, reporte.vectoresPresentes)

    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteConUnVectorPresente1CuandoHayUnSoloVectorEnEsaUbicacion(){
        Assert.assertEquals("Quilmes", ubicacion0.nombreUbicacion)
        val reporte = estadisticasService.reporteDeContagios("Quilmes")
        Assert.assertEquals(1, reporte.vectoresPresentes)
    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteConDosVectoresPresentes2CuandoHayDosVectoresEnEsaUbicacion(){
        val vector2 = Vector()
        vector2.tipo = Insecto()
        vector2.estado = Infectado()
        vector2.estado = estado
        vector2.ubicacion = ubicacion0
        vectorService.crearVector(vector2)
        ubicacionService.mover(vector.id!!.toInt(), ubicacion0.nombreUbicacion)
        val reporte = estadisticasService.reporteDeContagios("Quilmes")
        Assert.assertEquals(2, reporte.vectoresPresentes)
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
    fun elEstadisticasServiceDevuelveUnReporteCon0VectoresInfectadosCuandoNoHayNingunVectorInfectadoEnEsaUbicacion(){
        val reporte = estadisticasService.reporteDeContagios("Mar del Plata")
        Assert.assertEquals(0, reporte.vectoresInfecatods)
    }

    @Test
    fun elEstadisticaServiceDevuelveUnReporteCon1VectorInfectadoCuandoHayUnVectorInfectadoEnEsaUbicacionMDP(){
        this.crearNConEstadoEn(1, Infectado(),"Mar del Plata")
        val reporte = estadisticasService.reporteDeContagios("Mar del Plata")
        Assert.assertEquals(1, reporte.vectoresInfecatods)
    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteCon1VectorInfectadoCuandoHayUnVectorInfectadoEnMarDelPlataCuandoHayOtrosVectoresSanos(){
        this.crearNConEstadoEn(1, Infectado(), "Mar del Plata")
        this.crearNConEstadoEn(5, Sano(), "Mar del Plata")
        val reporte = estadisticasService.reporteDeContagios("Mar del Plata")
        Assert.assertEquals(1, reporte.vectoresInfecatods)
    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteCon1VectorInfectadoCuandoHayUnVectorInfectadoEnEsaUbicacion(){
        this.crearNConEstadoEn(1, Infectado(), "Quilmes")
        val reporte = estadisticasService.reporteDeContagios("Quilmes")
        Assert.assertEquals(2, reporte.vectoresInfecatods)
    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteCon2VectoresInfectados2CuandoHayDosVectoresInfectadosEnEsaUbicacion(){
        this.crearNConEstadoEn(2, Infectado(),"Quilmes")
        val reporte = estadisticasService.reporteDeContagios("Quilmes")
        Assert.assertEquals(3, reporte.vectoresInfecatods)
    }

    @Test
    fun  laEspecieMasInfecciosaEsLaUnicaEspecieQueHayEnQuilmesYEsAlgo(){
        val reporte = estadisticasService.reporteDeContagios("Quilmes")
        Assert.assertEquals("Algo", reporte.nombreDeEspecieMasInfecciosa)
    }
    @Test
    fun elEstadisticasServiceDevuelveLasPrimeras10EspeciesCuandoSoloExistenTresEspecies(){

        val especiesLideres=estadisticasService.lideres()
        Assert.assertEquals(especiesLideres.size, 3)
    }

    @Test
    fun lideresTest() {
        hibernateData.eliminarTodo()

        especie = Especie()
        especie2 = Especie()
        especie3 = Especie()
        especie.cantidadInfectadosParaADN = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        especie2.cantidadInfectadosParaADN = 42
        especie2.nombre = "Algo2"
        especie2.paisDeOrigen = "Alemania"
        especie3.cantidadInfectadosParaADN = 42
        especie3.nombre = "Algo3"
        especie3.paisDeOrigen = "Alemania"

        val ubicacionFinal = ubicacionService.crearUbicacion("Maeame")
        val vectorRandom = Vector()
        val vectorAlfa = Vector()
        val vectorBeta = Vector()
        val vectorGama = Vector()
        vectorRandom.tipo = Animal()
        vectorAlfa.tipo = Humano()
        vectorBeta.tipo = Insecto()
        vectorGama.tipo = Humano()

        vectorRandom.ubicacion = ubicacionFinal
        vectorAlfa.ubicacion = ubicacionFinal
        vectorBeta.ubicacion= ubicacionFinal
        vectorGama.ubicacion= ubicacionFinal

//        especie1 infecta a vector humano
        vectorAlfa.infectarse(especie)

//        especie2 infecta a vector humano
//        especie2 infecta a vector humano
//        especie2 infecta a vector animal
        vectorAlfa.infectarse(especie2)
        vectorGama.infectarse(especie2)
        vectorRandom.infectarse(especie2)

//        especie3 infecta a vector humano
//        especie3 infecta a vector humano
        vectorAlfa.infectarse(especie3)
        vectorGama.infectarse(especie3)

        vectorService.crearVector(vectorRandom)
        vectorService.crearVector(vectorAlfa)
        vectorService.crearVector(vectorBeta)
        vectorService.crearVector(vectorGama)

        val especiesLideres=estadisticasService.lideres()
        Assert.assertEquals(1, especiesLideres.first().id!!)
    }




    @Test
    fun  laNombreDeLaEspecieMasInfecciosaEsElStringVacioCuandoNoQueHayNingunaEspecieEnLaUbicacion(){
        val ubicacionDesconocida = Ubicacion()
        ubicacionDesconocida.nombreUbicacion = "The twilight zone"
        val reporte = estadisticasService.reporteDeContagios("The twilight zone")
        Assert.assertEquals("", reporte.nombreDeEspecieMasInfecciosa)
    }

    @Test
    fun  laNombreDeLaEspecieMasInfecciosaEsElStringVacioCuandoLaUbicacionNoExiste(){
        val reporte = estadisticasService.reporteDeContagios("Tokyo, baby")
        Assert.assertEquals("", reporte.nombreDeEspecieMasInfecciosa)
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
        val reporte = estadisticasService.reporteDeContagios("Maeame")
        Assert.assertEquals("Paperas", reporte.nombreDeEspecieMasInfecciosa)
    }


    @Test
    fun laEspecieQueMasHumanosInfectaEsEspecie(){
        Assert.assertEquals(1, estadisticasService.especieLider().id)
    }

    @Test(expected = NoHayEspecieQueInfectaronHumanos::class)
    fun NingunHumanoInfectado(){
        eliminarTodo()
        val ubicacionFinal = ubicacionService.crearUbicacion("Maeame")
        val vectorRandom = Vector()
        val vectorAlfa = Vector()
        val vectorBeta = Vector()
        vectorRandom.tipo = Humano()
        vectorAlfa.tipo = Humano()
        vectorBeta.tipo = Humano()
        vectorRandom.ubicacion = ubicacionFinal
        vectorAlfa.ubicacion = ubicacionFinal
        vectorBeta.ubicacion= ubicacionFinal
        vectorService.crearVector(vectorAlfa)
        vectorService.crearVector(vectorBeta)
        vectorService.crearVector(vectorRandom)
        estadisticasService.especieLider()
    }


    @Test(expected = NoHayEspecieQueInfectaronHumanos::class)
    fun TodosLosVectoresExistentesEstanInfectadosPeroNoSonHumanos() {
        eliminarTodo()
        val ubicacionFinal = ubicacionService.crearUbicacion("Maeame")
        val vectorRandom = Vector()
        val vectorAlfa = Vector()
        val vectorBeta = Vector()
        vectorRandom.tipo = Animal()
        vectorAlfa.tipo = Insecto()
        vectorBeta.tipo = Insecto()
        especie = Especie()
        especie.cantidadInfectadosParaADN = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        vectorRandom.infectarse(especie)
        vectorAlfa.infectarse(especie)
        vectorBeta.infectarse(especie)
        vectorRandom.ubicacion = ubicacionFinal
        vectorAlfa.ubicacion = ubicacionFinal
        vectorBeta.ubicacion= ubicacionFinal
        vectorService.crearVector(vectorAlfa)
        vectorService.crearVector(vectorBeta)
        vectorService.crearVector(vectorRandom)
        estadisticasService.especieLider()
    }


    @After
    fun eliminarTodo(){
        hibernateData.eliminarTodo()
        dataDaoNeo4j.eliminarTodo()
    }
}