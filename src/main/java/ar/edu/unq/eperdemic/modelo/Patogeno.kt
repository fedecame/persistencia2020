package ar.edu.unq.eperdemic.modelo

import java.io.Serializable

class Patogeno(val tipo: String) : Serializable{

    var cantidadDeEspecies: Int = 0
    var id : Int? = null
    var factorContagioAnimal = 0
    var factorContagioInsecto = 0
    var factorContagioHumano = 0

    override fun toString(): String {
        return tipo
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String) : Especie{
        cantidadDeEspecies++
        val especie = Especie()
        //Aca settearla
        return Especie()
    }

    //agregar 5 atributos

    fun factorContagioAnimal(): Int = factorContagioAnimal

    fun factorContagioInsecto(): Int = factorContagioInsecto

    fun factorContagioHumano(): Int = factorContagioHumano
}