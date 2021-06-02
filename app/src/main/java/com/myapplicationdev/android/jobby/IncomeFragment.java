package com.myapplicationdev.android.jobby;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


public class IncomeFragment extends Fragment {


    //Todo: Declaring objects
    //Todo: Firebase 
    FirebaseAuth myFirebaseAuth;
    DatabaseReference myIncomeDatabaseReference;
    //Todo: Recyclerview
    RecyclerView myRecyclerView;
    //Todo: TextView
    TextView myIncomeTotalSumTextView;
    //Todo: EditText
    EditText myEditTextAmount, myEditTextType, myEditTextNote;
    //Todo: Button
    Button btnUpdate, btnDelete;
    //Todo: Data item value
    String type;
    String note;
    int amount;
    String myFirebaseKey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Todo:  Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        myFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();
        assert myFirebaseUser != null;
        String myFirebaseUserId = myFirebaseUser.getUid();

        myIncomeDatabaseReference = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(myFirebaseUserId);

        myIncomeTotalSumTextView = myView.findViewById(R.id.income_txt_result);

        myRecyclerView = myView.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(layoutManager);


        myIncomeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                int myTotalValue = 0;

                // A DataSnapshot instance contains data from a Firebase Database location.
                // Any time you read Database data, you receive the data as a DataSnapshot.
                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {

                    Data data = myDataSnapshot.getValue(Data.class);

                    assert data != null;
                    myTotalValue += data.getAmount();

                    String myStringTotalValue = String.valueOf(myTotalValue);

                    myIncomeTotalSumTextView.setText("$" + myStringTotalValue + ".00");
                }


            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });


        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();

        //Todo:  Init Firebase Recycler options
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(myIncomeDatabaseReference, Data.class)
                        .setLifecycleOwner(this)
                        .build();

        //Todo:  Init adapter
        FirebaseRecyclerAdapter<Data, myViewHolder> adapter =
                new FirebaseRecyclerAdapter<Data, myViewHolder>
                        (options) {
                    @NonNull
                    @NotNull
                    @Override
                    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        //Todo:  Create a new instance of the ViewHolder, in this case we are using a custom
                        //Todo:  layout called R.layout.message for each item
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_income, parent, false);
                        return new myViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull IncomeFragment.myViewHolder holder, int position, @NonNull @NotNull Data model) {
                        holder.setType(model.getType());
                        holder.setNote(model.getNote());
                        holder.setDate(model.getDate());
                        holder.setamount(model.getAmount());

                        holder.myView.setOnClickListener(view -> {

                            myFirebaseKey = getRef(position).getKey();

                            type = model.getType();
                            note = model.getNote();
                            amount = model.getAmount();

                            updateDataItem();
                        });
                    }


                };

        //Todo:   Set the adapter
        myRecyclerView.setAdapter(adapter);

    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public myViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        void setType(String type) {
            TextView myType = myView.findViewById(R.id.type_txt_income);
            myType.setText(type);
        }

        void setNote(String note) {

            TextView myNote = myView.findViewById(R.id.note_txt_income);
            myNote.setText(note);

        }

        void setDate(String date) {
            TextView myDate = myView.findViewById(R.id.date_txt_income);
            myDate.setText(date);
        }

        void setamount(int amount) {

            TextView mamount = myView.findViewById(R.id.amount_txt_income);
            String stamount = String.valueOf(amount);
            mamount.setText(stamount);

        }


    }


    void updateDataItem() {

        // Todo: creating and initializing relevant variables for Login Method
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.activity_update, null);
        mydialog.setView(myView);

        myEditTextAmount = myView.findViewById(R.id.amount_edt);
        myEditTextType = myView.findViewById(R.id.type_edt);
        myEditTextNote = myView.findViewById(R.id.note_edt);

        //Todo: Set data to edit text..
        myEditTextType.setText(type);
        myEditTextType.setSelection(type.length());

        myEditTextNote.setText(note);
        myEditTextNote.setSelection(note.length());

        myEditTextAmount.setText(String.valueOf(amount));
        myEditTextAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myView.findViewById(R.id.btn_upd_Update);
        btnDelete = myView.findViewById(R.id.btnuPD_Delete);

        final AlertDialog dialog = mydialog.create();

        // TODO: Update action
        btnUpdate.setOnClickListener(view -> {

            type = myEditTextType.getText().toString().trim();
            note = myEditTextNote.getText().toString().trim();


            String stringAmount = myEditTextAmount.getText().toString().trim();
            int myAmount = Integer.parseInt(stringAmount);

            String myDate = DateFormat.getDateInstance().format(new Date());

            Data data = new Data(myAmount, type, note, myFirebaseKey, myDate);

            myIncomeDatabaseReference.child(myFirebaseKey).setValue(data);

            dialog.dismiss();
        });

        // TODO: Delete action
        btnDelete.setOnClickListener(view -> {

            myIncomeDatabaseReference.child(myFirebaseKey).removeValue();

            dialog.dismiss();
        });

        dialog.show();


    }


}
