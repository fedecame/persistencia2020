package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacionADondeSeDeseaMover
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UbicacionServiceTest {
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO(), HibernateUbicacionDAO())
    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO(), HibernateDataDAO())
    val vector = Vector()
    val tipo = Humano()
    val estado = Sano()
    var ubicacionCreada = ubicacionService.crearUbicacion("Florencio Varela")
    var ubicacion2Creada= ubicacionService.crearUbicacion("Quilmes")
    @Before
    fun setUp(){
        vector.tipo=tipo
        vector.estado=estado
    }

    @Test
    fun creacionDeUbicacion() {
        Assert.assertEquals(ubicacionCreada.nombreUbicacion, "Florencio Varela")
    }

    @Test

    fun verificacionDeVectorAlojado() {
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        Assert.assertEquals(vectorService.recuperarVector(1).ubicacion?.nombreUbicacion,"Florencio Varela")
    }

    @Test
    fun alMoverEnUbicacionMueveElVectorDeseado(){
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        var vectorCreado=vectorService.recuperarVector(1)
        Assert.assertEquals(vectorCreado.ubicacion?.nombreUbicacion,"Florencio Varela")
        ubicacionService.mover(1,"Quilmes")
        var vectorActualizado=vectorService.recuperarVector(1)
        Assert.assertEquals(vectorActualizado.ubicacion?.nombreUbicacion,"Quilmes")
    }

    @Test (expected = NoExisteUbicacionADondeSeDeseaMover::class)
    fun seMueveAUbicacionQueNoExiste(){
        vector.ubicacion = ubicacionService.crearUbicacion("Florencio Varela666")
        vectorService.crearVector(vector)
        ubicacionService.mover(1,"Sarandi")
    }
    @After
    open fun eliminarTodo(){
//        ubicacionService.borrarTodo()
    }
}