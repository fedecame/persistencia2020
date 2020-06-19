package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoCamino

interface MyFormat {
    fun darTipo(camino: String): TipoCamino?
    fun tiposFormateados(tipos : List<String>, movimientos: Int) : String
    fun caminosFormateados(tipos : List<String>) : String
}