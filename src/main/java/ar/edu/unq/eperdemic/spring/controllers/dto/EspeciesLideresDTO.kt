package ar.edu.unq.eperdemic.spring.controllers.dto


class EspecieLiderDTO(val especie_nombre: String?, val especie_patogeno: String?, val cantidadInfectados: Int?, val esPandemia: Boolean?) {


    companion object {
        fun from(especieNombre: String?, patogenoNombre: String?, cantidadDeInfectados: Int?, esPandemia: Boolean?) =
                EspecieLiderDTO(
                        especieNombre,
                        patogenoNombre,
                        cantidadDeInfectados,
                        esPandemia
                )
        }

}
