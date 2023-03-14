package es.frantoribio.recetas.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import es.frantoribio.recetas.R
import es.frantoribio.recetas.RecetasApplication
import es.frantoribio.recetas.databinding.FragmentEditRecetaBinding
import es.frantoribio.recetas.model.Receta
import kotlinx.coroutines.launch
import java.util.*


class EditRecetaFragment : Fragment(), MenuProvider {
    val oldReceta by navArgs<EditRecetaFragmentArgs>()
    lateinit var binding: FragmentEditRecetaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditRecetaBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this)
        binding.editName.setText(oldReceta.data.name)
        binding.editProducto.setText(oldReceta.data.producto)
        binding.editCategoria.setText(oldReceta.data.categorias)
        binding.editWebUrl.setText(oldReceta.data.webUrl)
        binding.btnDone.setOnClickListener {
            updateReceta(it)
        }
    }

    private fun updateReceta(it: View?) {
        val name = binding.editName.text.toString()
        val producto = binding.editProducto.text.toString()
        val categoria = binding.editCategoria.text.toString()
        val webUrl = binding.editWebUrl.text.toString()
        val d = Date()
        val recetaDate: CharSequence = DateFormat.format("MMMM d, yyyy ", d.time)
        val data = Receta (
            oldReceta.data.id,
            name = name,
            producto = producto,
            categorias = categoria,
            webUrl = webUrl,
            date = recetaDate.toString(),
            iscomplited = oldReceta.data.iscomplited
        )
        lifecycleScope.launch {
            RecetasApplication.database.recetaDao().updateReceta(data)
        }
        Toast.makeText(requireContext(),"Receta modificada", Toast.LENGTH_SHORT).show()
        Navigation.findNavController(requireView())
            .navigate(R.id.action_editRecetaFragment_to_homeFragment)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.menu_delete){
            val bottomSheet = BottomSheetDialog(requireContext(),R.style.BottonSheetStyle)
            bottomSheet.setContentView(R.layout.dialog_delete)
            val textYes = bottomSheet.findViewById<TextView>(R.id.dialogYes)
            val textNo  = bottomSheet.findViewById<TextView>(R.id.dialogNo)

            textYes?.setOnClickListener {
                lifecycleScope.launch {
                    RecetasApplication.database.recetaDao().deleteReceta(oldReceta.data.id!!)
                }
                Toast.makeText(requireContext(),"Receta borrada", Toast.LENGTH_SHORT).show()
                bottomSheet.dismiss()
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_editRecetaFragment_to_homeFragment)
            }

            textNo?.setOnClickListener {
                bottomSheet.dismiss()
            }

            bottomSheet.show()
        }
        return true
    }

    override fun onDestroyView() {
        activity?.removeMenuProvider(this)
        super.onDestroyView()
    }
}