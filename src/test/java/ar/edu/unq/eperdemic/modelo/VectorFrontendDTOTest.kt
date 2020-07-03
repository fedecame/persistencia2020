package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorFrontendDTOTest {

    lateinit var vectorDTOP : VectorFrontendDTO
    lateinit var vectorDTOA : VectorFrontendDTO
    lateinit var vectorDTOI : VectorFrontendDTO
    lateinit var vectorP : Vector
    lateinit var vectorA : Vector
    lateinit var vectorI : Vector
    lateinit var ubicacion : Ubicacion

    @Before
    fun setUp(){
        ubicacion = Ubicacion()
        ubicacion.nombreUbicacion = "Narnia"
        vectorDTOA = VectorFrontendDTO(VectorFrontendDTO.TipoDeVector.Animal, ubicacion)
        vectorDTOI = VectorFrontendDTO(VectorFrontendDTO.TipoDeVector.Insecto, ubicacion)
        vectorDTOP =  VectorFrontendDTO(VectorFrontendDTO.TipoDeVector.Persona, ubicacion)
        vectorA = vectorDTOA.aModelo()
        vectorI = vectorDTOI.aModelo()
        vectorP = vectorDTOP.aModelo()
    }

    @Test
    fun laUbicacionDeLosVectorResultadosDeAModeloLaCorrecta(){
        Assert.assertEquals("Narnia", vectorA.ubicacion!!.nombreUbicacion)
        Assert.assertEquals("Narnia", vectorI.ubicacion!!.nombreUbicacion)
        Assert.assertEquals("Narnia", vectorP.ubicacion!!.nombreUbicacion)
    }

    @Test
    fun losVectoresResultadosDeAModeloTienenEstadoInicialSano(){
        Assert.assertTrue(vectorA.estado is Sano)
        Assert.assertTrue(vectorI.estado is Sano)
        Assert.assertTrue(vectorP.estado is Sano)
    }

    @Test
    fun losVectoresResultadosDeAModeloTienenTiposDelModeloCorrectamente(){
        Assert.assertTrue(vectorA.tipo is Animal)
        Assert.assertTrue(vectorI.tipo is Insecto)
        Assert.assertTrue(vectorP.tipo is Humano)
    }
}