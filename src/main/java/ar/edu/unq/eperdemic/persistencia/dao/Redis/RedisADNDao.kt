package ar.edu.unq.eperdemic.persistencia.dao.Redis

import io.lettuce.core.ScriptOutputType


import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.boot.autoconfigure.cache.CacheProperties
import java.util.*


class RedisADNDao {
    var connectRedis= ConnectRedis()

    fun incorporarADNDeEspecie(vector: Vector,especie:Especie){
        val map: MutableMap<String, String> = HashMap()
        map["especie"] = "Especie:${especie.nombre}"
        connectRedis.syncCommands.hmset("AdnDe:${vector.id}",map)
        connectRedis.syncCommands.expire("AdnDe:${vector.id}",1)
    }

    fun darAdnDeEspecie(vector: Vector):String?{
      return  connectRedis.connection.sync().hget("AdnDe:${vector.id}","especie")
    }

    fun noExisteAdn(vector: Vector): Boolean {
        return darAdnDeEspecie(vector).isNullOrEmpty()
    }

    fun hacerTodo(vector: Vector,nombreEspecie:String): MutableList<String>? {
        //" local darAdnDeEspecie = redis.call('hget','Antidoto:quedateEnCasa','especie') return {darAdnDeEspecie}"


        var script =  "local darAdnDeEspecie = redis.call('hget','AdnDe:${vector.id}','especie')" +
                "        if  redis.call('hexists','AdnDe:${vector.id}', 'especie')==1 then local darAntidoto = redis.call('hget','Especie:$nombreEspecie','antidoto')" +
                "                local items = { darAdnDeEspecie, darAntidoto ,'sabes que si'}" +

                "return items end " +
                "               local darAntidoto = redis.call('hget','Especie:$nombreEspecie','antidoto')" +
                " local pepito=redis.call('hexists','AdnDe:${vector.id}', 'especie')"+

                "                local items={'pepito'}" +
                "return items "

        val response= connectRedis.connection.sync().eval<MutableList<String>>(script,ScriptOutputType.MULTI)
    return response

    }
    fun deleteAll(){
        connectRedis
    }


}