package ar.edu.unq.eperdemic.utils.neo4j


import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.modelo.exception.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.Neo4jDataService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.neo4j.ogm.utils.asParam

class UbicacionNeo4jTest {
    lateinit var neo4jData: Neo4jDataService
    lateinit var hibernateDataService: HibernateDataService

    var ubicacionService=UbicacionServiceImpl(HibernateUbicacionDAO())
    var ubicacionTibetDojo=Ubicacion()
    var vectorService=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO())
    var vector= Vector()
    var vectorAnimal=Vector()

    //funciones auxiliares para "resolver" el problema de null safety checking entre Kotlin y Mockito, osea para usar "any"
    private fun <T> uninitialized(): T = null as T
    private fun <T> any(classMetadata: Class<T>): T {
        Mockito.any<T>(classMetadata)
        return uninitialized()
    }

    @Before
    fun setUp() {
        neo4jData = Neo4jDataService()
        hibernateDataService = HibernateDataService()
        neo4jData.crearSetDeDatosIniciales()
        ubicacionService.crearUbicacion("BichoLandia")
        ubicacionService.crearUbicacion("Florencio Varela")
        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
        val mdq = ubicacionService.crearUbicacion("Mar Del Plata")
        vector.ubicacion = mdq
        vectorAnimal.ubicacion = mdq
        vector.tipo = Humano()
        vectorAnimal.tipo = Animal()
        vector.estado = Sano()
        vectorAnimal.estado = Sano()
        vectorService.crearVector(vector)
        vectorService.crearVector(vectorAnimal)
    }

