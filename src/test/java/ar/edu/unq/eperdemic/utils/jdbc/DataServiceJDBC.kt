package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.utils.DataService

class DataServiceJDBC(var patogenoDao : PatogenoDAO) : DataService {

    override fun crearSetDeDatosIniciales() {
        var patogenos = listOf("Protozoo", "Hongo", "Virus", "Bacteria")
        for (patogeno in patogenos) {
            patogenoDao.crear(Patogeno(patogeno))
        }
    }

    override fun eliminarTodo() {
        patogenoDao.eliminarTodos()
    }
}