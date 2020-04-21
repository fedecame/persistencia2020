package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.utils.DataService
import ar.edu.unq.eperdemic.utils.jdbc.DataDAOImpl
import ar.edu.unq.eperdemic.utils.jdbc.DataServiceJDBC

open class ModData(protected var dao : PatogenoDAO = JDBCPatogenoDAO(), protected val dataService : DataService = DataServiceJDBC(DataDAOImpl(dao))) {

    open fun crearModelo() {
        dataService.crearSetDeDatosIniciales()
    }

    open fun eliminarModelo() {
        dataService.eliminarTodo()
    }
}