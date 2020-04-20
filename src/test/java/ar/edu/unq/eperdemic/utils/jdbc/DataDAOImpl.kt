package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCConnector.execute
import java.sql.Connection

class DataDAOImpl : DataDAO {
    override fun crearSetDatosIniciales(){

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