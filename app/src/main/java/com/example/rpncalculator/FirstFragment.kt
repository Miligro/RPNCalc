package com.example.rpncalculator

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.rpncalculator.databinding.FragmentFirstBinding
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class FirstFragment : Fragment(){

    private var _binding: FragmentFirstBinding? = null
    private lateinit var editTextView: TextView
    private lateinit var editRow: TableRow
    private lateinit var lastStackRow: TableRow
    private lateinit var stackSizeView: TextView
    private val mutableList = mutableListOf<Double>()
    private var stackTextViews = mutableListOf<TextView>()
    private var stackDisplaySize = 4
    private val binding get() = _binding!!
    private val screenWidth = getScreenWidth()
    private var lastFirstItem: Double? = null
    private var lastSecondItem: Double? = null
    private var firstX = 0.0F
    private var firstY = 0.0F
    private var precision = 2
    private var fontSize: Float = 40.0F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stackSizeView = binding.stackSizeView
        editTextView = binding.editValue
        setDisplay(stackDisplaySize)

        binding.zeroBtn.setOnClickListener {getValue(binding.zeroBtn)}
        binding.oneBtn.setOnClickListener {getValue(binding.oneBtn)}
        binding.twoBtn.setOnClickListener {getValue(binding.twoBtn)}
        binding.threeBtn.setOnClickListener {getValue(binding.threeBtn)}
        binding.fourBtn.setOnClickListener {getValue(binding.fourBtn)}
        binding.fiveBtn.setOnClickListener {getValue(binding.fiveBtn)}
        binding.sixBtn.setOnClickListener {getValue(binding.sixBtn)}
        binding.sevenBtn.setOnClickListener {getValue(binding.sevenBtn)}
        binding.eightBtn.setOnClickListener {getValue(binding.eightBtn)}
        binding.nineBtn.setOnClickListener {getValue(binding.nineBtn)}
        binding.dotBtn.setOnClickListener {getValue(binding.dotBtn)}

        binding.plusMinusBtn.setOnClickListener {onSignClick(binding.plusMinusBtn)}
        binding.enterBtn.setOnClickListener {onEnterClick(binding.enterBtn)}
        binding.plusBtn.setOnClickListener {onPlusClick(binding.plusBtn)}
        binding.minusBtn.setOnClickListener {onMinusClick(binding.minusBtn)}
        binding.divisionBtn.setOnClickListener {onDivisionClick(binding.divisionBtn)}
        binding.multiBtn.setOnClickListener {onMultiplicationClick(binding.multiBtn)}
        binding.delBtn.setOnClickListener {onBackSpaceClick(binding.delBtn)}
        binding.rootBtn.setOnClickListener {onRootClick(binding.rootBtn)}
        binding.powerBtn.setOnClickListener {onPowerClick(binding.powerBtn)}
        binding.swapBtn.setOnClickListener {onSwapClick(binding.swapBtn)}
        binding.clearBtn.setOnClickListener {onACClick(binding.clearBtn)}
        binding.dropBtn.setOnClickListener {onDropClick(binding.dropBtn)}
        binding.setBtn.setOnClickListener {onSetClick(binding.setBtn)}

        binding.myLayout.setOnTouchListener() {_:View, m: MotionEvent -> handleTouch(m); true}
        loadSettings()
    }

    private fun handleTouch(m: MotionEvent) {
        val action: Int = m.action
        if (action == MotionEvent.ACTION_DOWN) {
            firstX = m.rawX
            firstY = m.rawY
        } else if(action == MotionEvent.ACTION_UP) {
            val finalX = m.rawX
            val finalY = m.rawY

            val distX = (finalX - firstX).toInt()
            val distY = (finalY - firstY).toInt()

            if (abs(distX) > abs(distY) && abs(distX) > screenWidth*1/2) {
                if (firstX < finalX)
                undo()
            }
        }
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun undo(){
        if (lastSecondItem != null) {
            mutableList[0] = lastSecondItem!!
            mutableList.add(0, lastFirstItem!!)

        } else if(lastFirstItem != null){
            mutableList[0] = lastFirstItem!!
        }
        lastFirstItem = null
        lastSecondItem = null
        updateStackView()
    }

    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val color: String? = sp.getString("color", "")
        stackDisplaySize = sp.getString("stackSize", "4")?.toInt() ?: 4
        precision = sp.getString("precision", "3")?.toInt() ?: 3
        fontSize = sp.getInt("fontSize", 40).toFloat()
        val tableLayout2: TableLayout = binding.tableLayout2
        tableLayout2.setBackgroundColor(Color.parseColor(color))
        setDisplay(stackDisplaySize)
        updateStackView()
    }

    private fun setDisplay(n: Int) {
        val tableLayout2: TableLayout = binding.tableLayout2
        tableLayout2.removeAllViews()

        var newRow: TableRow = TableRow(activity)
        val newSpace1: Space = Space(activity)
        newRow.addView(newSpace1)
        val newSpace2: Space = Space(activity)
        newSpace2.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(newSpace2)
        var newTextView1: TextView = TextView(activity)
        newTextView1.text = "STACK:"
        newTextView1.textSize = (0.8 * fontSize).toFloat()
        newRow.addView(newTextView1)
        var newTextView2: TextView = TextView(activity)
        newTextView2.text = "0"
        newTextView2.textSize = (0.8 * fontSize).toFloat()
        stackSizeView = newTextView2
        newRow.addView(newTextView2)
        val newSpace3: Space = Space(activity)
        newRow.addView(newSpace3)
        tableLayout2.addView(newRow)

        for (index in n downTo 1) {
            newRow = TableRow(activity)
            newTextView1 = TextView(activity)
            newTextView2 = TextView(activity)
            newTextView1.text = "$index:"
            newTextView2.text = ""
            stackTextViews.add(0,newTextView2)
            newTextView1.textSize = fontSize
            newTextView2.textSize = fontSize
            newRow.addView(newTextView1)
            newRow.addView(newTextView2)
            tableLayout2.addView(newRow)
            if (index == n) lastStackRow = newRow
        }

        newRow = TableRow(activity)
        newTextView1 = TextView(activity)
        newTextView2 = TextView(activity)
        newTextView1.text = "=>"
        newTextView2.text = ""
        newTextView1.textSize = fontSize
        newTextView2.textSize = fontSize
        newRow.addView(newTextView1)
        newRow.addView(newTextView2)
        tableLayout2.addView(newRow)
        editTextView = newTextView2
        editRow = newRow
        editRow.visibility = View.GONE
    }

    fun getValue(v: View){
        val btn: Button = v as Button
        lastStackRow.visibility = View.GONE
        editRow.visibility = View.VISIBLE
        editTextView.append(btn.text.toString())
    }

    private fun onEnterClick(v: View){

        if (editTextView.text.toString() == ""){
            if (mutableList.size > 0){
                mutableList.add(0, mutableList[0])
            }else{
                Toast.makeText(activity, "Brak liczb na stosie", Toast.LENGTH_LONG).show()
            }
        } else {
            try{
                val value: Double = editTextView.text.toString().toDouble()
                mutableList.add(0, value)
                editTextView.text = ""
            } catch (e: Exception) {
                editTextView.text = ""
                Toast.makeText(activity, "Niepoprawna liczba", Toast.LENGTH_LONG).show()
            }
        }
        lastStackRow.visibility = View.VISIBLE
        editRow.visibility = View.GONE
        updateStackView()
    }

    private fun updateStackView(){
        val tmp = 10.toDouble().pow(precision)
        val stackSize = mutableList.size
        stackSizeView.text = stackSize.toString()
        var upTo = stackDisplaySize - 1
        if (stackSize < stackDisplaySize){
            upTo = stackSize - 1
            for (index in upTo+1..stackDisplaySize-1){
                stackTextViews[index].text = ""
            }
        }
        for (index in 0..upTo) {
            stackTextViews[index].text = (round(mutableList[index]*tmp) / tmp).toString()
        }
    }

    private fun onPlusClick(v: View) {
        if (mutableList.size >= 2){
            lastFirstItem = mutableList[0]
            lastSecondItem = mutableList[1]
            val value = mutableList[0] + mutableList[1]
            mutableList.removeAt(0)
            mutableList[0] = value
            updateStackView()
        }
    }

    private fun onMinusClick(v: View){
        if (mutableList.size >= 2){
            lastFirstItem = mutableList[0]
            lastSecondItem = mutableList[1]
            val value = mutableList[0] - mutableList[1]
            mutableList.removeAt(0)
            mutableList[0] = value
            updateStackView()
        }
    }

    private fun onDivisionClick(v: View){
        if (mutableList.size >= 2){
            lastFirstItem = mutableList[0]
            lastSecondItem = mutableList[1]
            val value = mutableList[0] / mutableList[1]
            mutableList.removeAt(0)
            mutableList[0] = value
            updateStackView()
        }
    }

    private fun onMultiplicationClick(v: View){
        if (mutableList.size >= 2){
            lastFirstItem = mutableList[0]
            lastSecondItem = mutableList[1]
            val value = mutableList[0] * mutableList[1]
            mutableList.removeAt(0)
            mutableList[0] = value
            updateStackView()
        }
    }

    private fun onRootClick(v: View){
        if (mutableList.size >= 1){
            lastFirstItem = mutableList[0]
            val value = sqrt(mutableList[0])
            mutableList[0] = value
            updateStackView()
        }
    }

    private fun onPowerClick(v: View){
        if (mutableList.size >= 2){
            lastFirstItem = mutableList[0]
            lastSecondItem = mutableList[1]
            val value = mutableList[0].pow(mutableList[1])
            mutableList.removeAt(0)
            mutableList[0] = value
            updateStackView()
        }
    }

    private fun onDropClick(v: View){
        if (mutableList.size >= 1) {
            mutableList.removeAt(0)
            updateStackView()
        }
    }

    private fun onBackSpaceClick(v: View){
        editTextView.text = editTextView.text.dropLast(1)
    }

    private fun onSwapClick(v: View){
        if(mutableList.size >= 2){
            val tmp = mutableList[0]
            mutableList[0] = mutableList[1]
            mutableList[1] = tmp
            updateStackView()
        }
    }

    private fun onACClick(v: View){
        mutableList.removeAll(mutableList)
        updateStackView()
    }

    private fun onSignClick(v: View){
        if (mutableList.size >= 1){
            mutableList[0] = -mutableList[0]
            updateStackView()
        }
    }

    private fun onSetClick(v: View){
        findNavController().navigate(R.id.action_FirstFragment_to_settingsFragment)
    }

}
