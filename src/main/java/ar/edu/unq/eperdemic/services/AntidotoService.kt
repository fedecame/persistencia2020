package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Antidoto
import ar.edu.unq.eperdemic.modelo.Especie

interface AntidotoService {

    fun crearAntidoto(antidoto: Antidoto)
    fun getNombreAntido(especie: Especie):String?

}