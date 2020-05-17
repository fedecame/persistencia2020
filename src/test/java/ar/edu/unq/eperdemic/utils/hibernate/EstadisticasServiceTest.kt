package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.EstadisticaServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
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
        estadisticasService = EstadisticaServiceImpl(estadisticasDAO)
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
        especie.cantidadInfectados = 42
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
        vector.ubicacion = ubicacion1
        ubicacion1.vectores.add(vector)
        vectorService.crearVector(vector)
        ubicacionService.mover(vector.id!!.toInt(), ubicacion0.nombreUbicacion)
    }
/*
    @Test
    fun elEstadisticasServiceDevuelve0CuandoNoHayNingunVectorEnEsaUbicacion(){
        val reporte = estadisticasService.reporteDeContagios("Mar del Plata")
        Assert.assertEquals(0, reporte.vectoresPresentes)
    }
*/
    @Test
    fun elEstadisticasServiceDevuelve1CuandoHayUnSoloVectorEnEsaUbicacion(){
        Assert.assertEquals("Quilmes", ubicacion0.nombreUbicacion)
        val reporte = estadisticasService.reporteDeContagios("Quilmes")
        Assert.assertEquals(1, reporte.vectoresPresentes)
    }

    @Test
    fun elEstadisticasServiceDevuelve2CuandoHayDosVectoresEnEsaUbicacion(){
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

    @After
    open fun eliminarTodo(){
        TransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}