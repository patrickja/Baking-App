package com.example.android.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.myapplication.Activities.RecipeActivity;
import com.example.android.myapplication.Adapters.RecipeAdapter;
import com.example.android.myapplication.IdlingResources.SimpleIdlingResource;
import com.example.android.myapplication.Models.Recipe;
import com.example.android.myapplication.Network.APIClient;
import com.example.android.myapplication.Network.APIInterface;
import com.example.android.myapplication.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFragment extends Fragment {

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recyclerView;

    private Bundle savedInstanceState;
    private static final String SAVED_LAYOUT_MANAGER_KEY = "saved_layout_manager";
    private SimpleIdlingResource idlingResource;

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        ButterKnife.bind(this, rootView);
        this.savedInstanceState = savedInstanceState;
        applyConfiguration(rootView);
        return rootView;
    }

    private void applyConfiguration(View rootView) {
        applyIdlingConfiguration();
        RecipeAdapter recipesAdapter = new RecipeAdapter((RecipeActivity) getActivity());
        applyLayoutManager(rootView);
        recyclerView.setAdapter(recipesAdapter);
        fetchRecipeData(recipesAdapter);
    }

    @SuppressLint("VisibleForTests")
    private void applyIdlingConfiguration() {
        RecipeActivity recipeActivity = (RecipeActivity) getActivity();
        idlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        idlingResource.setIdleState(false);
    }

    private void applyLayoutManager(View rootView) {
        if (rootView.getTag() != null && rootView.getTag().equals("sw-600")) {
            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),
                    getResources().getInteger(R.integer.grid_view_landscape_column_number));
            recyclerView.setLayoutManager(mLayoutManager);
        } else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
        }
    }

    private void fetchRecipeData(final RecipeAdapter recipesAdapter) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<Recipe>> call = apiInterface.getRecipe();
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {
                ArrayList<Recipe> recipeList = response.body();
                recipesAdapter.setRecipeData(recipeList, getContext());
                restoreViewState();
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable throwable) {
                Log.e("http error: ", throwable.getMessage());
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void restoreViewState() {
        if (savedInstanceState == null) {
            return;
        }
        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER_KEY);
        recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }
}