package ar.edu.unq.eperdemic.tipo

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.utility.random.RandomMaster

abstract class TipoVector(){
    lateinit var randomGenerator : RandomMaster

    fun contagiamePor(vectorAContagiar: Vector, tipoDelContagiador: TipoVector, especiesContagiador: List<Especie>){
        if(this.puedeSerContagiadoPor(tipoDelContagiador)) {
            especiesContagiador.forEach{
                especie -> this.infectameSiCorresponde(vectorAContagiar, especie)
            }
        }
    }

    fun infectameSiCorresponde(vectorAContagiar: Vector, especie: Especie) {
    if (this.porcentajeDeContagioExitoso(especie) >= randomGenerator.giveMeARandonNumberBeetween(1.0,100.0)) {
        vectorAContagiar.infectarse(especie)
        }
    }

    fun porcentajeDeContagioExitoso(especie: Especie): Double = randomGenerator.giveMeARandonNumberBeetween(1.0,10.0) + this.factorContagio(especie)

    open fun esHumano(): Boolean = false

    open fun esInsecto() : Boolean = false

    open fun esAnimal() : Boolean = false

    abstract fun factorContagio(especie : Especie): Int

    abstract fun puedeSerContagiadoPor(unTipo : TipoVector): Boolean

    open fun agregarInfectado(especie : Especie){}
}