//    @Test
//    fun vectorQuiereMoverAUbicacionNoAledaña() {
//        var daoNeo4j = Neo4jUbicacionDAO()
//        TransactionRunner.addNeo4j().runTrx {
//            daoNeo4j.conectar("Plantalandia", "TibetDojo", "Terrestre")
//        }
//        ubicacionService.crearUbicacion("BichoLandia")
//        ubicacionService.crearUbicacion("Florencio Varela")
//
//        ubicacionTibetDojo = ubicacionService.crearUbicacion("TibetDojo")
//        vector.ubicacion = ubicacionService.crearUbicacion("Plantalandia")
//        vector.tipo = Humano()
//        vector.estado = Sano()
//        var id = vectorService.crearVector(vector).id
//        if (id != null) {
//            ubicacionService.mover(id.toInt(), "TibetDojo")
//        }

    @Test
    fun vectorMueveAUbicacionAledania() {
        ubicacionService.conectar("Mar Del Plata","Quilmes","Terrestre")
        ubicacionService.mover(vector.id?.toInt()!!,"Quilmes")

        Assert.assertEquals("Quilmes", vectorService.recuperarVector(vector.id!!.toInt()).ubicacion!!.nombreUbicacion)
    }

    @Test(expected = UbicacionMuyLejana::class)
    fun vectorNoPuedeMoverPorqueUbicacionEsLejana() {
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")
        vector.ubicacion = babilonia
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(babilonia)
        }
        ubicacionService.mover(vector.id!!.toInt(),"Narnia")
    }

    @Test(expected = NoExisteUbicacion::class)
    fun vectorNoPuedeMoverAUnaUbicacionQueNoEstaPersistida() {
        ubicacionService.mover(vector.id!!.toInt(),"")
    }

    @Test(expected = CaminoNoSoportado::class)
    fun vectorHumanoNoPuedeMoversePorCaminoAereoNoSoportado() {
        ubicacionService.conectar("Mar Del Plata","Florencio Varela", "Aereo")
        ubicacionService.mover(vector.id!!.toInt(), "Florencio Varela")
    }

    @Test
    fun vectorAnimalMuevePorCaminoMaritimo(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        vectorAnimal.ubicacion = jamaica
        jamaica.vectores.add(vectorAnimal)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(jamaica)
        }
        ubicacionService.conectar("Jamaica", "Florencio Varela","Maritimo")
        ubicacionService.mover(vectorAnimal.id!!.toInt(),"Florencio Varela")
        val vectorAnimalActualizado = vectorService.recuperarVector(vectorAnimal.id!!.toInt())

        val jamaicaActualizada = ubicacionService.recuperarUbicacion("Jamaica")
        Assert.assertTrue(jamaicaActualizada.vectores.isEmpty())
        Assert.assertEquals("Florencio Varela", vectorAnimalActualizado.ubicacion!!.nombreUbicacion)
        val florencioVarelaActualizado = ubicacionService.recuperarUbicacion("Florencio Varela")
        Assert.assertEquals(1, florencioVarelaActualizado.vectores.size)
        Assert.assertNotNull(florencioVarelaActualizado.vectores.find { it.id!! == vectorAnimal.id!! })
    }

    @Test(expected = NullPointerException::class)
    fun unVectorNoPersistidoIntentaMoverMasCorto() {
        val quilmes = ubicacionService.recuperarUbicacion("Quilmes")
        val vectorsito = Vector()
        ubicacionService.moverMasCorto(vectorsito.id!!, quilmes.nombreUbicacion)
    }

    @Test(expected = NoExisteUbicacion::class)
    fun intentaMoverMasCortoAUnaUbicacionNoPersistida() {
        val fakeLocation = Ubicacion()
        fakeLocation.nombreUbicacion="CalleFalsa123"
        val narnia = ubicacionService.recuperarUbicacion("Narnia")
        vector.ubicacion = narnia
        vectorService.crearVector(vector)
        ubicacionService.moverMasCorto(vector.id!!, fakeLocation.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun intentaMoverMasCortoPeroNoHayCaminosQueLleguenAEsaUbicacion() {
        val quilmes = ubicacionService.recuperarUbicacion("Quilmes")
        val nonePlace = ubicacionService.recuperarUbicacion("elNodoSolitario")
        vector.ubicacion = quilmes
        quilmes.vectores.add(vector)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(quilmes)
        }
        ubicacionService.moverMasCorto(vector.id!!, nonePlace.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun unVectorHumanoIntentaMoverMasCortoPeroNoHayCaminosDeSusTiposViables() {
        val mordor = ubicacionService.recuperarUbicacion("Mordor")
        val zion = ubicacionService.recuperarUbicacion("Zion")
        vector.ubicacion = mordor
        mordor.vectores.add(vector)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(mordor)
        }
        ubicacionService.moverMasCorto(vector.id!!, zion.nombreUbicacion)
    }

    @Test(expected = UbicacionNoAlcanzable::class)
    fun unVectorInsectoIntentaMoverMasCortoPeroNoHayCaminosDeSusTiposViables() {
        val zion = ubicacionService.recuperarUbicacion("Zion")
        val ezpeleta = ubicacionService.recuperarUbicacion("Ezpeleta")
        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = zion
        zion.vectores.add(vectorInsecto)
        vectorService.crearVector(vectorInsecto)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(zion)
        }
        ubicacionService.moverMasCorto(vectorInsecto.id!!, ezpeleta.nombreUbicacion)
    }

    @Test
    fun unVectorHumanoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosibleConMocks() {
        //la combinacion mas corta es: zion>babilonia>ezpeleta>mordor
        //de zion a mordor

        val zion = ubicacionService.recuperarUbicacion("Zion")
        val mordor = ubicacionService.recuperarUbicacion("Mordor")

        vector.ubicacion = zion
        val vectorSpy = Mockito.spy(vector)
        zion.vectores.add(vectorSpy)
        vectorService.crearVector(vectorSpy)

        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(zion)
        }

        val babiloniaSpy = Mockito.spy(ubicacionService.recuperarUbicacion("Babilonia"))
        val ezpeletaSpy = Mockito.spy(ubicacionService.recuperarUbicacion("Ezpeleta"))
        val mordorSpy = Mockito.spy(mordor)
        val vectorDAOMock: HibernateVectorDAO = Mockito.mock(HibernateVectorDAO::class.java)
        val hibernateUbicacionDAOSpy = Mockito.spy(HibernateUbicacionDAO())
        val neo4jUbicacionDAO = Neo4jUbicacionDAO()
        neo4jUbicacionDAO.hibernateUbicacionDAO = hibernateUbicacionDAOSpy
        ubicacionService = UbicacionServiceImpl(hibernateUbicacionDAOSpy)
        ubicacionService.neo4jUbicacionDAO = neo4jUbicacionDAO
        ubicacionService.vectorDao = vectorDAOMock

        Mockito.`when`(vectorDAOMock.recuperar(Mockito.anyInt())).thenReturn(vectorSpy)

        TransactionRunner.addHibernate().runTrx {
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(babiloniaSpy)
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(ezpeletaSpy)
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(mordorSpy)
        }
        val nombreBabiloniaSpy = babiloniaSpy.nombreUbicacion
        val nombreEzpeletaSpy = ezpeletaSpy.nombreUbicacion
        val nombreMordorSpy = mordorSpy.nombreUbicacion
        TransactionRunner.addHibernate().runTrx {
            Mockito.doReturn(babiloniaSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreBabiloniaSpy)
            Mockito.doReturn(ezpeletaSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreEzpeletaSpy)
            Mockito.doReturn(mordorSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreMordorSpy)
        }

        ubicacionService.moverMasCorto(vectorSpy.id!!, mordorSpy.nombreUbicacion)

        Mockito.verify(vectorSpy, Mockito.times(1)).ubicacion = babiloniaSpy
        Mockito.verify(vectorSpy, Mockito.times(1)).ubicacion = ezpeletaSpy
        Mockito.verify(vectorSpy, Mockito.times(1)).ubicacion = mordorSpy
        Mockito.verify(vectorSpy, Mockito.times(3)).ubicacion = Mockito.any(Ubicacion::class.java)

        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorSpy, nombreBabiloniaSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorSpy, nombreEzpeletaSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorSpy, nombreMordorSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(3)).mover(any(Vector::class.java), Mockito.anyString())

        //verifico que el vector termina en mordor
        Assert.assertEquals(mordorSpy.nombreUbicacion, vectorSpy.ubicacion!!.nombreUbicacion)
    }

    @Test
    fun unVectorHumanoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosibleSinMocks() {
        //de zion a mordor
        //la combinacion mas corta es: zion>babilonia>ezpeleta>mordor

        var zion = ubicacionService.recuperarUbicacion("Zion")
        val mordor = ubicacionService.recuperarUbicacion("Mordor")

        zion.vectores.add(vector)
        vector.ubicacion = zion

        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(zion)
        }

        ubicacionService.moverMasCorto(vector.id!!, mordor.nombreUbicacion)

        vector = vectorService.recuperarVector(vector.id!!.toInt())
        //verifico que el vector termina en mordor
        Assert.assertEquals(mordor.nombreUbicacion, vector.ubicacion!!.nombreUbicacion)

        zion = ubicacionService.recuperarUbicacion(zion.nombreUbicacion)
        //verifico que el vector no esta mas en zion
        Assert.assertNull(zion.vectores.find { it.id == vector.id!! })
    }

    @Test
    fun unVectorInsectoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosibleConMocks() {
        //de narnia a babilonia
        //el camino mas corto seria: narnia>quilmes>babilonia

        val narnia = ubicacionService.recuperarUbicacion("Narnia")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")

        val vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = narnia
        val vectorInsectoSpy = Mockito.spy(vectorInsecto)
        vectorService.crearVector(vectorInsectoSpy)
        narnia.vectores.add(vectorInsectoSpy)

        val neo4jDataDao = Neo4jDataDAO()
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            HibernateUbicacionDAO().actualizar(narnia)
            neo4jDataDao.conectUni("Narnia", "Quilmes", "Terrestre")
        }

        val quilmesSpy = Mockito.spy(ubicacionService.recuperarUbicacion("Quilmes"))
        val babiloniaSpy = Mockito.spy(babilonia)
        val vectorDAOMock: HibernateVectorDAO = Mockito.mock(HibernateVectorDAO::class.java)
        val hibernateUbicacionDAOSpy = Mockito.spy(HibernateUbicacionDAO())
        val neo4jUbicacionDAO = Neo4jUbicacionDAO()
        neo4jUbicacionDAO.hibernateUbicacionDAO = hibernateUbicacionDAOSpy
        ubicacionService = UbicacionServiceImpl(hibernateUbicacionDAOSpy)
        ubicacionService.neo4jUbicacionDAO = neo4jUbicacionDAO
        ubicacionService.vectorDao = vectorDAOMock

        Mockito.`when`(vectorDAOMock.recuperar(Mockito.anyInt())).thenReturn(vectorInsectoSpy)

        TransactionRunner.addHibernate().runTrx {
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(quilmesSpy)
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(babiloniaSpy)
        }
        val nombreQuilmesSpy = quilmesSpy.nombreUbicacion
        val nombreBabiloniaSpy = babiloniaSpy.nombreUbicacion
        TransactionRunner.addHibernate().runTrx {
            Mockito.doReturn(quilmesSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreQuilmesSpy)
            Mockito.doReturn(babiloniaSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreBabiloniaSpy)
        }

        ubicacionService.moverMasCorto(vectorInsectoSpy.id!!, babiloniaSpy.nombreUbicacion)

        Mockito.verify(vectorInsectoSpy, Mockito.times(1)).ubicacion = quilmesSpy
        Mockito.verify(vectorInsectoSpy, Mockito.times(1)).ubicacion = babiloniaSpy
        Mockito.verify(vectorInsectoSpy, Mockito.times(2)).ubicacion = Mockito.any(Ubicacion::class.java)

        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorInsectoSpy, nombreQuilmesSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorInsectoSpy, nombreBabiloniaSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(2)).mover(any(Vector::class.java), Mockito.anyString())

        //verifico que el vector termina en babilonia
        Assert.assertEquals(babiloniaSpy.nombreUbicacion, vectorInsectoSpy.ubicacion!!.nombreUbicacion)
    }

    @Test
    fun unVectorInsectoIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosibleSinMocks() {
        //de narnia a babilonia
        //el camino mas corto seria: narnia>quilmes>babilonia

        var narnia = ubicacionService.recuperarUbicacion("Narnia")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")

        var vectorInsecto = Vector()
        vectorInsecto.tipo = Insecto()
        vectorInsecto.ubicacion = narnia
        narnia.vectores.add(vectorInsecto)

        val neo4jDataDao = Neo4jDataDAO()
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            HibernateUbicacionDAO().actualizar(narnia)
            neo4jDataDao.conectUni("Narnia", "Quilmes", "Terrestre")
        }

        ubicacionService.moverMasCorto(vectorInsecto.id!!, babilonia.nombreUbicacion)

        vectorInsecto = vectorService.recuperarVector(vectorInsecto.id!!.toInt())
        //verifico que el vector termina en babilonia
        Assert.assertEquals(babilonia.nombreUbicacion, vectorInsecto.ubicacion!!.nombreUbicacion)

        narnia = ubicacionService.recuperarUbicacion(narnia.nombreUbicacion)
        //verifico que el vector no esta mas en narnia
        Assert.assertNull(narnia.vectores.find { it.id == vectorInsecto.id!! })
    }

    @Test
    fun unVectorAnimalIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosibleConMocks() {
        //de mordor a babilonia
        //tiene que hacer: mordor>zion>babilonia

        val mordor = ubicacionService.recuperarUbicacion("Mordor")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")

        vectorAnimal.ubicacion = mordor
        val vectorAnimalSpy = Mockito.spy(vectorAnimal)
        mordor.vectores.add(vectorAnimalSpy)

        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(mordor)
        }

        val zionSpy = Mockito.spy(ubicacionService.recuperarUbicacion("Zion"))
        val babiloniaSpy = Mockito.spy(babilonia)
        val vectorDAOMock: HibernateVectorDAO = Mockito.mock(HibernateVectorDAO::class.java)
        val hibernateUbicacionDAOSpy = Mockito.spy(HibernateUbicacionDAO())
        val neo4jUbicacionDAO = Neo4jUbicacionDAO()
        neo4jUbicacionDAO.hibernateUbicacionDAO = hibernateUbicacionDAOSpy
        ubicacionService = UbicacionServiceImpl(hibernateUbicacionDAOSpy)
        ubicacionService.neo4jUbicacionDAO = neo4jUbicacionDAO
        ubicacionService.vectorDao = vectorDAOMock

        Mockito.`when`(vectorDAOMock.recuperar(Mockito.anyInt())).thenReturn(vectorAnimalSpy)

        TransactionRunner.addHibernate().runTrx {
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(zionSpy)
            Mockito.doNothing().`when`(hibernateUbicacionDAOSpy).actualizar(babiloniaSpy)
        }
        val nombreZionSpy = zionSpy.nombreUbicacion
        val nombreBabiloniaSpy = babiloniaSpy.nombreUbicacion
        TransactionRunner.addHibernate().runTrx {
            Mockito.doReturn(zionSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreZionSpy)
            Mockito.doReturn(babiloniaSpy).`when`(hibernateUbicacionDAOSpy).recuperar(nombreBabiloniaSpy)
        }

        ubicacionService.moverMasCorto(vectorAnimalSpy.id!!, babiloniaSpy.nombreUbicacion)

        Mockito.verify(vectorAnimalSpy, Mockito.times(1)).ubicacion = zionSpy
        Mockito.verify(vectorAnimalSpy, Mockito.times(1)).ubicacion = babiloniaSpy
        Mockito.verify(vectorAnimalSpy, Mockito.times(2)).ubicacion = Mockito.any(Ubicacion::class.java)

        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorAnimalSpy, nombreZionSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(1)).mover(vectorAnimalSpy, nombreBabiloniaSpy)
        Mockito.verify(hibernateUbicacionDAOSpy, Mockito.times(2)).mover(any(Vector::class.java), Mockito.anyString())

        //verifico que el vector termina en babilonia
        Assert.assertEquals(babiloniaSpy.nombreUbicacion, vectorAnimalSpy.ubicacion!!.nombreUbicacion)
    }

    @Test
    fun unVectorAnimalIntentaMoverMasCortoYMueveLaCombinacionMasCortaPosibleSinMocks() {
        //de mordor a babilonia
        //tiene que hacer: mordor>zion>babilonia

        var mordor = ubicacionService.recuperarUbicacion("Mordor")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")

        vectorAnimal.ubicacion = mordor
        mordor.vectores.add(vectorAnimal)

        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(mordor)
        }

        ubicacionService.moverMasCorto(vectorAnimal.id!!, babilonia.nombreUbicacion)

        vectorAnimal = vectorService.recuperarVector(vectorAnimal.id!!.toInt())
        //verifico que el vector termina en babilonia
        Assert.assertEquals(babilonia.nombreUbicacion, vectorAnimal.ubicacion!!.nombreUbicacion)

        mordor = ubicacionService.recuperarUbicacion(mordor.nombreUbicacion)
        //verifico que el vector no esta mas en narnia
        Assert.assertNull(mordor.vectores.find { it.id == vectorAnimal.id!! })
    }

    @Test (expected = CaminoNoSoportado::class)
    fun vectorHumanoQuiereMoverPorCaminoAereo(){
        var vectorHumano= Vector()
        vectorHumano.tipo=Humano()
        vectorHumano.estado=Sano()
        vectorHumano.ubicacion=ubicacionService.recuperarUbicacion("Quilmes")
        vectorHumano= vectorService.crearVector(vectorHumano)
        ubicacionService.mover(vectorHumano.id?.toInt()!!,"Babilonia")
    }

    @Test
    fun vectorInsectoMuevePorCaminoAereo(){
        var vectorInsecto= Vector()
        vectorInsecto.tipo=Insecto()
        vectorInsecto.estado=Sano()
        vectorInsecto.ubicacion=ubicacionService.recuperarUbicacion("Quilmes")
        vectorInsecto= vectorService.crearVector(vectorInsecto)
        ubicacionService.mover(vectorInsecto.id?.toInt()!!,"Babilonia")
        Assert.assertEquals(vectorService.recuperarVector(vectorInsecto.id?.toInt()!!).ubicacion?.nombreUbicacion,"Babilonia")
    }

    @Test
    fun vectorAnimalMuevePorCaminoAereo(){
        var vectorAnimal= Vector()
        vectorAnimal.tipo=Insecto()
        vectorAnimal.estado=Sano()
        vectorAnimal.ubicacion=ubicacionService.recuperarUbicacion("Quilmes")
        vectorAnimal= vectorService.crearVector(vectorAnimal)
        ubicacionService.mover(vectorAnimal.id?.toInt()!!,"Babilonia")
        Assert.assertEquals(vectorService.recuperarVector(vectorAnimal.id?.toInt()!!).ubicacion?.nombreUbicacion,"Babilonia")

    }
    @Test
    fun vectorAnimalMuevePorCaminoAereoYDespuesPorTerrestre(){
        var vectorAnimal= Vector()
        vectorAnimal.tipo=Insecto()
        vectorAnimal.estado=Sano()
        vectorAnimal.ubicacion=ubicacionService.recuperarUbicacion("Zion")
        vectorAnimal= vectorService.crearVector(vectorAnimal)
        ubicacionService.mover(vectorAnimal.id?.toInt()!!,"Mordor")
        Assert.assertEquals(vectorService.recuperarVector(vectorAnimal.id?.toInt()!!).ubicacion?.nombreUbicacion,"Mordor")
        ubicacionService.mover(vectorAnimal.id?.toInt()!!,"Narnia")
        Assert.assertEquals(vectorService.recuperarVector(vectorAnimal.id?.toInt()!!).ubicacion?.nombreUbicacion,"Narnia")
    }
    @Test
    fun ubicacionMerloConectaAUbicacionLaMatanzaPorCaminoTerrestre(){
        var ubicacion=ubicacionService.crearUbicacion("Merlo")
        var ubicacion1=ubicacionService.crearUbicacion("La Matanza")
        ubicacionService.conectar(ubicacion.nombreUbicacion,ubicacion1.nombreUbicacion,"Terrestre")
        Assert.assertEquals(ubicacionService.conectados("Merlo")[0].nombreUbicacion,"La Matanza")
    }
    @Test
    fun ubicacionMerloConectaAUbicacionLaMatanzaPorCaminoMaritimo(){
        var ubicacion=ubicacionService.crearUbicacion("Merlo")
        var ubicacion1=ubicacionService.crearUbicacion("La Matanza")
        ubicacionService.conectar(ubicacion.nombreUbicacion,ubicacion1.nombreUbicacion,"Maritimo")
        Assert.assertEquals(ubicacionService.conectados("Merlo")[0].nombreUbicacion,"La Matanza")
    }
    @Test(expected = CaminoNoSoportado::class)
    fun ubicacionMerloConectaAUbicacionLaMatanzaPorCaminoAereoVectorTrataDeMoverPeroNoPuedePorNoSerCaminoNoSoportado(){
        var ubicacion=ubicacionService.crearUbicacion("Merlo")
        var ubicacion1=ubicacionService.crearUbicacion("La Matanza")
        ubicacionService.conectar(ubicacion.nombreUbicacion,ubicacion1.nombreUbicacion,"Aereo")
        Assert.assertEquals(ubicacionService.conectados("Merlo")[0].nombreUbicacion,"La Matanza")
        var vectorHumano= Vector()
        vectorHumano.tipo=Humano()
        vectorHumano.estado=Sano()
        vectorHumano.ubicacion=ubicacionService.recuperarUbicacion("Merlo")
        vectorHumano= vectorService.crearVector(vectorHumano)
        ubicacionService.mover(vectorHumano.id?.toInt()!!,"La Matanza")
    }
    @Test(expected = CaminoNoSoportado::class)
    fun ubicacionMerloConectaAUbicacionLaMatanzaPorCaminoMaritimoVectorTrataDeMoverPeroNoPuedePorNoSerCaminoNoSoportado(){
        var ubicacion=ubicacionService.crearUbicacion("Merlo")
        var ubicacion1=ubicacionService.crearUbicacion("La Matanza")
        ubicacionService.conectar(ubicacion.nombreUbicacion,ubicacion1.nombreUbicacion,"Maritimo")
        Assert.assertEquals(ubicacionService.conectados("Merlo")[0].nombreUbicacion,"La Matanza")
        var vectorInsecto= Vector()
        vectorInsecto.tipo=Insecto()
        vectorInsecto.estado=Sano()
        vectorInsecto.ubicacion=ubicacionService.recuperarUbicacion("Merlo")
        vectorInsecto= vectorService.crearVector(vectorInsecto)
        ubicacionService.mover(vectorInsecto.id?.toInt()!!,"La Matanza")
    }
    @Test
    fun ubicacionMerloConectaAUbicacionLaMatanzaPorCaminoMaritimoYALacrozePorAereo() {
        var ubicacion = ubicacionService.crearUbicacion("Merlo")
        var ubicacion1 = ubicacionService.crearUbicacion("La Matanza")
        var ubicacion2 = ubicacionService.crearUbicacion("Lacroze")

        ubicacionService.conectar(ubicacion.nombreUbicacion, ubicacion1.nombreUbicacion, "Maritimo")
        ubicacionService.conectar(ubicacion.nombreUbicacion, ubicacion2.nombreUbicacion, "Aereo")

        Assert.assertEquals(ubicacionService.conectados("Merlo")[1].nombreUbicacion, "La Matanza")
        Assert.assertEquals(ubicacionService.conectados("Merlo")[0].nombreUbicacion, "Lacroze")

    }


    @Test
    fun seRecuperaUnNodoUbicacionPersistidoEnNeo4j() {
        var sut = Neo4jUbicacionDAO()
        val res = TransactionRunner.addNeo4j().runTrx { sut.recuperar("Quilmes") }
        Assert.assertTrue(res is Ubicacion)
        Assert.assertEquals("Quilmes", res.nombreUbicacion)

    }
        @After
    fun eliminarTodo() {
        neo4jData.eliminarTodo()
        hibernateDataService.eliminarTodo()
    }
}
