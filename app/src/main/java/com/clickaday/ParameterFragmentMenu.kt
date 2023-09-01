package com.clickaday

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment


class ParameterFragmentMenu : Fragment() {

    private lateinit var passwordSwitch: SwitchCompat
    private lateinit var blurSwitch: SwitchCompat
    private lateinit var descriptionSwitch: SwitchCompat
    private lateinit var folderTxtView: TextView
    private lateinit var folderImageButton: ImageButton
    private lateinit var smallItalicText: TextView
    private lateinit var backgroundView: View

    private var returnToMainActivityListener: Interfaces.ReturnToMainActivity? = null

    override fun onAttach(context: Context) {//called when the fragment is attached to an activity
        super.onAttach(context)
        returnToMainActivityListener = context as? Interfaces.ReturnToMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        returnToMainActivityListener = null
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
        initializeView(view)
        initializeActionListener()

        return view
    }

    private fun initializeView(v: View) {
        //initialise view
        passwordSwitch = v.findViewById(R.id.switch_button_password)
        blurSwitch = v.findViewById(R.id.switch_button_blur_pic)
        descriptionSwitch = v.findViewById(R.id.switch_button_description_pic)
        folderTxtView = v.findViewById(R.id.edit_txt_item_menu_changer)
        folderImageButton = v.findViewById(R.id.image_button)
        smallItalicText = v.findViewById(R.id.italic_txt_version)
        backgroundView = v.findViewById(R.id.img_background_menu_param)

        //initialize values
        passwordSwitch.isChecked =
            PreferencesTools.getPrefBool(requireContext(), PreferencesTools.PREF_PASSWORD)
        blurSwitch.isChecked =
            !PreferencesTools.getPrefBool(requireContext(), PreferencesTools.PREF_BLUR_IMG)
        descriptionSwitch.isChecked =
            PreferencesTools.getPrefBool(requireContext(), PreferencesTools.PREF_DESCRIPTION_IMG)
        folderTxtView.text =
            PreferencesTools.getPrefStr(
                requireContext(),
                PreferencesTools.PREF_FOLDER_PICT
            )

    }

