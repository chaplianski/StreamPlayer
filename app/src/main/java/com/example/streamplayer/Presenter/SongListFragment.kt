package com.example.streamplayer.Presenter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamplayer.Adapters.TrackListAdapter
import com.example.streamplayer.R
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.Viewmodels.ListViewModelFactury
import com.example.streamplayer.Viewmodels.SongViewModel
import com.example.streamplayer.model.Tracks
import java.util.*


class SongListFragment : Fragment()  {

    private var topListAdapter: TrackListAdapter? = null

    var tracks = mutableListOf<Tracks>()
    val viewModel: SongViewModel by viewModels { ListViewModelFactury(requireActivity().application) }
    val repository = RepositoryInstance.getMusicRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MyLog","viewModel: $viewModel ")
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    @SuppressLint("Recycle")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logo = view.findViewById<ImageView>(R.id.iv_logo)
        val noteLeft = view.findViewById<TextView>(R.id.tv_top_left)
        val noteRight = view.findViewById<TextView>(R.id.tv_top_right)
        val noteWait = view.findViewById<TextView>(R.id.tv_note_wait)

        val set = AnimatorSet()
        val screenOrientation = resources.configuration.orientation
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT){
            set.playSequentially(
                firstDownLogo(logo, -100f, 950f),
                CrashNotes(noteLeft,noteRight,noteWait,logo),
                secondDownLogo(logo, 950f),
                secondUpLogo(logo, 800f),
                thirdDownLogo(logo, 950f),
                waitNoteMotion(noteWait),
                RotateLogo(logo),
            )
            set.start()
        }
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            set.playSequentially(
                firstDownLogo(logo, -100f, 1500f),
                CrashNotes(noteLeft,noteRight,noteWait,logo),
                secondDownLogo(logo, 1500f),
                secondUpLogo(logo, 1350f),
                thirdDownLogo(logo, 1500f),
                waitNoteMotion(noteWait),
                RotateLogo(logo),
            )
            set.start()
        }


        topListAdapter = TrackListAdapter(tracks as ArrayList<Tracks>, object: TrackListAdapter.PositionTransfer{
            override fun onChangePosition(position: Int) {
                Log.d("MyLog","Position in ListFragment: $position ")
                repository?.getTrackPosition(position)
            }

        })
        view.findViewById<RecyclerView>(R.id.list_tracks_rv).apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = topListAdapter

                viewModel.trackListLiveData.observe(this@SongListFragment.viewLifecycleOwner, { tracks ->
                    topListAdapter?.updateData(tracks)
               //     view.findViewById<TextView>(R.id.tv_top_right).visibility = GONE
               //     view.findViewById<TextView>(R.id.tv_note_wait).visibility = GONE
                    logo.visibility = GONE
                    Log.d("MyLog", "tracks: $tracks ")
                })
            Log.d("MyLog", "viewModel: $viewModel ")
  }

    }

    fun firstDownLogo (logo: View, from: Float, to: Float): Animator{
        val logoBegin = ObjectAnimator.ofFloat(logo,View.TRANSLATION_Y, from, to)
        logoBegin.interpolator = AccelerateInterpolator()
        logoBegin.duration = 700
        return logoBegin
    }
    fun firstUpLogo (logo: View): Animator{
        val logoBegin = ObjectAnimator.ofFloat(logo,View.TRANSLATION_Y, 600f)
        logoBegin.interpolator = DecelerateInterpolator()
        logoBegin.duration = 400
        return logoBegin
    }
    fun secondDownLogo (logo: View, to: Float): Animator{
        val logoBegin = ObjectAnimator.ofFloat(logo,View.TRANSLATION_Y, to)
        logoBegin.interpolator = AccelerateInterpolator()
        logoBegin.duration = 200
        return logoBegin
    }
    fun secondUpLogo (logo: View, to: Float): Animator{
        val logoBegin = ObjectAnimator.ofFloat(logo,View.TRANSLATION_Y, to)
        logoBegin.interpolator = DecelerateInterpolator()
        logoBegin.duration = 150
        return logoBegin
    }
    fun thirdDownLogo (logo: View, to: Float): Animator{
        val logoBegin = ObjectAnimator.ofFloat(logo,View.TRANSLATION_Y, to)
        logoBegin.interpolator = AccelerateInterpolator()
        logoBegin.duration = 100
        return logoBegin
    }

    fun RotateLogo (logo: View): Animator{
        val logoBegin = ObjectAnimator.ofFloat(logo,View.ROTATION,-360f, 0f)
        logoBegin.duration = 1000
        logoBegin.repeatCount = 30
        return logoBegin
    }
    fun CrashNotes (noteLeft: View, noteRight: View, noteWait: View, logo: View): Animator{
        val animationSet = AnimatorSet()
        animationSet.playTogether(
            leftCrashMotion(noteLeft),
            rightCrashMotion(noteRight),
            downCrashMotion(noteWait),
            firstUpLogo(logo)
        )
        animationSet.duration = 200
        return animationSet
    }
    fun waitNoteMotion(noteWait: View): Animator{
        val note1 = ObjectAnimator.ofFloat(noteWait, View.TRANSLATION_Y, 40f)
        note1.duration = 500
        return note1
    }
    fun leftCrashMotion (noteLeft: View): Animator {
        val note1 = ObjectAnimator.ofFloat(noteLeft, View.ROTATION,  15f)
        return note1
    }
    fun rightCrashMotion (noteRight: View): Animator {
        val note1 = ObjectAnimator.ofFloat(noteRight, View.ROTATION,  -15f)
        return note1
    }
    fun downCrashMotion (noteWait: View): Animator {
        val note1 = ObjectAnimator.ofFloat(noteWait, View.TRANSLATION_Y,  150f)
        return note1
    }


}


