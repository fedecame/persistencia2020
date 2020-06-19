package ar.edu.unq.eperdemic.utils.hibernate


import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.modelo.exception.MoverMismaUbicacion
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import ar.edu.unq.eperdemic.utility.random.RandomMasterImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*


class UbicacionServiceTest {
    var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
    var ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())
    val vector = Vector()
    val vector1 = Vector()
    val tipo = Humano()
    val estado = Sano()
    lateinit var ubicacionCreada: Ubicacion
    lateinit var ubicacionCreada1: Ubicacion
    lateinit var ubicacionCreada2: Ubicacion
    lateinit var ubicacionCreada3: Ubicacion
    lateinit var randomGeneratorMock: RandomMaster
    lateinit var hibernateData: HibernateDataService
    lateinit var neo4jData: Neo4jDataService

    @Before
    fun setUp(){
        neo4jData = Neo4jDataService()
        hibernateData = HibernateDataService()
        vector.tipo=tipo
        vector.estado=estado
        vector1.tipo=tipo
        vector1.estado=estado

        ubicacionCreada = ubicacionService.crearUbicacion("Florencio Varela")
        ubicacionCreada1 = ubicacionService.crearUbicacion("Berazategui")
        ubicacionCreada2 = ubicacionService.crearUbicacion("Saavedra")
        ubicacionCreada3 = ubicacionService.crearUbicacion("Don Bosco")
        randomGeneratorMock = Mockito.mock(RandomMaster::class.java)
        ubicacionService.randomGenerator = randomGeneratorMock
        ubicacionService.conectar("Florencio Varela","Quilmes","Terrestre")
        ubicacionService.conectar("Florencio Varela","Berazategui","Terrestre")
        ubicacionService.conectar("Quilmes","Berazategui","Terrestre")
        ubicacionService.conectar("Florencio Varela","Sarandi","Terrestre")
        ubicacionService.conectar("Berazategui","Florencio Varela","Terrestre")


    }

    @Test
    fun testSaavedraSeConectaConDonBoscoPorMar(){
        ubicacionService.conectar("Saavedra","Don Bosco","Maritimo")
        val listConectados =ubicacionService.conectados("Saavedra")
        Assert.assertEquals(1,listConectados.size)
        Assert.assertEquals("Don Bosco",listConectados.get(0).nombreUbicacion)
    }

    @Test
    fun testSaavedraSeconectaConVarelaPorAire(){
        ubicacionService.conectar("Saavedra","Florencio Varela","Aereo")
        val listConectados =ubicacionService.conectados("Saavedra")
        Assert.assertEquals(1,listConectados.size)
        Assert.assertEquals("Florencio Varela",listConectados.get(0).nombreUbicacion)
    }

    @Test
    fun testSaavedraSeconectaConBerazateguiPorTierra(){
        ubicacionService.conectar("Saavedra","Berazategui","Terrestre")
        val listConectados =ubicacionService.conectados("Saavedra")
        Assert.assertEquals(1,listConectados.size)
        Assert.assertEquals("Berazategui",listConectados.get(0).nombreUbicacion)
    }

    @Test
    fun testSaavedraEstaConectadoCon3Ubicaciones(){

        ubicacionService.conectar("Saavedra","Berazategui","Terrestre")
        ubicacionService.conectar("Saavedra","Florencio Varela","Aereo")
        ubicacionService.conectar("Saavedra","Don Bosco","Maritimo")

        val listConectados =ubicacionService.conectados("Saavedra")
        Assert.assertEquals(3,listConectados.size)
        Assert.assertEquals("Don Bosco",listConectados.get(0).nombreUbicacion)
        Assert.assertEquals("Florencio Varela",listConectados.get(1).nombreUbicacion)
        Assert.assertEquals("Berazategui",listConectados.get(2).nombreUbicacion)
    }

    @Test
    fun testBerazateguiEsElConectadoConVarela(){
        ubicacionService.conectar("Florencio Varela","Berazategui","Terrestre")
        val listConectados =ubicacionService.conectados("Florencio Varela")
        Assert.assertEquals(1,listConectados.size)
        Assert.assertEquals("Berazategui",listConectados.get(0).nombreUbicacion)
    }

    @Test
    fun  testVarelaSeConectaConBerazategui(){
        ubicacionService.conectar("Florencio Varela","Berazategui","Terrestre")
        val listConectados =ubicacionService.conectados("Florencio Varela")
        Assert.assertEquals(1, listConectados.size)
    }

    @Test
    fun testVarelaNoSeConectaConNingunNodo(){
        hibernateData.eliminarTodo()
        neo4jData.eliminarTodo()
        ubicacionService.crearUbicacion("Florencio Varela")
        val listConectados =ubicacionService.conectados("Florencio Varela")
        Assert.assertEquals(0, listConectados.size)
    }

    @Test
    fun creacionDeUbicacion() {
        val ubicacion2Creada= ubicacionService.crearUbicacion("Quilmes")
        Assert.assertEquals("Quilmes", ubicacion2Creada.nombreUbicacion)
        Assert.assertEquals("Quilmes", ubicacionService.recuperarUbicacion("Quilmes").nombreUbicacion)
    }

    @Test
    fun verificacionDeVectorAlojado() {
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        Assert.assertEquals(vectorService.recuperarVector(1).ubicacion?.nombreUbicacion,"Florencio Varela")
    }

    @Test
    fun alMoverEnUbicacionMueveElVectorDeseadoALaUbicacionDeseada(){
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        var vectorCreado=vectorService.recuperarVector(1)
        Assert.assertEquals(vectorCreado.ubicacion?.nombreUbicacion,"Florencio Varela")
        ubicacionService.crearUbicacion("Quilmes")
        ubicacionService.conectar("Florencio Varela", "Quilmes", "Terrestre")
        ubicacionService.mover(1,"Quilmes")
        var vectorActualizado=vectorService.recuperarVector(1)
        Assert.assertEquals(vectorActualizado.ubicacion?.nombreUbicacion,"Quilmes")
    }

    @Test (expected = NoExisteUbicacion::class)
    fun seMueveAUbicacionQueNoExiste(){
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        ubicacionService.mover(1,"Sarandi")
    }

    @Test(expected = MoverMismaUbicacion::class)
    fun alMoverAMismaUbicacionDondeEstaArrojaExcepcionPorqueNoEsLogico(){
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)

        ubicacionService.mover(1,"Florencio Varela")
    }

    @Test(expected = IDVectorNoEncontradoException::class )
    fun moverUnVectorQueNoExiste(){
        vector.ubicacion=ubicacionCreada
        ubicacionService.mover(1,"Florencio Varela")
    }


    @Test
    fun moverDosVectoresAUnaMismaUbicacion() {
        vector.ubicacion = ubicacionCreada
        vector1.ubicacion = ubicacionCreada
        vectorService.crearVector(vector)
        vectorService.crearVector(vector1)
        var ubicacionRecuperada= ubicacionService.recuperarUbicacion("Florencio Varela")
        Assert.assertEquals(ubicacionRecuperada.vectores.size,2)
        ubicacionService.mover(1, "Berazategui")
        ubicacionService.mover(2,"Berazategui")
        var ubicacionRecuperada1= ubicacionService.recuperarUbicacion("Berazategui")
        Assert.assertEquals(ubicacionRecuperada1.vectores.size,2)
    }

    @Test
    fun  alMoverVectorAlojadoEnUnaPosicionLaUbicacionTieneUnVectorMenosAlojado(){
        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        var  ubicacionCreadaActualizada=ubicacionService.recuperarUbicacion("Florencio Varela")
        ubicacionCreada1 = ubicacionService.crearUbicacion("Quilmes")
        Assert.assertEquals(ubicacionCreadaActualizada.vectores.size,1)
        ubicacionService.conectar("Florencio Varela", "Quilmes", "Terrestre")
        ubicacionService.mover(vector.id!!.toInt(),"Quilmes")
        var ubicacionRecuperada= ubicacionService.recuperarUbicacion("Florencio Varela")
        Assert.assertEquals(ubicacionRecuperada.vectores.size.toInt(),0)
    }

    @Test
    fun recuperarUbicacion(){
        var ubicacion= ubicacionService.recuperarUbicacion("Florencio Varela")
        Assert.assertEquals(ubicacion.nombreUbicacion,"Florencio Varela")
    }

    @Test(expected =  NoExisteUbicacion::class)
    fun recuperarUbicacionQueNoExiste(){
        ubicacionService.recuperarUbicacion("Avellaneda")
    }

    @Test
    fun expandirSinVectoresInfectadosEnUbicacion(){
        ubicacionService.expandir(ubicacionCreada1.nombreUbicacion)
        verifyZeroInteractions(randomGeneratorMock)
    }

    @Test
    fun `al expandir con mas de 1 vector infectado en la ubicacion, se usa el randomGenerator para elegir uno de los infectados aleatoriamente`(){
        val patogenoDelTest = Patogeno()
        patogenoDelTest.cantidadDeEspecies = 1
        patogenoDelTest.defensaContraMicroorganismos = 2
        patogenoDelTest.factorContagioAnimal = 3
        patogenoDelTest.factorContagioHumano = 999 // Asigno un factor de contagio muy grande para forzar la infeccion de los vectores!
        patogenoDelTest.factorContagioInsecto = 9
        patogenoDelTest.letalidad = 33
        patogenoDelTest.tipo = "Imaginario"

        val especieDelTest = Especie()
        especieDelTest.paisDeOrigen = "Argentina"
        especieDelTest.nombre = "Argentinitis"
        especieDelTest.cantidadInfectadosParaADN = 19
        especieDelTest.patogeno = patogenoDelTest

        val especieDelTest2 = Especie()
        especieDelTest2.paisDeOrigen = "Jamaica"
        especieDelTest2.nombre = "Rastafaritis"
        especieDelTest2.cantidadInfectadosParaADN = 22
        especieDelTest2.patogeno = patogenoDelTest

        val vectorInfectado1 = Vector()
        vectorInfectado1.tipo = Insecto()
        vectorInfectado1.agregarEspecie(especieDelTest)

        val vectorInfectado2 = Vector()
        vectorInfectado2.tipo = Insecto()
        vectorInfectado2.agregarEspecie(especieDelTest2)

        Assert.assertTrue(vectorInfectado1.estado is Infectado)
        Assert.assertTrue(vectorInfectado2.estado is Infectado)

        vector.ubicacion = ubicacionCreada1
        vectorService.crearVector(vector)
        ubicacionCreada1.vectores.add(vector)

        vector1.ubicacion = ubicacionCreada1
        vectorService.crearVector(vector1)
        ubicacionCreada1.vectores.add(vector1)

        vectorInfectado1.ubicacion=ubicacionCreada1
        vectorService.crearVector(vectorInfectado1)
        ubicacionCreada1.vectores.add(vectorInfectado1)

        vectorInfectado2.ubicacion=ubicacionCreada1
        vectorService.crearVector(vectorInfectado2)
        ubicacionCreada1.vectores.add(vectorInfectado2)

        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(ubicacionCreada1)
        }

        Assert.assertNotNull(patogenoDelTest.id)

        Assert.assertTrue(vector.estado is Sano)
        Assert.assertTrue(vector1.estado is Sano)

        Mockito.`when`(randomGeneratorMock.giveMeARandonNumberBeetween(0.0, 1.0)).thenReturn(0.0)

        ubicacionService.expandir(ubicacionCreada1.nombreUbicacion)

        Mockito.verify(randomGeneratorMock, Mockito.times(1)).giveMeARandonNumberBeetween(0.0, 1.0)

        val vectorDB = vectorService.recuperarVector(vector.id!!.toInt())
        val vector1DB = vectorService.recuperarVector(vector1.id!!.toInt())
        Assert.assertTrue(vectorDB.estado is Infectado)
        Assert.assertTrue(vector1DB.estado is Infectado)

        Assert.assertEquals(1, vectorDB.especies.size)
        Assert.assertEquals(1, vector1DB.especies.size)
    }

    @Test
    fun `al expandir con 1 vector infectado en la ubicacion y 2 vectores sanos, los sanos se infectan de las especies del infectado (forzado con factor de contagio elevado)`(){
        val patogenoDelTest = Patogeno()
        patogenoDelTest.cantidadDeEspecies = 1
        patogenoDelTest.defensaContraMicroorganismos = 2
        patogenoDelTest.factorContagioAnimal = 3
        patogenoDelTest.factorContagioHumano = 999 // Asigno un factor de contagio muy grande para forzar la infeccion de los vectores!
        patogenoDelTest.factorContagioInsecto = 9
        patogenoDelTest.letalidad = 33
        patogenoDelTest.tipo = "Imaginario"

        val especieDelTest = Especie()
        especieDelTest.paisDeOrigen = "Argentina"
        especieDelTest.nombre = "Argentinitis"
        especieDelTest.cantidadInfectadosParaADN = 19
        especieDelTest.patogeno = patogenoDelTest

        val especieDelTest2 = Especie()
        especieDelTest2.paisDeOrigen = "Jamaica"
        especieDelTest2.nombre = "Rastafaritis"
        especieDelTest2.cantidadInfectadosParaADN = 22
        especieDelTest2.patogeno = patogenoDelTest

        val vectorInfectado = Vector()
        vectorInfectado.tipo = Insecto()
        vectorInfectado.agregarEspecie(especieDelTest)
        vectorInfectado.agregarEspecie(especieDelTest2)

        Assert.assertTrue(vectorInfectado.estado is Infectado)

        //no uso al mock de random en ubicacionService porque solo hay 1 infectado en la ubicacion
        //en su lugar uso un spy para verificar que se mandan los parametros correctos al randomGenerator
        val randomGeneratorSpy = Mockito.spy(RandomMasterImpl)
        ubicacionService.randomGenerator = randomGeneratorSpy

        vector.ubicacion = ubicacionCreada1
        vectorService.crearVector(vector)
        ubicacionCreada1.vectores.add(vector)

        vector1.ubicacion = ubicacionCreada1
        vectorService.crearVector(vector1)
        ubicacionCreada1.vectores.add(vector1)

        vectorInfectado.ubicacion=ubicacionCreada1
        vectorService.crearVector(vectorInfectado)
        ubicacionCreada1.vectores.add(vectorInfectado)

        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(ubicacionCreada1)
        }

        Assert.assertTrue(vector.estado is Sano)
        Assert.assertTrue(vector1.estado is Sano)

        ubicacionService.expandir(ubicacionCreada1.nombreUbicacion)

        verify(randomGeneratorSpy, times(0)).giveMeARandonNumberBeetween(Mockito.anyDouble(), Mockito.anyDouble())

        val vectorDB = vectorService.recuperarVector(vector.id!!.toInt())
        val vector1DB = vectorService.recuperarVector(vector1.id!!.toInt())
        Assert.assertTrue(vectorDB.estado is Infectado)
        Assert.assertTrue(vector1DB.estado is Infectado)

        Assert.assertEquals(2, vectorDB.especies.size)
        Assert.assertEquals(2, vector1DB.especies.size)

        Assert.assertNotNull(vectorDB.especies.find { it.id!! == especieDelTest.id!! })
        Assert.assertNotNull(vectorDB.especies.find { it.id!! == especieDelTest2.id!! })
        Assert.assertNotNull(vector1DB.especies.find { it.id!! == especieDelTest.id!! })
        Assert.assertNotNull(vector1DB.especies.find { it.id!! == especieDelTest2.id!! })
    }

    @Test
    fun luegoDeExpandirEnUNaUbicacionSinVectoresInfectadosEnUbicacionNoHayVectoresEnEsaUbicacion(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val vectorSanoA = Vector()
        vectorSanoA.tipo = Humano()
        vectorSanoA.ubicacion = jamaica

        val vectorSanoB = Vector()
        vectorSanoB.tipo = Animal()
        vectorSanoB.ubicacion = jamaica

        val vectorSanoC = Vector()
        vectorSanoC.tipo = Insecto()
        vectorSanoC.ubicacion = jamaica

        vectorService.crearVector(vectorSanoA)
        vectorService.crearVector(vectorSanoB)
        vectorService.crearVector(vectorSanoC)

        ubicacionService.expandir("Jamaica")
        Assert.assertTrue(vectorService.recuperarVector(vectorSanoA.id!!.toInt()).estado is Sano)
        Assert.assertTrue(vectorService.recuperarVector(vectorSanoB.id!!.toInt()).estado is Sano)
        Assert.assertTrue(vectorService.recuperarVector(vectorSanoC.id!!.toInt()).estado is Sano)
    }

    @Test
    fun elExpandirEnUnaUbicacionSinNingunVectorNoProvocaNingunEfecto(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        ubicacionService.expandir("Jamaica")
        val jamaicaRecuperada = ubicacionService.recuperarUbicacion("Jamaica")
        Assert.assertTrue(jamaicaRecuperada.vectores.isEmpty())
    }

    @After
    fun eliminarTodo(){
        hibernateData.eliminarTodo()
        neo4jData.eliminarTodo()
    }
}