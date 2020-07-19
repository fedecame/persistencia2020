package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Antidoto
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisAntidotoDao
import ar.edu.unq.eperdemic.services.AntidotoService

class AntidotoServiceImpl : AntidotoService {
    var redisAntidotoDao=RedisAntidotoDao()

    override fun crearAntidoto(antidoto:Antidoto){
        redisAntidotoDao.crearAntidoto(antidoto.nombre,antidoto.especie.nombre)
    }

    override fun getNombreAntido(especie:Especie):String?{
        return redisAntidotoDao.getNombre(especie.nombre)
    }
}