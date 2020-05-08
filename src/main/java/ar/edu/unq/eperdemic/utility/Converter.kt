package ar.edu.unq.eperdemic.utility

import javax.persistence.AttributeConverter

open class Converter<T>(protected var delivery : Delivery<T>) : AttributeConverter<T, String> {

    override fun convertToDatabaseColumn(attribute: T): String? {
        //T puede ser nul!!
        //val clasS = attribute!!.javaClass
        //val word =  clasS.simpleName
        //val sb = StringBuilder()
        //sb.append(word)
        return ""//sb.toString()
    }

    override fun convertToEntityAttribute(dbData: String): T = delivery.get(dbData)!!
}