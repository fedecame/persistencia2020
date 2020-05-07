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
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())
    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO(), HibernateDataDAO())
     val vector = Vector()
    val tipo = Humano()
    val estado = Sano()
   lateinit var ubicacionCreada:Ubicacion
    @Before
    fun setUp(){
    vector.tipo=tipo
    vector.estado=estado
         ubicacionCreada = ubicacionService.crearUbicacion("Florencio Varela")
        var ubicacion2Creada= ubicacionService.crearUbicacion("Quilmes")

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

//(expected = NoExisteUbicacionADondeSeDeseaMover::class)internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.secure.spi.JaccIntegrator].
//00:21:30.557 [main] DEBUG org.hibernate.integrator.internal.IntegratorServiceImpl - Adding Integrator [org.hibernate.cache.internal.CollectionCacheInvalidator].
//00:21:30.959 [main] INFO org.hibernate.Version - HHH000412: Hibernate ORM core version 5.4.12.Final
//00:21:30.966 [main] DEBUG org.hibernate.cfg.Environment - HHH000206: hibernate.properties not found
//00:21:31.655 [main] DEBUG org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver - Interpreting public/system identifier : [-//Hibernate/Hibernate Configuration DTD 3.0//EN] - [http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd]
//00:21:31.657 [main] DEBUG org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver - Recognized hibernate-configuration identifier; attempting to resolve on classpath under org/hibernate/
//00:21:31.668 [main] DEBUG org.hibernate.boot.cfgxml.internal.Jax
 fun seMueveAUbicacionQueNoExiste(){
    vector.setearNombreUbicacion("Florencio Varela")
    vectorService.crearVector(vector)
    ubicacionService.mover(1,"Sarandi")
}

    open fun eliminarTodo(){
        vectorService.borrarTodo()
    }
}