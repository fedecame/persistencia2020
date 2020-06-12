package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utils.DataService


class HibernateDataService() : DataService{

    override fun crearSetDeDatosIniciales() {
        //Esto lo delegamos a cada integrante del equipo para que haga sus propios casos de prueba
        //segun lo que se este testeando.
    }

    override fun eliminarTodo() {
        TransactionRunner.addHibernate().runTrx { HibernateDataDAO().clear() }
    }
}
