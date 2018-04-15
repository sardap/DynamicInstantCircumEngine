package club.psarda.dynamicinstantcircumengine.UI.Fragments

import android.app.Fragment
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*

import club.psarda.dynamicinstantcircumengine.R
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.StatsFocusFragment
import club.psarda.dynamicinstantcircumengine.UI.MainActivity
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StorageClasses.StatsDataStorage
import club.psarda.dynamicinstantcircumengine.database.FireBase.FireBaseHelper
import com.google.firebase.auth.FirebaseAuth
import java.util.*




/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StatsSelectionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StatsSelectionFragment.newInstance] factory method to
 * create an INSTANCE of this fragment.
 */
class StatsSelectionFragment : Fragment() {

    companion object {

        fun newInstance(): StatsSelectionFragment {
            return StatsSelectionFragment()
        }
    }

    private inner class ViewHolder(view: View) {
        val selectionRecyle = view.findViewById<RecyclerView>(R.id.stats_selection_recycler_view)
    }

    private inner class SelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val statsLaydown = itemView.findViewById<TextView>(R.id.stats_laydown)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.input_delete)
        val selectionLayout = itemView.findViewById<RelativeLayout>(R.id.selection_layout)
    }

    private inner class SelectionAdapter(contex: Context, options: FirebaseRecyclerOptions<StatsDataStorage>) : FirebaseRecyclerAdapter<StatsDataStorage, SelectionViewHolder>(options){

        private val _context = contex
        private val _inflator = LayoutInflater.from(_context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
            val view = _inflator.inflate(R.layout.list_adapter_selection_remove, parent, false)
            return SelectionViewHolder(view)
        }

        override fun onBindViewHolder(holder: SelectionViewHolder, position: Int, statsData: StatsDataStorage) {

            holder.title.text = statsData.Name

            val c = Calendar.getInstance()
            c.timeInMillis = statsData.CreationDate
            val d = c.time as Date
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val formattedTimeCreated = format.format(d)

            holder.statsLaydown.text = getString(
                    R.string.stats_selection_laydown_text,
                    statsData.Sides,
                    statsData.NumberOfRolls,
                    formattedTimeCreated
            )

            holder.selectionLayout.setOnClickListener{
                val bundle = Bundle()
                bundle.putParcelable("stats", StatsData(statsData))
                bundle.putString(StatsFocusFragment.States::class.java.canonicalName, StatsFocusFragment.States.VIEW_MODE.name)
                (activity as MainActivity ).switchToStatsFocus(bundle)
            }

            holder.deleteButton.setOnClickListener{
                getRef(position).removeValue()
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }

        }
    }

    private var _viewHolder: ViewHolder? = null
    private var _adapter: SelectionAdapter? = null
    private var _query = FireBaseHelper.instance.allStatsDataForUser(FirebaseAuth.getInstance().currentUser!!.uid)

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.stats_selection_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val query = FireBaseHelper.instance.allStatsDataForUser(FirebaseAuth.getInstance().currentUser!!.uid)

        when (item!!.itemId) {

            R.id.sort_name -> {
                _query = query.orderByChild(StatsDataStorage.NAME_KEY)
            }

            R.id.sort_avg -> {
                _query = query.orderByChild(StatsDataStorage.AVERAGE_KEY)
            }

            R.id.sort_date -> {
                _query = query.orderByChild(StatsDataStorage.CREATEION_DATE_KEY)
            }

            R.id.sort_sides -> {
                _query = query.orderByChild(StatsDataStorage.SIDES_KEY)
            }

            R.id.sort_amount -> {
                _query = query.orderByChild(StatsDataStorage.AMOUNT_KEY)
            }
        }

        if(item.itemId != R.id.sort){
            initialiseListView()
            _adapter!!.startListening()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _viewHolder = ViewHolder(view)
        //InitialiseProgressBar()f
        initialiseListView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStart() {
        super.onStart()
        _adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        _adapter!!.stopListening()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    private fun initialiseListView() {

        val options = FirebaseRecyclerOptions.Builder<StatsDataStorage>()
                .setQuery(_query, StatsDataStorage::class.java)
                .build()

        _adapter = SelectionAdapter(activity, options)
        _viewHolder!!.selectionRecyle.adapter = _adapter
        _viewHolder!!.selectionRecyle.layoutManager = LinearLayoutManager(activity)
    }

}// Required empty public constructor
