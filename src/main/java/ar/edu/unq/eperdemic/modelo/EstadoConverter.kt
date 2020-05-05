package ar.edu.unq.eperdemic.modelo

import javax.persistence.AttributeConverter

class EstadoConverter : AttributeConverter<EstadoVector, String>  {

    override fun convertToDatabaseColumn(estadoVector: EstadoVector): String{
        val clase = estadoVector::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    override fun convertToEntityAttribute(dbEstado : String) : EstadoVector? = EstadoDelivery().estado(dbEstado)!!
}