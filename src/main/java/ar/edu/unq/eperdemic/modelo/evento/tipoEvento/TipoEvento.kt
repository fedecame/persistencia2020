package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Evento
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.codecs.pojo.annotations.BsonProperty

@BsonDiscriminator
abstract class TipoEvento{
    val tipo : String? = this.javaClass.simpleName.toString()

    @BsonIgnore
    lateinit var evento : Evento

    protected constructor() {}

    abstract fun log() : String
}