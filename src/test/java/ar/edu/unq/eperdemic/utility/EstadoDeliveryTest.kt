package ar.edu.unq.eperdemic.utility

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.estado.transformer.EstadoDelivery
import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeEstadoException
import ar.edu.unq.eperdemic.modelo.exception.EstadoNoEncontradoException
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EstadoDeliveryTest {
    private lateinit var estadoDeliverySUT : EstadoDelivery
    private lateinit var infectado : String
    private lateinit var sano : String

    @Before
    fun setUP(){
            estadoDeliverySUT = EstadoDelivery(mutableListOf(Sano(), Infectado()))
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicada(){
        val result = estadoDeliverySUT.get("sano")!!
        Assert.assertTrue(result is Sano)
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = estadoDeliverySUT.get("SANO")!!
        Assert.assertTrue(result is Sano)
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = estadoDeliverySUT.get("sano")!!
        Assert.assertTrue(result is Sano)
    }

    @Test
    fun estadoDeliveryRetornaElEstadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculasYMayusculas(){
        val result = estadoDeliverySUT.get("SaNo")!!
        Assert.assertTrue(result is Sano)
    }

    @Test
    fun estadoDeliveryRetornaElEstadoInfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = estadoDeliverySUT.get("Infectado")!!
        Assert.assertTrue(result is Infectado)
    }

    @Test
    fun estadoDeliveryRetornaElEstadoinfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = estadoDeliverySUT.get("infectado")!!
        Assert.assertTrue(result is Infectado)
    }


    @Test
    fun estadoDeliveryRetornaElEstadoinfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculasYMayusculas(){
        val result = estadoDeliverySUT.get("InFeCtAdO")!!
        Assert.assertTrue(result is Infectado)
    }

    @Test(expected = EstadoNoEncontradoException::class)
    fun testAlIntentarRecuperarUnVectorConUnaKeyErroneaArrojaUNEstadoNoEncontradoRunTimeException(){
        val algo = estadoDeliverySUT.get("sarasa")
    }

    @Test(expected = ClaveRepetidaDeEstadoException::class)
    fun testAlIntentaAgregarUnEstadoConUnaKeyYaUtilizadaArrojaUnEstadoNoEncontradoRunTimeException(){
        val algo = estadoDeliverySUT.add(Infectado())
    }

}