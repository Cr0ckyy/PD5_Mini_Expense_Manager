package com.myapplicationdev.android.jobby;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapplicationdev.android.jobby.Model.Data;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;


public class DashBoardFragment extends Fragment {

    // TODO:TODO: FloatingActionButton
    FloatingActionButton myMainFloatingActionButton, myIncomeFloatingActionButton, myExpenseFloatingActionButton;

    // TODO:TODO: FloatingActionButton TextView..
    TextView fabIncomeTextView, fabExpenseTextView;

    // TODO:TODO:boolean value
    boolean isOpen = false;

    // TODO:TODO:Animation
    Animation FadOpen, FadeClose;

    // TODO:TODO: Dashboard income and expense result..
    TextView totalIncomeResultTextView, totalExpenseResultTextView;

    // TODO:TODO: Firebase objects
    FirebaseAuth myFirebaseAuth;
    DatabaseReference myIncomeDatabaseReference;
    DatabaseReference myExpenseDatabaseReference;

    // TODO:TODO: Recycler view
    RecyclerView myIncomeRecyclerView, myExpenseRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO: Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);


        myFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();
        assert myFirebaseUser != null;
        String uid = myFirebaseUser.getUid();

        myIncomeDatabaseReference = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        myExpenseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        // TODO: By calling `keepSynced(true)` on a location,
