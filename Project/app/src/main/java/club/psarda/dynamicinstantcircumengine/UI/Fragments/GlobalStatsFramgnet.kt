package club.psarda.dynamicinstantcircumengine.UI.Fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.TextWatcher
import android.view.*
import android.widget.*
import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.TargetAdapterData

import club.psarda.dynamicinstantcircumengine.R
import club.psarda.dynamicinstantcircumengine.database.FireBase.DataStorage
import java.beans.PropertyChangeListener
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableStringBuilder
import club.psarda.dynamicinstantcircumengine.Filters.NumberFitler
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass
import android.widget.RelativeLayout
import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.ListInputAdapterNew


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GlobalStatsFramgnet.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GlobalStatsFramgnet.newInstance] factory method to
 * create an instance of this fragment.
 */
 class GlobalStatsFramgnet: Fragment() {

	companion object {

		fun newInstance(): GlobalStatsFramgnet {
			return GlobalStatsFramgnet()
		}

		private val DEFUALT_SIDES = "6"
	}

	private class ViewHolder(view : View){
		val GlobalRecyclerListView = view.findViewById<RecyclerView>(R.id.global_stats_list)
		val SidesInput = view.findViewById<EditText>(R.id.input)
		val SidesInputTitle = view.findViewById<TextView>(R.id.input_title)
		val SidesInputSubTitle = view.findViewById<TextView>(R.id.input_subtitle)
		val PlusButton = view.findViewById<Button>(R.id.input_plus)
		val MinusButton = view.findViewById<Button>(R.id.input_minus)
		val StatsGlobalText = view.findViewById<TextView>(R.id.global_stats_loading_text)
		val ProgressBar = view.findViewById<ProgressBar>(R.id.global_stats_stats_progress)
	}

	private var _viewHolder: ViewHolder? = null
	private var _adapater: ListInputAdapterNew? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater!!.inflate(R.layout.fragment_global_stats, container, false)
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		_viewHolder = ViewHolder(getView() as View)

		InitliseSidesInput()
		InitliseGloabalStatsList()
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
	}

	override fun onDetach() {
		super.onDetach()
	}

	override fun onPause() {
		super.onPause()
	}

	private fun InitliseSidesInput(){
		val dataStoreage = DataStorage.INSTANCE

		_viewHolder!!.SidesInputTitle.text = getString(R.string.global_stats_sides_input_title)
		_viewHolder!!.SidesInputSubTitle.text = getString(R.string.global_stats_sides_input_sub_title)

		val sides : String

		if(dataStoreage.SettingsLoaded){
			sides = dataStoreage.Settings?.DefualtSides.toString()
		}else{
			sides = DEFUALT_SIDES
		}

		_viewHolder!!.SidesInput.text = SpannableStringBuilder(sides)
		_viewHolder!!.SidesInput.filters = arrayOf<InputFilter>(NumberFitler(context))

		_viewHolder!!.SidesInput.addTextChangedListener(object : TextWatcher {
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
			}

			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun afterTextChanged(s: Editable) {

				if(s.toString() != ""){
					_viewHolder!!.ProgressBar.visibility = View.VISIBLE
					_viewHolder!!.StatsGlobalText.visibility = View.VISIBLE
					_viewHolder!!.GlobalRecyclerListView.visibility = View.GONE

					loadNewStats(Integer.parseInt(_viewHolder!!.SidesInput.text.toString()))
				}
			}
		})

		_viewHolder!!.SidesInput.filters = arrayOf(NumberFitler(context))

		InitialisePlusMinusButtons()
		InitliseGloabalStatsList()
	}

	private fun InitliseGloabalStatsList(){
		val dataStorage = DataStorage.INSTANCE
		val sides = Integer.parseInt(_viewHolder!!.SidesInput.text.toString())

		_adapater = ListInputAdapterNew(activity)
		_viewHolder!!.GlobalRecyclerListView.adapter = _adapater
		_viewHolder!!.GlobalRecyclerListView.addItemDecoration(DividerItemDecoration(_viewHolder!!.GlobalRecyclerListView.context, DividerItemDecoration.VERTICAL))

		dataStorage.addPropertyChangeListener(PropertyChangeListener { propertyChangeEvent ->

			if(isVisible){

				if (propertyChangeEvent.propertyName == DataStorage.GLOABAL_STATS_LOADED) {

					val globalStats = dataStorage.LoadedGlobalStatsData

					if(globalStats != null){

						val layoutType = ListInputAdapterNew.EntireType.RESULT_ONE

						if(globalStats.TotalNumberOfStats > 0L){

							_adapater!!.clearData()

							_adapater!!.addData(
									TargetAdapterData(
											getString(R.string.global_stats_avg_title),
											getString(R.string.global_stats_avg_sub_title, sides.toString()),
											globalStats.AverageRoll.toString(),
											layoutType
									)
							)

							_adapater!!.addData(
									TargetAdapterData(
											getString(R.string.global_stats_number_of_dice_title),
											getString(R.string.global_stats_number_of_dice_sub_title),
											globalStats.TotalRolls.toString(),
											layoutType
									)
							)

							_adapater!!.addData(TargetAdapterData(
									getString(R.string.global_stats_number_of_stats_data_title),
									getString(R.string.global_stats_number_of_stats_data_sub_title),
									globalStats.TotalNumberOfStats.toString(),
									layoutType
								)
							)

							_viewHolder!!.StatsGlobalText.visibility = View.GONE
							_viewHolder!!.GlobalRecyclerListView.visibility = View.VISIBLE

						}else{
							val layoutParams = _viewHolder!!.StatsGlobalText.getLayoutParams() as RelativeLayout.LayoutParams
							layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
							_viewHolder!!.StatsGlobalText.layoutParams = layoutParams

							_viewHolder!!.StatsGlobalText.text = getString(R.string.global_stats_no_stats_exist)
						}

						_viewHolder!!.ProgressBar.visibility = View.GONE
					}
				}
			}
		})

		loadNewStats(6)
	}

	private fun loadNewStats(sides: Int){
		val dataStorage = DataStorage.INSTANCE

		dataStorage.loadGlobalStats(sides)
	}

	private fun InitialisePlusMinusButtons() {
		_viewHolder!!.PlusButton.setOnClickListener(View.OnClickListener { UtilsClass.addToInput(1, _viewHolder!!.SidesInput) })

		_viewHolder!!.MinusButton.setOnClickListener(View.OnClickListener { UtilsClass.addToInput(-1, _viewHolder!!.SidesInput) })
	}
}
