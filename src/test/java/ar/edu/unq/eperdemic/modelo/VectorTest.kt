package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class VectorTest {
    private lateinit var vectorSUT : Vector
    private lateinit var especie : Especie
    private lateinit var ubicacion : Ubicacion

    @Before
    fun setUp(){
        vectorSUT = Vector()
        vectorSUT.estado = Sano()
        vectorSUT.tipo = Animal()

        ubicacion = Ubicacion()
        ubicacion.nombreUbicacion = "Rusia"
        vectorSUT.ubicacion= ubicacion
        especie = Especie()
        especie.cantidadInfectados = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        especie.patogeno = Patogeno("")

    }

    @Test
    fun testUnVectorNoTieneEspeciesRepetidas(){
        vectorSUT.agregarEspecie(especie)
        vectorSUT.agregarEspecie(especie)
        val list = this.toList(vectorSUT.especies)
        Assert.assertEquals(1,list.size)
    }

    @Test
    fun testUnVectorSinEspeciesRetornaUnaListaVaciaAlPedirselas(){
        val list = vectorSUT.especies
        Assert.assertTrue(list.isEmpty())
        Assert.assertEquals(0,list.size)
    }

    private fun toList(ms : MutableSet<Especie>) = ms.toList()


    @Test
    fun testAlIrAgregandoAlVectorEspeciesEstasSeIncrementan(){
        var list = this.toList(vectorSUT.especies)
        Assert.assertTrue(list.isEmpty())
        Assert.assertEquals(0,list.size)
        vectorSUT.agregarEspecie(especie)
        list =  this.toList(vectorSUT.especies)
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(1,list.size)
        vectorSUT.agregarEspecie(Especie())
        list =  this.toList(vectorSUT.especies)
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(2,list.size)
    }


    @Test
    fun testSeAgregaUnaNuevaEspecieAlVectorYAlRecuperarlaEstaEsIdentica(){
        vectorSUT.agregarEspecie(especie)
        val list =  this.toList(vectorSUT.especies)
        Assert.assertFalse(list.isEmpty())
        Assert.assertEquals(1,list.size)
        val especie = list.first()
        Assert.assertEquals(42,especie.cantidadInfectados)
        Assert.assertEquals("Algo",especie.nombre)
        Assert.assertEquals("Alemania",especie.paisDeOrigen)
        Assert.assertEquals("",especie.patogeno.tipo)
    }

    @Test
    fun testelVectorNaceSinId(){
        Assert.assertEquals(null, vectorSUT.id)
    }

    @Test
    fun testElEstadoDelVectorEsElIndicado(){
        Assert.assertEquals("Sano",vectorSUT.estado.nombre())
    }
    @Test
    fun testelVectorSeCreaConUnEstadoSano(){
        Assert.assertEquals("Sano", vectorSUT.estado.nombre())
    }

    @Test
    fun testElVectorPasaAInfectadoAlEnfermarse(){
        Assert.assertEquals("Sano", vectorSUT.estado.nombre())
        vectorSUT.infectarse(especie)
        Assert.assertEquals("Infectado", vectorSUT.estado.nombre())
        Assert.assertEquals(1,vectorSUT.especies.size)
    }

    @Test
    fun testElVectorTieneLaUbicacionIndicada(){
        Assert.assertEquals("Rusia",vectorSUT.ubicacion!!.nombreUbicacion)
    }
//
//    @Test
//    fun testAlRecibirContagiarsePorSeEnviaElMensajeAlTipo(){
//        //val unTipo = Mockito.mock(Humano::class.java)
//         val spyTipoAnimal= Mockito.spy(vectorSUT.tipo)
//        val vector0 =Vector()
//        vector0.tipo = Humano()
//
//
//        vectorSUT.contagiarsePor(vector0)
//        Mockito.verify(spyTipoAnimal).contagiamePor(vectorSUT,vector0.tipo,vector0.especies.toList())
//    }

    @Test
    fun testAlIntentarContagiarLograInfectar(){

        val mockespecie= Mockito.mock(Especie::class.java)
        val mocktipoInsecto = Mockito.mock(Insecto::class.java)
        val mockRandom= Mockito.mock(RandomMaster::class.java)
        val listaAContagiar : MutableList<Vector> = mutableListOf()
        var vectorAContagiar = Vector()


        vectorAContagiar.tipo= mocktipoInsecto
        mocktipoInsecto.randomGenerator=mockRandom
        vectorSUT.especies.add(mockespecie)
        Mockito.`when`(mockRandom.giveMeARandonNumberBeetween(1.0,2.0)).thenReturn(0.0)
        Mockito.`when`(mocktipoInsecto.porcentajeDeContagioExitoso(mockespecie)).thenReturn(100.0)
        Mockito.`when`(mockespecie.nombre).thenReturn("Algo")


        listaAContagiar.add(vectorAContagiar)


        vectorSUT.contagiar(listaAContagiar)




        var especiesret =vectorAContagiar.especies.first()
        Assert.assertEquals("Infectado",vectorAContagiar.estado.nombre())
        Assert.assertEquals("Algo",especiesret.nombre)
    }
}