package ar.edu.unq.eperdemic.persistencia.dao.Redis

class RedisAntidotoDao {
    var connectRedis= ConnectRedis()

    fun crearAntidoto(nombreAntidoto:String, nombreEspecie:String){
        connectRedis.syncCommands.hmget("Antidoto:$nombreAntidoto","$nombreEspecie")
    }
    fun getNombre(nombreEspecie:String):String{
       return connectRedis.syncCommands.hget("Especie:$nombreEspecie","antidoto")
    }


}