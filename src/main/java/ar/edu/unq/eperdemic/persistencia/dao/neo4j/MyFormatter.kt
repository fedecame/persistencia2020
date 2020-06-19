package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoCamino

class MyFormatter : MyFormat {

    override fun darTipo(camino: String): TipoCamino? {
        when (camino) {
            "Terrestre" -> return TipoCamino.Terrestre
            "Aereo" -> return TipoCamino.Aereo
            "Maritimo" -> return TipoCamino.Maritimo
            else -> return null
        }
    }

    override fun tiposFormateados(tipos : List<String>, movimientos: Int) : String = tipos.toString().toString().replace("[", "[:").replace(",", " |").replace("]", "*0..${movimientos.toString()}]").trim().trim()

    override fun caminosFormateados(tiposDeCaminosPosibles : List<String>) : String = tiposDeCaminosPosibles.toString().replace(",", "|").trim().drop(1).dropLast(1).toString()
}