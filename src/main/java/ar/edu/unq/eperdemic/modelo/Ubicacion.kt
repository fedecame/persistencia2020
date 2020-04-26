package ar.edu.unq.eperdemic.modelo

class Ubicacion(var nombreUbicacion: String) {

    private val vectorAlojados = mutableListOf<Vector>()

    fun getVectorAlojados() : List<Vector>{

        return vectorAlojados
    }
    fun addVectorUbicacion(vector:Vector){
        vectorAlojados.add(vector)
    }

}