package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.modelo.exception.NoHayEspecieQueInfectaronHumanos
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.springframework.web.servlet.NoHandlerFoundException
import javax.persistence.NoResultException

class EstadisticasServiceImpl(private var estadisticasDAO: EstadisticasDAO) : EstadisticasService {

    override fun especieLider(): Especie =
            try {
                runTrx { estadisticasDAO.especieLider() }
            } catch (e: NoResultException) {
                throw NoHayEspecieQueInfectaronHumanos()
            }

    override fun lideres(): MutableList<Especie> {
        return runTrx { estadisticasDAO.lideres() }
    }

    override fun reporteDeContagios(nombreUbicacion: String): ReporteDeContagios = ReporteDeContagios(this.vectoresPresentes(nombreUbicacion), this.vectoresInfectados(nombreUbicacion), this.especieQueInfectaAMasVectoresEn(nombreUbicacion))

    private fun vectoresPresentes(nombreUbicacion: String): Int {
        return runTrx { estadisticasDAO.vectoresPresentes(nombreUbicacion) }
    }

    private fun vectoresInfectados(nombreUbicacion: String): Int = runTrx { estadisticasDAO.vectoresInfectados(nombreUbicacion) }


    private fun especieQueInfectaAMasVectoresEn(nombreUbicacion: String): String {
        var res: String = ""
        try {
            runTrx { res = estadisticasDAO.especieQueInfectaAMasVectoresEn(nombreUbicacion) }
        } catch (e: NoResultException) {
            res = ""
        }
        return res
    }

}



