package ar.edu.unq.eperdemic.utils.redis

import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exception.AnalisisDeSangreImposibleHacer
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisADNDao
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.MegalodonService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class Redis {
    lateinit var  antidotoServiceImpl: AntidotoServiceImpl
    lateinit var vectorServiceImpl : VectorService
    lateinit var ubicacionServiceImpl : UbicacionService
    lateinit var patogenoService : PatogenoService
    lateinit var vector :Vector
    lateinit var especieCreada : Especie
    lateinit var patogeno : Patogeno
    lateinit var antidoto : Antidoto
    lateinit var florencioVarela : Ubicacion
    lateinit var redisAdnDao :RedisADNDao
    @Before
    fun setUp(){
        var redisAdnDao =RedisADNDao()
        antidotoServiceImpl=AntidotoServiceImpl()
        vectorServiceImpl=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO(),FeedServiceImpl(FeedMongoDAO()))
        ubicacionServiceImpl=UbicacionServiceImpl(HibernateUbicacionDAO())
        patogenoService=PatogenoServiceImpl(HibernatePatogenoDAO(),HibernateEspecieDAO())
        patogeno=Patogeno()
        patogeno.tipo="Bacteria"
        vector= Vector()
        florencioVarela= ubicacionServiceImpl.crearUbicacion("Florencio Varela")
        vector.ubicacion=florencioVarela
        vector.tipo= Humano()
        patogenoService.crearPatogeno(patogeno);
        especieCreada= patogenoService.agregarEspecie(patogeno.id!!, "Covid-19", "China")
        antidoto = Antidoto("quedateEnCasa",especieCreada)

        vectorServiceImpl.crearVector(vector)
        antidotoServiceImpl.CrearAntidoto(antidoto)
    }

    @Test(expected = AnalisisDeSangreImposibleHacer::class)
    fun vectorVaAlMedicoPeroEsImposibleHacerElAnilisis(){
        vectorServiceImpl.infectar(vector,patogenoService.recuperarEspecie(especieCreada.id!!))
        Thread.sleep(1001)
        vectorServiceImpl.irAlMedico(vector, patogenoService.recuperarEspecie(especieCreada.id!!))
    }

    @Test
    fun vectorVaAlMedicoAntesDeQueSeaImposibleHacerElAnilisis(){
        var redisAdnDao =RedisADNDao()
        vectorServiceImpl.infectar(vector,patogenoService.recuperarEspecie(especieCreada.id!!))
        Thread.sleep(50)
        vectorServiceImpl.irAlMedico(vector, patogenoService.recuperarEspecie(especieCreada.id!!))
        var vectorCurado= vectorServiceImpl.recuperarVector(vector.id?.toInt()!!)
        Assert.assertTrue(vectorCurado.estado is Sano)
    }

    @Test(expected = AnalisisDeSangreImposibleHacer::class)
    fun vectorVaAlMedicoPeroEsImposibleHacerElAnilisisScript(){
        vectorServiceImpl.infectar(vector,patogenoService.recuperarEspecie(especieCreada.id!!))
        Thread.sleep(1001)
        vectorServiceImpl.irAlMedico2(vector, patogenoService.recuperarEspecie(especieCreada.id!!))
    }

    @Test
    fun vectorVaAlMedicoAntesDeQueSeaImposibleHacerElAnilisisScript(){
        var redisAdnDao =RedisADNDao()
        vectorServiceImpl.infectar(vector,patogenoService.recuperarEspecie(especieCreada.id!!))
        Thread.sleep(50)
        vectorServiceImpl.irAlMedico2(vector, patogenoService.recuperarEspecie(especieCreada.id!!))
        var vectorCurado= vectorServiceImpl.recuperarVector(vector.id?.toInt()!!)
        Assert.assertTrue(vectorCurado.estado is Sano)
    }


    /*
    @Test
    fun Scrip(){
        var script='local val = "Hola IBM Cloud" return val'
        var redisDAO=ConnectRedis()
        redisDAO.syncCommands.eval(script, o)
    }
    */

    @After
    fun dropAll(){
        MegalodonService().eliminarTodo()
    }
}

