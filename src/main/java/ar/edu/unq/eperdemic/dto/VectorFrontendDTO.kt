package ar.edu.unq.eperdemic.dto

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
import ar.edu.unq.eperdemic.tipo.TipoVector

class VectorFrontendDTO(val tipoDeVector : TipoDeVector,
                        val nombreDeUbicacionPresente: String) {

    enum class TipoDeVector {
        Persona, Insecto, Animal
    }

    fun aModelo() : Vector {
        val vector = Vector()
        val ubicacion = Ubicacion()
        ubicacion.nombreUbicacion = nombreDeUbicacionPresente
        ubicacion.agregarVector(vector)
        vector.ubicacion =  ubicacion
        vector.tipo = this.aTipoModel()
        return vector
    }

    private fun aTipoModel() : TipoVector{
        val tipo : TipoVector
        when(tipoDeVector){
            TipoDeVector.Persona -> tipo = Humano()
            TipoDeVector.Insecto -> tipo = Insecto()
            TipoDeVector.Animal -> tipo = Animal()
        }
        return tipo
    }
}