package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector

interface UbicacionDAO {
    fun crear (ubicacion: Ubicacion): Ubicacion
    fun recuperar(nombre:String): Ubicacion
   // fun recuperarAll():MutableList<Ubicacion>
}