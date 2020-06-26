package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
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

class VectorServiceTest {

    lateinit var vectorService : VectorService
    lateinit var ubicacionService : UbicacionService
    lateinit var vector : Vector
    lateinit var vector1 : Vector
    lateinit var tipo : TipoVector
    lateinit var estado : EstadoVector
    lateinit var estado1 : EstadoVector
    lateinit var especie : Especie
    lateinit var especie1 : Especie
    lateinit var dataDAO : DataDAO
    lateinit var ubicacionDAO : UbicacionDAO
    lateinit var vectorDAO : VectorDAO
    lateinit var ubicacion : Ubicacion
    lateinit var patogeno : Patogeno
    lateinit var hibernateData : HibernateDataService
    var dataNeoj4= Neo4jDataService()

    @Before
    fun setUp(){
        hibernateData = HibernateDataService()
        vector = Vector()
        vector1 = Vector()
        dataDAO = HibernateDataDAO()
        ubicacionDAO = HibernateUbicacionDAO()
        vectorDAO = HibernateVectorDAO()
        vectorService = VectorServiceImpl(vectorDAO, ubicacionDAO)
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        ubicacion = ubicacionService.crearUbicacion("Alemania")
        tipo = Humano()
        estado = Sano()
        estado1 = Infectado()
        especie = Especie()
        especie.cantidadInfectadosParaADN = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        patogeno = Patogeno()
        patogeno.tipo = ""
        patogeno.factorContagioHumano= 1000
        especie1 = Especie()
        especie1.cantidadInfectadosParaADN = 42
        especie1.nombre = "soyUnaEspecie"
        especie1.paisDeOrigen = "Masachuset"
        especie1.patogeno = patogeno


        especie.patogeno = patogeno
        vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
        vector.tipo = tipo
        vector.estado = estado
        vector.agregarEspecie(especie)

        vector1.tipo = tipo
        vector1.estado = estado1
        vector1.agregarEspecie(especie1)

        ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())
        ubicacion = ubicacionService.crearUbicacion("Quilmes")
        vector.ubicacion = ubicacion
        ubicacion.vectores.add(vector)
        ubicacion.vectores.add(vector1)
        vector1.ubicacion = ubicacion
        vectorService.crearVector(vector)

        vectorService.crearVector(vector1)

    }

    @Test
    fun testSeContagioVector(){
        val vector2 = Vector()
        vector2.tipo = tipo
        vector2.estado = estado
        vector2.ubicacion = ubicacionService.crearUbicacion("Saavedra")
        vectorService.crearVector(vector2)
        val vectoresAContagiar : MutableList<Vector> = mutableListOf()
        vectoresAContagiar.add(vector2)

        vectorService.contagiar(vector1,vectoresAContagiar)
        val res = vectorService.recuperarVector(vector2.id!!.toInt())

        Assert.assertEquals(1,res.especies.size)

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
        Assert.assertEquals(42,especie.cantidadInfectadosParaADN)
        Assert.assertEquals("Algo",especie.nombre)
        Assert.assertEquals("Alemania",especie.paisDeOrigen)
        Assert.assertEquals("",especie.patogeno.tipo)
    }

    @Test
    fun testAlSolicitarLasEnfermedadesDeUnVectorConDosEspeciesRetornaUnaListaConLasEspeciesIndicadas(){
        val vector1 = Vector()
        vector1.tipo = Insecto()
        vector1.estado = Infectado()
        val especie2 = Especie()
        especie2.cantidadInfectadosParaADN = 23
        especie2.nombre = "Sarasa"
        especie2.paisDeOrigen = "Japon"
        val patogenoTest = Patogeno()
        patogenoTest.tipo = "Nisman"
        especie2.patogeno = patogenoTest
        vector1.agregarEspecie(especie)
        vector1.agregarEspecie(especie2)
        vector1.ubicacion = ubicacion
        vectorService.crearVector(vector1)
        val list = vectorService.enfermedades(vector1.id!!.toInt()).toList()
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(2,list.size)
        val especie1 = list.find { it.nombre == "Sarasa" }
        val especie0 = list.find { it.nombre == "Algo" }
        Assert.assertEquals(23,especie1!!.cantidadInfectadosParaADN)
        Assert.assertEquals("Sarasa",especie1!!.nombre)
        Assert.assertEquals("Japon",especie1!!.paisDeOrigen)
        Assert.assertEquals("Nisman",especie1!!.patogeno.tipo)
        Assert.assertEquals(42,especie0!!.cantidadInfectadosParaADN)
        Assert.assertEquals("Algo",especie0!!.nombre)
        Assert.assertEquals("Alemania",especie0!!.paisDeOrigen)
        Assert.assertEquals("",especie0!!.patogeno.tipo)
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
        val recuperado = vectorService.recuperarVector(1)
        val list = recuperado.especies
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(1,list.size)
        val especie = list.first()
        Assert.assertEquals(42,especie.cantidadInfectadosParaADN)
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
        val vectorSano = Vector()
        vectorSano.tipo = Humano()
        vectorSano.estado = Sano()
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        vectorSano.ubicacion = jamaica
        vectorService.crearVector(vectorSano)
        val recuperado = vectorService.recuperarVector(vectorSano.id!!.toInt())
        Assert.assertTrue(recuperado.estado is Sano)
    }

    @Test
    fun testAlInfectarseUnVectorTieneEstadoInfectado(){
        val patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        val otroPatogeno = Patogeno()
        otroPatogeno.tipo = "VIRUS"
        otroPatogeno.cantidadDeEspecies = 1
        patogenoService.crearPatogeno(otroPatogeno)
//        val especie2 = otroPatogeno.crearEspecie("un nombrecito", "Tailandia")
        val especie2 = patogenoService.agregarEspecie(otroPatogeno.id!!, "un nombrecito", "Tailandia")
//        especie2.patogeno = otroPatogeno
        val recuperado = vectorService.recuperarVector(1)
        vectorService.infectar(recuperado,especie2)
        val recupInfectado = vectorService.recuperarVector(1)
        Assert.assertTrue(recupInfectado.estado is Infectado)
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
        Assert.assertEquals(3, vectorRecuperado.id!!)
    }

    @Test(expected = IDVectorNoEncontradoException::class)
    fun testAlRecuperarUnIDInexistenteRetornaNull(){
        val vectorRecuperado = vectorService.recuperarVector(42)
    }

    @Test(expected = IDVectorNoEncontradoException::class)
    fun testBorraVector(){
        vectorService.borrarVector(vector.id!!.toInt())
        vectorService.recuperarVector(vector.id!!.toInt())
    }

    @Test
    fun testBorraVectorDeUbicacion(){
        val ubicacionAnt = ubicacionService.recuperarUbicacion(ubicacion.nombreUbicacion)
        Assert.assertTrue(ubicacionAnt.vectores.any { curr -> curr.id == vector.id })
        vectorService.borrarVector(vector.id!!.toInt())
        val ubicacionAct = ubicacionService.recuperarUbicacion(ubicacion.nombreUbicacion)
        Assert.assertFalse(ubicacionAct.vectores.any { curr -> curr.id == vector.id })
    }

    @After
    fun eliminarTodo(){
        hibernateData.eliminarTodo()
        dataNeoj4.eliminarTodo()

    }

}