package com.example.app0.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app0.R;

import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    private List<String> suggestions;
    private OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    public SuggestionsAdapter(List<String> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(suggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewGoalSuggestion);   // Get CardView by ID
            textView = itemView.findViewById(R.id.goalSuggestionTitle);   // Get TextView by ID
        }

        void bind(String suggestion) {
            textView.setText(suggestion); // example bullet prefix
            cardView.setOnClickListener(v -> listener.onSuggestionClick(suggestion));
        }
    }

}