package com.example.alexey_kovalev_auii_drill_i

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TicketsPick : AppCompatActivity() {

    private var selectedDate: String = ""
    private var numChildTickets: Int = 0
    private var numAdultTickets: Int = 0
    private lateinit var totalSumTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.activity_tickets_pick)

        val backButton = findViewById<Button>(R.id.back_button)
        val purchaseButton = findViewById<Button>(R.id.purchase_button)
        totalSumTextView = findViewById(R.id.total_sum_text_view)

        val dateSpinner = findViewById<Spinner>(R.id.date_spinner)
        val adultTicketsSlider = findViewById<Slider>(R.id.adult_tickets_slider)
        val childTicketsSlider = findViewById<Slider>(R.id.child_tickets_slider)

        childTicketsSlider.addOnChangeListener { _, value, _ ->
            numChildTickets = value.toInt()
            updateTotalSum()
            findViewById<TextView>(R.id.child_amount).text = value.toInt().toString()
        }

        adultTicketsSlider.addOnChangeListener { _, value, _ ->
            numAdultTickets = value.toInt()
            updateTotalSum()
            findViewById<TextView>(R.id.adult_amount).text = value.toInt().toString()
        }


        // Populate date spinner with custom adapter
        val dates = generateDates()
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(ContextCompat.getColor(this@TicketsPick, android.R.color.white))
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateSpinner.adapter = adapter

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDate = dates[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        // Update total sum when sliders change
        childTicketsSlider.addOnChangeListener { _, value, _ ->
            numChildTickets = value.toInt()
            updateTotalSum()
        }

        adultTicketsSlider.addOnChangeListener { _, value, _ ->
            numAdultTickets = value.toInt()
            updateTotalSum()
        }

        backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            startActivity(Intent(this, MainActivity::class.java))
        }

        purchaseButton.setOnClickListener {
            if (calculateTotalSum() == 0) {
                // User forgot to pick tickets
                Toast.makeText(this, getString(R.string.forgot_pick_tickets), Toast.LENGTH_SHORT).show()
            } else {
                // Show confirmation dialog
                showConfirmationDialog()
            }
        }
    }

    private fun generateDates(): List<String> {
        val dateFormat = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())

        val calendar = Calendar.getInstance()

        // Default time is 18:30 for every date
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)

        val dates = mutableListOf<String>()

        for (i in 1..10) {
            // Add 1 day to the current date
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val date = calendar.time
            dates.add(dateFormat.format(date))
        }

        return dates
    }


    private fun hideSystemUI() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }

    private fun showConfirmationDialog() {
        val totalSum = calculateTotalSum()
        var confirmationMessage = getString(R.string.confirmation_message) + "\n\n"
        confirmationMessage += getString(R.string.date_label) + " $selectedDate\n"

        if (numChildTickets > 0) {
            confirmationMessage += getString(R.string.child_tickets_label) + " $numChildTickets\n"
        }

        if (numAdultTickets > 0) {
            confirmationMessage += getString(R.string.adult_tickets_label) + " $numAdultTickets\n"
        }

        confirmationMessage += getString(R.string.total_sum_label) + " $totalSum$"

        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        builder.setTitle(getString(R.string.confirmation_title))

        val messageTextView = TextView(this)
        messageTextView.text = confirmationMessage
        messageTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        messageTextView.setPadding(40, 40, 40, 40)
        builder.setView(messageTextView)

        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            Toast.makeText(this, getString(R.string.ticket_order_sent), Toast.LENGTH_SHORT).show()
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            startActivity(Intent(this, MainActivity::class.java))
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.semiTransparentColor)))
        dialog.setOnShowListener { dialogInterface ->
            val positiveButton = (dialogInterface as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
            val negativeButton = dialogInterface.getButton(DialogInterface.BUTTON_NEGATIVE)
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.goldenYellow))
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.goldenYellow))
        }

        dialog.show()
    }

    private fun calculateTotalSum(): Int {
        // Calculate total sum based on ticket prices
        return (numChildTickets * 15) + (numAdultTickets * 20)
    }

    private fun updateTotalSum() {
        val totalSum = calculateTotalSum()
        totalSumTextView.text = getString(R.string.total_sum_template, totalSum)
    }
}
