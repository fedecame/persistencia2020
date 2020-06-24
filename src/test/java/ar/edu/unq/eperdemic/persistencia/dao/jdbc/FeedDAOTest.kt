package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoPatogeno
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedDAOTest {
    lateinit var dao : FeedMongoDAO
    lateinit var ubicacionService : UbicacionService
    lateinit var patogenoService : PatogenoService
    lateinit var vectorService : VectorService
    lateinit var hibernateData : DataService
    lateinit var eventoFactory : EventoFactory
    lateinit var evento : Evento

    @Before
    fun setUp(){
        dao = FeedMongoDAO()
        val ubicacionDAO = HibernateUbicacionDAO()
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        vectorService = VectorServiceImpl(HibernateVectorDAO(), ubicacionDAO)
        hibernateData = HibernateDataService()
        eventoFactory = EventoFactory()

        evento = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.VIRUS.name, "gripe")
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
    }

    @Test
    fun alRecuperarPorUnTipoDePatogenoInexistenteRetornaListaVacia(){
        val resultado = dao.getByTipoPatogeno("Nisman")
        Assert.assertEquals(0, resultado.size)
    }

    @Test
    fun seGuardaYSeRecuperaPorTipoDePatogeno() {
        val resultado = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
    }

    @Test
    fun elEventoGuardoYRecuperadoPorElTipoDePatogenoEsConsistente(){
        val resultado = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertEquals(1, resultado!!.size)
        val unicoEvento = resultado.get(0)
        Assert.assertTrue(unicoEvento is Evento)
        Assert.assertNotNull(resultado)
        Assert.assertEquals(evento.tipoPatogeno, unicoEvento!!.tipoPatogeno)
        Assert.assertEquals(TipoPatogeno.VIRUS.name, unicoEvento.tipoPatogeno)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
    }

    @Test
    fun seGuardaYSeRecuperaPorTipoDeEvento() {
        val evento = eventoFactory.eventoContagioPorPandemia(evento.tipoPatogeno!!, "gripe")
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
        val resultSet = dao.getByTipoEvento(Contagio())
        val resultado = resultSet.get(0)
        Assert.assertTrue(resultado is Evento)
        Assert.assertNotNull(resultado)
        Assert.assertEquals(evento.tipoPatogeno, resultado!!.tipoPatogeno)
        Assert.assertEquals(TipoPatogeno.VIRUS.name, resultado!!.tipoPatogeno)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, resultado!!.accionQueLoDesencadena)
        Assert.assertTrue(resultado.tipoEvento is Contagio)
    }

    @Test
    fun alpedirLosEventosDeContagioDeUnPatogenoNoCreadoDevuelveUNaListaVacia(){
        val result = dao.feedPatogeno("sarasa")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoTieneUNSoloResultadoCuandoElPatogenoSoloSeVolvioPandemiaUnaUnicaVez(){
        this.dropAll()
        //Una especie se vuelve pandemia se encuentra presenta en mas de la mitad de las locaciones
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)

        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        val result = dao.feedPatogeno(TipoPatogeno.VIRUS.name)
        val pivote = dao.getByTipoPatogeno(TipoPatogeno.VIRUS.name)
        Assert.assertEquals(1, result.size)
        val unicoEvento = result.get(0)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
        Assert.assertEquals(patogenoModel.tipo, unicoEvento.tipoPatogeno)
        Assert.assertEquals(1, unicoEvento.n)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoQueTieneVariosEstosEstanOrdenadosEn(){
        this.dropAll()
        //Una especie se vuelve pandemia se encuentra presenta en mas de la mitad de las locaciones
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        val result = dao.feedPatogeno(TipoPatogeno.VIRUS.name)
        Assert.assertEquals(1, result.size)
        val unicoEvento = result.get(0)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
        Assert.assertEquals(patogenoModel.tipo, unicoEvento.tipoPatogeno)
        Assert.assertEquals(1, unicoEvento.n)
    }

    @Test
    fun elFeedPatogenoRetornaUNaListaDeEventosOrdenadasPorElAtributoNDelTipoDePatogenoIndicado(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")

        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()

        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)
        vectorService.infectar(vectorJamaiquino, especie)
        dao.startTransaction()
        var n = 0
        repeat(7) {//Ya hay uno
            dao.save(Evento(n++, Contagio(), Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, TipoPatogeno.VIRUS.name))//El mismo tipo de patogeno.
        }
        dao.commit()
        val result = dao.feedPatogeno(TipoPatogeno.VIRUS.name)
        //PosicionElemento = evento
        val uno = result.get(0)
        val dos = result.get(1)
        val tres = result.get(2)
        val cuatro = result.get(3)
        val cinco = result.get(4)
        val seis = result.get(5)
        val siete = result.get(6)
        val ocho = result.get(7)
    //Esto se cambia por Date cuando decidamos el tipo de dato
        Assert.assertEquals(8, result.size)
        Assert.assertEquals(6, uno.n)
        Assert.assertEquals(5, dos.n)
        Assert.assertEquals(4, tres.n)
        Assert.assertEquals(3, cuatro.n)
        Assert.assertEquals(2, cinco.n)
        //Uno de estos es el elemento del setup
        Assert.assertEquals(1, seis.n)
        Assert.assertEquals(1, siete.n)

        Assert.assertEquals(0, ocho.n)
    }

    @After
    fun dropAll() {
        MegalodonService().eliminarTodo()
    }
}