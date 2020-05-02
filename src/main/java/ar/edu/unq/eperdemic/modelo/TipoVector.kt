package ar.edu.unq.eperdemic.modelo

abstract class TipoVector(val vector : Vector){
    fun contagiamePor(especiesContagiador: List<Especie>, tipoDelContagiador: TipoVector){
        if(this.puedeSerContagiadoPor(tipoDelContagiador)) {
            especiesContagiador.forEach{
                especie -> this.infectameSiCorresponde(especie)
            }
        }
    }

    fun infectameSiCorresponde(especie: Especie) {
        //if (this.porcentajeDeContagioExitoso(especie) >= 100) {
        //    vector.infectar(especie)
        //    this.agregarInfectado(especie)
    //    }
    }

    fun porcentajeDeContagioExitoso(especie: Especie): Int = (1)/*random AQUI*/ + this.factorContagio(especie)

    open fun esHumano(): Boolean = false

    open fun esInsecto() : Boolean = false

    open fun esAnimal() : Boolean = false

    abstract fun factorContagio(especie : Especie): Int

    abstract fun puedeSerContagiadoPor(unTipo : TipoVector): Boolean

    open fun agregarInfectado(especie : Especie){}
}