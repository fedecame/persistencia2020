package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import javax.persistence.NoResultException

class EstadisticasServiceImpl(private var estadisticasDAO : EstadisticasDAO) : EstadisticasService {

    override fun especieLider(): Especie {
        TODO("Not yet implemented")
    }

    override fun lideres(): List<Especie> {
        TODO("Not yet implemented")
    }

    override fun reporteDeContagios(nombreUbicacion: String): ReporteDeContagios = ReporteDeContagios(this.vectoresPresentes(nombreUbicacion), this.vectoresInfectados(nombreUbicacion), this.especieQueInfectaAMasVectoresEn(nombreUbicacion))

    private fun vectoresPresentes(nombreUbicacion: String): Int {
        return runTrx { estadisticasDAO.vectoresPresentes(nombreUbicacion) }
    }

    private fun vectoresInfectados(nombreUbicacion: String): Int = runTrx { estadisticasDAO.vectoresInfectados(nombreUbicacion) }


    private fun especieQueInfectaAMasVectoresEn(nombreUbicacion: String): String {
        var res : String = ""
        try {
            runTrx { res = estadisticasDAO.especieQueInfectaAMasVectoresEn(nombreUbicacion) }
        }
        catch (e : NoResultException){
            res = ""
        }
        return res
    }

}



