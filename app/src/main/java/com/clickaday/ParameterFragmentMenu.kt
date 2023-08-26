package com.clickaday

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


class ParameterFragmentMenu : Fragment() {

    private lateinit var passwordSwitch: Switch
    private lateinit var folderEditText: EditText
    private lateinit var folderImageButton: ImageButton
    private lateinit var smallItalicText: TextView
    private lateinit var backgroundView: View

    private var enableButtonListener: Interfaces.EnableButtonListener? = null

    override fun onAttach(context: Context) {//called when the fragment is attached to an activity
        super.onAttach(context)
        enableButtonListener = context as? Interfaces.EnableButtonListener
    }

    override fun onDetach() {
        super.onDetach()
        enableButtonListener?.enablePhotoBtn()
        enableButtonListener = null
    }


    companion object {
        const val TAG_FRAG = "TAG_PARAMETER_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_menu_parameter_fragment, container, false)
        initializeUI(view, requireContext())

        return view
    }

    private fun initializeUI(v: View, ctx: Context) {
        passwordSwitch = v.findViewById(R.id.switch_button_password)
        folderEditText = v.findViewById(R.id.edit_txt_item_menu_changer)
        folderImageButton = v.findViewById(R.id.image_button)
        smallItalicText = v.findViewById(R.id.italic_txt_version)
        backgroundView=v.findViewById(R.id.img_background_menu_param)

        passwordSwitch.setOnClickListener {
            Toast.makeText(ctx, "CLICK switch", Toast.LENGTH_SHORT).show()

        }
        folderImageButton.setOnClickListener {
            Toast.makeText(ctx, "CLICK folder", Toast.LENGTH_SHORT).show()

        }

        backgroundView.setOnClickListener {
            USRTools.closeFragmentWithTag(TAG_FRAG, requireActivity().supportFragmentManager)
            enableButtonListener?.enablePhotoBtn()
        }
    }


}