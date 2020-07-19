package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

interface ADNDAO {
    fun incorporarADNDeEspecie(vector: Vector, especie: Especie)
    fun darAdnDeEspecie(vector: Vector):String?
    fun noExisteAdn(vector: Vector): Boolean
}