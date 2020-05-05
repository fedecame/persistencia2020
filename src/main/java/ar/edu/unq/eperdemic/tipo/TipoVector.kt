package ar.edu.unq.eperdemic.tipo

import ar.edu.unq.eperdemic.modelo.Especie

abstract class TipoVector(){

    //Esto no lo necesitamos con la logica del convert
    //enum class TipoDeVector{
    //    Insecto, Animal, Humano
    //}

    //fun contagiamePor(vector: Vector, tipoDelContagiador: TipoVector, especiesContagiador: List<Especie>){
    //    if(this.puedeSerContagiadoPor(tipoDelContagiador)) {
    //        especiesContagiador.forEach{
    //            especie -> this.infectameSiCorresponde(vector, especie)
    //        }
    //    }
    //}

    //fun infectameSiCorresponde(vector: Vector, especie: Especie) {
        //if (this.porcentajeDeContagioExitoso(especie) >= 100) {
        //    vector.infectar(especie)
        //    this.agregarInfectado(especie)
    //    }
    //}

   // fun porcentajeDeContagioExitoso(especie: Especie): Int = (1)/*random AQUI*/ + this.factorContagio(especie)

    open fun esHumano(): Boolean = false

    open fun esInsecto() : Boolean = false

    open fun esAnimal() : Boolean = false

    abstract fun factorContagio(especie : Especie): Int

    abstract fun puedeSerContagiadoPor(unTipo : TipoVector): Boolean

    open fun agregarInfectado(especie : Especie){}
}