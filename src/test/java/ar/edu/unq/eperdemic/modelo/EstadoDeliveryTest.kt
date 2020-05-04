package ar.edu.unq.eperdemic.modelo

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
            estadoDeliverySUT = EstadoDelivery()
            infectado = "Infectado"
            sano = "Sano"
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicada(){
        val result = estadoDeliverySUT.estado(sano)!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = estadoDeliverySUT.estado("SANO")!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoSanoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = estadoDeliverySUT.estado("sano")!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculasYMayusculas(){
        val result = estadoDeliverySUT.estado("SaNo")!!
        Assert.assertEquals(sano,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoInfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculas(){
        val result = estadoDeliverySUT.estado("Infectado")!!
        Assert.assertEquals(infectado,result.nombre())
    }

    @Test
    fun estadoDeliveryRetornaElEstadoinfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMinusculas(){
        val result = estadoDeliverySUT.estado("infectado")!!
        Assert.assertEquals(infectado,result.nombre())
    }


    @Test
    fun estadoDeliveryRetornaElEstadoinfectadoCorrectoCuandoLaClaveEsLaIndicadaSiSeLoEscribeConMayusculasYMinusculasYMayusculas(){
        val result = estadoDeliverySUT.estado("InFeCtAdO")!!
        Assert.assertEquals(infectado,result.nombre())
    }

    @Test(expected = EstadoNoEncontradoException::class)
    fun testAlIntentarRecuperarUnVectorConUnaKeyErroneaArrojaUNEstadoNoEncontradoRunTimeException(){
        val algo = estadoDeliverySUT.estado("sarasa")
        Assert.assertEquals(null, algo)
    }

    @Test(expected = ClaveRepetidaDeEstadoException::class)
    fun testAlIntentaAgregarUnEstadoConUnaKeyYaUtilizadaArrojaUnEstadoNoEncontradoRunTimeException(){
        val algo = estadoDeliverySUT.agregarEstado(Infectado())
    }

}