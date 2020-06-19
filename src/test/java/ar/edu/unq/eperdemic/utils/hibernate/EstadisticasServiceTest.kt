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
        vector.agregarEspecie(especie3)
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
        ubicacionService.conectar(ubicacion1.nombreUbicacion,ubicacion0.nombreUbicacion,"Terrestre")
        ubicacionService.mover(vector.id!!.toInt(), ubicacion0.nombreUbicacion)
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
    fun elEstadisticasServiceDevuelveUnReporteConDosVectoresPresentesCuandoHayDosVectoresEnEsaUbicacion(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val vector2 = Vector()
        vector2.tipo = Insecto()
        vector2.estado = Sano()
        vector2.ubicacion = jamaica
        vectorService.crearVector(vector2)
        vector.ubicacion = jamaica
        jamaica.vectores.add(vector)
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            ubicacionDAO.actualizar(jamaica)
        }
        val reporte = estadisticasService.reporteDeContagios("Jamaica")
        Assert.assertEquals(2, reporte.vectoresPresentes)
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
    fun elEstadisticasServiceDevuelveUnReporteCon0VectoresInfectadosCuandoNoHayNingunVectorInfectadoEnEsaUbicacion(){
        val ubicacionVacia = ubicacionService.crearUbicacion("NoTieneVectoresInfectados")
        val vectorSano = Vector()
        vectorSano.estado = Sano()
        vectorSano.tipo = Humano()
        vectorSano.ubicacion = ubicacionVacia
        vectorService.crearVector(vectorSano)
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            ubicacionDAO.actualizar(ubicacionVacia)
        }
        val reporte = estadisticasService.reporteDeContagios("NoTieneVectoresInfectados")
        Assert.assertEquals(0, reporte.vectoresInfecatods)
    }

    @Test
    fun elEstadisticaServiceDevuelveUnReporteCon1VectorInfectadoCuandoHayUnVectorInfectadoEnEsaUbicacionJamaica(){
        ubicacionService.crearUbicacion("Jamaica")
        this.crearNConEstadoEn(1, Infectado(),"Jamaica")
        val reporte = estadisticasService.reporteDeContagios("Jamaica")
        Assert.assertEquals(1, reporte.vectoresInfecatods)
    }

    @Test
    fun elEstadisticasServiceDevuelveUnReporteCon1VectorInfectadoCuandoHayUnVectorInfectadoEnJamaicaCuandoHayOtrosVectoresSanos(){
        ubicacionService.crearUbicacion("Jamaica")

        this.crearNConEstadoEn(1, Infectado(), "Jamaica")
        this.crearNConEstadoEn(5, Sano(), "Jamaica")
        val reporte = estadisticasService.reporteDeContagios("Jamaica")
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
        Assert.assertEquals(2, especiesLideres.first().id!!)
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
        val ubicacionFinal = ubicacionService.crearUbicacion("Maeame")
        val paperas = Especie()
        val unaGripe = Especie()
        unaGripe.paisDeOrigen = "Jamaica"
        unaGripe.nombre = "GripeJ"
        paperas.paisDeOrigen = "Rusia"
        paperas.nombre = "Paperas"
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

    @Test
    fun estadisticaServiceDevuelveLasPrimeros10EspeciesQueMasInfectaronEnOrdenDesendente(){
        val argentina = ubicacionService.crearUbicacion("Argentina")
        var vectorUno = Vector()
        vectorUno.tipo = Humano()
        vectorUno.ubicacion = argentina
        var vectorDos = Vector()
        vectorDos.tipo = Humano()
        vectorDos.ubicacion = argentina
        var vectorTres=Vector()
        vectorTres.tipo = Humano()
        vectorTres.ubicacion = argentina
        var vectorCuatro = Vector()
        vectorCuatro.tipo = Humano()
        vectorCuatro.ubicacion = argentina
        especie2 = patogeno.crearEspecie("especie2","Argentina")
        vectorUno.agregarEspecie(especie2)
        vectorDos.agregarEspecie(especie2)
        vectorTres.agregarEspecie(especie2)
        especie3=patogeno.crearEspecie("especie3","Argentina")
        vectorUno.agregarEspecie(especie3)
        vectorDos.agregarEspecie(especie3)
        vectorTres.agregarEspecie(especie3)
        vectorCuatro.agregarEspecie(especie3)
        var especie4=patogeno.crearEspecie("especie4","Argentina")
        vectorUno.agregarEspecie(especie4)
        vectorDos.agregarEspecie(especie4)
        var especie5=patogeno.crearEspecie("especie5","Argentina")
        vectorUno.agregarEspecie(especie5)
        vectorDos.agregarEspecie(especie5)
        var especie6=patogeno.crearEspecie("especie6","Argentina")
        vectorUno.agregarEspecie(especie6)
        vectorDos.agregarEspecie(especie6)
        var especie7=patogeno.crearEspecie("especie7","Argentina")
        vectorUno.agregarEspecie(especie7)
        vectorDos.agregarEspecie(especie7)
        var especie8=patogeno.crearEspecie("especie8","Argentina")
        vectorUno.agregarEspecie(especie8)
        vectorDos.agregarEspecie(especie8)

        var especie9=patogeno.crearEspecie("especie9","Argentina")
        var especie10=patogeno.crearEspecie("especie10","Argentina")
        var especie11=patogeno.crearEspecie("especie11","Argentina")
        var especie12=patogeno.crearEspecie("especie12","Argentina")

        vectorService.crearVector(vectorUno)
        vectorService.crearVector(vectorDos)
        vectorService.crearVector(vectorTres)
        vectorService.crearVector(vectorCuatro)

        var especies= estadisticasService.lideres()
        Assert.assertEquals("Algo", especies[0].nombre)
        Assert.assertEquals("Algo2", especies[1].nombre)
        Assert.assertEquals("Algo3", especies[2].nombre)
        Assert.assertEquals("especie2", especies[3].nombre)
        Assert.assertEquals("especie3", especies[4].nombre)
        Assert.assertEquals("especie4", especies[5].nombre)
        Assert.assertEquals("especie5", especies[6].nombre)
        Assert.assertEquals("especie6", especies[7].nombre)
        Assert.assertEquals("especie7", especies[8].nombre)
        Assert.assertEquals("especie8", especies[9].nombre)
    }

    @Test
    fun estadisticaServiceDevuelveLas10SolamenteLasEspeciesQueMasInfectaron(){
        val argentina = ubicacionService.crearUbicacion("Argentina")
        var vectorUno = Vector()
        vectorUno.tipo = Humano()
        vectorUno.ubicacion = argentina
        var vectorDos = Vector()
        vectorDos.tipo = Humano()
        vectorDos.ubicacion = argentina
        var vectorTres=Vector()
        vectorTres.tipo = Humano()
        vectorTres.ubicacion = argentina
        var vectorCuatro = Vector()
        vectorCuatro.tipo = Humano()
        vectorCuatro.ubicacion = argentina
        especie2 = patogeno.crearEspecie("especie2","Argentina")
        vectorUno.agregarEspecie(especie2)
        vectorDos.agregarEspecie(especie2)
        vectorTres.agregarEspecie(especie2)
        especie3=patogeno.crearEspecie("especie3","Argentina")
        vectorUno.agregarEspecie(especie3)
        vectorDos.agregarEspecie(especie3)
        vectorTres.agregarEspecie(especie3)
        vectorCuatro.agregarEspecie(especie3)
        var especie4=patogeno.crearEspecie("especie4","Argentina")
        vectorUno.agregarEspecie(especie4)
        vectorDos.agregarEspecie(especie4)
        var especie5=patogeno.crearEspecie("especie5","Argentina")
        vectorUno.agregarEspecie(especie5)
        vectorDos.agregarEspecie(especie5)
        var especie6=patogeno.crearEspecie("especie6","Argentina")
        vectorUno.agregarEspecie(especie6)
        vectorDos.agregarEspecie(especie6)
        var especie7=patogeno.crearEspecie("especie7","Argentina")
        vectorUno.agregarEspecie(especie7)
        vectorDos.agregarEspecie(especie7)
        var especie8=patogeno.crearEspecie("especie8","Argentina")
        vectorUno.agregarEspecie(especie8)
        vectorDos.agregarEspecie(especie8)

        vectorService.crearVector(vectorUno)
        vectorService.crearVector(vectorDos)
        vectorService.crearVector(vectorTres)
        vectorService.crearVector(vectorCuatro)

        var especies= estadisticasService.lideres()
        Assert.assertEquals(especies.size,10)
    }


    @After
    fun eliminarTodo(){
        hibernateData.eliminarTodo()
        dataDaoNeo4j.eliminarTodo()
    }
}