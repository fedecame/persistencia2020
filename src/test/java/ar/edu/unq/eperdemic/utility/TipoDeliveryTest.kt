package ar.edu.unq.eperdemic.utility

import ar.edu.unq.eperdemic.estado.transformer.EstadoDelivery
import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeTipoException
import ar.edu.unq.eperdemic.modelo.exception.TipoNoEncontradoException
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.transformer.TipoDelivery
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TipoDeliveryTest {    private lateinit var estadoDeliverySUT : EstadoDelivery
    private lateinit var tipoDeliverySUT : TipoDelivery
    private lateinit var tipos : List<TipoVector>
    private lateinit var animal  : String
    private lateinit var humano  : String
    private lateinit var insecto : String
    private lateinit var tipoAnimal  : TipoVector
    private lateinit var tipoHumano  : TipoVector
    private lateinit var tipoInsecto : TipoVector

    @Before
    fun setUP(){
        tipos = mutableListOf(Animal(), Humano(), Insecto())
        tipoDeliverySUT = TipoDelivery(tipos)
        animal  = "Animal"
        humano  = "Humano"
        insecto = "Insecto"
    }

    @Test
    fun estadoDeliveryRetornaElTipoAnimalCuandoLaClaveEsLaIndicada(){
        val result = tipoDeliverySUT.get(animal)!!
        Assert.assertTrue(result.esAnimal())
    }


    @Test
    fun estadoDeliveryRetornaElTipoHumanoCuandoLaClaveEsLaIndicada(){
        val result = tipoDeliverySUT.get(humano)!!
        Assert.assertTrue(result.esHumano())
    }


    @Test
    fun estadoDeliveryRetornaElTipoInsectoCuandoLaClaveEsLaIndicada(){
        val result = tipoDeliverySUT.get(insecto)!!
        Assert.assertTrue(result.esInsecto())
    }

    @Test
    fun estadoDeliveryRetornaElTipoAnimalCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = tipoDeliverySUT.get("ANIMAL")!!
        Assert.assertTrue(result.esAnimal())
    }

    @Test
    fun estadoDeliveryRetornaElTipoHumanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = tipoDeliverySUT.get("HUMANO")!!
        Assert.assertTrue(result.esHumano())
    }

    @Test
    fun estadoDeliveryRetornaElTipoInsectoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = tipoDeliverySUT.get("INSECTO")!!
        Assert.assertTrue(result.esInsecto())
    }

    @Test
    fun estadoDeliveryRetornaElTipoAnimalCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = tipoDeliverySUT.get("animal")!!
        Assert.assertTrue(result.esAnimal())
    }

    @Test
    fun estadoDeliveryRetornaElTipoHumanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = tipoDeliverySUT.get("humano")!!
        Assert.assertTrue(result.esHumano())
    }

    @Test
    fun estadoDeliveryRetornaElTipoInsectoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = tipoDeliverySUT.get("insecto")!!
        Assert.assertTrue(result.esInsecto())
    }


    @Test
    fun estadoDeliveryRetornaElTipoAnimalCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculas(){
        val result = tipoDeliverySUT.get("AnImAl")!!
        Assert.assertTrue(result.esAnimal())
    }

    @Test
    fun estadoDeliveryRetornaElTipoHumanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculas(){
        val result = tipoDeliverySUT.get("HuMaNo")!!
        Assert.assertTrue(result.esHumano())
    }

    @Test
    fun estadoDeliveryRetornaElTipoInsectoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculas(){
        val result = tipoDeliverySUT.get("InSeCtO")!!
        Assert.assertTrue(result.esInsecto())
    }

    @Test(expected = TipoNoEncontradoException::class)
    fun testAlIntentarRecuperarUnVectorConUnaKeyErroneaArrojaUNEstadoNoEncontradoRunTimeException(){
        val algo = tipoDeliverySUT.get("sarasa")
    }

    @Test(expected = ClaveRepetidaDeTipoException::class)
    fun testAlIntentaAgregarUnEstadoConUnaKeyYaUtilizadaArrojaUnEstadoNoEncontradoRunTimeException(){
        val algo = tipoDeliverySUT.add(Humano())
    }
}