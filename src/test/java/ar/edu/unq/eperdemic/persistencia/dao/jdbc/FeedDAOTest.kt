package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Arribo
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoPatogeno
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.utils.DataService
import com.mongodb.client.model.Filters
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

        evento = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.VIRUS.name)
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
    }

    @Test
    fun `prueba de concepto - Test commit`(){
        this.dropAll()
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
        val resultado0 = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertEquals(1, resultado0.size)
    }

    @Test
    fun `prueba de concepto - Comportamiento del OR para subquery (Habiendo uno solo que  cumple)`(){
        dao.deleteAll()
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

        var resultadoOR = dao.find(Filters.or(Filters.eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name), Filters.eq("accion", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)))
        Assert.assertEquals(1, resultadoOR.size)
    }

    @Test
    fun `prueba de concepto - Comportamiento del OR para subquery (Habiendo muchos)`(){
        dao.deleteAll()
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
        vectorService.infectar(vectorBabilonico, especie)
        vectorService.infectar(vectorBabilonico, especie)
        vectorService.infectar(vectorBabilonico, especie)

        var resultadoOR = dao.find(Filters.or(Filters.eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name), Filters.eq("accion", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)))
        Assert.assertEquals(4, resultadoOR.size)
    }

    @Test
    fun `prueba de concepto - Comportamiento del OR para subquery (Habiendo uno solo que cumple y muchos que no)`(){
        dao.deleteAll()
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
        vectorService.infectar(vectorBabilonico, especie)
        dao.startTransaction()
        repeat(15) {//Ya hay uno
            dao.save(Evento(1, Arribo(), "otra accion", TipoPatogeno.VIRUS.name))//El mismo tipo de patogeno.
        }
        dao.commit()
        var cantTotal = dao.findEq("tipoPatogeno",TipoPatogeno.VIRUS.name).size
        Assert.assertEquals(16, cantTotal)
        var resultadoOR = dao.find(Filters.or(Filters.eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name), Filters.eq("accion", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)))
        Assert.assertEquals(1, resultadoOR.size)
    }

    @Test
    fun `prueba de concepto - Comportamiento del AND para subquery - Imposible arroje un resultado`(){
        dao.deleteAll()
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

        var resultadoOR = dao.find(Filters.and(Filters.eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name), Filters.eq("accion", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)))
        Assert.assertEquals(0, resultadoOR.size)
    }

    @Test
    fun `prueba de concepto - Comportamiento del *OR*AND* para subquery (Habiendo uno solo que cumple y muchos que no)`(){
        dao.deleteAll()
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
        vectorService.infectar(vectorBabilonico, especie)
        dao.startTransaction()
        var n = 0
        repeat(15) {//Ya hay uno
            dao.save(Evento(n++, Arribo(), "otra accion", TipoPatogeno.VIRUS.name))//El mismo tipo de patogeno.
        }
        repeat(15) {
            dao.save(Evento(++n, Arribo(), "alguna otra accion", TipoPatogeno.BACTERIA.name))//El mismo tipo de patogeno.
        }
        repeat(15) {
            dao.save(Evento(++n, Arribo(), "no me preguntes a mi, solo soy un string AH-ja", TipoPatogeno.HONGO.name))//El mismo tipo de patogeno.
        }
        dao.commit()
        //Esto asi escrito me trae t0d0s los envento que haya sido generado por una Accion de Pandemia **O** Por Contagio por Primera vez) **Y** sea del tipo de patogeno Virus
        //
        var resultadoOR = dao.find(
                                Filters.and(
                                    Filters.or(
                                            Filters.eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name),
                                            Filters.eq("accionQueLoDesencadena", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)),
                                   Filters.eq("tipoPatogeno", TipoPatogeno.VIRUS.name)))
        Assert.assertEquals(1, resultadoOR.size)
    }

    //Test de dominio:

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
        val evento = eventoFactory.eventoContagioPorPandemia(evento.tipoPatogeno!!)
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
        val pivote = dao.getByTipoPatogeno(TipoPatogeno.VIRUS.name)
        Assert.assertEquals(1, result.size)
        val unicoEvento = result.get(0)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
        Assert.assertEquals(patogenoModel.tipo, unicoEvento.tipoPatogeno)
        Assert.assertEquals(1, unicoEvento.n)
    }


    @After
    fun dropAll() {
        dao.deleteAll()
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            Neo4jDataDAO().clear()
        }
    }
}