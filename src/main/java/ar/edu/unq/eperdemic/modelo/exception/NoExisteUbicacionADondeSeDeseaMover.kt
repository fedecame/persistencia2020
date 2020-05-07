package ar.edu.unq.eperdemic.modelo.exception

class NoExisteUbicacionADondeSeDeseaMover(ubicacion_nombre:String):RuntimeException ("ubicacion: $ubicacion_nombre, donde se desea mover no existe")