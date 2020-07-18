package ar.edu.unq.eperdemic.persistencia.dao.Redis

import java.util.HashMap

class RedisAntidotoDao {
    var connectRedis= ConnectRedis()

    fun crearAntidoto(nombreAntidoto:String, nombreEspecie:String){
        val map: MutableMap<String, String> = HashMap()
        map["antidoto"] = nombreAntidoto

        connectRedis.syncCommands.hmset("Especie:$nombreEspecie",map)
    }
    fun getNombre(nombreEspecie:String):String{
       return connectRedis.syncCommands.hget("Especie:$nombreEspecie","antidoto")
    }


}