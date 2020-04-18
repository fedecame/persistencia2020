package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCConnector.execute


class JDBCPatogenoDAO : PatogenoDAO {

    override fun crear(patogeno: Patogeno): Int {
        execute {
          val ps=  it.prepareStatement("INSERT INTO personaje (tipo,cantidadDeEspecies) VALUES (?,?)")
            ps.setString(1, patogeno!!.tipo)
            ps.execute()
            if (ps.updateCount != 1) {
                throw RuntimeException("No se inserto el personaje $patogeno")
            }
            ps.close()
            null


    }

    override fun actualizar(patogeno: Patogeno) {
        TODO("not implemented")
    }

    override fun recuperar(patogenoId: Int): Patogeno {
        TODO("not implemented")
    }

    override fun recuperarATodos(): List<Patogeno> {
        TODO("not implemented")
    }

    init {
        val initializeScript = javaClass.classLoader.getResource("createAll.sql").readText()
        execute {
            val ps = it.prepareStatement(initializeScript)
            ps.execute()
            ps.close()
            null
        }
    }
}