    private fun initializeActionListener() {

        passwordSwitch.setOnClickListener {
            val curPasswordPref =
                PreferencesTools.getPrefBool(requireContext(), PreferencesTools.PREF_PASSWORD)

            val fragment: Fragment?
            //if is to SAVE
            if (passwordSwitch.isChecked && !curPasswordPref) {

                requireActivity().supportFragmentManager.setFragmentResultListener(
                    PasswordFragment.RESULT_KEY,
                    viewLifecycleOwner
                ) { _, _ ->
                    passwordSwitch.isChecked = PreferencesTools.getPrefBool(
                        requireContext(),
                        PreferencesTools.PREF_PASSWORD
                    )

                }
                //launch the fragment
                fragment = USRTools.buildPasswordFragment(true)
                USRTools.launchFragment(
                    fragment,
                    R.id.frame_layout_main_act2,
                    PasswordFragment.TAG_FRAG,
                    requireActivity().supportFragmentManager
                )

            }
            //if is to REMOVE
            else if (!passwordSwitch.isChecked && curPasswordPref) {
                checkPasswordAndPerformActions(
                    actionIfCorrect = {
                        PreferencesTools.savePrefStrCrypt(
                            requireContext(),
                            "",
                            PreferencesTools.PREF_PASSWORD_VALUE_CRYPT
                        )//set password to ""
                        PreferencesTools.savePrefBool(
                            requireContext(),
                            false,
                            PreferencesTools.PREF_PASSWORD
                        )//set is password to false
                        Toast.makeText(
                            requireContext(),
                            "Password successfully removed",
                            Toast.LENGTH_SHORT
                        ).show()
                        passwordSwitch.isChecked = PreferencesTools.getPrefBool(
                            requireContext(),
                            PreferencesTools.PREF_PASSWORD
                        )

                    },
                    actionIfIncorrect = {
                        Toast.makeText(
                            requireContext(),
                            "Password not removed",
                            Toast.LENGTH_SHORT
                        ).show()
                        passwordSwitch.isChecked = PreferencesTools.getPrefBool(
                            requireContext(),
                            PreferencesTools.PREF_PASSWORD
                        )

                    }
                )

            }

        }

        blurSwitch.setOnClickListener {
            if (PreferencesTools.getPrefBool(
                    requireContext(),
                    PreferencesTools.PREF_PASSWORD
                )
            ) {//If there is a password : ask password
                checkPasswordAndPerformActions(
                    actionIfCorrect = {
                        PreferencesTools.savePrefBool(
                            requireContext(),
                            !blurSwitch.isChecked,
                            PreferencesTools.PREF_BLUR_IMG

                        )
                        returnToMainActivityListener?.launchDisplayImg()
                    },
                    actionIfIncorrect = {
                        blurSwitch.isChecked = PreferencesTools.getPrefBool(
                            requireContext(),
                            PreferencesTools.PREF_BLUR_IMG
                        )

                    }
                )

            } else {//there is no password : Save in the preferences
                PreferencesTools.savePrefBool(
                    requireContext(),
                    !blurSwitch.isChecked,
                    PreferencesTools.PREF_BLUR_IMG
                )
                returnToMainActivityListener?.launchDisplayImg()
            }
        }



        descriptionSwitch.setOnClickListener {
            if (PreferencesTools.getPrefBool(
                    requireContext(),
                    PreferencesTools.PREF_PASSWORD
                )
            ) {//If there is a password : ask password
                //launch the fragment
                checkPasswordAndPerformActions(
                    actionIfCorrect = {
                        PreferencesTools.savePrefBool(
                            requireContext(),
                            descriptionSwitch.isChecked,
                            PreferencesTools.PREF_DESCRIPTION_IMG
                        )
                        returnToMainActivityListener?.launchDayImg()
                    },
                    actionIfIncorrect = {
                        descriptionSwitch.isChecked = PreferencesTools.getPrefBool(
                            requireContext(),
                            PreferencesTools.PREF_DESCRIPTION_IMG
                        )
                    }
                )
            } else {//there is no password
                //Save in the preferences
                PreferencesTools.savePrefBool(
                    requireContext(),
                    descriptionSwitch.isChecked,
                    PreferencesTools.PREF_DESCRIPTION_IMG
                )
                returnToMainActivityListener?.launchDayImg()

            }
        }



        folderImageButton.setOnClickListener {
            if (PreferencesTools.getPrefBool(
                    requireContext(),
                    PreferencesTools.PREF_PASSWORD
                )
            ) {//If there is a password : ask password
                checkPasswordAndPerformActions(
                    actionIfCorrect = {

                        askFolderWithFrag {
                            folderTxtView.text = PreferencesTools.getPrefStr(
                                requireContext(),
                                PreferencesTools.PREF_FOLDER_PICT
                            )
                        }

                    },
                    actionIfIncorrect = {
                        Toast.makeText(
                            requireContext(),
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                )


            } else {//there is no password : ask new folder (launch the folder fragment)

                askFolderWithFrag {
                    folderTxtView.text = PreferencesTools.getPrefStr(
                        requireContext(),
                        PreferencesTools.PREF_FOLDER_PICT
                    )
                }

            }

        }

        backgroundView.setOnClickListener {
            USRTools.closeFragmentWithTag(TAG_FRAG, requireActivity().supportFragmentManager)
        }
    }

    private fun checkPasswordAndPerformActions(
        actionIfCorrect: () -> Unit,
        actionIfIncorrect: () -> Unit
    ) {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            PasswordFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val isPasswordCorrect =
                result.getBoolean(PasswordFragment.RESPOND_VAL_KEY, false)

            if (isPasswordCorrect) {
                actionIfCorrect.invoke() // Appeler l'action associée si le mot de passe est correct
            } else {
                actionIfIncorrect.invoke() // Appeler l'action associée si le mot de passe est incorrect
            }
        }
        USRTools.launchPasswordCheckFrag(requireActivity().supportFragmentManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        returnToMainActivityListener?.enablePhotoBtn()
    }

    private fun askFolderWithFrag(actionOk: () -> Unit) {
        //Listener
        requireActivity().supportFragmentManager.setFragmentResultListener(
            ChangeFolderFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val isChangeOk =
                result.getBoolean(ChangeFolderFragment.RESPOND_VAL_KEY, false)

            if (isChangeOk) {
                actionOk.invoke()
            }
        }

        //ask new folder (launch the folder fragment)
        val parameterFrag: Fragment = ChangeFolderFragment()
        USRTools.launchFragment(
            parameterFrag,
            R.id.frame_layout_main_act2,
            ChangeFolderFragment.TAG_FRAG,
            requireActivity().supportFragmentManager
        )
    }

}