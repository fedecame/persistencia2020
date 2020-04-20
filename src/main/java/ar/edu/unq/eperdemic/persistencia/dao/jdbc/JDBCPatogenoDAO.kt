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
        var patogenoId : Int = -1
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
                patogenoId = set_resultado.getInt(1)
            }
            ps.close()
        }
        return patogenoId
    }

    override fun actualizar(patogeno: Patogeno) {
        execute {
            val ps = it.prepareStatement("UPDATE patogeno SET tipo = ?, cantidadDeEspecies = ? WHERE id = ?")
            ps.setString(1, patogeno.tipo)
            ps.setInt(2, patogeno.cantidadDeEspecies)
            ps.setInt(3, patogeno.id!!)
            val x = ps.executeUpdate()
            if (x == 0) {
               throw PatogenoNotFoundRunTimeException(patogeno.id!!)
            }
            ps.close()
        }
    }
    override fun recuperar(patogenoId: Int): Patogeno {
        lateinit var patogenoBuscado : Patogeno
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT * FROM patogeno WHERE id = ?")
            ps.setInt(1, patogenoId)
            val resultSet = ps.executeQuery()
            while (resultSet.next()) {
                patogenoBuscado = Patogeno(resultSet.getString("tipo"), resultSet.getInt("cantidadDeEspecies"), patogenoId)
                if (patogenoBuscado == null) {
                    throw PatogenoNotFoundRunTimeException(patogenoId)
                }
            }
            ps.close()
            patogenoBuscado
        }
    }

//    override fun recuperarATodos(): List<Patogeno> {
//        TODO("not implemented")
//    }

    fun eliminar(patogeno: Patogeno) {
        execute { conn: Connection ->
            val ps =  conn.prepareStatement("DELETE FROM patogeno WHERE id =  ? ")
            ps.setInt(1, patogeno.id!!)
            ps.execute()
            if (ps.updateCount != 1) {
                throw RuntimeException(patogeno.id as String)
            }
            ps.close()
            null
        }
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