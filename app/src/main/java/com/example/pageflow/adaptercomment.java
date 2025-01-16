package com.example.pageflow;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adaptercomment extends RecyclerView.Adapter<adaptercomment.HolderComment> {

    private Context context;
    private ArrayList<comment> commentArrayList;

    private FirebaseAuth firebaseAuth;

    public adaptercomment(Context context, ArrayList<comment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new HolderComment(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        comment modelComment = commentArrayList.get(position);
        String comment = modelComment.getComment();
        String uid = modelComment.getUserId();
        long timestamp = modelComment.getTimestamp();

        String date = myapplication.formatTimestamp(timestamp);

        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        loadUserDetails(modelComment, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null && uid.equals(firebaseAuth.getUid())) {
                    deleteComment(modelComment, holder);
                }
            }
        });
    }

    private void deleteComment(comment modelComment, HolderComment holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(modelComment.getBookId())
                                .child("Comments")
                                .child(modelComment.getId())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to delete due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void loadUserDetails(comment modelComment, HolderComment holder) {
        String uid = modelComment.getUserId();

        Toast.makeText(context.getApplicationContext(), uid.toString(), Toast.LENGTH_SHORT).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = "" + snapshot.child("firstName").getValue();
                        String profileImage = "" + snapshot.child("photoUrl" +
                                "").getValue();
                        holder.nameTv.setText(name);
                        try {
                            Glide.with(context)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(holder.profileIv);
                        } catch (Exception e) {
                            holder.profileIv.setImageResource(R.drawable.ic_person_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder {

        ShapeableImageView profileIv;
        TextView nameTv, dateTv, commentTv;


        public HolderComment(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            commentTv = itemView.findViewById(R.id.commentTv);
        }
    }
}
