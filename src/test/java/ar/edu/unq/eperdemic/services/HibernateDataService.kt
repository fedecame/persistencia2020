package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.utils.DataService


class HibernateDataService() : DataService, HibernateDataDAO() {

    //Falta agregar compartamiento para que se cree todos los SUT de una. Es mas facil para testear despues?
    override fun crearSetDeDatosIniciales() {
        TODO("Not yet implemented")
    }

    override fun eliminarTodo() {
        super.clear()
    }

}
