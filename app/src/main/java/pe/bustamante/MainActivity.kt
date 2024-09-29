package pe.bustamante

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pe.bustamante.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    //variables perezosas
    private lateinit var mBinding:ActivityMainBinding
    private lateinit var mAdapter:ComicAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //instancia del view binding, vinculación de vistas
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /* //comentar evento
        //evento del botón btnSave
        mBinding.btnSave.setOnClickListener {
            val commerce=CommerceEntity(name=mBinding.etName.text.toString().trim())

            //se registra un nuevo comercio a la BD
            Thread {
                CommerceApplication.database.commerceDao().addCommerce(commerce)
            }.start()

            mAdapter.add(commerce)
        }
        */

        //llamara función lanzar fragmento
        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }

        //configuración del RecycleView
        setupRecyclerView()
    }

    override fun onClick(comicEntity: ComicEntity)
    {
        val argumento=Bundle()
        argumento.putLong(getString(R.string.arg_id),comicEntity.id)

        //llamar lanzamiento del fragmento
        launchEditFragment(argumento)
    }

    private fun setupRecyclerView()
    {
        //lista vacía
        mAdapter=ComicAdapter(mutableListOf(),this)

        //nro. de columnas 2
        mGridLayout= GridLayoutManager(this,1)

        //llamar listado de comercios
        getComics()

        //se pasar a configurar el RecyclerView
        mBinding.recyclerView.apply {

            //el tamaño no sea modificable
            setHasFixedSize(true)

            //asignando valores
            adapter=mAdapter
            layoutManager=mGridLayout
        }
    }

    //listado de comercios de la BD
    private fun getComics()
    {
        //ejecutar la consulta sincronizada al momento de guardar un comic
        doAsync {
            val comics=ComicApplication.database.comicDao().getAllComic()

            uiThread {
                mAdapter.setComics(comics)
            }
        }
    }

    override fun onFavorityComic(comicEntity: ComicEntity)
    {
        //cambiar el estado del icono de favorito
        comicEntity.isFavorite=!comicEntity.isFavorite

        //actualizar sincronizadamente
        doAsync {
            ComicApplication.database.comicDao().updateComic(comicEntity)

            uiThread {
                mAdapter.update(comicEntity)
            }
        }
    }

    override fun onDeleteComic(comicEntity: ComicEntity)
    {
        val items= arrayOf("Eliminar","Ir al sitio web")

        //multiples opciones
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items, DialogInterface.OnClickListener{ dialogInterface, i ->
                when(i) {
                    0 -> confirmDelete(comicEntity)
                    1 -> goToWebsite(comicEntity.website)
                }
            })
            .show()
    }


    private fun goToWebsite(website:String)
    {
        if(website.isEmpty()) {
            Toast.makeText(this,R.string.main_error_no_website, Toast.LENGTH_SHORT).show()
        }
        else
        {
            val websiteIntent= Intent().apply {
                action= Intent.ACTION_VIEW
                data= Uri.parse(website)
            }

            if(websiteIntent.resolveActivity(packageManager)!=null) {
                //llamar a la app explorador
                startActivity(websiteIntent)
            }
            else {
                Toast.makeText(this,R.string.main_error_no_resolve, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDelete(comicEntity: ComicEntity)
    {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm,
                DialogInterface.OnClickListener{ dialogInterface, i ->
                //eliminar comic
                doAsync {
                    //eliminar de la BD
                    ComicApplication.database.comicDao().deleteComic(comicEntity)

                    uiThread {
                        //eliminar de la lista de la ventana principal
                        mAdapter.delete(comicEntity)
                    }
                }
            })
            .setNegativeButton(R.string.dialog_delete_cancel,null)
            .show()
    }

    //lanzar el fragmento
    private fun launchEditFragment(args:Bundle?=null)
    {
        val fragment=EditComicFragment()

        if(args!=null) {
            fragment.arguments=args
        }

        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()

        //ver como se quiere mostrar el fragmento
        fragmentTransaction.add(R.id.containerMain,fragment)

        //volver a la actividad principal
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()

        //ocultar el botón flotante
        //mBinding.fab.hide()

        //llamar método
        hideFab()
    }

    override fun hideFab(isVisible: Boolean)
    {
        if(isVisible) {
            mBinding.fab.show()
        } else {
            mBinding.fab.hide()
        }
    }

    override fun addComic(comicEntity: ComicEntity) {
        mAdapter.add(comicEntity)
    }

    override fun updateComic(comicEntity: ComicEntity) {
        mAdapter.update(comicEntity)
    }
}