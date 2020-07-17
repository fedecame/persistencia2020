package ar.edu.unq.eperdemic.modelo.exception

class CaminoNoSoportado(ubicacionOrigen:String,ubicacionDestino:String): RuntimeException("No se puede mover de $ubicacionOrigen a $ubicacionDestino, camino no soportado.")

