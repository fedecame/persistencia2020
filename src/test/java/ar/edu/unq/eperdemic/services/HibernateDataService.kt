package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utils.DataService


class HibernateDataService() {

    fun eliminarTodo() {
        TransactionRunner.addHibernate().runTrx { HibernateDataDAO().clear() }
    }
}
