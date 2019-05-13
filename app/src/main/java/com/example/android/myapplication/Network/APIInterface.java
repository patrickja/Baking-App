package com.example.android.myapplication.Network;

import com.example.android.myapplication.Models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipe();
}