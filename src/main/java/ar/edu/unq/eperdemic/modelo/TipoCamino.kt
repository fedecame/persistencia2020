package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector

enum class TipoCamino {
        Terrestre{
            override fun puedeSerAtravesadoPor(tipoVector : TipoVector) = true
        },
        Maritimo{
            override fun puedeSerAtravesadoPor(tipoVector : TipoVector) = tipoVector is Humano || tipoVector is Animal

        },
        Aereo{
            override fun puedeSerAtravesadoPor(tipoVector : TipoVector) =  tipoVector is Insecto || tipoVector is Animal
        };
    abstract fun puedeSerAtravesadoPor(tipoVector : TipoVector) : Boolean
    fun nombre() = this.name
}

