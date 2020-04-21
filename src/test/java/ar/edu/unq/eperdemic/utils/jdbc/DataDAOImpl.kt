package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCConnector.execute
import java.sql.Connection

class DataDAOImpl(private var patogenoDao : PatogenoDAO) : DataDAO {
    override fun crearSetDeDatosIniciales() {
        var patogenos = listOf("Bacteria", "Hongo", "Protozoo", "Virus")
        for (patogeno in patogenos) {
            patogenoDao.crear(Patogeno(patogeno))
        }
    }
    override fun eliminarTodo(){
        execute{
            conn: Connection ->
            val ps =  conn.prepareStatement("TRUNCATE TABLE patogeno")
            ps.execute()
            ps.close()
            null
        }

    }
}