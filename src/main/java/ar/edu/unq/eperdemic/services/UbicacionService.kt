package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

interface UbicacionService {

    fun mover(vectorId: Int, nombreUbicacion: String)
    fun expandir(nombreUbicacion: String)
    /* Operaciones CRUD*/
    fun crearUbicacion(nombreUbicacion: String): Ubicacion
//     fun borrarTodo()
    fun recuperarUbicacion(nombreUbicacion: String):Ubicacion


}