package com.clickaday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class PasswordFragment : Fragment() {

    lateinit var passwordTxt: TextView
    private lateinit var backgroundView: View
    private lateinit var deleteAll: Button
    private lateinit var deleteOne: Button
    private lateinit var Button1: Button
    private lateinit var Button2: Button
    private lateinit var Button3: Button
    private lateinit var Button4: Button
    private lateinit var Button5: Button
    private lateinit var Button6: Button
    private lateinit var Button7: Button
    private lateinit var Button8: Button
    private lateinit var Button9: Button
    private lateinit var Button0: Button
    private lateinit var ButtonCancel: Button
    private lateinit var ButtonOk: Button

    private var isToSave: Boolean = false


    companion object {
        const val TAG_FRAG = "TAG_PASSWORD_FRAGMENT"
        const val TAG_BOOL = "IS_TO_SAVE"
        const val MAX_PASSWORD_LENGTH = 20
        const val MIN_PASSWORD_LENGTH = 4
        const val RESULT_KEY = "RESULT_KEY_PASSWORD"
        const val RESPOND_VAL_KEY = "RESPOND_VAL_KEY_PASSWORD"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.password_enter_layout, container, false)
        isToSave = arguments?.getBoolean(TAG_BOOL) ?: false

        initializeView(view)
        initializeListener()

        return view
    }

    private fun initializeView(v: View) {
        //initialise view
        passwordTxt = v.findViewById(R.id.textViewPassword)
        backgroundView = v.findViewById(R.id.img_background_password)
        deleteAll = v.findViewById(R.id.delete_all_number_password)
        deleteOne = v.findViewById(R.id.delete_number_password)
        Button1 = v.findViewById(R.id.Button1)
        Button2 = v.findViewById(R.id.Button2)
        Button3 = v.findViewById(R.id.Button3)
        Button4 = v.findViewById(R.id.Button4)
        Button5 = v.findViewById(R.id.Button5)
        Button6 = v.findViewById(R.id.Button6)
        Button7 = v.findViewById(R.id.Button7)
        Button8 = v.findViewById(R.id.Button8)
        Button9 = v.findViewById(R.id.Button9)
        Button0 = v.findViewById(R.id.Button0)
        ButtonCancel = v.findViewById(R.id.cancel_btn_password)
        ButtonOk = v.findViewById(R.id.ok_btn_password)
    }

    private fun initializeListener() {

        Button1.setOnClickListener { addToTextView(1) }
        Button2.setOnClickListener { addToTextView(2) }
        Button3.setOnClickListener { addToTextView(3) }
        Button4.setOnClickListener { addToTextView(4) }
        Button5.setOnClickListener { addToTextView(5) }
        Button6.setOnClickListener { addToTextView(6) }
        Button7.setOnClickListener { addToTextView(7) }
        Button8.setOnClickListener { addToTextView(8) }
        Button9.setOnClickListener { addToTextView(9) }
        Button0.setOnClickListener { addToTextView(0) }
        deleteAll.setOnClickListener { passwordTxt.text = "" }

        deleteOne.setOnClickListener {
            var txt = passwordTxt.text.toString()
            if (txt.isNotEmpty()) {
                txt = txt.substring(0, txt.length - 1)
            }
            passwordTxt.text = txt
        }

        ButtonCancel.setOnClickListener {
            returnVal(false, RESULT_KEY)
        }
        ButtonOk.setOnClickListener {
            val enteredPassword = passwordTxt.text.toString()
            if (isToSave) {
                if ((enteredPassword.length >= MIN_PASSWORD_LENGTH) and (enteredPassword.length <= MAX_PASSWORD_LENGTH)) {
                    //save password
                    PreferencesTools.savePrefStrCrypt(
                        requireContext(),
                        enteredPassword,
                        PreferencesTools.PREF_PASSWORD_VALUE_CRYPT
                    )
                    //save is password
                    PreferencesTools.savePrefBool(
                        requireContext(),
                        true,
                        PreferencesTools.PREF_PASSWORD
                    )
                    Toast.makeText(requireContext(), "Password saved", Toast.LENGTH_SHORT).show()
                    returnVal(true, RESULT_KEY)

                } else {
                    //save is password
                    PreferencesTools.savePrefBool(
                        requireContext(),
                        false,
                        PreferencesTools.PREF_PASSWORD
                    )
                    Toast.makeText(
                        requireContext(),
                        "Password not saved : Invalid lenght",
                        Toast.LENGTH_SHORT
                    ).show()
                    returnVal(false, RESULT_KEY)
                }

            } else {
                //verify if the password entered is the good one

                if (enteredPassword == PreferencesTools.getPrefStrCrypt(
                        requireContext(),
                        PreferencesTools.PREF_PASSWORD_VALUE_CRYPT
                    )
                ) {
                    //return that was a good value
                    returnVal(true, RESULT_KEY)

                } else {
                    //return that was a bad value
                    returnVal(false, RESULT_KEY)
                }
            }

        }
        backgroundView.setOnClickListener {
            returnVal(false, RESULT_KEY)
        }
    }

    private fun closeFrag() {
        USRTools.closeFragmentWithTag(TAG_FRAG, requireActivity().supportFragmentManager)
    }

    private fun returnVal(value: Boolean, key: String) {
        val bundle = Bundle().apply { putBoolean(RESPOND_VAL_KEY, value) }
        requireActivity().supportFragmentManager.setFragmentResult(key, bundle)
        closeFrag()
    }


    private fun addToTextView(number: Int) {
        val currentText = passwordTxt.text.toString()

        if (currentText.length < MAX_PASSWORD_LENGTH) {
            val newValue: String
            newValue = if (currentText.isNotEmpty()) {
                currentText + number.toString()
            } else {
                number.toString()
            }

            passwordTxt.text = newValue
        } else {
            Toast.makeText(requireContext(), "Too long", Toast.LENGTH_SHORT).show()
        }
    }

}

