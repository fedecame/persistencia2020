package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

class ReporteDeContagiosDTO(val vectoresPresentes:Int,
                            val vectoresInfecatods:Int,
                            val nombreDeEspecieMasInfecciosa: String,
                            val nombreDeUbicacion: String,
                            val nombreDelEquipo: String) {
    companion object {
        fun from(reporte: ReporteDeContagios, nombreDeUbicacion: String, nombreDelEquipo: String) =
                ReporteDeContagiosDTO(
                        reporte.vectoresPresentes,
                        reporte.vectoresInfecatods,
                        reporte.nombreDeEspecieMasInfecciosa!!,
                        nombreDeUbicacion,
                        nombreDelEquipo
                )
    }
}