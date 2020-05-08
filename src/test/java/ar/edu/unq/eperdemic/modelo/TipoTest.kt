package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class TipoTest {

    lateinit var unHumanoSut :Humano
    lateinit var vectorAContagiar : Vector
    lateinit var tipoContagiador : Animal
    lateinit var tipoInsecto : Insecto
    lateinit var especie1 : Especie
    lateinit var listaEspeciesContagiador : MutableList<Especie>

    @Before
    fun setUp(){
        unHumanoSut= Humano()
        vectorAContagiar= Vector()
        tipoContagiador= Animal()
        tipoInsecto= Insecto()
        especie1= Especie()
        listaEspeciesContagiador= mutableListOf()
        listaEspeciesContagiador.add(especie1)
    }

    @Test
    fun testInfectaSiLeCorresponde(){
        var mockEspecie= Mockito.mock(Especie::class.java)
        var mockRandom = Mockito.mock(RandomMaster::class.java)
        var mockVectorAContagiar = Mockito.mock(Vector::class.java)
        unHumanoSut.randomGenerator=mockRandom
        unHumanoSut.infectameSiCorresponde(mockVectorAContagiar,mockEspecie)
        Mockito.`when`(mockRandom.giveMeARandonNumberBeetween(1.0,100.0)).thenReturn(100.0)
        Mockito.`when`(mockEspecie.factorContagioAnimal()).thenReturn(2)
        Mockito.verify(mockVectorAContagiar).infectarse(mockEspecie)
    }

    @Test
    fun testPorcentajeContagioCorrecto(){
        var mockEspecie= Mockito.mock(Especie::class.java)
        var mockRandom = Mockito.mock(RandomMaster::class.java)
        Mockito.`when`(mockEspecie.factorContagioHumano()).thenReturn(2)
        Mockito.`when`(mockRandom.giveMeARandonNumberBeetween(1.0,10.0)).thenReturn(2.0)
        unHumanoSut.randomGenerator= mockRandom
        Assert.assertTrue(4.0 ==unHumanoSut.porcentajeDeContagioExitoso(mockEspecie))
    }

    @Test
    fun testHumanoPuedeSerContagiadoPorUnAnimal(){
        Assert.assertTrue(unHumanoSut.puedeSerContagiadoPor(tipoContagiador))

    }

    @Test
    fun testAnimalNoPuedeSerContagiadoPorUnHumano(){
        Assert.assertFalse(tipoContagiador.puedeSerContagiadoPor(unHumanoSut))
    }

    @Test
    fun testInsectoNoPuedeSerContagiadoPorOtroInsecto(){

        Assert.assertFalse(tipoInsecto.puedeSerContagiadoPor(tipoInsecto))
    }
}
