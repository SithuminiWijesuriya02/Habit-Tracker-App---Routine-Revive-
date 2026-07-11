package com.example.labexam3.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.labexam3.R

/**
 * Improved onboarding fragment with beautiful design
 */
class OnboardingFragment : Fragment() {
    
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE_RES = "image_res"
        private const val ARG_BG_COLOR = "bg_color"
        
        fun newInstance(title: String, description: String, imageRes: Int, backgroundColor: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION, description)
            args.putInt(ARG_IMAGE_RES, imageRes)
            args.putInt(ARG_BG_COLOR, backgroundColor)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_page, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val description = arguments?.getString(ARG_DESCRIPTION) ?: ""
        val imageRes = arguments?.getInt(ARG_IMAGE_RES) ?: 0
        val bgColor = arguments?.getInt(ARG_BG_COLOR) ?: 0
        
        view.findViewById<TextView>(R.id.onboarding_title).text = title
        view.findViewById<TextView>(R.id.onboarding_description).text = description
        view.findViewById<ImageView>(R.id.onboarding_image).setImageResource(imageRes)
        
        // Set background color
        if (bgColor != 0) {
            view.findViewById<CardView>(R.id.onboarding_container)
                .setCardBackgroundColor(ContextCompat.getColor(requireContext(), bgColor))
        }
    }
}
