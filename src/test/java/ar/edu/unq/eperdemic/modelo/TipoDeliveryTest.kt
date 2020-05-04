package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeEstadoException
import ar.edu.unq.eperdemic.modelo.exception.EstadoNoEncontradoException
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TipoDeliveryTest {    private lateinit var estadoDeliverySUT : EstadoDelivery
    private lateinit var tipoDeliverySUT : TipoDelivery
    private lateinit var animal  : String
    private lateinit var humano  : String
    private lateinit var insecto : String
    private lateinit var tipoAnimal  : TipoVector
    private lateinit var tipoHumano  : TipoVector
    private lateinit var tipoInsecto : TipoVector




    @Before
    fun setUP(){
        tipoDeliverySUT = TipoDelivery()
        animal  = "Animal"
        humano  = "Humano"
        insecto = "Insecto"
    }

    @Test
    fun estadoDeliveryRetornaElTipoAnimalCuandoLaClaveEsLaIndicada(){
        val result = tipoDeliverySUT.tipo(animal)!!
        Assert.assertTrue(result.esAnimal())
    }


    @Test
    fun estadoDeliveryRetornaElTipoHumanoCuandoLaClaveEsLaIndicada(){
        val result = tipoDeliverySUT.tipo(humano)!!
        Assert.assertTrue(result.esHumano())
    }


    @Test
    fun estadoDeliveryRetornaElTipoInsectoCuandoLaClaveEsLaIndicada(){
        val result = tipoDeliverySUT.tipo(insecto)!!
        Assert.assertTrue(result.esInsecto())
    }

    @Test
    fun estadoDeliveryRetornaElTipoAnimalCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = tipoDeliverySUT.tipo("ANIMAL")!!
        Assert.assertTrue(result.esAnimal())
    }

    @Test
    fun estadoDeliveryRetornaElTipoHumanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = tipoDeliverySUT.tipo("HUMANO")!!
        Assert.assertTrue(result.esHumano())
    }

    @Test
    fun estadoDeliveryRetornaElTipoInsectoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = tipoDeliverySUT.tipo("INSECTO")!!
        Assert.assertTrue(result.esInsecto())
    }

    @Test
    fun estadoDeliveryRetornaElTipoAnimalCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = tipoDeliverySUT.tipo("animal")!!
        Assert.assertTrue(result.esAnimal())
    }

    @Test
    fun estadoDeliveryRetornaElTipoHumanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = tipoDeliverySUT.tipo("humano")!!
        Assert.assertTrue(result.esHumano())
    }

    @Test
    fun estadoDeliveryRetornaElTipoInsectoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = tipoDeliverySUT.tipo("insecto")!!
        Assert.assertTrue(result.esInsecto())
    }


    @Test
    fun estadoDeliveryRetornaElTipoAnimalCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculas(){
        val result = tipoDeliverySUT.tipo("AnImAl")!!
        Assert.assertTrue(result.esAnimal())
    }

    @Test
    fun estadoDeliveryRetornaElTipoHumanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculas(){
        val result = tipoDeliverySUT.tipo("HuMaNo")!!
        Assert.assertTrue(result.esHumano())
    }

    @Test
    fun estadoDeliveryRetornaElTipoInsectoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculas(){
        val result = tipoDeliverySUT.tipo("InSeCtO")!!
        Assert.assertTrue(result.esInsecto())
    }

    @Test(expected = EstadoNoEncontradoException::class)
    fun testAlIntentarRecuperarUnVectorConUnaKeyErroneaArrojaUNEstadoNoEncontradoRunTimeException(){
        val algo = tipoDeliverySUT.tipo("sarasa")
    }

    @Test(expected = ClaveRepetidaDeEstadoException::class)
    fun testAlIntentaAgregarUnEstadoConUnaKeyYaUtilizadaArrojaUnEstadoNoEncontradoRunTimeException(){
        val algo = tipoDeliverySUT.agregarTipo(Humano())
    }

}