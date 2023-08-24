package com.clickaday

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


class ParameterFragmentMenu : Fragment() {
    private lateinit var passwordSwitch: Switch
    private lateinit var folderEditText: EditText
    private lateinit var folderImageButton: ImageButton
    private lateinit var smallItalicText: TextView

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

        passwordSwitch.setOnClickListener {
            Toast.makeText(ctx, "CLICK switch", Toast.LENGTH_SHORT).show()

        }
        folderImageButton.setOnClickListener {
            Toast.makeText(ctx, "CLICK folder", Toast.LENGTH_SHORT).show()

        }

        v.setOnClickListener {
            Toast.makeText(requireContext(), "CLICK view", Toast.LENGTH_SHORT).show()
            USRTools.closeFragmentWithTag(TAG_FRAG, requireActivity().supportFragmentManager)
        }
        //ADD click listener for all the other components (to avoir closing if we click the txt)
    }

}