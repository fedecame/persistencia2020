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
        val result = estadoDeliverySUT.get(sano)!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = estadoDeliverySUT.get("SANO")!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = estadoDeliverySUT.get("sano")!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculasYMayusculas(){
        val result = estadoDeliverySUT.get("SaNo")!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoInfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = estadoDeliverySUT.get("Infectado")!!
        Assert.assertEquals(infectado,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoinfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = estadoDeliverySUT.get("infectado")!!
        Assert.assertEquals(infectado,result.nombre())
    }


    @Test
    fun estadoDeliveryRetornaElEstadoinfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculasYMayusculas(){
        val result = estadoDeliverySUT.get("InFeCtAdO")!!
        Assert.assertEquals(infectado,result.nombre())
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