package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.utils.DataService

//
class HibernateDataService() : DataService, HibernateDataDAO() {
    override fun crearSetDeDatosIniciales() {
        TODO("Not yet implemented")
    }

    override fun eliminarTodo() {
        super.clear()
    }

}
