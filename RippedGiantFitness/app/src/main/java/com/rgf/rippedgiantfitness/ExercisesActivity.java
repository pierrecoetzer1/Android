package com.rgf.rippedgiantfitness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.rgf.rippedgiantfitness.helper.ActivityHelper;
import com.rgf.rippedgiantfitness.helper.DialogHelper;
import com.rgf.rippedgiantfitness.helper.LogHelper;
import com.rgf.rippedgiantfitness.helper.SharedPreferencesHelper;
import com.rgf.rippedgiantfitness.interfaces.RGFActivity;

import java.util.List;

public class ExercisesActivity extends AppCompatActivity implements RGFActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;
        final AppCompatActivity activity = this;
        final String workoutIndex = getIntent().getStringExtra(SharedPreferencesHelper.WORKOUTS);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AppCompatEditText editTextName = DialogHelper.createEditText(context, "", DialogHelper.EXERCISE_NAME, InputType.TYPE_CLASS_TEXT);
                final AppCompatEditText editTextIncrement = DialogHelper.createEditText(context, "", DialogHelper.WEIGHT_INCREMENT, InputType.TYPE_CLASS_NUMBER);
                final AppCompatEditText editTextWarmupSets = DialogHelper.createEditText(context, "", DialogHelper.SETS_WARMUP, InputType.TYPE_CLASS_NUMBER);
                final AppCompatEditText editTextReps = DialogHelper.createEditText(context, "", DialogHelper.NUMBER_OF_REPS, InputType.TYPE_CLASS_NUMBER);
                final AppCompatEditText editTextRest = DialogHelper.createEditText(context, "", DialogHelper.REST_IN_SECONDS, InputType.TYPE_CLASS_NUMBER);
                final AppCompatEditText editTextMinWeight = DialogHelper.createEditText(context, "", DialogHelper.MIN_WEIGHT, InputType.TYPE_CLASS_NUMBER);
                final AppCompatEditText editTextMaxWeight = DialogHelper.createEditText(context, "", DialogHelper.MAX_WEIGHT, InputType.TYPE_CLASS_NUMBER);

                final AlertDialog dialog = DialogHelper.createDialog(context, DialogHelper.CREATE, DialogHelper.CREATE, DialogHelper.CANCEL, editTextName, editTextIncrement, editTextWarmupSets, editTextReps, editTextRest, editTextMinWeight, editTextMaxWeight);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreferencesHelper.addExercise(workoutIndex,
                                editTextName.getText().toString(),
                                editTextIncrement.getText().toString(),
                                editTextWarmupSets.getText().toString(),
                                editTextReps.getText().toString(),
                                editTextRest.getText().toString(),
                                editTextMinWeight.getText().toString(),
                                editTextMaxWeight.getText().toString())) {
                            ((ViewGroup)findViewById(R.id.content_exercises)).removeAllViews();
                            init();
                            dialog.dismiss();
                        } else {
                            LogHelper.error("Failed to add the exercise");
                        }
                    }
                });
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    public void init() {
        final Context context = this;
        final AppCompatActivity activity = this;
        final String workoutIndex = getIntent().getStringExtra(SharedPreferencesHelper.WORKOUTS);

        List<String> exercises =  SharedPreferencesHelper.getExercises(workoutIndex);

        for(String exercise : exercises) {
            final String exerciseIndex = exercise;

            final AppCompatButton buttonExercise = ActivityHelper.createButton(context, SharedPreferencesHelper.getPreference(exerciseIndex, SharedPreferencesHelper.NAME), true);
            buttonExercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExerciseActivity.class);
                    intent.putExtra(SharedPreferencesHelper.EXERCISES, exerciseIndex);
                    startActivity(intent);
                }
            });
            buttonExercise.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog dialog = DialogHelper.createDialog(context, SharedPreferencesHelper.getPreference(exerciseIndex, SharedPreferencesHelper.NAME), DialogHelper.MENU);
                    dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String text = ((AppCompatTextView) view).getText().toString();
                            switch (text) {
                                case DialogHelper.MOVE_UP:
                                    ActivityHelper.moveUp(activity, R.id.content_exercises, exerciseIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.MOVE_DOWN:
                                    ActivityHelper.moveDown(activity, R.id.content_exercises, exerciseIndex);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.EDIT:
                                    DialogHelper.createEditDialog(context, buttonExercise, exerciseIndex, DialogHelper.EXERCISE_NAME, "", "", SharedPreferencesHelper.NAME, InputType.TYPE_CLASS_TEXT);
                                    dialog.dismiss();
                                    break;
                                case DialogHelper.REMOVE:
                                    DialogHelper.createRemoveDialog(activity, R.id.content_exercises, exerciseIndex, "", "", SharedPreferencesHelper.NAME);
                                    dialog.dismiss();
                                    break;
                                default:
                                    LogHelper.error("Received unknown menu selection: " + text);
                            }
                        }
                    });
                    return true;
                }
            });

            ((ViewGroup)findViewById(R.id.content_exercises)).addView(buttonExercise);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.getComponent().getClassName().equals(WorkoutsActivity.class.getName())) {
            finish();
        } else {
            super.startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
