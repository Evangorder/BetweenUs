package com.example.betweenus.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.activities.DrawingActivity;
import com.example.betweenus.R;
import com.example.betweenus.adapter.AvatarAdapter;
import com.example.betweenus.adapter.AvatarPagerAdapter;
import com.example.betweenus.adapter.TodoPreviewAdapter;
import com.example.betweenus.model.AvatarGroup;
import com.example.betweenus.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerTodoPreview;
    private ImageButton moodHappy, moodNeutral, moodSad, moodAngry, moodExcited;
    private ViewPager2 avatarPager;

    private TodoPreviewAdapter todoPreviewAdapter;
    private SharedTodoViewModel todoViewModel;
    private DatabaseHelper dbHelper;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());

        Bundle args = getArguments();
        if (args != null) {
            dbHelper.currentUserId = args.getInt("USER_ID", -1);
        }

        initializeViews(view);
        setupDashboard(view);
        setupAvatars();
        setupTodoPreview(view);
        setupMoodButtons();
        setupNavigation(view);

        CardView dashboardCard = view.findViewById(R.id.cardDashboard);
        dashboardCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DrawingActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void initializeViews(View view) {
        avatarPager = view.findViewById(R.id.avatarPager);
        recyclerTodoPreview = view.findViewById(R.id.recyclerTodoPreview);

        moodHappy = view.findViewById(R.id.moodHappy);
        moodNeutral = view.findViewById(R.id.moodNeutral);
        moodSad = view.findViewById(R.id.moodSad);
        moodAngry = view.findViewById(R.id.moodAngry);
        moodExcited = view.findViewById(R.id.moodExcited);
    }

    private void setupDashboard(View view) {
        ImageView dashboardImage = view.findViewById(R.id.dashboardImage);
        File file = new File(requireContext().getFilesDir(), "dashboard_drawing.png");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            dashboardImage.setImageBitmap(bitmap);
        }
    }

    private void setupTodoPreview(View view) {
        RecyclerView recyclerTodoPreview = view.findViewById(R.id.recyclerTodoPreview);
        recyclerTodoPreview.setLayoutManager(new LinearLayoutManager(getContext()));

        todoViewModel = new ViewModelProvider(requireActivity()).get(SharedTodoViewModel.class);

        todoPreviewAdapter = new TodoPreviewAdapter(new ArrayList<>(), task -> {
            boolean newStatus = !task.isComplete();
            dbHelper.toggleTaskStatus(task.getTaskId(), newStatus);

            // Refresh the data in the ViewModel. ProductivityFragment (if active) 
            // will react to this change if it observes the same ViewModel.
            todoViewModel.loadTasks(dbHelper, dbHelper.currentUserId);
        });

        recyclerTodoPreview.setAdapter(todoPreviewAdapter);

        todoViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            todoPreviewAdapter.updateTasks(tasks);
        });
        
        // Initial load
        todoViewModel.loadTasks(dbHelper, dbHelper.currentUserId);
    }

    private void setupAvatars() {
        List<AvatarGroup> avatarGroups = dbHelper.getAvatarGroups(dbHelper.currentUserId);
        AvatarPagerAdapter adapter = new AvatarPagerAdapter(avatarGroups);
        avatarPager.setAdapter(adapter);
    }

    private void setupMoodButtons() {
        moodHappy.setOnClickListener(v -> updateMood(0));
        moodSad.setOnClickListener(v -> updateMood(1));
        moodExcited.setOnClickListener(v -> updateMood(2));
        moodNeutral.setOnClickListener(v -> updateMood(3));
        moodAngry.setOnClickListener(v -> updateMood(4));
    }

    private void updateMood(int mood) {
        dbHelper.updateUserMood(dbHelper.currentUserId, mood);
        setupAvatars();
    }

    private void setupNavigation(View view) {
        view.findViewById(R.id.cardTodo).setOnClickListener(v -> {
            Fragment productivityFragment = new ProductivityFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("USER_ID", dbHelper.currentUserId);
            productivityFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, productivityFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupDashboard(getView());
    }
}
