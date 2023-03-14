package es.frantoribio.recetas.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import es.frantoribio.recetas.R
import es.frantoribio.recetas.RecetasApplication
import es.frantoribio.recetas.adapter.RecetasAdapter
import es.frantoribio.recetas.adapter.RecetasOnClickListener
import es.frantoribio.recetas.databinding.FragmentHomeBinding
import es.frantoribio.recetas.model.Receta
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), RecetasOnClickListener {
    lateinit var  binding : FragmentHomeBinding
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: RecetasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddRcetas.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_homeFragment_to_createRecetaFragment)
        }
        getAllRecetas()
        setupRecyclerView()
        setFilter()
    }

    private fun setFilter() {
        binding.allRecetas.setOnClickListener {
            getAllRecetas()
            setupRecyclerView()
        }
        binding.filterMeGusta.setOnClickListener {
            getComplitedReceta()
            setupRecyclerView()
        }
        binding.filterNoMeGusta.setOnClickListener {
            getPendingReceta()
            setupRecyclerView()
        }
    }

    private fun getPendingReceta() {
        lifecycleScope.launch{
            val recetasList = RecetasApplication.database.recetaDao().getPendingReceta()
            mAdapter.setRecetas(recetasList)
        }
    }

    private fun getComplitedReceta() {
        lifecycleScope.launch {
            val recetaComplited = RecetasApplication.database.recetaDao().getCompleatedReceta()
            mAdapter.setRecetas(recetaComplited)
        }
    }

    private fun setupRecyclerView() {
        mAdapter = RecetasAdapter(mutableListOf(),this)
        mGridLayoutManager = GridLayoutManager(requireContext(),2)

        binding.recycler.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayoutManager
            adapter = mAdapter
        }
    }

    private fun getAllRecetas() {
        lifecycleScope.launch {
            val recetas = RecetasApplication.database.recetaDao().getAllRecetas()
            mAdapter.setRecetas(recetas)
        }
    }

    override fun onComplitedReceta(receta: Receta) {
        receta.iscomplited = !receta.iscomplited
        lifecycleScope.launch {
            val recetasList = RecetasApplication.database.recetaDao().updateReceta(receta)
            mAdapter.update(receta)
        }
    }

    override fun onClickReceta(receta: Receta) {
        val action = HomeFragmentDirections.actionHomeFragmentToEditRecetaFragment(receta)
        Navigation.findNavController(requireView()).navigate(action)
    }
}