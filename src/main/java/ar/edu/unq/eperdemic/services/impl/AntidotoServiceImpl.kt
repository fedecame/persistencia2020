package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisAntidotoDao

class AntidotoServiceImpl {
    var redisAntidotoDao=RedisAntidotoDao()
    fun CrearAntidoto(nombreAntidoto:String,nombreEspecie:String){
      redisAntidotoDao.CrearAntidoto(nombreAntidoto,nombreEspecie)
    }
    fun getNombreAntido(especie:Especie):String?{
        return redisAntidotoDao.getNombre(especie.nombre)
    }


}