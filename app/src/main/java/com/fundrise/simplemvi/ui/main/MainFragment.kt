package com.fundrise.simplemvi.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.lifecycle.whenCreated
import com.fundrise.simplemvi.databinding.MainFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * A simple Fragment that mimics moderately complex state changes.
 *
 * When the user taps "Click Me" the intent is passed and handled to the ViewModel, the view
 * model then dictates the resulting state, be it error, loading, or content view.
 */
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private lateinit var actor: MainActor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actor = MainActor(viewModel::handleIntent)

        observeChanges()

        binding.errorRefresh.setOnClickListener {
            actor.tapErrorRefresh()
        }

        binding.incrementButton.setOnClickListener {
            actor.tapClickMe()
        }
    }

    private fun observeChanges() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.state.collect {
                Log.d("MVIDEMO_FRAGMENT", "State: $it")

                when (it) {
                    MainState.Init -> handleInit()
                    MainState.Loading -> handleLoading()
                    MainState.Error -> handleError()
                    is MainState.Content -> handleContent(it)
                }
            }
        }
    }

    private fun handleInit() {
        actor.initialize(8)
    }

    private fun handleLoading() {
        binding.loading.visibility = View.VISIBLE
        binding.error.visibility = View.GONE
        binding.main.visibility = View.GONE
    }

    private fun handleError() {
        binding.error.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
        binding.main.visibility = View.GONE
    }

    private fun handleContent(state: MainState.Content) {
        binding.main.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
        binding.error.visibility = View.GONE

        binding.timesClicked.text = state.timesClicked.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        with (binding) {
            errorRefresh.setOnClickListener(null)
            incrementButton.setOnClickListener(null)
        }

        _binding = null
    }
}

