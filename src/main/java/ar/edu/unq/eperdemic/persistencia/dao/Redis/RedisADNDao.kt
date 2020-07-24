package ar.edu.unq.eperdemic.persistencia.dao.Redis

import io.lettuce.core.ScriptOutputType


import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.boot.autoconfigure.cache.CacheProperties
import java.util.*

import ar.edu.unq.eperdemic.persistencia.dao.ADNDAO
import java.util.HashMap

class RedisADNDao : ADNDAO{
    var connectRedis= ConnectRedis()

    override fun incorporarADNDeEspecie(vector: Vector,especie:Especie){
        val map: MutableMap<String, String> = HashMap()
        map["especie"] = "Especie:${especie.nombre}"
        connectRedis.syncCommands.hmset("AdnDe:${vector.id}",map)
        connectRedis.syncCommands.expire("AdnDe:${vector.id}",10)
    }

    override fun darAdnDeEspecie(vector: Vector):String?{
      return  connectRedis.connection.sync().hget("AdnDe:${vector.id}","especie")
    }


    override fun noExisteAdn(vector: Vector): Boolean {
        return !connectRedis.connection.sync().hexists("AdnDe:${vector.id}","especie")
    }


    fun deleteAll(){
        connectRedis
    }



}