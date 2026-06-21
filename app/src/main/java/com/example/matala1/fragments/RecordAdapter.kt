package com.example.matala1.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.matala1.R
import com.example.matala1.utilities.Record
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(
    private val records: List<Record>,
    private val onRecordClickListener: (Record) -> Unit // listen to click on record
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]

        // מעבירים את הרשומה יחד עם המיקום הנוכחי פלוס 1 (כדי שיתחיל מ-1 ולא מ-0)
        holder.bind(record, position + 1)

        // clicked item
        holder.itemView.setOnClickListener {
            onRecordClickListener(record)
        }
    }

    override fun getItemCount(): Int = records.size

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // הוספת ה-TextView של המיקום הסידורי בטבלה
        private val lblPosition: View = itemView.findViewById(R.id.record_TXT_position)
        private val lblName: View = itemView.findViewById(R.id.record_LBL_name)
        private val lblDate: View = itemView.findViewById(R.id.record_LBL_date)
        private val lblScore: View = itemView.findViewById(R.id.record_LBL_score)

        fun bind(record: Record, displayPosition: Int) {
            // הצגת המספר הסידורי (למשל: "1." או "2.")
            (lblPosition as? AppCompatTextView)?.text = "$displayPosition."

            (lblName as? AppCompatTextView)?.text = record.name
            (lblScore as? AppCompatTextView)?.text = String.format("%03d", record.score)

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            (lblDate as? AppCompatTextView)?.text = sdf.format(Date(record.date))
        }
    }
}