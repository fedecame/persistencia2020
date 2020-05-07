package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorDAOTest {

        lateinit var vectorDAO: VectorDAO
        lateinit var vector : Vector
        lateinit var tipo : TipoVector
        lateinit var estado : EstadoVector
        lateinit var especie : Especie

        @Before
        fun setUp(){
            vector = Vector()
            tipo = Humano()
            estado = Sano()
            especie = Especie()
            especie.cantidadInfectados = 42
            especie.nombre = "Algo"
            especie.paisDeOrigen = "Alemania"
            especie.patogeno = Patogeno("")
            vectorDAO = HibernateVectorDAO()
            vector.tipo = tipo
            vector.estado = estado
            vector.agregarEspecie(especie)
            vectorDAO.crear(vector)
        }

        @Test
        fun testALPedirLasEnfermedadesDeUnaVectorSinNIngunaEspecieRetornaUnaListaVacia(){
            val vector0 = Vector()
            vector0.tipo = tipo
            vector0.estado = estado
            vectorDAO.crear(vector0)
            val list = vectorDAO.enfermedades(vector0.id!!.toInt())
            Assert.assertTrue(list.isEmpty())
            Assert.assertEquals(0,list.size)
        }

        @Test
        fun testAlSolicitarLasEnfermedadesDeUnVectorConUnaEspecieRetornaUnaListaConLaEspecieIndicada(){
            val list = vectorDAO.enfermedades(vector.id!!.toInt())
            Assert.assertFalse(list.isEmpty())
            Assert.assertEquals(1,list.size)
            val especie = list.first()
            Assert.assertEquals(42,especie.cantidadInfectados)
            Assert.assertEquals("Algo",especie.nombre)
            Assert.assertEquals("Alemania",especie.paisDeOrigen)
            Assert.assertEquals("",especie.patogeno.tipo)
        }

        @Test
        fun testAlSolicitarLasEnfermedadesDeUnVectorConDosEspeciesRetornaUnaListaConLaEspecieIndicada(){
            val vector1 = Vector()
            vector1.tipo = Insecto()
            vector1.estado = Infectado()
            val especie2 = Especie()
            especie2.cantidadInfectados = 23
            especie2.nombre = "Sarasa"
            especie2.paisDeOrigen = "Japon"
            especie2.patogeno = Patogeno("Nisman")
            vector1.agregarEspecie(especie)
            vector1.agregarEspecie(especie2)
            vectorDAO.crear(vector1)
            val list = vectorDAO.enfermedades(vector1.id!!.toInt())
            Assert.assertFalse(list.isEmpty())
            Assert.assertEquals(2,list.size)
            val especie1 = list.get(0)
            val especie0 = list.get(1)
            Assert.assertEquals(23,especie1.cantidadInfectados)
            Assert.assertEquals("Sarasa",especie1.nombre)
            Assert.assertEquals("Japon",especie1.paisDeOrigen)
            Assert.assertEquals("Nisman",especie1.patogeno.tipo)
            Assert.assertEquals(42,especie0.cantidadInfectados)
            Assert.assertEquals("Algo",especie0.nombre)
            Assert.assertEquals("Alemania",especie0.paisDeOrigen)
            Assert.assertEquals("",especie0.patogeno.tipo)

        }



        @Test
        fun testAlRecuperarUNVectorSinEspeciesRetornaUnaListaVacia(){
            val vector0 = Vector()
            vector0.tipo = tipo
            vector0.estado = estado
            vectorDAO.crear(vector0)
            val n = vector0.id!!.toInt()
            val recuperado = vectorDAO.recuperar(n)
            Assert.assertEquals(n, recuperado.id!!.toInt())
            val list = recuperado.especies
            Assert.assertTrue(list.isEmpty())
            Assert.assertEquals(0,list.size)
        }

        @Test
        fun testAlRecuperarUnVectorConUnaEspecieRetornaUnaListaConLaEspecieIndicada(){
            //Cuando tengamos el service con el infectar y demas, lo vamos a poder probar a nivel de Service
            //Estaria bueno agregar la ruta de sanar(vectorID) en VectorService
            val recuperado = vectorDAO.recuperar(1)
            val list = recuperado.especies
            Assert.assertFalse(list.isEmpty())
            Assert.assertEquals(1,list.size)
            val especie = list.first()
            Assert.assertEquals(42,especie.cantidadInfectados)
            Assert.assertEquals("Algo",especie.nombre)
            Assert.assertEquals("Alemania",especie.paisDeOrigen)
            Assert.assertEquals("",especie.patogeno.tipo)
        }

        @Test
        fun testAlCrearUnVectorEsteSePuedeRecuperarConSuID(){
            val recuperado = vectorDAO.recuperar(1)
            Assert.assertEquals(1, recuperado.id!!)
        }

        @Test
        fun testAlCrearseUnVectorTieneEstadoSano(){
            val recuperado = vectorDAO.recuperar(1)
            Assert.assertEquals("Sano", recuperado.estado.nombre())
        }

        @Test
        fun testAlInfectarseUnVectorTieneEstadoInfectado(){
            val especie2 = Especie()
            val recuperado = vectorDAO.recuperar(1)
            vectorDAO.infectar(recuperado,especie2)
//            puedefallar
            Assert.assertEquals("Infectado", recuperado.estado.nombre())
        }
//
//        @Test
//        fun testAlInfectarseUnVectorTieneEstadoSano(){
//            val recuperado = vectorDAO.recuperar(1)
//            Assert.assertEquals("Sano", recuperado.estado.nombre())
//        }


        @Test
        fun testAlCrearUnVectorElModeloQuedaConsistente(){
            val vector0 = Vector()
            vector0.tipo = tipo
            Assert.assertEquals(null, vector0.id)
            vectorDAO.crear(vector0)
            Assert.assertNotEquals(null, vector0.id)
            Assert.assertEquals(1, vector.id!!.toInt())
        }
//          implementar la exception
//        @Test()
//        fun testAlIntentarRecuperarUnVectorConUNIdInexistenteSeLanzaUNaIDVectorNoEncontradoException(){
//            vectorDAO.recuperar(420)
//        }

        @Test
        fun testElIDEsAutoincrementalALaMedidaQueSeCreanNuevosVectores(){
            val vector0 = Vector()
            vector0.tipo = tipo
            val vector1 = Vector()
            vector1.tipo = tipo
            val id1 = vectorDAO.crear(vector0).id!!
            val id2 = vectorDAO.crear(vector1).id!!
            Assert.assertTrue(id1 < id2)
            Assert.assertEquals(id1+1, id2)
        }

        @Test
        fun testAlCrearUnVectorEsteSePuedeRecuperarPorSuID(){
            val vectorAGuardar = Vector()
            vectorAGuardar.tipo = tipo
            vectorDAO.crear(vectorAGuardar)
            val vectorRecuperado = vectorDAO.recuperar(vectorAGuardar.id!!.toInt())
            Assert.assertEquals(2, vectorRecuperado.id!!)
        }

        @Test
        fun testAlRecuperarUnIDInexistenteRetornaNull(){
            val vectorRecuperado = vectorDAO.recuperar(42)
            Assert.assertEquals(null, vectorRecuperado)
        }

//        @After
//        open fun eliminarTodo(){
//            vectorDAO.()
//        }
        @After
         fun eliminarTodo(){
    val dataDrop = HibernateDataDAO()
    dataDrop.clear()
}

}