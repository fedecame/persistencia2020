package ar.edu.unq.eperdemic.spring.services


import ar.edu.unq.eperdemic.modelo.Especie



interface EspecieSpringService {

    fun recuperarTodasLasEspecies(): List<Especie>
    fun recuperarEspeciesDePatogeno(patogeniId:Long): List<Especie>
    fun recuperarPorNombre(nombreDeLaEspecie: String): Especie

}