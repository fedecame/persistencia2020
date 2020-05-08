package ar.edu.unq.eperdemic.modelo.exception


class MoverUnVectorQueNoEstaCreado(idVector: Int) : RuntimeException("El vector $idVector que desea mover no existe")
