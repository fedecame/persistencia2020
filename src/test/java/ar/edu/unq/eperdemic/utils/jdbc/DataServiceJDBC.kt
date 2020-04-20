package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.utils.DataService

class DataServiceJDBC(var dataDAO: DataDAO) : DataService {
    override fun crearSetDeDatosIniciales() {
        dataDAO.crearSetDatosIniciales()
    }

    override fun eliminarTodo() {
        dataDAO.eliminarTodo()
    }
}