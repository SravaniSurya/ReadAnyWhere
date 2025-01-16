package com.example.pageflow;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class bookadaptermodel extends RecyclerView.Adapter<bookadaptermodel.ViewHolder> implements Filterable {

    private Context context;
    public ArrayList<book> categoryArrayList, filterList;

    private searchoption filter;

    public bookadaptermodel(Context context, ArrayList<book> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        book model = categoryArrayList.get(position);
        String title = model.getCategory();
        holder.categoryTv.setText(title != null ? title : "No Title Available");

        holder.deleteBtn.setOnClickListener(v -> {
            if (model != null) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this book?")
                        .setPositiveButton("Confirm", (dialog, which) -> deleteBook(model))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (model != null) {
                Intent intent = new Intent(context, pdflistadmin.class);
                intent.putExtra("categoryId", String.valueOf(model.getTimestamp()));
                intent.putExtra("categoryTitle", title);
                context.startActivity(intent);
            }
        });
    }


    private void deleteBook(book model) {
        String id = model.getUuid();
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "Invalid book ID", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    Log.d("DeleteBook", "Successfully deleted: " + id);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DeleteBook", "Error deleting book: " + id, e);
                });
    }



    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new searchoption(filterList, this);
        }
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTv;
        ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
