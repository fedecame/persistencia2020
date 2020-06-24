package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento
import org.bson.codecs.pojo.annotations.BsonProperty

class Evento {
    //Aca faltan colaboradores, ejemplo la especie y bla
   var n : Int? = null // Para probar ordenamiento
   var tipoEvento: TipoEvento? = null
   var accionQueLoDesencadena : String? = null
   var tipoPatogeno : String? = null
    //Quizas convenga mas guardar una instancia de especie que solo el nombre, aunque en esta primer parte no nevcesitamos mas
   var nombreEspecie : String? = null
   var nombreUbicacion : String? = null
    var ubicacionOrigen :String?=null
    var idVector:Int?=null
   //Seguramente aca faltan todos los otros atributos de los eventos, independientemente de que se usen o no.

    constructor() {}
    constructor(_int : Int, _tipoEvento : TipoEvento, _accion : String, _tipoPatogeno : String?=null, _nombreEspecie : String? = null, _nombreUbicacion : String? = null,_nombreubicacionOrigen:String?=null,_idVector:Int?=null) {
        this.n = _int //Para probar el ordenar antes de poner una fecha
        this.tipoEvento = _tipoEvento
        this.accionQueLoDesencadena = _accion
        this.tipoPatogeno = _tipoPatogeno
        this.nombreEspecie = _nombreEspecie
        this.nombreUbicacion = _nombreUbicacion
        this.ubicacionOrigen=_nombreubicacionOrigen
        this.idVector=_idVector
   }

    fun log() : String = tipoEvento!!.log()
}
