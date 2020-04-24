package ar.edu.unq.eperdemic.modelo

import java.io.Serializable

class Patogeno(val tipo: String) : Serializable{

    var cantidadDeEspecies: Int = 0
    var id : Int? = null

    override fun toString(): String {
        return tipo
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String) : Especie{
        cantidadDeEspecies++
        return Especie(this, nombreEspecie, paisDeOrigen)
    }
}