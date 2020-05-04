package ar.edu.unq.eperdemic.modelo

import javax.persistence.AttributeConverter

class TipoConverter : AttributeConverter<TipoVector, String> {

    override fun convertToDatabaseColumn(tipoVector: TipoVector): String? {
        val clase = tipoVector::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    override fun convertToEntityAttribute(dbTipo : String) : TipoVector?{
        val res = TipoDelivery().tipo(dbTipo)!!
        return res!!
    }

}
