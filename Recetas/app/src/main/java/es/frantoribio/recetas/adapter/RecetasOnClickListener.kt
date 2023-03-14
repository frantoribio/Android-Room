package es.frantoribio.recetas.adapter

import es.frantoribio.recetas.model.Receta

interface RecetasOnClickListener {

    fun onComplitedReceta(receta: Receta)
    fun onClickReceta(receta: Receta)

}