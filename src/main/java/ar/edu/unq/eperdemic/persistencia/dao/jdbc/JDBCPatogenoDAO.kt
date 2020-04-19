package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNoCreadoRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCConnector.execute
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement


class JDBCPatogenoDAO: PatogenoDAO {

    override    fun crear(patogeno: Patogeno): Int {
        var patogeno_id : Int = -1 //Tiene que estar inicializada
            execute {
            val ps = it.prepareStatement("INSERT INTO patogeno(tipo, cantidadDeEspecies) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, patogeno.tipo)
            ps.setInt(2, patogeno.cantidadDeEspecies)
            ps.executeUpdate()
            if (ps.updateCount != 1) {
                throw PatogenoNoCreadoRunTimeException(patogeno.tipo)
            }
            val set_resultado = ps.getGeneratedKeys()
            if (set_resultado.next()) {
                patogeno_id = set_resultado.getInt(1)
            }
            ps.close()
        }
        return patogeno_id
    }

    override fun actualizar(patogeno: Patogeno) {

        execute {
            val ps = it.prepareStatement("UPDATE patogeno SET tipo = ?, cantidadDeEspecies = ? WHERE id = ?")
            ps.setString(1, patogeno.tipo)
            ps.setInt(2, patogeno.cantidadDeEspecies)
            ps.setInt(3, patogeno.id!!)
            ps.executeUpdate()
            ps.close()
        }
    }

    override fun recuperar(patogenoId: Int): Patogeno {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT * FROM patogeno WHERE id = ?")
            ps.setInt(1, patogenoId)
            val resultSet = ps.executeQuery()
            var patogenoBuscado: Patogeno? = null
            while (resultSet.next()) {
                if (patogenoBuscado != null) {
                    throw PatogenoNotFoundRunTimeException(patogenoId)
                }
                patogenoBuscado = Patogeno(resultSet.getString("tipo"), resultSet.getInt("cantidadDeEspecies"), patogenoId)
            }
            ps.close()
            patogenoBuscado!!
        }
    }

//    override fun recuperarATodos(): List<Patogeno> {
//        TODO("not implemented")
//    }

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