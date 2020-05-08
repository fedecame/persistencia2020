package ar.edu.unq.eperdemic.estado.transformer

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import ar.edu.unq.eperdemic.tipo.transformer.TipoDelivery
import ar.edu.unq.eperdemic.utility.Converter
import javax.persistence.AttributeConverter

//Cuando funcione el converter :
//class EstadoConverter(EstadoDelivery(mutableListOf(Sano(), Infectado())))

class EstadoConverter: AttributeConverter<EstadoVector, String>  {

    override fun convertToDatabaseColumn(estadoVector: EstadoVector): String{
        val clase = estadoVector::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    override fun convertToEntityAttribute(dbEstado : String) : EstadoVector? = EstadoDelivery(mutableListOf(Sano(), Infectado())).get(dbEstado)!!
}