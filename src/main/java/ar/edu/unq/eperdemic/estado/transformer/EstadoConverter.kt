package ar.edu.unq.eperdemic.estado.transformer

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import javax.persistence.AttributeConverter

class EstadoConverter : AttributeConverter<EstadoVector, String>  {

    override fun convertToDatabaseColumn(estadoVector: EstadoVector): String{
        val clase = estadoVector::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    override fun convertToEntityAttribute(dbEstado : String) : EstadoVector? = EstadoDelivery(mutableListOf(Sano(), Infectado())).get(dbEstado)!!
}