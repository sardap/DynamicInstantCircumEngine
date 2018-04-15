package club.psarda.dynamicinstantcircumengine.Adapters.ListTargetResults

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import club.psarda.dynamicinstantcircumengine.R

/**
 * Created by pfsar on 12/10/2017.
 */
class ListTwoResultAdapter(
        context: Context,
        data : MutableList<ListTwoResultData>) :
        ArrayAdapter<ListTwoResultData>(context, R.layout.list_adapter_result_two_values, data) {

    private class ViewHolder(view: View) {
        val MainTitle = view.findViewById<TextView>(R.id.title)
        val LeftResultTitle = view.findViewById<TextView>(R.id.left_result_title)
        val RightResultTitle = view.findViewById<TextView>(R.id.right_result_title)
        val LeftResult = view.findViewById<TextView>(R.id.left_result)
        val RightResult = view.findViewById<TextView>(R.id.right_result)
    }

    private val _inflator = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ViewHolder

        if (convertView == null) {
            view = this._inflator.inflate(R.layout.list_adapter_result_two_values, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        vh.MainTitle.text = getItem(position).MainTitle
        vh.LeftResultTitle.text = getItem(position).ColTitles[0]
        vh.RightResultTitle.text = getItem(position).ColTitles[1]
        vh.LeftResult.text = getItem(position).ColResults[0]
        vh.RightResult.text = getItem(position).ColResults[1]

        return view
    }
}