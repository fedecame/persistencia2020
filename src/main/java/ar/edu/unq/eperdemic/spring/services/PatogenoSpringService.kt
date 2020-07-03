package ar.edu.unq.eperdemic.spring.services

import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoSpringService {
    fun recuperarATodosLosPatogenos(): List<Patogeno>
}