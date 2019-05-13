package com.example.android.myapplication.Activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.android.myapplication.Adapters.RecipeAdapter;
import com.example.android.myapplication.IdlingResources.SimpleIdlingResource;
import com.example.android.myapplication.Models.Ingredient;
import com.example.android.myapplication.Models.Recipe;
import com.example.android.myapplication.R;
import com.example.android.myapplication.Widgets.ListRemoteViewsService;

import java.util.ArrayList;

import static com.example.android.myapplication.Models.Constants.INTENT_EXTRA_NAME_RECIPE_DETAILS;
import static com.example.android.myapplication.Models.Constants.WIDGET_EXTRA_NAME_INGREDIENT;
import static com.example.android.myapplication.Models.Constants.WIDGET_EXTRA_NAME_QUANTITY;


public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {

    @Nullable
    private SimpleIdlingResource idlingResource;

    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public void onListItemClick(Recipe recipe) {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(INTENT_EXTRA_NAME_RECIPE_DETAILS, recipe);
            startActivity(intent);
            return;
        }
        updateAppWidget(recipe);
    }

    private void updateAppWidget(Recipe recipe) {
        applyWidgetConfiguration(recipe);
        Intent intentWidget = new Intent();
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, intentWidget);
        finish();
    }

    private void applyWidgetConfiguration(Recipe recipe) {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(getBaseContext().getPackageName(), R.layout.widget_configured_layout);
        views.setTextViewText(R.id.widget_title, recipe.getName());
        Intent ingredientWidgetListIntent = getWidgetIngredientListIntent(context, recipe);
        views.setRemoteAdapter(R.id.widget_list, ingredientWidgetListIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private Intent getWidgetIngredientListIntent(Context context, Recipe recipe) {
        Intent intent = new Intent(context, ListRemoteViewsService.class);
        ArrayList<String> ingredientList = new ArrayList<>();
        ArrayList<String> quantityList = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientList.add(ingredient.getIngredient());
            quantityList.add(String.format("%s %s", ingredient.getQuantity(), ingredient.getMeasure()));
        }
        intent.putStringArrayListExtra(WIDGET_EXTRA_NAME_INGREDIENT, ingredientList);
        intent.putStringArrayListExtra(WIDGET_EXTRA_NAME_QUANTITY, quantityList);
        return intent;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }
}