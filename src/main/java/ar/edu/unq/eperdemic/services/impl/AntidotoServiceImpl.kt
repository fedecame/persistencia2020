package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Antidoto
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisAntidotoDao

class AntidotoServiceImpl {
    var redisAntidotoDao=RedisAntidotoDao()
    fun CrearAntidoto(antidoto:Antidoto){

        redisAntidotoDao.crearAntidoto(antidoto.nombre,antidoto.especie.nombre)
    }
    fun getNombreAntido(especie:Especie):String?{
        return redisAntidotoDao.getNombre(especie.nombre)!!
    }


}