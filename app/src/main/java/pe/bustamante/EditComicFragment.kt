package pe.bustamante

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pe.bustamante.databinding.FragmentEditComicBinding

class EditComicFragment: Fragment() {
    //variable perezosa
    private lateinit var mBinding:FragmentEditComicBinding

    //se define
    private var mActivity:MainActivity?=null

    //se define variables para saber si se está guardando o editando
    private var mIsEditMode:Boolean=false
    private var mComicEntity:ComicEntity?=null

    //creación de vista
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        mBinding= FragmentEditComicBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    //para la vista creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val id=arguments?.getLong(getString(R.string.arg_id),0)

        if(id!=null && id!=0L) {
            /*Toast.makeText(activity,id.toString(),Toast.LENGTH_SHORT).show()*/
            mIsEditMode=true //modo editar comic

            //llamar comic y pintar en el fragmento
            getComic(id)
        }
        else {
            //Toast.makeText(activity,id.toString(),Toast.LENGTH_SHORT).show()

            mIsEditMode=false //modo guardar comic

            //inicializando comic
            mComicEntity=ComicEntity(name="", descripcion = "", categoria = "",photoUrl="")
        }

        setupTextFields()

        //acciones del bar
        setupActionBar()


    }

    private fun setupTextFields()
    {
        with(mBinding)
        {
            etName.addTextChangedListener {
                validateFieldsNEW(itName)
            }
            etDescripcion.addTextChangedListener {
                validateFieldsNEW(itDescripcion)
            }
            etCategoria.addTextChangedListener {
                validateFieldsNEW(itCategoria)
            }

            etPhotoURL.addTextChangedListener {
                validateFieldsNEW(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url:String)
    {
        //hacer aparecer la imagen
        Glide.with(this).load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(mBinding.imgPhoto)
    }

    private fun setupActionBar()
    {
        mActivity=activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mActivity?.supportActionBar?.title=
            if(mIsEditMode) {
                getString(R.string.edit_comic_title_edit)
            }
            else {
                getString(R.string.edit_comic_title_add)
            }

        //acceso al menú
        setHasOptionsMenu(true)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun getComic(id:Long)
    {
        doAsync {
            mComicEntity=ComicApplication.database.comicDao().getComicById(id)

            //pintar en el fragmento
            uiThread {
                if(mComicEntity!=null)
                {
                    with(mBinding)
                    {
                        etName.setText(mComicEntity!!.name)
                        etCategoria.setText(mComicEntity!!.categoria)
                        etDescripcion.setText(mComicEntity!!.descripcion)
                        etWebsite.setText(mComicEntity!!.website)
                        etPhotoURL.setText(mComicEntity!!.photoUrl)

                        Glide.with(activity!!)
                            .load(mComicEntity!!.photoUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop().into(imgPhoto)
                    }
                }
            }
        }
    }

    //al momento de inicar la actividad se llamará al menú de opciones
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.menu_save,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //seleccionar eventos dentro del menú de opciones
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when(item.itemId)
        {
            //ícono atrás del menu
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            //ícono save
            R.id.action_save ->
            {
                if(mComicEntity!=null &&
                    validateFieldsNEW(mBinding.tilPhotoUrl,mBinding.itCategoria,mBinding.itDescripcion,mBinding.itName))
                {
                    with(mComicEntity!!)
                    {
                        name=mBinding.etName.text.toString().trim()
                        descripcion=mBinding.etDescripcion.text.toString().trim()
                        categoria=mBinding.etCategoria.text.toString().trim()
                        website=mBinding.etWebsite.text.toString().trim()
                        photoUrl=mBinding.etPhotoURL.text.toString().trim()
                    }

                    doAsync {
                        if(mIsEditMode) {
                            //actualizar
                            ComicApplication.database.comicDao().updateComic(mComicEntity!!)
                        }
                        else {

                            mComicEntity!!.id= ComicApplication.database.comicDao().addComic(mComicEntity!!)
                        }

                        uiThread {
                            hideKeyboard()

                            if(mIsEditMode) //modo editar
                            {
                                mActivity?.updateComic(mComicEntity!!)

                                Snackbar.make(mBinding.root,R.string.edit_comic_message_update_success,
                                    Snackbar.LENGTH_SHORT).show()
                            }
                            else //modo guardar
                            {
                                mActivity?.addComic(mComicEntity!!)

                                Toast.makeText(mActivity,R.string.edit_comic_message_save_success,
                                    Toast.LENGTH_SHORT).show()
                            }

                            //volver a la ventana principal
                            mActivity?.onBackPressed()
                        }
                    }
                }

                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //terminar ciclo de vida del fragmento
    override fun onDestroy()
    {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title=getString(R.string.app_name)

        //mostrar el icono de la tienda
        mActivity?.hideFab(true)

        //disvincular del menú
        setHasOptionsMenu(false)

        super.onDestroy()
    }

    //método heredado
    override fun onDestroyView() {
        ocultar()
        super.onDestroyView()
    }

    //ocultar teclado
    @SuppressLint("UseRequireInsteadOfGet")
    private fun hideKeyboard()
    {
        val inputMM=mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputMM!=null) {
            inputMM.hideSoftInputFromWindow(view!!.windowToken,0)
        }
    }

    //funcion para cerrar el teclado cuando se da atrás parte superior
    fun ocultar() {
        val vieww = mActivity!!.currentFocus
        if (vieww != null) {
            //Aquí esta la magia
            val input =
                mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(vieww.windowToken, 0)
        }
    }



    private fun validateFieldsNEW(vararg textFields: TextInputLayout): Boolean
    {
        var isValid=true

        for(textField in textFields)
        {
            if(textField.editText?.text.toString().trim().isEmpty())
            {
                textField.error=getString(R.string.helper_required)
                isValid=false
            }
            else {
                textField.error=null
            }

            if(!isValid) {
                Snackbar.make(mBinding.root,R.string.edit_comic_message_valid, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        return isValid
    }
}