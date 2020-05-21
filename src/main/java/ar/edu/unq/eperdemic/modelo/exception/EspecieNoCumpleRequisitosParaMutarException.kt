package ar.edu.unq.eperdemic.modelo.exception

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion

class EspecieNoCumpleRequisitosParaMutarException(especie: Especie, mutacion: Mutacion) : RuntimeException("La especie de id ${especie.id} y nombre ${especie.nombre}, no cumple con los requisitos para mutar con la mutación de id ${mutacion.id}")