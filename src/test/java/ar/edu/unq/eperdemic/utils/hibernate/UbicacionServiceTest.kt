package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UbicacionServiceTest {
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())
    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO(), HibernateDataDAO())
     val vector = Vector()
    val tipo = Humano()
    val estado = Sano()
    @Before
    fun setUp(){
    vector.tipo=tipo
    vector.estado=estado
        var ubicacionCreada = ubicacionService.crearUbicacion("Florencio Varela")

    }

    @Test
    fun creacionDeUbicacion() {
        var ubicacionCreada = ubicacionService.crearUbicacion("Florencio Varela")

        Assert.assertEquals(ubicacionCreada.nombreUbicacion, "Florencio Varela")
    }

    @Test

    fun verificacionDeVectorAlojado() {
        vector.setearNombreUbicacion("Florencio Varela")
        vectorService.crearVector(vector)
        Assert.assertEquals(vectorService.recuperarVector(1).ubicacion?.nombreUbicacion,"Florencio Varela")
    }


   // @Test(expected = PatogenoNotFoundRunTimeException::class)
   // fun alRecuperarUnPatogenoDeIDNoExistenteLanzaUnaExcepcion() {
     //   dao.recuperar(42)
    //}
}