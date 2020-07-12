package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Antidoto
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.AnalisisDeSangreImposibleHacer
import ar.edu.unq.eperdemic.persistencia.dao.Redis.ConnectRedis
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisDao
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.Assert
import org.junit.Test

class redis {
    var vectorServiceImpl=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO(),FeedServiceImpl(FeedMongoDAO()))
    var ubicacionServiceImpl=UbicacionServiceImpl(HibernateUbicacionDAO())
    var redisDao=RedisDao()
    @Test(expected = AnalisisDeSangreImposibleHacer::class)
    fun test(){
        var especie=Especie()
        var vector= Vector()
        var antidoto=Antidoto("quedateEnCasa")
        var florencioVarela= ubicacionServiceImpl.crearUbicacion("Florencio Varela")
        vector.ubicacion=florencioVarela
        vector.tipo= Humano()
        especie.nombre="Covic-19"
        vector.infectarse(especie)
        var vectorCreado=vectorServiceImpl.crearVector(vector)
        Assert.assertTrue(vectorCreado.especies.contains(especie))

        vectorServiceImpl.irAlMedico(vector,especie)
       // vectorServiceImpl.tomarAntidoto(antidoto.nombre,especie,vector)
        var vectorActualizado= vectorServiceImpl.recuperarVector(vector.id?.toInt()!!)
        Assert.assertFalse(vectorActualizado.especies.contains(especie))
        Assert.assertEquals(vectorActualizado.estado.toString(),"Sano")
    }

}