package ar.edu.unq.eperdemic.modelo

class MapaDeUbicaciones {
    var listadoDeUbicaciones= mutableListOf<Ubicacion>()

    fun addAgregarUbicacion(ubicacion :Ubicacion){
        listadoDeUbicaciones.add(ubicacion)
    }
    fun getUbicacion(nombreUbicacion:String):Ubicacion?{
        return listadoDeUbicaciones.find{ it.nombre==nombreUbicacion }
    }


}