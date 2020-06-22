package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEventoModel
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.codecs.pojo.annotations.BsonProperty

class Evento {
    var tipoEvento: TipoEvento? = null
    var accionQueLoDesencadena : String? = null
    var tipoPatogeno : String? = null
    //Seguramente aca faltan todos los otros atributos de los eventos, independientemente de que se usen o no.
    //Se delega en el tipo algunas cosas?

    protected constructor() {}
    constructor(_tipoEvento : TipoEvento, _accion : String, _tipoPatogeno : String) {
        this.tipoEvento = _tipoEvento
        this.accionQueLoDesencadena = _accion
        this.tipoPatogeno = _tipoPatogeno
    }

    fun log() : String = tipoEvento!!.aModel(this).log()
}
