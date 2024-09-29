package pe.bustamante

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import pe.bustamante.databinding.ItemStoreBinding

class ComicAdapter(private var comics:MutableList<ComicEntity>,
                      private var listener:OnClickListener)
    : RecyclerView.Adapter<ComicAdapter.ViewHolder>()
{

    private lateinit var mContext:Context

    //clase interna
    inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view)
    {
        val binding=ItemStoreBinding.bind(view)

        //función que va a escuchar un comic
        fun setListener(comicEntity: ComicEntity)
        {
            //optimizando
            with(binding.root)
            {
                setOnClickListener {
                    listener.onClick(comicEntity)
                }

                //click largo para eliminar
                setOnLongClickListener {
                    listener.onDeleteComic(comicEntity)
                    true
                }
            }

            //evento para actualizar
            binding.cbFavorite.setOnClickListener {
                listener.onFavorityComic(comicEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        mContext=parent.context
        val view=LayoutInflater.from(mContext).inflate(R.layout.item_store,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val comic=comics.get(position)

        //queremos que el ViewHolder escuche al comic
        with(holder)
        {
            setListener(comic)

            //que se va a trabajar con la propiedad name,cat,des
            binding.tvName.text=comic.name
            binding.ecat.text="Categoria:"
            binding.edes.text="Descripción"
            binding.tvCategoria.text=comic.categoria
            binding.tvDescripcion.text=comic.descripcion

            //configuración del checked
            binding.cbFavorite.isChecked=comic.isFavorite

            //mostrar imagen en la ventana principal
            Glide.with(mContext).load(comic.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(binding.imgPhoto)
        }
    }

    override fun getItemCount(): Int = comics.size

    //agregar
    fun add(comicEntity: ComicEntity)
    {
        //si en la colección no existe el nuevo comic que se quiere agregar
        if(!comics.contains(comicEntity))
        {
            comics.add(comicEntity) //agregar un nuevo comic
            notifyDataSetChanged() //guardar cambios
        }
    }

    //cargar lista de comic
    fun setComics(comics:MutableList<ComicEntity>)
    {
        this.comics=comics //se carga lista de comic
        notifyDataSetChanged() //se notifica que ha habido un cambio
    }

    //actualizar comic
    fun update(comicEntity: ComicEntity)
    {
        //index del comic que se quiere actualizar
        val index=comics.indexOf(comicEntity)

        //existe el comic
        if(index!=-1)
        {
            comics.set(index,comicEntity) //actualizando el comic
            notifyItemChanged(index) //refrescar el index del comic
        }
    }

    //eliminar comic
    fun delete(comicEntity: ComicEntity)
    {
        //index del comic que se quiere actualizar
        val index=comics.indexOf(comicEntity)

        //existe el comic
        if(index!=-1)
        {
            //eliminar comic de la colección
            comics.removeAt(index)
            notifyItemRemoved(index) //notificación de eliminación
        }
    }
}