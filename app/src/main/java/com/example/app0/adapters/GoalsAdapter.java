package com.example.app0.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app0.R;
import com.example.app0.models.GoalItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {
    private List<GoalItem> goals;
    private OnEditClickListener editClickListener;

    public interface OnEditClickListener {
        void onEditClick(GoalItem goal);
    }

    // Single constructor that takes both list and listener
    public GoalsAdapter(List<GoalItem> goals, OnEditClickListener listener) {
        this.goals = goals;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        GoalItem currentGoal = goals.get(position);

        // Bind data to views
        holder.goalTitle.setText(currentGoal.getTitle());
        holder.sunPoints.setText(String.valueOf(currentGoal.getPoints()));
        holder.goalFrequency.setText(currentGoal.getFrequency());
        holder.setCompleted(currentGoal.isCompleted());

        holder.checkboxContainer.setOnClickListener(v -> {
            boolean newState = !currentGoal.isCompleted();
            currentGoal.setCompleted(newState);
            holder.setCompleted(newState);
            notifyItemChanged(position);
        });

        holder.editButton.setOnClickListener(v -> {
            // Use the interface callback instead of showing dialog directly
            if (editClickListener != null) {
                editClickListener.onEditClick(currentGoal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView goalTitle, sunPoints, goalFrequency;
        FrameLayout checkboxContainer;
        ImageView checkboxBackground, checkmark;
        ImageButton editButton;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTitle = itemView.findViewById(R.id.goalTitle);
            sunPoints = itemView.findViewById(R.id.sun_points);
            goalFrequency = itemView.findViewById(R.id.goalFrequency);
            checkboxContainer = itemView.findViewById(R.id.checkboxContainer);
            checkboxBackground = itemView.findViewById(R.id.checkboxBackground);
            checkmark = itemView.findViewById(R.id.checkmark);
            editButton = itemView.findViewById(R.id.editButton);
        }

        public void setCompleted(boolean isCompleted) {
            checkmark.setVisibility(isCompleted ? View.VISIBLE : View.GONE);
            checkboxBackground.setImageResource(isCompleted ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
        }
    }
}