package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl

class VectorServiceTest {

    private var vectorDao = HibernateVectorDAO()
    private var vectorService = VectorServiceImpl(vectorDao)

    fun testSeCreaUnVector(){
        //Parametros para que no rompa. Hay que sacarlos
        val vector = Vector(1,"")
        vectorService.crearVector(vector)
    }

}