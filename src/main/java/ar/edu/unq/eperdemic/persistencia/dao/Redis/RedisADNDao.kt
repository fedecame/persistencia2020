package ar.edu.unq.eperdemic.persistencia.dao.Redis

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import java.util.HashMap

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

    fun existeAdn(vector: Vector): Boolean {
        return darAdnDeEspecie(vector).isNullOrEmpty()
    }

    fun deleteAll(){
        connectRedis
    }


}