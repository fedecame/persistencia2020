package ar.edu.unq.eperdemic.persistencia.dao.Redis

import ar.edu.unq.eperdemic.modelo.Vector
import io.lettuce.core.ScriptOutputType

class RedisScriptDao {
    var connectRedis= ConnectRedis()

    fun todasLasQuerys(vector: Vector, nombreEspecie:String): MutableList<String>? {
        //" local darAdnDeEspecie = redis.call('hget','Antidoto:quedateEnCasa','especie') return {darAdnDeEspecie}"
        var script =  "local darAdnDeEspecie = redis.call('hget','AdnDe:${vector.id}','especie')" +
                "        if  redis.call('hexists','AdnDe:${vector.id}', 'especie')==1 and " +
                "              redis.call('hexists','Especie:$nombreEspecie','antidoto')    " +
                "then local darAntidoto = redis.call('hget','Especie:$nombreEspecie','antidoto')" +
                "                local respuestas = { darAdnDeEspecie, darAntidoto }" +

                "return respuestas end " +

                "                local items={'No se puede hacer nada'}" +
                "return items "
        val response= connectRedis.connection.sync().eval<MutableList<String>>(script, ScriptOutputType.MULTI)
        return response

    }










}