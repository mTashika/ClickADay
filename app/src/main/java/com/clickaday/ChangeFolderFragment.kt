package com.clickaday

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class ChangeFolderFragment : Fragment() {

    private lateinit var okButton: Button
    private lateinit var cancelButton: Button
    private lateinit var backgroundView: View
    private lateinit var editText: EditText

    private var returnToMainActivityListener: Interfaces.ReturnToMainActivity? = null

    companion object {
        const val TAG_FRAG = "TAG_CHANGE_FOLDER_FRAGMENT"
        const val MAX_FOLDER_NAME_LENGTH = 20
        const val MIN_FOLDER_NAME_LENGTH = 4
        const val RESULT_KEY = "REQUEST_KEY_FOLDER_CHANGE"
        const val RESPOND_VAL_KEY = "RESPOND_KEY_FOLDER_CHANGE"

    }

    override fun onAttach(context: Context) {//called when the fragment is attached to an activity
        super.onAttach(context)
        returnToMainActivityListener = context as? Interfaces.ReturnToMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        returnToMainActivityListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.set_folder_change_layout, container, false)
        initializeView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        okButton.setOnClickListener {
            val newFolderName = editText.text.toString()
            if (USRTools.isValidDirectoryName(newFolderName)) {
                PreferencesTools.savePrefStr(
                    requireContext(),
                    newFolderName,
                    PreferencesTools.PREF_FOLDER_PICT
                )
                Toast.makeText(requireContext(), "Folder name saved", Toast.LENGTH_SHORT)
                    .show()
                returnToMainActivityListener?.launchDisplayImg()
                returnVal(true)
            } else {
                Toast.makeText(requireContext(), "Folder name not compatible", Toast.LENGTH_SHORT)
                    .show()
                returnVal(false)
            }
        }

        cancelButton.setOnClickListener {
            returnVal(false)
        }
        backgroundView.setOnClickListener {
            returnVal(false)
        }
    }

    private fun returnVal(value: Boolean) {
        val bundle = Bundle().apply { putBoolean(RESPOND_VAL_KEY, value) }
        requireActivity().supportFragmentManager.setFragmentResult(RESULT_KEY, bundle)
        closeFragment()
    }

    private fun closeFragment() {
        USRTools.closeFragmentWithTag(TAG_FRAG, requireActivity().supportFragmentManager)
    }

    private fun initializeView(v: View) {
        backgroundView = v.findViewById(R.id.img_background_select_folder)
        okButton = v.findViewById(R.id.okButton_change_folder)
        cancelButton = v.findViewById(R.id.cancelButton_change_folder)
        editText = v.findViewById(R.id.newEditText_change_folder)

    }



}
