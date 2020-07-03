package ar.edu.unq.eperdemic.spring.services


import ar.edu.unq.eperdemic.modelo.Ubicacion


interface UbicacionSpringService {

    fun recuperarUbicacionRandom(): Ubicacion
    fun recuperarTodasLasUbicaciones(): List<Ubicacion>
    fun guardarTodos(recuperarTodasLasUbicaciones: List<Ubicacion>)
}