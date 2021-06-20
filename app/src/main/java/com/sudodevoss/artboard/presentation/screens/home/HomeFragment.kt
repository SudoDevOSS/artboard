package com.sudodevoss.artboard.presentation.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudodevoss.App
import com.sudodevoss.artboard.R
import com.sudodevoss.artboard.application.extensions.hideKeyboard
import com.sudodevoss.artboard.presentation.adapters.MediaTracksAdapter
import com.sudodevoss.artboard.presentation.components.itemDecorators.CustomSpaceDecorator
import com.sudodevoss.artboard.presentation.components.itemDecorators.SpacingOrientation
import com.sudodevoss.artboard.utils.SnackBarUtils
import com.sudodevoss.artboard.utils.imageLoader.ImageLoader
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.search_box.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.instance

class HomeFragment : Fragment() {
    private val mViewModel by App.diContainer.instance<HomeViewModel>()
    private lateinit var mAdapter: MediaTracksAdapter
    private val mImageLoader by App.diContainer.instance<ImageLoader>()
    private var mPage = 0

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun canLoadMorePages(): Boolean {
        return mPage <= 5
    }

    private fun initUI() {
        initSearchBar()
        initRecyclerView()
        initProgress()
        initUiStateListener()
    }

    private fun initSearchBar() {
        searchField.addTextChangedListener {
            mViewModel.searchChannel.offer(it.toString())
        }
        searchField.setOnEditorActionListener { v, actionId, event ->
            requireContext().hideKeyboard(v)
            true
        }
        mViewModel.searchActivityIndicatorVisible.observe(viewLifecycleOwner, {
            if (it) {
                mPage = 0
                mAdapter.update(emptyList(), true)
            }
            searchIcon.visibility = if (it) View.GONE else View.VISIBLE
            searchActivityIndicator.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun initRecyclerView() {
        mAdapter = MediaTracksAdapter(mutableListOf(), mImageLoader)
        recyclerViewMediaTracks.adapter = mAdapter
        recyclerViewMediaTracks.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewMediaTracks.addItemDecoration(
            CustomSpaceDecorator(16, SpacingOrientation.Both)
        )
        recyclerViewMediaTracks.setHasFixedSize(true)
        mViewModel.mediaTracks.observe(viewLifecycleOwner, {
            mAdapter.update(it)
        })

        recyclerViewMediaTracks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)
                    && mViewModel.pageActivityIndicatorVisible.value != true
                    && canLoadMorePages()
                ) {
                    mViewModel.pageChannel.offer(mPage++)
                }
            }
        })
    }

    private fun initProgress() {
        mViewModel.pageActivityIndicatorVisible.observe(viewLifecycleOwner, {
            progressBarPageLoader.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun initUiStateListener() {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                mViewModel.uiState.collect {
                    when (it) {
                        is HomeViewUIState.Success -> dismissError()
                        is HomeViewUIState.Error -> showError(it.exception)
                    }
                }
            }
        }
    }

    private fun showError(exception: Throwable) {
        SnackBarUtils.instance.show(requireActivity(), exception.toString())
    }

    private fun dismissError() {
        SnackBarUtils.instance.dismiss()
    }
}