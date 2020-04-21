package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNoCreadoRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCConnector.execute
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


class JDBCPatogenoDAO: PatogenoDAO {

    override fun crear(patogeno: Patogeno): Int {
        var patogenoId : Int? = null
            execute {
            val ps = it.prepareStatement("INSERT INTO patogeno(tipo, cantidadDeEspecies) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)
            this.setQueryWithoutID(patogeno.tipo, patogeno.cantidadDeEspecies, ps)
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
        return patogenoId!!
    }

    override fun actualizar(patogeno: Patogeno) {
        execute {
            val ps = it.prepareStatement("UPDATE patogeno SET tipo = ?, cantidadDeEspecies = ? WHERE id = ?")
            this.setQueryWithoutID(patogeno.tipo, patogeno.cantidadDeEspecies, ps)
            ps.setInt(3, patogeno.id!!)
            val x = ps.executeUpdate()
            if (x == 0) {
               throw PatogenoNotFoundRunTimeException(patogeno.id!!)
            }
            ps.close()
        }
    }

    override fun recuperar(patogenoId: Int): Patogeno {
        var patogenoBuscado : Patogeno? = null
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT * FROM patogeno WHERE id = ?")
            ps.setInt(1, patogenoId)
            val resultSet = ps.executeQuery()
            while (resultSet.next()) {
                patogenoBuscado = Patogeno(resultSet.getString("tipo"), resultSet.getInt("cantidadDeEspecies"), patogenoId)
                if(resultSet.next()){
                    print("Hay otro")
                }
            }
            if (patogenoBuscado == null) {
                throw PatogenoNotFoundRunTimeException(patogenoId)
            }
            ps.close()
            patogenoBuscado!!
        }
    }

    override fun recuperarATodos(): List<Patogeno> {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT * FROM patogeno ORDER BY tipo ASC ")
            val resultSet = ps.executeQuery()
            val patogenos = this.resultSet2List(resultSet)
            ps.close()
            patogenos
        }
    }

    private fun setQueryWithoutID(tipo : String, cantidadDeEspecies : Int, ps : PreparedStatement){
        ps.setString(1, tipo)
        ps.setInt(2, cantidadDeEspecies)
    }

     private fun resultSet2List(resultSet : ResultSet) : List<Patogeno>{
         val lista = mutableListOf<Patogeno>()
         while (resultSet.next()) {
             val id: Int = resultSet.getInt("id")
             val tipo: String = resultSet.getString("tipo")
             val cantidadDeEspecies : Int = resultSet.getInt("cantidadDeEspecies")
             val patogeno = Patogeno(tipo, cantidadDeEspecies, id)
             lista.add(patogeno)
         }
         return lista
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