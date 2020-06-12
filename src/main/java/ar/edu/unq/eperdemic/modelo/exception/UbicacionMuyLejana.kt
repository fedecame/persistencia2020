package ar.edu.unq.eperdemic.modelo.exception

class UbicacionMuyLejana(nombreOrigen :String,nombreUbicacion:String): RuntimeException("No se puede mover de $nombreOrigen a $nombreUbicacion ubicacion muy lejana ") {
}