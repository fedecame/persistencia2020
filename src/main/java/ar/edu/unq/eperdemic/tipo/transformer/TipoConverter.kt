package ar.edu.unq.eperdemic.tipo.transformer

import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import ar.edu.unq.eperdemic.utility.Converter
import javax.persistence.AttributeConverter

//Cuando funcione el converter : class TipoConverter : Converter<TipoVector>(TipoDelivery(mutableListOf(Animal(), Humano(), Insecto())))

class TipoConverter : AttributeConverter<TipoVector, String> {

    override fun convertToDatabaseColumn(tipoVector: TipoVector): String {
        val clase = tipoVector::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    override fun convertToEntityAttribute(dbTipo : String) : TipoVector? = TipoDelivery(mutableListOf(Animal(), Insecto(), Humano())).get(dbTipo)!!

}
