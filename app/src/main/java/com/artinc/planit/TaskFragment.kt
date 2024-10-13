package com.artinc.planit

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TASK = "task"

        fun newInstance(task: Task): TaskFragment {
            val fragment = TaskFragment()
            val args = Bundle()
            args.putParcelable(ARG_TASK, task)
            fragment.arguments = args
            return fragment
        }
    }

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            task = it.getParcelable(ARG_TASK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)
        // Используйте task для отображения данных во фрагменте
        view.findViewById<TextView>(R.id.title).text = task!!.title
        view.findViewById<TextView>(R.id.desc).text = task!!.description
        view.findViewById<View>(R.id.colorTask).backgroundTintList = ColorStateList.valueOf(getTaskColor(task!!.color))
        setPrior(task!!.priority, view)

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        // Устанавливаем состояние в Expanded
        BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
    }

    // Дополнительная логика фрагмента
    private fun getTaskColor(color: String): Int {
        return when (color) {
            "blue" -> Color.rgb(24, 136, 255)
            "green" -> Color.rgb(34, 133, 7)
            "yellow" -> Color.rgb(237, 178, 0)
            "red" -> Color.rgb(225, 7, 7)
            else -> Color.rgb(24, 136, 255)
        }
    }

    private fun setPrior(prior: Int, view: View) {
        view.findViewById<ImageView>(R.id.first_prior).setBackgroundResource(if (prior >= 1) R.drawable.star else R.drawable.star_outline)
        view.findViewById<ImageView>(R.id.second_prior).setBackgroundResource(if (prior >= 2) R.drawable.star else R.drawable.star_outline)
        view.findViewById<ImageView>(R.id.third_prior).setBackgroundResource(if (prior >= 3) R.drawable.star else R.drawable.star_outline)
    }
}
