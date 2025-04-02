package com.example.app0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app0.R;
import com.example.app0.adapters.GoalsAdapter;
import com.example.app0.models.GoalItem;
import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment implements GoalsAdapter.OnEditClickListener {

    private RecyclerView recyclerView;
    private GoalsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout only once
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.goalsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter with click listener and data
        adapter = new GoalsAdapter(getSampleGoals(), this);
        recyclerView.setAdapter(adapter);

        // Set up add button click listener
        ImageButton addGoalButton = view.findViewById(R.id.addGoalButton);
        addGoalButton.setOnClickListener(v -> openAddGoalFragment());

        return view;
    }

    private void openAddGoalFragment() {
        AddGoalFragment addGoalFragment = new AddGoalFragment();

        // Use FragmentManager to begin transaction
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, addGoalFragment) // Replace with your container ID
                .addToBackStack("add_goal") // Optional: Add to back stack
                .commit();
    }

    @Override
    public void onEditClick(GoalItem goal) {
        // Launch EditGoalFragment with goal data
        EditGoalFragment editFragment = new EditGoalFragment();

        Bundle args = new Bundle();
        args.putString("goal_title", goal.getTitle());
        args.putInt("goal_points", goal.getPoints());
        args.putString("goal_frequency", goal.getFrequency());
        editFragment.setArguments(args);

        // Replace current fragment with EditGoalFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack("edit_goal")
                .commit();
    }

    private List<GoalItem> getSampleGoals() {
        List<GoalItem> goals = new ArrayList<>();
        goals.add(new GoalItem("Take a stretch break", 3, "Daily"));
        goals.add(new GoalItem("Drink a glass of water", 3, "Daily"));
        goals.add(new GoalItem("Go for a short walk", 6, "Daily"));
        goals.add(new GoalItem("Mood check in", 3, "Daily"));
        goals.add(new GoalItem("Reflect on your day", 3, "Daily"));
        return goals;
    }
}