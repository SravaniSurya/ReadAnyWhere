package com.example.pageflow;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class bookadaptermodel extends RecyclerView.Adapter<bookadaptermodel.ViewHolder> {



        private Context context;
        ArrayList<book> categoryArrayList;

        public bookadaptermodel(Context context, ArrayList<book> categoryArrayList) {
            this.context = context;
            this.categoryArrayList = categoryArrayList;
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

            holder.titleTextView.setText(model.getTitle());
            holder.categoryTextView.setText(model.getCategory());

            holder.deleteBtn.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            deleteCategory(model.getUuid());
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return categoryArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, categoryTextView;
            Button deleteBtn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                categoryTextView = itemView.findViewById(R.id.categoryTv);
                deleteBtn = itemView.findViewById(R.id.deleteBtn);
            }
        }

        public void deleteCategory(String categoryId) {
            if (categoryId == null || categoryId.isEmpty()) {
                Toast.makeText(context, "Error: Invalid Category ID", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
            ref.child(categoryId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
