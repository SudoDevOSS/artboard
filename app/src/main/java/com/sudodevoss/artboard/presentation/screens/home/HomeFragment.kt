package com.sudodevoss.artboard.presentation.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.sudodevoss.App
import com.sudodevoss.artboard.R
import com.sudodevoss.artboard.application.extensions.hideKeyboard
import com.sudodevoss.artboard.presentation.adapters.mediaTracksAdapter.MediaTracksAdapter
import com.sudodevoss.artboard.presentation.components.itemDecorators.CustomSpaceDecorator
import com.sudodevoss.artboard.presentation.components.itemDecorators.SpacingOrientation
import com.sudodevoss.artboard.utils.SnackBarUtils
import com.sudodevoss.artboard.utils.imageLoader.ImageLoader
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.search_box.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.instance

@ExperimentalPagingApi
class HomeFragment : Fragment() {
    private val mViewModel by App.diContainer.instance<HomeViewModel>()
    private lateinit var mAdapter: MediaTracksAdapter
    private val mImageLoader by App.diContainer.instance<ImageLoader>()

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

    private fun initUI() {
        initSearchBar()
        initRecyclerView()
        initProgress()
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
            searchIcon.visibility = if (it) View.GONE else View.VISIBLE
            searchActivityIndicator.visibility = if (it) View.VISIBLE else View.GONE
        })
        mViewModel.searchQuery.observe(viewLifecycleOwner, {
            mAdapter.refresh()
        })
    }

    private fun initRecyclerView() {
        mAdapter = MediaTracksAdapter(mutableListOf(), mImageLoader)
        recyclerViewMediaTracks.adapter = mAdapter
        recyclerViewMediaTracks.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewMediaTracks.addItemDecoration(
            CustomSpaceDecorator(16, SpacingOrientation.Both)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest {
                mViewModel.searchActivityIndicatorVisible.value = it.refresh is LoadState.Loading
                mViewModel.pageActivityIndicatorVisible.value = it.source.append is LoadState.Loading
                dismissError()
                if (it.refresh is LoadState.Error) {
                    val err = it.refresh as LoadState.Error
                    showError(err.error)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.mediaTracksSteamFlow.collect {
                lifecycleScope.launch {
                    mAdapter.submitData(it)
                }
            }
        }
    }

    private fun initProgress() {
        mViewModel.pageActivityIndicatorVisible.observe(viewLifecycleOwner, {
            progressBarPageLoader.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun showError(exception: Throwable) {
        SnackBarUtils.instance.show(requireActivity(), exception.toString())
    }

    private fun dismissError() {
        SnackBarUtils.instance.dismiss()
    }
}