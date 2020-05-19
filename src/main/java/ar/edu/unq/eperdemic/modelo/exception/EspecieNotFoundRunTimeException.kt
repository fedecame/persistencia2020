package ar.edu.unq.eperdemic.modelo.exception

class EspecieNotFoundRunTimeException(especie_id : Int) : RuntimeException("La especie de id $especie_id no fue encontrada")

