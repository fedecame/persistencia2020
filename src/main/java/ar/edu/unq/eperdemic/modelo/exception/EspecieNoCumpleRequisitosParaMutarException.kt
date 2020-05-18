package ar.edu.unq.eperdemic.modelo.exception

class EspecieNoCumpleRequisitosParaMutarException(especieId: String, mutacionId: String) : RuntimeException("La especie de id $especieId no cumple con los requisitos para mutar con la mutación de id $mutacionId")