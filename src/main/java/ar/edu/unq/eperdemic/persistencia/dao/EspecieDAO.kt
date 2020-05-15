package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieDAO {

    fun crearEspecie(especie : Especie) : Int

    fun recuperarEspecie(id : Int) : Especie
}