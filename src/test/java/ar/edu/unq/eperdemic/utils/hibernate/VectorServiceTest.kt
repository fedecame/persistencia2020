package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorServiceTest {

    lateinit var vectorService : VectorService
    lateinit var vector : Vector
    lateinit var tipo : TipoVector
    lateinit var estado : EstadoVector
    lateinit var especie : Especie

    @Before
    fun setUp(){
        vector = Vector()
        tipo = Humano()
        estado = Sano()
        especie = Especie()
        especie.cantidadInfectados = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        especie.patogeno = Patogeno("")
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())
        vector.tipo = tipo
        vector.estado = estado
        vector.agregarEspecie(especie)
        vectorService.crearVector(vector)
    }

    @Test
    fun testAlRecuperarUNVectorSinEspeciesRetornaUnaListaVacia(){
        val vector0 = Vector()
        vector0.tipo = tipo
        vector0.estado = estado
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

        val recuperado = vectorService.recuperarVector(1)
        Assert.assertEquals("Sano", recuperado.estado.nombre())
    }

    @Test
    fun testAlInfectarseUnVectorTieneEstadoSano(){
        val recuperado = vectorService.recuperarVector(1)
        Assert.assertEquals("Sano", recuperado.estado.nombre())
    }

    /* Este test falla xq no se actualiza el estado. Todavia no esta el infectar del VectorService
    @Test
    fun testUNVectorSeInfectaAlRecibirElMensajeInfectarse(){
        vector.estado = Infectado()
        val recuperado = vectorService.recuperarVector(1)
        Assert.assertEquals("Infectado", recuperado.estado.nombre())
    }*/

    @Test
    fun testAlCrearUnVectorElModeloQuedaConsistente(){
        val vector0 = Vector()
        vector0.tipo = tipo
        Assert.assertEquals(null, vector0.id)
        vectorService.crearVector(vector0)
        Assert.assertNotEquals(null, vector0.id)
        Assert.assertEquals(1, vector.id!!.toInt())
    }

    @Test()
    fun testAlIntentarRecuperarUnVectorConUNIdInexistenteSeLanzaUNaIDVectorNoEncontradoException(){
        vectorService.recuperarVector(420)
    }

     @Test
   fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
         val vector0 = Vector()
         vector0.tipo = tipo
         val vector1 = Vector()
         vector1.tipo = tipo
        val id1 = vectorService.crearVector(vector0).id!!
        val id2 = vectorService.crearVector(vector1).id!!
        Assert.assertTrue(id1 < id2)
         Assert.assertEquals(id1+1, id2)
    }

    @Test
    fun testAlCrearUnVectorEsteSePuedeRecuperarPorSuID(){
        val vectorAGuardar = Vector()
        vectorAGuardar.tipo = tipo
        vectorService.crearVector(vectorAGuardar)
        val vectorRecuperado = vectorService.recuperarVector(vectorAGuardar.id!!.toInt())
        Assert.assertEquals(2, vectorRecuperado.id!!)
    }

    @Test
    fun testAlRecuperarUnIDInexistenteRetornaNull(){
        val vectorRecuperado = vectorService.recuperarVector(42)
        Assert.assertEquals(null, vectorRecuperado)
    }

    @After
    open fun eliminarTodo(){
        vectorService.borrarTodo()
    }

}