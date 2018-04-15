package club.psarda.dynamicinstantcircumengine.UI.Fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.TargetAdapterData

import club.psarda.dynamicinstantcircumengine.R
import club.psarda.dynamicinstantcircumengine.UI.MainActivity
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass
import club.psarda.dynamicinstantcircumengine.database.DataHolders.SettingsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.TargetData
import club.psarda.dynamicinstantcircumengine.database.FireBase.DataStorage
import club.psarda.dynamicinstantcircumengine.database.FireBase.FireBaseHelper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import android.widget.SeekBar
import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.ListInputAdapterNew


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    private class ViewHolder(view : View){
        val addTargetButton:android.support.design.widget.FloatingActionButton = view.findViewById(R.id.save_fab)
        val settingsLayout: RelativeLayout = view.findViewById(R.id.settings_layout)
        val loginText: TextView = view.findViewById(R.id.settings_loading_text)
        val shakeSeekBar: SeekBar = view.findViewById(R.id.settings_shake_seek_bar)
        val settingsInput: RecyclerView = view.findViewById(R.id.settings_input)
    }

    private var _viewHolder: ViewHolder? = null
    private var _settingsInput: ListInputAdapterNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.settings_fragmnet_menu, menu)

        updateGoogleMenuIcon(menu!!.findItem(R.id.google_sign_in), activity as MainActivity)

        FirebaseAuth.getInstance().addAuthStateListener {

            if(activity is MainActivity){
                val mainAct = activity as MainActivity
                val googleItem = menu.findItem(R.id.google_sign_in );

                if(googleItem != null){
                    if(mainAct.googleSignedIn){
                        googleItem.setIcon(context.getDrawable(R.drawable.log_out))
                    }else{
                        googleItem.setIcon(context.getDrawable(R.drawable.common_google_signin_btn_icon_dark_normal))
                    }
                }
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val main = activity as MainActivity

        when (item!!.itemId) {
            R.id.google_sign_in -> {

                if(main.googleSignedIn){
                    updateDatabase()
                    main.googleSignOut()
                }else{
                    signIn()
                }

            }

            R.id.add_target -> {
                _settingsInput!!.addData(addTarget(SettingsData.DEFUALT_TARGET))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

        signIn()

        return super.onOptionsItemSelected(item)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        (activity as MainActivity).onConnectionFailed(connectionResult)
    }

    private fun signIn() {
        (activity as MainActivity).soogleSignIn()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        _viewHolder = ViewHolder(getView() as View)
        initialiseUserListener()
        InitiliseSaveButton()
        refreshUI()
    }

    private fun InitiliseSaveButton(){
        _viewHolder!!.addTargetButton.setOnClickListener {
            (activity as MainActivity).switchToRoll(null)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onPause() {
        super.onPause()
        if((activity as MainActivity).googleSignedIn){
            updateDatabase()
        }
    }

    private var lastUserId: String = ""

    private fun initialiseUserListener(){

        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.addAuthStateListener {

            if(firebaseAuth.currentUser != null && firebaseAuth.currentUser!!.uid != lastUserId){
                lastUserId = firebaseAuth.currentUser!!.uid

                class Removeable : PropertyChangeListener {

                    override fun propertyChange(propertyChangeEvent: PropertyChangeEvent){

                        if (propertyChangeEvent.propertyName == DataStorage.SETTINGS_LOADED) {

                            if(isVisible){
                                refreshUI()
                            }

                            DataStorage.INSTANCE.removePropertyChangeListener(this)
                        }
                    }

                }

                DataStorage.INSTANCE.addPropertyChangeListener(Removeable())

            }

        }

    }

    private fun updateGoogleMenuIcon(googleItem : MenuItem, mainAct: MainActivity){

        if(mainAct.googleSignedIn){
            googleItem.icon = context.getDrawable(R.drawable.log_out)
        }else{
            googleItem.icon = context.getDrawable(R.drawable.common_google_signin_btn_icon_dark_normal)
        }
    }

    private fun updateDatabase(){

        if(FirebaseAuth.getInstance().currentUser != null && _viewHolder != null) {
            var settings = SettingsData()

            val newDefualtSides = _settingsInput!!.getItemWithType(0, ListInputAdapterNew.EntireType.NUMBER_INPUT)
            val newDefualtNumberOfDice = _settingsInput!!.getItemWithType(1, ListInputAdapterNew.EntireType.NUMBER_INPUT)

            settings.DefualtSides = newDefualtSides._inputtedText.toLong()
            settings.DefualtNumberOfDice = newDefualtNumberOfDice._inputtedText.toLong()
            // Don't forget that the min is added to the seek bar because the lowest value has to be 0
            settings.ShakeThreseHold = _viewHolder!!.shakeSeekBar.progress + SettingsData.SHAKE_THRESHOLD_MIN

            settings.Targets.clear()

            for (i in 0 until _settingsInput!!.getCountForType(ListInputAdapterNew.EntireType.TARGET_INPUT_NO_REROLL)) {
                val currentTargetData = _settingsInput!!.getItemWithType(i, ListInputAdapterNew.EntireType.TARGET_INPUT_NO_REROLL)

              //  settings.Targets.add(TargetData(i))
                settings.Targets.add(
                        TargetData(currentTargetData._inputtedText)
                )
            }

            FireBaseHelper.instance.writeSettings(settings, FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

    private fun refreshUI(){

        val settings = DataStorage.INSTANCE.Settings

        if((activity as MainActivity).googleSignedIn){
            _viewHolder!!.settingsLayout.visibility = View.VISIBLE
            _viewHolder!!.loginText.visibility = View.GONE
        }else{
            _viewHolder!!.settingsLayout.visibility = View.GONE
            _viewHolder!!.loginText.visibility = View.VISIBLE
        }

        if(settings != null) {
            initialiseInputUI(settings)
        }
    }

    private fun initialiseInputUI(settings: SettingsData){
        initialiseShakeSeekBar(settings)
        initialiseInputList(settings)
    }

    private fun initialiseShakeSeekBar(settings: SettingsData){
        // Lowest value must be 0 so it only needs the values between the min and the max
        _viewHolder!!.shakeSeekBar.max = SettingsData.SHAKE_THRESHOLD_MAX - SettingsData.SHAKE_THRESHOLD_MIN

        // Don't forget it needs to start at 0 refer to above if confused
        _viewHolder!!.shakeSeekBar.progress = settings.ShakeThreseHold - SettingsData.SHAKE_THRESHOLD_MIN
    }

    private fun initialiseInputList(settings: SettingsData){
        _settingsInput = ListInputAdapterNew(activity)
        _viewHolder!!.settingsInput.adapter = _settingsInput
        _viewHolder!!.settingsInput.addItemDecoration(DividerItemDecoration(_viewHolder!!.settingsInput.context, DividerItemDecoration.VERTICAL))

        _settingsInput!!.addData(listEntriyDefaultSides(settings))
        _settingsInput!!.addData(listEntriyDefaultDice(settings))
        addListEntriyTargets(settings)

        // Fixes the height of the view so it doesn't cover the FAB
        view!!.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        FixListViewHeight()
                        view!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
        )
    }

    private fun listEntriyDefaultSides(settings : SettingsData): TargetAdapterData{
        return TargetAdapterData(
                    getString(R.string.settings_defualt_sides_title),
                    getString(R.string.settings_defualt_sides_subTitle),
                    settings.DefualtSides.toString(),
                    ListInputAdapterNew.EntireType.NUMBER_INPUT
                )
    }

    private fun listEntriyDefaultDice(settings : SettingsData): TargetAdapterData{
        return TargetAdapterData(
                getString(R.string.settings_defualt_number_of_dice_title),
                getString(R.string.settings_defualt_number_of_dice_subtitle),
                settings.DefualtNumberOfDice.toString(),
                ListInputAdapterNew.EntireType.NUMBER_INPUT
            )
    }

    private fun addListEntriyTargets(settings : SettingsData){

        val targets = settings.TargetsAsStrings

        //@Bad figure out how to fix this with the shorter thing
        for (target in targets) {
            _settingsInput!!.addData(addTarget(target))
        }

    }

    private fun addTarget(target: String): TargetAdapterData{
        return TargetAdapterData(
            String.format(getString(R.string.settings_defualt_target_title), UtilsClass.ordinal(_settingsInput!!.getCountForType(ListInputAdapterNew.EntireType.TARGET_INPUT_NO_REROLL) + 1)),
            getString(R.string.settings_defualt_target_subtitle),
            target,
            ListInputAdapterNew.EntireType.TARGET_INPUT_NO_REROLL
        )
    }

    private fun FixListViewHeight() {

        if(_viewHolder != null){
            val lp = _viewHolder!!.settingsInput.layoutParams as android.view.ViewGroup.LayoutParams

            lp.height = (_viewHolder!!.addTargetButton.y - _viewHolder!!.settingsInput.y).toInt() - 5

            _viewHolder!!.settingsInput.layoutParams = lp
        }
    }
}