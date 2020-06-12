package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


import org.mockito.Mockito.mock
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import kotlin.math.exp

class UbicacionNeo4jTest {

    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())

       var vector = Vector()
    var vectorAnimal=Vector()
    var ubicacionPlantalandia = Ubicacion()
    var ubicacionBichoLandia = Ubicacion()
    var ubicacionTibetDojo = Ubicacion()
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
    var neo4jData=Neo4jDataService()
var id=0
    @Before
    fun setUp() {
        ubicacionService.crearUbicacion("BichoLandia")
        ubicacionService.crearUbicacion("Florencio Varela")

        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
        vector.ubicacion = ubicacionService.crearUbicacion("Mar Del Plata")
         ubicacionService.crearUbicacion("Quilmes")
        vectorAnimal.ubicacion=vector.ubicacion
        vector.tipo = Humano()
        vectorAnimal.tipo= Animal()
        vector.estado = Sano()
        vectorAnimal.estado=Sano()
        id = vectorService.crearVector(vector)?.id?.toInt()!!
        vectorService.crearVector(vectorAnimal)

    }
    @Test

    fun vectorMueveAUbicacionAledaña() {
        ubicacionService.conectar("Mar Del Plata","Quilmes","Terrestre")
       ubicacionService.mover(vector.id?.toInt()!!,"Quilmes")

        Assert.assertEquals(vectorService.recuperarVector(vector.id?.toInt()!!).ubicacion?.nombreUbicacion,"Quilmes")
    }


    @Test(expected = UbicacionMuyLejana::class)
    fun vectorNoPuedeMoverPorqueUbicacionEsLejana() {
        ubicacionService.mover(1,"")

    }


    @Test(expected = CaminoNoSoportado::class)
    fun vectorNoPuedeMoversePorCaminoNoSoportado() {
        ubicacionService.conectar("Mar Del Plata","Florencio Varela", "Maritimo")
        ubicacionService.mover(1, "Florencio Varela")
    }
    @Test
    fun vectorAnimalMuevePorCaminoMaritimo(){
        ubicacionService.conectar("Mar Del Plata", "Florencio Varela","Maritimo")
        ubicacionService.mover(2,"Florencio Varela")
        Assert.assertEquals(vectorAnimal.ubicacion?.nombreUbicacion,"Florencio Varela")
    }



    @After
    fun eliminarTodo() {
        neo4jData.eliminarTodo()
    }





}