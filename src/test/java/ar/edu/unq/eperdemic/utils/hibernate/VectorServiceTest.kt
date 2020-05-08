package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorServiceTest {

    lateinit var vectorService : VectorService
    lateinit var ubicacionService : UbicacionService
    lateinit var vector : Vector
    lateinit var tipo : TipoVector
    lateinit var estado : EstadoVector
    lateinit var especie : Especie
    lateinit var dataDAO : DataDAO
    lateinit var ubicacionDAO : UbicacionDAO
    lateinit var vectorDAO : VectorDAO
    lateinit var ubicacion : Ubicacion

    @Before
    fun setUp(){
        vector = Vector()
        dataDAO = HibernateDataDAO()
        ubicacionDAO = HibernateUbicacionDAO()
        vectorDAO = HibernateVectorDAO()
        vectorService = VectorServiceImpl(vectorDAO, dataDAO, ubicacionDAO)
        ubicacionService = UbicacionServiceImpl(ubicacionDAO, dataDAO)
        ubicacion = ubicacionService.crearUbicacion("Alemania")
        tipo = Humano()
        estado = Sano()
        especie = Especie()
        especie.cantidadInfectados = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        especie.patogeno = Patogeno("")
        vector.tipo = tipo
        vector.estado = estado
        vector.agregarEspecie(especie)
        vector.ubicacion = ubicacion

        vectorService.crearVector(vector)
    }

    @Test
    fun testALPedirLasEnfermedadesDeUnaVectorSinNIngunaEspecieRetornaUnaListaVacia(){
        val vector0 = Vector()
        vector0.tipo = tipo
        vector0.estado = estado
        vector0.ubicacion = ubicacion
        vectorService.crearVector(vector0)
        val list = vectorService.enfermedades(vector0.id!!.toInt())
        Assert.assertTrue(list.isEmpty())
        Assert.assertEquals(0,list.size)
    }

    @Test
    fun testAlSolicitarLasEnfermedadesDeUnVectorConUnaEspecieRetornaUnaListaConLaEspecieIndicada(){
        val list = vectorService.enfermedades(vector.id!!.toInt())
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(1,list.size)
        val especie = list.first()
        Assert.assertEquals(42,especie.cantidadInfectados)
        Assert.assertEquals("Algo",especie.nombre)
        Assert.assertEquals("Alemania",especie.paisDeOrigen)
        Assert.assertEquals("",especie.patogeno.tipo)
    }

    @Test
    fun testAlSolicitarLasEnfermedadesDeUnVectorConDosEspeciesRetornaUnaListaConLasEspeciesIndicadas(){
        val vector1 = Vector()
        vector1.tipo = Insecto()
        vector1.estado = Infectado()
        vector1.ubicacion = ubicacion
        val especie2 = Especie()
        especie2.cantidadInfectados = 23
        especie2.nombre = "Sarasa"
        especie2.paisDeOrigen = "Japon"
            especie2.patogeno = Patogeno("Nisman")
        vector1.agregarEspecie(especie)
        vector1.agregarEspecie(especie2)
        vectorService.crearVector(vector1)
        val list = vectorService.enfermedades(vector1.id!!.toInt())
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(2,list.size)
        val especie0 = list.get(0)
        val especie1 = list.get(1)
        Assert.assertEquals(23,especie1.cantidadInfectados)
        Assert.assertEquals("Sarasa",especie1.nombre)
        Assert.assertEquals("Japon",especie1.paisDeOrigen)
        Assert.assertEquals("Nisman",especie1.patogeno.tipo)
        Assert.assertEquals(42,especie0.cantidadInfectados)
        Assert.assertEquals("Algo",especie0.nombre)
        Assert.assertEquals("Alemania",especie0.paisDeOrigen)
        Assert.assertEquals("",especie0.patogeno.tipo)
    }



    @Test
    fun testAlRecuperarUNVectorSinEspeciesRetornaUnaListaVacia(){
        val vector0 = Vector()
        vector0.tipo = tipo
        vector0.estado = estado
        vector0.ubicacion = ubicacion
        vectorService.crearVector(vector0)
        val n = vector0.id!!.toInt()
        val recuperado = vectorService.recuperarVector(n)
        Assert.assertEquals(n, recuperado.id!!.toInt())
        val list = recuperado.especies
        Assert.assertTrue(list.isEmpty())
        Assert.assertEquals(0,list.size)
    }

    @Test
    fun testAlRecuperarUnVectorConUnaEspeciesRetornaUnaListaConLaEspecieIndicada(){
        //Cuando tengamos el service con el infectar y demas, lo vamos a poder probar a nivel de Service
        //Estaria bueno agregar la ruta de sanar(vectorID) en VectorService
        val recuperado = vectorService.recuperarVector(1)
        val list = recuperado.especies
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(1,list.size)
        val especie = list.first()
        Assert.assertEquals(42,especie.cantidadInfectados)
        Assert.assertEquals("Algo",especie.nombre)
        Assert.assertEquals("Alemania",especie.paisDeOrigen)
        Assert.assertEquals("",especie.patogeno.tipo)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarConSuID(){
        val recuperado = vectorService.recuperarVector(1)
        Assert.assertEquals(1, recuperado.id!!)
    }

    @Test
    fun testAlCrearseUnVectorTieneEstadoSano(){
        val recuperado = vectorService.recuperarVector(1)
        Assert.assertEquals("Sano", recuperado.estado.nombre())
    }

    @Test
    fun testAlInfectarseUnVectorTieneEstadoInfectado(){
        val especie2 = Especie()
        val recuperado = vectorService.recuperarVector(1)
        vectorService.infectar(recuperado,especie2)
        val recupInfectado = vectorService.recuperarVector(1)
        Assert.assertEquals("Infectado", recupInfectado.estado.nombre())
    }

    @Test
    fun testAlCrearUnVectorElModeloQuedaConsistente(){
        val vector0 = Vector()
        vector0.tipo = tipo
        vector0.ubicacion = ubicacion
        Assert.assertEquals(null, vector0.id)
        vectorService.crearVector(vector0)
        Assert.assertNotEquals(null, vector0.id)
        Assert.assertEquals(1, vector.id!!.toInt())
    }

     @Test
   fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
         val vector0 = Vector()
         vector0.tipo = tipo
         vector0.ubicacion = ubicacion
         val vector1 = Vector()
         vector1.tipo = tipo
         vector1.ubicacion = ubicacion
         val id1 = vectorService.crearVector(vector0).id!!
         val id2 = vectorService.crearVector(vector1).id!!
         Assert.assertTrue(id1 < id2)
         Assert.assertEquals(id1+1, id2)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarPorSuID(){
        val vectorAGuardar = Vector()
        vectorAGuardar.tipo = tipo
        vectorAGuardar.ubicacion = ubicacion
        vectorService.crearVector(vectorAGuardar)
        val vectorRecuperado = vectorService.recuperarVector(vectorAGuardar.id!!.toInt())
        Assert.assertEquals(2, vectorRecuperado.id!!)
    }

    @Test(expected = IDVectorNoEncontradoException::class)
    fun testAlRecuperarUnIDInexistenteRetornaNull(){
        val vectorRecuperado = vectorService.recuperarVector(42)
    }

    @After
    open fun eliminarTodo(){
        vectorService.borrarTodo()
    }

}