package com.example.streamplayer.UI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamplayer.R
import com.example.streamplayer.adapters.TrackListAdapter
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.viewmodel.ListViewModelFactury
import com.example.streamplayer.viewmodel.PositionViewModel
import com.example.streamplayer.viewmodel.SongViewModel
import java.util.*


class SongListFragment : Fragment()  {

    private var topListAdapter: TrackListAdapter? = null

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
//    private val viewModel: SongViewModel by activityViewModels()
//    private val viewModel: SongViewModel by activityViewModels()
    var tracks = mutableListOf<Tracks>()
      val positionViewModel: PositionViewModel by activityViewModels()
      val viewModel: SongViewModel by viewModels { ListViewModelFactury(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("MyLog","viewModel: $viewModel ")
        return inflater.inflate(R.layout.fragment_song_list, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topListAdapter = TrackListAdapter(tracks as ArrayList<Tracks>, object: TrackListAdapter.PositionTransfer{
            override fun onChangePosition(position: Int) {
                Log.d("MyLog","Position in ListFragment: $position ")

                positionViewModel.getTrackPosition(position)

            }

        })
        view.findViewById<RecyclerView>(R.id.list_tracks_rv).apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = topListAdapter


        //    if (viewModel != null) {
                viewModel.trackListLiveData.observe(this@SongListFragment.viewLifecycleOwner, { tracks ->
                    topListAdapter?.updateData(tracks)
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
                    view.findViewById<TextView>(R.id.tv_top_ad).visibility = GONE
                    view.findViewById<TextView>(R.id.tv_note_wait).visibility = GONE
                    Log.d("MyLog", "tracks: $tracks ")
                })
        //    }
            //    Log.d("MyLog","tracks: $viewModelFactor ")
            Log.d("MyLog", "viewModel: $viewModel ")
       //     Toast.makeText(activity, "Please wait", Toast.LENGTH_LONG).show()
            //  viewModel.fetch()


        }

    }
}


