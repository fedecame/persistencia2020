package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEventoModel
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.codecs.pojo.annotations.BsonProperty

class Evento {
    @BsonProperty("id")
    var id: String? = null
    @BsonIgnore
    var tipoEventoModel : TipoEventoModel? = null
    var tipo: String? = null
    var accionQueLoDesencadena : String? = null
    var tipoPatogeno : String? = null
    //Seguramente aca faltan todos los otros atributos de los eventos, independientemente de que se usen o no.
    //Se delega en el tipo algunas cosas?

    protected constructor() {}
    constructor(_tipoEvento : TipoEvento, _accion : String, _tipoPatogeno : String) {
        this.tipoEventoModel = _tipoEvento.aModel(this)
        this.tipo = _tipoEvento.name
        this.accionQueLoDesencadena = _accion
        this.tipoPatogeno = _tipoPatogeno
    }

    fun log() : String = tipoEventoModel!!.log()
}
