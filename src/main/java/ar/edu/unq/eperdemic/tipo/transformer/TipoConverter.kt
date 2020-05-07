package ar.edu.unq.eperdemic.tipo.transformer

import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector
import javax.persistence.AttributeConverter

class TipoConverter : AttributeConverter<TipoVector, String> {

    override fun convertToDatabaseColumn(tipoVector: TipoVector): String {
        val clase = tipoVector::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    //Cuando el Delivery funcione
    //override fun convertToEntityAttribute(dbTipo : String) : TipoVector? = TipoDelivery(mutableListOf(Animal(), Humano(), Insecto())).get(dbTipo)!!
    override fun convertToEntityAttribute(dbTipo : String) : TipoVector? = TipoDelivery().tipo(dbTipo)!!

}
