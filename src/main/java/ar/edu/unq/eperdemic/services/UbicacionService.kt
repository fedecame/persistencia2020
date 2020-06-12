package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

interface UbicacionService {

    fun mover(vectorId: Int, nombreUbicacion: String)
    fun expandir(nombreUbicacion: String)

    /* Operaciones CRUD*/
    fun crearUbicacion(nombreUbicacion: String): Ubicacion

    fun recuperarUbicacion(nombreUbicacion: String): Ubicacion
    fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String)
    fun capacidadDeExpansion(vectorId : Long, movimientos : Int) : Int

}