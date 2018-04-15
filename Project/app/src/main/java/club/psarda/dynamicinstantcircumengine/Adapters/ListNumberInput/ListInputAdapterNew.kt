package club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import club.psarda.dynamicinstantcircumengine.Filters.NumberFitler
import club.psarda.dynamicinstantcircumengine.R
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass
import org.apache.commons.lang3.StringUtils
import java.util.ArrayList

/**
 * Created by pfsar on 4/11/2017.
 */

class ListInputAdapterNew(contex: Context) : RecyclerView.Adapter<ListInputAdapterNew.ViewHolderBase>() {

    inner open class ViewHolderBase(v: View): RecyclerView.ViewHolder(v){
        val title = v.findViewById<TextView>(R.id.input_title)
        val subtitle = v.findViewById<TextView>(R.id.input_subtitle)

        open fun bind(position: Int){
            title.text = _data[position]._title
            subtitle.text = _data[position]._subTitle
        }
    }

    private inner open class ResultOneViewHolder(v: View): ViewHolderBase(v){
        val result = v.findViewById<TextView>(R.id.result)

        override fun bind(position: Int){
            super.bind(position)
            result.text = _data[position]._inputtedText
        }
    }

    private abstract inner class InputViewHolder(v: View): ViewHolderBase(v){
        inner class CustomWatcher constructor(private val item: TargetAdapterData) : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                item._inputtedText = editable.toString()
            }
        }

        val input = v.findViewById<EditText>(R.id.input)
        val buttonUp = v.findViewById<Button>(R.id.input_plus)
        val buttonDown = v.findViewById<Button>(R.id.input_minus)

        override fun bind(position: Int) {
            super.bind(position)
            InitialiseInput(position)
            InitialisePlusMinusButtons()
        }

        abstract val inputFilter: InputFilter

        private fun InitialiseInput(postion: Int) {
            val oldWatcher = input.getTag() as CustomWatcher?

            if (oldWatcher != null) {
                input.removeTextChangedListener(oldWatcher)
            } else {
                input.setText(_data[postion]._inputtedText)
            }

            val newWatcher = CustomWatcher(_data[postion])
            input.setTag(newWatcher)
            input.addTextChangedListener(newWatcher)

            input.setFilters(arrayOf(inputFilter))
        }

        private fun InitialisePlusMinusButtons() {
            buttonUp.setOnClickListener { UtilsClass.addToInput(1, input) }
            buttonDown.setOnClickListener { UtilsClass.addToInput(-1, input) }
        }



    }

    private inner open class TargetInputViewHolder(v: View): InputViewHolder(v){

        val deleteButton = v.findViewById<ImageButton>(R.id.input_delete)
        val swipeLayout = v.findViewById<com.daimajia.swipe.SwipeLayout>(R.id.swipe_layout)
        val deleteSection = v.findViewById<LinearLayout>(R.id.bottom_wrapper)

        override val inputFilter: InputFilter
            get() = InputFilter { source, start, end, dest, dstart, dend ->
                    var result = ""
                    var errorMessage = ""

                    val newString = dest.toString() + source.toString()
                    val plusMatches = StringUtils.countMatches(newString, "+")
                    val miunsMatches = StringUtils.countMatches(newString, "-")

                    if (plusMatches >= 1 && miunsMatches >= 1) {
                        errorMessage += _contex.getString(R.string.error_both_inputed, "+", "-") + "\n"
                    }

                    if (plusMatches > 1) {
                        errorMessage += _contex.getString(R.string.error_single_amount, "+") + "\n"
                    }

                    if (miunsMatches > 1) {
                        errorMessage += _contex.getString(R.string.error_single_amount, "-") + "\n"
                    }

                    var i = start
                    while (i < end && errorMessage.length == 0) {
                        if (!Character.isDigit(source[i]) && source[i] != '+' && source[i] != '-') {
                            errorMessage = _contex.getString(R.string.error_invalid_target_field)
                            break
                        }

                        result += source[i]
                        i++
                    }

                    if (errorMessage.length != 0) {

                        Toast.makeText(_contex, errorMessage.substring(0, errorMessage.length - 1), Toast.LENGTH_SHORT).show()
                    }

                    result
                }

        override fun bind(position: Int) {
            super.bind(position)
            intiliseDeleteButton(position)

        }

        protected open fun getCountOfType(): Int{
            return getCountForType(EntireType.TARGET_INPUT_NO_REROLL)
        }

        private fun intiliseDeleteButton(position: Int){

            deleteButton.setOnClickListener {
                 if (getCountOfType() > 1) {
                    _data.removeAt(position)
                    notifyItemRemoved(position)
                     notifyDataSetChanged()
                } else {
                    Toast.makeText(_contex, _contex.getString(R.string.error_cannot_delete_last_field), Toast.LENGTH_SHORT).show()
                    swipeLayout.close()
                }
            }
        }
    }

    private inner open class TargetInputRerollViewHolder(v: View) : TargetInputViewHolder(v){
        override fun getCountOfType(): Int {
            return getCountForType(EntireType.TARGET_INPUT)
        }
    }

    private inner open class NumberInputViewHolder(v: View) : InputViewHolder(v){
        override val inputFilter: InputFilter
            get() = NumberFitler(_contex)
    }

    enum class EntireType(value: Int) {
        NUMBER_INPUT(0), TARGET_INPUT(1), TARGET_INPUT_NO_REROLL(2), RESULT_ONE(3), RESULT_TWO(4)
    }

    companion object {
        private val map = EntireType.values().associateBy(EntireType::ordinal);
        fun fromInt(type: Int) = map[type]
    }

    private val _inflator = LayoutInflater.from(contex)
    private val _contex = contex
    private val _data: MutableList<TargetAdapterData> = ArrayList()

    fun getCountForType(type: EntireType): Int{

        var sum = 0

        for (i in _data){
            if(i._type == type){
                sum++
            }
        }

        return sum
    }

    fun getItemWithType(targetIndex: Int, entireType: EntireType): TargetAdapterData{
        var currentIndex = 0

        for(data in _data){

            if(data._type == entireType){

                if(currentIndex == targetIndex){
                    return data
                }

                currentIndex++
            }
        }

        throw Exception()
    }

    fun clearData(){
        val oldSize = _data.size - 1
        _data.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    fun addData(newData: MutableList<TargetAdapterData>){
        _data.addAll(newData)
        notifyItemRangeInserted(0, _data.size)
    }

    fun addData(newData: TargetAdapterData){
        _data.add(newData)
        notifyItemInserted(_data.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListInputAdapterNew.ViewHolderBase {
        when (fromInt(viewType)) {
            EntireType.NUMBER_INPUT -> return NumberInputViewHolder(Infalte(R.layout.list_adaper_number_input, parent))

            EntireType.TARGET_INPUT -> return TargetInputRerollViewHolder(Infalte(R.layout.list_adapter_targets, parent))

            EntireType.RESULT_ONE -> return ResultOneViewHolder(Infalte(R.layout.list_adatper_reuslt_single_value, parent))

            EntireType.RESULT_TWO -> throw Exception()

            EntireType.TARGET_INPUT_NO_REROLL -> return TargetInputViewHolder(Infalte(R.layout.list_adaper_target_input_remove, parent))

            else -> throw Exception()
        }
    }

    override fun onBindViewHolder(holder: ViewHolderBase, position: Int) {
        holder.bind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return _data[position]._type.ordinal
    }

    override fun getItemCount(): Int {
        return _data.size
    }

    private fun Infalte(layoutId: Int, parent: ViewGroup): View{
        return _inflator.inflate(layoutId, parent, false)
    }
}