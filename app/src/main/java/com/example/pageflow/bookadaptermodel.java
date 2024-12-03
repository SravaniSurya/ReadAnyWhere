package com.example.pageflow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

public class bookadaptermodel extends RecyclerView.Adapter<bookadaptermodel.HolderCategory> implements Filterable {

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
        public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);
            return new HolderCategory(view);
        }

        @Override
        public void onBindViewHolder(@NonNull bookadaptermodel.HolderCategory holder, int position) {

            book model = categoryArrayList.get(position);
            String id = model.getId();
            String title = model.getTitle();


            holder.categoryTv.setText(title);


            holder.deleteBtn.setOnClickListener(v -> {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Confirm", (dialog, which) -> deleteCategory(model))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            });


            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, pdflistadmin.class);
                intent.putExtra("categoryId", id);
                intent.putExtra("categoryTitle", title);
                context.startActivity(intent);
            });
        }

        private void deleteCategory(book model) {

            String id = model.getId();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
            ref.child(id)
                    .removeValue()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(context, "Category deleted successfully!", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
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


        class HolderCategory extends RecyclerView.ViewHolder {

            TextView categoryTv;
            ImageButton deleteBtn;

            public HolderCategory(@NonNull View itemView) {
                super(itemView);


                deleteBtn = itemView.findViewById(R.id.deleteBtn);
            }
        }
    }


