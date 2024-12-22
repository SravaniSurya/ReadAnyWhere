package com.example.pageflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapterpdfuser extends RecyclerView.Adapter<adapterpdfuser.HolderPdfUser> {

    private Context context;
    public ArrayList<book> pdfArrayList, filterList;

    public adapterpdfuser(Context context, ArrayList<book> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);

        return new HolderPdfUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        book model = pdfArrayList.get(position);
        String title = model.getTitle();
        String pdfUrl = model.getUrl();
        String category = model.getCategory();
        long timestamp = model.getTimestamp();

        String date = myapplication.formatTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.dateTv.setText(date);

        myapplication.loadCategory(
                "" + category,
                holder.categoryTv
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapplication.downloadBook(context, title, pdfUrl);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class HolderPdfUser extends RecyclerView.ViewHolder {

        TextView titleTv, categoryTv, dateTv;

        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            dateTv = itemView.findViewById(R.id.dateTv);
        }
    }
}
