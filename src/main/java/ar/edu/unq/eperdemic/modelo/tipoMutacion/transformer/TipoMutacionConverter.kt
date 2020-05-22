package ar.edu.unq.eperdemic.modelo.tipoMutacion.transformer

import ar.edu.unq.eperdemic.modelo.tipoMutacion.*
import javax.persistence.AttributeConverter

class TipoMutacionConverter : AttributeConverter<TipoMutacion, String> {
    override fun convertToDatabaseColumn(tipoMutacion: TipoMutacion): String {
        val clase = tipoMutacion::class.java
        val sb = StringBuilder()
        sb.append(clase.simpleName)
        return sb.toString()
    }

    override fun convertToEntityAttribute(dbTipoMutacion: String): TipoMutacion? {
        return TipoMutacionDelivery(mutableListOf(
                MutacionDefensaMicroorganismos(),
                MutacionFactorContagioAnimal(),
                MutacionFactorContagioHumano(),
                MutacionFactorContagioInsecto(),
                MutacionLetalidad())
        ).get(dbTipoMutacion)!!
    }
}