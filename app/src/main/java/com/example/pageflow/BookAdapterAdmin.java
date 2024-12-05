package com.example.pageflow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapterAdmin extends RecyclerView.Adapter<BookAdapterAdmin.HolderPdfAdmin> {
    private Context context;
    public ArrayList<modelpdf> pdfArrayList;

    public BookAdapterAdmin(Context context, ArrayList<modelpdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_book_admin, parent, false);
        return new HolderPdfAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        modelpdf model = pdfArrayList.get(position);

        // Get data
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        // Format timestamp to date
        String formattedDate = myapplication.formatTimestamp(timestamp);

        // Set data to views
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        // Load category
        myapplication.loadCategory("" + categoryId, holder.categoryTv);

        // Handle more options click
        holder.moreBtn.setOnClickListener(v -> moreOptionsDialog(model));

        // Handle item click for PDF details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, pdfdetailedactivity.class);
            intent.putExtra("bookId", pdfId);
            context.startActivity(intent);
        });
    }

    private void moreOptionsDialog(modelpdf model) {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        // Options to show in dialog
        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit option
                        Intent intent = new Intent(context, pdfeditactivity.class);
                        intent.putExtra("bookId", bookId);
                        context.startActivity(intent);
                    } else if (which == 1) {
                        myapplication.deleteBook(context, bookId, bookUrl, bookTitle);
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    // ViewHolder class for row_pdf_admin.xml
    class HolderPdfAdmin extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, dateTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}