//  the data for that location will automatically be downloaded and kept in sync,
//  even when no listeners are attached for that location.
//  Additionally, while a location is kept synced,
//  it will not be evicted from the persistent disk cache.
        myIncomeDatabaseReference.keepSynced(true);
        myExpenseDatabaseReference.keepSynced(true);

        // TODO:Connect FloatingActionButton to layout
        myMainFloatingActionButton = myView.findViewById(R.id.fb_main_plus_btn);
        myIncomeFloatingActionButton = myView.findViewById(R.id.income_Ft_btn);
        myExpenseFloatingActionButton = myView.findViewById(R.id.expense_Ft_btn);

        // TODO: Connect FloatingActionButton TextView
        fabIncomeTextView = myView.findViewById(R.id.income_ft_text);
        fabExpenseTextView = myView.findViewById(R.id.expense_ft_text);

        // TODO: Result of total income and expenses set
        totalIncomeResultTextView = myView.findViewById(R.id.income_set_result);
        totalExpenseResultTextView = myView.findViewById(R.id.expense_set_result);

        // TODO:Connect Animation
        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);


        myMainFloatingActionButton.setOnClickListener(view -> {

            addData();

            if (isOpen) {

                myIncomeFloatingActionButton.startAnimation(FadeClose);
                myExpenseFloatingActionButton.startAnimation(FadeClose);
                myIncomeFloatingActionButton.setClickable(false);
                myExpenseFloatingActionButton.setClickable(false);

                fabIncomeTextView.startAnimation(FadeClose);
                fabExpenseTextView.startAnimation(FadeClose);
                fabIncomeTextView.setClickable(false);
                fabExpenseTextView.setClickable(false);
                isOpen = false;

            } else {
                myIncomeFloatingActionButton.startAnimation(FadOpen);
                myExpenseFloatingActionButton.startAnimation(FadOpen);
                myIncomeFloatingActionButton.setClickable(true);
                myExpenseFloatingActionButton.setClickable(true);

                fabIncomeTextView.startAnimation(FadOpen);
                fabExpenseTextView.startAnimation(FadOpen);
                fabIncomeTextView.setClickable(true);
                fabExpenseTextView.setClickable(true);
                isOpen = true;

            }

        });

        // TODO: Calculate total income
        myIncomeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                int myTotalSum = 0;
                // TODO :A DataSnapshot instance contains data from a Firebase Database location.
                //  Any time you read Database data, you receive the data as a DataSnapshot.
                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {

                    Data data = myDataSnapshot.getValue(Data.class);

                    assert data != null;
                    myTotalSum += data.getAmount();

                    String stResult = String.valueOf(myTotalSum);

                    totalIncomeResultTextView.setText( "$" + stResult + ".00");

                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

        // TODO: Calculate total expense
        myExpenseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                int myTotalSum = 0;

                // TODO :A DataSnapshot instance contains data from a Firebase Database location.
                //  Any time you read Database data, you receive the data as a DataSnapshot.
                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {

                    Data data = myDataSnapshot.getValue(Data.class);
                    assert data != null;
                    myTotalSum += data.getAmount();

                    String stringMyTotalSum = String.valueOf(myTotalSum);

                    totalExpenseResultTextView.setText("$" + stringMyTotalSum + ".00");

                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });


        // TODO: Bind RecyclerView
        myIncomeRecyclerView = myView.findViewById(R.id.recycler_income);
        myExpenseRecyclerView = myView.findViewById(R.id.recycler_epense);


        // TODO: Set LinearLayoutManagers
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setReverseLayout(true);
        layoutManagerIncome.setStackFromEnd(true);
        myIncomeRecyclerView.setHasFixedSize(true);
        myIncomeRecyclerView.setLayoutManager(layoutManagerIncome);


        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        myExpenseRecyclerView.setHasFixedSize(true);
        myExpenseRecyclerView.setLayoutManager(layoutManagerExpense);


        return myView;
    }

    // TODO: Floating Action Button animation
    void ftAnimation() {
        if (isOpen) {

            myIncomeFloatingActionButton.startAnimation(FadeClose);
            myExpenseFloatingActionButton.startAnimation(FadeClose);
            myIncomeFloatingActionButton.setClickable(false);
            myExpenseFloatingActionButton.setClickable(false);

            fabIncomeTextView.startAnimation(FadeClose);
            fabExpenseTextView.startAnimation(FadeClose);
            fabIncomeTextView.setClickable(false);
            fabExpenseTextView.setClickable(false);
            isOpen = false;

        } else {
            myIncomeFloatingActionButton.startAnimation(FadOpen);
            myExpenseFloatingActionButton.startAnimation(FadOpen);
            myIncomeFloatingActionButton.setClickable(true);
            myExpenseFloatingActionButton.setClickable(true);

            fabIncomeTextView.startAnimation(FadOpen);
            fabExpenseTextView.startAnimation(FadOpen);
            fabIncomeTextView.setClickable(true);
            fabExpenseTextView.setClickable(true);
            isOpen = true;

        }

    }

    void addData() {

        // TODO: Floating Action Button Click Actions
        myIncomeFloatingActionButton.setOnClickListener(view -> incomeDataInsert());
        myExpenseFloatingActionButton.setOnClickListener(view -> expenseDataInsert());

    }


    public void incomeDataInsert() {

        // Todo: creating and initializing relevant variables for incomeDataInsert Method
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.activity_add, null);
        myAlertDialog.setView(myView);
        final AlertDialog dialog = myAlertDialog.create();

        dialog.setCancelable(false);

        // Todo: get data from the AlertDialog
        final EditText myEditTextAmount = myView.findViewById(R.id.amount_edt);
        final EditText myEditTextType = myView.findViewById(R.id.type_edt);
        final EditText myEditTextNote = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(view -> {

            String myType = myEditTextType.getText().toString().trim();
            String myAmount = myEditTextAmount.getText().toString().trim();
            String myNote = myEditTextNote.getText().toString().trim();

            // Todo: Verification for user inputs
            if (TextUtils.isEmpty(myType) || TextUtils.isEmpty(myAmount) || TextUtils.isEmpty(myNote)) {
                myEditTextType.setError("Required field...");
                return;
            }


            int myIntAmount = Integer.parseInt(myAmount);


            String myId = myIncomeDatabaseReference.push().getKey();

            String myDate = DateFormat.getDateInstance().format(new Date());

            Data data = new Data(myIntAmount, myType, myNote, myId, myDate);

            assert myId != null;
            myIncomeDatabaseReference.child(myId).setValue(data);

            Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();

            ftAnimation();
            dialog.dismiss();

        });

        btnCancel.setOnClickListener(view -> {
            ftAnimation();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void expenseDataInsert() {

// Todo: creating and initializing relevant variables for Login Method
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.activity_add, null);
        myAlertDialog.setView(myView);
        final AlertDialog dialog = myAlertDialog.create();

        dialog.setCancelable(false);

        final EditText amount = myView.findViewById(R.id.amount_edt);
        final EditText type = myView.findViewById(R.id.type_edt);
        final EditText note = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(view -> {

            String myTrimAmount = amount.getText().toString().trim();
            String myTrimyType = type.getText().toString().trim();
            String myTrimNote = note.getText().toString().trim();

            if (TextUtils.isEmpty(myTrimAmount) || TextUtils.isEmpty(myTrimyType) || TextUtils.isEmpty(myTrimNote)) {
                amount.setError("Required Field..");
                return;
            }

            int myIntAmount = Integer.parseInt(myTrimAmount);
            String id = myExpenseDatabaseReference.push().getKey();
            String myDate = DateFormat.getDateInstance().format(new Date());

            Data data = new Data(myIntAmount, myTrimyType, myTrimNote, id, myDate);
            myExpenseDatabaseReference.child(id).setValue(data);
            Toast.makeText(getActivity(), "Data added", Toast.LENGTH_SHORT).show();

            ftAnimation();
            dialog.dismiss();
        });


        btnCancel.setOnClickListener(view -> {
            ftAnimation();
            dialog.dismiss();
        });

        dialog.show();


    }


    @Override
    public void onStart() {
        super.onStart();
        // TODO: TODO:Init Firebase Recycler options
        FirebaseRecyclerOptions<Data> myFirebaseRecyclerOptionOne =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(myIncomeDatabaseReference, Data.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter =
                new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                        (myFirebaseRecyclerOptionOne) {
                    @NonNull
                    @NotNull
                    @Override
                    public IncomeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard_income, parent, false);


                        return new IncomeViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull DashBoardFragment.IncomeViewHolder holder, int position, @NonNull @NotNull Data model) {

                        holder.setIncomeType(model.getType());
                        holder.setIncomeamount(model.getAmount());
                        holder.setIncomeDate(model.getDate());
                    }


                };
        myIncomeRecyclerView.setAdapter(incomeAdapter);


        // TODO: TODO:Init Firebase Recycler options
        FirebaseRecyclerOptions<Data> myFirebaseRecyclerOptionTwo =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(myExpenseDatabaseReference, Data.class)
                        .setLifecycleOwner(this)
                        .build();


        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter =
                new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                        (myFirebaseRecyclerOptionTwo) {
                    @NotNull
                    @Override
                    public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard_expense, parent, false);
                        return new ExpenseViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull DashBoardFragment.ExpenseViewHolder holder, int position, @NonNull @NotNull Data model) {
                        holder.setExpenseType(model.getType());
                        holder.setExpenseamount(model.getAmount());
                        holder.setExpenseDate(model.getDate());
                    }


                };

        myExpenseRecyclerView.setAdapter(expenseAdapter);

    }

    // TODO:For Income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {

            TextView myType = mIncomeView.findViewById(R.id.type_Income_ds);
            myType.setText(type);

        }

        public void setIncomeamount(int amount) {

            TextView myAmount = mIncomeView.findViewById(R.id.ammoun_income_ds);
            String myStringAmount = String.valueOf(amount);
            myAmount.setText(myStringAmount);
        }

        public void setIncomeDate(String date) {

            TextView myDate = mIncomeView.findViewById(R.id.date_income_ds);
            myDate.setText(date);

        }

    }

    // TODO:For expense data..

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type) {
            TextView myType = mExpenseView.findViewById(R.id.type_expense_ds);
            myType.setText(type);
        }

        public void setExpenseamount(int amount) {
            TextView myAmount = mExpenseView.findViewById(R.id.ammoun_expense_ds);
            String myStringAmount = String.valueOf(amount);
            myAmount.setText(myStringAmount);
        }

        public void setExpenseDate(String date) {
            TextView myDate = mExpenseView.findViewById(R.id.date_expense_ds);
            myDate.setText(date);
        }

    }


}
