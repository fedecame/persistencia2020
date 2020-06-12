package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.neo4j.driver.Values

class VectorTest {
    private lateinit var vectorSUT : Vector
    private lateinit var especie : Especie
    private lateinit var ubicacion : Ubicacion
    private lateinit var patogeno : Patogeno

    @Before
    fun setUp(){
        vectorSUT = Vector()
        vectorSUT.estado = Sano()
        vectorSUT.tipo = Animal()

        ubicacion = Ubicacion()
        ubicacion.nombreUbicacion = "Rusia"
        vectorSUT.ubicacion= ubicacion
        especie = Especie()
        especie.cantidadInfectadosParaADN = 42
        especie.nombre = "Algo"
        especie.paisDeOrigen = "Alemania"
        patogeno = Patogeno()
        patogeno.tipo = ""
        especie.patogeno = patogeno
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
        Assert.assertEquals(42,especie.cantidadInfectadosParaADN)
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
        Assert.assertTrue(vectorSUT.estado is Sano)
    }

    @Test
    fun testelVectorSeCreaConUnEstadoSano(){
        Assert.assertTrue(vectorSUT.estado is Sano)
    }

    @Test
    fun testElVectorPasaAInfectadoAlEnfermarse(){
        Assert.assertTrue(vectorSUT.estado is Sano)
        vectorSUT.infectarse(especie)
        Assert.assertTrue(vectorSUT.estado is Infectado)
        Assert.assertEquals(1,vectorSUT.especies.size)
    }

    @Test
    fun testElVectorTieneLaUbicacionIndicada(){
        Assert.assertEquals("Rusia",vectorSUT.ubicacion!!.nombreUbicacion)
    }

    @Test
    fun elVectorAnimalTiene3TiposDeCaminos() {
        Assert.assertEquals(3, vectorSUT.tipo.posiblesCaminos.size)
    }

    @Test
    fun elVectorAnimalTieneLos3TiposDeCaminos(){
        val t = vectorSUT.tipo.posiblesCaminos
        Assert.assertTrue(t.contains(TipoCamino.Aereo))
        Assert.assertTrue(t.contains(TipoCamino.Maritimo))
        Assert.assertTrue(t.contains(TipoCamino.Terrestre))
    }

    @Test
    fun elVectorInsectoTiene2TiposDeCaminos() {
        vectorSUT.tipo = Insecto()
        Assert.assertEquals(1, vectorSUT.tipo.posiblesCaminos.size)
    }

    @Test
    fun elVectorInsectoTieneTipoDeCaminoAereo(){
        vectorSUT.tipo = Insecto()
        val t = vectorSUT.tipo.posiblesCaminos
        Assert.assertTrue(t.contains(TipoCamino.Aereo))
    }

    @Test
    fun elVectorHumanoTiene2TiposDeCaminos() {
        vectorSUT.tipo = Humano()
        Assert.assertEquals(2, vectorSUT.tipo.posiblesCaminos.size)
    }

    @Test
    fun elVectorHumanoTieneTiposDeCaminosMaritimoYTerrestre() {
        vectorSUT.tipo = Humano()
        val t = vectorSUT.tipo.posiblesCaminos
        Assert.assertTrue(t.contains(TipoCamino.Terrestre))
        Assert.assertTrue(t.contains(TipoCamino.Maritimo))
    }

    @Test
    fun losNombresDeLosTiposDelCaminoDelVectorSonCorrectos(){
        val t = vectorSUT.tipo.posiblesCaminos
        val esperado = listOf("Aereo", "Terrestre", "Maritimo")
        Assert.assertTrue(t.map{it.nombre()}.all{esperado.contains(it)})
    }

    @Test
    fun seImprimenCorrectamenteLosTiposDeCamino() {
        val coso = vectorSUT.tipo.posiblesCaminos.toString()
        val coso2 = coso.toString().replace("[", "[:").replace(",", " |") + "*".trim()
        Assert.assertEquals(coso2, "[:Terrestre | Maritimo | Aereo]*")

        vectorSUT.tipo = Insecto()
        val coso3 = vectorSUT.tipo.posiblesCaminos.toString().toString().replace("[", "[:").replace(",", " |") + "*".trim()
        Assert.assertEquals(coso3, "[:Aereo]*")

        vectorSUT.tipo = Humano()

        //Este es el que quiero!
        val movimientos0 = 2
        val coso4 = vectorSUT.tipo.posiblesCaminos.toString().toString().replace("[", "[:").replace(",", " |").replace("]", "*${movimientos0.toString()}]").trim().trim()

        Assert.assertEquals(coso4, "[:Terrestre | Maritimo*2]")
        val query2 = this.coso2("lejano", coso4)
        //Esta query funciona y es la que quiero ocn la fora que quiero
        Assert.assertEquals("MATCH (n:Ubicacion {nombre:lejano})-[:Terrestre | Maritimo*2]  -> (fof) RETURN COUNT(DISTINCT fof)", query2)
    }

    private fun coso2(nombreUbicacion : String, tiposQueryConMov : String) : String =
        "MATCH (n:Ubicacion {nombre:${nombreUbicacion}})-${tiposQueryConMov}  -> (fof) RETURN COUNT(DISTINCT fof)"

}