package ar.edu.unq.eperdemic.modelo.random

import ar.edu.unq.eperdemic.modelo.exception.MalosParametrosRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class RandomMasterTest {
    val randomMaster = RandomMasterImpl()

    @Test
    fun giveMeARandomNumberDaUnDouble(){
        Assert.assertTrue(randomMaster.giveMeARandonNumberBeetween(0.00, 100.00) is Double)
    }

    @Test
    fun giveMeARandomNumberDaUnDoubleEntreElRangoEstablecidosEnLosParametros(){
        var aNumber = 0.00
        var otherNumber = 100.00
        var numeroX=randomMaster.giveMeARandonNumberBeetween(aNumber, otherNumber)
        print(numeroX)
        Assert.assertTrue(numeroX is Double)
        Assert.assertTrue(numeroX >= aNumber)
        Assert.assertTrue(numeroX <= otherNumber)

        Assert.assertTrue(numeroX is Double)
        Assert.assertTrue(numeroX >= aNumber)
        Assert.assertTrue(numeroX <= otherNumber)
        Assert.assertTrue(numeroX in aNumber..otherNumber)
    }

    @Test
    fun giveMeARandomNumberDaUnDoubleEntreElRangoEstablecidosEnLosParametrosCualquieraSeanEstos(){
        var numeroX : Double
            repeat(200){
                var aNumber = Random.nextDouble(0.00, 50.00)
                var otherNumber = Random.nextDouble(50.00, 100.00)
                numeroX = randomMaster.giveMeARandonNumberBeetween(aNumber, otherNumber)
                Assert.assertTrue(numeroX is Double)
                Assert.assertTrue(numeroX >= aNumber)
                Assert.assertTrue(numeroX <= otherNumber)
                Assert.assertTrue(numeroX in aNumber..otherNumber)
            }
    }

    @Test(expected = MalosParametrosRunTimeException::class)
    fun randonMasterTiraUnaExcepcionCuandoElIndiceDeLaIzquierdaEsMayorAlDeLaDerecha() {
        randomMaster.giveMeARandonNumberBeetween(100.00, 0.0)
    }


    @Test(expected = MalosParametrosRunTimeException::class)
    fun randonMasterTiraUnaExcepcionCuandoAmbosIndicesSonIguales() {
        randomMaster.giveMeARandonNumberBeetween(80.00, 80.00)
    }

}