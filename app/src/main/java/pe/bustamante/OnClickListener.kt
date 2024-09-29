package pe.bustamante

interface OnClickListener
{
    fun onClick(comicEntity: ComicEntity)
    fun onFavorityComic(comicEntity: ComicEntity)

    fun onDeleteComic(comicEntity: ComicEntity)
}